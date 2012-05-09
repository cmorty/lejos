package org.lejos.sample.waypointnav;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Waypoint;
import lejos.util.PilotProps;

/**
 * The WaypointNav class uses the Navigator to execute the
 * individual segments
 *
 * You can run the PilotParams sample to create a property file which 
 * sets the parameters of the Pilot to the dimensions
 * and motor connections for your robot.
 * 
 * @author Dirk Sturzebecher - 20090131 - initial version
 * revised 20090701 to use instead of extend  a SimpleNavigator.
 * revised 20110428 to use Navigator by BB. 
 */
public class WaypointNav
{
	Navigator nav;

	/**
	 * test of WaypointNav
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		PilotProps pp = new PilotProps();
		pp.loadPersistentValues();
		float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "4.32"));
		float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "16.35"));
		RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
		RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
		boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"true"));

		System.out.println("Any button to start");
		Button.waitForAnyPress();
		
		DifferentialPilot p = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
		Navigator nav = new Navigator(p);

		nav.addWaypoint(new Waypoint(20, 20));
		nav.addWaypoint(new Waypoint(20,0));
		nav.addWaypoint(new Waypoint(0,20));
		nav.addWaypoint(new Waypoint(0,0));
		nav.followPath();
		System.out.println("Any button to halt");
		Button.waitForAnyPress();
	}
}
