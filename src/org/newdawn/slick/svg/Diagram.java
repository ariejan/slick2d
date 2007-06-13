package org.newdawn.slick.svg;

import java.util.ArrayList;

/**
 * A diagram read from SVG containing multiple figures
 *
 * @author kevin
 */
public class Diagram {
	/** The figures in the diagram */
	private ArrayList figures = new ArrayList();
	
	/**
	 * Create a new empty diagram
	 */
	public Diagram() {
		
	}
	
	/**
	 * Add a figure to the diagram
	 * 
	 * @param figure The figure to add
	 */
	public void addFigure(Figure figure) {
		figures.add(figure);
	}
	
	/**
	 * Get the number of figures in the diagram
	 * 
	 * @return The number of figures in the diagram
	 */
	public int getFigureCount() {
		return figures.size();
	}
	
	/**
	 * Get the figure at a given index
	 * 
	 * @param index The index of the figure to retrieve
	 * @return The figure at the given index
	 */
	public Figure getFigure(int index) {
		return (Figure) figures.get(index);
	}
	
	/**
	 * Remove a figure from the diagram
	 * 
	 * @param figure The figure to be removed
	 */
	public void removeFigure(Figure figure) {
		figures.remove(figure);
	}
}
