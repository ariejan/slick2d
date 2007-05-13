package org.newdawn.slick.tools.hiero.effects;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.newdawn.slick.tools.hiero.Hiero;

/**
 * A dialog to allow setup and configuration of visual effects
 * on the fonts
 *
 * @author kevin
 */
public class EffectsDialog extends JFrame {
	/** The list of effects available */
	private DefaultListModel effects = new DefaultListModel();
	/** The visual list */
	private JList effectsList = new JList(effects);
	/** The list of effects being applied */
	private DefaultListModel applied = new DefaultListModel();
	/** The visual list */
	private JList appliedList = new JList(applied);
	/** The hiero instance wi're configuring */
	private Hiero hiero;
	
	/**
	 * Create a new dialog
	 * 
	 * @param hiero The parent dialog
	 */
	public EffectsDialog(final Hiero hiero) {
		super("Effects");
		
		this.hiero = hiero;
		
		JButton add = new JButton("Add >");
		JButton remove = new JButton("Remove");
		JButton up = new JButton("Up");
		JButton down = new JButton("Down");
		JButton configure = new JButton("Configure");
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		JScrollPane pane = new JScrollPane(effectsList);
		pane.setBounds(5,30,180,250);
		panel.add(pane);
		pane = new JScrollPane(appliedList);
		pane.setBounds(265,30,180,250);
		panel.add(pane);
		
		JButton apply = new JButton("Apply");
		apply.setBounds(450,285,90,25);
		panel.add(apply);
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hiero.applyEffects();
			}
		});
		
		JLabel label = new JLabel("Effects");
		label.setBounds(5,5,180,25);
		panel.add(label);
		label = new JLabel("Applied");
		label.setBounds(265,5,180,25);
		panel.add(label);
		
		add.setBounds(190,30,70,25);
		panel.add(add);
		up.setBounds(450,30,90,25);
		panel.add(up);
		down.setBounds(450,60,90,25);
		panel.add(down);
		configure.setBounds(450,95,90,25);
		panel.add(configure);
		remove.setBounds(450,130,90,25);
		panel.add(remove);
		
		Effect[] effects = EffectsRegistry.getEffects();
		for (int i=0;i<effects.length;i++) {
			this.effects.addElement(effects[i]);
		}
		effectsList.setCellRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
			
				label.setText(((Effect) value).getEffectName());
				return label;
			}
		});
		
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addEffect();
			}
		});
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeEffect();
			}
		});
		up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				up();
			}
		});
		down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				down();
			}
		});
		configure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configure();
			}
		});
		
		setContentPane(panel);
		setSize(560,350);
		setResizable(false);
    	Dimension dims = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dims.width - getWidth())/2, (dims.height - getHeight()) / 2);
	}

	/**
	 * Get the list of effects to apply
	 * 
	 * @return The list of effects to apply
	 */
	public ArrayList getEffects() {
		ArrayList result = new ArrayList();
		
		for (int i=0;i<applied.size();i++) {
			result.add(applied.get(i));
		}
		
		return result;
	}
	
	/**
	 * Configure the current selected effect
	 */
	private void configure() {
		Effect effect = (Effect) appliedList.getSelectedValue();
		if (effect != null) {
			EffectsConfigurationDialog dialog = new EffectsConfigurationDialog(this, effect);
			appliedList.repaint(0);
		}
	}
	
	/**
	 * Move the currently selected effect up
	 */
	private void up() {
		Effect effect = (Effect) appliedList.getSelectedValue();
		if (effect != null) {
			int index = applied.indexOf(effect);
			if (index > 0) {
				applied.removeElement(effect);
				applied.add(index - 1, effect);
				appliedList.setSelectedIndex(index - 1);
			}
		}
	}

	/**
	 * Move the currently selected effect down
	 */
	private void down() {
		Effect effect = (Effect) appliedList.getSelectedValue();
		if (effect != null) {
			int index = applied.indexOf(effect);
			if (index < applied.size()-1) {
				applied.removeElement(effect);
				applied.add(index + 1, effect);
				appliedList.setSelectedIndex(index+1);
			}
		}
	}
	
	/**
	 * Remove the currently selected effect
	 */
	private void removeEffect() {
		Effect effect = (Effect) appliedList.getSelectedValue();
		if (effect != null) {
			applied.removeElement(effect);
			if (applied.size() > 0) {
				appliedList.setSelectedIndex(0);
			}
		}
	}
	
	/**
	 * Remove all the effects displayed
	 */
	public void removeAllEffects() {
		applied.removeAllElements();
	}
	
	/**
	 * Save the current configuration of this dialog to the properties structure provided
	 * 
	 * @param props The properties to be configured with the effects here in
	 */
	public void saveTo(Properties props) {
		props.setProperty("effects.count", ""+applied.size());
		for (int i=0;i<applied.size();i++) {
			Effect effect = (Effect) applied.get(i);
			String prefix = "effect"+i+".";
			props.setProperty(prefix+"name", effect.getEffectName());
			if (effect instanceof StorableEffect) {
				StorableEffect storeMe = (StorableEffect) effect;
				storeMe.store(prefix, props);
			}
		}
	}
	
	/**
	 * Load the an effects configuration from the given properties object
	 * 
	 * @param props The properties to load from
	 */
	public void loadFrom(Properties props) {
		removeAllEffects();
		int count = Integer.parseInt(props.getProperty("effects.count"));
		for (int i=0;i<count;i++) {
			String prefix = "effect"+i+".";
			String name = props.getProperty(prefix+"name");
			Effect effect = EffectsRegistry.getEffectByName(name);
			if (effect != null) {
				effect = effect.getInstance();
				if (effect instanceof StorableEffect) {
					StorableEffect storeMe = (StorableEffect) effect;
					storeMe.load(prefix, props);
				}
				applied.addElement(effect);
			}
		}
		hiero.applyEffects();
	}
	
	/**
	 * Add an instance of the currently selected effect
	 */
	private void addEffect() {
		Effect effect = (Effect) effectsList.getSelectedValue();
		if (effect != null) {
			applied.addElement(effect.getInstance());
		}
	}
	
	/**
	 * Test for this dialog
	 * 
	 * @param argv The arguments passed to the test
	 */
	public static void main(String[] argv) {
		EffectsDialog dialog = new EffectsDialog(null);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}
