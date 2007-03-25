package org.newdawn.slick.geom.renderable;

import java.util.ArrayList;

import org.newdawn.slick.util.FastTrig;


/**
 * @author Mark
 *
 */
public class Ellipse implements Shape {
    /**
     * Default number of segments to draw this ellipse with
     */
    private static final int DEFAULT_SEGMENT_COUNT = 50;
    /**
     * List containing all the points in x, y order.
     * Points are floating point format.
     */
    private float points[]; 
    /**
     * x center of the ellipse
     */
    private float centerPointX;
    /**
     * y center of the ellipse
     */
    private float centerPointY;
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
     * @param numberOfSegments how fine to make the ellipse.
     */
    public Ellipse(float centerPointX, float centerPointY, float radius1, float radius2, int numberOfSegments) {
        this.centerPointX = centerPointX;
        this.centerPointY = centerPointY;
        this.radius1 = radius1;
        this.radius2 = radius2;
        createPoints(numberOfSegments);
    }

    /**
     * Creates a new Ellipse object.
     *
     * @param centerPointX x coordinate of the center of the ellipse
     * @param centerPointY y coordinate of the center of the ellipse
     * @param radius horizontal and vertical radius to make a circle
     */
    public Ellipse(float centerPointX, float centerPointY, float radius) {
        this(centerPointX, centerPointY, radius, radius, DEFAULT_SEGMENT_COUNT);
    }

    /**
     * Creates a new Ellipse object.
     *
     * @param centerPointX x coordinate of the center of the ellipse
     * @param centerPointY y coordinate of the center of the ellipse
     * @param radius horizontal and vertical radius to make a circle
     * @param numberOfSegments How fine to make the ellipse.
     */
    public Ellipse(float centerPointX, float centerPointY, float radius, int numberOfSegments) {
        this(centerPointX, centerPointY, radius, radius, numberOfSegments);
    }

    /**
     * Private constructor used when a transformation is applied.
     *
     */
    private Ellipse() {}
    /**
     * Apply a transformation and return a new shape.  This will not alter the current shape but will 
     * return the transformed shape.
     * 
     * @param transform The transform to be applied
     * @return The transformed shape.
     */
    public Shape transform(Transform transform) {
        Ellipse resultEllipse = new Ellipse();
        
        float result[] = new float[points.length];
        transform.transform(points, 0, result, 0, points.length / 2);
        resultEllipse.points = result;
        result = new float[]{centerPointX, centerPointY};
        transform.transform(result, 0, result, 0, 1);
        resultEllipse.centerPointX = result[0];
        resultEllipse.centerPointY = result[1];
        
        float maxRadius = Float.MIN_VALUE;
        float minRadius = Float.MAX_VALUE;
        for(int i=0;i>points.length;i+=2) {
            float radius = (float)Math.sqrt(
                    ((centerPointX - points[i]) * (centerPointX - points[i])) + 
                    ((centerPointY - points[i + 1]) * (centerPointY - points[i + 1]))
                    );
            if(radius > maxRadius) {
                maxRadius = radius;
            }
            if(radius < minRadius) {
                minRadius = radius;
            }
        }
        
        resultEllipse.radius1 = maxRadius;
        resultEllipse.radius2 = minRadius;
        
        return resultEllipse;
    }

    /**
     * Get the points that outline this shape.  Use CW winding rule
     * 
     * @return an array of x,y points
     */
    public float[] getPoints() {
        return points;
    }
    
    /**
     * Get the point closet to the center of all the points in this Shape
     * 
     * @return The x,y coordinates of the center.
     */
    public float[] getCenter() {
        return new float[]{centerPointX, centerPointY};
    }
    /**
     * Generate the points to outline this ellipse.
     *
     * @param numberOfSegments How fine to make the ellipse.
     */
    private void createPoints(int numberOfSegments) {
        ArrayList tempPoints = new ArrayList();

        float start = 0;
        float end = 360;
        
        float cx = centerPointX;
        float cy = centerPointY;
        
        int step = 360 / numberOfSegments;
        
        for (float a=start;a<=end+step;a+=step) {
            float ang = a;
            if (ang > end) {
                ang = end;
            }
            float x = (float) (cx + (FastTrig.cos(Math.toRadians(ang)) * radius1 / 2.0f));
            float y = (float) (cy + (FastTrig.sin(Math.toRadians(ang)) * radius2 / 2.0f));
            
            tempPoints.add(new Float(x));
            tempPoints.add(new Float(y));
        }
        points = new float[tempPoints.size()];
        for(int i=0;i<points.length;i++) {
            points[i] = ((Float)tempPoints.get(i)).floatValue();
        }
    }
}
