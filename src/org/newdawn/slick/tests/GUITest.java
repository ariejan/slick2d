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
import org.newdawn.slick.gui.BasicComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.gui.TextField;

/**
 * A test for the GUI components available in Slick. Very simple stuff
 *
 * @author kevin
 */
public class GUITest extends BasicGame implements ComponentListener {
	/** The image being rendered */
	private Image image;
	/** The areas defined */
	private MouseOverArea[] areas = new MouseOverArea[4];
	/** The game container */
	private GameContainer container;
	/** The message to display */
	private String message = "Demo Menu System with stock images";
	/** The text field */
	private TextField field;
	/** The background image */
	private Image background;
	/** The font used to render */
	private Font font;
	
	/**
	 * Create a new test of GUI  rendering
	 */
	public GUITest() {
		super("gui Test");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		font = new AngelCodeFont("testdata/demo2.fnt","testdata/demo2_00.tga");
		field = new TextField(container, font, 150,20,500,35, this);
		
		this.container = container;
		
		image = new Image("testdata/logo.tga", true);
		background = new Image("testdata/dungeontiles.gif", true);
		container.setMouseCursor("testdata/cursor.tga", 0, 0);
		
		for (int i=0;i<4;i++) {
			areas[i] = new MouseOverArea(container, image, 300, 100 + (i*100), 200, 90, this);
			areas[i].setNormalColor(new Color(1,1,1,0.8f));
			areas[i].setMouseOverColor(new Color(1,1,1,0.9f));
		}
	}

	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.Graphics)
	 */
	public void render(Graphics g) {
		background.draw(0, 0, 800, 500);
		
		for (int i=0;i<4;i++) {
			areas[i].render(container, g);
		}
		field.render(container, g);
		
		g.setFont(font);
		g.drawString(message, 200, 550);
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
			AppGameContainer container = new AppGameContainer(new GUITest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see org.newdawn.slick.gui.ComponentListener#componentActivated(org.newdawn.slick.gui.BasicComponent)
	 */
	public void componentActivated(BasicComponent source) {
		for (int i=0;i<4;i++) {
			if (source == areas[i]) {
				message = "Option "+(i+1)+" pressed!";
			}
		}
		if (source == field) {
			message = "Entered: "+field.getText();
		}
	}
}
