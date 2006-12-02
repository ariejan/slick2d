package org.newdawn.slick.state;

import java.util.HashMap;
import java.util.Iterator;

import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.Transition;

/**
 * A state based game isolated different stages of the game (menu, ingame, hiscores, etc) into 
 * different states so they can be easily managed and maintained.
 *
 * @author kevin
 */
public abstract class StateBasedGame implements Game {
	/** The list of states making up this game */
	private HashMap states = new HashMap();
	/** The current state */
	private GameState currentState;
	/** The next state we're moving into */
	private GameState nextState;
	/** The container holding this game */
	private GameContainer container;
	/** The title of the game */
	private String title;
	
	/** The transition being used to enter the state */
	private Transition enterTransition;
	/** The transition being used to leave the state */
	private Transition leaveTransition;
	
	/**
	 * Create a new state based game
	 * 
	 * @param name The name of the game
	 */
	public StateBasedGame(String name) {
		this.title = name;
		
		currentState = new BasicGameState() {
			public int getID() {
				return -1;
			}
			public void init(GameContainer container, StateBasedGame game) throws SlickException {
			}
			public void render(StateBasedGame game, Graphics g) throws SlickException {
			}
			public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
			}
			public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
			}
		};
	}

	/**
	 * @see org.newdawn.slick.InputListener#setInput(org.newdawn.slick.Input)
	 */
	public void setInput(Input input) {
	}
	
	/**
	 * Add a state to the game. The state will be updated and maintained
	 * by the game
	 * 
	 * @param state The state to be added
	 */
	public void addState(GameState state) {
		states.put(new Integer(state.getID()), state);
		
		if (currentState.getID() == -1) {
			currentState = state;
		}
	}
	
	/**
	 * Get a state based on it's identifier
	 * 
	 * @param id The ID of the state to retrieve
	 * @return The state requested or null if no state with the specified ID exists
	 */
	public GameState getState(int id) {
		return (GameState) states.get(new Integer(id));
	}

	/**
	 * Enter a particular game state with no transition
	 * 
	 * @param id The ID of the state to enter
	 */
	public void enterState(int id) {
		enterState(id, new EmptyTransition(), new EmptyTransition());
	}
	
	/**
	 * Enter a particular game state with no transition
	 * 
	 * @param id The ID of the state to enter
	 * @param leave The transition to use when leaving the current state
	 * @param enter The transition to use when entering the new state
	 */
	public void enterState(int id, Transition leave, Transition enter) {
		leaveTransition = leave;
		enterTransition = enter;
		
		nextState = getState(id);
		if (nextState == null) {
			throw new RuntimeException("No game state registered with the ID: "+id);
		}
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public final void init(GameContainer container) throws SlickException {
		this.container = container;
		initStatesList(container);
		
		Iterator gameStates = states.values().iterator();
		
		while (gameStates.hasNext()) {
			GameState state = (GameState) gameStates.next();
		
			state.init(container, this);
		}
	}

	/**
	 * Initialise the list of states making up this game
	 * 
	 * @param container The container holding the game
	 * @throws SlickException Indicates a failure to initialise the state based game resources
	 */
	public abstract void initStatesList(GameContainer container) throws SlickException;
	
	/**
	 * @see org.newdawn.slick.Game#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public final void render(GameContainer container, Graphics g) throws SlickException {
		currentState.render(container, this, g);
		
		if (leaveTransition != null) {
			leaveTransition.render(container, g);
		} else if (enterTransition != null) {
			enterTransition.render(container, g);
		}
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public final void update(GameContainer container, int delta) throws SlickException {
		if (leaveTransition != null) {
			leaveTransition.update(container, delta);
			if (leaveTransition.isComplete()) {
				currentState.leave(container, this);
				currentState = nextState;
				nextState = null;
				leaveTransition = null;
			} else {
				return;
			}
		}
		
		if (enterTransition != null) {
			enterTransition.update(container, delta);
			if (enterTransition.isComplete()) {
				currentState.enter(container, this);
				enterTransition = null;
			} else {
				return;
			}
		}
		
		currentState.update(container, this, delta);
	}

	/**
	 * Check if the game is transitioning between states
	 * 
	 * @return True if we're transitioning between states 
	 */
	private boolean transitioning() {
		return (leaveTransition != null) || (enterTransition != null);
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
	 * Get the container holding this game
	 * 
	 * @return The game container holding this game
	 */
	public GameContainer getContainer() {
		return container;
	}
	
	/**
	 * @see org.newdawn.slick.InputListener#controllerButtonPressed(int, int)
	 */
	public void controllerButtonPressed(int controller, int button) {
		if (transitioning()) {
			return;
		}
		
		currentState.controllerButtonPressed(controller, button);
	}

	/**
	 * @see org.newdawn.slick.InputListener#controllerButtonReleased(int, int)
	 */
	public void controllerButtonReleased(int controller, int button) {
		if (transitioning()) {
			return;
		}
		
		currentState.controllerButtonReleased(controller, button);
	}

	/**
	 * @see org.newdawn.slick.InputListener#controllerDownPressed(int)
	 */
	public void controllerDownPressed(int controller) {
		if (transitioning()) {
			return;
		}
		
		currentState.controllerDownPressed(controller);
	}

	/**
	 * @see org.newdawn.slick.InputListener#controllerDownReleased(int)
	 */
	public void controllerDownReleased(int controller) {
		if (transitioning()) {
			return;
		}
		
		currentState.controllerDownPressed(controller);
	}

	/**
	 * @see org.newdawn.slick.InputListener#controllerLeftPressed(int)
	 */
	public void controllerLeftPressed(int controller) {
		if (transitioning()) {
			return;
		}
		
		currentState.controllerLeftPressed(controller);
	}

	/**
	 * @see org.newdawn.slick.InputListener#controllerLeftReleased(int)
	 */
	public void controllerLeftReleased(int controller) {
		if (transitioning()) {
			return;
		}
		
		currentState.controllerLeftReleased(controller);
	}

	/**
	 * @see org.newdawn.slick.InputListener#controllerRightPressed(int)
	 */
	public void controllerRightPressed(int controller) {
		if (transitioning()) {
			return;
		}
		
		currentState.controllerRightPressed(controller);
	}

	/**
	 * @see org.newdawn.slick.InputListener#controllerRightReleased(int)
	 */
	public void controllerRightReleased(int controller) {
		if (transitioning()) {
			return;
		}
		
		currentState.controllerRightReleased(controller);
	}

	/**
	 * @see org.newdawn.slick.InputListener#controllerUpPressed(int)
	 */
	public void controllerUpPressed(int controller) {
		if (transitioning()) {
			return;
		}
		
		currentState.controllerUpPressed(controller);
	}

	/**
	 * @see org.newdawn.slick.InputListener#controllerUpReleased(int)
	 */
	public void controllerUpReleased(int controller) {
		if (transitioning()) {
			return;
		}
		
		currentState.controllerUpReleased(controller);
	}

	/**
	 * @see org.newdawn.slick.InputListener#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		if (transitioning()) {
			return;
		}
		
		currentState.keyPressed(key, c);
	}

	/**
	 * @see org.newdawn.slick.InputListener#keyReleased(int, char)
	 */
	public void keyReleased(int key, char c) {
		if (transitioning()) {
			return;
		}
		
		currentState.keyReleased(key, c);
	}

	/**
	 * @see org.newdawn.slick.InputListener#mouseMoved(int, int, int, int)
	 */
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		if (transitioning()) {
			return;
		}
		
		currentState.mouseMoved(oldx, oldy, newx, newy);
	}

	/**
	 * @see org.newdawn.slick.InputListener#mousePressed(int, int, int)
	 */
	public void mousePressed(int button, int x, int y) {
		if (transitioning()) {
			return;
		}
		
		currentState.mousePressed(button, x, y);
	}

	/**
	 * @see org.newdawn.slick.InputListener#mouseReleased(int, int, int)
	 */
	public void mouseReleased(int button, int x, int y) {
		if (transitioning()) {
			return;
		}
		
		currentState.mouseReleased(button, x, y);
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
	
	/**
	 * @see org.newdawn.slick.InputListener#mouseWheelMoved(int)
	 */
	public void mouseWheelMoved(int newValue) {
	}

}
