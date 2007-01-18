package org.newdawn.slick.tools.hiero.effects;

import java.awt.Graphics2D;

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
	 * Set a configuration property
	 * 
	 * @param key The key identifing the property
	 * @param value The value identifying the property
	 */
	public void setProperty(String key, String value);
	
	/**
	 * Get the value of a given property
	 * 
	 * @param key The key associated with the property to read
	 * @return The value for the property
	 */
	public String getProperty(String key);
	
	/**
	 * Get the properties that can be configured on this effect
	 * 
	 * @return The properties supported by this effect
	 */
	public String[] getPropertyNames();
}
