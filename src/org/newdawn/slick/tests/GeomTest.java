package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.renderable.Ellipse;
import org.newdawn.slick.geom.renderable.Polygon;
import org.newdawn.slick.geom.renderable.Shape;
import org.newdawn.slick.geom.renderable.Transform;

/**
 * A geomertry test
 *
 * @author kevin
 */
public class GeomTest extends BasicGame {
	/** The rectangle drawn */
	private Rectangle rect = new Rectangle(100,100,100,100);
	/** The rectangle drawn */
	private Circle circle = new Circle(500,200,50);
	/** The rectangle tested */
	private Rectangle rect1 = new Rectangle(200,170,50,100);
	/** The rectangle tested */
	private Rectangle rect2 = new Rectangle(310,210,50,100);
	/** The circle tested */
	private Circle circle1 = new Circle(150,90,30);
	/** The circle tested */
	private Circle circle2 = new Circle(310,110,70);
	/** The circle tested */
	private Circle circle3 = new Circle(510,150,70);
	/** The circle tested */
	private Circle circle4 = new Circle(510,350,30);
    /** Ellipse for scaling and translation */
    private Ellipse ellipse1 = new Ellipse(250.0f, 250.0f, 70.0f, 120.0f);
    /** Ellipse for scaling and translation */
    private Shape ellipse2 = new Ellipse(0, 0, 70, 120, 360).transform(
            Transform.createRotateTransform(45)).transform(Transform.createTranslateTransform(100, 300));
    /** Polygon for scaling and translation */
    private Polygon polygon1 = new Polygon(400, 300, 50, 120);
    /** Polygon for scaling and translation */
    private Shape polygon2 = new Polygon(0, 0, 120, 120).transform(
            Transform.createRotateTransform(25)).transform(Transform.createTranslateTransform(600, 250));
    /** Polygon for scaling and translation */
    private Polygon polygon3 = new Polygon(new float[]{30,30,80,20,150,100,120,200,70,220,30,130});
    /** Polygon for scaling and translation */
    private Shape polygon4 = polygon3.transform(
                    Transform.createRotateTransform(65, polygon3.getCenter()[0], 
                            polygon3.getCenter()[1]));
	
	/**
	 * Create a new test of graphics context rendering
	 */
	public GeomTest() {
		super("Geom Test");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
	}

	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) {
		g.setColor(Color.white);
		g.drawString("Red indicates a collision, green indicates no collision", 50, 420);
        g.drawString("White are the targets", 50, 435);
        g.drawString("Orange are shapes that have had a transformation applied", 50, 450);
		
		g.setColor(Color.white);
		g.draw(rect);
		g.draw(circle);
		
		g.setColor(rect1.intersects(rect) ? Color.red : Color.green);
		g.draw(rect1);
		g.setColor(rect2.intersects(rect) ? Color.red : Color.green);
		g.draw(rect2);
		g.setColor(circle1.intersects(rect) ? Color.red : Color.green);
		g.draw(circle1);
		g.setColor(circle2.intersects(rect) ? Color.red : Color.green);
		g.draw(circle2);
		g.setColor(circle3.intersects(circle) ? Color.red : Color.green);
		g.draw(circle3);
		g.setColor(circle4.intersects(circle) ? Color.red : Color.green);
		g.draw(circle4);
        
        g.setColor(Color.orange);
        g.draw(ellipse1);
        g.fill(ellipse2);
        g.draw(polygon1);
        g.fill(polygon2);
        g.draw(polygon3);
        g.fill(polygon4);
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
			AppGameContainer container = new AppGameContainer(new GeomTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
