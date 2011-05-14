package lejos.robotics.pathfinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import lejos.robotics.navigation.*;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * This path finder class uses one of the common search algorithms (e.g. A*) and a navigation mesh (e.g. grid) and
 * uses them to find a path around map geometry.
 *  
 * @author BB
 *
 */
public class NodePathFinder implements PathFinder{

	private ArrayList<WayPointListener> listeners;
	private SearchAlgorithm alg;
	private NavigationMesh mesh = null;
	
	/**
	 * Instantiates a NodePathFinder object using a specified algorithm. Ideally this class would work with
	 * self propagating nodes, but it currently is not able to. 
	 * @param alg
	 */
	private NodePathFinder(SearchAlgorithm alg) {
		// TODO: This method is not really valid (hence why market private for now) because the
		// algorithm will currently not work without a mesh.
		setSearchAlgorithm(alg);
	}
	
	/**
	 * Instantiates a NodePathFinder object using a specified algorithm. The supplied mesh is used
	 * to add and remove nodes (start and goal) when requesting a path.   
	 * @param alg The search algorithm.
	 * @param mesh The navigation mesh is a set of nodes in various configurations (e.g. grid).
	 */
	public NodePathFinder(SearchAlgorithm alg, NavigationMesh mesh) {
		this(alg);
		setNavMesh(mesh);
	}
	
	/**
	 * Method for changing the navigation mesh after this has been instantiated.
	 * @param mesh
	 */
	public void setNavMesh(NavigationMesh mesh) {
		this.mesh = mesh;
	}
	
	/**
	 * Method for changing the search algorithm after this has been instantiated.
	 * @param alg
	 */
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
		// TODO: Big problem: These nodes will not be linked to anything if no mesh was given!
		Node startNode = new Node(start.getX(), start.getY());
		Node goalNode = new Node((float)goal.getX(), (float)goal.getY());
		
		// Step 2: If Mesh is not null, add them to set? 
		if(mesh != null) {
			mesh.addNode(startNode, 4);
			mesh.addNode(goalNode, 4);
		}
		// Step 3: Use alg to find path.
		Collection <WayPoint> path = alg.findPath(startNode, goalNode);
		if(path == null) throw new DestinationUnreachableException();
		
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
			// TODO: Call pathComplete() on all listeners with false if unable to find route.
			// l.pathComplete(false); // Code should be below. Mark boolean success as false here.
		}
		if(listeners != null) { 
			for(WayPointListener l : listeners) {
				Iterator<WayPoint> iterator = solution.iterator(); // TODO: If solution null, this can crash here.
				while(iterator.hasNext()) {
					l.nextWaypoint(iterator.next());
				}
				l.pathComplete();
			}
		}
	}
}