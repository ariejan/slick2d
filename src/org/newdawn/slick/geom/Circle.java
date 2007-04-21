package org.newdawn.slick.geom;

/**
 * A simple Circle geometry
 * 
 * @author Kevin Glass
 */
public strictfp class Circle extends Ellipse {
	/** The radius of the circle */
	public float radius;
	/** The x position of the center of this circle */
	public float x;
	/** The y position of the center of this circle */
	public float y;
	
	/**
	 * Create a new circle based on its radius
	 * 
	 * @param x The x location of the center of the circle
	 * @param y The y location of the center of the circle
	 * @param radius The radius of the circle
	 */
	public Circle(float x, float y, float radius) {
        super(x, y, radius);
		this.x = x;
		this.y = y;
		this.radius = radius;
	}

	/**
	 * Set the radius of this circle
	 * 
	 * @param radius The radius of this circle
	 */
	public void setRadius(float radius) {
		this.radius = radius;
        setRadii(radius, radius);
	}
	
	/**
	 * Set the x location of the center of this circle
	 * 
	 * @param x The x location of the center of this circle
	 */
	public void setX(float x) {
        super.setX(x);
		this.x = x;
	}

	/**
	 * Set the y location of the center of this circle
	 * 
	 * @param y The y location of the center of this circle
	 */
	public void setY(float y) {
        super.setY(y);
		this.y = y;
	}
	
	/**
	 * Get the radius of the circle
	 * 
	 * @return The radius of the circle
	 */
	public float getRadius() {
		return radius;
	}
	/**
	 * Check if this circle touches another
	 * 
	 * @param shape The other circle
	 * @return True if they touch
	 */
	public boolean intersects(Shape shape) {
        if(shape instanceof Circle) {
            Circle other = (Circle)shape;
    		float totalRad2 = getRadius() + other.getRadius();
    		
    		if (Math.abs(other.x - x) > totalRad2) {
    			return false;
    		}
    		if (Math.abs(other.y - y) > totalRad2) {
    			return false;
    		}
    		
    		totalRad2 *= totalRad2;
    		
    		float dx = Math.abs(other.x - x);
    		float dy = Math.abs(other.y - y);
    		
    		return totalRad2 >= ((dx*dx) + (dy*dy));
        }
        else if(shape instanceof Rectangle) {
            return intersects((Rectangle)shape);
        }
        else {
            return super.intersects(shape);
        }
	}
	
	/**
	 * Check if a point is contained by this circle
	 * 
	 * @param x The x coordinate of the point to check
	 * @param y The y coorindate of the point to check
	 * @return True if the point is contained by this circle
	 */
	public boolean contains(float x, float y) {
		return intersects(new Circle(x,y,0));
	}
	
	/**
	 * Check if this circle touches a rectangle
	 * 
	 * @param other The rectangle to check against
	 * @return True if they touch
	 */
	private boolean intersects(Rectangle other) {
		Rectangle box = other;
		Circle circle = this;
		
		if (box.contains(x,y)) {
			return true;
		}
		
		float x1 = box.getX();
		float y1 = box.getY();
		float x2 = box.getX() + box.getWidth();
		float y2 = box.getY() + box.getHeight();
		
		Line[] lines = new Line[4];
		lines[0] = new Line(x1,y1,x2,y1);
		lines[1] = new Line(x2,y1,x2,y2);
		lines[2] = new Line(x2,y2,x1,y2);
		lines[3] = new Line(x1,y2,x1,y1);
		
		float r2 = circle.getRadius() * circle.getRadius();
		
		Vector2f pos = new Vector2f(circle.getX(), circle.getY());
		
		for (int i=0;i<4;i++) {
			float dis = lines[i].distanceSquared(pos);
			if (dis < r2) {
				return true;
			}
		}
		
		return false;
	}

    public Shape transform(Transform transform) {
        float oldPoints[] = {x, y, x + radius, y};
        float result[] = new float[4];
        transform.transform(oldPoints, 0, result, 0, 2);
        float newRadius = (float)Math.sqrt(((result[2] - result[0]) * (result[2] - result[0])) + ((result[3] - result[1]) * (result[3] - result[1])));
        Shape newShape = new Circle(result[0], result[1], newRadius);
        return newShape;
    }
    
}
