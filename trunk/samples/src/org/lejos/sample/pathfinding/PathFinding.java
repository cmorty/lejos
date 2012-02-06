package org.lejos.sample.pathfinding;
import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.AstarSearchAlgorithm;
import lejos.robotics.pathfinding.FourWayGridMesh;
import lejos.robotics.pathfinding.NodePathFinder;
import lejos.robotics.pathfinding.PathFinder;
import lejos.util.PilotProps;

/**
 * This sample uses the A* search algorithm to find a path from one location to another. The code demonstrates 
 * how to create a trivial LineMap, feed it to a GridMesh, and then use a pathfinder
 * to control the robot and allow it to navigate around map obstacles. You will need to construct a simple
 * pilot robot to use this class. No sensors are required.
 * 
 * @author BB
 *
 */
public class PathFinding {

	public static void main(String[] args) throws Exception {
		
		PilotProps pp = new PilotProps();
		pp.loadPersistentValues();
		float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "4.32"));
		float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "16.35"));
		RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
		RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
		boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));

		DifferentialPilot robot = new DifferentialPilot(wheelDiameter,trackWidth,leftMotor,rightMotor,reverse);
		
		// Create a rudimentary map:
		Line [] lines = new Line[3];
		lines [0] = new Line(75f, 100f, 100f, 100f);
		lines [1] = new Line(100, 100, 87, 75);
		lines [2] = new Line(87, 75, 75, 100);
		lejos.geom.Rectangle bounds = new Rectangle(-50, -50, 250, 250);
		LineMap myMap = new LineMap(lines, bounds);
		
		// Use a regular grid of node points. Grid space = 20. Clearance = 15:
		FourWayGridMesh grid = new FourWayGridMesh(myMap, 20, 15);
		
		// Use A* search:
		AstarSearchAlgorithm alg = new AstarSearchAlgorithm();
		
		// Give the A* search alg and grid to the PathFinder:
		PathFinder pf = new NodePathFinder(alg, grid);
		
		PoseProvider posep = new OdometryPoseProvider(robot);
		
		Navigator nav = new Navigator(robot, posep) ;
		System.out.println("Planning path...");
		nav.followPath(pf.findRoute(posep.getPose(), new Waypoint(90, 140)));
		nav.waitForStop();
	}		
}
