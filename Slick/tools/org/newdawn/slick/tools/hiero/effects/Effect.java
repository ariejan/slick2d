package org.newdawn.slick.tools.hiero.effects;

import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * An effect to render text in some exciting manner
 *
 * @author kevin
 */
public interface Effect {
	/**
	 * Notification that we're about to render a page
	 * 
	 * @param g The graphics context to draw to
 	 * @param context The context in which the font is being rendered
	 */
	public void prePageRender(Graphics2D g, DrawingContext context);

	/**
	 * Notification that we're about to render a glyph
	 * 
	 * @param g The graphics context to draw to
	 * @param glyph The inforation about the glyph to be rendered
 	 * @param context The context in which the font is being rendered
	 */
	public void preGlyphRender(Graphics2D g, DrawingContext context, Glyph glyph);

	/**
	 * Notification that we've just rendered a glyph
	 * 
	 * @param g The graphics context to draw to
	 * @param glyph The inforation about the glyph to be rendered
 	 * @param context The context in which the font is being rendered
	 */
	public void postGlyphRender(Graphics2D g, DrawingContext context, Glyph glyph);

	/**
	 * Notification that we've just rendered a page
	 * 
	 * @param g The graphics context to draw to
 	 * @param context The context in which the font is being rendered
	 */
	public void postPageRender(Graphics2D g, DrawingContext context);
	
	/**
	 * Get a panel that can be used to configure this effect
	 * 
	 * @return A panel that can be used to configure this effect
	 */
	public JPanel getConfigurationPanel();
	
	/**
	 * Set the configuration of the effect
	 * 
	 * @param panel The panel that was used to setup the configuration. This is guaranteed to 
	 * be the same type that was supplied from {@link #getConfigurationPanel()}
	 */
	public void setConfigurationFromPanel(JPanel panel);
	
	/**
	 * Get the name to use to describe this effect
	 * 
	 * @return The name of the effect
	 */
	public String getEffectName();

	/**
	 * Get a new instance of this effect
	 * 
	 * @return The new instance of this effect
	 */
	public Effect getInstance();
}
