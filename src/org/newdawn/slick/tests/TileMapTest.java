package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

/**
 * A test of the tile map system based around the TilED (http://www.mapeditor.org) tool
 *
 * @author kevin
 */
public class TileMapTest extends BasicGame {
	/** The tile map we're going to load and render */
	private TiledMap map;
	
	/**
	 * Create our tile map test
	 */
	public TileMapTest() {
		super("Tile Map Test");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		map = new TiledMap("testdata/testmap.tmx","testdata");
	}

	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) {
		map.render(10, 10, 4,4,15,15);
		
		g.scale(0.35f,0.35f);
		map.render(1400, 0);
		g.resetTransform();
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) {
	}

	/**
	 * @see org.newdawn.slick.BasicGame#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_ESCAPE) {
			System.exit(0);
		}
	}
	
	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments passed to the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new TileMapTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
