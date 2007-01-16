package org.newdawn.slick.tools.hiero.effects;

import java.awt.Shape;

/**
 * Information about a single glyph
 *
 * @author kevin
 */
public interface Glyph {
	/** 
	 * Get the x position the glyph was rendered at 
	 * 
	 * @return The position the glyph was rendered at
 	 */
	public int getX();

	/** 
	 * Get the y position the glyph was rendered at 
	 * 
	 * @return The position the glyph was rendered at
 	 */
	public int getY();

	/** 
	 * Get the width of the glyph rendered
	 * 
	 * @return The width of the glyph rendered
 	 */
	public int getWidth();

	/** 
	 * Get the height of the glyph rendered
	 * 
	 * @return The height of the glyph rendered
 	 */
	public int getHeight();
	
	/**
	 * Get the shape representing the glyph
	 * 
	 * @return The shape representing the glyph
	 */
	public Shape getOutlineShape();
}
