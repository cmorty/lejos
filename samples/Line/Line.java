import lejos.nxt.*;
import lejos.robotics.subsumption.*;
import lejos.robotics.navigation.*;

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
		// The wheel and axle dimension parameters should be
		// set for your robot, but are not critical.
		final Pilot pilot = new TachoPilot(5.6f,16.0f,Motor.A, Motor.C, false);
		final LightSensor light = new LightSensor(SensorPort.S1);
        pilot.setTurnSpeed(180);
        /**
         * this behavior wants to take contrtol when the light sensor sees the line
         */
		Behavior DriveForward = new Behavior()
		{
			public boolean takeControl() {return light.readValue() <= 40;}
			
			public void suppress() {
				pilot.stop();
			}
			public void action() {
				pilot.forward();
                while(light.readValue() <= 40) Thread.yield(); //action complete when not on line
			}					
		};
		
		Behavior OffLine = new Behavior()
		{
			private boolean suppress = false;
			
			public boolean takeControl() {return light.readValue() > 40;}

			public void suppress() {
				suppress = true;
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

		Behavior[] bArray = {OffLine, DriveForward};
        LCD.drawString("Line ", 0, 1);
        Button.waitForPress();
	    (new Arbitrator(bArray)).start();
	}
}

