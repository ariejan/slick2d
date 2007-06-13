package org.newdawn.slick.svg;

import org.newdawn.slick.geom.Shape;

/**
 * A figure that is part of diagram loaded from SVG
 *
 * @author kevin
 */
public class Figure {
	/** The geometric shape of the figure */
	private Shape shape;
	/** The other bits of data assocaited with the SVG element */
	private NonGeometricData data;
	
	/**
	 * Create a new figure
	 * 
	 * @param shape The shape of the figure
	 * @param data The other associated data
	 */
	public Figure(Shape shape, NonGeometricData data) {
		this.shape = shape;
		this.data = data;
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
