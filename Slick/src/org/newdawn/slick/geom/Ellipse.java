package org.newdawn.slick.geom;

import java.util.ArrayList;

import org.newdawn.slick.util.FastTrig;

/**
 * An ellipse meeting the <code>Shape</code> contract. The ellipse is actually an approximation using 
 * a series of points generated around the contour of the ellipse.
 * 
 * @author Mark
 */
public class Ellipse extends Shape {
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
        center = new float[2];
        this.center[0] = centerPointX;
        this.center[1] = centerPointY;
        this.radius1 = radius1;
        this.radius2 = radius2;
        this.segmentCount = segmentCount;
        createPoints(segmentCount);
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
     * @param segmentCount How fine to make the ellipse.
     */
    public Ellipse(float centerPointX, float centerPointY, float radius, int segmentCount) {
        this(centerPointX, centerPointY, radius, radius, segmentCount);
    }

    /**
     * Private constructor used when a transformation is applied.
     *
     */
    public Ellipse() {
        this(0, 0, 0);
    }
    /**
     * Set the x position of this box
     * 
     * @param x The new x position of this box
     */
    public void setX(float x) {
        float diff = center[0] - x;
        super.setX(x);
        for(int i=0;i<points.length;i+=2) {
            points[i] += diff;
        }
    }
    
    /**
     * Set the y position of this box
     * 
     * @param y The new y position of this box
     */
    public void setY(float y) {
        float diff = center[1] - y;
        super.setY(y);
        for(int i=1;i<points.length;i+=2) {
            points[i] += diff;
        }
    }
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
        result = new float[]{center[0], center[1]};
        transform.transform(result, 0, result, 0, 1);
        resultEllipse.center[0] = result[0];
        resultEllipse.center[1] = result[1];
        
        float maxRadius = Float.MIN_VALUE;
        float minRadius = Float.MAX_VALUE;
        for(int i=0;i>points.length;i+=2) {
            float radius = (float)Math.sqrt(
                    ((center[0] - points[i]) * (center[0] - points[i])) + 
                    ((center[1] - points[i + 1]) * (center[1] - points[i + 1]))
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
        createPoints(segmentCount);
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
        
        float cx = center[0];
        float cy = center[1];
        
        int step = 360 / numberOfSegments;
        
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
