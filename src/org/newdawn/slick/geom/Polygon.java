package org.newdawn.slick.geom;

import java.util.ArrayList;

/**
 * A polygon implementation meeting the <code>Shape</code> contract. 
 * 
 * @author Mark
 */
public class Polygon extends Shape {
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
        maxX = Float.MIN_VALUE;
        maxY = Float.MIN_VALUE;
        x = Float.MAX_VALUE;
        y = Float.MAX_VALUE;
        
        for(int i=0;i<length;i++) {
            this.points[i] = points[i];
            if(i % 2 == 0) {
                if(points[i] > maxX) {
                    maxX = points[i];
                }
                if(points[i] < x) {
                    x = points[i];
                }
            }
            else {
                if(points[i] > maxY) {
                    maxY = points[i];
                }
                if(points[i] < y) {
                    y = points[i];
                }
            }
        }
        
        findCenter();
        calculateRadius();
        pointsDirty = true;
    }
    /**
     * Create an empty polygon
     *
     */
    public Polygon(){
        points = new float[0];
        maxX = Float.MIN_VALUE;
        maxY = Float.MIN_VALUE;
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
        if(x > maxX) {
            maxX = x;
        }
        if(y > maxY) {
            maxY = y;
        }
        findCenter();
        calculateRadius();
        
        pointsDirty = true;
    }


    /**
     * Apply a transformation and return a new shape.  This will not alter the current shape but will 
     * return the transformed shape.
     * 
     * @param transform The transform to be applied
     * @return The transformed shape.
     */
    public Shape transform(Transform transform) {
        checkPoints();
        
        Polygon resultPolygon = new Polygon();
        
        float result[] = new float[points.length];
        transform.transform(points, 0, result, 0, points.length / 2);
        resultPolygon.points = result;
        resultPolygon.findCenter();

        return resultPolygon;
    }
    
    public void setX(float x) {
        float xDiff = x - this.x;
        super.setX(x);
        
        for(int i=0;i<points.length;i+=2) {
            points[i] += xDiff;
        }
        maxX += xDiff;
        center[0] += xDiff;
        pointsDirty = false;
    }
    
    public void setY(float y) {
        float yDiff = y - this.y;
        super.setY(y);
        
        for(int i=1;i<points.length;i+=2) {
            points[i] += yDiff;
        }
        maxY += yDiff;
        center[1] += yDiff;
        pointsDirty = false;
    }
    
    //This is empty since a polygon must have it's points all the time.
    protected void createPoints() {
    }
}
