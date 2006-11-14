package org.newdawn.slick;


/**
 * The proprites of any font implementation
 * 
 * @author Kevin Glass
 */
public interface Font {
	/**
	 * Get the width of the given string
	 * 
	 * @param str The string to obtain the rendered with of
	 * @return The width of the given string
	 */
	public abstract int getWidth(String str);
	
	/**
	 * Get the height of the given string
	 * 
	 * @param str The string to obtain the rendered with of
	 * @return The width of the given string
	 */
	public abstract int getHeight(String str);
	
	/**
	 * Draw a string to the screen
	 * 
	 * @param x The x location at which to draw the string
	 * @param y The y location at which to draw the string
	 * @param text The text to be displayed
	 */
	public abstract void drawString(int x, int y, String text);

	/**
	 * Draw a string to the screen
	 * 
	 * @param x The x location at which to draw the string
	 * @param y The y location at which to draw the string
	 * @param text The text to be displayed
	 * @param col The colour to draw with
	 */
	public abstract void drawString(int x, int y, String text, Color col);
}