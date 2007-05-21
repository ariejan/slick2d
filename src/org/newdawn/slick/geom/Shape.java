package org.newdawn.slick.geom;

/**
 * The description of any 2D shape that can be transformed. The points provided approximate the intent
 * of the shape. 
 * 
 * @author Mark
 */
public abstract class Shape {
    /** The points representing this polygon. */
    protected float points[];
    /** Center point of the polygon. */
    protected float center[];
    /** The left most point of this shape. */
    protected Float left;
    /** The top most point of this shape. */
    protected Float top;
    /** Radius of a circle that can completely enclose this shape. */
    protected float boundingCircleRadius;

    /**
     * Apply a transformation and return a new shape.  This will not alter the current shape but will 
     * return the transformed shape.
     * 
     * @param transform The transform to be applied
     * @return The transformed shape.
     */
    public abstract Shape transform(Transform transform);

    /**
     * Get the x location of the left side of this shape.
     * 
     * @return The x location of the left side of this shape.
     */
    public float getX() {
        if(left == null) {
            calculateLeft();
        }

        return left.floatValue();
    }
    /**
     * Set the x position of the left side this shape.
     * 
     * @param x The new x position of the left side this shape.
     */
    public void setX(float x) {
        if(left == null) {
            calculateLeft();
        }
        
        float xLeftDiff = center[0] - left.floatValue();
        float newCenter = x - xLeftDiff;
        float xCenterDiff = newCenter - center[0];
        center[0] = x - xLeftDiff;
        left = new Float(left.floatValue() + xCenterDiff);
        for(int i=0;i<points.length;i+=2) {
            points[i] += xCenterDiff;
        }
    }
    
    /**
     * Set the y position of the top of this shape.
     * 
     * @param y The new y position of the top of this shape.
     */
    public void setY(float y) {
        if(top == null) {
            calculateTop();
        }
        
        float yTopDiff = center[0] - top.floatValue();
        float newCenter = y - yTopDiff;
        float yCenterDiff = newCenter - center[1];
        center[1] = y - yTopDiff;
        top = new Float(top.floatValue() + yCenterDiff);
        for(int i=1;i<points.length;i+=2) {
            points[i] += yCenterDiff;
        }
    }

