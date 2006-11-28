package org.newdawn.slick.tools.peditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.lwjgl.LWJGLException;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.util.Log;

/**
 * The bootstrap and main frame for the particle editor Pedigree.
 *
 * @author kevin
 */
public class ParticleEditor extends JFrame {
	/** The canvas displaying the particles */
	private ParticleCanvas canvas;
	/** Create a new system */
	private JMenuItem newSystem = new JMenuItem("New System");
	/** Load a complete particle system */
	private JMenuItem load = new JMenuItem("Load System");
	/** Save a complete particle system */
	private JMenuItem save = new JMenuItem("Save System");
	/** Load a single particle emitter */
	private JMenuItem imp = new JMenuItem("Import Emitter");
	/** Save a single particle emitter */
	private JMenuItem exp = new JMenuItem("Export Emitter");
	/** Exit the editor */
	private JMenuItem quit = new JMenuItem("Exit");

	/** The visual list of emitters */
	private EmitterList emitters;
	/** The controls for the initial emission settings */
	private EmissionControls emissionControls;
	/** The positional controls for spawnng particles */
	private PositionControls positionControls;
	/** The global settings for the emitter */
	private SettingsPanel settingsPanel;
	/** The color controls for particles */
	private ColorPanel colorPanel;
	
	/** Control for the type of particle system blending */
	private JCheckBox additive = new JCheckBox("Additive Blending");
	/** The currently selected particle emitter */
	private ConfigurableEmitter selected;
	/** Chooser used to load/save/import/export */
	private JFileChooser chooser = new JFileChooser(new File("."));
	/** Reset the particle counts on the canvas */
	private JButton reset = new JButton("Reset Maximum");
	
	/**
	 * Create a new editor
	 * 
	 * @throws LWJGLException Indicates a failure to create an OpenGL context
	 */
	public ParticleEditor() throws LWJGLException {
		super("Pedigree");

		chooser.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				
				if (f.getName().endsWith(".xml")) {
					return true;
				}
				
				return false;
			}

