package org.newdawn.slick.tools.hiero;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
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

public class Hiero extends JFrame {
    public static final CharSet SET_ASCII = new CharSet(32, 255,"ASCII");
    public static final CharSet SET_NEHE = new CharSet(32, 32+96,"NEHE");
    
    private Font font = new Font("Verdana", Font.ITALIC, 50);
    private DefaultListModel fonts = new DefaultListModel();
    private JList fontList = new JList(fonts);
    private FontPanel fontPanel;
    private JSpinner size;
    private JCheckBox bold;
    private JCheckBox italic;
    private JFileChooser chooser = new JFileChooser(new File("."));
    
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
        file.add(save);
        
        JPanel panel = new JPanel();
        panel.setLayout(null);
        
        fontPanel = new FontPanel();
        JScrollPane pane = new JScrollPane(fontPanel);
        pane.setBounds(5,5,550,550);
        panel.add(pane);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String fontNames[] = ge.getAvailableFontFamilyNames();
        for (int i=0;i<fontNames.length;i++) {
        	fonts.addElement(fontNames[i]);
        }
        
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
    
    private void updateFont() {
    	String name = fontList.getSelectedValue().toString();
    	int size = ((Integer) this.size.getValue()).intValue();
    	boolean bold = this.bold.isSelected();
    	boolean italic = this.italic.isSelected();
    	
    	int style = Font.PLAIN;
    	if (bold) {
    		style |= Font.BOLD;
    	}
    	if (italic) {
    		style |= Font.ITALIC;
    	}
    	
    	font = new Font(name, style, size);
    	fontPanel.setFont(font);
    }
    
    public static void main(String[] argv) {
        new Hiero();
    }
}
