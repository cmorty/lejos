package lejos.robotics.pathfinding;

import java.util.Collection;

import lejos.robotics.navigation.WayPoint;

public interface SearchAlgorithm {
	
	public Collection <WayPoint> findPath(Node startNode, Node endNode);
}
