package lejos.robotics.proposal;

import lejos.geom.Point;
import lejos.robotics.Pose;

/**
 * This class directs a pilot from the current known coordinates to a destination set of coordinates.
 * The SimplePathPlanner is not capable of avoiding objects or planning a route. It can only drive in a straight
 * line that is not obstructed.  
 * 
 */
public class SimplePathFinder implements PathFinder {
	private ArcPilot pilot;
	private PoseProvider poseProvider;
	
	public SimplePathFinder(ArcPilot pilot, PoseProvider poseProvider) {
		this.pilot = pilot;
		this.poseProvider = poseProvider;
	}
	
	public ArcPilot getPilot() {
		return pilot;
	}

	public PoseProvider getPoseProvider() {
		return poseProvider;
	}

	public void setPoseProvider(PoseProvider replacement) {
		poseProvider = replacement;
	}

	public Pose goTo(Point destination) {
		Pose pose = poseProvider.getPose();
		if (pilot instanceof RotatePilot) { // optimize for RotatePilot
			((RotatePilot) pilot).rotate(pose.angleTo(destination) - pose.getHeading());
			pilot.travel(pose.distanceTo(destination));			
		} else {
			//TODO: Calculate arc of minimum radius needed to point to the
			// destination and the do an arc and a travel.
		}
		return poseProvider.getPose();
	}
}
