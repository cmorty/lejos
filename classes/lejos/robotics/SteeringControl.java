package lejos.robotics;
import lejos.nxt.Motor;

/**
 * The SteeringControl class contains methods to control NXT robot movents: travel forward or backward in a straight line or a circular path or rotate to a new direction.  <br>
 * Note: this class will only work with two independently controlled drive motors to steer differentially, so it can rotate within its own footprint (i.e. turn on one spot).<br>
 * It can be used with robots that have reversed motor design: the robot moves in the direction opposite to the the dirction of motor rotation.
 * Uses the Motor class, which regulates motor speed using the NXT motor's built in tachometer. <br>
 * Many methods return immediately.  
 * Resets tacho count every time a movement command is issued. 
 **/
 
public class SteeringControl 
{
/**
 *left motor
 */
	public Motor _left;
/**
 * right motor
 */
	public Motor _right;
/**
 *motor degrees per unit of travel
 */	
	public final float _degPerDistance; 
/**
 * Motor revolutions for 360 degree rotation of robot (motors running in opposite directions
 **/
	public final float _turnRatio; //Motor revolutions for robot complete revolution
/** 
 * motor speed  degrees per second
 */
	private int _speed;
/**
 *  Motor rotation forward makes robot move forward iff parity == 1.
 */ 
	private byte _parity = 1; 
/**
 * distance between wheels - used in steer();
 */
	public final float _trackWidth;
	public final float _wheelDiameter;

/**
 *  Allocates a SteeringControl object, and sets the physical parameters of the NXT robot. <br>
 *  Assumes  Motor.forward() causes the robot to move forward);
 *  @param wheelDiameter.  Diameter of the tire, in any convenient units.  (The diameter in mm is usually printed on the tire). 
 *  @param trackWidth Distance between center of right tire and center of left tire, in same units as wheelDiameter
*/
	public SteeringControl(Motor leftMotor, Motor rightMotor,float wheelDiameter,float trackWidth)
	{
		_left = leftMotor;
		_right = rightMotor;
		_degPerDistance = 360/(3.14158f*wheelDiameter);
		_turnRatio = trackWidth/wheelDiameter;
		_left.regulateSpeed(true);
		_right.regulateSpeed(true);
		_trackWidth = trackWidth;
		_wheelDiameter = wheelDiameter;
	}
/**
 *  Allocates a SteeringControl object, and sets the physical parameters of the NXT robot. <br>
 *  @param wheelDiameter.  Diameter of the tire, in any convenient units.  (The diameter in mm is usually printed on the tire). 
 *  @param trackWidth Distance between center of right tire and center of left tire, in the same units as wheelDiameter
 *  @param reverse : if true, the NXT robot moves forward when the motors are running backward.
 */
	public SteeringControl(Motor leftMotor, Motor rightMotor,float wheelDiameter,float trackWidth, boolean reverse)
	{
		this(leftMotor,rightMotor,wheelDiameter, trackWidth);
		if(reverse) _parity = -1;
		else _parity = 1;
	}
/**
 *returns tachoCount of left motor; Positive value means motor has moved the robot forward;
 */
	public int getLeftCount(){ return _parity*_left.getTachoCount();}

/**
 *returns tachoCount of the right motor; Positive value means motor has moved the robot forward;
 */
	public int getRightCount(){ return _parity*_right.getTachoCount();}

	public float getTurnRatio(){ return _turnRatio;}
/**
 *  Moves the NXT robot forward until stop() is called.
 */
	public void forward() 
	{	
		setSpeed(_speed);
		if(_parity == 1) fwd();
		else bak();
	}


/**
 * Moves the NXT robot backward until stop() is called.
 */
	public void backward() 
	{
		setSpeed(_speed);
		if(_parity == 1)bak();
		else fwd();
	}

/**
 * Rotates the  NXT robot through a specific angle; Rotates left if angle is positive, right if negative; Returns immediately.
 * Wheels turn in opposite directions producing a  zero radius turn.
 * @parameter angle,  degrees. Positive angle rotates to the left (clockwise); negative to the right. <br>Requires correct values for wheel diameter and track width.
 */
	public void rotate(int angle)
	{
		setSpeed(_speed);
		resetTachoCount();
		int ta = _parity*(int)( angle*_turnRatio);
		_left.rotate(-ta);
		_right.rotate(ta);
	}
/**
 * Rotates the  NXT robot through a specific angle; Rotates left if angle is positive, right if negative;
 * Returns when angle is reached if waitForCompletion is true.
 * Wheels turn in opposite directions producing a  zero radius turn.
 * @parameter angle,  degrees. Positive angle rotates to the left; negative to the right. <br>Requires correct values for wheel diameter and track width.
 * @parameter  waitForCompletion :if false, this method returns immediately; otherwise angle is reached 
 */

	public void rotate(int angle, boolean waitForCompletion )
	{
		setSpeed(_speed);
		rotate(angle);
		if(waitForCompletion)while(isMoving())Thread.yield();
	}
/**
 *Stops the NXT robot
 */
	public void stop()
	{
		_left.stop();
		_right.stop();
	}
/**
 *returns true iff the NXT robot is moving
 **/
    	public boolean isMoving()
	{
		return _left.isMoving()||_right.isMoving()||_left.isRotating()||_right.isRotating();
	}
/**
 *resets tacho count for both motors
**/
  public void resetTachoCount()
  {
  	_left.resetTachoCount();
  	_right.resetTachoCount();
  }

/**
 *returns distance taveled since last reset of tacho count
 **/ 
 	public float getTravelDistance()
 	{
		int avg =( _left.getTachoCount()+_right.getTachoCount())/2;
 		return  _parity*avg/_degPerDistance;
 	}
  
