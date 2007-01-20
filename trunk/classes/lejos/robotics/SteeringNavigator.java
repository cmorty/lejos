package lejos.robotics;
import lejos.nxt.Motor;


import lejos.nxt.LCD;
/**
* The SteeringNavigator class can keep track of the robot position and the direction angle it faces, It uses a SteeringControl object to control NXT robot movements.<br>
* The position and direction angle values are updated automatically when the movement command returns after the movement is complete and and after stop() command is issued.
* However, some commands return immediately (to allow for sensor monitoring in the main thread).  It is then the programmers responsibilit to 
* call updatePosition() when the robot motion is completed. 
*       
*/

public class SteeringNavigator { // implements

	// orientation and co-ordinate data
	private float _heading = 0;
	private float _x = 0;
	private float _y = 0;
	// The essential component
	public SteeringControl sc;
/**
 * set false whenever the robot moves,  set to true by updatePosition();
 */
	private boolean updated = false;

/**
* Allocates a RotationNavigator object and initializes it with the proper motors.
* The x and y values will each equal 0 and the starting
* angle is 0 degrees, so if the first move is forward() the robot will run along
* the x axis. <BR>
* @param wheelDiameter The diameter of the wheel, usually printed right on the
* wheel, in centimeters (e.g. 49.6 mm = 4.96 cm) 
* @param trackWidth The distance from the center of the left tire to the center
* of the right tire, in units of your choice
* @param rightMotor The motor used to drive the right wheel e.g. Motor.C.
* @param leftMotor The motor used to drive the left wheel e.g. Motor.A.
*/
	
	public SteeringNavigator(float wheelDiameter, float trackWidth, Motor leftMotor, Motor rightMotor) 
	{
		sc = new SteeringControl(leftMotor, rightMotor,wheelDiameter,trackWidth);
	}
	   public SteeringNavigator(float wheelDiameter, float trackWidth, Motor leftMotor, Motor rightMotor, boolean reverse) 
	{
		sc = new SteeringControl(leftMotor, rightMotor,wheelDiameter,trackWidth,reverse);
	}
	/**
	* Overloaded SteeringNavigator constructor that assumes the following:<BR>
	* Left motor = Motor.A   Right motor = Motor.C <BR>
	* @param wheelDiameter The diameter of the wheel, usually printed right on the
	* wheel, in centimeters (e.g. 49.6 mm = 4.96 cm)  
	* @param driveLength The distance from the center of the left tire to the center
	* of the right tire, 
	*/
	public SteeringNavigator(float wheelDiameter, float driveLength)
	{
	  this(wheelDiameter, driveLength, Motor.A, Motor.C);
	}
	
/**
* Returns the current x coordinate of the NXT.
* @return float Present x coordinate.
*/
	public float getX() 
	{
		return _x;
	}
	
	/**
	* Returns the current y coordinate of the NXT.
	* Note: At present it will only give an updated reading when the NXT is stopped.
	* @return float Present y coordinate.
	*/
	public float getY() {
	  return _y;
	}
	
