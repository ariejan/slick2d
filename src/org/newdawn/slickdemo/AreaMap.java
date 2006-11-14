package org.newdawn.slickdemo;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

/**
 * A tile map of an area
 *
 * @author kevin
 */
public class AreaMap extends TiledMap {

	/** The actors in the area */
	private ArrayList actors = new ArrayList();
	/** The central player */
	private Actor player;
	/** The graphics context */
	private Graphics g;
	/** A map of blocked areas */
	private boolean[][] blocked;
	/** The map offset */
	private int tx;
	/** The map offset */
	private int ty;
	
	/**
	 * Create a new area map
	 * 
	 * @param ref The location of the tiled map file
 	 * @throws SlickException Indicates a failure to read the map
	 */
	public AreaMap(String ref) throws SlickException {
		super(ref, "demodata");
		
		blocked = new boolean[width][height];
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				for (int i=0;i<3;i++) {
					int id = getTileId(x,y,i);
					
					if (id != 0) {
						String isBlocked = getTileProperty(id, "blocked", "false");
						if (isBlocked.equals("true")) {
							blocked[x][y] = true;
						}
					}
				}
			}
		}
	}

	/**
	 * Check if a pixel location is on a blocked tile
	 * 
	 * @param x The x coordinate (pixel level) to check
	 * @param y The y coordinate (pixel level) to check
	 * @return True if the location is on a blocked tile
	 */
	public boolean blockedAt(int x, int y) {
		return blocked[x/48][y/48];
	}
	
	/**
	 * Check if an actor at a specified location would be in a blocked area
	 * 
	 * @param x The x position of the actor
	 * @param y The y position of the actor
	 * @return True if an actor at the specified position would be intersecting a blocked area
	 */
	public boolean isBlockedForActorAt(float x, float y) {
		int xp = (int) (x+16);
		int yp = (int) (y+44);
		
		boolean blocked = false;
		blocked |= blockedAt(xp-16,yp-8);
		blocked |= blockedAt(xp+16,yp-8);
		blocked |= blockedAt(xp-16,yp+2);
		blocked |= blockedAt(xp+16,yp+2);
		
		return blocked;
	}
	
	/**
	 * Add an actor to this map
	 * 
	 * @param actor The actor to add to the map
	 */
	public void addActor(Actor actor) {
		actors.add(actor);
		actor.setAreaMap(this);
	}
	
	/**
	 * Remove an actor from this map
	 * 
	 * @param actor The actor to remove from the map
	 */
	public void removeActor(Actor actor) {
		actors.remove(actor);
	}
	
	/**
	 * Render this map to the screen. Only the section around the player will
	 * be rendered
	 * 
	 * @param g The graphics context to render the map to
	 * @param player The player we're rendering around
	 */
	public void render(Graphics g, Actor player) {
		tx = player.getX() - 400;
		ty = player.getY() - 320;
		
		tx = Math.max(tx, 0);
		ty = Math.max(ty, 0);
		tx = Math.min(tx, (width * 48) - 800);
		ty = Math.min(ty, (height * 48) - 640 + 40);
		
		this.player = player;
		this.g = g;
		
		render(g, -(tx%48),-(ty%48),tx/48,ty/48,18,15,true);
	}

	/**
	 * Render a section of the map
	 * 
	 * @param g The graphics context to render the map to
	 * @param x The x coordinate we're rendering at
	 * @param y The y coordinate we're rendering at
	 * @param sx The x coordinate of the top left corner of the section to render
	 * @param sy The y coorindate of the top left corner of the section to render
	 * @param width The width of the section to render
	 * @param height The height of the section to render
	 * @param lineByLine True if we're rendering line by line and inserting actors 
	 */
	private void render(Graphics g, int x,int y,int sx,int sy,int width,int height, boolean lineByLine) {
		Layer layer0 = (Layer) layers.get(0);
		Layer layer1 = (Layer) layers.get(1);
		Layer layer2 = (Layer) layers.get(2);
		
		for (int ty=0;ty<height;ty++) {
			layer0.render(x,y,sx,sy,width,ty,lineByLine);
		}
		
		for (int ty=0;ty<height;ty++) {
			if (ty != 0) {
				layer2.render(x,y,sx,sy,width,ty-1,lineByLine);
			}
			layer1.render(x,y,sx,sy,width,ty,lineByLine);
		}

		// DEBUG BLOCK
//		g.setColor(new Color(1,0,0,0.5f));
//		for (int ty=0;ty<height;ty++) {
//			for (int tx=0;tx<width;tx++) {
//				if (ty+sy >= this.height) {
//					continue;
//				}
//				if (tx+sx >= this.width) {
//					continue;
//				}
//				if (blocked[tx+sx][ty+sy]) {
//					g.fillRect(x+(tx*48), y+(ty*48), 48, 48);
//				}
//			}
//		}
	}
	
	/**
	 * Update the map and all the actor therin
	 * 
	 * @param delta The amount of time since the last update (in milliseconds)
	 */
	public void update(int delta) {
		for (int i=0;i<actors.size();i++) {
			Actor actor = (Actor) actors.get(i);
			actor.update(delta);
		}
	}
	
	/**
	 * @see org.newdawn.slick.tiled.TiledMap#renderedLine(int, int, int)
	 */
	protected void renderedLine(int visualY, int mapY, int layer) {
		if (layer == 1) {
			int thisOne = (mapY * 48) + 48;
			int nextOne = thisOne + 48;

			g.translate(-tx, -ty);
			for (int i=0;i<actors.size();i++) {
				Actor actor = (Actor) actors.get(i);
				int actorY = actor.getY() + 48;
				
				if ((actorY >= thisOne) && (actorY < nextOne)) {
					actor.render();
				}
			}
			g.translate(tx, ty);
		}
	}
}
