package org.lejos.sample.steernavdemo;
import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.Sound;
import lejos.robotics.navigation.ArcMoveController;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.SteeringPilot;
import lejos.robotics.navigation.Waypoint;

/**
 * <p>This sample demonstrates a steering vehicle that executes turns by rotating the front wheels  
 * (such as a car or go-kart). The vehicle is capable of navigating to x, y coordinates (or waypoints which
 * contain a target heading. The target heading is the direction the vehicle will face when it arrives at a waypoint).</p>  
 * 
 * <p>This sample starts at 0,0 with a heading of 0 degrees (facing east). It drives to 50,50 with no specified heading.
 * Then it drives to -50, 50 with a heading of 270 (facing south). Then it drives back to the original position and ends.</p>
 * 
 *  <p>NOTE: If you don't have LEGO plans for a steering vehicle, you can simulate this kind of movement using a regular
 *  DifferentialPilot robot. Use the commented-out code below to test this type of vehicle.</p>
 *  
 * @author BB
 *
 */
public class SteerNavDemo {
	
	public static void main(String [] args) throws InterruptedException {
		
		// Make sure the parameters in this constructor match your vehicle.
		ArcMoveController p = new SteeringPilot(5.6f, Motor.B, Motor.C, 41, -33, 40);
		
		// Alternate code to simulate a steering vehicle of steering radius 40 cm:
		//ArcMoveController p = new DifferentialPilot(4.32F, 4.32F, 16F, Motor.B, Motor.C, false);
		//p.setMinRadius(40);
		
		Navigator c = new Navigator(p);
		
		// To retrieve the coordinates and heading, access the PoseProvider: 
		Pose pose = c.getPoseProvider().getPose();
		System.out.println("x=" + pose.getX() + " y=" + pose.getY() + " H=" + pose.getHeading());
		
		c.goTo(new Waypoint(0, 50, 0));
		c.waitForStop();
		Sound.beep();
		Thread.sleep(1000);
		
		pose = c.getPoseProvider().getPose();
		System.out.println("x=" + pose.getX() + " y=" + pose.getY() + " H=" + pose.getHeading());
		
		c.goTo(new Waypoint(50, 50, 90));
		c.waitForStop();
		Sound.beep();
		Thread.sleep(1000);
				
		pose = c.getPoseProvider().getPose();
		System.out.println("x=" + pose.getX() + " y=" + pose.getY() + " H=" + pose.getHeading());
		
		Waypoint dest = new Waypoint(-50, 50, 270);
		c.goTo(dest);
		c.waitForStop();
		Sound.beep();
		Thread.sleep(1000);
		
		pose = c.getPoseProvider().getPose();
		System.out.println("x=" + pose.getX() + " y=" + pose.getY() + " H=" + pose.getHeading());
		
		dest = new Waypoint(0, 0, 0);
		c.goTo(dest);
		c.waitForStop();
		Sound.beep();
		Thread.sleep(1000);
		
		pose = c.getPoseProvider().getPose();
		System.out.println("x=" + pose.getX() + " y=" + pose.getY() + " H=" + pose.getHeading());

		Button.ESCAPE.waitForPressAndRelease();
	}
}