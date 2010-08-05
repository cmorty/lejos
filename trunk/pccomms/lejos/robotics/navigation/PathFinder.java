package lejos.robotics.navigation;

import java.util.Collection;
import lejos.robotics.Pose;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * 
 * This class creates a set of waypoints connected by straight lines that lead from one location to another without
 * colliding with mapped geometry. 
 *
 */
public interface PathFinder {
	Collection <WayPoint> findRoute(Pose start, WayPoint destination) throws DestinationUnreachableException;
}
