package lejos.robotics.proposal;

import lejos.robotics.Movement;

public interface RotatePilot extends BasicPilot {
  /**
   * Rotates the NXT robot the specified number of degrees; direction determined by the sign of the parameter.
   * Method returns when rotation is done.
   * 
   * @param angle The angle to rotate in degrees. A positive value rotates left, a negative value right (clockwise).
   */
  public Movement rotate(float angle);

  /**
   * Rotates the NXT robot the specified number of degrees; direction determined by the sign of the parameter.
   * Motion stops  when rotation is done.
   * 
   * @param angle The angle to rotate in degrees. A positive value rotates left, a negative value right (clockwise).
   * @param immediateReturn If immediateReturn is true then the method returns immediately
   */
  public Movement rotate(float angle, boolean immediateReturn);
  
  public float getAngleIncrement();
  
  public void setTurnSpeed(float speed);
  
  public float getTurnSpeed();
  
  public float getTurnMaxSpeed();
}
