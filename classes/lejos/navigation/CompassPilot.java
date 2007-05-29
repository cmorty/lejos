package lejos.navigation;
import lejos.nxt.LCD; // DELETE
import lejos.nxt.CompassSensor;
import lejos.nxt.Motor;

public class CompassPilot extends Pilot {

	CompassSensor compass;
	Regulator regulator;
	int heading;
	
	boolean isTravelling = false;
	
	public CompassPilot(CompassSensor cs, float wheelDiameter,float trackWidth,Motor leftMotor, Motor rightMotor) {
		this(cs, wheelDiameter, trackWidth, leftMotor, rightMotor, false);
	}
	
	public CompassPilot(CompassSensor cs, float wheelDiameter,float trackWidth,Motor leftMotor, Motor rightMotor, boolean reverse) {
		super(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
		this.compass = cs;
		heading = (int)compass.getDegreesCartesian();
		regulator = new Regulator();
		regulator.setDaemon(true);
		regulator.start();
	}
	
	// !! IMMEDIATE RETURN! Need delay using THRESH.
	public void rotate(int angle, boolean immediateReturn) {
		// ALTERNATE to sync! Store value temporarily, then assign when correct
		synchronized(this) {
			heading += angle;
			// Handle wraparound problem:
			if (heading >= 360) heading = heading - 360;
			if (heading < 0) heading = heading + 360;
		}
	}
	
	/**
	 * Needed this because Regulator can't call Pilot methods.
	 * @param angle
	 */
	private void actual_rotate(int angle) {
		super.rotate(angle, false);
	}
	
	public void travel(float distance,boolean immediateReturn) {
		isTravelling = true;
		super.travel(distance, immediateReturn);
		isTravelling = false; // !! Need to switch this even if immediate return
	}
	
	public String COUNT = "Iter"; // LCD DELETE ME
	public String HEADING = "Headng"; // LCD DELETE ME
	public String DIR = "Dir"; // LCD DELETE ME
	public String ERROR = "Error"; // LCD DELETE ME
	public String FREEMEM = "FREE"; // LCD DELETE ME
	
	class Regulator extends Thread {
		public void run() {
			int count = 0;
			while(true) {
				int error = getHeadingError(heading);
				LCD.clear();
				LCD.drawString(DIR, 0, 0);
				LCD.drawInt((int)compass.getDegreesCartesian(), 7, 0);
				LCD.drawString(HEADING, 0, 1);
				LCD.drawInt(heading, 7, 1);
				LCD.drawInt(error, 7, 2);
				LCD.drawString(ERROR, 0, 2);
				LCD.drawInt(count++, 7, 3);
				LCD.drawString(COUNT, 0, 3);
				LCD.refresh();
				if(!isTravelling)
					if(error != 0)
						actual_rotate(-error);
				Thread.yield();
			}
		}
	}
	
	/**
	 * Determines the difference between actual compass direction and target heading in degrees 
	 * @param heading The target angle (in degrees). 
	 * @return error (in degrees)
	 */
	private int getHeadingError(int heading) {
		int err = (int)compass.getDegreesCartesian() - heading;
	
		// Handles the wrap-around problem:
		if (err < -180) err = err + 360;
		if (err > 180) err = err - 360;
		return err;
	}	
}