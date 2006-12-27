package org.newdawn.slick.geom;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;

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
	 * Get a polygon based on this one 
	 * 
	 * @param offset The offset from the current points to form the new one
	 * @return The newly created polygon
	 */
	public Polygon getScaled(float offset) {
		Polygon result = new Polygon();
		
		for (int i=0;i<points.size();i++) {
			int pi = i-1 < 0 ? points.size() - 1 : i - 1;
			int ni = i+1 > points.size() - 1 ? 0 : i + 1;

			float[] p = (float[]) points.get(pi);
			float[] c = (float[]) points.get(i);
			float[] n = (float[]) points.get(ni);
			
			// first line
			float dx1 = c[0] - p[0];
			float dy1 = c[1] - p[1];
			float l1 = (float) Math.sqrt((dx1*dx1)+(dy1*dy1));
			dx1 /= l1;
			dy1 /= l1;
			// second line
			float dx2 = (n[0] - c[0]);
			float dy2 = (n[1] - c[1]);
			float l2 = (float) Math.sqrt((dx2*dx2)+(dy2*dy2));
			dx2 /= l2;
			dy2 /= l2;
			
			float dx = (dx1 + dx2) / 2;
			float dy = (dy1 + dy2) / 2;
			float l = (float) Math.sqrt((dx*dx)+(dy*dy));
			dx /= l;
			dy /= l;
			
			result.addPoint(c[0]+(dy*offset), c[1]-(dx*offset));
		}
		
		return result;
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
		
		Texture.bindNone();
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
	
	/**
	 * Render the polygon with an image
	 * 
	 * @param g The graphics context on which to render 
	 * @param image The image to use to texture this polygon - note image bounding will not
	 * be taken into account so subimages will use the full image.
	 * @param scale The scaling to apply to the image
	 */
	public void texture(Graphics g, Image image, float scale) {
		if (updated) {
			tris.triangulate();
			updated = false;
		}
		
		image.bind();
		GL11.glBegin(GL11.GL_TRIANGLES);
			int count = tris.getTriangleCount();
			for (int i=0;i<count;i++) {
				float[] pt = tris.getTrianglePoint(i, 0);
				GL11.glTexCoord2f(pt[0] * scale,pt[1] * scale);
				GL11.glVertex3f(pt[0],pt[1],0);
				pt = tris.getTrianglePoint(i, 1);
				GL11.glTexCoord2f(pt[0] * scale,pt[1] * scale);
				GL11.glVertex3f(pt[0],pt[1],0);
				pt = tris.getTrianglePoint(i, 2);
				GL11.glTexCoord2f(pt[0] * scale,pt[1] * scale);
				GL11.glVertex3f(pt[0],pt[1],0);
			}
		GL11.glEnd();
	}
	
	/**
	 * Check if this polygon contains the given point
	 * 
	 * @param x The x position of the point to check
	 * @param y The y position of the point to check
	 * @return True if the point is contained in the polygon
	 */
	public boolean contains(float x, float y) {
		if (updated) {
			tris.triangulate();
			updated = false;
		}
		
		int count = tris.getTriangleCount();
		for (int i=0;i<count;i++) {
			if (inTriangle(i, x, y)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Check if a point lies within a triangle
	 * 
	 * @param triangle The index of the triangle to check
	 * @param x The x position of the point
	 * @param y The y position of the point
	 * @return True if the point lies within the triangle
	 */
	private boolean inTriangle(int triangle, float x, float y) {
		float[] a = tris.getTrianglePoint(triangle, 0);
		float[] b = tris.getTrianglePoint(triangle, 1);
		float[] c = tris.getTrianglePoint(triangle, 2);
		
		boolean sideA = ((b[1]-a[1])*(x-a[0])-(b[0]-a[0])*(y-a[1])) > 0.0f;
		boolean sideB = ((c[1]-b[1])*(x-b[0])-(c[0]-b[0])*(y-b[1])) > 0.0f;
		if (sideA != sideB) return false;
		boolean sideC = ((a[1]-c[1])*(x-c[0])-(a[0]-c[0])*(y-c[1])) > 0.0f;
		return (sideA == sideC);
	}
}
