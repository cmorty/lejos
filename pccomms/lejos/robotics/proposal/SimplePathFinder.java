package lejos.robotics.proposal;

import java.util.ArrayList;
import java.util.Collection;

import lejos.geom.Point;
import lejos.robotics.Pose;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Very simple path finder that assumes there is a direct route to the target
 * 
 * @author Lawrie Griffiths
 *
 */
public class SimplePathFinder implements PathFinder {
	
	public Collection<WayPoint> findRoute(Pose start, Point destination)
			throws DestinationUnreachableException {
		ArrayList<WayPoint> route = new ArrayList<WayPoint>();
		route.add(new WayPoint(destination));
		return route;
	}

	public Collection<WayPoint> findRoute(Pose start, Pose destination)
			throws DestinationUnreachableException {
		ArrayList<WayPoint> route = new ArrayList<WayPoint>();
		route.add(new WayPoint(destination));
		return route;
	}
}
