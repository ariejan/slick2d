package org.newdawn.slick.geom;

import java.util.ArrayList;

import org.newdawn.slick.util.FastTrig;

/**
 * An ellipse meeting the <code>Shape</code> contract. The ellipse is actually an approximation using 
 * a series of points generated around the contour of the ellipse.
 * 
 * @author Mark
 */
public class Ellipse extends Polygon {
    /**
     * Default number of segments to draw this ellipse with
     */
    private static final int DEFAULT_SEGMENT_COUNT = 50;
    
    /**
     * The number of segments for graphical representation.
     */
    private int segmentCount;
    /**
     * horizontal radius
     */
    private float radius1;
    /**
     * vertical radius
     */
    private float radius2;

    /**
     * Creates a new Ellipse object.
     *
     * @param centerPointX x coordinate of the center of the ellipse
     * @param centerPointY y coordinate of the center of the ellipse
     * @param radius1 horizontal radius
     * @param radius2 vertical radius
     */
    public Ellipse(float centerPointX, float centerPointY, float radius1, float radius2) {
        this(centerPointX, centerPointY, radius1, radius2, DEFAULT_SEGMENT_COUNT);
    }

    /**
     * Creates a new Ellipse object.
     *
     * @param centerPointX x coordinate of the center of the ellipse
     * @param centerPointY y coordinate of the center of the ellipse
     * @param radius1 horizontal radius
     * @param radius2 vertical radius
     * @param segmentCount how fine to make the ellipse.
     */
    public Ellipse(float centerPointX, float centerPointY, float radius1, float radius2, int segmentCount) {
        this.x = centerPointX - radius1;
        this.y = centerPointY - radius2;
        this.radius1 = radius1;
        this.radius2 = radius2;
        this.segmentCount = segmentCount;
    }

    /**
     * Get the radius of a circle that can completely enclose this shape.
     * 
     * @return The radius of the circle.
     */
    public float getBoundingCircleRadius() {
        return (radius1 > radius2) ? radius1 : radius2;
    }

    /**
     * Change the shape of this Ellipse
     * 
     * @param radius1 horizontal radius
     * @param radius2 vertical radius
     */
    public void setRadii(float radius1, float radius2) {
        this.radius1 = radius1;
        this.radius2 = radius2;
        pointsDirty = true;
    }

    public float getRadius1() {
        return radius1;
    }

    public void setRadius1(float radius1) {
        this.radius1 = radius1;
        pointsDirty = true;
    }

    public float getRadius2() {
        return radius2;
    }

    public void setRadius2(float radius2) {
        this.radius2 = radius2;
        pointsDirty = true;
    }

    /**
     * Generate the points to outline this ellipse.
     *
     */
    protected void createPoints() {
        ArrayList tempPoints = new ArrayList();

        float start = 0;
        float end = 360;
        
        float cx = x + radius1;
        float cy = y + radius2;
        
        int step = 360 / segmentCount;
        
        for (float a=start;a<=end+step;a+=step) {
            float ang = a;
            if (ang > end) {
                ang = end;
            }
            float x = (float) (cx + (FastTrig.cos(Math.toRadians(ang)) * radius1));
            float y = (float) (cy + (FastTrig.sin(Math.toRadians(ang)) * radius2));
            
            tempPoints.add(new Float(x));
            tempPoints.add(new Float(y));
        }
        points = new float[tempPoints.size()];
        for(int i=0;i<points.length;i++) {
            points[i] = ((Float)tempPoints.get(i)).floatValue();
        }
    }
}
