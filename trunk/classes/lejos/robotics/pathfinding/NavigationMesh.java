package lejos.robotics.pathfinding;

import java.util.Collection;

/**
 * An interface for classes which build navigation meshes. 
 * TODO: Consider making it abstract with common methods if any develop?
 * TODO: Consider Self generating nodes inherit this?
 * 
 * @author BB
 *
 */
public interface NavigationMesh {
	// TODO: Fill in some methods here? Or is it up to use how he wants to do this. It isn't like the NavigationMesh is
	// actually used by a class, is it? Unless it can be used to build meshes dynamically too. That's an option.
	
	
	/**
	 * Adds a node to this set and connects it with a number of neighboring nodes. If it is unable to find any
	 * neighbors it will return 0. This might be because the node is outside of the bounded area of the map. 
	 * @return the number of neighboring nodes it was able to connect with
	 */
	public int addNode(Node node, int neighbors);
	
	/**
	 * Removes a node from the set and removes any existing connections with its neighbors.
	 * @param node
	 * @return
	 */
	public boolean removeNode(Node node);
	
	public boolean connect(Node node1, Node node2);
	
	public boolean disconnect(Node node1, Node node2);
	
	// Note: Not used by NodePathFinder but seemed like a conspicuously absent method. 
	public Collection <Node> getMesh();
	
	// Note: Not used by NodePathFinder but seems useful for GUIs and such when parameters changed.
	public void regenerate();
	
}
