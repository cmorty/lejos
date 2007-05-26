package lejos.navigation;
import lejos.nxt.CompassSensor;
import lejos.nxt.Motor;
import lejos.nxt.LCD;

public class CompassPilot extends Pilot {
	
	private CompassSensor compass;
	public Regulator regulator = new Regulator(); // inner regulator for thread
	private int heading; // Heading to point robot
	private static final int THRESH = 10; // IMPT! Threshold value for rotate()
	
	public CompassPilot(CompassSensor cs, float wheelDiameter,float trackWidth,Motor leftMotor, Motor rightMotor) {
		this(cs, wheelDiameter, trackWidth, leftMotor, rightMotor, false);
	}
	
	public CompassPilot(CompassSensor cs, float wheelDiameter,float trackWidth,Motor leftMotor, Motor rightMotor, boolean reverse) {
		super(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
		this.compass = cs;
		heading = (int)compass.getDegreesCartesian(); // Current compass direction = heading target
		regulator.start();
		regulator.setDaemon(true);
	}
	
	public void rotate(int angle, boolean immediateReturn) {
		//lejos.nxt.Sound.buzz();
		/** 
		 * Just change the new heading and the regulate thread will handle the rest.
		 */
		synchronized(this) {
			// Using current heading, calculate heading to rotate to:
			heading = heading + angle;
			// Handle wraparound problem:
			if (heading >= 360) heading = heading - 360;
			if (heading < 0) heading = heading + 360;
			
			if(immediateReturn) return;
	  	}
		while(Math.abs(getHeadingError(heading)) > THRESH) Thread.yield();
	}
	
	/**
	 * Returns the angle from the compass.
	 */
	public int getAngle() {
		return (int)compass.getDegreesCartesian();
	}
	
	/**
	 * Determines the difference between actual compass direction and target heading in degrees 
	 * @param heading The target angle (in degrees). 
	 * @return error (in degrees)
	 */
	private int getHeadingError(int heading) {
		int err = getAngle() - heading;
	
		// Handles the wrap-around problem:
		if (err < -180) err = err + 360;
		if (err > 180) err = err - 360;
		return err;
	}
	
	public String CMPS = "Cmps"; // LCD DELETE ME
	public String HEADING = "Headng"; // LCD DELETE ME
	public String ERROR = "Error"; // LCD DELETE ME
	public String FREEMEM = "FREE"; // LCD DELETE ME
	
	/**
	 * Reason for this method is because Regulator can't access super.
	 * @param angle
	 */
	private void performRotation(int angle) {
		// DISPLAY CODE: ** REMOVE WHEN DONE
		
		LCD.drawString(CMPS, 0, 0);
		LCD.drawInt(getAngle(), 7, 0);
		LCD.drawString(HEADING, 0, 1);
		LCD.drawInt(heading, 7, 1);
		LCD.drawInt(angle, 7, 2);
		LCD.drawString(ERROR, 0, 2);
		LCD.drawString(FREEMEM, 0, 4);
		LCD.drawInt((int)System.getRuntime().freeMemory(), 5, 4);
		LCD.refresh();
		LCD.clear();
		//lejos.nxt.Sound.beep();
		//try{Thread.sleep(1000);}catch(Exception e){}
		
		if(angle != 0) super.rotate(angle, false);
	}
	
	private class Regulator extends Thread {
		public void run() {
			while(true) {
				synchronized(this) {
					int error = getHeadingError(heading);
					performRotation(error);
				}
				Thread.yield();
			}
			
		}
	}
}