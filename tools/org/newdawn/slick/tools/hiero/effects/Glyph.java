package org.newdawn.slick.tools.hiero.effects;

import java.awt.Shape;

/**
 * Information about a single glyph
 *
 * @author kevin
 */
public interface Glyph {
	/** 
	 * Get the x position of the bounding box
	 * 
	 * @return The position of the bounding box
 	 */
	public int getX();

	/** 
	 * Get the y position of the bounding box
	 * 
	 * @return The position of the bounding box
 	 */
	public int getY();
	
	/** 
	 * Get the x position the glyph was rendered at 
	 * 
	 * @return The position the glyph was rendered at
 	 */
	public int getDrawX();

	/** 
	 * Get the y position the glyph was rendered at 
	 * 
	 * @return The position the glyph was rendered at
 	 */
	public int getDrawY();

	/** 
	 * Get the width of the glyph rendered
	 * 
	 * @return The width of the glyph rendered
 	 */
	public int getWidth();

	/**
	 * Get the offset on the y-axis this glyph should be rendered at
	 * 
	 * @return The offset on the y-axis this glyph should be rendered at
	 */
	public int getYOffset();
	
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
