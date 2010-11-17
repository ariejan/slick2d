package org.newdawn.slick.util.pathfinding.navmesh;

import java.util.ArrayList;

/**
 * A path across a navigation mesh
 * 
 * @author kevin
 */
public class NavPath {
	/** The list of links that form this path */
	private ArrayList links = new ArrayList();
	
	/**
	 * Create a new path
	 */
	public NavPath() {		
	}
	
	/**
	 * Push a link to the start of the path
	 * 
	 * @param link The link to the start of the path
	 */
	public void push(Link link) {
		links.add(0, link);
	}
}
