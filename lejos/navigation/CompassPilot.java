package lejos.navigation;

import lejos.nxt.*;
import lejos.nxt.addon.*;


/**
 * A Pilot that keeps track of direction using a CompassSensor.
 */
public class CompassPilot extends TachoPilot {

	protected CompassSensor compass;
	private  Regulator regulator = new Regulator(); // inner regulator for thread
	private int _heading; // Heading to point robot	
	private boolean _traveling = false; // state variable used by regulator
	private boolean _rotating = false; // state variable used by regulator
	private float _distance; // set by travel()  used by regulator to stop
	private byte _direction;// direction of travel = sign of _distance
    public int _angle0; // compass heading at last call to reset();
	
	/**
	 * returns true if robot is rotating to a specific direction
	 * @return true iff robot is rotating to a specific direction
	 */
	public boolean isRotating(){return _rotating;}
	
	/**
	 *returns returns  true if the robot is travelling for a specific distance;
	 **/	
	public boolean isTraveling(){ return _traveling;}
	
	/**
	 *  Allocates a CompasPilot object, and sets the physical parameters of the NXT robot. <br>
	 *  Assumes  Motor.forward() causes the robot to move forward);
	 *  @param compassPort the sensor port connected to the CompassSensor e.g. SensorPort.S1
	 *  @param wheelDiameter  Diameter of the tire, in any convenient units.  (The diameter in mm is usually printed on the tire). 
	 *  @param trackWidth Distance between center of right tire and center of left tire, in same units as wheelDiameter
	 *  @param leftMotor
	 * @param rightMotor
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
		_heading = getCompassHeading(); // Current compass direction = heading target
		regulator.setDaemon(true);
		regulator.start();
	}
    
    /**
     * return the compass 
     * @return the compass
     */	
    public CompassSensor getCompass(){ return compass;}
	/**
	 * Returns the compass angle in degrees, Cartesian (increasing counter clockwise) i.e. the actual robot heading
	 */
	public float getAngle() {
		return compass.getDegreesCartesian()- _angle0;
	}
	
	/**
	 * Returns  direction of desired robot facing
	 */
	public int getHeading() { return _heading;}
/**
 * returns current compass heading
 * @return
 */
    public int getCompassHeading()
    {
      int heading = Math.round(compass.getDegreesCartesian());
      if(heading>360)heading -= 360;
      if(heading<0)heading += 0;
      return heading;
    }

	/**
	 * sets  direction of desired robot facing in degrees
	 */
	public void setHeading(int angle){ _heading = angle;}
	/**
	 * Rotates the robot 360 degrees while calibrating the compass
     * resets compass zero to heading at end of calibration
	 */
	public synchronized void calibrate()
	{
		int spd =_motorSpeed;
		setSpeed(100);
		//regulateSpeed(true); BB
		compass.startCalibration();
		super.rotate(360,false);
        compass.stopCalibration();             
		setSpeed(spd);
	}
    public void resetCartesianZero()
    {
      compass.resetCartesianZero();
      _heading = 0;
      _angle0 = 0;
    }

	/**
	 * Determines the difference between actual compass direction and desired  heading in degrees  
	 * @return error (in degrees)
	 */
	private int getHeadingError()
	{
	   int  err = getCompassHeading() - _heading;
		// Handles the wrap-around problem:
		while (err < -180) err = err + 360;
		while (err > 180) err = err - 360;
		return err;
	}
	
	/**
	 * Moves the NXT robot a specific distance. A positive value moves it forwards and
	 * a negative value moves it backwards. The robot steers to maintain its compass heading.
	 * @param distance The positive or negative distance to move the robot, same units as _wheelDiameter
	 * @param immediateReturn iff true, the method returns immediately. 
	 */
	public void travel(float distance, boolean immediateReturn) {
		//setHeading(getCompassHeading());
		//regulateSpeed(false); BB
		reset();
		_distance = distance;
		if(_distance > 0)
		   {
		   _direction = 1;
		   forward();
		   }
		else {
		   _direction = -1;
		   backward();
		}
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
	public void rotateTo(float angle, boolean immediateReturn)
	{
		_heading = Math.round(angle);
		_traveling = false;
		//regulateSpeed(true); BB // accurate use of tacho count to regulate speed;
		_rotating = true;
		if(immediateReturn)return;
		while(_rotating) Thread.yield();
	}
	/**
	 * robot rotates to the specified compass heading;
	 * @param heading   Desired compass heading
	 */	
	public void rotateTo(float angle)
	{
		rotateTo(angle,false);
	}
	
	/** 
	 * robot rotates to the specified compass heading;
	 * @param  immediateReturn  - if true, method returns immediately. <br>
	 * Robot stops when specified angle is reached
	 */
	public void rotate(float angle, boolean immediateReturn)
	{
      rotateTo(angle+_heading,immediateReturn);
	}
	
	/**
	 * Rotates the  NXT robot through a specific angle; Rotates left if angle is positive, right if negative,
	 * Returns when angle is reached.
	 * Wheels turn in opposite directions producing a  zero radius turn.
	 * @param angle  degrees. Positive angle rotates to the left (clockwise); negative to the right. <br>Requires correct values for wheel diameter and track width.
	 */
	public void rotate(float angle)
	{
		rotate(angle,false);
	}

    public void reset()
    {
      super.reset();
      _angle0 = getCompassHeading();
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
		super.rotate(angle,false);
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
				   if(_direction*(getTravelDistance() - _distance) >=0)
					{
						stopNow();
						_traveling = false;
					}
					else
					{
			            float gain = -3;    
			            int error = (int)(gain* getHeadingError());
			            steer(_direction * error, 360*_direction,true);
			        }
				}
				if(_rotating && ! pilotIsMoving())
				{
					int error = getHeadingError();
					if(Math.abs(error) > 3) performRotation(-error);
					else 
					{
						_rotating = false;
						stopNow();
                        while(isMoving())Thread.yield();
					}
				}
				Thread.yield();
			}	
		}		
	}


}

