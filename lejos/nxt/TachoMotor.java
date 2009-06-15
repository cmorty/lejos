package lejos.nxt;

/**
 * Interface for encoded motors without limited range of movement (e.g. NXT motor).
 * 
 * @author dsturze
 */
public interface TachoMotor {

  /**
   * Causes motor to rotate forward until stop is called.
   */
  void forward();

  /**
   * Causes motor to rotate backwards until stop is called.
   */
  void backward();

  /**
   * causes motor to rotate through angle; <br>
   * iff immediateReturn is true, method returns immediately and the motor stops by itself <br>
   * If any motor method is called before the limit is reached, the rotation is canceled. 
   * When the angle is reached, the method isRotating() returns false;<br>
   * 
   * @param  angle through which the motor will rotate
   * @param immediateReturn iff true, method returns immediately, thus allowing monitoring of sensors in the calling thread. 
   * 
   *  @see TachoMotor#rotate(int, boolean)
   */
  void rotate(int angle, boolean immediateReturn);

  /**
   * Causes motor to rotate by a specified angle. The resulting tachometer count should be within +- 2 degrees on the NXT.
   * This method does not return until the rotation is completed.
   * 
   * @param angle by which the motor will rotate.
   * 
   */
  void rotate(int angle);

  
  /**
   * Causes motor to rotate to limitAngle;  <br>
   * Then getTachoCount should be within +- 2 degrees of the limit angle when the method returns
   * @param  limitAngle to which the motor will rotate, and then stop (in degrees). Includes any positive or negative int, even values > 360.
   */
  public void rotateTo(int limitAngle);
  
  /**
   * causes motor to rotate to limitAngle; <br>
   * if immediateReturn is true, method returns immediately and the motor stops by itself <br> 
   * and getTachoCount should be within +- 2 degrees if the limit angle
   * If any motor method is called before the limit is reached, the rotation is canceled. 
   * When the angle is reached, the method isRotating() returns false;<br>
   * @param  limitAngle to which the motor will rotate, and then stop (in degrees). Includes any positive or negative int, even values > 360. 
   * @param immediateReturn iff true, method returns immediately, thus allowing monitoring of sensors in the calling thread.
   */
  public void rotateTo(int limitAngle,boolean immediateReturn);  
  
  /**
   * Causes motor to stop immediately. It will resist any further motion. Cancels any rotate() orders in progress.
   */
  void stop();

  /**
   * Motor loses all power, causing the rotor to float freely to a stop.
   * This is not the same as stopping, which locks the rotor. 
   */   
  public void flt();
  
  /**
   * Return if the motor is moving.
   * 
   * @return true if the motor is currently in motion
   */
  boolean isMoving();

  /**
   * Set motor speed. As a rule of thumb 100 degrees per second are possible for each volt on an NXT motor.
   * 
   * @param speed in degrees per second.
   */
  void setSpeed(int speed);

  /**
   * Returns the current motor speed.
   * 
   * @return motor speed in degrees per second
   */
  int getSpeed();

  /**
   * Returns the actual speed. This value is calculated every 100 ms on the NXT.
   * 
   * @return speed in degrees per second, negative value means motor is rotating backward
   */
  int getActualSpeed();

  /**
   * Reset the tachometer count.
   */
  void resetTachoCount();

  /**
   * Returns the tachometer count.
   * 
   * @return tachometer count in degrees
   */
  int getTachoCount();

  /**
   * Turns speed regulation on/off. Cumulative speed error is within about 1 degree after initial acceleration on the
   * NXT.
   * 
   * @param activate is true for speed regulation.
   */
  void regulateSpeed(boolean activate);

  /**
   * Enables smoother acceleration. Motor speed increases gently, and does not overshoot when regulate Speed is used.
   * 
   * @param activate is true for smooth acceleration.
   */
  void smoothAcceleration(boolean activate);
}
