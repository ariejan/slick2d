package org.newdawn.slick.tests;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.pbuffer.GraphicsFactory;

/**
 * A test for rendering to an image
 *
 * @author kevin
 */
public class ImageGraphicsTest extends BasicGame {
	/** The image rendered to */
	private Image target;
	/** The image cut from the screen */
	private Image cut;
	/** The offscreen graphics */
	private Graphics offscreen;
	/** The image loaded */
	private Image testImage;
	/** The font loaded */
	private Font testFont;
	/** The angle of the rotation */
	private float ang;
	/** The name of the dynamic image technique in use */
	private String using = "none";
	
	/**
	 * Create a new image rendering test
	 */
	public ImageGraphicsTest() {
		super("Image Graphics Test");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		testImage = new Image("testdata/logo.png");
		testFont = new AngelCodeFont("testdata/hiero.fnt","testdata/hiero.png");
		target = new Image(400,300);
		cut = new Image(100,100);
		offscreen = target.getGraphics();
		
		if (GraphicsFactory.usingFBO()) {
			using = "FBO (Frame Buffer Objects)";
		} else if (GraphicsFactory.usingPBuffer()) {
			using = "Pbuffer (Pixel Buffers)";
		}
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) throws SlickException {
		offscreen.clear();
		offscreen.rotate(200,160,ang);
		offscreen.setFont(testFont);
		offscreen.fillRect(10, 10, 50, 50);
		offscreen.drawString("HELLO WORLD",10,10);

		offscreen.drawImage(testImage,100,150);
		offscreen.drawImage(testImage,100,50);
		offscreen.drawImage(testImage,50,75);
		offscreen.flush();
		
		g.setColor(Color.white);
		g.drawString("Testing Font On Back Buffer", 10, 100);
		g.drawString("Using: "+using, 10, 580);
		g.setColor(Color.red);
		g.fillRect(10,120,200,5);
		
		target.draw(300,100);
		target.draw(300,410,200,150);
		target.draw(505,410,100,75);
		
		g.setColor(Color.white);
		g.drawString("Testing On Offscreen Buffer", 300, 80);
		g.setColor(Color.green);
		g.drawRect(300, 100, target.getWidth(), target.getHeight());
		g.drawRect(300, 410, target.getWidth()/2, target.getHeight()/2);
		g.drawRect(505, 410, target.getWidth()/4, target.getHeight()/4);
		
		int xp = (int) (60 + (Math.sin(ang / 60) * 50));
		g.copyArea(cut,xp,50);
		
		cut.draw(30,250);
		g.setColor(Color.white);
		g.drawRect(30, 250, cut.getWidth(), cut.getHeight());
		g.setColor(Color.gray);
		g.drawRect(xp, 50, cut.getWidth(), cut.getHeight());
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) {
		ang += delta * 0.1f;
	}

	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new ImageGraphicsTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
