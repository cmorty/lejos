package lejos.robotics.pathfinding;

import java.awt.geom.Point2D;
import java.util.*;
import lejos.robotics.navigation.WayPoint;

// TODO: This works, but this code keeps the Node properties right in the Node object. The same Node set
// (aka Navigation Mesh) might conceivably (probably) be used repeatedly for many different searches. So things
// like setPredecessor() and setG_Score() should be temporary, not part of Node object?

/**
 * This is an implementation of the A* search algorithm. Typically this object would be instantiated and then used
 * in a NodePathFinder constructor, along with a set of connected nodes.
 * @see lejos.robotics.pathfinding.NodePathFinder
 * @author BB 
 */
public class AstarSearchAlgorithm implements SearchAlgorithm{

	private static final String STRING_NAME = "A*";

	public Collection <WayPoint> findPath(Node start, Node goal) {
		
		ArrayList <Node> closedset = new ArrayList<Node>(); // The set of nodes already evaluated. Empty at start.     
		ArrayList <Node> openset = new ArrayList<Node>(); // The set of tentative nodes to be evaluated. 
		openset.add(start); // openset contains startNode at start.
		//ArrayList <Node> path = new ArrayList<Node>(); // came_from := the empty map // The map of navigated nodes.
		start.setG_Score(0); // Distance from start along optimal path. Zero by definition since at start. g(start)
		start.setH_Score((float)Point2D.distance(start.x, start.y, goal.x, goal.y)); // h(start)

		while (!openset.isEmpty()) {
			Node x = getLowestCost(openset); // get the node in openset having the lowest f_score[] value

			if(x == goal) {
				//return reconstructPath(goal); // reconstruct_path(came_from, came_from[goal])
				ArrayList <WayPoint> final_path = new ArrayList<WayPoint>();
				Node.reconstructPath(goal, start, final_path);
				return final_path;
			}
			openset.remove(x); // remove x from openset
			closedset.add(x); // add x to closedset

			Collection <Node> yColl = x.getNeighbors();
			Iterator <Node> yIter = yColl.iterator();

			while(yIter.hasNext()) { // for each y in neighbor_nodes(x)
				Node y = yIter.next();
				if(closedset.contains(y)) continue;  // if y in closedset already, go to next one

				float tentative_g_score = x.getG_Score() + (float)Point2D.distance(x.x, x.y, y.x, y.y); // g_score[x] + dist_between(x,y)
				boolean tentative_is_better = false;

				if (!openset.contains(y)) { // if y not in openset
					openset.add(y); // add y to openset
					tentative_is_better = true;
				} else if(tentative_g_score < y.getG_Score()) { // if tentative_g_score < g_score[y]
					tentative_is_better = true;
				} else
					tentative_is_better = false;

				if (tentative_is_better) {
					y.setPredecessor(x); // came_from[y] := x
				}

				y.setG_Score(tentative_g_score);
				y.setH_Score((float)Point2D.distance(y.x, y.y, goal.x, goal.y)); // heuristic_estimate_of_distance(y, goal)
				// Update(closedset,y)  This might mean update the values of y in closedset and openset? Unneeded I assume.
				// Update(openset,y)  
			} // while(yIter.hasNext()
		} // main loop while
		return null; // TODO return failure. I suppose null could indicate failure? Or boolean and use object in constructor.
	} // method end

	/**
	 * Finds the node within a set of neighbors with the least cost (potentially shortest distance to goal). 
	 * @return The node with the least cost.
	 */
	private static Node getLowestCost(Collection <Node> nodeSet) {
		/* TODO: This method has potential for optimization. Called very frequently. Probably best to 
		 * move this method to Node (not NavigationMesh), then can optimize individually based on mesh 
		 * type (e.g. integer grid or float navigation mesh). Mesh would probably be preferable, but 
		 * SearchAlgorithm has no access to the mesh, just individual nodes. Perhaps alternate findPath()
		 * or set method for accepting mesh? (float or int is optimization concern) */ 
		Iterator <Node> nodeIterator = nodeSet.iterator();
		Node best = nodeIterator.next();
		while(nodeIterator.hasNext()) {
			Node cur = nodeIterator.next();
			if(cur.getF_Score() < best.getF_Score())
				best = cur;
		}
		return best;
	}

	public String toString() {
		return STRING_NAME;
	}
}