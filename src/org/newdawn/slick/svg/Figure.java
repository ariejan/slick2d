package org.newdawn.slick.svg;

import org.newdawn.slick.geom.Shape;

/**
 * A figure that is part of diagram loaded from SVG
 *
 * @author kevin
 */
public class Figure {
	/** Ellipse Type */
	public static final int ELLIPSE = 1;
	/** Line Type */
	public static final int LINE = 2;
	/** Rectangle Type */
	public static final int RECTANGLE = 3;
	/** Path Type */
	public static final int PATH = 4;
	/** Polygon Type */
	public static final int POLYGON = 5;
	
	/** The type of this figure */
	private int type;
	
	/** The geometric shape of the figure */
	private Shape shape;
	/** The other bits of data assocaited with the SVG element */
	private NonGeometricData data;
	
	/**
	 * Create a new figure
	 *
	 * @param type The type of the figure
	 * @param shape The shape of the figure
	 * @param data The other associated data
	 */
	public Figure(int type, Shape shape, NonGeometricData data) {
		this.shape = shape;
		this.data = data;
		this.type = type;
	}

	/**
	 * Get the type of this figure
	 * 
	 * @return The type of this figure
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Get the shape of this figure
	 * 
	 * @return The shape of this figure
	 */
	public Shape getShape() {
		return shape;
	}
	
	/**
	 * Get the data associated with this figure
	 * 
	 * @return The data associated with this figure
	 */
	public NonGeometricData getData() {
		return data;
	}
}
