//package lejos.robotics;
//import lejos.nxt.Motor;
//import lejos.nxt.LCD;


/**
* The CircleNavigator class can keep track of the robot position and the direction angle it faces; It uses a SteeringControl object to control NXT robot movements.<br>
* The position and direction angle values are updated automatically when the movement command returns after the movement is complete and and after stop() command is issued.
* However, some commands optionally return immediately, to permit sensor monitoring in the main thread.  It is then the programmers responsibility to 
* call updatePosition() when the robot motion is completed.  All angles are in degrees, distances in the units used to specify robot dimensions.
* As with SteeringControl, the robot must be have two independently controlled drive wheels. 
*@author Roger Glassey  22 Jan 2007
*/

public class CircleNavigator { // implements

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
* The x and y values and the direction angle are all initialized to 0, so if the first move is forward() the robot will run along
* the x axis. <BR>
* @param wheelDiameter The diameter of the wheel, usually printed right on the
* wheel, in centimeters (e.g. 49.6 mm = 4.96 cm) 
* @param trackWidth The distance from the center of the left tire to the center
* of the right tire, in units of your choice
* @param rightMotor The motor used to drive the right wheel e.g. Motor.C.
* @param leftMotor The motor used to drive the left wheel e.g. Motor.A.
*/
	
	public CircleNavigator(float wheelDiameter, float trackWidth, Motor leftMotor, Motor rightMotor) 
	{
		sc = new SteeringControl(leftMotor, rightMotor,wheelDiameter,trackWidth);
	}
	   public CircleNavigator(float wheelDiameter, float trackWidth, Motor leftMotor, Motor rightMotor, boolean reverse) 
	{
		sc = new SteeringControl(leftMotor, rightMotor,wheelDiameter,trackWidth,reverse);
	}
	/**
	* Overloaded CircleNavigator constructor that assumes the following:<BR>
	* Left motor = Motor.A   Right motor = Motor.C <BR>
	* @param wheelDiameter The diameter of the wheel, usually printed right on the
	* wheel, in centimeters (e.g. 49.6 mm = 4.96 cm)  
	* @param driveLength The distance from the center of the left tire to the center
	* of the right tire, 
	*/
	public CircleNavigator(float wheelDiameter, float driveLength)
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
*sets robot location (x,y) and direction angle
*@param x  the x coordinate of the robot
*@param y the y coordinate of the robot
*@param directionAngle  the angle the robot is heading, measured from the x axis
*/	
	public void setPosition(float x, float y, float directionAngle)
	{
		_x = x;
		_y = y;
		_heading = directionAngle;
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
* Rotates the NXT robot towards the target point (x,y)  and moves the required distance.
* Method returns when target point is reached, and the robot position is updated;
* @param x The x coordinate to move to.
* @param y The y coordinate to move to.
*/
   public void goTo(float x, float y) 
   	{
      rotateTo(angleTo(x,y),true);
      travel(distanceTo(x,y),true);
   }
/**
 * distance from robot to the point with coordinates (x,y) .
 *@param x coordinate of the point
 *@param y coordinate of the point
 *@return the distance from the robot current location to the point
 */
	public float distanceTo( float x, float y)
	{
		float dx = x -_x;
		float dy = y -_y; 
		//use hypotenuse formula
		return (float)Math.sqrt(dx*dx+dy*dy);
	}
/**
 * returns the direction angle (degrees) to point with coordinates (x,y)
 *@param x coordinate of the point
 *@param y coordinate of the point
 *@return the direction angle to the point (x,y) from the NXT.  Rotate to this angle to head toward it. 
 */
	public float angleTo(float x, float y)
	{
		float dx = x -_x;
		float dy = y -_y;
		return(float)Math.toDegrees(Math.atan2(dy,dx));
	}

/**
* Updates robot location (x,y) and direction angle. Called by stop, and movement commands that terminate when complete.
* Must be called after a command that returns immediatly, but after robot movement stops, and before another movement method is called.
*/ 
	public void updatePosition()
	{
		if(updated)return;// don't do it again
		try{Thread.sleep(70);}
		catch(InterruptedException e){}
		int left = sc.getLeftCount();//left wheel rotation angle
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
		float moveAngle = 0; // angle of displacement in robot coordinates, degrees
		float projection = 0;  //angle to project displacement to world coordinates, in radians
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
 * Moves the NXT robot in a circular path with a specified radius. <br>
 * The center of the turning circle is on the right side of the robot iff parameter radius is negative;  <br>
 * Postcondition:  motor speed is NOT restored to previous value;
 * @param radius is the radius of the circular path. If positive, the left wheel is on the inside of the turn.  If negative, the left wheel is on the outside.
 */

	public void turn(float radius)
	{
		updated = false;
		sc.steer(turnRate(radius));
	}
/**
 * Moves the NXT robot in a circular path through a specific angle; If waitForCompletion is true, returns when angle is reached. <br>
 * The center of the turning circle is on the right side of the robot iff parameter radius is negative.
 *  Robot will stop when total rotation equals angle. If angle is negative, robot will move travel backwards.
 * @param radius  see tu(turnRage, angle)
 * @param waitForCompletion  If true,  this method returns when angle is reached, and the robot position is automatically updated.<br> 
 *Otherwise it returns immediately and you code MUST call updatePosition when the robot stops moving, or else the robot positios lost.
 */

	public void turn(float radius, int angle, boolean waitForCompletion)
	{
		updated = false;

		sc.steer(turnRate(radius),angle,waitForCompletion);
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
	private int turnRate(float radius)
	{
		int direction = 1;
		if(radius<0) 
		{
			direction = -1;
			radius = -radius;
		}
		float ratio = (2*radius - sc._trackWidth)/(2*radius+sc._trackWidth);
		return Math.round(direction * 100*(1 - ratio));
	}
}


