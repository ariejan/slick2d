package org.newdawn.slickdemo;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 * A demo for YASL, a little RPG called "The Light of Laminos"
 *
 * @author kevin
 */
public class Laminos extends BasicGame {
	/** The game area we're currently in */
	private AreaMap map;
	/** The actor representing our player */
	private Actor player;
	/** The controls */
	private boolean left;
	/** The controls */
	private boolean right;
	/** The controls */
	private boolean up;
	/** The controls */
	private boolean down;
	
	/**
	 * Create a new instance of the Laminos game
	 */
	public Laminos() {
		super("The Light of Laminos");
	}

	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		container.setIcon("demodata/icon.png");
		container.setShowFPS(true);
		
		map = new AreaMap("demodata/test.tmx");
		
		SpriteSheet chars1 = new SpriteSheet("demodata/1.png",32,48);
		player = new Actor(chars1, 0);
		
		map.addActor(player);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.Graphics)
	 */
	public void render(Graphics g) throws SlickException {
		map.render(g, player);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) throws SlickException {
		if (up) {
			if (left) {
				player.setMoving(Actor.NORTH_WEST);
			} else if (right) {
				player.setMoving(Actor.NORTH_EAST);
			} else {
				player.setMoving(Actor.NORTH);
			}
		} else if (down) {
			if (left) {
				player.setMoving(Actor.SOUTH_WEST);
			} else if (right) {
				player.setMoving(Actor.SOUTH_EAST);
			} else {
				player.setMoving(Actor.SOUTH);
			}
		} else {
			if (left) {
				player.setMoving(Actor.WEST);
			} else if (right) {
				player.setMoving(Actor.EAST);
			} else {
				player.setMoving(Actor.STOPPED);
			}
		}
		
		map.update(delta);
	}

	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments passed into our test
 	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new Laminos());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public void keyPressed(int key, char c) {
		if (key == Input.KEY_LEFT) {
			left = true;
		}
		if (key == Input.KEY_RIGHT) {
			right = true;
		}
		if (key == Input.KEY_DOWN) {
			down = true;
		}
		if (key == Input.KEY_UP) {
			up = true;
		}
	}

	public void keyReleased(int key, char c) {
		if (key == Input.KEY_LEFT) {
			left = false;
		}
		if (key == Input.KEY_RIGHT) {
			right = false;
		}
		if (key == Input.KEY_DOWN) {
			down = false;
		}
		if (key == Input.KEY_UP) {
			up = false;
		}
	}
}
