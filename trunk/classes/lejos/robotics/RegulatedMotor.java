package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Interface for encoded motors without limited range of movement (e.g. NXT motor).
 * TODO: Alternate names: EncodedMotor,EncoderMotor. 
 * 
 * @author dsturze
 */
public interface RegulatedMotor extends BaseMotor, Tachometer {

  /**
   * Adds a listener object that will be notified when rotation has started or stopped 
   * @param listener
   */
	public void addListener(RegulatedMotorListener listener);

    /**
     * Causes motor to stop, pretty much
     * instantaneously. In other words, the
     * motor doesn't just stop; it will resist
     * any further motion.
     * Cancels any rotate() orders in progress
     * @param immediateReturn if true do not wait for the motor to actually stop
     */
    public void stop(boolean immediateReturn);

    /**
     * Set the motor into float mode. This will stop the motor without braking
     * and the position of the motor will not be maintained.
     * @param immediateReturn If true do not wait for the motor to actually stop
     */
    public void flt(boolean immediateReturn);

    /**
     * Wait until the current movement operation is complete (this can include
     * the motor stalling).
     */
    public void waitComplete();

	
  /**
   * causes motor to rotate through angle; <br>
   * iff immediateReturn is true, method returns immediately and the motor stops by itself <br>
   * If any motor method is called before the limit is reached, the rotation is canceled. 
   * When the angle is reached, the method isRotating() returns false;<br>
   * 
   * @param  angle through which the motor will rotate
   * @param immediateReturn iff true, method returns immediately, thus allowing monitoring of sensors in the calling thread. 
   * 
   *  @see RegulatedMotor#rotate(int, boolean)
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
   * Return the limit angle (if any)
   * @return the current limit angle
   */
  public int getLimitAngle();

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
   * Returns the maximim speed of the motor.
   * 
   * @return the maximum speed of the Motor in degrees per second.
   */
  float getMaxSpeed();
  
  /**
   * returns true if motor is stalled
   * @return true if stalled
   */
   boolean isStalled();
   
   /**
    * Set the required rate of acceleration degrees/s/s
    * @param acceleration
    */
   void setAcceleration(int acceleration);
}