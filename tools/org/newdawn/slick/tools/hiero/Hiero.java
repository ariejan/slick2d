package org.newdawn.slick.tools.hiero;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.newdawn.slick.tools.hiero.effects.EffectsDialog;
import org.newdawn.slick.tools.hiero.truetype.FontData;

/**
 * A tool to generate bitmap fonts
 *
 * @author kevin
 */
public class Hiero extends JFrame {
	/** Save the bitmap font as an XML file */
	public static final int XML = 1;
	/** Save the bitmap font as an TEXT file */
	public static final int TEXT = 2;
	
	/** The character set for all ascii characters */
    public static final CharSet SET_ASCII = new CharSet(32, 255,"ASCII");
    /** The subset for NEHE style fonts */
    public static final CharSet SET_NEHE = new CharSet(32, 32+96,"NEHE");
    
    /** The font data for the currently selected font */
    private FontData font;
    /** The list of fonts available */
    private DefaultListModel fonts = new DefaultListModel();
    /** The visual list of fonts */
    private JList fontList = new JList(fonts);
    /** The panel displaying the rendered font texture */
    private FontPanel fontPanel;
    /** The size of the font rendered */
    private JSpinner size;
    /** Indicator for bold fonts */
    private JCheckBox bold;
    /** Indicator for italic fonts */
    private JCheckBox italic;
    /** The chooser for everything */
    private JFileChooser chooser = new JFileChooser(".");
    /** The width of the texture to generate */
    private int textureWidth = 512;
    /** The height of the texture to generate */
    private int textureHeight = 512;
    /** The charsets combo */
    private final JComboBox charsets; 
    /** The list of character sets */
    private DefaultComboBoxModel types = new DefaultComboBoxModel();
    /** The dialog for effects configuration */
    private EffectsDialog effectsDialog;
    