    /**
     * Get the y position of the top of this shape.
     * 
     * @return The y position of the top of this shape.
     */
    public float getY() {
        if(top == null) {
            calculateTop();
        }

        return top.floatValue();
    }
    /**
     * Get the radius of a circle that can completely enclose this shape.
     * 
     * @return The radius of the circle.
     */
    public float getBoundingCircleRadius() {
        return boundingCircleRadius;
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
     * Get the number of points in this polygon
     * 
     * @return The number of points in this polygon
     */
    public int getPointCount() {
        return points.length / 2;
    }

    /**
     * Get a single point in this polygon
     * 
     * @param index The index of the point to retrieve
     * @return The point's coordinates
     */
    public float[] getPoint(int index) {
        float result[] = new float[2];
        
        result[0] = points[index * 2];
        result[1] = points[index * 2 + 1];
        
        return result;
    }

    /**
     * Check if this polygon contains the given point
     * 
     * @param x The x position of the point to check
     * @param y The y position of the point to check
     * @return True if the point is contained in the polygon
     */
    public boolean contains(float x, float y) {
        //TODO
        return false;
    }
    
    /**
     * Check if this shape intersects with the shape provided.
     * 
     * @param shape The shape to check if it intersects with this one.
     * @return True if the shapes do intersect, false otherwise.
     */
    public boolean intersects(Shape shape) {
        /*
         * Intersection formula used:
         *      (x4 - x3)(y1 - y3) - (y4 - y3)(x1 - x3)
         * UA = ---------------------------------------
         *      (y4 - y3)(x2 - x1) - (x4 - x3)(y2 - y1)
         *      
         *      (x2 - x1)(y1 - y3) - (y2 - y1)(x1 - x3)
         * UB = ---------------------------------------
         *      (y4 - y3)(x2 - x1) - (x4 - x3)(y2 - y1)
         *      
         * if UA and UB are both between 0 and 1 then the lines intersect.
         * 
         * Source: http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline2d/
         */
        boolean result = false;
        float points[] = getPoints();           // (x3, y3)  and (x4, y4)
        float thatPoints[] = shape.getPoints(); // (x1, y1)  and (x2, y2)
        int length = points.length - 2;
        int thatLength = thatPoints.length - 2;
        float unknownA;
        float unknownB;
        
        // x1 = thatPoints[j]
        // x2 = thatPoints[j + 2]
        // y1 = thatPoints[j + 1]
        // y2 = thatPoints[j + 3]
        // x3 = points[i]
        // x4 = points[i + 2]
        // y3 = points[i + 1]
        // y4 = points[i + 3]
        for(int i=0;i<length;i+=2) {
            for(int j=0;j<thatLength;j+=2) {
                unknownA = (((points[i + 2] - points[i]) * (thatPoints[j + 1] - points[i + 1])) - 
                        ((points[i + 3] - points[i + 1]) * (thatPoints[j] - points[i]))) / 
                        (((points[i + 3] - points[i + 1]) * (thatPoints[j + 2] - thatPoints[j])) - 
                                ((points[i + 2] - points[i]) * (thatPoints[j + 3] - thatPoints[j + 1])));
                unknownB = (((thatPoints[j + 2] - thatPoints[j]) * (thatPoints[j + 1] - points[i + 1])) - 
                        ((thatPoints[j + 3] - thatPoints[j + 1]) * (thatPoints[j] - points[i]))) / 
                        (((points[i + 3] - points[i + 1]) * (thatPoints[j + 2] - thatPoints[j])) - 
                                ((points[i + 2] - points[i]) * (thatPoints[j + 3] - thatPoints[j + 1])));
                
                if(unknownA >= 0 && unknownA <= 1 && unknownB >= 0 && unknownB <= 1) {
                    result = true;
                    break;
                }
            }
            if(result) {
                break;
            }
        }

        return result;
    }

    /**
     * Check if a particular location is a vertex of this polygon
     * 
     * @param x The x coordinate to check
     * @param y The y coordinate to check
     * @return True if the cordinates supplied are a vertex of this polygon
     */
    public boolean hasVertex(float x, float y) {
        boolean result = false;
        
        for (int i=0;i<points.length;i+=2) {
            if(points[i] == x && points[i + 1] == y) {
                result = true;
                break;
            }
        }
        
        return result;
    }

    /**
     * Get the center of this polygon.
     *
     */
    protected void findCenter() {
        center = new float[]{0, 0};
        int length = points.length;
        for(int i=0;i<length;i+=2) {
            center[0] += points[i];
            center[1] += points[i + 1];
        }
        center[0] /= (length / 2);
        center[1] /= (length / 2);
    }
    
    /**
     * Calculate the radius of a circle that can completely enclose this shape.
     *
     */
    protected void calculateRadius() {
        boundingCircleRadius = 0;
        
        for(int i=0;i<points.length;i+=2) {
            float temp = ((points[i] - center[0]) * (points[i] - center[0])) + 
                    ((points[i + 1] - center[1]) * (points[i + 1] - center[1]));
            boundingCircleRadius = (boundingCircleRadius > temp) ? boundingCircleRadius : temp;
        }
        boundingCircleRadius = (float)Math.sqrt(boundingCircleRadius);
    }
    
    /**
     * Calculate the left most point on this shape.
     *
     */
    private void calculateLeft() {
        left = new Float(Float.MAX_VALUE);
        for(int i=0;i<points.length;i+=2) {
            if(points[i] < left.floatValue()) {
                left = new Float(points[i]);
            }
        }
    }

    /**
     * Calculate the top most point on this shape.
     *
     */
    private void calculateTop() {
        top = new Float(Float.MAX_VALUE);
        for(int i=1;i<points.length;i+=2) {
            if(points[i] < top.floatValue()) {
                top = new Float(points[i]);
            }
        }
    }
}
