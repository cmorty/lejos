package lejos.robotics.pathfinding;

import java.util.Collection;
import lejos.robotics.navigation.WayPoint;

public interface SearchAlgorithm {
	
	/**
	 * This method returns the name of this algorithm. The purpose of this method is for GUI front ends
	 * which allow the user to pick an algorithm from a collection of possible algorithms. 
	 * @return The string representation of the algorithm. e.g. "A*", "Dijkstra", "Best-First", "D* Lite"
	 */
	public String getSearchName();
	
	public Collection <WayPoint> findPath(Node startNode, Node endNode);
}
