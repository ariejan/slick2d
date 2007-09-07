package org.newdawn.slick.geom;

import java.io.Serializable;

/**
 * The description of any 2D shape that can be transformed. The points provided approximate the intent
 * of the shape. 
 * 
 * @author Mark
 */
public abstract class Shape implements Serializable {
    /** The points representing this polygon. */
    protected float points[];
    /** Center point of the polygon. */
    protected float center[];
    /** The left most point of this shape. */
    protected float x;
    /** The top most point of this shape. */
    protected float y;
    /** The right most point of this shape */
    protected float maxX;
    /** The bottom most point of this shape */
    protected float maxY;
    /** Radius of a circle that can completely enclose this shape. */
    protected float boundingCircleRadius;
    /** Flag to tell whether points need to be generated */
    protected boolean pointsDirty;
    /** The triangles that define the shape */
    protected Triangulator tris;
    /** True if the triangles need updating */
    protected boolean trianglesDirty;
    
    /**
     * Shape constructor.
     *
     */
    public Shape() {
        pointsDirty = true;
    }
    
    /**
     * Apply a transformation and return a new shape.  This will not alter the current shape but will 
     * return the transformed shape.
     * 
     * @param transform The transform to be applied
     * @return The transformed shape.
     */
    public abstract Shape transform(Transform transform);

    /**
     * Subclasses implement this to create the points of the shape.
     *
     */
    protected abstract void createPoints();
    
    /**
     * Get the x location of the left side of this shape.
     * 
     * @return The x location of the left side of this shape.
     */
    public float getX() {
        return x;
    }
    /**
     * Set the x position of the left side this shape.
     * 
     * @param x The new x position of the left side this shape.
     */
    public void setX(float x) {
    	if (x != this.x) {
    		float dx = x - this.x;
	        this.x = x;
	        
	        if ((points == null) || (center == null)) {
	        	checkPoints();
	        }
	        // update the points in the special case
    		for (int i=0;i<points.length/2;i++) {
    			points[i*2] += dx;
    		}
    		center[0] += dx;
    		x += dx;
    		maxX += dx;
	        trianglesDirty = true;
    	}
    }
    
    /**
     * Set the y position of the top of this shape.
     * 
     * @param y The new y position of the top of this shape.
     */
    public void setY(float y) {
    	if (y != this.y) {
    		float dy = y - this.y;
	        this.y = y;

	        if ((points == null) || (center == null)) {
	        	checkPoints();
	        }
	        // update the points in the special case
    		for (int i=0;i<points.length/2;i++) {
    			points[(i*2)+1] += dy;
    		}
    		center[1] += dy;
    		y += dy;
    		maxY += dy;
	        trianglesDirty = true;
    	}
    }

    /**
     * Get the y position of the top of this shape.
     * 
     * @return The y position of the top of this shape.
     */
    public float getY() {
        return y;
    }
    
    /**
     * Get the x center of this shape.
     * 
     * @return The x center of this shape.
     */
    public float getCenterX() {
        checkPoints();
        
        return center[0];
    }
    
    /**
     * Set the x center of this shape.
     * 
     * @param centerX The center point to set.
     */
    public void setCenterX(float centerX) {
        if ((points == null) || (center == null)) {
        	checkPoints();
        }
        
        float xDiff = centerX - getCenterX();
        setX(x + xDiff);
    }

    /**
     * Get the y center of this shape.
     * 
     * @return The y center of this shape.
     */
    public float getCenterY() {
        checkPoints();
        
        return center[1];
    }
    
    /**
     * Set the y center of this shape.
     * 
     * @param centerY The center point to set.
     */
    public void setCenterY(float centerY) {
        if ((points == null) || (center == null)) {
        	checkPoints();
        }
        
        float yDiff = centerY - getCenterY();
        setY(y + yDiff);
    }
    
    /**
     * Get the right most point of this shape.
     * 
     * @return The right most point of this shape.
     */
    public float getMaxX() {
        return maxX;
    }
    /**
     * Get the bottom most point of this shape.
     * 
     * @return The bottom most point of this shape.
     */
    public float getMaxY() {
        return maxY;
    }
    /**
     * Get the radius of a circle that can completely enclose this shape.
     * 
     * @return The radius of the circle.
     */
    public float getBoundingCircleRadius() {
        checkPoints();
        return boundingCircleRadius;
    }
    
    /**
     * Get the point closet to the center of all the points in this Shape
     * 
     * @return The x,y coordinates of the center.
     */
    public float[] getCenter() {
        checkPoints();
        return center;
    }

    /**
     * Get the points that outline this shape.  Use CW winding rule
     * 
     * @return an array of x,y points
     */
    public float[] getPoints() {
        checkPoints();
        return points;
    }

    /**
     * Get the number of points in this polygon
     * 
     * @return The number of points in this polygon
     */
    public int getPointCount() {
        checkPoints();
        return points.length / 2;
    }

