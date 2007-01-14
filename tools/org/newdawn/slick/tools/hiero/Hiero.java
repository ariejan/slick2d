package org.newdawn.slick.tools.hiero;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

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

import org.newdawn.slick.tools.hiero.truetype.FontData;

/**
 * A tool to generate bitmap fonts
 *
 * @author kevin
 */
public class Hiero extends JFrame {
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
    private JFileChooser chooser = new JFileChooser(new File("."));
    
    /**
     * Create a new instance of the tool
     */
    public Hiero() {
        super("Hiero Bitmap Font Tool");
   
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        bar.add(file);
        setJMenuBar(bar);
        
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		saveFont();
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
        file.add(save);
        
        JPanel panel = new JPanel();
        panel.setLayout(null);
        
        fontPanel = new FontPanel();
        JScrollPane pane = new JScrollPane(fontPanel);
        pane.setBounds(5,5,550,550);
        panel.add(pane);

        updateFontList();
        
        JLabel label;
        
        label = new JLabel("Font");
        label.setBounds(560,5,165,25);
        panel.add(label);
        
        JScrollPane list = new JScrollPane(fontList);
        list.setBounds(560,30,165,200);
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(list);
        fontList.setSelectedValue("Arial", true);
        fontList.addListSelectionListener(new ListSelectionListener() {
        	public void valueChanged(ListSelectionEvent e) {
        		updateFont();
        	}
        });
        
        label = new JLabel("Size");
        label.setBounds(555,230,165,25);
        panel.add(label);
        size = new JSpinner(new SpinnerNumberModel(10,8,100,1));
        size.setBounds(560,255,165,25);
        panel.add(size);
        size.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
        		updateFont();
        	}
        });
        
        bold = new JCheckBox("Bold");
        bold.setBounds(560,280,165,25);
        panel.add(bold);
        bold.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		updateFont();
        	}
        });
        
        italic = new JCheckBox("Italic");
        italic.setBounds(560,305,165,25);
        panel.add(italic);
        italic.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		updateFont();
        	}
        });
        
        label = new JLabel("Character Set");
        label.setBounds(560,330,165,25);
        panel.add(label);
        
        Vector types = new Vector();
        types.addElement(SET_ASCII);
        types.addElement(SET_NEHE);
        
        final JComboBox type = new JComboBox(types);
        type.setBounds(560,355,165,25);
        panel.add(type);
        type.setSelectedItem(SET_NEHE);
        
        type.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		fontPanel.setCharSet((CharSet) type.getSelectedItem());
        	}
        });
        
        setResizable(false);
        setContentPane(panel);
    	setSize(750,620);
        setVisible(true);
        
        addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		System.exit(0);
        	}
        });
        
        size.setValue(new Integer(45));
    }
    
    /**
     * Save the font out to the data file and image
     */
    private void saveFont() {
    	int resp = chooser.showSaveDialog(this);
    	if (resp == JFileChooser.APPROVE_OPTION) {
    		String path = chooser.getSelectedFile().getAbsolutePath();
    		if (path.indexOf(".") >= 0) {
    			path = path.substring(0, path.indexOf("."));
    		}
    		
    		try {
                fontPanel.generateData();
    			fontPanel.save(path);
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
    			supportsBold = false;
    		}
    		if (supportsItalic) {
    			italic = true;
    			this.italic.setSelected(true);
    			supportsItalic = false;
    		}
    	}
    	
    	this.bold.setEnabled(supportsBold);
    	this.italic.setEnabled(supportsItalic);
    	
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
    	JFileChooser chooser = new JFileChooser(".");
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