	/**
	* Returns the current angle the NXT robot is facing.
	* Note: At present it will only give an updated reading when the NXT is stopped.
	* @return float Angle in degrees.
	*/
	public float getAngle() 
	{
	  return _heading;
	}
/**
 *sets the motor speed of the robot, in degrees/second. 
 */
	public void setSpeed(int speed)
	{
		sc.setSpeed(speed);
	}
/**
* Moves the NXT robot forward until stop() is called.
* @see Navigator#stop().
*/
   public void forward() 
   	{
	  updated = false;
	  sc.forward();
   }
/**
* Moves the NXT robot backward until stop() is called.
*
* @see Navigator#stop().
*/
	public void backward() 
	{
	  updated = false;
	  sc.backward();
	}
/**
* Halts the NXT robot and calculates new x, y coordinates.
*/
	public void stop() 
	{
	   	sc.stop();
		updatePosition();
	}
/**
 *returns true iff the robot is moving under power
 */
	public boolean isMoving()
	{
		return sc.isMoving();
	}
/**
* Rotates the NXT robot a specific number of degrees in a direction (+ or -).
*  If waitForCompletion is true, the robot position is updated atomatically when the method returns. 
* If it is false, the method returns immediately, and your code MUST call updatePostion()
* when the robot has stopped.  Otherwise, the robot position is lost. 
* @param angle Angle to rotate in degrees. A positive value rotates left, a negative value right.
* @param waitForCompletion determines if the method returns immediately (false), in which case the programmer is responsible for calling 
* updatePosition() before the robot moves again. 
*/
	   public void rotate(float angle,boolean waitForCompletion)
		{
		  updated = false; 
	      int turnAngle = Math.round(normalize(angle));
	      sc.rotate(turnAngle,waitForCompletion);
	      if(waitForCompletion) updatePosition();
		}

/**
* Rotates the NXT robot to point in a specific direction. It will take the shortest
* path necessary to point to the desired angle. 
*  If waitForCompletion is true, the robot position is updated atomatically when the method returns. 
* If it is false, the method returns immediately, and your code MUST call updatePostion()
* when the robot has stopped.  Otherwise, the robot position is lost. 
* @param angle The angle to rotate to, in degrees.
* @param waitForCompletion determines if the method returns immediately (false), in which case the programmer is responsible for calling 
* updatePosition() before the robot moves again. 
*/
   public void rotateTo(float angle,boolean waitForCompletion) 
   	{
        float turnAngle = normalize( angle - _heading);
      	rotate(turnAngle,waitForCompletion);
   }
/**
* Rotates the NXT robot towards the target point and moves the required distance.
* Method returns when target point is reached, and the robot position is updated;
* @param x The x coordinate to move to.
* @param y The y coordinate to move to.
*/
   public void gotoPoint(float x, float y) 
   	{

      // Determine relative points
      float dx = x - _x;
      float dy = y - _y;
      // Calculate angle to go to:
      float angle = (float)Math.atan2(dy,dx);
      // Calculate distance to travel:
      float distance = (float)Math.sqrt( dy*dy + dx*dx);
      // Convert angle from rads to degrees:
      angle= (float)Math.toDegrees(angle);
      // Now convert theory into action:
      rotateTo(angle,true);
      travel(distance,true);
   }

/**
* Moves the NXT robot a specific distance. A positive value moves it forwards and
* a negative value moves it backwards. 
*  If waitForCompletion is true, the robot position is updated atomatically when the method returns. 
* If it is false, the method returns immediately, and your code MUST call updatePostion()
* when the robot has stopped.  Otherwise, the robot position is lost. 
* @param dist The positive or negative distance to move the robot, same units as _wheelDiameter
* @param waitForCompletion determines if the method returns immediately (false), in which case the programmer is responsible for calling 
* updatePosition() before the robot moves again. 
*/
	public void travel(float distance,boolean waitForCompletion) 
	{
		updated = false;
		sc.travel(distance,waitForCompletion);
		if(waitForCompletion) updatePosition();
	}
/**
* Updates robot location (x,y) and direction angle. Called by stop, and movement commands that terminate when complete.
* Must be called after a command that returns immediatly, but after robot movement stops, and before another movement method is called.
*/ 
	public void updatePosition()
	{
		if(updated)return;// don't do it again
		int left = sc.getLeftCount();
		int right = sc.getRightCount();
		if(left == 0 && right == 0)return; // no movement
		int outsideRotation = 0;
		int insideRotation = 0;
		int direction = 1; // assume left turn
		if(Math.abs(left)<Math.abs(right))
		{
			outsideRotation = right;
			insideRotation = left;
		}
		else
		{
			outsideRotation = left;
			insideRotation = right;
			direction = -1; // turn to right
		}
		float turnAngle = direction*(outsideRotation-insideRotation)*sc._wheelDiameter/(2*sc._trackWidth);
		float ratio = 1.0f*insideRotation/outsideRotation;
		float moveAngle = 0; // angle of displacement in robot coordinates
		float projection = 0;  //angle to project displacement to world coordinates
		float distance = 0; // of displacement
		boolean approx = false;
		if(ratio>.95) // probably movement was intended to be straight
		{
			float avg = (insideRotation+outsideRotation)/2.0f; 
			distance = avg/sc._degPerDistance;
			projection = (float)Math.toRadians(_heading+turnAngle/2);
			approx = true;
		}
		else
		{ 
			float turnRadius =sc._trackWidth/(1 - ratio) -  sc._trackWidth/2 ; // 
			float radians = (float) Math.toRadians(turnAngle); // turnAngle in radians
			float dx0 = turnRadius*(float)Math.sin(radians);  //displacement  in robot coordinates
			float dy0 = turnRadius*(1 -(float) Math.cos(radians)); 
			distance = (float) Math.sqrt(dx0*dx0+dy0*dy0);  //distance 
			moveAngle = (float)Math.atan2(dy0,dx0); //in robot coordinates
			approx = false;
			projection = moveAngle + (float)Math.toRadians(_heading); // angle to project displacement onto world coordinates
		}
		_heading = normalize(_heading + turnAngle); // keep angle between -180 and 180
		_x += distance * Math.cos(projection); // displacement in world coordinates
		_y += distance * Math.sin(projection);
		if(approx) _heading += turnAngle/2; // correct approximation
		updated = true;
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
		updated = false;
		sc.steer(turnRate);
	}
/**
 * Moves the NXT robot in a circular path through a specific angle; If waitForCompletion is true, returns when angle is reached. <br>
 * Negative turnRate means center of turning circle is on right side of the robot; <br>
 * Range of turnRate values : -200 : 200 ; 
 * Robot will stop when total rotation equals angle. If angle is negative, robot will move travel backwards.
 * @param turnRate see steer( turnRate)
 * @param angle  see steer(turnRage, angle)
 * @param waitForCompletion  If true,  this method returs when angle is reached, and the robot position is automatically updated.<br> 
 *Otherwise it returns immidiately and you code MUST call updatePosition when the robot stops moving, or else the robot positios lost.
 */

	public void steer(int turnRate, int angle, boolean waitForCompletion)
	{
		updated = false;
		sc.steer(turnRate,angle,waitForCompletion);
		if(waitForCompletion) updatePosition();
	   }

/**
 *retuns equivalent angle between -180 and +180
 */
	private float normalize(float angle)
	{
	  float a = angle;
	  while(a > 180) a -= 360;
	  while(a < -180) a += 360;
	  return a;
	}
	

	}