    /**
     * Get a single point in this polygon
     * 
     * @param index The index of the point to retrieve
     * @return The point's coordinates
     */
    public float[] getPoint(int index) {
        checkPoints();

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
        checkPoints();
        boolean result = false;
        float xnew,ynew;
        float xold,yold;
        float x1,y1;
        float x2,y2;
        int npoints = points.length;

        xold=points[npoints - 2];
        yold=points[npoints - 1];
        for (int i=0;i < npoints;i+=2) {
             xnew = points[i];
             ynew = points[i + 1];
             if (xnew > xold) {
                  x1 = xold;
                  x2 = xnew;
                  y1 = yold;
                  y2 = ynew;
             }
             else {
                  x1 = xnew;
                  x2 = xold;
                  y1 = ynew;
                  y2 = yold;
             }
             if ((xnew < x) == (x <= xold)          /* edge "open" at one end */
              && ((double)y - (double)y1) * (x2 - x1)
               < ((double)y2 - (double)y1) * (x - x1)) {
                  result = !result;
             }
             xold = xnew;
             yold = ynew;
        }
        
        return result;
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
        checkPoints();

        boolean result = false;
        float points[] = getPoints();           // (x3, y3)  and (x4, y4)
        float thatPoints[] = shape.getPoints(); // (x1, y1)  and (x2, y2)
        int length = points.length;
        int thatLength = thatPoints.length;
        float unknownA;
        float unknownB;
        
        if (!closed()) {
        	length -= 2;
        }
        if (!shape.closed()) {
        	thatLength -= 2;
        }
        
        // x1 = thatPoints[j]
        // x2 = thatPoints[j + 2]
        // y1 = thatPoints[j + 1]
        // y2 = thatPoints[j + 3]
        // x3 = points[i]
        // x4 = points[i + 2]
        // y3 = points[i + 1]
        // y4 = points[i + 3]
        for(int i=0;i<length;i+=2) {        	
        	int iNext = i+2;
	    	if (iNext >= points.length) {
	    		iNext = 0;
	    	}
	    	
            for(int j=0;j<thatLength;j+=2) {
            	int jNext = j+2;
            	if (jNext >= thatPoints.length) {
            		jNext = 0;
            	}

                unknownA = (((points[iNext] - points[i]) * (thatPoints[j + 1] - points[i + 1])) - 
                        ((points[iNext+1] - points[i + 1]) * (thatPoints[j] - points[i]))) / 
                        (((points[iNext+1] - points[i + 1]) * (thatPoints[jNext] - thatPoints[j])) - 
                                ((points[iNext] - points[i]) * (thatPoints[jNext+1] - thatPoints[j + 1])));
                unknownB = (((thatPoints[jNext] - thatPoints[j]) * (thatPoints[j + 1] - points[i + 1])) - 
                        ((thatPoints[jNext+1] - thatPoints[j + 1]) * (thatPoints[j] - points[i]))) / 
                        (((points[iNext+1] - points[i + 1]) * (thatPoints[jNext] - thatPoints[j])) - 
                                ((points[iNext] - points[i]) * (thatPoints[jNext+1] - thatPoints[j + 1])));
                
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
        checkPoints();

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
     * Calculate the triangles that can fill this shape
     */
    protected void calculateTriangles() {
    	if (!trianglesDirty) {
    		return;
    	}
    	if (points.length >= 6) {
    		boolean clockwise = true;
    		float area = 0;
    		for (int i=0;i<(points.length/2)-1;i++) {
    			float x1 = points[(i*2)];
    			float y1 = points[(i*2)+1];
    			float x2 = points[(i*2)+2];
    			float y2 = points[(i*2)+3];
    			
    			area += (x1 * y2) - (y1 * x2);
    		}
    		area /= 2;
    		clockwise = area > 0;

        	tris = new NeatTriangulator();
    		for (int i=0;i<points.length;i+=2) {
	    		tris.addPolyPoint(points[i], points[i+1]);
	    	}
    		tris.triangulate();
    	}
    	
    	trianglesDirty = false;
    }
    
    /**
     * The triangles that define the filled version of this shape
     * 
     * @return The triangles that define the 
     */
    public Triangulator getTriangles() {
        checkPoints();
        calculateTriangles();
    	return tris;
    }
    
    /**
     * Check the dirty flag and create points as necessary.
     */
    protected final void checkPoints() {
        if (pointsDirty) {
            createPoints();
            findCenter();
            calculateRadius();
            pointsDirty = false;
            trianglesDirty = true;
        }
    }
    
    /**
     * Cause all internal state to be generated and cached
     */
    public void preCache() {
    	checkPoints();
    	getTriangles();
    }
    
    /**
     * True if this is a closed shape
     * 
     * @return True if this is a closed shape
     */
    public boolean closed() {
    	return true;
    }
}
