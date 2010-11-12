package org.newdawn.slick.util.pathfinding.navmesh;

import java.util.ArrayList;

/**
 * A nav-mesh is a set of shapes that describe the navigation of a map. These
 * shapes are linked together allow path finding but without the high
 * resolution that tile maps require. This leads to fast path finding and 
 * potentially much more accurate map definition.
 *  
 * @author kevin
 *
 */
public class NavMesh {
	/** The list of spaces that build up this navigation mesh */
	private ArrayList spaces = new ArrayList();
	
	/**
	 * Create a new empty mesh
	 */
	public NavMesh() {
		
	}
	
	/**
	 * Create a new mesh with a set of spaces
	 * 
	 * @param spaces The spaces included in the mesh
	 */
	public NavMesh(ArrayList spaces) {
		this.spaces.addAll(spaces);
	}
	
	/**
	 * Get the number of spaces that are in the mesh
	 * 
	 * @return The spaces in the mesh
	 */
	public int getSpaceCount() {
		return spaces.size();
	}
	
	/**
	 * Get the space at a given index
	 * 
	 * @param index The index of the space to retrieve
	 * @return The space at the given index
	 */
	public Space getSpace(int index) {
		return (Space) spaces.get(index);
	}
	
	/**
	 * Add a single space to the mesh
	 * 
	 * @param space The space to be added
	 */
	public void addSpace(Space space) {
		spaces.add(space);
	}
}
