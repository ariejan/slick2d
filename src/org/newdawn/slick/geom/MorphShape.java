package org.newdawn.slick.geom;

import java.util.ArrayList;

/**
 * A shape that morphs between a set of other shapes
 *  
 * @author kevin
 */
public class MorphShape extends Shape {
	/** The shapes to morph between */
	private ArrayList shapes = new ArrayList();
	/** The index of the first shape */
	private int a;
	/** The index of the second shape we're morphing to */
	private int b;
	/** The offset between the shapes */
	private float offset;
	
	/**
	 * Create a new mighty morphin shape
	 * 
	 * @param base The base shape we're starting the morph from
	 */
	public MorphShape(Shape base) {
		shapes.add(base);
		float[] copy = base.points;
		this.points = new float[copy.length];
	}

	/**
	 * Add a subsequent shape that we should morph too in order
	 * 
	 * @param shape The new shape that forms part of the morphing shape
	 */
	public void addShape(Shape shape) {
		if (shape.points.length != points.length) {
			throw new RuntimeException("Attempt to morph between two shapes with different vertex counts");
		}
		shapes.add(shape);
	}
	
	/**
	 * Set the "time" index for this morph. This is given in terms of shapes, so
	 * 0.5f would give you the position half way between the first and second shapes.
	 * 
	 * @param time The time index to represent on this shape
	 */
	public void setMorphTime(float time) {
		int p = (int) time;
		int n = p +1;
		float offset = time - p;
		
		p = rational(p);
		n = rational(n);
		
		setFrame(p, n, offset);
	}
	
	/**
	 * Get an index that is rational, i.e. fits inside this set of shapes
	 * 
	 * @param n The index to rationalize
	 * @return The index rationalized
	 */
	private int rational(int n) {
		while (n >= shapes.size()) {
			n -= shapes.size();
		}
		while (n < 0) {
			n += shapes.size();
		}
		
		return n;
	}
	
	/**
	 * Set the frame to be represented 
	 * 
	 * @param a The index of the first shape
	 * @param b The index of the second shape
	 * @param offset The offset between the two shapes to represent
	 */
	private void setFrame(int a, int b, float offset) {
		this.a = a;
		this.b = b;
		this.offset = offset;
		pointsDirty = true;
	}
	
	/**
	 * @see MorphShape#createPoints()
	 */
	protected void createPoints() {
		float[] apoints = ((Shape) shapes.get(a)).points;
		float[] bpoints = ((Shape) shapes.get(b)).points;
		
		for (int i=0;i<points.length;i++) {
			points[i] = apoints[i] * (1 - offset);
			points[i] += bpoints[i] * offset;
		}
	}

	/**
	 * @see MorphShape#transform(Transform)
	 */
	public Shape transform(Transform transform) {
		throw new UnsupportedOperationException("Unable to transform a morphing shape");
	}
}
