package lejos.navigation;

import lejos.nxt.CompassSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Motor;
import lejos.nxt.LCD;



public class CompassPilot extends Pilot {
	
	public CompassSensor compass;
	public Regulator regulator = new Regulator(); // inner regulator for thread
	private int heading; // Heading to point robot
	
	private boolean _traveling = false; // state variable used by regulator
	private boolean _rotating = false; // state variable used by regulator
	private int _distance; // set by travel()  used by regulator to stop
	
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
	
	
	/**
	 * Returns the angle from the compass in degrees, Cartesian (increasing counter clockwise)
	 */
	public int getAngle() {
		return (int)compass.getDegreesCartesian();
	}
	
	/**
	 * Returns target direction of robot facing
	 */
	public int getHeading() { return heading;}
	
	/**
	 * sets target direction of robot facing in degrees
	 */
	public void setHeading(int heading){ this.heading = heading;}
	
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
 *  see method   travel(distance);
 * @param immediateReturn  if TRUE, method returns immediately; robot stops after moving specified distance
 */	
	public void travel(int distance, boolean immediateReturn)
	{
		regulateSpeed(false);
		resetTachoCount();
		forward();
		_distance = distance;
		_traveling = true;
		if(immediateReturn)return;
		while(_traveling)Thread.yield();
	}
	
 /**
 * Moves the NXT robot a specific distance;<br>
 * A positive distance causes forward motion;  negative distance  moves backward.  
 * Robot steers to maintain its compass heading;
 * @param  distance of robot movement. Unit of measure for distance must be same as wheelDiameter and trackWidth
 **/
	public void travel(int distance)
	{ 
		this.travel(distance,false);
	}
/**
 * robot rotates to the specified compass heading;
 * @param heading   Desired compass heading
 * @param immediateReturn  if TRUE, method returns immediately; robot stops facing in specified direction
 */		
	public void rotateTo(int heading, boolean immediateReturn)
	{	
		this.heading = heading;
		_traveling = false;
		regulateSpeed(true); // accurate use of tacho count to regulate speed;
		_rotating = true;
		if(immediateReturn)return;
		while(_rotating) Thread.yield();
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
	 * @parameter  immediateReturn  - if true, method returns immediately. <br>
	 * Robot stops when specified angle is reached
	 */
	public void rotate(int angle, boolean immediateReturn) 
	{
		rotateTo(heading + angle,immediateReturn);	
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
	
	private void performRotation(int angle)
	{ 
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
					int error = (int) getHeadingError(heading);
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
			int error = (int)(gain* getHeadingError(heading));
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

