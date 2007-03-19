package lejos.navigation;

/**
* The Navigator interface contains methods for performing basic navigational
* movements. Normally the Navigator class is instantiated as an object and
* methods are called on that object.
*
* Note: This class will only work for robots using two motors to steer differentially
* that can rotate within its footprint (i.e. turn on one spot).
* Modified by Roger Glassey 29 Jan 2007
* @author <a href="mailto:bbagnall@escape.ca">Brian Bagnall</a>
* @version 0.2  - Jan-2007
*/
public interface Navigator
{
  
  /**
  * Returns the current x coordinate of the NXT.
  * Note: At present it will only give an updated reading when the NXT is stopped.
  * @return float Present x coordinate.
  */
  public float getX();
  
  /**
  * Returns the current y coordinate of the NXT.
  * Note: At present it will only give an updated reading when the NXT is stopped.
  * @return float Present y coordinate.
  */
  public float getY();
  
  /**
  * Returns the current angle the NXT robot is facing.
  * Note: At present it will only give an updated reading when the NXT is stopped.
  * @return float directionAngle in degrees.
  */
  public float getAngle();
  
  /**
   *returns true iff the robot is moving under power
   */
  public boolean isMoving();
  
  /**
   *sets the motor speed of the robot, in degrees/second. 
   */
  public void setSpeed(int speed);

  /**
   * Moves the NXT robot forward until stop() is called.
   *
   * @see Navigator#stop().
   */	
  public void forward();

  /**
   * Moves the NXT robot backward until stop() is called.
   *
   * @see Navigator#stop().
  */
  public void backward();

  /**
   * Halts the NXT robot and calculates new x, y coordinates.
   *
   * @see Navigator#forward().
   */
  public void stop();
 
  /**
   *sets robot location (x,y) and direction angle
   * @param x  the x coordinate of the robot
   * @param y the y coordinate of the robot
   * @param directionAngle  the angle the robot is heading, measured from the x axis
   */	
  public void setPosition(float x, float y, float directionAngle); 

/**
*Rotates the NXT to the left (increasing angle) until stop() is called;
*/
  public void rotateLeft();
  
/**
*Rotates the NXT to the right (decreasing angle) until stop() is called;
*/
  public void rotateRight();
  
  
  /**
   * Rotates the NXT robot a specific number of degrees in a direction (+ or -).This
   * method will return once the rotation is complete.
   *
   * @param angle Angle to rotate in degrees. A positive value rotates left, a negative value right.
   */
  public void rotate(float angle);
  
  /**
   * Rotates the NXT robot a specific number of degrees in a direction (+ or -).
   * If immediateReturn is true, method returns immediately and your code MUST call updatePostion()
   * when the robot has stopped.  Otherwise, the robot position is lost. 
   * @param angle Angle to rotate in degrees. A positive value rotates left, a negative value right.
   * @param immediateReturn iff true, the method returns immediately, in which case the programmer <br>
   *  is responsible for calling updatePosition() before the robot moves again. 
   */
  public void rotate(float angle, boolean immediateReturn);


  /**
   * Rotates the NXT robot to point in a specific direction. It will take the shortest
   * path necessary to point to the desired angle. 
   * @param angle The angle to rotate to, in degrees.
   */
  public void rotateTo(float angle); 
  
  /**
   * Rotates the NXT robot to point in a specific direction. It will take the shortest
   * path necessary to point to the desired angle. 
   * If immediateReturnis true, method returns immidiately and your code MUST call updatePostion()
   * when the robot has stopped.  Otherwise, the robot position is lost. 
   * @param angle The angle to rotate to, in degrees.
   * @param immediateReturn iff true,  method returns immediately and the programmer is responsible for calling 
   * updatePosition() before the robot moves again. 
   */
  public void rotateTo(float angle,boolean immediateReturn) ;
   	
  
    /**
   * Moves the NXT robot a specific distance. A positive value moves it forward and
   * a negative value moves it backward. Method returns when movement is done.
   * @param distance The positive or negative distance to move the robot.
   */
  public void travel(float distance);
  
  /**
   * Moves the NXT robot a specific distance. A positive value moves it forwards and
   * a negative value moves it backwards. 
   *  If immediateReturnis true, method returns immidiately and your code MUST call updatePostion()
   * when the robot has stopped.  Otherwise, the robot position is lost. 
   * @param distance The positive or negative distance to move the robot, same units as _wheelDiameter
   * @param immediateReturn iff true, the method returns immediately, in which case the programmer <br>
   *  is responsible for calling updatePosition() before the robot moves again. 
   */
  public void travel(float distance, boolean immediateReturn) ;

  /**
   * Rotates the NXT robot towards the target point and moves the required distance.
   *
   * @param x The x coordinate to move to.
   * @param y The y coordinate to move to.
   */
  public void goTo(float x, float y);

  /**
   * Rotates the NXT robot towards the target point and moves the required distance.
   *
   * @param x The x coordinate to move to.
   * @param y The y coordinate to move to.
   * @param immediateReturn Indicates whether method should return immediately.
   */
   public void goTo(float x, float y, boolean immediateReturn);

  
  /**
   * returns the distance from robot to the point with coordinates (x,y) .
   * @param x coordinate of the point
   * @param y coordinate of the point
   * @return the distance from the robot current location to the point
   */
  public float distanceTo( float x, float y);
	
  /**
   * returns the direction angle (degrees) to the point with coordinates (x,y)
   * @param x coordinate of the point
   * @param y coordinate of the point
   * @return the direction angle to the point (x,y) from the NXT.  Rotate to this angle to head toward it. 
   */
  public float angleTo(float x, float y);
	
  /**
   * Updates robot location (x,y) and direction angle. Called by stop, and movement commands that terminate when complete.
   * Must be called after a command that returns immediatly, but after robot movement stops, and before another movement method is called.
   */ 
  public void updatePosition();
 
}


