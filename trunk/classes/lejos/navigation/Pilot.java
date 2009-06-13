package lejos.navigation;

/**
 * The Pilot interface forms a common set of functions an implementation must offer in order to be used by higher level
 * navigation classes. The Pilot hides the details of the robots physical construction and the required control
 * algorithms from the rest of this package.
 */
public interface Pilot {
  /**
   * Moves the NXT robot forward until stop() is called.
   * 
   * @see Navigator#stop()
   */
  public void forward();

  /**
   * Moves the NXT robot backward until stop() is called.
   * 
   * @see Navigator#stop()
   */
  public void backward();

  /**
   * Halts the NXT robot and calculates new x, y coordinates.
   * 
   * @see Navigator#forward()
   */
  public void stop();

  /**
   * @return true if the robot is moving under power.
   */
  public boolean isMoving();

  /**
   * Sets the movement speed of the robot.
   * 
   * @param speed The speed in wheel diameter units per second.
   */
  public void setMoveSpeed(float speed);

  /**
   * @return the movement speed of the robot in wheel diameter units per second.
   */
  public float getMoveSpeed();

  /**
   * @return the maximal movement speed of the robot in wheel diameter units per second which can be maintained
   *         accurately. Will change with time, as it is normally dependent on the battery voltage.
   */
  public float getMoveMaxSpeed();

  /**
   * Sets the turning speed of the robot.
   * 
   * @param speed The speed in degree per second.
   */
  public void setTurnSpeed(float speed);

  /**
   * @return the turning speed of the robot in degree per second.
   */
  public float getTurnSpeed();

  /**
   * @return the maximal turning speed of the robot in degree per second which can be maintained accurately. Will change
   *         with time, as it is normally dependent on the battery voltage.
   */
  public float getTurnMaxSpeed();

  /**
   * Sets drive motor speed.
   * 
   * @param speed The speed of the drive motor(s) in degree per second.
   * 
   * @deprecated in 0.8, use setTurnSpeed() and setMoveSpeed(). The method was deprecated, as this it requires knowledge
   *             of the robots physical construction, which this interface should hide! 
   */
  public void setSpeed(int speed);

  /**
   * Moves the NXT robot a specific distance. A positive value moves it forward and a negative value moves it backward.
   * Method returns when movement is done.
   * 
   * @param distance The positive or negative distance to move the robot.
   */
  public void travel(float distance);

  /**
   * Moves the NXT robot a specific distance. A positive value moves it forward and a negative value moves it backward.
   * 
   * @param distance The positive or negative distance to move the robot.
   * @param immediateReturn If immediateReturn is true then the method returns immediately and your code MUST call
   *          updatePostion() when the robot has stopped. Otherwise, the robot position is lost.
   */
  public void travel(float distance, boolean immediateReturn);

  /**
   * Rotates the NXT robot a specific number of degrees in a direction. Method returns when rotation is done.
   * 
   * @param angle The angle to rotate in degrees. A positive value rotates left, a negative value right (clockwise).
   */
  public void rotate(float angle);

  /**
   * Rotates the NXT robot a specific number of degrees in a direction. Method returns when rotation is done.
   * 
   * @param angle The angle to rotate in degrees. A positive value rotates left, a negative value right (clockwise).
   * @param immediateReturn If immediateReturn is true then the method returns immediately and your code MUST call
   *          updatePostion() when the robot has stopped. Otherwise, the robot position is lost.
   */
  public void rotate(float angle, boolean immediateReturn);

  /**
   * @return the angle of rotation of the robot since last call to reset.
   */
  public float getAngle();

  /**
   * @return distance traveled by the robot since last call to reset.
   **/
  public float getTravelDistance();

  /**
   * Moves the NXT robot in a circular path at a specific turn rate. The center of the turning circle is on the right
   * side of the robot if parameter turnRate is negative. Values for turnRate are between -200 and +200. The turnRate
   * determines the ratio of inner wheel speed to outer wheel speed (as a percent). <br>
   * <I>Formula:</I> ratio = 100 - abs(turnRate). When the ratio is negative, the outer and inner wheels rotate in
   * opposite directions. <br>
   * Examples:
   * <UL>
   * <LI>steer(25) -> inner wheel turns at 75% of the speed of the outer wheel
   * <LI>steer(100) -> inner wheel stops
   * <LI>steer(200) -> means that the inner wheel turns at the same speed as the outer wheel - a zero radius turn.
   * </UL>
   * Note: Even if you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param turnRate If positive, the left wheel is on the inside of the turn. If negative, the left wheel is on the
   *          outside.
   */
  public void turn(int turnRate);

