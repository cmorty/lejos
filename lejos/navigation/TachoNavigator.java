package lejos.navigation;
import lejos.nxt.Motor;

/**
* The TachoNavigator class can keep track of the robot position and the direction angle it faces; It uses a pilot object to control NXT robot movements.<br>
* The position and direction angle values are updated automatically when the movement command returns after the movement is complete and and after stop() command is issued.
* However, some commands optionally return immediately, to permit sensor monitoring in the main thread.  It is then the programmers responsibility to 
* call updatePosition() when the robot motion is completed.  All angles are in degrees, distances in the units used to specify robot dimensions.
* As with pilot, the robot must be have two independently controlled drive wheels. 
*/

public class TachoNavigator  implements Navigator
{ 
	// orientation and co-ordinate data
	private float _heading = 0;
	private float _x = 0;
	private float _y = 0;
	// The essential component
	public Pilot pilot;
/**
 * set false whenever the robot moves,  set to true by updatePosition();
 */
	private boolean _updated = false;

/**
* Allocates a Navigator object and initializes it with the proper motors.
* The x and y values and the direction angle are all initialized to 0, so if the first move is forward() the robot will run along
* the x axis. <BR>
* @param wheelDiameter The diameter of the wheel, usually printed right on the
* wheel, in centimeters (e.g. 49.6 mm = 4.96 cm = 1.95 in) 
* @param trackWidth The distance from the center of the left tire to the center
* of the right tire, in units of your choice
* @param rightMotor The motor used to drive the right wheel e.g. Motor.C.
* @param leftMotor The motor used to drive the left wheel e.g. Motor.A.
*/
	public TachoNavigator(float wheelDiameter, float trackWidth, Motor leftMotor, Motor rightMotor) 
	{
		pilot = new Pilot(wheelDiameter,trackWidth,leftMotor, rightMotor);
	}
	   public TachoNavigator(float wheelDiameter, float trackWidth, Motor leftMotor, Motor rightMotor, boolean reverse) 
	{
		pilot = new Pilot(wheelDiameter,trackWidth,leftMotor, rightMotor,reverse);
	}
	   
	   public TachoNavigator(Pilot pilot) {
	  	 this.pilot = pilot;
	   }
	   
	/**
	* Overloaded TachoNavigator constructor that assumes the following:<BR>
	* Left motor = Motor.A   Right motor = Motor.C <BR>
	* @param wheelDiameter The diameter of the wheel, usually printed right on the
	* wheel, in centimeters (e.g. 49.6 mm = 4.96 cm)  
	* @param driveLength The distance from the center of the left tire to the center
	* of the right tire, 
	*/
	public TachoNavigator(float wheelDiameter, float driveLength)
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
		pilot.setSpeed(speed);
	}
	
	/**
	 * Moves the NXT robot forward until stop() is called.
	 * @see Navigator#stop().
	 */
    public void forward() 
    {
	  _updated = false;
	  pilot.resetTachoCount();
	  pilot.forward();
    }
   
    /**
     * Moves the NXT robot backward until stop() is called.
     */
	public void backward() 
	{
	  _updated = false;
  	  pilot.resetTachoCount();
	  pilot.backward();
	}
	
	/**
	 * Halts the NXT robot and calculates new x, y coordinates.
	 */
	public void stop() 
	{
	   	pilot.stop();
		updatePosition();
	}
	
	/**
	 *returns true iff the robot is moving under power
	 */
	public boolean isMoving()
	{
		return pilot.isMoving();
	}

	/**
	 * Moves the NXT robot a specific distance. A positive value moves it forwards and
	 * a negative value moves it backwards. 
	 * The robot position is updated atomatically when the method returns. 
	 * @param distance The positive or negative distance to move the robot, same units as _wheelDiameter
	 */
	public void travel(float distance) 
	{
		travel(distance,false);
	}

	/**
	 * Moves the NXT robot a specific distance. A positive value moves it forwards and
	 * a negative value moves it backwards. 
	 *  If immediateReturnis true, method returns immidiately and your code MUST call updatePostion()
	 * when the robot has stopped.  Otherwise, the robot position is lost. 
	 * @param distance The positive or negative distance to move the robot, same units as _wheelDiameter
	 * @param immediateReturn iff true, the method returns immediately, in which case the programmer <br>
	 *  is responsible for calling updatePosition() before the robot moves again. 
	 */
	public void travel(float distance,boolean immediateReturn) 
	{
		_updated = false;
		pilot.resetTachoCount();
		pilot.travel(distance,immediateReturn);
		if(!immediateReturn) updatePosition();
	}

/**
*Rotates the NXT to the left (increasing angle) until stop() is called;
*/
	public void rotateLeft()
	{
	  _updated = false;
	  pilot.resetTachoCount();
	  pilot.steer(200);
	}
  
