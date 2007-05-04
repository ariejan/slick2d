package org.newdawn.slick.geom;

import java.util.ArrayList;

/**
 * A polygon implementation meeting the <code>Shape</code> contract. 
 * 
 * @author Mark
 */
public class Polygon extends Shape {
    /**
     * Convenience constructor to make a square or a rectangle.
     *  
     * @param x Left side
     * @param y Top side
     * @param width The width
     * @param height The height
     */
    public Polygon(float x, float y, float width, float height) {
        points = new float[8];
        
        points[0] = x;
        points[1] = y;
        
        points[2] = x + width;
        points[3] = y;
        
        points[4] = x + width;
        points[5] = y + height;
        
        points[6] = x;
        points[7] = y + height;
        
        findCenter();
        calculateRadius();
    }
    
    /**
     * Construct a new polygon with 3 or more points. 
     * This constructor will take the first set of points and copy them after
     * the last set of points to create a closed shape.
     * 
     * @param points An array of points in x, y order.
     */
    public Polygon(float points[]) {
        int length = points.length;
        
        this.points = new float[length];
        
        for(int i=0;i<length;i++) {
            this.points[i] = points[i];
        }
        
        findCenter();
        calculateRadius();
    }
    /**
     * Create an empty polygon
     *
     */
    public Polygon(){
        points = new float[0];
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
     * Add a point to the polygon
     * 
     * @param x The x coordinate of the point
     * @param y The y coordinate of the point
     */
    public void addPoint(float x, float y) {
        ArrayList tempPoints = new ArrayList();
        for(int i=0;i<points.length;i++) {
            tempPoints.add(new Float(points[i]));
        }
        tempPoints.add(new Float(x));
        tempPoints.add(new Float(y));
        int length = tempPoints.size();
        points = new float[length];
        for(int i=0;i<length;i++) {
            points[i] = ((Float)tempPoints.get(i)).floatValue();
        }
        findCenter();
        calculateRadius();
    }


    /**
     * Apply a transformation and return a new shape.  This will not alter the current shape but will 
     * return the transformed shape.
     * 
     * @param transform The transform to be applied
     * @return The transformed shape.
     */
    public Shape transform(Transform transform) {
        Polygon resultPolygon = new Polygon();
        
        float result[] = new float[points.length];
        transform.transform(points, 0, result, 0, points.length / 2);
        resultPolygon.points = result;
        result = new float[]{center[0], center[1]};
        transform.transform(result, 0, result, 0, 1);
        resultPolygon.center = result;

        return resultPolygon;
    }
}
