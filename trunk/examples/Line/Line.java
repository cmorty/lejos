import lejos.nxt.*;
import lejos.subsumption.*;
import lejos.navigation.*;

public class Line {
	
	public static void main (String[] aArg)
	throws Exception
	{
		final Pilot pilot = new Pilot(5.5f,11.2f,Motor.A, Motor.C);
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

		Button.ENTER.waitForPressAndRelease();

		Behavior[] bArray = {OffLine, DriveForward};		
	    (new Arbitrator(bArray)).start();
	}
}

