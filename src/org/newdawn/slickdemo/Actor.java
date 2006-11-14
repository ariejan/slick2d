package org.newdawn.slickdemo;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class Actor {
	public static final int SOUTH = 0;
	public static final int SOUTH_WEST = 1;
	public static final int WEST = 2;
	public static final int NORTH_WEST = 3;
	public static final int NORTH = 4;
	public static final int NORTH_EAST = 5;
	public static final int EAST = 6;
	public static final int SOUTH_EAST = 7;
	public static final int STOPPED = 8;
	
	private Animation[] dirs = new Animation[8];
	private int[][] moves = new int[][] {{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1},{1,0},{1,1},{0,0}};
	
	private int currentDir = SOUTH;
	private float x = 200;
	private float y = 100;
	private boolean stopped = false;
	
	private AreaMap map;
	
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
		
		stop();
	}
	
	public void setAreaMap(AreaMap map) {
		this.map = map;
	}
	
	public void setMoving(int dir) {
		if (dir == STOPPED) {
			stopped = true;
			stop();
		} else {
			stopped = false;
			currentDir = dir;
			start();
		}
	}
	
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
	
	public int getX() {
		return (int) x;
	}
	
	public int getY() {
		return (int) y;
	}
	
	public void render() {
		dirs[currentDir].draw(getX(), getY());
	}
	
	private void stop() {
		for (int i=0;i<8;i++) {
			dirs[i].stop();
		}
	}
	
	private void start() {
		for (int i=0;i<8;i++) {
			dirs[i].start();
		}
	}
}