			public String getDescription() {
				return "XML Files";
			}
		});
		
		try {
			InputStream in = ParticleEditor.class.getClassLoader().getResourceAsStream("org/newdawn/slick/tools/peditor/data/icon.gif");
			
			setIconImage(ImageIO.read(in));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		emitters = new EmitterList(this);
		emissionControls = new EmissionControls();
		positionControls = new PositionControls();
		settingsPanel = new SettingsPanel(emitters);
		colorPanel = new ColorPanel();
		
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
		JMenu file = new JMenu("File");
		file.add(newSystem);
		file.addSeparator();
		file.add(load);
		file.add(save);
		file.addSeparator();
		file.add(imp);
		file.add(exp);
		file.addSeparator();
		file.add(quit);

		newSystem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewSystem();
			}
		});
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadSystem();
			}
		});
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveSystem();
			}
		});
		exp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportEmitter();
			}
		});
		imp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importEmitter();
			}
		});
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		JMenuBar bar = new JMenuBar();
		bar.add(file);
		setJMenuBar(bar);
		
		canvas = new ParticleCanvas();
		canvas.setSize(500,600);
		JPanel controls = new JPanel();
		controls.setLayout(null);
		emitters.setBounds(0,0,300,150);
		emitters.setBorder(BorderFactory.createTitledBorder("Emitters"));
		controls.add(emitters);
		JTabbedPane tabs = new JTabbedPane();
		tabs.setBounds(0,150,300,450);
		controls.add(tabs);
		
		tabs.add("Settings", settingsPanel);
		tabs.add("Emission", emissionControls);
		tabs.add("Position", positionControls);
		tabs.add("Color", colorPanel);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		canvas.setBounds(0,0,500,600);
		controls.setBounds(500,20,300,600);
		reset.setBounds(680,0,110,25);
		panel.add(reset);
		additive.setBounds(500,0,150,25);
		panel.add(additive);
		panel.add(canvas);
		panel.add(controls);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		additive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateBlendMode();
			}
		});
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				canvas.resetCounts();
			}
		});
		
		ConfigurableEmitter test = new ConfigurableEmitter("Default");
		emitters.add(test);
		canvas.addEmitter(test);
		
		additive.setSelected(true);
		
		setContentPane(panel);
		setSize(800,600);
		setResizable(false);
		setVisible(true);

		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() != 1) {
					positionControls.setPosition(0,0);
				}
			}
		});
		
		canvas.addMouseMotionListener(new MouseMotionListener() {

			public void mouseDragged(MouseEvent e) {
				int xp = e.getX() - 250;
				int yp = e.getY() - 300;
				positionControls.setPosition(xp,yp);
			}

			public void mouseMoved(MouseEvent e) {
			}
			
		});
		
		emitters.setSelected(0);
	}
	
	/**
	 * Import an emitter XML file
	 */
	public void importEmitter() {
		int resp = chooser.showOpenDialog(this);
		if (resp == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			try {
				ConfigurableEmitter emitter = ParticleIO.loadEmitter(file);
				addEmitter(emitter);
				emitters.setSelected(emitter);
			} catch (IOException e) {
				Log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
	}

	/**
	 * Export an emitter XML file
	 */
	public void exportEmitter() {
		if (selected == null) {
			return;
		}
		
		int resp = chooser.showSaveDialog(this);
		if (resp == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (!file.getName().endsWith(".xml")) {
				file = new File(file.getAbsolutePath()+".xml");
			}
			
			try {
				ParticleIO.saveEmitter(file, selected);
			} catch (IOException e) {
				Log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
	}

	/**
	 * Create a completely new particle system
	 */
	public void createNewSystem() {
		canvas.clearSystem(additive.isSelected());
		emitters.clear();
	}
	
	/**
	 * Load a complete particle system XML description
	 */
	public void loadSystem() {
		int resp = chooser.showOpenDialog(this);
		if (resp == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			try {
				ParticleSystem system = ParticleIO.loadConfiguredSystem(file);
				canvas.setSystem(system);
				emitters.clear();
				for (int i=0;i<system.getEmitterCount();i++) {
					emitters.add((ConfigurableEmitter) system.getEmitter(i));
				}
				additive.setSelected(system.getBlendingMode() == ParticleSystem.BLEND_ADDITIVE);
			} catch (IOException e) {
				Log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
	}

	/**
	 * Save a complete particle system XML description
	 */
	public void saveSystem() {
		if (selected == null) {
			return;
		}
		
		int resp = chooser.showSaveDialog(this);
		if (resp == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (!file.getName().endsWith(".xml")) {
				file = new File(file.getAbsolutePath()+".xml");
			}
			
			try {
				ParticleIO.saveConfiguredSystem(file, canvas.getSystem());
			} catch (IOException e) {
				Log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
	}
	
	/**
	 * Add a new emitter to the editor 
	 * 
	 * @param emitter The emitter to add
	 */
	public void addEmitter(ConfigurableEmitter emitter) {
		emitters.add(emitter);
		canvas.addEmitter(emitter);
	}
	
	/**
	 * Remove a particle emitter from the editor
	 * 
	 * @param emitter The emitter to be removed
	 */
	public void removeEmitter(ConfigurableEmitter emitter) {
		emitters.remove(emitter);
		canvas.removeEmitter(emitter);
	}
	
	/**
	 * Set the currently selected and edited particle emitter
	 * 
	 * @param emitter The emitter that should be selected or null for none
	 */
	public void setCurrentEmitter(ConfigurableEmitter emitter) {
		this.selected = emitter;
		
		if (emitter == null) {
			emissionControls.setEnabled(false);
			settingsPanel.setEnabled(false);
			positionControls.setEnabled(false);
			colorPanel.setEnabled(false);
		} else {
			emissionControls.setEnabled(true);
			settingsPanel.setEnabled(true);
			positionControls.setEnabled(true);
			colorPanel.setEnabled(true);
			
			emissionControls.setTarget(emitter);
			positionControls.setTarget(emitter);
			settingsPanel.setTarget(emitter);
			colorPanel.setTarget(emitter);
		}
	}
	
	/**
	 * Change the visual indicator for the current particle system 
	 * blend mode
	 */
	public void updateBlendMode() {
		if (additive.isSelected()) {
			canvas.getSystem().setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
 		} else {
			canvas.getSystem().setBlendingMode(ParticleSystem.BLEND_COMBINE);
 		}
	}
	
	/**
	 * Entry point in the editor
	 * 
	 * @param argv The arguments passed on the command line
	 */
	public static void main(String[] argv) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
			new ParticleEditor();
		} catch (Exception e) {
			Log.warn(e.getMessage());
		}
	}
}
