package org.newdawn.slick;

/**
 * An attempt to cover up some bad decisions in design for Color. This class
 * cross checks any attempted changes to a percievable constant colour.
 * 
 * @author kevin
 */
public class ConstantColor extends Color {
	/** The original red component */
	private float or;
	/** The original green component */
	private float og;
	/** The original blue component */
	private float ob;
	/** The original alpha component */
	private float oa;

	/**
	 * Create a 4 component colour
	 * 
	 * @param r The red component of the colour (0.0 -> 1.0)
	 * @param g The green component of the colour (0.0 -> 1.0)
	 * @param b The blue component of the colour (0.0 -> 1.0)
	 * @param a The alpha component of the colour (0.0 -> 1.0)
	 */
	public ConstantColor(float r, float g, float b, float a) {
		super(r, g, b, a);
		
		or = r;
		og = g;
		ob = b;
		oa = a;
	}

	/**
	 * Create a 4 component colour
	 * 
	 * @param r The red component of the colour (0 -> 255)
	 * @param g The green component of the colour (0 -> 255)
	 * @param b The blue component of the colour (0 -> 255)
	 * @param a The alpha component of the colour (0 -> 255)
	 */
	public ConstantColor(int r, int g, int b, int a) {
		super(r, g, b, a);
		
		or = r;
		og = g;
		ob = b;
		oa = a;
	}

	/**
	 * Confirm the colour hasn't changed
	 */
	private void checkColor() {
		if (or != r) {
			throw new IllegalArgumentException("Attempted modification of constant color");
		}
		if (og != g) {
			throw new IllegalArgumentException("Attempted modification of constant color");
		}
		if (ob != b) {
			throw new IllegalArgumentException("Attempted modification of constant color");
		}
		if (oa != a) {
			throw new IllegalArgumentException("Attempted modification of constant color");
		}
	}
	
	/**
	 * @see org.newdawn.slick.Color#add(org.newdawn.slick.Color)
	 */
	public void add(Color c) {
		throw new UnsupportedOperationException("Constant colors can not be modified");
	}
	
	/**
	 * @see org.newdawn.slick.Color#scale(float)
	 */
	public void scale(float value) {
		throw new UnsupportedOperationException("Constant colors can not be modified");
	}

	/**
	 * @see org.newdawn.slick.Color#bind()
	 */
	public void bind() {
		checkColor();
		
		super.bind();
	}

	/**
	 * @see org.newdawn.slick.Color#brighter()
	 */
	public Color brighter() {
		checkColor();
		
		return super.brighter();
	}

	/**
	 * @see org.newdawn.slick.Color#brighter(float)
	 */
	public Color brighter(float scale) {
		checkColor();
		
		return super.brighter(scale);
	}

	/**
	 * @see org.newdawn.slick.Color#darker()
	 */
	public Color darker() {
		checkColor();
		
		return super.darker();
	}

	/**
	 * @see org.newdawn.slick.Color#darker(float)
	 */
	public Color darker(float scale) {
		checkColor();
		
		return super.darker(scale);
	}

	/**
	 * @see org.newdawn.slick.Color#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		checkColor();
		
		return super.equals(other);
	}

	/**
	 * @see org.newdawn.slick.Color#getAlpha()
	 */
	public int getAlpha() {
		checkColor();
		
		return super.getAlpha();
	}

	/**
	 * @see org.newdawn.slick.Color#getAlphaByte()
	 */
	public int getAlphaByte() {
		checkColor();
		
		return super.getAlphaByte();
	}

	/**
	 * @see org.newdawn.slick.Color#getBlue()
	 */
	public int getBlue() {
		checkColor();
		
		return super.getBlue();
	}

	/**
	 * @see org.newdawn.slick.Color#getBlueByte()
	 */
	public int getBlueByte() {
		checkColor();
		
		return super.getBlueByte();
	}

	/**
	 * @see org.newdawn.slick.Color#getGreen()
	 */
	public int getGreen() {
		checkColor();
		
		return super.getGreen();
	}

	/**
	 * @see org.newdawn.slick.Color#getGreenByte()
	 */
	public int getGreenByte() {
		checkColor();
		
		return super.getGreenByte();
	}

	/**
	 * @see org.newdawn.slick.Color#getRed()
	 */
	public int getRed() {
		checkColor();
		
		return super.getRed();
	}

	/**
	 * @see org.newdawn.slick.Color#getRedByte()
	 */
	public int getRedByte() {
		checkColor();
		
		return super.getRedByte();
	}

	/**
	 * @see org.newdawn.slick.Color#hashCode()
	 */
	public int hashCode() {
		checkColor();
		
		return super.hashCode();
	}

	/**
	 * @see org.newdawn.slick.Color#multiply(org.newdawn.slick.Color)
	 */
	public Color multiply(Color c) {
		checkColor();
		
		return super.multiply(c);
	}

	/**
	 * @see org.newdawn.slick.Color#toString()
	 */
	public String toString() {
		checkColor();
		
		return super.toString();
	}

	/**
	 * @see org.newdawn.slick.Color#addToCopy(org.newdawn.slick.Color)
	 */
	public Color addToCopy(Color c) {
		checkColor();
		
		return super.addToCopy(c);
	}

	/**
	 * @see org.newdawn.slick.Color#scaleCopy(float)
	 */
	public Color scaleCopy(float value) {
		checkColor();
		
		return super.scaleCopy(value);
	}

}