    /**
     * Create a new instance of the tool
     */
    public Hiero() {
        super("Hiero Bitmap Font Tool");
   
        effectsDialog = new EffectsDialog(this);
        
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        bar.add(file);
        setJMenuBar(bar);

        JMenuItem cset = new JMenuItem("Edit Character Set");
        cset.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		editChars();
        	}
        });
        
        JMenuItem ef = new JMenuItem("Show/Hide Effects Dialog");
        ef.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		effectsDialog.setVisible(!effectsDialog.isVisible());
        	}
        });
        
        JMenuItem saveText = new JMenuItem("Save As BMFont Text File..");
        saveText.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		saveFont(TEXT);
        	}
        });
        JMenuItem saveXML = new JMenuItem("Save As BMFont XML File..");
        saveXML.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		saveFont(XML);
        	}
        });
        JMenuItem quit = new JMenuItem("Exit");
        quit.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		System.exit(0);
        	}
        });
        JMenuItem addDir = new JMenuItem("Add Font Directory");
        addDir.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		addDirectory();
        	}
        });
        
        file.add(addDir);
        file.addSeparator();
        file.add(ef);
        file.add(cset);
        file.addSeparator();
        file.add(saveText);
        file.add(saveXML);
        file.addSeparator();
        file.add(quit);
        
        Box panel = Box.createHorizontalBox();
        
        fontPanel = new FontPanel();
        JScrollPane pane = new JScrollPane(fontPanel);
        pane.setBounds(5,5,550,550);
        panel.add(pane);

        JPanel controls = new JPanel();
        controls.setLayout(null);
        Box vert = Box.createVerticalBox();
        Component strut = Box.createRigidArea(new Dimension(180,5));
        
        vert.add(controls);
        vert.add(strut);
        panel.add(vert);
        vert.setMinimumSize(new Dimension(180,650));
        vert.setMaximumSize(new Dimension(180,650));
        vert.setPreferredSize(new Dimension(180,650));
        
        Splash splash = new Splash();
        updateFontList();
        splash.dispose();
        
        JLabel label;
        
        label = new JLabel("Font");
        label.setBounds(5,5,165,25);
        controls.add(label);
        
        JScrollPane list = new JScrollPane(fontList);
        list.setBounds(5,30,165,200);
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        controls.add(list);
        fontList.setSelectedValue("Arial", true);
        fontList.addListSelectionListener(new ListSelectionListener() {
        	public void valueChanged(ListSelectionEvent e) {
        		updateFont();
        	}
        });
        
        label = new JLabel("Size");
        label.setBounds(5,230,165,25);
        controls.add(label);
        size = new JSpinner(new SpinnerNumberModel(10,8,300,1));
        size.setBounds(5,255,165,25);
        controls.add(size);
        size.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
        		updateFont();
        	}
        });
        
        bold = new JCheckBox("Bold");
        bold.setBounds(5,280,165,25);
        controls.add(bold);
        bold.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		updateFont();
        	}
        });
        
        italic = new JCheckBox("Italic");
        italic.setBounds(5,305,165,25);
        controls.add(italic);
        italic.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		updateFont();
        	}
        });
        
        label = new JLabel("Character Set");
        label.setBounds(5,330,165,25);
        controls.add(label);
        
        charsets = new JComboBox(types);
        charsets.setBounds(5,355,165,25);
        controls.add(charsets);
        updateSets("");
        charsets.setSelectedItem(SET_NEHE);
        
        label = new JLabel("Width");
        label.setBounds(5,380,165,25);
        controls.add(label);
        
        Vector widths = new Vector();
        widths.addElement("64");
        widths.addElement("128");
        widths.addElement("256");
        widths.addElement("512");
        widths.addElement("1024");
        
        final JComboBox width = new JComboBox(widths);
        width.setBounds(5,405,165,25);
        controls.add(width);
        width.setSelectedItem("512");

        label = new JLabel("Height");
        label.setBounds(5,430,165,25);
        controls.add(label);
        
        final JComboBox height = new JComboBox(widths);
        height.setBounds(5,455,165,25);
        controls.add(height);
        height.setSelectedItem("512");
        
        charsets.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		fontPanel.setCharSet((CharSet) charsets.getSelectedItem());
        	}
        });
        width.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		textureWidth = Integer.parseInt(width.getSelectedItem().toString());
        		fontPanel.setRequiredDimensions(textureWidth, textureHeight);
        	}
        });
        height.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		textureHeight = Integer.parseInt(height.getSelectedItem().toString());
        		fontPanel.setRequiredDimensions(textureWidth, textureHeight);
        	}
        });

        label = new JLabel("Padding");
        label.setBounds(5,480,165,25);
        controls.add(label);
        final JSpinner paddingTop = new JSpinner(new SpinnerNumberModel(0,0,100,1));
        final JSpinner paddingLeft = new JSpinner(new SpinnerNumberModel(0,0,100,1));
        final JSpinner paddingRight = new JSpinner(new SpinnerNumberModel(0,0,100,1));
        final JSpinner paddingBottom = new JSpinner(new SpinnerNumberModel(0,0,100,1));
        final JSpinner paddingAdvance = new JSpinner(new SpinnerNumberModel(0,-100,100,1));
        
        paddingTop.setBounds(60,495,50,25);
        controls.add(paddingTop);
        paddingLeft.setBounds(5,520,50,25);
        controls.add(paddingLeft);
        paddingRight.setBounds(110,520,50,25);
        controls.add(paddingRight);
        paddingBottom.setBounds(60,545,50,25);
        controls.add(paddingBottom);
        paddingTop.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
        		fontPanel.setPadding(FontPanel.TOP, ((Integer) paddingTop.getValue()).intValue());
        	}
        });
        paddingLeft.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
        		fontPanel.setPadding(FontPanel.LEFT, ((Integer) paddingLeft.getValue()).intValue());
        		int total = ((Integer) paddingLeft.getValue()).intValue();
        		total += ((Integer) paddingRight.getValue()).intValue();
        		
        		paddingAdvance.setValue(new Integer(total));
        	}
        });
        paddingRight.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
        		fontPanel.setPadding(FontPanel.RIGHT, ((Integer) paddingRight.getValue()).intValue());
        		int total = ((Integer) paddingLeft.getValue()).intValue();
        		total += ((Integer) paddingRight.getValue()).intValue();
        		
        		paddingAdvance.setValue(new Integer(total));
        	}
        });
        paddingBottom.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
        		fontPanel.setPadding(FontPanel.BOTTOM, ((Integer) paddingBottom.getValue()).intValue());
        	}
        });

        label = new JLabel("Advance Padding");
        label.setBounds(5,570,165,25);
        controls.add(label);
        paddingAdvance.setBounds(5,595,50,25);
        controls.add(paddingAdvance);
        paddingAdvance.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
        		fontPanel.setPadding(FontPanel.ADVANCE, ((Integer) paddingAdvance.getValue()).intValue());
        	}
        });
        
        setContentPane(panel);
    	setSize(750,700);
    	
    	Dimension dims = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dims.width - getWidth())/2, (dims.height - getHeight()) / 2);
        setVisible(true);
        
        addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		System.exit(0);
        	}
        });
        
        size.setValue(new Integer(32));
    }
    
    /**
     * Apply the effects defined against the font
     */
    public void applyEffects() {
    	fontPanel.setEffects(effectsDialog.getEffects());
    }
    
    /**
     * Update all the characters sets
     * 
     * @param name The name of the set recently added
     * @return The character set representing the named one
     */
    private CharSet updateSets(String name) {
    	types.removeAllElements();
        
        types.addElement(SET_ASCII);
        types.addElement(SET_NEHE);
    
        CharSet loadedSet = null;
        File[] sets = HieroConfig.listFiles(".set");
        
        for (int i=0;i<sets.length;i++) {
        	try {
        		CharSet nSet = new CharSet(sets[i]);
        		types.addElement(nSet);
        		
        		if (nSet.getName().equals(name)) {
        			loadedSet = nSet;
        		}
        	} catch (IOException e) {
        		System.err.println("Unable to red character set: "+sets[i].getName());
        	}
        }
        
        return loadedSet;
    }
    
    /**
     * Edit the currently selected char set
     */
    private void editChars() {
    	CharSet set = (CharSet) charsets.getSelectedItem();
    	CharSetDialog dialog = new CharSetDialog(this, set);

		dialog.setVisible(true);
		if (dialog.getNewSet() != null) {
			String newName = dialog.getNewSet();
			CharSet s = updateSets(newName);
			if (s != null) {
		        charsets.setSelectedItem(s);
			}
		}
    }
    
    /**
     * Save the font out to the data file and image
     * 
     * @param type The type of file to save as
     */
    private void saveFont(int type) {
    	int resp = chooser.showSaveDialog(this);
    	if (resp == JFileChooser.APPROVE_OPTION) {
    		String path = chooser.getSelectedFile().getAbsolutePath();
    		if (path.indexOf(".") >= 0) {
    			path = path.substring(0, path.indexOf("."));
    		}
    		
    		try {
                fontPanel.generateData();
    			fontPanel.save(path, type);
    		} catch (IOException e) {
    			e.printStackTrace();
    			JOptionPane.showMessageDialog(this, "Failed to save font, given reason: "+e.getMessage());
    		}
    	}
    }
    
    /**
     * Update the current font
     */
    private void updateFont() {
    	if (fontList.getSelectedValue() == null) {
    		return;
    	}
    	
    	String name = fontList.getSelectedValue().toString();
    	int size = ((Integer) this.size.getValue()).intValue();
    	
    	boolean supportsBold = FontData.getBold(name) != null;
    	boolean supportsItalic = FontData.getItalic(name) != null;
    	boolean bold = this.bold.isSelected() && supportsBold;
    	boolean italic = this.italic.isSelected() && supportsItalic;
    	
    	if (FontData.getPlain(name) == null) {
    		if (supportsBold) {
    			bold = true;
    			this.bold.setSelected(true);
    		} else if (supportsItalic) {
    			italic = true;
    			this.italic.setSelected(true);
    		}
			supportsBold = false;
			supportsItalic = false;
    	}
    	
    	this.bold.setEnabled(supportsBold);
    	this.italic.setEnabled(supportsItalic);
    	
    	if (bold && italic) {
    		if (FontData.getBoldItalic(name) == null) {
    			italic = false;
    			bold = false;
    		}
    	}
    	
    	int style = Font.PLAIN;
    	if (bold) {
    		style |= Font.BOLD;
    	}
    	if (italic) {
    		style |= Font.ITALIC;
    	}
    	
    	font = FontData.getStyled(name, style);
    	font = font.deriveFont(size);
    	fontPanel.setFont(font);
    }
    
    /**
     * Update the list of fonts available
     */
    public void updateFontList() {
    	fonts.clear();
    	
        String fontNames[] = FontData.getFamilyNames();
        for (int i=0;i<fontNames.length;i++) {
        	fonts.addElement(fontNames[i]);
        }
    }
    /**
     * Add a font directory
     */
    public void addDirectory() {
    	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	int resp = chooser.showOpenDialog(this);
    	if (resp == JFileChooser.APPROVE_OPTION) {
    		File dir = chooser.getSelectedFile();
    		FontData.addFontDirectory(dir);
    		updateFontList();
    	}
    }
    
    /**
     * Entry point into the application
     * 
     * @param argv The arguments passed to the program
     */
    public static void main(String[] argv) {
        new Hiero();
    }
}
