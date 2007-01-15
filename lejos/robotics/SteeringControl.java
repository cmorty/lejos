
import lejos.nxt.LCD;
import lejos.nxt.Button;

/**
 * The SteeringControl class contains methods to control NXT robot movents: travel forward or backward in a straight line or a circular path or rotate to a new direction.  <br>
 * Note: this class will only work with two independently controlled drive motors to steer differentially, so it can rotate within its own footprint (i.e. turn on one spot).<br>
 * Uses Motor class, which regulates motor speed using the NXT motor's built in tachometer. <br>
 * Many methods return immediately.  
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
	private float _degPerDistance; 
/**
 * Motor revolutions for 360 degree rotation of robot
 **/
	private float _turnRatio; //Motor revolutions for robot complete revolution
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
	private float _trackWidth;




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
 *  Moves the NXT robot forward until stop() is called.
 */
	public void forward() 
	{
		if(_parity == 1) fwd();
		else bak();
	}


/**
 * Moves the NXT robot backward until stop() is called.
 */
	public void backward() 
	{
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
 * Moves the NXT robot a specific distance; This method returns immediately.<br>
 * A positive distance causes forward motion;  negative distance  moves backward.  
 * @param  distance - of robot movement. Unit of measure for distance must be same as wheelDiameter and trackWidth
 **/
	public void travel(float distance)
	{
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
 *Sets speed of both motors,  degrees/sec
 */
	public void setSpeed(int speed) 
	{
		_speed = speed;
		_left.setSpeed(speed);
		_right.setSpeed(speed);
	}
/**
 * Moves the NXT robot in a circular path at a specific turn rate. <br>
 * The center of the turning circle is on the right side of the robot iff parameter turnRate is negative;  <br>
 *     turnRate values are between -200 and +200;
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
		int ratio =(100 - rate);
		inside.setSpeed(_speed*ratio/100);
		float rotAngle  = _degPerDistance*_trackWidth*(3.1416f*angle/360)*2/(1-ratio/100f);
		if(angle == Integer.MAX_VALUE) rotAngle = Integer.MAX_VALUE; // turn rate == 0
		inside.rotate(_parity*(int)rotAngle*ratio/100);
		outside.rotate(_parity*(int)rotAngle);
	}
/**
 * Moves the NXT robot in a circular path through a specific angle; If waitForCompletion is true, returns when angle is reached. <br>
 * Negative turnRate means center of turning circle is on right side of the robot; <br>
 * Range of turnRate values : -200 : 200 ; 
 * Robot will stop when total rotation equals angle. If angle is negative, robot will move travel backwards.
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
		_left.backward();
		_right.backward();
	}

/**
 * motors forward
 */
	 private void fwd()
	 {
	 	_left.forward();
		_right.forward(); 
	 }

	public static void main(String[] args ) 
	{

		Start.press("Steering Control");
		SteeringControl robot = new SteeringControl(Motor.A, Motor.C,2.1f,4.4f,true);
		LCD.drawInt((int)(10*robot._trackWidth),0,4);
		LCD.refresh();
		robot.setSpeed(400);
//		for(int i = 0; i<4; i++)
//		{
//			robot.rotate(90,true);
//			Tools.pause(100);
//		}
//		LCD.drawInt(robot._left.getTachoCount(),0,6);
//		LCD.drawInt(robot._right.getTachoCount(),5,6);
//				for(int i = 0; i<4; i++)
//		{
//			robot.rotate(-90,true);
//			Tools.pause(100);
//		}
//		LCD.drawInt(robot._left.getTachoCount(),0,6);
//		LCD.drawInt(robot._right.getTachoCount(),5,6);
//		LCD.refresh();

		robot.travel(12,true);
		robot.travel(-5,true);
//		robot.forward();
//		Tools.pause(1000);
//		robot.backward();
//		Tools.pause(1000);
		robot.travel(12);
		while(robot.isMoving())Thread.yield();
		robot.travel(-12);
		while(robot.isMoving())Thread.yield();
//		robot.setSpeed(500);
//		robot.rotate(90);
//		while(robot.isMoving())Thread.yield();
//		robot.rotate(-90);
//		while(robot.isMoving())Thread.yield();
//		while(robot.isMoving())Thread.yield();
//		robot.steer(-50,180);
//		while(robot.isMoving())Thread.yield();
//		robot.steer(-50,-180);
//		while(robot.isMoving())Thread.yield();
//		robot.steer(50,180);
//		while(robot.isMoving())Thread.yield();
//		robot.steer(50, -180);
//		while(robot.isMoving())Thread.yield();	
//		robot.steer(100);
//		Tools.pause(500);
//		robot.stop();
//		robot.steer(0);
//		Tools.pause(1000);
//		robot.stop();
//		Tools.pause(500);
//		LCD.drawInt(robot._left.getTachoCount(),0,7);
//		LCD.drawInt(robot._right.getTachoCount(),5,7);
//		LCD.refresh();
		while(Button.readButtons()==0)Thread.yield();
//		robot.shutDown();
	}
		
}
