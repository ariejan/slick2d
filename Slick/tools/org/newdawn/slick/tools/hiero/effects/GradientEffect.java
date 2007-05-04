package org.newdawn.slick.tools.hiero.effects;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * An effect to spread a gradient down the text
 * 
 * @author kevin
 */
public class GradientEffect implements Effect {
	/** The color of the top */
	private Color top = new Color(255,255,0);
	/** The color of the bottom */
	private Color bottom = new Color(255,0,0);
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
		int top = -context.getMaxGlyphHeight();
		int bottom = context.getMaxGlyphDecent();
		
		GradientPaint paint = new GradientPaint(glyph.getX(), top, this.top, glyph.getX(), bottom, this.bottom);
		g.setPaint(paint);
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
		confPanel.newTop = top;
		confPanel.newBottom = bottom;
		
		return confPanel;
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#getEffectName()
	 */
	public String getEffectName() {
		return "Gradient Effect";
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#getInstance()
	 */
	public Effect getInstance() {
		return new GradientEffect();
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.effects.Effect#setConfigurationFromPanel(javax.swing.JPanel)
	 */
	public void setConfigurationFromPanel(JPanel panel) {
		top = confPanel.newTop;
		bottom = confPanel.newBottom;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Gradient";
	}
	
	/**
	 * A panel to configure this effect
	 *
	 * @author kevin
	 */
	private class ConfigPanel extends JPanel {
		/** The button to change the color */
		private JButton topButton = new JButton("Set Top");
		/** The color for the top*/
		private Color newTop;
		
		/** The button to change the color */
		private JButton bottomButton = new JButton("Set Bottom");
		/** The color for the bottom */
		private Color newBottom;
		
		/**
		 * Create a new configuration panel
		 */
		public ConfigPanel() {
			setLayout(null);
			
			JLabel label = new JLabel("Color");
			label.setBounds(5,30,200,25);
			add(label);
			topButton.setBounds(5,55,150,25);
			add(topButton);
			
			topButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					newTop = JColorChooser.showDialog(ConfigPanel.this, "Choose Color", newTop);
				}
			});
			
			bottomButton.setBounds(5,85,150,25);
			add(bottomButton);
			
			bottomButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					newBottom = JColorChooser.showDialog(ConfigPanel.this, "Choose Color", newBottom);
				}
			});
			setSize(300,140);
		}
		
		/**
		 * @see javax.swing.JComponent#paint(java.awt.Graphics)
		 */
		public void paint(Graphics g) {
			super.paint(g);
			
			g.setColor(newTop);
			g.fillRect(160,55,100,25);
			g.setColor(Color.black);
			g.drawRect(160,55,100,25);
			g.setColor(newBottom);
			g.fillRect(160,85,100,25);
			g.setColor(Color.black);
			g.drawRect(160,85,100,25);
		}
	}
}
