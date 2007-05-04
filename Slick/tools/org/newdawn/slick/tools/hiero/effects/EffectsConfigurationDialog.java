package org.newdawn.slick.tools.hiero.effects;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A dialog to allow a single effect configuration
 *
 * @author kevin
 */
public class EffectsConfigurationDialog extends JDialog {
	/** The panel for configuration */
	private JPanel confPanel;
	/** The effect to be configured */
	private Effect effect;
	
	/**
	 * Create a new effects configuration dialog
	 * 
	 * @param parent The parent window
	 * @param effect The effect to be configured
	 */
	public EffectsConfigurationDialog(JFrame parent, final Effect effect) {
		super(parent, effect.getEffectName()+" configuration", true);

		this.effect = effect;
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		
		confPanel = effect.getConfigurationPanel();

		int width = confPanel.getWidth();
		int height = confPanel.getHeight();
		if (width < 200) {
			width = 200;
		}
		
		confPanel.setSize(width,height);
		confPanel.setPreferredSize(new Dimension(width,height));
		
		confPanel.setBounds(0,0,confPanel.getWidth(), confPanel.getHeight());
		panel.add(confPanel);
		
		JButton ok = new JButton("OK");
		JButton cancel = new JButton("Cancel");
		ok.setBounds(confPanel.getWidth()-185,confPanel.getHeight()+5,80,25);
		cancel.setBounds(confPanel.getWidth()-100,confPanel.getHeight()+5,80,25);
		panel.add(ok);
		panel.add(cancel);
		
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				effect.setConfigurationFromPanel(confPanel);
				dispose();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		setContentPane(panel);
		setSize(confPanel.getWidth(), confPanel.getHeight() + 70);
		setLocationRelativeTo(parent);
		setVisible(true);
	}
}

