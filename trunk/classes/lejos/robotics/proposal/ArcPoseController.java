package lejos.robotics.proposal;

import java.awt.geom.Point2D;

import lejos.geom.Point;
import lejos.robotics.Move;
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
	private ArcMoveController pilot;
	private PoseProvider poseProvider;
	
		
	public ArcPoseController(ArcMoveController pilot, PoseProvider poseProvider) {
		this.pilot = pilot;
		this.poseProvider = poseProvider;
	}
	
	/**
	 * Alternate constructor that takes no PoseProvider. Creates default DeadReckoner internally using the Pilot provided. 
	
	 * @param pilot
	 */
	public ArcPoseController(ArcMoveController pilot) {
		this(pilot, new DeadReckonerPoseProvider(pilot));
	}
	
	public ArcMoveController getPilot() {
		return pilot;
	}

	public PoseProvider getPoseProvider() {
		return poseProvider;
	}

	public void setPoseProvider(PoseProvider replacement) {
		poseProvider = replacement;
	}

	// TODO: I think goTo(Pose) should be part of PoseController interface?
	public Pose goTo(Pose destination) {
		
		// 1. Get shortest path:
		Move [] moves = ArcAlgorithms.getBestPath(poseProvider.getPose(), pilot.getMinRadius(), destination, pilot.getMinRadius());

		// 2. Drive the path
		for(int i=0;i<moves.length;i++) {
			pilot.travelArc(moves[i].getArcRadius(), moves[i].getDistanceTraveled());
		}
		
		return poseProvider.getPose();
	}
	
	public Pose goTo(Point destination) {
		/* TODO Commented Lawrie's optimized code in order to test arcs with DifferentialPilot
		Pose pose = poseProvider.getPose();
		if (pilot instanceof RotateMoveController) { // optimize for RotateMoveController
		    float turnAngle = pose.angleTo(destination) - pose.getHeading();
		    while (turnAngle < -180)turnAngle += 360;
		    while (turnAngle > 180) turnAngle -= 360;
			((RotateMoveController) pilot).rotate(turnAngle);
			pilot.travel(pose.distanceTo(destination));			
		} else { */
			// 1. Get shortest moves
			Move [] moves = ArcAlgorithms.getBestPath(poseProvider.getPose(), destination, pilot.getMinRadius());
			
			// 2. Drive the path
			for(int i=0;i<moves.length;i++) {
				// TODO: I don't think DifferentialPilot is programmed to work with travelArc() properly yet (infinity etc)
				pilot.travelArc(moves[i].getArcRadius(), moves[i].getDistanceTraveled());
			}
		//} TODO Commented Lawrie's optimized code in order to test arcs with DifferentialPilot
		return poseProvider.getPose();
	}

	public Pose goTo(float x, float y) {
		return goTo(new Point(x, y));
	}
	
}
