package org.newdawn.slick.geom;

/**
 * An axis oriented used for shape bounds
 * 
 * @author Kevin Glass
 */
public class Rectangle extends Polygon {
	/** The width of the box */
	public float width;
	/** The height of the box */
	public float height;
	/** The x position of the box */
	public float x;
	/** The y position of the box */
	public float y;
	
	/**
	 * Create a new bounding box
	 * 
	 * @param x The x position of the box
	 * @param y The y position of the box
	 * @param width The width of the box
	 * @param height The hieght of the box
	 */
	public Rectangle(float x, float y, float width, float height) {
        super(x, y, width, height);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Check if this rectangle contains a point
	 * 
	 * @param xp The x coordinate of the point to check
	 * @param yp The y coordinate of the point to check
	 * @return True if the point is within the rectangle
	 */
	public boolean contains(float xp, float yp) {
		return (xp >= x) && (yp >= y) && (xp <= x+width) && (yp <= y+height);
	}
	
	/**
	 * Get the width of the box
	 * 
	 * @return The width of the box
	 */
	public float getWidth() {
		return width;
	}
	
	/**
	 * Get the height of the box
	 * 
	 * @return The height of the box
	 */
	public float getHeight() {
		return height;
	}
	
	/**
	 * Set the x position of this box
	 * 
	 * @param x The new x position of this box
	 */
	public void setX(float x) {
        super.setX(x + ((width - x) / 2.0f));
		this.x = x;
	}
	
	/**
	 * Set the y position of this box
	 * 
	 * @param y The new y position of this box
	 */
	public void setY(float y) {
        super.setY(y + ((height - y) / 2));
		this.y = y;
	}

	/**
	 * Set the width of this box
	 * 
	 * @param width The new width of this box
	 */
	public void setWidth(float width) {
        points[2] = x + width;
        points[4] = x + width;
        findCenter();
        calculateRadius();
		this.width = width;
	}
	
	/**
	 * Set the heightof this box
	 * 
	 * @param height The height of this box
	 */
	public void setHeight(float height) {
        points[5] = y + height;
        points[7] = y + height;
        findCenter();
        calculateRadius();
		this.height = height;
	}
	
	/**
	 * Check if this box touches another
	 * 
	 * @param shape The other shape to check against
	 * @return True if the rectangles touch
	 */
	public boolean intersects(Shape shape) {
        if(shape instanceof Rectangle) {
            Rectangle other = (Rectangle)shape;
    		if ((x > (other.x + other.width)) || ((x + width) < other.x)) {
    			return false;
    		}
    		if ((y > (other.y + other.height)) || ((y + height) < other.y)) {
    			return false;
    		}
            return true;
        }
        else if(shape instanceof Circle) {
            return intersects((Circle)shape);
        }
        else {
            return super.intersects(shape);
        }
	}

	/**
	 * Check if a circle touches this rectangle
	 * 
	 * @param other The circle to check against
	 * @return True if they touch
	 */
	private boolean intersects(Circle other) {
		return other.intersects(this);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[Rectangle "+width+"x"+height+"]";
	}
}
