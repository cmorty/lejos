package lejos.robotics;

/**
* The Navigator interface contains methods for performing basic navigational
* movements. Normally the Navigator class is instantiated as an object and
* methods are called on that object.
*
* Note: This class will only work for robots using two motors to steer differentially
* that can rotate within its footprint (i.e. turn on one spot).
* 
* @author <a href="mailto:bbagnall@escape.ca">Brian Bagnall</a>
* @version 0.1  23-June-2001
*/
public interface Navigator{
  
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
  * @return float Angle in degrees.
  */
  public float getAngle();
  
  /**
  * Rotates the NXT robot a specific number of degrees in a direction (+ or -).This
  * method will return once the rotation is complete.
  *
  * @param angle Angle to rotate in degrees. A positive value rotates left, a negative value right.
  */
  public void rotate(float angle);

  /**
  * Rotates the NXT robot to point in a certain direction. It will take the shortest
  * path necessary to point to the desired angle. Method returns once rotation is complete.
  *
  * @param angle The angle to rotate to, in degrees.
  */
  public void gotoAngle(float angle);

  /**
  * Rotates the NXT robot towards the target point and moves the required distance.
  *
  * @param x The x coordinate to move to.
  * @param y The y coordinate to move to.
  */
  public void gotoPoint(float x, float y);

  /**
  * Moves the NXT robot a specific distance. A positive value moves it forward and
  * a negative value moves it backward. Method returns when movement is done.
  *
  * @param distance The positive or negative distance to move the robot.
  */
  public void travel(int distance);
  
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
}
