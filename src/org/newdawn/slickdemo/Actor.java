package org.newdawn.slickdemo;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;

/**
 * An actor wandering around in our RPG world
 *
 * @author kevin
 */
public class Actor {
	/** Indicates the actor is facing south */
	public static final int SOUTH = 0;
	/** Indicates the actor is facing south west */
	public static final int SOUTH_WEST = 1;
	/** Indicates the actor is facing west */
	public static final int WEST = 2;
	/** Indicates the actor is facing north west */
	public static final int NORTH_WEST = 3;
	/** Indicates the actor is facing north */
	public static final int NORTH = 4;
	/** Indicates the actor is facing north east */
	public static final int NORTH_EAST = 5;
	/** Indicates the actor is facing east */
	public static final int EAST = 6;
	/** Indicates the actor is facing south east */
	public static final int SOUTH_EAST = 7;
	/** Indicates the actor is facing stopped */
	public static final int STOPPED = 8;
	
	/** The animations for each direction */
	private Animation[] dirs = new Animation[8];
	/** The movement in each direction */
	private int[][] moves = new int[][] {{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1},{1,0},{1,1},{0,0}};
	
	/** The direction the actor is facing */
	private int currentDir = SOUTH;
	/** The actor's current x position */
	private float x = 200;
	/** The actor's current y position */
	private float y = 100;
	/** True if the actor isn't moving */
	private boolean stopped = false;
	/** The map the actor is wandering around */
	private AreaMap map;
	
	/**
	 * Create a new actor
	 * 
	 * @param src The sprite sheet for this actor
	 * @param index The index of the actor's sprite
	 */
	public Actor(SpriteSheet src, int index) {
		int basey = index * 2;
		
		for (int a=0;a<8;a++) {
			int basex = (a % 4) * 3;
			dirs[a] = new Animation();

			dirs[a].addFrame(src.getSprite(basex+0, basey+(a/4)), 150);
			dirs[a].addFrame(src.getSprite(basex+1, basey+(a/4)), 150);
			dirs[a].addFrame(src.getSprite(basex+0, basey+(a/4)), 150);
			dirs[a].addFrame(src.getSprite(basex+2, basey+(a/4)), 150);
		}
		
		stopAnimation();
	}
	
	/**
	 * Set the map this actor is wandering around
	 * 
	 * @param map The map the actor is wandering around
	 */
	public void setAreaMap(AreaMap map) {
		this.map = map;
	}
	
	/**
	 * Indicate in which direction of the actor is moving
	 * 
	 * @param dir The direction the actor is moving
	 */
	public void setMoving(int dir) {
		if (dir == STOPPED) {
			stopped = true;
			stopAnimation();
		} else {
			stopped = false;
			currentDir = dir;
			startAnimation();
		}
	}
	
	/**
	 * Update the actor's state, animation, position etc.
	 * 
	 * @param delta The amount of time thats passed since last update (in milliseconds)
	 */
	public void update(int delta) {
		if (!stopped) {
			float oldx = x;
			float oldy = y;
			
			x += moves[currentDir][0] * delta * 0.1f;
			y += moves[currentDir][1] * delta * 0.1f;
		
			if (map.isBlockedForActorAt(x, y)) {
				if (!map.isBlockedForActorAt(x, oldy)) {
					y = oldy;
				} else if (!map.isBlockedForActorAt(oldx, y)) {
					x = oldx;
				} else {
					x = oldx;
					y = oldy;
				}
			}
		}
	}
	
	/**
	 * Get the x position of this actor
	 * 
	 * @return The x position of this actor
	 */
	public int getX() {
		return (int) x;
	}

	/**
	 * Get the y position of this actor
	 * 
	 * @return The y position of this actor
	 */
	public int getY() {
		return (int) y;
	}
	
	/**
	 * Render the actor to the screen
	 */
	public void render() {
		dirs[currentDir].draw(getX(), getY());
	}
	
	/**
	 * Stop the actor animating
	 */
	private void stopAnimation() {
		for (int i=0;i<8;i++) {
			dirs[i].stop();
		}
	}
	
	/**
	 * Start the actor animating
	 */
	private void startAnimation() {
		for (int i=0;i<8;i++) {
			dirs[i].start();
		}
	}
}
