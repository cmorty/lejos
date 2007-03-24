package lejos.navigation;
import lejos.nxt.Motor;

 
/**
* The Pilot class is a software abstraction of the Pilot mechanism of a NXT robot. It contains methods to control robot movents: travel forward or backward in a straight line or a circular path or rotate to a new direction.  <br>
* Note: this class will only work with two independently controlled Pilot motors to steer differentially, so it can rotate within its own footprint (i.e. turn on one spot).<br>
* It can be used with robots that have reversed motor design: the robot moves in the direction opposite to the the dirction of motor rotation.
* Uses the Motor class, which regulates motor speed using the NXT motor's built in tachometer. <br>
* Some methods optionally return immediately so the thread that called the method can monitor sensors and call stop() if necessary.  <br>  
* Uses the smoothAcceleration  property of Motors to improve motor symchronication
*  Example:<p>
* <code><pre>
*	Pilot sc = new Pilot(2.1f,4.4f,Motor.A, Motor.C,true);
*        sc.setSpeed(720);// 2 RPM
*	sc.travel(12);
*	sc.rotate(-90);
*	sc.travel(-12,true);
*	while(sc.isMoving())Thread.yield();
*	sc.rotate(-90);
*	sc.rotateTo(270);
*	sc.steer(-50,180,true);
*	while(sc.isMoving())Thread.yield();
*	sc.steer(100);
*	try{Thread.sleep(1000);}
*   catch(InterruptedException e){}
*	sc.stop();
* </pre></code>
 **/
 
