package lejos.robotics.proposal;

import java.awt.geom.Point2D;

import lejos.geom.Point;
import lejos.robotics.*;
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
	
	private static final int PATHS = 4; // Currently doesn't calculate Pilot.backward() movement to destination point P2 to P3
	private static final int MOVES_PER_PATH = 2; // Currently doesn't try to end move with a heading included in pose. i.e. heading ambiguous
		
	// TODO: PoseController implementations should have an alternate default constructor that takes no PoseProvider. Creates default DeadReckoner by itself using the Pilot provided? 
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
			// 1. Get shortest moves
			Movement [] moves = getBestPath(poseProvider.getPose(), destination, pilot.getMinRadius());
			
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
	
	// TODO: Perhaps this method is better suited in SteerAlgorithms class?
	public static Movement [][] getAvailablePaths(Pose start, Point destination, float r) {
		
		Movement [] [] paths = new Movement [PATHS] [MOVES_PER_PATH];
		
		// TODO: Use Point instead of Point2D.Double? 
		Point2D.Double p1 = new Point2D.Double(start.getX(), start.getY());
		Point2D.Double p3 = new Point2D.Double(destination.getX(), destination.getY());
		
		for(int i = 0;i<PATHS;i++) { 
			float radius = r;
			if(i>=PATHS/2) radius = -r; // Do calculations for +ve radius then -ve radius
			
			// Find two arc angles:
			Point2D.Double c = SteerAlgorithms.findCircleCenter(p1, radius, start.getHeading());
			Point2D.Double p2 = SteerAlgorithms.findP2(c, p3, radius);
			double arcLengthForward = SteerAlgorithms.getArcLength(p1, p2, radius, start.getHeading(), true);
			double arcLengthBackward = SteerAlgorithms.getArcLength(p1, p2, radius, start.getHeading(), false);
			
			// Find straight line:
			double z = SteerAlgorithms.distCtoP3(c, p3);
			double p2p3 = SteerAlgorithms.distP2toP3(radius, z);
			
			paths[i][0] = new Movement(Movement.MovementType.ARC, false, (float)arcLengthForward, radius);
			paths[i][1] = new Movement(Movement.MovementType.TRAVEL, (float)p2p3, 0, false);
			i++;
			paths[i][0] = new Movement(Movement.MovementType.ARC, false, (float)arcLengthBackward, radius);
			paths[i][1] = new Movement(Movement.MovementType.TRAVEL, (float)p2p3, 0, false);
		}

		return paths;
	}
	
	public static Movement [] getBestPath(Pose start, Point destination, float radius) {
		// Get all paths
		Movement [][] paths = getAvailablePaths(start, destination, radius);
		Movement [] bestPath = null;
		
		// Now see which one has shortest travel distance:
		float minDistance = Float.POSITIVE_INFINITY;
		for(int i=0;i<PATHS;i++) {
			float dist = 0;
			for(int j=0;j<MOVES_PER_PATH;j++) {
				dist += Math.abs(paths[i][j].getDistanceTraveled());
			}
			if(dist < minDistance) {
				minDistance = dist;
				bestPath = paths[i];
			}
		}
		
		return bestPath;
	}
}