/**
*Rotates the NXT to the right (decreasing angle) until stop() is called;
*/
  public void rotateRight()
  {
  	  _updated = false;
	  pilot.resetTachoCount();
	  pilot.steer(-200);
  }

	/**
	 * Rotates the NXT robot a specific number of degrees in a direction (+ or -).
	 * @param angle Angle to rotate in degrees. A positive value rotates left, a negative value right.
	 **/
	public void rotate(float angle)
	{
		rotate(angle,false);
	}
	
	/**
	 * Rotates the NXT robot a specific number of degrees in a direction (+ or -).
	 *  If immediateReturnis true, method returns immidiately and your code MUST call updatePostion()
	 * when the robot has stopped.  Otherwise, the robot position is lost. 
	 * @param angle Angle to rotate in degrees. A positive value rotates left, a negative value right.
	 * @param immediateReturn iff true, the method returns immediately, in which case the programmer <br>
	 *  is responsible for calling updatePosition() before the robot moves again. 
	 */
   public void rotate(float angle,boolean immediateReturn)
	{
	  _updated = false; 
      int turnAngle = Math.round(normalize(angle));
      pilot.resetTachoCount();
      pilot.rotate(turnAngle,immediateReturn);
      if(!immediateReturn) updatePosition();
	}

   /**
    * Rotates the NXT robot to point in a specific direction. It will take the shortest
    * path necessary to point to the desired angle. 
    * @param angle The angle to rotate to, in degrees.
    */
   public void rotateTo(float angle) 
   	{
        float turnAngle = normalize( angle - _heading);
      	rotate(turnAngle,false);
   }

   /**
    * Rotates the NXT robot to point in a specific direction. It will take the shortest
    * path necessary to point to the desired angle. 
    * If immediateReturnis true, method returns immidiately and your code MUST call updatePostion()
    * when the robot has stopped.  Otherwise, the robot position is lost. 
    * @param angle The angle to rotate to, in degrees.
    * @param immediateReturn iff true,  method returns immediately and the programmer is responsible for calling 
    * updatePosition() before the robot moves again. 
    */
   public void rotateTo(float angle,boolean immediateReturn) 
   	{
        float turnAngle = normalize( angle - _heading);
      	rotate(turnAngle,immediateReturn);
   }
   
   /**
    * Rotates the NXT robot towards the target point (x,y)  and moves the required distance.
    * Method returns when target point is reached, and the robot position is updated;
    * @param x The x coordinate to move to.
    * @param y The y coordinate to move to.
    */
   public void goTo(float x, float y) 
   {
      rotateTo(angleTo(x,y));
      travel(distanceTo(x,y));
   }
  
   /**
    * Rotates the NXT robot towards the target point (x,y)  and moves the required distance.
    * Method returns when target point is reached, and the robot position is updated;
    * @param x The x coordinate to move to.
    * @param y The y coordinate to move to.
    * @param immediateReturn iff true,  method returns immediately
    */
   public void goTo(float x, float y, boolean immediateReturn) 
   {
	   rotateTo(angleTo(x,y));
       travel(distanceTo(x,y), immediateReturn);
   }
   
   /**
    * distance from robot to the point with coordinates (x,y) .
    * @param x coordinate of the point
    * @param y coordinate of the point
    * @return the distance from the robot current location to the point
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
	 * @param x coordinate of the point
 	 * @param y coordinate of the point
 	 * @return the direction angle to the point (x,y) from the NXT.  Rotate to this angle to head toward it. 
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
		if(_updated)return;// don't do it again
		try{Thread.sleep(70);}
		catch(InterruptedException e){}
		int left = pilot.getLeftCount();//left wheel rotation angle
		int right = pilot.getRightCount();
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
		float turnAngle = direction*(outsideRotation-insideRotation)*pilot._wheelDiameter/(2*pilot._trackWidth);
		float ratio = 1.0f*insideRotation/outsideRotation;
		float moveAngle = 0; // angle of displacement in robot coordinates, degrees
		float projection = 0;  //angle to project displacement to world coordinates, in radians
		float distance = 0; // of displacement
		boolean approx = false;
		if(ratio>.95) // probably movement was intended to be straight
		{
			float avg = (insideRotation+outsideRotation)/2.0f; 
			distance = avg/pilot._degPerDistance;
			projection = (float)Math.toRadians(_heading+turnAngle/2);
			approx = true;
		}
		else
		{ 
			float turnRadius =pilot._trackWidth/(1 - ratio) -  pilot._trackWidth/2 ; // 
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
		_updated = true;
	}
	
	/**
	 * Moves the NXT robot in a circular path with a specified radius. <br>
	 * The center of the turning circle is on the right side of the robot iff parameter radius is negative;  <br>
	 * Postcondition:  motor speed is NOT restored to previous value;
	 * @param radius is the radius of the circular path. If positive, the left wheel is on the inside of the turn.  If negative, the left wheel is on the outside.
	 */
	public void turn(float radius)
	{
		_updated = false;
		pilot.resetTachoCount();
		pilot.steer(turnRate(radius));
	}

	/**
	 * Moves the NXT robot in a circular path through a specific angle; If waitForCompletion is true, returns when angle is reached. <br>
	 * The center of the turning circle is on the right side of the robot iff parameter radius is negative.
	 *  Robot will stop when total rotation equals angle. If angle is negative, robot will move travel backwards.
	 * @param radius radius of the turning circle
	 * @param angle the angle by which the robot heading changes, + or -
	 */
	public void turn(float radius, int angle)
	{
		turn(radius,angle,false);
	}
	
	/**
	 * Moves the NXT robot in a circular path through a specific angle; If waitForCompletion is true, returns when angle is reached. <br>
	 * The center of the turning circle is on the right side of the robot iff parameter radius is negative.
	 * Robot will stop when total rotation equals angle. If angle is negative, robot will move travel backwards.
	 * @param radius  see turn(turnRage, angle)
	 * @param immediateReturn iff true, the method returns immediately, in which case the programmer <br>
	 * is responsible for calling updatePosition() before the robot moves again. 
	 */
	public void turn(float radius, int angle, boolean immediateReturn)
	{
		_updated = false;
		pilot.resetTachoCount();
		pilot.steer(turnRate(radius),angle,immediateReturn);
		if(!immediateReturn) updatePosition();
	}

	/**
	 * returns equivalent angle between -180 and +180
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
		float ratio = (2*radius - pilot._trackWidth)/(2*radius+pilot._trackWidth);
		return Math.round(direction * 100*(1 - ratio));
	}
}

