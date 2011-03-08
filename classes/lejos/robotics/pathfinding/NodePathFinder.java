package lejos.robotics.pathfinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import lejos.robotics.Pose;
import lejos.robotics.navigation.*;

public class NodePathFinder implements PathFinder{

	private ArrayList<WayPointListener> listeners;
	private SearchAlgorithm alg;
	private NavigationMesh mesh = null;
	
	public NodePathFinder(SearchAlgorithm alg) {
		setSearchAlgorithm(alg);
	}
	
	public NodePathFinder(SearchAlgorithm alg, NavigationMesh mesh) {
		this(alg);
		setNavMesh(mesh);
	}
	
	public void setNavMesh(NavigationMesh mesh) {
		this.mesh = mesh;
	}
	
	public void setSearchAlgorithm(SearchAlgorithm alg) {
		this.alg = alg;
	}
	
	public void addListener(WayPointListener wpl) {
		if(listeners == null )listeners = new ArrayList<WayPointListener>();
		listeners.add(wpl);
	}

	public Collection<WayPoint> findRoute(Pose start, WayPoint goal)
			throws DestinationUnreachableException {
		// Step 1: Make nodes out of start and destination
		Node startNode = new Node(start.getX(), start.getY());
		Node goalNode = new Node((float)goal.getX(), (float)goal.getY());
		// Step 2: If Mesh is not null, add them to set? 
		if(mesh != null) {
			mesh.addNode(startNode, 4);
			mesh.addNode(goalNode, 4);
		}
		// Step 3: Use alg to find path.
		Collection <WayPoint> path = alg.findPath(startNode, goalNode);
		
		// Step 4: If mesh is not null, remove them from set?
		if(mesh != null) {
			mesh.removeNode(startNode);
			mesh.removeNode(goalNode);
		}
		
		return path;
	}

	public void startPathFinding(Pose start, WayPoint end) {
		Collection<WayPoint> solution = null;
		try {
			solution = findRoute(start, end);
		} catch (DestinationUnreachableException e) {
			// TODO Not sure what the proper response is here. All in one. Perhaps call pathComplete() with false?
		}
		if(listeners != null) { 
			for(WayPointListener l : listeners) {
				Iterator<WayPoint> iterator = solution.iterator(); 
				while(iterator.hasNext()) {
					l.nextWaypoint(iterator.next());
				}
				l.pathComplete();
			}
		}
	}
}