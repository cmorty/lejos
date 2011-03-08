package lejos.robotics.pathfinding;

import java.util.Collection;
import lejos.robotics.navigation.WayPoint;

/**
 * NOTE: Implementations of this interface should override Object.toString() with the name of the algorithm. 
 * e.g. "A*", "Dijkstra", "Best-First", "D* Lite"
 * @author BB
 * @see java.lang.Object#toString()
 */
public interface SearchAlgorithm {
	
	/**
	 * This method returns the name of this algorithm. The purpose of this method is for GUI front ends
	 * which allow the user to pick an algorithm from a collection of possible algorithms. 
	 * @return The string representation of the algorithm. e.g. "A*", "Dijkstra", "Best-First", "D* Lite"
	 */
	public String toString(); // TODO Redundant? Doesn't truly force user to implement this since Object does.
	
	public Collection <WayPoint> findPath(Node startNode, Node endNode);
}
