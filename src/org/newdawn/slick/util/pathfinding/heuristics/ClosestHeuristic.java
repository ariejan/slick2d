package org.newdawn.slick.util.pathfinding.heuristics;

import org.newdawn.slick.util.pathfinding.AStarHeuristic;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

/**
 * A heuristic that uses the tile that is closest to the target
 * as the next best tile.
 * 
 * @author Kevin Glass
 */
public class ClosestHeuristic implements AStarHeuristic {

	/**
	 * @see AStarHeuristic#getCost(TileBasedMap, Mover, int, int, int, int)
	 */
	public int getCost(TileBasedMap map, Mover mover, int x, int y, int tx, int ty) {		
		int dx = tx - x;
		int dy = ty - y;
		
		return (int) (Math.sqrt((dx*dx)+(dy*dy)) * 1000);
	}

}
