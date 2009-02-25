package org.newdawn.slick;

public interface ControlledInputReciever {

	/**
	 * Set the input that events are being sent from
	 * 
	 * @param input The input instance sending events
	 */
	public abstract void setInput(Input input);

	/**
	 * Check if this input listener is accepting input
	 * 
	 * @return True if the input listener should recieve events
	 */
	public abstract boolean isAcceptingInput();

	/**
	 * Notification that all input events have been sent for this frame
	 */
	public abstract void inputEnded();

}