public class Pilot 
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
	 * motor degrees per unit of travel
	 */	
	public final float _degPerDistance;
	
	/**
	 * Motor revolutions for 360 degree rotation of robot (motors running in opposite directions.
	 * calculated from wheel diameter and track width.  Used by rotate() and steer() methods
	 **/
	private final float _turnRatio; 
	
	/** 
	 * motor speed  degrees per second. Used by all methods that cause movememt
	 */
	private int _speed = 360;
	
	/**
	 * Motor rotation forward makes robot move forward iff parity == 1.
	 */ 
	private byte _parity = 1;
	
	/**
	 * if true, motor speed regulation is turned on
	 */
	 private boolean _regulating = true;
	 
	
	/**
	 * distance between wheels - used in steer() 
	 */
	public  final float _trackWidth;
	
	/**
	 *	diameter of tires
	 */
	public final float _wheelDiameter;

	/**
	 *  Allocates a Pilot object, and sets the physical parameters of the NXT robot. <br>
	 *  Assumes  Motor.forward() causes the robot to move forward);
	 *  @param wheelDiameter  Diameter of the tire, in any convenient units.  (The diameter in mm is usually printed on the tire). 
	 *  @param trackWidth Distance between center of right tire and center of left tire, in same units as wheelDiameter
	 */
	public Pilot(float wheelDiameter,float trackWidth,Motor leftMotor, Motor rightMotor)
	{
		_left = leftMotor;
		_right = rightMotor;
		_degPerDistance = 360/((float)Math.PI*wheelDiameter);
		_turnRatio = trackWidth/wheelDiameter;
		_left.regulateSpeed(true);
		_left.smoothAcceleration(true);
		_right.regulateSpeed(true);
		_right.smoothAcceleration(true);
		_trackWidth = trackWidth;
		_wheelDiameter = wheelDiameter;
	}
	
	/**
	 *  Allocates a Pilot object, and sets the physical parameters of the NXT robot. <br>
	 *  @param wheelDiameter  Diameter of the tire, in any convenient units.  (The diameter in mm is usually printed on the tire). 
	 *  @param trackWidth Distance between center of right tire and center of left tire, in the same units as wheelDiameter
	 *  @param reverse if true, the NXT robot moves forward when the motors are running backward.
	 */
	public Pilot(float wheelDiameter,float trackWidth,Motor leftMotor, Motor rightMotor, boolean reverse)
	{
		this(wheelDiameter, trackWidth,leftMotor,rightMotor);
		if(reverse) _parity = -1;
		else _parity = 1;
	}
	
	/**
	 *	returns tachoCount of left motor; Positive value means motor has moved the robot forward;
	 */
	public int getLeftCount(){ return _parity*_left.getTachoCount();}

	/**
	 *returns tachoCount of the right motor; Positive value means motor has moved the robot forward;
	 */
	public int getRightCount(){ return _parity*_right.getTachoCount();}
	
	/**
	 *returns actual speed of left motor in degrees per second; a negative value if motor is rotating backwards  <br>
	 * Updated avery 100 ms.
	 **/
	public int getLeftActualSpeed(){ return _left.getActualSpeed();}
	
	/**
 	 *returns actual speed of right motor in deg/sec;  a negative value if motor is rotating backwards. <br>
 	 *  Updated avery 100 ms.
 	 **/	
	public int getRightActualSpeed() { return _right.getActualSpeed();}

	/**
	 * return ratatio of Motor revolutions per 360 degree rotation of the robot
	 */
	public float getTurnRatio(){ return _turnRatio;}
	
	/**
	 * Sets speed of both motors,  degrees/sec; also sets retulate speed true 
	 */
	public void setSpeed(int speed) 
	{
		_speed = speed;
		_left.regulateSpeed(_regulating);
		_left.smoothAcceleration(true);
		_right.regulateSpeed(_regulating);
		_right.smoothAcceleration(true);
		_left.setSpeed(speed);
		_right.setSpeed(speed);
	}
	
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
	 * Rotates the  NXT robot through a specific angle; Rotates left if angle is positive, right if negative;
	 * Returns immediately  iff immediateReturn is true.
	 * Wheels turn in opposite directions producing a  zero radius turn.
	 * @param angle  degrees. Positive angle rotates to the left; negative to the right. <br>Requires correct values for wheel diameter and track width.
	 * @param immediateReturn if true this method returns immediately 
	 */
	public void rotate(int angle, boolean immediateReturn )
	{
		setSpeed(_speed);
		int ta = _parity*(int)( angle*_turnRatio);
		_left.rotate(-ta,true);
		_right.rotate(ta,true);
		if(immediateReturn)return;
		while(isMoving())Thread.yield();
	}
	
	/**
	 * returns the angle of rotation of the robot since last call to reset of tacho count;
	 */
	public int getAngle()
	{
		return  _parity*Math.round((getRightCount()-getLeftCount())/(2*_turnRatio));
	}
	
	/**
	 * Stops the NXT robot
	 */
	public void stop()
	{
		_left.stop();
		_right.stop();
	}
	
	/**
	 * returns true iff the NXT robot is moving
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
     * returns distance taveled since last reset of tacho count
     **/ 
    public float getTravelDistance()
  	{
		int avg =( _left.getTachoCount()+_right.getTachoCount())/2;
 		return  _parity*avg/_degPerDistance;
 	}
  
    /**
     * Moves the NXT robot a specific distance;<br>
     * A positive distance causes forward motion;  negative distance  moves backward.  
     * @param  distance of robot movement. Unit of measure for distance must be same as wheelDiameter and trackWidth
     **/
	public void travel(float distance)
	{
		travel(distance,false);
	}
	
	/**
	 * Moves the NXT robot a specific distance; if immediateReturn is true, method returns immediately. <br>
	 * A positive distance causes forward motion; negative distance moves backward.  
	 * @param immediateReturn  If true, method returns immediately, and robot stops after traveling the distance.  If false, method returns immediately.
	 */
	public void travel(float distance,boolean immediateReturn)
	{
		setSpeed(_speed);
		_left.rotate((int)(_parity*distance*_degPerDistance),true);
		_right.rotate((int)(_parity*distance*_degPerDistance),true);
		if(immediateReturn)return;
		while(isMoving())Thread.yield();
	}

	/**
	 * Moves the NXT robot in a circular path at a specific turn rate. <br>
	 * The center of the turning circle is on the right side of the robot iff parameter turnRate is negative;  <br>
	 * turnRate values are between -200 and +200;
	 * @param turnRate If positive, the left wheel is on the inside of the turn.  If negative, the left wheel is on the outside.
	 * This parameter determines the ratio of inner wheel speed to outer wheel speed (as a percent). <br>
	 * <I>Formula:</i>    ratio  =  100 - abs(turnRate). When the ratio is negative, the outer and inner wheels rotate in opposite directions.<br>
	 * Examples: <UL><LI> steer(25)-> inner wheel turns at 75% of the speed of the outer wheel <br>
	 *           <li>steer(100) -> inner wheel stops.<br>
	 * 			 <li>steer(200) -> means that the inner wheel turns at the same speed as the outer wheel - a zero radius turn. <br></UL>
 	 */
	public void steer(int turnRate)
	{
		steer(turnRate,Integer.MAX_VALUE,true);
	}

	/**
	 * Moves the NXT robot in a circular path through a specific angle; <br>
	 * Negative turnRate means center of turning circle is on right side of the robot; <br>
 	* Range of turnRate values : -200 : 200 ; 
 	* Robot will stop when total rotation equals angle. If angle is negative, robot will move travel backwards.
 	* @param turnRate If positive, the left wheel is on the inside of the turn.  If negative, the left wheel is on the outside.
 	* This parameter determines the ratio of inner wheel speed to outer wheel speed (as a percent). <br>
 	* @param angle  the angle through which the robot will rotate and then stop. If negative, robot traces the turning circle backwards. 
 	*/
	public void steer(int turnRate, int angle)
	{
		steer(turnRate,angle,false);
	}

	/**
	 * Moves the NXT robot in a circular path, and stops when the direction it is facing has changed by a specific angle;  <br>
	 * Returns immediately if immediateReturn is true.  The robot will stop automatically when the turm is complete. 
	 * The center of the turning circle is on right side of the robot iff parameter turnRate is negative  <br>
	 * turnRate values are between -200 and +200;
	 * @param turnRate If positive, the left wheel is on the inside of the turn.  If negative, the left wheel is on the outside.
	 * This parameter determines the ratio of inner wheel speed to outer wheel speed (as a percent). <br>
	 * @param angle  the angle through which the robot will rotate and then stop. If negative, robot traces the turning circle backwards. 
	 * @param immediateReturn iff true, method returns immedediately.
	 *
	 */
	public void steer(int turnRate, int angle, boolean immediateReturn)
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
		if(angle == Integer.MAX_VALUE) //no limit angle for turn
		{
			if(_parity == 1) outside.forward();
			else outside.backward();
			if( _parity*steerRatio > 0) inside.forward();
			else inside.backward();
			return;
		}
		float rotAngle  = angle*_trackWidth*2/(_wheelDiameter*(1-steerRatio));
//		if(angle == Integer.MAX_VALUE) rotAngle = Integer.MAX_VALUE/2; // turn rate == 0
		inside.rotate(_parity*(int)(rotAngle*steerRatio),true);
		outside.rotate(_parity*(int)rotAngle,true);
		if(immediateReturn)return;
		while (isMoving())Thread.yield();
		inside.setSpeed(outside.getSpeed());
	}

	/**
	 * motors backward  called by forward() and backward()
	 */
	private void bak() 
	{
		_left.backward();
		_right.backward();
	}
/**
 *Sets motor speed regulation   on = true (default) or off = false; <br>
 *Allows steer() method to be called by (for example)
 *a line tracker) so direction control is from sensor inputs 
 */	
	public void regulateSpeed(boolean yes)
	{
		_regulating = yes;
		 _left.regulateSpeed(yes);
		_right.regulateSpeed(yes);
	}

	/**
	 * motors forward called by forward() and backward()
	 */
	 private void fwd()
	 {
	 	_left.forward();
		_right.forward();
	 }

}