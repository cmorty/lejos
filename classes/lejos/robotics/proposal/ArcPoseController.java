package lejos.robotics.proposal;

import java.awt.geom.Point2D;

import lejos.geom.Point;
import lejos.nxt.Button;
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
 * @author BB
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

	public Pose goTo(Pose destination) {
		
		Pose pose = poseProvider.getPose();
	   	if (pilot instanceof RotateMoveController & pilot.getMinRadius() == 0) { // optimize for RotateMoveController
	   	   // TODO: What the heck happened to the rotateTo() method? Would make this so much easier.
	   		float turnAngle = pose.angleTo(destination.getLocation()) - pose.getHeading();
	   	    while (turnAngle < -180)turnAngle += 360;
	   	    while (turnAngle > 180) turnAngle -= 360;
	   	    ((RotateMoveController) pilot).rotate(turnAngle);
	   		pilot.travel(pose.distanceTo(destination.getLocation()));
	   		// TODO: What the heck happened to the rotateTo() method? Would make this so much easier.
	   		float turnAngle2 = destination.getHeading() - poseProvider.getPose().getHeading();
	   		while (turnAngle2 < -180)turnAngle += 360;
	   	    System.out.println("BUGSTOP"); // TODO Seems to stop it from freezing bug?
	   	    while (turnAngle2 > 180) turnAngle -= 360;
	   	    System.out.println("BUGSTOP"); // TODO Seems to stop it from freezing bug?
	   	    ((RotateMoveController) pilot).rotate(turnAngle2);
	   	} else {
		
			// 1. Get shortest path:
			Move [] moves = ArcAlgorithms.getBestPath(poseProvider.getPose(), pilot.getMinRadius(), destination, pilot.getMinRadius());
	
			// 2. Drive the path
			// TODO: A method to execute (drive) a Move might be nice here due to repetition of this code block below:
			for(int i=0;i<moves.length;i++) {
				pilot.travelArc(moves[i].getArcRadius(), moves[i].getDistanceTraveled());
			}
	   	}
		return poseProvider.getPose();
	}
	
	public Pose goTo(Point destination) {
		
		   	Pose pose = poseProvider.getPose();
		   	if (pilot instanceof RotateMoveController & pilot.getMinRadius() == 0) { // optimize for RotateMoveController
		   	    float turnAngle = pose.angleTo(destination) - pose.getHeading();
		   	    while (turnAngle < -180)turnAngle += 360;
		   	    while (turnAngle > 180) turnAngle -= 360;
		   		((RotateMoveController) pilot).rotate(turnAngle);
		   		pilot.travel(pose.distanceTo(destination));
		   	} else { 
   			// 1. Get shortest moves
    			Move [] moves = ArcAlgorithms.getBestPath(poseProvider.getPose(), destination, pilot.getMinRadius());
		   
		   		// 2. Drive the path
		   		for(int i=0;i<moves.length;i++) {
		   			pilot.travelArc(moves[i].getArcRadius(), moves[i].getDistanceTraveled());
		   		}
		   	}
		   	return poseProvider.getPose();

	}

	public Pose goTo(float x, float y) {
		return goTo(new Point(x, y));
	}
	
}
