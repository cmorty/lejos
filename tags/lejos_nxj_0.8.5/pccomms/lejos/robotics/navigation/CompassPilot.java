package lejos.robotics.navigation;

import lejos.robotics.*;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * A Pilot that keeps track of direction using a CompassSensor.
 */
public class CompassPilot extends TachoPilot {

	protected DirectionFinder compass;
	private  Regulator regulator = new Regulator(); // inner regulator for thread
	private int _heading; // Heading to point robot	
	private boolean _traveling = false; // state variable used by regulator
	private boolean _rotating = false; // state variable used by regulator
    private boolean _regulating = false;
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
	 * Allocates a CompasPilot object, and sets the physical parameters of the NXT robot. <br>
	 *  Assumes  Motor.forward() causes the robot to move forward);
	 * Parameters 
	 * @param compass :  a compass sensor;
	 * @param wheelDiameter Diameter of the tire, in any convenient units.  (The diameter in mm is usually printed on the tire). 
	 * @param trackWidth Distance between center of right tire and center of left tire, in same units as wheelDiameter
	 * @param leftMotor
	 * @param rightMotor
	 */
	public CompassPilot(DirectionFinder compass,  float wheelDiameter,float trackWidth,TachoMotor leftMotor, TachoMotor rightMotor) {
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
	public CompassPilot(DirectionFinder compass,  float wheelDiameter,float trackWidth,TachoMotor leftMotor, TachoMotor rightMotor, boolean reverse) {
		super(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
		this.compass = compass;
		_heading = getCompassHeading(); // Current compass direction = heading target
		regulator.setDaemon(true);
		regulator.start();
	}
    
    /**
     * Return the compass 
     * @return the compass
     */	
    public DirectionFinder getCompass(){ return compass;}
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
 * Method returns the current compass heading
 * @return Compass heading in degrees.
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
        _rotating = false;
        _regulating = true;
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
 *
 * @param angle   Desired compass heading
 * @param immediateReturn  if TRUE, method returns immediately;
 * Unfortunately, if you issue the stop(() command, the motion will run to
     * completion.robot stops facing in specified direction
 */		
	public void rotateTo(float angle, boolean immediateReturn)
	{
	     rotate(angle - _heading, immediateReturn);
	}
	/**
	 * Robot rotates to the specified compass heading.
	 * @param angle  Desired compass heading
	 */	
	public void rotateTo(float angle)
	{
		rotateTo(angle,false);
	}
	
	/** 
	 * robot rotates to the specified compass heading;
	 * @param  immediateReturn  - if true, method returns immediately.
     * Unfortunately, if you issue the stop(() command, the motion will run to
     * completion. <br>
	 * Robot stops when specified angle is reached
	 */
	public void rotate(float angle, boolean immediateReturn)
	{
      reset();
    performRotation(angle);
    _traveling = false;
    _rotating = true;
    _regulating = true;
    _heading += angle;
    if (immediateReturn)
    {
      return;
    }
    while (_rotating)
    {
      Thread.yield();
    }
    stop();
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
/**
 * Stops the robot soon after the method is executed. (It takes time for the motors
 * to slow to a halt)
 */
	public void stop()
    {
      super.stop();
      _regulating = false;
      _traveling = false;
      _rotating = false;
      while(isMoving())
      {
        super.stop();
        Thread.yield();
      }
    }
	private boolean pilotIsMoving() { return super.isMoving();}
	
	private void performRotation(float angle) // usd by regulator to call pilot rotate(angle, true)
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
              while(!_regulating)Thread.yield();
				if( _traveling)
				{
				   if(_direction*(getTravelDistance() - _distance) >=0)
					{
						stopNow();
					}
					else
					{
			            float gain = -3;    
			            int error = (int)(gain* getHeadingError());
			            steer(_direction * error, 360*_direction,true);
			        }
				}
				if(_rotating )
				{
					int error = getHeadingError();
					if(Math.abs(error) > 3) performRotation(-error);
					else 
					{
						stopNow();
					}
				}
				Thread.yield();
			}	
		}		
	}
}

