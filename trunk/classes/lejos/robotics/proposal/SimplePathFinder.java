package lejos.robotics.proposal;

import lejos.geom.Point;
import lejos.robotics.Pose;

/**
 * This class directs a pilot from the current known coordinates to a destination set of coordinates.
 * The SimplePathPlanner is not capable of avoiding objects or planning a route. It can only drive in a straight
 * line that is not obstructed.  
 * 
 *
 */
public class SimplePathFinder implements PathFinder {

	public SimplePathFinder(Pilot pilot, PoseProvider poseProvider) {
		
	}
	
	public Pilot getPilot() {
		// TODO Auto-generated method stub
		return null;
	}

	public PoseProvider getPoseProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPoseProvider(PoseProvider replacement) {
		// TODO Auto-generated method stub

	}

	public Pose goTo(Point destination) {
		// TODO Auto-generated method stub
		return null;
	}
}
