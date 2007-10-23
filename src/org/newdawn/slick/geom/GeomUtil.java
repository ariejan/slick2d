package org.newdawn.slick.geom;

/**
 * A set of utilities to play with geometry
 * 
 * @author kevin
 */
public class GeomUtil {
	/**
	 * Subtract one shape from another - note this is experimental and doesn't
	 * currently handle islands
	 *  
	 * @param target The target to be subtracted from
	 * @param missing The shape to subtract 
	 * @return The newly created shape
	 */
	public static Shape subtract(Shape target, Shape missing) {		
		return combine(target, missing, true);
	}

	/**
	 * Join to shapes together. Note that the shapes must be touching
	 * for this method to work.
	 * 
	 * @param target The target shape to union with
	 * @param other The additional shape to union
	 * @return The newly created shape or null if the shapes were not touching
	 */
	public static Shape union(Shape target, Shape other) {
		if (!target.intersects(other)) {
			return null;
		}
		
		return combine(target, other, false);
	}
	
	/**
	 * Combine two shapes
	 * 
	 * @param target The target shape
	 * @param missing The second shape to apply
	 * @param subtract True if we should subtract missing from target, otherwise union
	 * @return The newly created shape
	 */
	private static Shape combine(Shape target, Shape missing, boolean subtract) {
		Shape current = target;
		Shape other = missing;
		int point = 0;
		int dir = 1;
		
		Polygon poly = new Polygon();
		
		// while we've not reached the same point
		while (!poly.includes(current.getPoint(point)[0], current.getPoint(point)[1])) {
			// add the current point to the result shape
			poly.addPoint(current.getPoint(point)[0], current.getPoint(point)[1]);
	
			// if the line between the current point and the next one intersect the
			// other shape work out where on the other shape and start traversing it's 
			// path instead
			Line line = getLine(current, point, rationalPoint(current, point+dir));
			HitResult hit = intersect(other, line);
			if (hit != null) {
				Line hitLine = hit.line;
				Vector2f pt = hit.pt;
				poly.addPoint(pt.x,pt.y);
				
				float dx = hitLine.getDX() / hitLine.length();
				float dy = hitLine.getDY() / hitLine.length();
				dx *= 0.01f;
				dy *= 0.01f;
				
				if (poly.contains(pt.x + dx, pt.y + dy)) {
					// the point is the next one, we need to take the first and traverse
					// the path backwards
					point = hit.p1;
					if (subtract) {
						// if we're subtracting it depends whether we're dealing
						// with the target or the bit being cut out as to which way we turn
						if (current == target) {
							dir = -1;
							point = hit.p2;
						} else {
							dir = 1;
						}
					} else {
						dir = -1;
					}
				} else {
					point = hit.p2;
					if (subtract) {
						// if we're subtracting it depends whether we're dealing
						// with the target or the bit being cut out as to which way we turn
						if (current == target) {
							dir = -1;
							point = hit.p1;
						} else {
							dir = 1;
						}
					} else {
						dir = 1;
					}
				}
				
				// swap the shapes over, we'll traverse the other one 
				Shape temp = current;
				current = other;
				other = temp;
			} else {
				// otherwise just move to the next point in the current shape
				point = rationalPoint(current, point+dir);
			}
		}
		
		return poly;
	}
	
	/**
	 * Intersect a line with a shape
	 * 
	 * @param shape The shape to compare
	 * @param line The line to intersect against the shape
	 * @return The result describing the intersection or null if none
	 */
	private static HitResult intersect(Shape shape, Line line) {
		float distance = Float.MAX_VALUE;
		HitResult hit = null;
		
		for (int i=0;i<shape.getPointCount();i++) {
			int next = rationalPoint(shape, i+1);
			Line local = getLine(shape, i, next);
		
			Vector2f pt = line.intersect(local, true);
			if (pt != null) {
				float newDis = pt.distance(line.getStart());
				if (newDis < distance) {
					hit = new HitResult();
					hit.pt = pt;
					hit.line = local;
					hit.p1 = i;
					hit.p2 = next;
					distance = newDis;
				}
			}
		}
		
		return hit;
	}
	
	/**
	 * Rationalise a point in terms of a given shape
	 * 
	 * @param shape The shape 
	 * @param p The index of the point
	 * @return The index that is rational for the shape
	 */
	public static int rationalPoint(Shape shape, int p) {
		while (p < 0) {
			p += shape.getPointCount();
		}
		while (p >= shape.getPointCount()) {
			p -=  shape.getPointCount();
		}
		
		return p;
	}
	
	/**
	 * Get a line between two points in a shape
	 * 
	 * @param shape The shape
	 * @param s The index of the start point
	 * @param e The index of the end point
	 * @return The line between the two points
	 */
	public static Line getLine(Shape shape, int s, int e) {
		float[] start = shape.getPoint(s);
		float[] end = shape.getPoint(e);
		
		Line line = new Line(start[0],start[1],end[0],end[1]);
		return line;
	}
	
	/**
	 * A lightweigtht description of a intersection between a shape and 
	 * line.
	 * 
	 */
	private static class HitResult {
		/** The line on the target shape that intersected */
		Line line;
		/** The index of the first point on the target shape that forms the line */
		int p1;
		/** The index of the second point on the target shape that forms the line */
		int p2;
		/** The position of the intersection */
		Vector2f pt;
	}
}
