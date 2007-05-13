package org.newdawn.slick.tools.hiero.effects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * The distance the shadow falls
 *
 * @author kevin
 */
public class ShadowEffect implements StorableEffect {
	/** The shadow distance */
	private float distance = 1;
	/** The color of the outline */
	private Color col = new Color(0,0,0,0.5f);
	/** The configuration panel for this effect */
	private ConfigPanel confPanel = new ConfigPanel();
	
	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#postGlyphRender(java.awt.Graphics2D, org.newdawn.slick.tools.hiero.effects.DrawingContext, org.newdawn.slick.tools.hiero.effects.Glyph)
	 */
	public void postGlyphRender(Graphics2D g, DrawingContext context, Glyph glyph) {
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#postPageRender(java.awt.Graphics2D, org.newdawn.slick.tools.hiero.effects.DrawingContext)
	 */
	public void postPageRender(Graphics2D g, DrawingContext context) {
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#preGlyphRender(java.awt.Graphics2D, org.newdawn.slick.tools.hiero.effects.DrawingContext, org.newdawn.slick.tools.hiero.effects.Glyph)
	 */
	public void preGlyphRender(Graphics2D g, DrawingContext context, Glyph glyph) {
		Color original = g.getColor();
		g.setColor(col);
		g.translate(glyph.getDrawX()+distance, glyph.getDrawY()+distance);
		g.fill(glyph.getOutlineShape());
		g.translate(-(glyph.getDrawX()+distance), -(glyph.getDrawY()+distance));
		g.setColor(original);
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#prePageRender(java.awt.Graphics2D, org.newdawn.slick.tools.hiero.effects.DrawingContext)
	 */
	public void prePageRender(Graphics2D g, DrawingContext context) {
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#getConfigurationPanel()
	 */
	public JPanel getConfigurationPanel() {
		confPanel.newCol = col;
		
		return confPanel;
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#getEffectName()
	 */
	public String getEffectName() {
		return "ShadowEffect";
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#getInstance()
	 */
	public Effect getInstance() {
		return new ShadowEffect();
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#setConfigurationFromPanel(javax.swing.JPanel)
	 */
	public void setConfigurationFromPanel(JPanel panel) {
		col = confPanel.newCol;
		col = new Color(col.getRed(), col.getGreen(), col.getBlue(), 128);
		distance = ((Integer) confPanel.spinner.getValue()).intValue();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Shadow ("+distance+")";
	}
	
	/**
	 * A panel to configure this effect
	 *
	 * @author kevin
	 */
	private class ConfigPanel extends JPanel {
		/** The width information */
		private JSpinner spinner = new JSpinner(new SpinnerNumberModel(1,1,10,1));
		/** The button to change the color */
		private JButton colButton = new JButton("Set..");
		/** The color */
		private Color newCol;
		
		/**
		 * Create a new configuration panel
		 */
		public ConfigPanel() {
			setLayout(null);
			
			JLabel label = new JLabel("Distance");
			label.setBounds(5,5,200,25);
			add(label);
			spinner.setBounds(5,30,200,25);
			add(spinner);
			label = new JLabel("Color");
			label.setBounds(5,55,200,25);
			add(label);
			colButton.setBounds(5,80,150,25);
			add(colButton);
			
			colButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					newCol = JColorChooser.showDialog(ConfigPanel.this, "Choose Shadow Color", newCol);
				}
			});
			
			setSize(300,140);
		}
		
		/**
		 * @see javax.swing.JComponent#paint(java.awt.Graphics)
		 */
		public void paint(Graphics g) {
			super.paint(g);
			
			g.setColor(newCol);
			g.fillRect(160,80,100,25);
			g.setColor(Color.black);
			g.drawRect(160,80,100,25);
		}
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.StorableEffect#load(java.lang.String, java.util.Properties)
	 */
	public void load(String prefix, Properties props) {
		int red = Integer.parseInt(props.getProperty(prefix+"red"));
		int green = Integer.parseInt(props.getProperty(prefix+"green"));
		int blue = Integer.parseInt(props.getProperty(prefix+"blue"));
		col = new Color(red,green,blue);
		distance = Float.parseFloat(props.getProperty(prefix+"distance"));
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.StorableEffect#store(java.lang.String, java.util.Properties)
	 */
	public void store(String prefix, Properties props) {
		props.setProperty(prefix+"red", ""+col.getRed());
		props.setProperty(prefix+"green", ""+col.getGreen());
		props.setProperty(prefix+"blue", ""+col.getBlue());
		props.setProperty(prefix+"distance", ""+distance);
	}
}