  /**
 * Moves the NXT robot a specific distance; This method returns immediately.<br>
 * A positive distance causes forward motion;  negative distance  moves backward.  
 * @param  distance - of robot movement. Unit of measure for distance must be same as wheelDiameter and trackWidth
 **/
	public void travel(float distance)
	{
		setSpeed(_speed);
		resetTachoCount();
		_left.rotate((int)(_parity*distance*_degPerDistance));
		_right.rotate((int)(_parity*distance*_degPerDistance));

	}
/**
 * Moves the NXT robot a specific distance; if waitForCompletion is true, returns when distance is reached.<br>
 * A positive distance causes forward motion; negative distance moves backward.  
 * @param waitForCompletion: determines if method returns only after travel is complete.  If false, method returns immediately.
 */
	public void travel(float distance,boolean waitForCompletion)
	{
		travel(distance);
		if(waitForCompletion)while(isMoving())Thread.yield();
	}
/**
 *Sets speed of both motors,  degrees/sec; also sets retulate speed true 
 */
	public void setSpeed(int speed) 
	{
		_speed = speed;
		_left.regulateSpeed(true);
		_right.regulateSpeed(true);
		_left.setSpeed(speed);
		_right.setSpeed(speed);
	}
/**
 * Moves the NXT robot in a circular path at a specific turn rate. <br>
 * The center of the turning circle is on the right side of the robot iff parameter turnRate is negative;  <br>
 *     turnRate values are between -200 and +200;
 * Postcondition:  motor speed is NOT restored to previous value;
 * @param turnRate If positive, the left wheel is on the inside of the turn.  If negative, the left wheel is on the outside.
 * This parameter determines the ratio of inner wheel speed to outer wheel speed (as a percent). <br>
 * <I>Formula:</i>    ratio  =  100 - abs(turnRate). When the ratio is negative, the outer and inner wheels rotate in opposite directions.<br>
 * Examples: <UL><LI> steer(25)-> inner wheel turns at 75% of the speed of the outer wheel <br>
 *           <li>steer(100) -> inner wheel stops.<br>
 * 			 <li>steer(200) -> means that the inner wheel turns at the same speed as the outer wheel - a zero radius turn. <br></UL>
 * To calculate the turn radius:  radius =  0.5*trackWidth*(100+ratio)(100 - ratio);<br> When ratio = 0, inside wheel is stopped and turn radius 
 * is trackWidth/2.  
 */
	public void steer(int turnRate)
	{
		steer(turnRate,Integer.MAX_VALUE);
	}
/**
 * Moves the NXT robot in a circular path, and stops when the direction it is facing has changed by a specific angle;  <br>
 * This method returns immediately.
 * The center of the turning circle is on right side of the robot iff parameter turnRate is negative  <br>
 * turnRate values are between -200 and +200;
 * If angle is negative, robot will move travel backwards.
 * See details of steer( int).
 * @param  turnRate  see steer( turnRate) <br>
 * @param angle  the angle through which the robot will rotate and then stop. If negative, robot traces the turning circle backwards. 
 */
	public void steer(int turnRate, int angle)
	{
		Motor inside;
		Motor outside;
		int rate = turnRate;
		if(rate <- 200)rate = -200;
		if(rate > 200)rate = 200;
		if(rate==0)
		{
			if(angle<0)backward();
			else forward();
			return;
		}
		if (turnRate<0)
		{
			inside = _right;
			outside = _left;
			rate = -rate;
		} 
		else
		{
			inside = _left;
			outside = _right;
		}
		outside.setSpeed(_speed);
		float steerRatio = 1 - rate/100.0f;
		inside.setSpeed((int)(_speed*steerRatio));
		float rotAngle  = angle*_trackWidth*2/(_wheelDiameter*(1-steerRatio));
		if(angle == Integer.MAX_VALUE) rotAngle = Integer.MAX_VALUE; // turn rate == 0
		resetTachoCount();
		inside.rotate(_parity*(int)(rotAngle*steerRatio));
		outside.rotate(_parity*(int)rotAngle);

	}
/**
 * Moves the NXT robot in a circular path through a specific angle; If waitForCompletion is true, returns when angle is reached. <br>
 * Negative turnRate means center of turning circle is on right side of the robot; <br>
 * Range of turnRate values : -200 : 200 ; 
 * Robot will stop when total rotation equals angle. If angle is negative, robot will move travel backwards.
 * see also details of steer(int) and steer(int,int);
 * @param turnRate see steer( turnRate)
 * @param angle  see steer(turnRage, angle)
 * @param waitForCompletion  If true,  this method returs when angle is reached.  Otherwise it returns immidiately. 
 */

	public void steer(int turnRate, int angle, boolean waitForCompletion)
	{
		steer(turnRate,angle);
		if(waitForCompletion)while (isMoving())Thread.yield();
	}
/**
 *motors backward
 */
	private void bak() 
	{
		resetTachoCount();
		_left.backward();
		_right.backward();
	}

/**
 * motors forward
 */
	 private void fwd()
	 {
	 	resetTachoCount();
	 	_left.forward();
		_right.forward();
	 }
}
