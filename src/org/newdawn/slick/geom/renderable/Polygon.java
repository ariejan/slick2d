package org.newdawn.slick.geom.renderable;

import org.newdawn.slick.SlickException;

/**
 * A polygon implementation meeting the <code>Shape</code> contract. 
 * 
 * @author Mark
 */
public class Polygon implements Shape {
    /**
     * The points representing this polygon.
     */
    private float points[];
    /**
     * Center point of the polygon.
     */
    private float center[];
    
    /**
     * Convenience constructor to make a square or a rectangle.
     *  
     * @param x Left side
     * @param y Top side
     * @param width The width
     * @param height The height
     */
    public Polygon(int x, int y, int width, int height) {
        points = new float[10];
        
        points[0] = x;
        points[1] = y;
        
        points[2] = x + width;
        points[3] = y;
        
        points[4] = x + width;
        points[5] = y + height;
        
        points[6] = x;
        points[7] = y + height;

        points[8] = x;
        points[9] = y;
        
        findCenter();
    }
    
    /**
     * Construct a new polygon with 3 or more points.
     * 
     * @param points An array of points in x, y order.
     * @throws SlickException If the number of points is less than 3 or the number of array elements is odd.
     */
    public Polygon(float points[]) throws SlickException {
        int length = points.length;
        if(length < 6) {
            throw new SlickException("A polygon requires three or more points to create.");
        }
        if(length % 2 != 0) {
            throw new SlickException("The points should be in x,y pairs.");
        }
        
        this.points = new float[length + 2];
        
        for(int i=0;i<length;i++) {
            this.points[i] = points[i];
        }
        
        this.points[length - 2] = points[length - 2];
        this.points[length - 1] = points[length - 1];
        findCenter();
    }
    /**
     * Private constructor for transformations.
     *
     */
    private Polygon(){}
    /**
     * Get the center of this polygon.
     *
     */
    private void findCenter() {
        center = new float[]{0, 0};
    }
    /**
     * Get the point closet to the center of all the points in this Shape
     * 
     * @return The x,y coordinates of the center.
     */
    public float[] getCenter() {
        return center;
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
