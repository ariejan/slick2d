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
	 * Add a single space to the mesh
	 * 
	 * @param space The space to be added
	 */
	public void addSpace(Space space) {
		spaces.add(space);
	}
}
