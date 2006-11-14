package org.newdawn.slick.tests;

import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 * A test for basic animation rendering
 *
 * @author kevin
 */
public class AnimationTest extends BasicGame {
	/** The animation loaded */
	private Animation animation;
	/** The container */
	private GameContainer container;
	
	/**
	 * Create a new image rendering test
	 */
	public AnimationTest() {
		super("Animation Test");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		this.container = container;
		
		SpriteSheet sheet = new SpriteSheet("testdata/homeranim.png", 36, 65);
		animation = new Animation();
		for (int i=0;i<8;i++) {
			animation.addFrame(sheet.getSprite(i,0), 150);
		}
		
	}

	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.Graphics)
	 */
	public void render(Graphics g) {
		g.setBackground(new Color(0.4f,0.6f,0.6f));
		g.scale(-1,1);
		animation.draw(-100,100);
		animation.draw(-200,100,36*4,65*4);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) {
	}

	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new AnimationTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see org.newdawn.slick.BasicGame#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_ESCAPE) {
			container.exit();
		}
	}
}
