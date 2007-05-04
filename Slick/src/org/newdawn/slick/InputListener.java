package org.newdawn.slick;

/**
 * A listener that will be notified of keyboard, mouse and controller events
 *
 * @author kevin
 */
public interface InputListener {
	/**
	 * Set the input that events are being sent from
	 * 
	 * @param input The input instance sending events
	 */
	public void setInput(Input input);
	
	/**
	 * Check if this input listener is accepting input
	 * 
	 * @return True if the input listener should recieve events
	 */
	public boolean isAcceptingInput();
	
	/**
	 * Notification that all input events have been sent for this frame
	 */
	public void inputEnded();
	
	/**
	 * Notification that a key was pressed
	 * 
	 * @param key The key code that was pressed (@see org.newdawn.slick.Input)
	 * @param c The character of the key that was pressed
	 */
	public void keyPressed(int key, char c);

	/**
	 * Notification that a key was released
	 * 
	 * @param key The key code that was released (@see org.newdawn.slick.Input)
	 * @param c The character of the key that was released
	 */
	public void keyReleased(int key, char c);
	
	/**
	 * Notification that the mouse wheel position was updated
	 * 
	 * @param change The amount of the wheel has moved
	 */
	public void mouseWheelMoved(int change);
	
	/**
	 * Notification that a mouse button was pressed
	 * 
	 * @param button The index of the button (starting at 0)
	 * @param x The x position of the mouse when the button was pressed
	 * @param y The y position of the mouse when the button was pressed
	 */
	public void mousePressed(int button, int x, int y);

	/**
	 * Notification that a mouse button was released
	 * 
	 * @param button The index of the button (starting at 0)
	 * @param x The x position of the mouse when the button was released
	 * @param y The y position of the mouse when the button was released
	 */
	public void mouseReleased(int button, int x, int y);
	
	/**
	 * Notification that mouse cursor was moved
	 * 
	 * @param oldx The old x position of the mouse
	 * @param oldy The old y position of the mouse
	 * @param newx The new x position of the mouse
	 * @param newy The new y position of the mouse
	 */
	public void mouseMoved(int oldx, int oldy, int newx, int newy);
	
	/**
	 * Notification that the left control has been pressed on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was pressed.
	 */
	public void controllerLeftPressed(int controller);
	
	/**
	 * Notification that the left control has been released on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was released.
	 */
	public void controllerLeftReleased(int controller);

	/**
	 * Notification that the right control has been pressed on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was pressed.
	 */
	public void controllerRightPressed(int controller);

	/**
	 * Notification that the right control has been released on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was released.
	 */
	public void controllerRightReleased(int controller);

	/**
	 * Notification that the up control has been pressed on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was pressed.
	 */
	public void controllerUpPressed(int controller);
	
	/**
	 * Notification that the up control has been released on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was released.
	 */
	public void controllerUpReleased(int controller);

	/**
	 * Notification that the down control has been pressed on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was pressed.
	 */
	public void controllerDownPressed(int controller);
	
	/**
	 * Notification that the down control has been released on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was released.
	 */
	public void controllerDownReleased(int controller);

	/**
	 * Notification that a button control has been pressed on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was pressed.
	 * @param button The index of the button pressed (starting at 1)
	 */
	public void controllerButtonPressed(int controller, int button);
	
	/**
	 * Notification that a button control has been released on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was released.
	 * @param button The index of the button released (starting at 1)
	 */
	public void controllerButtonReleased(int controller, int button);
}
