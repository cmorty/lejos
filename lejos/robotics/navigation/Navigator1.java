package lejos.robotics.navigation;



/**
* The Navigator interface contains methods for performing basic navigational
* movements.  <br>
* <b>A note about coordinates:</b>  Angles that relate to positions in the plane are in degrees relative to the
 * direction of the X axis.  The direction of the Y axis is 90 degrees.
*
* @version 0.8  - June-2009
*/
public interface Navigator1
{
  
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
  public void setPose(float x, float y, float directionAngle);

  /**
   * Sets robot pose
   * @param aPose the new robot pose
   * @param directionAngle  the angle the robot is heading, measured from the x axis
   */
  public void setPost(Pose aPose);
/**
 * returns the current pose of the robot
 */
  public  Pose getPose();

  /**
   * Rotates the NXT robot towards the target point and moves the required distance.
   * Method returns when the point is reached, and the robot position is updated;
   * @param x The x coordinate to move to.
   * @param y The y coordinate to move to.
   */
  public void goTo(float x, float y);

  /**
   * Rotates the NXT robot towards the target point (x,y)  and moves the required distance.
   * If immediateReturnis true, method returns immediately and your code MUST call updatePostion()
   * when the robot has stopped.  Otherwise, the robot position is lost.
   * @param x The x coordinate to move to.
   * @param y The y coordinate to move to.
   * @param immediateReturn if true,  method returns immediately and your code must call
   * updatePosition() before the robot moves again.
   */
   public void goTo(float x, float y, boolean immediateReturn);


  /**
   * Updates robot location (x,y) and direction angle. Called by stop, and movement commands that terminate when complete.
   * Must be called after a command that returns immediately, but after robot movement stops, and before another movement method is called.
   */ 
  public void updatePose();
}


