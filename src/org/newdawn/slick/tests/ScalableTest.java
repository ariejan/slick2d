package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;

/**
 * A test for a scalable game
 *
 * @author kevin
 */
public class ScalableTest {
	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		// normal res
//		try {
//			AppGameContainer container = new AppGameContainer(new ScalableGame(new InputTest(),600,600));
//			container.setDisplayMode(600,600,false);
//			container.start();
//		} catch (SlickException e) {
//			e.printStackTrace();
//		}
		// smaller
		try {
			AppGameContainer container = new AppGameContainer(new ScalableGame(new InputTest(),600,600));
			container.setDisplayMode(300,300,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
		// bigger
//		try {
//			AppGameContainer container = new AppGameContainer(new ScalableGame(new InputTest(),600,600));
//			container.setDisplayMode(800,800,false);
//			container.start();
//		} catch (SlickException e) {
//			e.printStackTrace();
//		}
	}
}
