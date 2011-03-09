package lejos.robotics.pathfinding;

import java.awt.geom.Point2D;
import java.util.Collection;

import lejos.robotics.navigation.ArcAlgorithms;

// NOTE (add to docs): There is really no guarantee these will link up with goal node because they are randomly generated,
// but they should eventually if you use enough connections and a long enough maxDist.
// TODO: Add ability to prune out paths that come close to map data objects.
public class RandomSelfGeneratingNode extends Node {
	
	/**
	 * firstCall indicates if getNeighbors() has been called yet. false = has been called
	 */
	private boolean firstCall = true;
	
	private int connections;
	private float maxDist;
	
	static Node goal = null; // TODO: Of course, this blows apart the possibility of using two sets of nodes with different goals.
	
	public RandomSelfGeneratingNode(float x, float y, float maxDist, int connections) {
		super(x, y);
		this.maxDist = maxDist;
		this.connections = connections;
	}
	
	public RandomSelfGeneratingNode(float x, float y, float maxDist, int connections, Node goal) {
		this(x, y, maxDist, connections);
		RandomSelfGeneratingNode.goal = goal;
	}
	
	/** When this method is called the first time, it randomly generates a set of neighbors according to
	 * the parameters in the constructor. It then calls addNeighbor() for each one. The next time getNeighbors() is called
	 * it will return the same set of neighbors it initially generated.
	 * 
	 * Each of these neighbors is a RandomSelfGeneratingNode too, so when their neighbors are requested they will also
	 * self-generate a set of neighbors. 
	 * 
	 * Each random node will also add the "parent" node to the list of nodes. 
	 * 
	 * If a goal node was added, it checks if the node is within maxDist of the goal node. If it is, both the goal node and
	 * this node add each other as neighbors. 
	 */
	public Collection <Node> getNeighbors() {
		// TODO: When to do the pruning of these?
		if(firstCall) {
			
			// TODO: Really, if any of the previously generated nodes are in range, it should be added. Means it would
			// need to scrutinize every previous node generated. Either backtrack using Node.getPredecessor() or keep another
			// list of all nodes generated so far.
			// See if goal node is in range. Yes? Add.
			float goal_dist = (float)Point2D.distance(goal.x, goal.y, this.x, this.y);
			if(goal_dist <= maxDist) {
				this.addNeighbor(goal);
				goal.addNeighbor(this);
			}
			
			int nodes_to_add = connections - super.neighbors();
			for(int i=0;i<nodes_to_add;i++) {
				// Generate new node with random direction and distance from this one.
				float rand_x = (float)(Math.random() * maxDist);
				float rand_y = (float)(Math.random() * maxDist);
				if(Math.random() < 0.5) rand_x*= -1;
				if(Math.random() < 0.5) rand_y*= -1;
				float new_x = this.x + rand_x;
				float new_y = this.y + rand_y;
				//String new_id = "(" + new_x + ", " + new_y + ")";
				RandomSelfGeneratingNode newNode = new RandomSelfGeneratingNode(new_x, new_y, maxDist, connections);
				
				// Add parent node (this) and add new to this.
				newNode.addNeighbor(this);
				this.addNeighbor(newNode);
				
			}
			firstCall = false;
		}
		return super.getNeighbors();
	}
	
}
