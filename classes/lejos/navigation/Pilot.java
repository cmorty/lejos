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
   * This method is similar to the {@link #arc(float)} method except it uses a ratio of motor
   * speeds to follow a curved path and therefore has the ability to drive straight. This makes
   * it ideal for line following applications.
   * <p>
   * The <code>turnRate</code> specifies the sharpness of the turn, between -200 and +200.<br>
   * The <code>turnRate</code> determines the ratio of inner wheel speed to outer wheel speed <b>as a percent</b>.<br>
   * <I>Formula:</I> <code>ratio = 100 - abs(turnRate)</code>.<br>
   * When the ratio is negative, the outer and inner wheels rotate in
   * opposite directions.
   * <p>
   * If <code>turnRate</code> is positive, the center of the turning circle is on the left side of the robot.<br>
   * If <code>turnRate</code> is negative, the center of the turning circle is on the right side of the robot.<br>
   * If <code>turnRate</code> is zero, ...
   * <p>
   * Examples:
   * <UL>
   * <LI><code>steer(-200)</code> -> ...
   * <LI><code>steer(-100)</code> -> ...
   * <LI><code>steer(-25)</code> -> ...
   * <LI><code>steer(0)</code> -> ...
   * <LI><code>steer(25)</code> -> the inner wheel turns at 75% of the speed of the outer wheel
   * <LI><code>steer(100)</code> -> the inner wheel stops and the outer wheel is at 100 percent
   * <LI><code>steer(200)</code> -> the inner wheel turns at the same speed as the outer wheel - a zero radius turn.
   * </UL>
   * <p>
   * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param turnRate If positive, the left side of the robot is on the inside of the turn. If negative, 
   * the left side is on the outside.
   */
  public void steer(int turnRate);

  /**
   * This method is similar to the {@link #arc(float, int)} method except it uses a ratio of motor
   * speeds to follow a curved path and therefore has the ability to drive straight. This makes
   * it ideal for line following applications. This method does not return until the robot has
   * completed moving <code>angle</code> degrees along the arc.
   * <p>
   * The <code>turnRate</code> specifies the sharpness of the turn, between -200 and +200.<br>
   * The <code>turnRate</code> determines the ratio of inner wheel speed to outer wheel speed <b>as a percent</b>.<br>
   * <I>Formula:</I> <code>ratio = 100 - abs(turnRate)</code>.<br>
   * When the ratio is negative, the outer and inner wheels rotate in
   * opposite directions.
   * <p>
   * If <code>turnRate</code> is positive, the center of the turning circle is on the left side of the robot.<br>
   * If <code>turnRate</code> is negative, the center of the turning circle is on the right side of the robot.<br>
   * If <code>turnRate</code> is zero, ...
   * <p>
   * Examples:
   * <UL>
   * <LI><code>steer(-200)</code> -> ...
   * <LI><code>steer(-100)</code> -> ...
   * <LI><code>steer(-25)</code> -> ...
   * <LI><code>steer(0)</code> -> ...
   * <LI><code>steer(25)</code> -> the inner wheel turns at 75% of the speed of the outer wheel
   * <LI><code>steer(100)</code> -> the inner wheel stops and the outer wheel is at 100 percent
   * <LI><code>steer(200)</code> -> the inner wheel turns at the same speed as the outer wheel - a zero radius turn.
   * </UL>
   * <p>
   * The robot will stop when the degrees it has moved along the arc equals <code>angle</code>.<br> 
   * If <code>angle</code> is positive, the robot will move travel forwards.<br>
   * If <code>angle</code> is negative, the robot will move travel backwards.
   * If <code>angle</code> is zero, the robot will not move and the method returns immediately.
   * <p>
   * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param turnRate If positive, the left side of the robot is on the inside of the turn. If negative, 
   * the left side is on the outside.
   * @param angle The angle through which the robot will rotate. If negative, robot traces the turning circle backwards.
   */
  public void steer(int turnRate, int angle);

  /**
   * This method is similar to the {@link #arc(float, int)} method except it uses a ratio of motor
   * speeds to follow a curved path and therefore has the ability to drive straight. This makes
   * it ideal for line following applications. This method has the ability to return immediately
   * by using the <code>immediateReturn</code> parameter. <b>Warning: Your code <i>must</i> call
   * updatePostion() when the robot has stopped, otherwise, the robot position is lost.</b>
   * <p>
   * The <code>turnRate</code> specifies the sharpness of the turn, between -200 and +200.<br>
   * The <code>turnRate</code> determines the ratio of inner wheel speed to outer wheel speed <b>as a percent</b>.<br>
   * <I>Formula:</I> <code>ratio = 100 - abs(turnRate)</code>.<br>
   * When the ratio is negative, the outer and inner wheels rotate in
   * opposite directions.
   * <p>
   * If <code>turnRate</code> is positive, the center of the turning circle is on the left side of the robot.<br>
   * If <code>turnRate</code> is negative, the center of the turning circle is on the right side of the robot.<br>
   * If <code>turnRate</code> is zero, ...
   * <p>
   * Examples:
   * <UL>
   * <LI><code>steer(-200)</code> -> ...
   * <LI><code>steer(-100)</code> -> ...
   * <LI><code>steer(-25)</code> -> ...
   * <LI><code>steer(0)</code> -> ...
   * <LI><code>steer(25)</code> -> the inner wheel turns at 75% of the speed of the outer wheel
   * <LI><code>steer(100)</code> -> the inner wheel stops and the outer wheel is at 100 percent
   * <LI><code>steer(200)</code> -> the inner wheel turns at the same speed as the outer wheel - a zero radius turn.
   * </UL>
   * <p>
   * The robot will stop when the degrees it has moved along the arc equals <code>angle</code>.<br> 
   * If <code>angle</code> is positive, the robot will move travel forwards.<br>
   * If <code>angle</code> is negative, the robot will move travel backwards.
   * If <code>angle</code> is zero, the robot will not move and the method returns immediately.
   * <p>
   * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param turnRate If positive, the left side of the robot is on the inside of the turn. If negative, 
   * the left side is on the outside.
   * @param angle The angle through which the robot will rotate. If negative, robot traces the turning circle backwards.
   * @param immediateReturn If immediateReturn is true then the method returns immediately and your code MUST call
   *          updatePostion() when the robot has stopped. Otherwise, the robot position is lost.
   */
  public void steer(int turnRate, int angle, boolean immediateReturn);

  /**
   * Moves the NXT robot along an arc with a specified radius. This method returns immediately
   * after the motion has begun.
   * <p>
   * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
   * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
   * If <code>radius</code> is zero, ...
   * <p>
   * The <code>arc(float)</code> method <b>can not drive a straight line</b>, which makes
   * it impractical for line following. A better solution for line following is  
   * {@link #steer(int)}, which uses proportional steering and can drive straight lines and arcs.
   * <p>
   * Postcondition: Motor speeds are unpredictable.
   * <p>
   * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
   *          side of the robot is on the outside of the turn.
   * @see #steer(int)
   */
  public void arc(float radius);

  /**
   * Moves the NXT robot along an arc with a specified radius and a predescribed angle,
   * after which the robot stops moving. This method does not return until the robot has
   * completed moving <code>angle</code> degrees along the arc.
   * <p>
   * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
   * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
   * If <code>radius</code> is zero, ...
   * <p>
   * The <code>arc(float)</code> method <b>can not drive a straight line</b>, which makes
   * it impractical for line following. A better solution for line following is  
   * {@link #steer(int)}, which uses proportional steering and can drive straight lines and arcs.
   * <p>
   * Robot will stop when the degrees it has moved along the arc equals <code>angle</code>.<br> 
   * If <code>angle</code> is positive, the robot will move travel forwards.<br>
   * If <code>angle</code> is negative, the robot will move travel backwards.
   * If <code>angle</code> is zero, the robot will not move and the method returns immediately.
   * <p>
   * Postcondition: Motor speeds are unpredictable.
   * <p>
   * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
   *          side of the robot is on the outside of the turn.
   * @param angle The sign of the angle determines the direction of robot motion. Positive drives the robot forward, negative drives it backward.
   * @see #steer(int, int)
   * 
   */ 
  public void arc(float radius, int angle);

  /**
   * Moves the NXT robot along an arc with a specified radius and a predescribed angle,
   * after which the robot stops moving. This method has the ability to return immediately
   * by using the <code>immediateReturn</code> parameter. <b>Warning: Your code <i>must</i> call
   * updatePostion() when the robot has stopped, otherwise, the robot position is lost.</b>
   * <p>
   * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
   * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
   * If <code>radius</code> is zero, ...
   * <p>
   * The <code>arc(float, int, boolean)</code> method <b>can not drive a straight line</b>, which makes
   * it impractical for line following. A better solution for line following is  
   * {@link #steer(int, int, boolean)}, which uses proportional steering and can drive straight lines and arcs.
   * <p>
   * The robot will stop when the degrees it has moved along the arc equals <code>angle</code>.<br> 
   * If <code>angle</code> is positive, the robot will move travel forwards.<br>
   * If <code>angle</code> is negative, the robot will move travel backwards.
   * If <code>angle</code> is zero, the robot will not move and the method returns immediately.
   * <p>
   * Postcondition: Motor speeds are unpredictable.
   * <p>
   * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
   *          side of the robot is on the outside of the turn.
   * @param angle The sign of the angle determines the direction of robot motion. Positive drives the robot forward, negative drives it backward.
   * @param immediateReturn If immediateReturn is true then the method returns immediately and your code MUST call
   *          updatePostion() when the robot has stopped. Otherwise, the robot position is lost.
   * @see #steer(int, int, boolean)
   * 
   */
  public void arc(float radius, int angle, boolean immediateReturn);

  /**
   * Moves the NXT robot a specified distance along an arc,
   * after which the robot stops moving. This method does not return until the robot has
   * completed moving <code>distance</code> along the arc. The units (inches, cm) for <code>distance</code> 
   * should be the same as the units used for <code>radius</code>.
   * <p>
   * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
   * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
   * If <code>radius</code> is zero, ...
   * <p>
   * The <code>travelArc(float, float)</code> method <b>can not drive a straight line</b>, which makes
   * it impractical for line following. A better solution for line following is  
   * {@link #steer(int)}, which uses proportional steering and can drive straight lines and arcs.
   * <p>
   * The robot will stop when it has moved along the arc <code>distance</code> units.<br> 
   * If <code>distance</code> is positive, the robot will move travel forwards.<br>
   * If <code>distance</code> is negative, the robot will move travel backwards.
   * If <code>distance</code> is zero, the robot will not move and the method returns immediately.
   * <p>
   * Postcondition: Motor speeds are unpredictable.
   * <p>
   * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
   *          side of the robot is on the outside of the turn.
   * @param distance to travel, in same units as <code>radius</code>. The sign of the distance determines the direction of robot motion. Positive drives the robot forward, negative drives it backward.
   * @see #steer(int, int)
   * @see #arc(float, int)
   * 
   */ 
  public void travelArc(float radius, float distance);
  
  /**
   * Moves the NXT robot a specified distance along an arc,
   * after which the robot stops moving. This method has the ability to return immediately
   * by using the <code>immediateReturn</code> parameter.  
   * The units (inches, cm) for <code>distance</code> should be the same as the units used for <code>radius</code>.
   * <b>Warning: Your code <i>must</i> call updatePostion() when the robot has stopped, 
   * otherwise, the robot position is lost.</b>
   * 
   * <p>
   * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
   * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
   * If <code>radius</code> is zero, ...
   * <p>
   * The <code>travelArc(float, float, boolean)</code> method <b>can not drive a straight line</b>, which makes
   * it impractical for line following. A better solution for line following is  
   * {@link #steer(int, int, boolean)}, which uses proportional steering and can drive straight lines and arcs.
   * <p>
   * The robot will stop when it has moved along the arc <code>distance</code> units.<br> 
   * If <code>distance</code> is positive, the robot will move travel forwards.<br>
   * If <code>distance</code> is negative, the robot will move travel backwards.
   * If <code>distance</code> is zero, the robot will not move and the method returns immediately.
   * <p>
   * Postcondition: Motor speeds are unpredictable.
   * <p>
   * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
   *          side of the robot is on the outside of the turn.
   * @param distance to travel, in same units as <code>radius</code>. The sign of the distance determines the direction of robot motion. Positive drives the robot forward, negative drives it backward.
   @param immediateReturn If immediateReturn is true then the method returns immediately and your code MUST call
   *        updatePostion() when the robot has stopped. Otherwise, the robot position is lost. 
   * @see #steer(int, int, boolean)
   * @see #arc(float, int, boolean)
   * 
   */ 
  public void travelArc(float radius, float distance, boolean immediateReturn);
  
  /**
   * Reset traveled distance and rotated angle.
   */
  public void reset();

}
