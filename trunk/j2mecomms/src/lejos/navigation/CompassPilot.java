package lejos.navigation;

import lejos.nxt.*;
import lejos.nxt.addon.*;

/**
 * The iCommand CompassPilot is simpler than the NXJ CompassPilot.
 * This version rotates using the compass but does not move
 * forward/backward with compass assistance.
 *  
 * @author BB
 *
 */
public class CompassPilot extends Pilot {
	
	protected CompassSensor compass;
	private int _heading; 
	private final int THRESH = 8; // Quit correction when rotates to within THRESH degrees
	//private Regulator regulator = new Regulator(); // inner regulator for thread
	
	/**
	 *  Allocates a CompasPilot object, and sets the physical parameters of the NXT robot. <br>
	 *  Assumes  Motor.forward() causes the robot to move forward);
	 *  @param compassPort the sensor port connected to the CompassSensor e.g. SensorPort.S1
	 *  @param wheelDiameter  Diameter of the tire, in any convenient units.  (The diameter in mm is usually printed on the tire). 
	 *  @param trackWidth Distance between center of right tire and center of left tire, in same units as wheelDiameter
	 */
	public CompassPilot(SensorPort compassPort, double wheelDiameter,double trackWidth,Motor leftMotor, Motor rightMotor) {
		this(compassPort, wheelDiameter, trackWidth, leftMotor, rightMotor, false);
	}
		
/**
 * Allocates a CompasPilot object, and sets the physical parameters of the NXT robot. <br>
 *  Assumes  Motor.forward() causes the robot to move forward);
 * Parameters 
 * @param compassPort :  the compass sensor is connected to this port;
 * @param wheelDiameter Diameter of the tire, in any convenient units.  (The diameter in mm is usually printed on the tire). 
 * @param trackWidth Distance between center of right tire and center of left tire, in same units as wheelDiameter
 * @param leftMotor
 * @param rightMotor
 * @param reverse  if true of motor.forward() drives the robot backwards
 */
	public CompassPilot(SensorPort compassPort, double wheelDiameter,double trackWidth,Motor leftMotor, Motor rightMotor, boolean reverse) 
	{	
		this(new CompassSensor(compassPort), wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);		
	}
	
	/**
	 * Allocates a CompasPilot object, and sets the physical parameters of the NXT robot. <br>
	 *  Assumes  Motor.forward() causes the robot to move forward);
	 * Parameters 
	 * @param compass :  a compass sensor;
	 * @param wheelDiameter Diameter of the tire, in any convenient units.  (The diameter in mm is usually printed on the tire). 
	 * @param trackWidth Distance between center of right tire and center of left tire, in same units as wheelDiameter
	 * @param leftMotor
	 * @param rightMotor
	 */
	public CompassPilot(CompassSensor compass,  double wheelDiameter,double trackWidth,Motor leftMotor, Motor rightMotor) {
		this(compass, wheelDiameter, trackWidth, leftMotor, rightMotor, false);
	}
	
	/**
	 * Allocates a CompasPilot object, and sets the physical parameters of the NXT robot. <br>
	 *  Assumes  Motor.forward() causes the robot to move forward);
	 * Parameters 
	 * @param compass :  a compass sensor;
	 * @param wheelDiameter Diameter of the tire, in any convenient units.  (The diameter in mm is usually printed on the tire). 
	 * @param trackWidth Distance between center of right tire and center of left tire, in same units as wheelDiameter
	 * @param leftMotor
	 * @param rightMotor
	 * @param reverse if true of motor.forward() drives the robot backwards
	 */
	public CompassPilot(CompassSensor compass,  double wheelDiameter,double trackWidth,Motor leftMotor, Motor rightMotor, boolean reverse) {
		super(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
		this.compass = compass;
		_heading = (int)compass.getDegreesCartesian(); // Current compass direction = heading target
		//regulator.setDaemon(true);
		//regulator.start();
	}
	
	/**
	 * Slowly rotates twice while in calibration mode.
	 *
	 */
	public void calibrate()	{
		int spd = _speed;
		setSpeed(180);
		compass.startCalibration();
		super.rotate(720);
		compass.stopCalibration();
		setSpeed(spd);
	}
	
	/**
	 * robot rotates to the specified compass heading;
	 * @param angle   Desired compass heading
	 * @param immediateReturn  if TRUE, method returns immediately; robot stops facing in specified direction
	 */		
		public void rotateTo(int angle, boolean immediateReturn)
		{	
			_heading = angle;
			rotate(0, immediateReturn);
		}
		/**
		 * robot rotates to the specified compass heading;
		 * @param heading   Desired compass heading
		 */	
		public void rotateTo(int heading)
		{
			rotateTo(heading,false);
		}

	
	public int getAngle() {
		return (int)compass.getDegreesCartesian();
	}
	
	/**
	 * Determines the difference between actual compass direction and target heading in degrees 
	 * @param heading The target angle (in degrees). 
	 * @return error (in degrees)
	 */
	private int getError(int heading) {
		int err = getAngle() - heading;
	
		// Handles the wrap-around problem:
		if (err < -180) err = err + 360;
		if (err > 180) err = err - 360;
		System.out.println("Target: " + _heading + "  Actual: " + getAngle() + "   Error: " + err);
		return err;
	}
	
	/**
	 * Inner regulator thread can't call super without this helper method.
	 * @param error
	 */
	private void correctError(int error) {
		System.out.println("Rotating " + (-error) + "...");
		super.rotate(-error, false);
	}
		
	/**
	 * The rotate method takes a current reading from the compass
	 * then rotates the desired number of degrees.
	 * NOTE: immediateReturn is not functional currently (always
	 * waits until done rotating).
	 */
	public void rotate(int angle, boolean immediateReturn) {
		System.out.println("Old heading: " + _heading);
		synchronized(this) {
			_heading += angle;
			if(_heading >= 360) _heading -= 360;
			if(_heading < 0) _heading += 360;
		}
		System.out.println("New heading: " + _heading);
		
		int error;
		do {
			error = getError(_heading);
			correctError(error);
		} while(Math.abs(error) > THRESH);
		
		/*
		if(immediateReturn) return;
		while(Math.abs(getError(_heading)) > THRESH) {
			Thread.yield();
			System.out.println("YIELDED");
		}*/
	}	
}