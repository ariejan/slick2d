package org.newdawn.slick.geom;

/**
 * A two dimensional vector
 * 
 * @author Kevin Glass
 */
public strictfp class Vector2f {
	/** The x component of this vector */
	public float x;
	/** The y component of this vector */
	public float y;
	
	/**
	 * Create an empty vector
	 */
	public Vector2f() {
	}
	
	/**
	 * Get the x component
	 * 
	 * @return The x component
	 */
	public float getX() {
		return x;
	}

	/**
	 * Get the y component
	 * 
	 * @return The y component
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * Create a new vector based on another
	 * 
	 * @param other The other vector to copy into this one
	 */
	public Vector2f(Vector2f other) {
		this(other.getX(),other.getY());
	}
	
	/**
	 * Create a new vector 
	 * 
	 * @param x The x component to assign
	 * @param y The y component to assign
	 */
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Set the value of this vector
	 * 
	 * @param other The values to set into the vector
	 */
	public void set(Vector2f other) {
		set(other.getX(),other.getY());
	}
	
	/**
	 * Dot this vector against another
	 * 
	 * @param other The other vector dot agianst
	 * @return The dot product of the two vectors
	 */
	public float dot(Vector2f other) {
		return (x * other.getX()) + (y * other.getY());
	}
	
	/**
	 * Set the values in this vector
	 * 
	 * @param x The x component to set
	 * @param y The y component to set
	 */
	public void set(float x, float y) { 
		this.x = x; 
		this.y = y; 
	}

	/**
	 * Negate this vector 
	 * 
	 * @return A copy of this vector negated
	 */
	public Vector2f negate() {
		return new Vector2f(-x, -y); 
	}
	
	/**
	 * Add a vector to this vector
	 * 
	 * @param v The vector to add
	 */
	public void add(Vector2f v)
	{
		x += v.getX(); 
		y += v.getY();
	}
	
	/**
	 * Subtract a vector from this vector
	 * 
	 * @param v The vector subtract
	 */
	public void sub(Vector2f v)
	{
		x -= v.getX(); 
		y -= v.getY();
	}

	/**
	 * Scale this vector by a value
	 * 
	 * @param a The value to scale this vector by
	 */
	public void scale(float a)
	{
		x *= a; 
		y *= a;
	}

	/**
	 * Normalise the vector
	 *
	 */
	public void normalise() {
		float l = length();
		
		x /= l;
		y /= l;
	}
	
	/**
	 * The length of the vector squared
	 * 
	 * @return The length of the vector squared
	 */
	public float lengthSquared() {
		return (x * x) + (y * y);
	}
	
	/**
	 * Get the length of this vector
	 * 
	 * @return The length of this vector
	 */
	public float length() 
	{
		return (float) Math.sqrt(lengthSquared());
	}
	
	/**
	 * Project this vector onto another
	 * 
	 * @param b The vector to project onto
	 * @param result The projected vector
	 */
	public void projectOntoUnit(Vector2f b, Vector2f result) {
		float dp = b.dot(this);
		
		result.x = dp * b.getX();
		result.y = dp * b.getY();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[Vector2f "+x+","+y+" ("+length()+")]";
	}
	
	/**
	 * Get the distance from this point to another
	 * 
	 * @param other The other point we're measuring to
	 * @return The distance to the other point
	 */
	public float distance(Vector2f other) {
		float dx = other.getX() - getX();
		float dy = other.getY() - getY();
		
		return (float) Math.sqrt((dx*dx)+(dy*dy));
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (int) ((x*y)*1000);
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (other instanceof Vector2f) {
			Vector2f o = ((Vector2f) other);
			return (o.x == x) && (o.y == y);
		}
		
		return false;
	}
}
