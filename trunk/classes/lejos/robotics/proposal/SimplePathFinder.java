package lejos.robotics.proposal;

import java.util.ArrayList;
import java.util.Collection;

import lejos.geom.Point;
import lejos.robotics.Pose;

/**
 * Very simple path finder that assumes there is a direct route to the target
 * 
 * @author Lawrie Griffiths
 *
 */
public class SimplePathFinder extends ArrayList<WayPoint> implements PathFinder {

	public Collection<WayPoint> findRoute(Pose start, Point destination)
			throws DestinationUnreachableException {
		add(new WayPoint(destination));
		return this;
	}

	public Collection<WayPoint> findRoute(Pose start, Pose destination)
			throws DestinationUnreachableException {
		add(new WayPoint(destination));
		return this;
	}
}
