package org.newdawn.slick.state.transition;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * A transition between two game states
 *
 * @author kevin
 */
public interface Transition {
	/** 
	 * Update the transition. Cause what ever happens in the transition to happen
	 * 
	 * @param container The container holding the game
	 * @param delta The amount of time passed since last update
	 */
	public void update(GameContainer container, int delta);
	
	/**
	 * Render the transition over the existing state rendering
	 * 
	 * @param container The container holding the game
	 * @param g The graphics context to use when rendering the transiton
	 */
	public void render(GameContainer container, Graphics g);
	
	/**
	 * Check if this transtion has been completed
	 * 
	 * @return True if the transition has been completed
	 */
	public boolean isComplete();
}
