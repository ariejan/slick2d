package org.newdawn.slick.gui;

import org.newdawn.slick.Font;
import org.newdawn.slick.Input;

/**
 * The context in which GUI components are created and rendered
 *
 * @author kevin
 */
public interface GUIContext {

	/**
	 * Get the input system
	 * 
	 * @return The input system available to this game container
	 */
	public Input getInput();
	
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime();
	
	/**
	 * Get the width of the standard screen resolution
	 * 
	 * @return The screen width
	 */
	public abstract int getScreenWidth();
	
	/**
	 * Get the height of the standard screen resolution
	 * 
	 * @return The screen height
	 */
	public abstract int getScreenHeight();
	
	/**
	 * Get the width of the game canvas
	 * 
	 * @return The width of the game canvas
	 */
	public int getWidth();
	
	/**
	 * Get the height of the game canvas
	 * 
	 * @return The height of the game canvas
	 */
	public int getHeight();
	
	/**
	 * Get the default system font
	 * 
	 * @return The default system font
	 */
	public Font getDefaultFont();
}
