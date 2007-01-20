package org.newdawn.slick.tools.hiero.effects;

/**
 * The context in which the glyphs are being drawn
 *
 * @author kevin
 */
public interface DrawingContext {
	/** 
	 * Get the width of the texture being rendered
	 * 
	 * @return The width of the texture being rendered
	 */
	public int getTextureWidth();

	/** 
	 * Get the height of the texture being rendered
	 * 
	 * @return The height of the texture being rendered
	 */
	public int getTextureHeight();
	
	/**
	 * Get the maximum glyph height
	 * 
	 * @return Get the maximum glyph height
	 */
	public int getMaxGlyphHeight();
	
	/**
	 * Get the maximum glyph decent
	 * 
	 * @return Get the maximum glyph decent
	 */
	public int getMaxGlyphDecent();
}
