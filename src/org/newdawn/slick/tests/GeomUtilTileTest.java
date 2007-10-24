package org.newdawn.slick.tests;

import java.util.ArrayList;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.GeomUtil;
import org.newdawn.slick.geom.GeomUtilListener;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

/**
 * A test to try shape building from multiple tiles
 * 
 * @author Kevin Glass
 */
public class GeomUtilTileTest extends BasicGame implements GeomUtilListener {
	/** The shape we're cutting out of */
	private Shape source;
	/** The shape we're cutting */
	private Shape cut;
	/** The resulting shape */
	private Shape[] result;

	/** The util under test */
	private GeomUtil util = new GeomUtil();

	/** The original list of shapes */
	private ArrayList original = new ArrayList();
	/** The original list of shapes */
	private ArrayList combined = new ArrayList();

	/** The list of intersection points */
	private ArrayList intersections = new ArrayList();
	/** The list of used points */
	private ArrayList used = new ArrayList();

	/**
	 * Create a simple test
	 */
	public GeomUtilTileTest() {
		super("GeomUtilTileTest");
	}

	/**
	 * Perform the cut
	 */
	public void init() {
		int[][] map = new int[][] { 
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 4, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
//				
				{ 0, 0, 0, 0, 0, 0, 0, 3, 0, 0 },
				{ 0, 1, 1, 1, 0, 0, 1, 1, 1, 0 },
				{ 0, 1, 1, 0, 0, 0, 5, 1, 6, 0 },
				{ 0, 1, 2, 0, 0, 0, 4, 1, 1, 0 },
				{ 0, 1, 1, 0, 0, 0, 1, 1, 0, 0 },
				{ 0, 0, 0, 0, 3, 0, 1, 1, 0, 0 },
				{ 0, 0, 0, 1, 1, 0, 0, 0, 1, 0 },
				{ 0, 0, 0, 1, 1, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
				};
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				if (map[y][x] != 0) {
					switch (map[y][x]) {
					case 1:
						Polygon p2 = new Polygon();
						p2.addPoint(x * 32, y * 32);
						p2.addPoint((x * 32) + 32, y * 32);
						p2.addPoint((x * 32) + 32, (y * 32) + 32);
						p2.addPoint(x * 32, (y * 32) + 32);
						original.add(p2);
						//original.add(new Rectangle(x*32,y*32,33,33));
						break;
					case 2:
						Polygon poly = new Polygon();
						poly.addPoint(x * 32, y * 32);
						poly.addPoint((x * 32) + 32, y * 32);
						poly.addPoint(x * 32, (y * 32) + 32);
						original.add(poly);
						break;
					case 3:
						Circle ellipse = new Circle((x*32)+16,(y*32)+32,16,16);
						original.add(ellipse);
						break;
					case 4:
						Polygon p = new Polygon();
						p.addPoint((x * 32) + 32, (y * 32));
						p.addPoint((x * 32) + 32, (y * 32)+32);
						p.addPoint(x * 32, (y * 32) + 32);
						original.add(p);
						break;
					case 5:
						Polygon p3 = new Polygon();
						p3.addPoint((x * 32), (y * 32));
						p3.addPoint((x * 32) + 32, (y * 32));
						p3.addPoint((x * 32) + 32, (y * 32)+32);
						original.add(p3);
						break;
					case 6:
						Polygon p4 = new Polygon();
						p4.addPoint((x * 32), (y * 32));
						p4.addPoint((x * 32) + 32, (y * 32));
						p4.addPoint((x * 32), (y * 32)+32);
						original.add(p4);
						break;
					}
//					Polygon poly = new Polygon();
//					poly.addPoint(x * 64, y * 32);
//					poly.addPoint((x * 64) + 64, y * 32);
//					poly.addPoint((x * 64) + 64, (y * 32) + 32);
//					poly.addPoint(x * 64, (y * 32) + 32);
					//original.add(poly);
				}
			}
		}

		combined = combine(original);
	}

	/**
	 * Combine a set of shapes together
	 * 
	 * @param shapes
	 *            The shapes to be combined
	 * @return The list of combined shapes
	 */
	private ArrayList combine(ArrayList shapes) {
		ArrayList last = shapes;
		ArrayList current = shapes;
		boolean first = true;

		while ((current.size() != last.size()) || (first)) {
			first = false;
			last = current;
			current = combineImpl(current);
		}

		ArrayList pruned = new ArrayList();
		for (int i = 0; i < current.size(); i++) {
			pruned.add(((Shape) current.get(i)).prune());
		}
		return pruned;
	}

	/**
	 * Attempt to find a simple combination that can be performed
	 * 
	 * @param shapes
	 *            The shapes to be combined
	 * @return The new list of shapes - this will be the same length as the
	 *         input if there are no new combinations
	 */
	private ArrayList combineImpl(ArrayList shapes) {
		ArrayList result = new ArrayList(shapes);

		for (int i = 0; i < shapes.size(); i++) {
			Shape first = (Shape) shapes.get(i);
			for (int j = i + 1; j < shapes.size(); j++) {
				Shape second = (Shape) shapes.get(j);

				Shape[] joined = util.union(first, second);
				if (joined.length == 1) {
					result.remove(first);
					result.remove(second);
					result.add(joined[0]);
					return result;
				}
			}
		}

		return result;
	}

	/**
	 * @see BasicGame#init(GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		util.setListener(this);
		init();
		//container.setVSync(true);
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
		for (int i = 0; i < original.size(); i++) {
			Shape shape = (Shape) original.get(i);
			g.draw(shape);
		}

		g.translate(0, 320);
		g.setColor(Color.white);

		for (int i = 0; i < combined.size(); i++) {
			g.setColor(Color.white);
			Shape shape = (Shape) combined.get(i);
			g.draw(shape);
			for (int j = 0; j < shape.getPointCount(); j++) {
				g.setColor(Color.yellow);
				float[] pt = shape.getPoint(j);
				g.fillOval(pt[0] - 1, pt[1] - 1, 3, 3);
			}
		}

	}

	/**
	 * Entry point to our test
	 * 
	 * @param argv
	 *            The arguments passed to the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(
					new GeomUtilTileTest());
			container.setDisplayMode(800, 600, false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public void pointExcluded(float x, float y) {
	}

	public void pointIntersected(float x, float y) {
		intersections.add(new Vector2f(x, y));
	}

	public void pointUsed(float x, float y) {
		used.add(new Vector2f(x, y));
	}
}
