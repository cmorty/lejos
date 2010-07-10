package lejos.robotics.proposal;

//package lejos.robotics.proposal;

import lejos.robotics.MoveProvider;



/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */


/**
 * The MoveControl interface forms a common set of functions an implementation must offer in order to be used by higher level
 * navigation classes. The Pilot hides the details of the robots physical construction and
 * methods to control the basic movements of the robot.<p>
 * This Pilot interfaces uses the Navigation package  standard mathematical convention for angles in
 * the plane. The direction  of the X axis is 0 degrees, the direction  of
 * the Y axis is 90 degrees.  Therefore, a positive angle is a counter clockwise change of direction,
 * and a negative angle is clockwise. Angles are measured in degrees.<p>
 * Several methods allow travel in the arc of a circle with a specified radius.
 * While a negative radius does not exist in ordinary mathematics, this interface
 * uses the sign bit of the radius parameter to denote the location of the center of the
 * turning circle with respect to the direction of the robot heading.
*
 */
public interface MoveControl extends MoveProvider
{

    /**
   * Sets the travel speed of the robot in distance/second; Distance is measured
   * in the same units as wheelDiameter and trackWidth parameters of the constructor;
   * Must be non-negative;
   * @param travelSpeed  in wheel diameter units per second
   */
  public void setTravelSpeed(float travelSpeed);

  /**
   * returns the travel speed of the robot in distance/second; Distance is measured
   * in the same units as wheelDiameter and trackWidth parameters of the constructor;
   * @return the movement speed of the robot in wheel diameter units per second.
   */
  public float getTravelSpeed();

  /**
   * Returns the maximal movement speed of the robot depending on battery voltage
   * @return the maximal movement speed of the robot in wheel diameter units per second which can be maintained
   *         accurately. Will change with time, as it is normally dependent on the battery voltage.
   */
  public float getMaxTravelSpeed();

  /**
   * Sets the speed of rotation  of the robot; used by the Rotate() methods;
   * Must be non-negataive;
   * @param rotateSpeed in degrees per second
   */
  public void setRotateSpeed(float rotateSpeed);

  /**
   * Returns the turning speed of the robot in degrees per second.
   * @return the turning speed of the robot in degree per second.
   */
  public float getRotateSpeed();

  /**
   * Returns the maximum turning speed depending on battery voltage.
   * @return the maximal turning speed of the robot in degree per second which can be maintained accurately. Will change
   *         with time, as it is normally dependent on the battery voltage.
   */
  public float getMaxRotateSpeed();


  /**
   *Starts the  NXT robot moving  forward.
   */
  public void forward();

  /**
   *Starts the  NXT robot moving  backward .
   */
  public void backward();

  /**
   * Halts the NXT robot
   */
  public void stop();

  /**
   * true if the robot is moving 
   * @return true if the robot is moving under power.
   */
  public boolean isMoving();

  /**
   * Moves the NXT robot a specific distance. A positive value moves it forward and a negative value moves it backward.
   * Method returns when movement is done.
   * 
   * @param distance The positive or negative distance to move the robot.
   */
  public void travel(float distance);

  /**
   * Moves the NXT robot a specific distance. A positive value moves it forward and a negative value moves it backward.
   * @param distance The positive or negative distance to move the robot, in wheel diameter units.
   * @param immediateReturn If immediateReturn is true then the method returns immediately.
   */
  public void travel(float distance, boolean immediateReturn);


  /**
   * Rotates the NXT robot in place by the  specified number of degrees;
   * direction determined by the sign of the parameter.
   * Method returns when rotation is done.
   * @param angle The angle to rotate in degrees. A positive value rotates left, a negative value right (clockwise).
   */
  public void rotate(float angle);

  /**
   * Rotates the NXT robotin place by the specifed number of degress; direction determined by the sign of the parameter.
   * Motion stops  when rotation is done.
   *
   * @param angle The angle to rotate in degrees. A positive value rotates left, a negative value right (clockwise).
   * @param immediateReturn If immediateReturn is true then the method returns immediately
   */
  public void rotate(float angle, boolean immediateReturn);

  /**
   * return the distance moved
   * @return distance moved since start of the movement
   */

  public float getMovementIncrement();
  
  /**
   * returns heading change since start of the current movement
   * @return heading change since start of the current movement
   */

  public float getAngleIncrement();



 

  /**
   * Reset traveled distance and rotated angle.
   */
  public void reset();
}
