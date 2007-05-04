package org.newdawn.slick;


/**
 * A basic implementation of a game to take out the boring bits
 *
 * @author kevin
 */
public abstract class BasicGame implements Game {
	/** The title of the game */
	private String title;
	/** The state of the left control */
	protected boolean[] controllerLeft = new boolean[10];
	/** The state of the right control */
	protected boolean[] controllerRight = new boolean[10];
	/** The state of the up control */
	protected boolean[] controllerUp = new boolean[10];
	/** The state of the down control */
	protected boolean[] controllerDown = new boolean[10];
	/** The state of the button controlls */
	protected boolean[][] controllerButton = new boolean[10][10];
	
	/**
	 * Create a new basic game
	 * 
	 * @param title The title for the game
	 */
	public BasicGame(String title) {
		this.title = title;
	}

	/**
	 * @see org.newdawn.slick.InputListener#setInput(org.newdawn.slick.Input)
	 */
	public void setInput(Input input) {	
	}
	
	/**
	 * @see org.newdawn.slick.Game#closeRequested()
	 */
	public boolean closeRequested() {
		return true;
	}

	/**
	 * @see org.newdawn.slick.Game#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @see org.newdawn.slick.Game#init(org.newdawn.slick.GameContainer)
	 */
	public abstract void init(GameContainer container) throws SlickException;

	/**
	 * @see org.newdawn.slick.Game#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
	}

	/**
	 * @see org.newdawn.slick.Game#keyReleased(int, char)
	 */
	public void keyReleased(int key, char c) {
	}

	/**
	 * @see org.newdawn.slick.Game#mouseMoved(int, int, int, int)
	 */
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
	}

	/**
	 * @see org.newdawn.slick.Game#mousePressed(int, int, int)
	 */
	public void mousePressed(int button, int x, int y) {

	}
	
	/**
	 * @see org.newdawn.slick.Game#controllerButtonPressed(int, int)
	 */
	public void controllerButtonPressed(int controller, int button) {
		controllerButton[controller][button] = true;
	}

	public void controllerButtonReleased(int controller, int button) {
		controllerButton[controller][button] = false;
	}

	public void controllerDownPressed(int controller) {
		controllerDown[controller] = true;
	}

	public void controllerDownReleased(int controller) {
		controllerDown[controller] = false;
	}

	public void controllerLeftPressed(int controller) {
		controllerLeft[controller] = true;
	}

	public void controllerLeftReleased(int controller) {
		controllerLeft[controller] = false;
	}

	public void controllerRightPressed(int controller) {
		controllerRight[controller] = true;
	}

	public void controllerRightReleased(int controller) {
		controllerRight[controller] = false;
	}

	public void controllerUpPressed(int controller) {
		controllerUp[controller] = true;
	}

	public void controllerUpReleased(int controller) {
		controllerUp[controller] = false;
	}
	
	/**
	 * @see org.newdawn.slick.Game#mouseReleased(int, int, int)
	 */
	public void mouseReleased(int button, int x, int y) {
	}

	/**
	 * @see org.newdawn.slick.Game#update(org.newdawn.slick.GameContainer, int)
	 */
	public abstract void update(GameContainer container, int delta) throws SlickException;

	/**
	 * @see org.newdawn.slick.InputListener#mouseWheelMoved(int)
	 */
	public void mouseWheelMoved(int change) {
	}

	/**
	 * @see org.newdawn.slick.InputListener#isAcceptingInput()
	 */
	public boolean isAcceptingInput() {
		return true;
	}
	
	/**
	 * @see org.newdawn.slick.InputListener#inputEnded()
	 */
	public void inputEnded() {
		
	}
}
