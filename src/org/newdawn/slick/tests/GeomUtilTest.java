package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.GeomUtil;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;

/**
 * A test to try shape cutting
 * 
 * @author Kevin Glass
 */
public class GeomUtilTest extends BasicGame {
	/** The shape we're cutting out of */
	private Shape source;
	/** The shape we're cutting */
	private Shape cut;
	/** The resulting shape */
	private Shape result;
	
	/**
	 * Create a simple test
	 */
	public GeomUtilTest() {
		super("GeomUtilTest");
	}

	/**
	 * Perform the cut
	 */
	public void init() {
		Polygon source = new Polygon();
		source.addPoint(100,100);
		source.addPoint(150,80);
		source.addPoint(210,120);
		source.addPoint(210,120);
		source.addPoint(240,150);
		source.addPoint(150,200);
		source.addPoint(120,250);
		this.source = source;
		
		Circle cut = new Circle(200,150,50);
		this.cut = cut;
		
		result = GeomUtil.subtract(source, cut);
	}
	
	/**
	 * @see BasicGame#init(GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		init();
	}

	/**
	 * @see BasicGame#update(GameContainer, int)
	 */
	public void update(GameContainer container, int delta)
			throws SlickException {
	}

	/**
	 * @see org.newdawn.slick.Game#render(GameContainer, Graphics)
	 */
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		g.setColor(Color.green);
		g.draw(source);
		g.setColor(Color.red);
		g.draw(cut);
		g.translate(0,300);
		g.setColor(Color.white);
		g.draw(result);
	}
	
	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments passed to the test
	 */
	public static void main(String[] argv) {
		GeomUtilTest test = new GeomUtilTest();
		test.init();
		
		try {
			AppGameContainer container = new AppGameContainer(new GeomUtilTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
