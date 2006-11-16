package org.newdawn.slick.geom;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;

/**
 * A polygon to be draw to the graphcis context
 *
 * @author kevin
 */
public class Polygon {
	/** The triagulator holding the triangles for this polygon */
	private Triangulator tris = new Triangulator();
	/** True if the points have been updated since last render */
	private boolean updated;
	/** The points */
	private ArrayList points = new ArrayList();
	
	/**
	 * Create an empty polygon
	 */
	public Polygon() {
		
	}
	
	/**
	 * Create a polygon from a set of points
	 * 
	 * @param xpoints The x coordinates of the points
	 * @param ypoints The y coordinates of the points
	 */
	public Polygon(float[] xpoints, float[] ypoints) {
		if (xpoints.length == ypoints.length) {
			for (int i=0;i<xpoints.length;i++) {
				addPoint(xpoints[i], ypoints[i]);
			}
		} else {
			throw new RuntimeException("xpoints array must be the same length as ypoints array");
		}
	}
	
	/**
	 * Create a polygon from a set of points
	 * 
	 * @param points The points for the coordinates (points[n][2])
	 */
	public Polygon(float[][] points) {
		for (int i=0;i<points.length;i++) {
			addPoint(points[i][0], points[i][1]);
		}
	}
	
	/**
	 * Add a point to the polygon
	 * 
	 * @param x The x coordinate of the point
 	 * @param y The y coordinate of the point
	 */
	public void addPoint(float x, float y) {
		points.add(new float[] {x,y});
		tris.addPolyPoint(x, y);
		updated = true;
	}
	
	/**
	 * Get the number of points in this polygon
	 * 
	 * @return The number of points in this polygon
	 */
	public int getPointCount() {
		return points.size();
	}
	
	/**
	 * Get a single point in this polygon
	 * 
	 * @param index The index of the point to retrieve
	 * @return The point's coordinates
	 */
	public float[] getPoint(int index) {
		return (float[]) points.get(index);
	}
	
	/**
	 * Render the polygon 
	 * 
	 * @param g The graphics context on which to render 
	 */
	public void fill(Graphics g) {
		if (updated) {
			tris.triangulate();
			updated = false;
		}
		
		GL11.glBegin(GL11.GL_TRIANGLES);
			int count = tris.getTriangleCount();
			for (int i=0;i<count;i++) {
				float[] pt = tris.getTrianglePoint(i, 0);
				GL11.glVertex3f(pt[0],pt[1],0);
				pt = tris.getTrianglePoint(i, 1);
				GL11.glVertex3f(pt[0],pt[1],0);
				pt = tris.getTrianglePoint(i, 2);
				GL11.glVertex3f(pt[0],pt[1],0);
			}
		GL11.glEnd();
	}
}