  /**
   * Moves the NXT robot in a circular path at a specific turn rate. The center of the turning circle is on the right
   * side of the robot if parameter turnRate is negative. Values for turnRate are between -200 and +200. The turnRate
   * determines the ratio of inner wheel speed to outer wheel speed (as a percent).<br>
   * <I>Formula:</I> ratio = 100 - abs(turnRate). When the ratio is negative, the outer and inner wheels rotate in
   * opposite directions.<br>
   * Examples:
   * <UL>
   * <LI>steer(25) -> inner wheel turns at 75% of the speed of the outer wheel
   * <LI>steer(100) -> inner wheel stops
   * <LI>steer(200) -> means that the inner wheel turns at the same speed as the outer wheel - a zero radius turn.
   * </UL>
   * Note: Even if you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param turnRate If positive, the left wheel is on the inside of the turn. If negative, the left wheel is on the
   *          outside.
   * @param angle The angle through which the robot will rotate. If negative, robot traces the turning circle backwards.
   */
  public void turn(int turnRate, int angle);

  /**
   * Moves the NXT robot in a circular path at a specific turn rate. The center of the turning circle is on the right
   * side of the robot if parameter turnRate is negative. Values for turnRate are between -200 and +200. The turnRate
   * determines the ratio of inner wheel speed to outer wheel speed (as a percent).<br>
   * <I>Formula:</I> ratio = 100 - abs(turnRate). When the ratio is negative, the outer and inner wheels rotate in
   * opposite directions.<br>
   * Examples:
   * <UL>
   * <LI>steer(25) -> inner wheel turns at 75% of the speed of the outer wheel
   * <LI>steer(100) -> inner wheel stops
   * <LI>steer(200) -> means that the inner wheel turns at the same speed as the outer wheel - a zero radius turn.
   * </UL>
   * Note: Even if you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param turnRate If positive, the left wheel is on the inside of the turn. If negative, the left wheel is on the
   *          outside.
   * @param angle The angle through which the robot will rotate. If negative, robot traces the turning circle backwards.
   * @param immediateReturn If immediateReturn is true then the method returns immediately and your code MUST call
   *          updatePostion() when the robot has stopped. Otherwise, the robot position is lost.
   */
  public void turn(int turnRate, int angle, boolean immediateReturn);

  /**
   * Moves the NXT robot in a circular path with a specified radius.<br>
   * The center of the turning circle is on the right side of the robot if parameter radius is negative.<br>
   * Postcondition: Motor speeds are unpredictable.
   * 
   * @param radius of the circular path. If positive, the left wheel is on the inside of the turn. If negative, the left
   *          wheel is on the outside.
   */
  public void turn(float radius);

  /**
   * Moves the NXT robot in a circular path with a specified radius.<br>
   * The center of the turning circle is on the right side of the robot if parameter radius is negative.<br>
   * Robot will stop when total rotation equals angle. If angle is negative, robot will move travel backwards.
   * 
   * @param radius The radius of the turning circle.
   * @param angle The sign of the angle determines the direction of robot motion.
   */
  public void turn(float radius, int angle);

  /**
   * Moves the NXT robot in a circular path with a specified radius.<br>
   * The center of the turning circle is on the right side of the robot if parameter radius is negative.<br>
   * Robot will stop when total rotation equals angle. If angle is negative, robot will move travel backwards.
   * 
   * @param radius The radius of the turning circle.
   * @param angle The sign of the angle determines the direction of robot motion.
   * @param immediateReturn If immediateReturn is true then the method returns immediately and your code MUST call
   *          updatePostion() when the robot has stopped. Otherwise, the robot position is lost.
   */
  public void turn(float radius, int angle, boolean immediateReturn);

  /**
   * Reset traveled distance and rotated angle.
   */
  public void reset();

}
