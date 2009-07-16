package lejos.robotics.navigation;
import lejos.robotics.Pose;


/**
* The Navigator interface contains methods for moving to a destination.
* <b>A note about coordinates:</b>  Angles that relate to positions in the plane are in degrees relative to the
 * direction of the X axis.  The direction of the Y axis is 90 degrees.
*
* @version 0.8  - July-2009
*/
public interface Navigator1
{
  
  /**
   * Halts the NXT robot and calculates new x, y coordinates.
   */
  public void stop();
 
  /**
   * Sets robot pose: location (x,y) and direction angle
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
  public void setPose(Pose aPose);
/**
 * returns the current pose of the robot
 */
  public  Pose getPose();

  /**
   * Rotates the NXT robot towards the destination point (X,Y) and moves the required distance.
   * Method returns when the destination is rached, and the robot pose is updated;
   * @param x The x coordinate to move to.
   * @param y The y coordinate to move to.
   */
  public void goTo(float x, float y);

  /**
   * Rotates the NXT robot towards the destination  point (x,y)  and moves the required distance.
   * If immediateReturnis true, method returns immediately and your code MUST call updatePose()
   * or stop()  Otherwise, the robot position is lost.
   * @param x The x coordinate to move to.
   * @param y The y coordinate to move to.
   * @param immediateReturn if true,  method returns immediately.
   */
   public void goTo(float x, float y, boolean immediateReturn);


  /**
   * Updates robot location (x,y) and direction angle. Called by stop, and goTo(x,y)
   * Must by called after goTo(x,y,true) unless stop() is called;
   */ 
  public void updatePose();
}


