package org.newdawn.slick;

import org.lwjgl.opengl.GL11;

/**
 * A simple wrapper round the values required for a colour
 * 
 * @author Kevin Glass
 */
public class Color {
	/** The fixed colour white */
	public static final Color white = new Color(1.0f,1.0f,1.0f,1.0f);
	/** The fixed colour yellow */
	public static final Color yellow = new Color(1.0f,1.0f,0,1.0f);
	/** The fixed colour red */
	public static final Color red = new Color(1.0f,0,0,1.0f);
	/** The fixed colour blue */
	public static final Color blue = new Color(0,0,1.0f,1.0f);
	/** The fixed colour green */
	public static final Color green = new Color(0,1.0f,0,1.0f);
	/** The fixed colour black */
	public static final Color black = new Color(0,0,0,1.0f);
	/** The fixed colour gray */
	public static final Color gray = new Color(0.5f,0.5f,0.5f,1.0f);
	
	/** The red component of the colour */
	public float r;
	/** The green component of the colour */
	public float g;
	/** The blue component of the colour */
	public float b;
	/** The alpha component of the colour */
	public float a;
	
	/**
	 * Copy constructor
	 * 
	 * @param color The color to copy into the new instance
	 */
	public Color(Color color) {
		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;
	}
	
	/**
	 * Create a 3 component colour
	 * 
	 * @param r The red component of the colour (0.0 -> 1.0)
	 * @param g The green component of the colour (0.0 -> 1.0)
	 * @param b The blue component of the colour (0.0 -> 1.0)
	 */
	public Color(float r,float g,float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 1;
	}

	/**
	 * Create a 3 component colour
	 * 
	 * @param r The red component of the colour (0.0 -> 1.0)
	 * @param g The green component of the colour (0.0 -> 1.0)
	 * @param b The blue component of the colour (0.0 -> 1.0)
	 * @param a The alpha component of the colour (0.0 -> 1.0)
	 */
	public Color(float r,float g,float b,float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	/**
	 * Create a 3 component colour
	 * 
	 * @param r The red component of the colour (0 -> 255)
	 * @param g The green component of the colour (0 -> 255)
	 * @param b The blue component of the colour (0 -> 255)
	 */
	public Color(int r,int g,int b) {
		this.r = r / 255.0f;
		this.g = g / 255.0f;
		this.b = b / 255.0f;
		this.a = 1;
	}

	/**
	 * Create a 3 component colour
	 * 
	 * @param r The red component of the colour (0 -> 255)
	 * @param g The green component of the colour (0 -> 255)
	 * @param b The blue component of the colour (0 -> 255)
	 * @param a The alpha component of the colour (0 -> 255)
	 */
	public Color(int r,int g,int b,int a) {
		this.r = r / 255.0f;
		this.g = g / 255.0f;
		this.b = b / 255.0f;
		this.a = a / 255.0f;
	}
	
	/**
	 * Bind this colour to the GL context
	 */
	public void bind() {
		GL11.glColor4f(r,g,b,a);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return ((int) (r+g+b+a)*255);
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (other instanceof Color) {
			Color o = (Color) other;
			return ((o.r == r) && (o.g == g) && (o.b == b) && (o.a == a));
		}
		
		return false;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Color ("+r+","+g+","+b+","+a+")";
	}
	
	/**
	 * Make a darker instance of this colour
	 * 
	 * @return The darkver version of this colour
	 */
	public Color darker() {
		Color temp = new Color(r * 0.5f,g * 0.5f,b * 0.5f,a);
		
		return temp;
	}
}
