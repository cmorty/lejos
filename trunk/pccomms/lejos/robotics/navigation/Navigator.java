package lejos.robotics.navigation;



/**
* The Navigator interface contains methods for performing basic navigational
* movements.  <br>
* <b>A note about coordinates:</b>  Angles that relate to positions in the plane are in degrees relative to the
 * direction of the X axis.  The direction of the Y axis is 90 degrees.
*
* @version 0.8  - June-2009
*/
public interface Navigator
{
  
  /**
  * Returns the X coordinate of the NXT as calculated by the most recent  call to fixPosition()
  * @return float X coordinate.
  */
  public float getX();
  

 /**
  * Returns the Y coordinate of the NXT as calculated by the most recent  call to fixPosition()
  * @return float  Y coordinate.
  */
  public float getY();
  
  /**
  * Returns the direction the NXT is facing, as calculated by the most recent  call to fixPosition()
  * @return float directionAngle in degrees.
  */
  public float getAngle();
  
  /**
   *Returns true if the robot is moving under power
   */
  public boolean isMoving();
  

  /**
   * Sets the movement speed of the robot, wheel diameter units/sec
   */
  public void setMoveSpeed(float speed);
  /**
   * Sets the rotation speed of the robot in deg/sec when robot is turning in place
   */
  public void setTurnSpeed(float speed);

   /**
   * Starts the NXT robot moving forward.
   */
  public void forward();

   /**
   * Starts the NXT robot moving backward.
   */
  public void backward();

  /**
   * Halts the NXT robot and calculates new x, y coordinates.
   */
  public void stop();
 
  /**
   * Sets robot location (x,y) and direction angle
   * @param x  the x coordinate of the robot
   * @param y the y coordinate of the robot
   * @param directionAngle  the angle the robot is heading, measured from the x axis
   */	
  public void setPosition(float x, float y, float directionAngle); 

  /**
   * Moves the NXT robot a specific distance. A positive value moves it forwards and
   * a negative value moves it backwards.
   * The robot position is updated atomatically when the method returns.
   * @param distance The positive or negative distance to move the robot, same units as _wheelDiameter
   */
  public void travel(float distance);
  
  /**
   * Moves the NXT robot a specific distance. A positive value moves it forwards and
   * a negative value moves it backwards. 
   *  If immediateReturnis true, method returns immediately and your code MUST call updatePostion()
   * when the robot has stopped.  Otherwise, the robot position is lost. 
   * @param distance The positive or negative distance to move the robot, same units as _wheelDiameter
   * @param immediateReturn if true, the method returns immediately, in which case 
   *  your code must call updatePosition() before the robot moves again.
   */
  public void travel(float distance, boolean immediateReturn) ;


  /**
  *Starts  the NXT rotating to the left (increasing angle);
  */
  public void rotateLeft();

  /**
   *Starts the NXT rotating to the right (decreasing angle);
   */

  public void rotateRight();


   /**
   * Rotates the NXT robot a specific number of degrees in a direction (+ or -).
   * Robot position is updated when the method exits.
   * @param angle Angle to rotate in degrees. A positive value rotates left, a negative value right.
   */
  public void rotate(float angle);

  /**
   * Rotates the NXT robot through specific number of degrees in a direction (+ or -).
   * If immediateReturn is true, method returns immediately and your code MUST call updatePostion()
   * when the robot has stopped.  Otherwise, the robot position is lost.
   * @param angle Angle to rotate in degrees. A positive value rotates left, a negative value right.
   * @param immediateReturn if true, the method returns immediately, 
   * in which case your code must call  call updatePosition() before the robot moves again.
   */
  public void rotate(float angle, boolean immediateReturn);


  /**
   * Rotates the NXT robot to point in a specific direction. It will use the smallest
   * rotation  necessary to point to the desired angle.
   * @param angle The angle to rotate to, in degrees.
   */
  public void rotateTo(float angle);

  /**
   * Rotates the NXT robot to point in a specific direction. It will take the shortest
   * path necessary to point to the desired angle.
   * If immediateReturnis true, method returns immidiately and your code MUST call updatePostion()
   * when the robot has stopped.  Otherwise, the robot position is lost.
   * @param angle The angle to rotate to, in degrees.
   * @param immediateReturn if true,  method returns immediately and your code must call
   * updatePosition() before the robot moves again.
   */
  public void rotateTo(float angle,boolean immediateReturn) ;


  /**
   * Rotates the NXT robot towards the target point and moves the required distance.
   * Method returns when the point is reached, and the robot position is updated;
   * @param x The x coordinate to move to.
   * @param y The y coordinate to move to.
   */
  public void goTo(float x, float y);

  /**
   * Rotates the NXT robot towards the target point (x,y)  and moves the required distance.
   * If immediateReturnis true, method returns immidiately and your code MUST call updatePostion()
   * when the robot has stopped.  Otherwise, the robot position is lost.
   * @param x The x coordinate to move to.
   * @param y The y coordinate to move to.
   * @param immediateReturn if true,  method returns immediately and your code must call
   * updatePosition() before the robot moves again.
   */
   public void goTo(float x, float y, boolean immediateReturn);

  
  /**
   * Returns the distance from robot to the point with coordinates (x,y) .
   * @param x coordinate of the point
   * @param y coordinate of the point
   * @return the distance from the robot current location to the point
   */
  public float distanceTo( float x, float y);
	
  /**
   * Returns the direction angle (degrees) to the point with coordinates (x,y)
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


