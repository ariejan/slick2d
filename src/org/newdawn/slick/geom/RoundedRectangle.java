package org.newdawn.slick.geom;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.util.FastTrig;

/**
 * Class to create rounded rectangles with.
 * 
 * @author Mark Bernard
 */
public class RoundedRectangle extends Shape {
    /**
     * Default number of segments to draw the rounded corners with
     */
    private static final int DEFAULT_SEGMENT_COUNT = 10;

    /**
     * Construct a rectangle with rounded corners.
     * 
     * @param x The x position of the rectangle.
     * @param y The y position of the rectangle.
     * @param width The width of the rectangle.
     * @param height The hieght of the rectangle.
     * @param cornerRadius The radius to use for the arc in each corner.
     */
    public RoundedRectangle(float x, float y, float width, float height, float cornerRadius) {
        this(x, y, width, height, cornerRadius, DEFAULT_SEGMENT_COUNT);
    }

    /**
     * Construct a rectangle with rounded corners.
     * 
     * @param x The x position of the rectangle.
     * @param y The y position of the rectangle.
     * @param width The width of the rectangle.
     * @param height The hieght of the rectangle.
     * @param cornerRadius The radius to use for the arc in each corner.
     * @param segs The number of segments to use to draw each corner arc.
     */
    public RoundedRectangle(float x, float y, float width, float height, float cornerRadius, int segs) {
        if(cornerRadius < 0) {
            throw new IllegalArgumentException("corner radius must be > 0");
        }
        else if(cornerRadius == 0) {
            points = new float[8];
            
            points[0] = x;
            points[1] = y;
            
            points[2] = x + width;
            points[3] = y;
            
            points[4] = x + width;
            points[5] = y + height;
            
            points[6] = x;
            points[7] = y + height;
        }
        else {
            float doubleRadius = cornerRadius * 2;
            if(doubleRadius > width) {
                doubleRadius = width;
                cornerRadius = doubleRadius / 2;
            }
            if(doubleRadius > height) {
                doubleRadius = height;
                cornerRadius = doubleRadius / 2;
            }
            
            ArrayList tempPoints = new ArrayList();
            //the outer most set of points for each arc will also ac as the points that start the
            //straight sides, so the straight sides do not have to be added.
            
            //top left corner arc
            tempPoints.addAll(createPoints(segs, cornerRadius, x + cornerRadius, y + cornerRadius, 180, 270));
            
            //top right corner arc
            tempPoints.addAll(createPoints(segs, cornerRadius, x + width - cornerRadius, y + cornerRadius, 270, 360));
            
            //bottom right corner arc
            tempPoints.addAll(createPoints(segs, cornerRadius, x + width - cornerRadius, y + height - cornerRadius, 0, 90));

            //bottom left corner arc
            tempPoints.addAll(createPoints(segs, cornerRadius, x + cornerRadius, y + height - cornerRadius, 90, 180));
            
            points = new float[tempPoints.size()];
            for(int i=0;i<tempPoints.size();i++) {
                points[i] = ((Float)tempPoints.get(i)).floatValue();
            }
        }
        
        findCenter();
        calculateRadius();
    }

    /**
     * Construct an empty RoundedRectangle
     *
     */
    public RoundedRectangle() {
        points = new float[0];
    }
    /**
     * Apply a transformation and return a new shape.  This will not alter the current shape but will 
     * return the transformed shape.
     * <b>Copied from superclass Shape</b>
     * 
     * @param transform The transform to be applied
     * @return The transformed shape.
     */
    public Shape transform(Transform transform) {
        RoundedRectangle resultRoundedRectangle = new RoundedRectangle();
        
        float result[] = new float[points.length];
        transform.transform(points, 0, result, 0, points.length / 2);
        resultRoundedRectangle.points = result;
        result = new float[]{center[0], center[1]};
        transform.transform(result, 0, result, 0, 1);
        resultRoundedRectangle.center = result;

        return resultRoundedRectangle;
    }

    /**
     * Generate the points to fill a corner arc.
     *
     * @param numberOfSegments How fine to make the ellipse.
     * @param radius The radius of the arc.
     * @param cx The x center of the arc.
     * @param cy The y center of the arc.
     * @param start The start angle of the arc.
     * @param end The end angle of the arc.
     * @return The points created.
     */
    private List createPoints(int numberOfSegments, float radius, float cx, float cy, float start, float end) {
        ArrayList tempPoints = new ArrayList();

        int step = 360 / numberOfSegments;
        
        for (float a=start;a<=end+step;a+=step) {
            float ang = a;
            if (ang > end) {
                ang = end;
            }
            float x = (float) (cx + (FastTrig.cos(Math.toRadians(ang)) * radius));
            float y = (float) (cy + (FastTrig.sin(Math.toRadians(ang)) * radius));
            
            tempPoints.add(new Float(x));
            tempPoints.add(new Float(y));
        }
        
        return tempPoints;
    }
}
