package lejos.robotics.proposal;

import lejos.robotics.MoveProvider;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

public interface MoveController extends MoveProvider {
  /**
   *Starts the  NXT robot moving forward.
   */
  public void forward();
  
  /**
   *Starts the  NXT robot moving backwards.
   */
  public void backward();

  /**
   * Halts the NXT robot
   *
   * @return The movement it just achieved?
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
  
  public float getMovementIncrement();
  
  public void setMoveSpeed(float speed);
  
  public float getMoveSpeed();
  
  public float getMoveMaxSpeed();
  
}
