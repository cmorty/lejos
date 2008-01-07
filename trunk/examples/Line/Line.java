import lejos.nxt.*;
import lejos.subsumption.*;
import lejos.navigation.*;

/**
 * Demonstration of use of the Behavior and Pilot classes to
 * implement a simple line following robot.
 * 
 * Requires a wheeled vehicle with two independently controlled
 * wheels with motors connected to motor ports A and C, and a light
 * sensor mounted forwards and pointing down, connected to sensor port 1.
 * 
 * Press ENTER to start the robot.
 * 
 * @author Lawrie Griffiths
 *
 */
public class Line {
	
	public static void main (String[] aArg)
	throws Exception
	{
		// Change last parameter of Pilot to specify on which 
		// direction you want to be "forward" for your vehicle.
		// The wheel and axle dimension psarameters should be
		// set for your robot, but are not critical.
		final Pilot pilot = new Pilot(5.6f,16.0f,Motor.A, Motor.C, true);
		final LightSensor light = new LightSensor(SensorPort.S1);
		
		Behavior DriveForward = new Behavior()
		{
			public boolean takeControl() {return light.readValue() <= 40;}
			
			public void suppress() {
				pilot.stop();
			}
			
			public void action() {
				pilot.forward();
			}					
		};
		
		Behavior OffLine = new Behavior()
		{
			private boolean suppress = false;
			
			public boolean takeControl() {return light.readValue() > 40;}

			public void suppress() {
				suppress = true;
				while (suppress) Thread.yield();
			}
			
			public void action() {
				int sweep = 10;
				while (!suppress) {
					pilot.rotate(sweep,true);
					while (!suppress && pilot.isMoving()) Thread.yield();
					sweep *= -2;
				}
				pilot.stop();
				suppress = false;
			}
		};

		//Wait for ENTER button to be pressed
		Button.ENTER.waitForPressAndRelease();

		Behavior[] bArray = {OffLine, DriveForward};		
	    (new Arbitrator(bArray)).start();
	}
}

