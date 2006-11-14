package org.newdawn.slick.geom;

/**
 * An axis oriented used for shape bounds
 * 
 * @author Kevin Glass
 */
public class Rectangle {
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
	 * Get the x  position of this bounds
	 * 
	 * @return The x o position of this bounds
	 */ 
	public float getX() {
		return x;
	}
	
	/**
	 * Get the y position of this bounds
	 * 
	 * @return The y position of this bounds
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * Set the x position of this box
	 * 
	 * @param x The new x position of this box
	 */
	public void setX(float x) {
		this.x = x;
	}
	
	/**
	 * Set the y position of this box
	 * 
	 * @param y The new y position of this box
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Set the width of this box
	 * 
	 * @param width The new width of this box
	 */
	public void setWidth(float width) {
		this.width = width;
	}
	
	/**
	 * Set the heightof this box
	 * 
	 * @param height The height of this box
	 */
	public void setHeight(float height) {
		this.height = height;
	}
	
	/**
	 * Check if this box touches another
	 * 
	 * @param other The other rectangle to check against
	 * @return True if the rectangles touch
	 */
	public boolean intersects(Rectangle other) {
		if ((x > (other.x + other.width)) || ((x + width) < other.x)) {
			return false;
		}
		if ((y > (other.y + other.height)) || ((y + height) < other.y)) {
			return false;
		}
		
		return true;
	}

	/**
	 * Check if a circle touches this rectangle
	 * 
	 * @param other The circle to check against
	 * @return True if they touch
	 */
	public boolean intersects(Circle other) {
		return other.intersects(this);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[Rectangle "+width+"x"+height+"]";
	}
}
