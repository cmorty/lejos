package lejos.navigation;
import lejos.navigation.*;
import lejos.nxt.*;

/**
 * A Pilot that keeps track of direction using a CompassSensor.
 */
public class CompassPilot extends Pilot {
	
	protected CompassSensor compass;
	private  Regulator regulator = new Regulator(); // inner regulator for thread
	private int _heading; // Heading to point robot
	
	private boolean _traveling = false; // state variable used by regulator
	private boolean _rotating = false; // state variable used by regulator
	private float _distance; // set by travel()  used by regulator to stop
	
	/**
	 * returns true if robot is rotating to a specific direction
	 * @return true iff robot is rotating to a specific direction
	 */
	public boolean isRotating(){return _rotating;}
	
	/**
	 *returns returns if the robot is travelling for a specific distance;
	 **/	
	public boolean isTraveling(){ return _traveling;}
	
	/**
	 *  Allocates a CompasPilot object, and sets the physical parameters of the NXT robot. <br>
	 *  Assumes  Motor.forward() causes the robot to move forward);
	 *  @param compassPort the sensor port connected to the CompassSensor e.g. SensorPort.S1
	 *  @param wheelDiameter  Diameter of the tire, in any convenient units.  (The diameter in mm is usually printed on the tire). 
	 *  @param trackWidth Distance between center of right tire and center of left tire, in same units as wheelDiameter
	 */
	public CompassPilot(SensorPort compassPort, float wheelDiameter,float trackWidth,Motor leftMotor, Motor rightMotor) {
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
	public CompassPilot(SensorPort compassPort, float wheelDiameter,float trackWidth,Motor leftMotor, Motor rightMotor, boolean reverse) 
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
	public CompassPilot(CompassSensor compass,  float wheelDiameter,float trackWidth,Motor leftMotor, Motor rightMotor) {
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
	public CompassPilot(CompassSensor compass,  float wheelDiameter,float trackWidth,Motor leftMotor, Motor rightMotor, boolean reverse) {
		super(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
		this.compass = compass;
		_heading = (int)compass.getDegreesCartesian(); // Current compass direction = heading target
		regulator.setDaemon(true);
		regulator.start();
	}
    
    /**
     * return the compass 
     * @return the compass
     */	
    public CompassSensor getCompass(){ return compass;}
	/**
	 * Returns the compass angle in degrees, Cartesian (increasing counter clockwise)
	 */
	public int getAngle() {
		return (int)compass.getDegreesCartesian();
	}
	
	/**
	 * Returns target direction of robot facing
	 */
	public int getHeading() { return _heading;}

	/**
	 * sets target direction of robot facing in degrees
	 */
	public void setHeading(int angle){ _heading = angle;}
	
	public void calibrate()
	{
		int spd = _speed;
		setSpeed(100);
		regulateSpeed(true);
		compass.startCalibration();
		super.rotate(360,false);
//		while(isMoving()) LCD.drawInt(super.getAngle(),4,0,0);
		compass.stopCalibration();
		setSpeed(spd);
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
	
	/**
	 * Moves the NXT robot a specific distance. A positive value moves it forwards and
	 * a negative value moves it backwards.
	 * If immediateReturn is fale, this method calls updateXY(). 
	 * If immediateReturn is true, method returns immidiately and your code MUST call updateXY()
	 * after the robot stops and before the  robot moves again.  Otherwise, the robot position is lost. 
	 * @param distance The positive or negative distance to move the robot, same units as _wheelDiameter
	 * @param immediateReturn iff true, the method returns immediately. 
	 */
	public void travel(float distance, boolean immediateReturn)
	{
		regulateSpeed(false);
		resetTachoCount();
		forward();
		_distance = distance;
		_traveling = true;
		if(immediateReturn)return;
		while(_traveling)Thread.yield(); // regulator will call stop when distance is reached
	}
	
 /**
 * Moves the NXT robot a specific distance;<br>
 * A positive distance causes forward motion;  negative distance  moves backward.  
 * Robot steers to maintain its compass heading;
 * @param  distance of robot movement. Unit of measure for distance must be same as wheelDiameter and trackWidth
 **/
	public void travel(float distance)
	{ 
		travel(distance,false);
	}
/**
 * robot rotates to the specified compass heading;
 * @param angle   Desired compass heading
 * @param immediateReturn  if TRUE, method returns immediately; robot stops facing in specified direction
 */		
	public void rotateTo(int angle, boolean immediateReturn)
	{	
		_heading = angle;
		_traveling = false;
		regulateSpeed(true); // accurate use of tacho count to regulate speed;
		_rotating = true;
		if(immediateReturn)return;
		while(_rotating) Thread.yield();
		_heading = (int) compass.getDegreesCartesian();
	}
	/**
	 * robot rotates to the specified compass heading;
	 * @param heading   Desired compass heading
	 */	
	public void rotateTo(int heading)
	{
		rotateTo(heading,false);
	}
	
	/** 
	 * see rotate(angle)
	 * @param  immediateReturn  - if true, method returns immediately. <br>
	 * Robot stops when specified angle is reached
	 */
	public void rotate(int angle, boolean immediateReturn) 
	{
		super.rotate(angle,immediateReturn);
		if(immediateReturn)return;
		while(isMoving())Thread.yield();
		rotateTo(_heading + angle);
	}

	/**
	 * Rotates the  NXT robot through a specific angle; Rotates left if angle is positive, right if negative,
	 * Returns when angle is reached.
	 * Wheels turn in opposite directions producing a  zero radius turn.
	 * @param angle  degrees. Positive angle rotates to the left (clockwise); negative to the right. <br>Requires correct values for wheel diameter and track width.
	 */
	public void rotate(int angle) 
	{
		rotate(angle,false);
	}
	
/**
 *  returns TRUE if robot is moving 
 */	
	public boolean isMoving()
	{
		return super.isMoving()  || _rotating || _traveling;		
	}
 // methods required to give regulator access to Pilot superclass
	private void stopNow(){stop();} 
	
	private boolean pilotIsMoving() { return super.isMoving();}
	
	private void performRotation(int angle) // usd by regulator to call pilot rotate(angle, true)
	{ 
		if(angle > 180) angle = angle -  360;
		if(angle < -180) angle = angle +360;
		if(angle>5) angle -= 3;
		if(angle < -5)angle += 3;  // attempt to correct overshoot
		super.rotate(angle,true);
	} 
/**
 * inner class to regulate rotation and travel to get direction control from compass instead of motor tacho.
 * @author Roger Glassey
 */	
	 class Regulator extends Thread 
	{
		public void run() 
		{
			while(true) 
			{
				if(pilotIsMoving()&& _traveling)
				{
					if(getTravelDistance() >= _distance)
					{
						stopNow();
						_traveling = false;
					}
					else
					controlTravel();
				}
				if(_rotating && ! pilotIsMoving())
				{
					int error = (int) getHeadingError(_heading);
					if(Math.abs(error) > 3) performRotation(-error);
					else 
					{
						_rotating = false;
						stopNow();
					}
				}
				Thread.yield();
			}	
		}
		private void controlTravel() 
		{
			float gain = 2;
			int slowSpeed;		
			int error = (int)(gain* getHeadingError(_heading));
			if(error<0)// turn right
			{
				error = -error;
				if(error>100)error = 100;
				slowSpeed = _speed*(100-error)/100;// use error as speed ratio
				_left.setSpeed(slowSpeed);
				_right.setSpeed(_speed);
			}
			else // turn left
			{
				if(error>100)error = 100;
				slowSpeed = _speed*(100-error)/100;
				_right.setSpeed(slowSpeed);	
				_left.setSpeed(_speed);
			}
		}
	}

}

