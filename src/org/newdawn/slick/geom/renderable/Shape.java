package org.newdawn.slick.geom.renderable;


/**
 * The description of any 2D shape that can be transformed. The points provided approximate the intent
 * of the shape. 
 * 
 * @author Mark
 */
public interface Shape {
    /**
     * Apply a transformation and return a new shape.  This will not alter the current shape but will 
     * return the transformed shape.
     * 
     * @param transform The transform to be applied
     * @return The transformed shape.
     */
    public Shape transform(Transform transform);
    
    /**
     * Get the points that outline this shape.  Use CW winding rule
     * 
     * @return an array of x,y points
     */
    public float[] getPoints();
    
    /**
     * Get the point closet to the center of all the points in this Shape
     * 
     * @return The x,y coordinates of the center.
     */
    public float[] getCenter();
}
