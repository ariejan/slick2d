package org.newdawn.slick;

import org.lwjgl.opengl.GL11;

/**
 * A wrapper to allow any game to be scalable. This relies on knowing the 
 * normal width/height of the game - i.e. the dimensions that the game is
 * expecting to be run at. The wrapper then takes the size of the container
 * and scales rendering and input based on the ratio.
 *
 * Note: Using OpenGL directly within a ScalableGame can break it
 * 
 * @author kevin
 */
public final class ScalableGame implements Game {
	/** The normal or native width of the game */
	private float normalWidth;
	/** The normal or native height of the game */
	private float normalHeight;
	/** The game that is being wrapped */
	private Game held;
	
	/** 
	 * Create a new scalable game wrapper
	 * 
	 * @param held The game to be wrapper and displayed at a different resolution
	 * @param normalWidth The normal width of the game
	 * @param normalHeight The noral height of the game
	 */
	public ScalableGame(Game held, int normalWidth, int normalHeight) {
		this.held = held;
		this.normalWidth = normalWidth;
		this.normalHeight = normalHeight;
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		if (held instanceof InputListener) {
			container.getInput().addListener((InputListener) held);
		}
		container.getInput().setScale(normalWidth / container.getWidth(),
									  normalHeight / container.getHeight());
		held.init(container);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) throws SlickException {
		held.update(container, delta);
	}

	/**
	 * @see org.newdawn.slick.Game#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		GL11.glScalef(container.getWidth() / normalWidth,
					  container.getHeight() / normalHeight,0);
		GL11.glPushMatrix();
		held.render(container, g);
		GL11.glPopMatrix();
	}

	/**
	 * @see org.newdawn.slick.Game#closeRequested()
	 */
	public boolean closeRequested() {
		return held.closeRequested();
	}

	/**
	 * @see org.newdawn.slick.Game#getTitle()
	 */
	public String getTitle() {
		return held.getTitle();
	}

}
