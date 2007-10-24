package org.newdawn.slick.svg;

import java.util.ArrayList;

import org.newdawn.slick.geom.MorphShape;

/**
 * A utility to allow morphing between a set of similar SVG diagrams
 * 
 * @author kevin
 */
public class SVGMorph extends Diagram {
	/** The list of figures being morphed */
	private ArrayList figures = new ArrayList();
	
	/**
	 * Create a new morph with a first diagram base
	 * 
	 * @param diagram The base diagram which provides the first step of the morph
	 */
	public SVGMorph(Diagram diagram) {
		for (int i=0;i<diagram.getFigureCount();i++) {
			Figure figure = diagram.getFigure(i);
			Figure copy = new Figure(figure.getType(), new MorphShape(figure.getShape()), figure.getData(), figure.getTransform());
			
			figures.add(copy);
		}
	}
	
	/**
	 * Add a subsquent step to the morphing
	 * 
	 * @param diagram The diagram to add as the next step in the morph
	 */
	public void addStep(Diagram diagram) {
		if (diagram.getFigureCount() != figures.size()) {
			throw new RuntimeException("Mismatched diagrams, missing ids");
		}
		for (int i=0;i<diagram.getFigureCount();i++) {
			Figure figure = diagram.getFigure(i);
			String id = figure.getData().getMetaData();
			
			for (int j=0;j<figures.size();j++) {
				Figure existing = (Figure) figures.get(j);
				if (existing.getData().getMetaData().equals(id)) {
					MorphShape morph = (MorphShape) existing.getShape();
					morph.addShape(figure.getShape());
					break;
				}
			}
		}
	}

	/**
	 * Set the "time" index for this morph. This is given in terms of diagrams, so
	 * 0.5f would give you the position half way between the first and second diagrams.
	 * 
	 * @param time The time index to represent on this diagrams
	 */
	public void setMorphTime(float time) {
		for (int i=0;i<figures.size();i++) {
			Figure figure = (Figure) figures.get(i);
			MorphShape shape = (MorphShape) figure.getShape();
			shape.setMorphTime(time);
		}
	}
	
	/**
	 * @see Diagram#getFigureCount()
	 */
	public int getFigureCount() {
		return figures.size();
	}

	/**
	 * @see Diagram#getFigure(int)
	 */
	public Figure getFigure(int index) {
		return (Figure) figures.get(index);
	}
}
