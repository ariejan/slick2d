package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SlickException;

/**
 * A test for the sound system of the library
 * 
 * @author kevin
 */
public class SoundTest extends BasicGame {
	/** The sound to be played */
	private Sound sound;
	/** The sound to be played */
	private Sound charlie;
	/** The music to be played */
	private Music music;
	/** The sound to be played */
	private Sound engine;
	
	/**
	 * Create a new test for sounds
	 */
	public SoundTest() {
		super("Sound Test");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		sound = new Sound("testdata/restart.ogg");
		charlie = new Sound("testdata/cbrown01.wav");
		engine = new Sound("testdata/engine.wav");
		music = new Music("testdata/SMB-X.XM");
		music.loop(1.0f,0.5f);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) {
		g.drawString("Press space for sound effect (OGG)",100,100);
		g.drawString("Press P to pause/resume music (XM)",100,130);
		g.drawString("Press E to pause/resume engine sound (WAV)",100,190);
		g.drawString("Press enter for charlie (WAV)",100,160);
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
		if (key == Input.KEY_SPACE) {
			sound.play();
		}
		if (key == Input.KEY_RETURN) {
			charlie.play(1.0f,1.0f);
		}
		if (key == Input.KEY_P) {
			if (music.playing()) {
				music.pause();
			} else {
				music.loop();
			}
		}
		if (key == Input.KEY_E) {
			if (engine.playing()) {
				engine.stop();
			} else {
				engine.loop();
			}
		}
	}
	
	/**
	 * Entry point to the sound test
	 * 
	 * @param argv The arguments provided to the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new SoundTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
