package org.newdawn.slick.tests;
	
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

/**
 * A test of the font rendering capabilities
 *
 * @author kevin
 */
public class FontTest extends BasicGame {
	/** The font we're going to use to render */
	private Font font;
	/** The image of the font to compare against */
	private Image image;
	
	/**
	 * Create a new test for font rendering
	 */
	public FontTest() {
		super("Font Test");
	}
	
	/**
	 * @see org.newdawn.slick.Game#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		font = new AngelCodeFont("testdata/demo2.fnt","testdata/demo2_00.tga");
		image = new Image("testdata/demo2_00.tga", false);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.Graphics)
	 */
	public void render(Graphics g) {
		font.drawString(80, 5, "A Font Example", Color.red);
		font.drawString(100, 32, "Here is a more complete line that hopefully");
		font.drawString(100, 36 + font.getHeight("Here is a more complete line that hopefully"), 
				             "will show some kerning.");
		image.draw(100,200);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) throws SlickException {
	}
	
	/**
	 * @see org.newdawn.slick.Game#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_ESCAPE) {
			System.exit(0);
		}
		if (key == Input.KEY_SPACE) {
			try {
				container.setDisplayMode(640, 480, false);
			} catch (SlickException e) {
				Log.error(e);
			}
		}
	}
	
	/** The container we're using */
	private static AppGameContainer container;
	
	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments passed in the test
	 */
	public static void main(String[] argv) {
		try {
			container = new AppGameContainer(new FontTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
