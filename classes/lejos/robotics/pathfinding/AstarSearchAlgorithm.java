package lejos.robotics.pathfinding;

import java.util.*;

import lejos.robotics.navigation.ArcAlgorithms;
import lejos.robotics.navigation.WayPoint;

public class AstarSearchAlgorithm implements SearchAlgorithm{
	
	public Collection <WayPoint> findPath(Node start, Node goal) {
		
	     ArrayList <Node> closedset = new ArrayList(); // The set of nodes already evaluated. Empty at start.     
	     ArrayList <Node> openset = new ArrayList(); // The set of tentative nodes to be evaluated. 
	     openset.add(start); // openset contains startNode at start.
	     ArrayList <Node> path = new ArrayList(); // came_from := the empty map // The map of navigated nodes.
	     start.setCost(0); // Distance from start along optimal path. Zero by definition since at start. g(start)
	     start.setHeuristicEstimate(ArcAlgorithms.distBetweenPoints(start, goal)); // h(start)
	     
	     while (!openset.isEmpty()) {
	         Node x = getLowestCost(openset); // get the node in openset having the lowest f_score[] value
	         System.out.println("Node " + x.getId() + " F score " + x.getFScore());
	         if(x == goal) {
	             //return reconstructPath(goal); // reconstruct_path(came_from, came_from[goal])
	         	 ArrayList <WayPoint> final_path = new ArrayList();
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
	                 
	             float tentative_g_score = x.getCost() + ArcAlgorithms.distBetweenPoints(x, y); // g_score[x] + dist_between(x,y)
	             boolean tentative_is_better = false;
	             
	             if (!openset.contains(y)) { // if y not in openset
	                 openset.add(y); // add y to openset
	                 tentative_is_better = true;
	             } else if(tentative_g_score < y.getCost()) { // if tentative_g_score < g_score[y]
	                 tentative_is_better = true;
	             } else
	                 tentative_is_better = false;
	             
	             if (tentative_is_better) {
	                y.setPredecessor(x); // came_from[y] := x
	             }
	             
	             y.setCost(tentative_g_score);
	             y.setHeuristicEstimate(ArcAlgorithms.distBetweenPoints(y, goal)); // heuristic_estimate_of_distance(y, goal)
	             // TODO Update(closedset,y)  // This might mean update the values of y in closedset and openset? Unneeded.
	             // TODO Update(openset,y)  
	         } // while(yIter.hasNext()
	     } // main loop while
	     return null; // TODO return failure. I suppose null could indicate failure? Or boolean and use object in constructor.
	} // method end
	
	public static Node getLowestCost(Collection <Node> nodeSet) {
		Iterator <Node> nodeIterator = nodeSet.iterator();
		Node best = nodeIterator.next();
		while(nodeIterator.hasNext()) {
			Node cur = nodeIterator.next();
			if(cur.getFScore() < best.getFScore())
				best = cur;
		}
		return best;
	}
}

// TODO: Need to also do D*Lite, which is dominantly used in robotics.