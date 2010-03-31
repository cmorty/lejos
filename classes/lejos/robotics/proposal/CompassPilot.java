package lejos.robotics.proposal;

import lejos.robotics.navigation.*;
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
	protected  Regulator regulator = new Regulator(); // inner regulator for thread
	protected float _heading; // Heading to point robot
	protected boolean _traveling = false; // state variable used by regulator
	protected float _distance; // set by travel()  used by regulator to stop
	protected byte _direction;// direction of travel = sign of _distance
	protected float  _heading0 = 0;// heading when rotate immediate is called
	
	
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
	public CompassPilot(DirectionFinder compass,  float wheelDiameter,
                float trackWidth,TachoMotor leftMotor, TachoMotor rightMotor)
        {
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
	public CompassPilot(DirectionFinder compass,  float wheelDiameter,
                float trackWidth,TachoMotor leftMotor, TachoMotor rightMotor, boolean reverse) {
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
	 * Returns the change in robot heading since the last call of reset()
     * normalized to be within -180 and _180 degrees
	 */
	public float getAngle()
    {
      return normalize(getCompassHeading()-_heading0);
	}
	
	/**
	 * Returns  direction of desired robot facing
	 */
	public float getHeading() { return _heading;}
/**
 * Method returns the current compass heading
 * @return Compass heading in degrees.
 */
    public float getCompassHeading()
    {
      return normalize(compass.getDegreesCartesian());
    }

	/**
	 * sets  direction of desired robot facing in degrees
	 */
	public void setHeading(float angle){ _heading = angle;}
	/**
	 * Rotates the robot 360 degrees while calibrating the compass
     * resets compass zero to heading at end of calibration
	 */
	public synchronized void calibrate()
	{
		int spd =_motorSpeed;
		setRotateSpeed(50);
		compass.startCalibration();
		super.rotate(360,false);
        compass.stopCalibration();             
		setTravelSpeed(spd);
	}
    public void resetCartesianZero()
    {
      compass.resetCartesianZero();
      _heading =compass.getDegreesCartesian();
    }

	/**
	 * Determines the difference between actual compass direction and desired  heading in degrees  
	 * @return error (in degrees)
	 */
	private float getHeadingError()
	{

	   float   err = compass.getDegreesCartesian() - _heading;
		// Handles the wrap-around problem:
       return normalize(err);

	}
	
	/**
	 * Moves the NXT robot a specific distance. A positive value moves it forwards and
	 * a negative value moves it backwards. The robot steers to maintain its compass heading.
	 * @param distance The positive or negative distance to move the robot, same units as _wheelDiameter
	 * @param immediateReturn iff true, the method returns immediately. 
	 */
	public void travel(float distance, boolean immediateReturn) {
     _heading = getCompassHeading();
		_distance = distance + getTravelDistance();
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
	 * @param  immediateReturn  - if true, method returns immediately.
	 * Robot stops when specified angle is reached or when stop() is called
	 */
	public void rotate(float angle, boolean immediateReturn)
	 {
    _heading = getCompassHeading();
    super.rotate(angle, immediateReturn);
    if (immediateReturn)
    {
      return;
    }
    _heading += angle;
    _traveling = false;


    float error = getHeadingError();
    while (Math.abs(error) > 3)
    {
      super.rotate(-error, false);
      error = getHeadingError();
    }
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
      _heading0 = getCompassHeading();
      super.reset();
    }

 // methods required to give regulator access to Pilot superclass
	protected void stopNow(){stop();}
/**
 * Stops the robot soon after the method is executed. (It takes time for the motors
 * to slow to a halt)
 */
	public void stop()
    {
      super.stop();
      _traveling = false;
      while(isMoving())
      {
        super.stop();
        Thread.yield();
      }
    }
	private boolean pilotIsMoving() { return super.isMoving();}
	
	protected float normalize(float angle)
	{ 
      while(angle > 180)angle -= 360;
      while(angle < -180) angle += 360;
      return angle;
    }
/**
 * inner class to regulate rotation and travel to get direction control from compass instead of motor tacho.
 * @author Roger Glassey
 */	
	 class Regulator extends Thread 
	 {

    public void run()
    {
      while (true)
      {
        while (!_traveling)
        {
          Thread.yield();
        }
        {
          if (_direction * (getTravelDistance() - _distance) >= 0)
          {
            stopNow();
          } else
          {
            float gain = -3;
            float error = getHeadingError();
            steer(gain * error);
          }
        }
        Thread.yield();
      }
    }
  }
}

