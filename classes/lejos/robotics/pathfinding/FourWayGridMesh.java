package lejos.robotics.pathfinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;

// TODO: Recursion might be used when collecting all nodes for a set given one node.
// Generates a node set once at the beginning of user program, then uses same set for all subsequent navigation.
public class FourWayGridMesh implements NavigationMesh {

	ArrayList <Node> mesh = null;
	LineMap map = null;
	private float clearance;
	private float gridspace;
	
	public FourWayGridMesh(LineMap map, float gridSpace, float clearance) {
		setMap(map);
		setClearance(clearance);
		setGridSpacing(gridSpace);
		// TODO: OPTION 1: Generate now (predictable) or later (allows changes to be made to map? Nah.) 
		// Maybe generate called here? If called later, someone could  add a node before it was
		// generated and expect it to be connected, which it won't be.
	}
	
	public Collection <Node> getMesh(){
		if(mesh == null) regenerate();
		return mesh;
	}
	
	// NOTE: When grid space value is changed, this class does not regenerate the navigation mesh until regenerate() is explicitly called.
	public void setGridSpacing(float gridSpace) {
		this.gridspace = gridSpace;
	}
	
	// NOTE: When clearance value is changed, this class does not regenerate the navigation mesh until regenerate() is explicitly called.
	public void setClearance(float clearance) {
		this.clearance = clearance;
	}
	
	// NOTE: When Map changed, does not regenerate the navigation mesh until regenerate() is explicitly called. 
	public void setMap(LineMap map) {
		this.map = map;
	}
	
	public void regenerate() {
		long startNanoT = System.nanoTime();
		long startFreeMem = Runtime.getRuntime().freeMemory();
		
		mesh = new ArrayList <Node> ();
		
		// First node is "clearance" from the corner of the map
		Rectangle bounds = map.getBoundingRect();
		
		float startx = bounds.x + clearance;
		float starty = bounds.y + clearance;
		
		float endx = bounds.width + bounds.x - clearance;
		float endy = bounds.height + bounds.y - clearance;
		
		int x_grid_squares = 0;
		int y_grid_squares = 0;
		
		System.out.println("Grid space: " + gridspace);
		
		
		for(float y = starty;y<endy;y+=gridspace) {
			y_grid_squares += 1;
			for(float x = startx;x<endx;x+=gridspace) {
				x_grid_squares += 1;
				mesh.add(new Node(x, y));
				// TODO: Why not use addNode for each subsequent node?! Because it tries to connect it to others.
			}
		}
		x_grid_squares /= y_grid_squares;
		
		System.out.println(x_grid_squares + " by " + y_grid_squares);
		
		// Start connecting neighbors in upper left, connect to one to right and one down
		Node cur = mesh.get(0);
		for(int i=1;i<mesh.size();i++) {
			Node rightNode = mesh.get(i);
			Node downNode = null;
			int down = i - 1 + x_grid_squares;
			if(down < mesh.size()) {
				downNode = mesh.get(down);
				connect(cur, downNode); // Check if no more down.
			}
			if(i % x_grid_squares != 0) connect(cur, rightNode); // Check if no more to right
			
			cur = rightNode;
		}
		
		// TODO: At this point I could optionally remove nodes that are too close to geometry. Pretty quick.
		
		long totalNanoT = System.nanoTime() - startNanoT;
		long endFreeMem = Runtime.getRuntime().freeMemory();
		
		System.out.println("Mesh time " + (totalNanoT/1000000D) + " ms");
		System.out.print("Free Memory start: " + startFreeMem);
		System.out.print(" end: " + endFreeMem);
		System.out.println(" used: " + (startFreeMem - endFreeMem));
	}
	
	public boolean connect(Node node1, Node node2) {
		
		if(map != null) {
			// Check if nodes are within bounding box:
			if(!map.getBoundingRect().contains(node1)) return false;
			if(!map.getBoundingRect().contains(node2)) return false;
			
			Line connection = new Line(node1.x, node1.y, node2.x, node2.y);
			Line [] lines = map.getLines();
			for(int i=0;i<lines.length;i++) {
				if(lines[i].segDist(connection) < clearance)
					return false;
			}
		}
		node1.addNeighbor(node2);
		node2.addNeighbor(node1);
		
		return true;
	}
	
	public boolean disconnect(Node node1, Node node2) {
		// TODO: Return true if nodes were previously connected? Or return void.
		node1.removeNeighbor(node2);
		node2.removeNeighbor(node1);
		return true;
	}
	
	// TODO: Okay, if 4 is used, connect to 4. If >4 used, only 4 still. If 2 used, quit after 2. If 0 used, don't connect to any.
	// Returns 0 if no connections made, making this an orphaned node. Probably the x, y of the node you tried to add
	// was outside of the bounded area of the map.
	public int addNode(Node node, int neighbors) {
		if(mesh == null) regenerate();
		
		int total = 0;
		
		// Fact: Only four nodes can logically be within "gridspace" of a node.
		for(int i=0;i<mesh.size();i++) {
			Node cur = mesh.get(i);
			float dif_x = Math.abs(cur.x - node.x);
			if(dif_x <= gridspace) {
				float dif_y = Math.abs(cur.y - node.y);
				if(dif_y <= gridspace) {
					if(connect(node, cur)) total++;
					if(total >= neighbors) {
						mesh.add(node);
						return total;
					}
				}
			}
		}
		mesh.add(node);
		return total;
	}

	// Removes it from this set and disconnects it from all neighbors.
	// NOTE: There is no guarantee it is disconnecting from only nodes in this mesh. It disconnects from all nodes
	// it currently has registered as neighbors.
	public boolean removeNode(Node node) {
		//System.out.print("MAIN NODE ");
		//outputNodeData(node);
		Collection <Node> coll = node.getNeighbors();
		ArrayList <Node> arr = new ArrayList <Node> (coll);
		for(int i=0;i<arr.size();i++) {
			Node neighbor = arr.get(i);
			//System.out.print("NEIGHBOR NODE ");
			//outputNodeData(neighbor);
			neighbor.removeNeighbor(node);
			node.removeNeighbor(neighbor); // Could remove all of them after with one call!
		}
		
		//System.out.print("POST MAIN NODE ");
		//outputNodeData(node);
				
		return mesh.remove(node);
	}
	
	// TODO: DELETE WHEN DONE TESTING
	static public void outputNodeData(Node node) {
		System.out.println("Name: " + node);
		Collection <Node> coll = node.getNeighbors();
		ArrayList <Node> arr = new ArrayList <Node> (coll);
		for(int i=0;i<arr.size();i++)
			System.out.println("Neighbor " + i + ": " + arr.get(i));
	}
	
	
}


