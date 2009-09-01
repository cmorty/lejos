package lejos.robotics.proposal;

import lejos.geom.Point;
import lejos.robotics.Pose;
import lejos.robotics.localization.PoseProvider;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * This class directs a pilot from the current known coordinates to a destination set of coordinates.
 * The ArcPoseController is not capable of avoiding objects or planning a route. It can only drive in a straight
 * line that is not obstructed.  
 * 
 */
public class ArcPoseController implements PoseController {
	private ArcPilot pilot;
	private PoseProvider poseProvider;
	
	public ArcPoseController(ArcPilot pilot, PoseProvider poseProvider) {
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
		    float turnAngle = pose.angleTo(destination) - pose.getHeading();
		    while (turnAngle < -180)turnAngle += 360;
		    while (turnAngle > 180) turnAngle -= 360;
			((RotatePilot) pilot).rotate(turnAngle);
			pilot.travel(pose.distanceTo(destination));			
		} else {
			//TODO: Calculate arc of minimum radius needed to point to the
			// destination and the do an arc and a travel.
		}
		return poseProvider.getPose();
	}

	public Pose goTo(float x, float y) {
		return goTo(new Point(x, y));
	}
}
