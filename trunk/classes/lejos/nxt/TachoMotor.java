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
   * Causes motor to rotate by a specified angle. The resulting tachometer count should be within +- 2 degrees on the
   * NXT. If any motor method is called before the limit is reached, the rotation is canceled.
   * 
   * @param angle by which the motor will rotate.
   * @param immediateReturn if true return immediately, else wait for rotation to finish.
   */
  void rotate(int angle, boolean immediateReturn);

  /**
   * Causes motor to stop immediately. It will resist any further motion. Cancels any rotate() orders in progress.
   */
  void stop();

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
