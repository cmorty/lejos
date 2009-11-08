package lejos.robotics.navigation;



/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * The Pilot interface forms a common set of functions an implementation must offer in order to be used by higher level
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
public interface Pilot
{

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
   * Sets the movement speed of the robot.
   * 
   * @param speed The speed in wheel diameter units per second.
   */
  public void setMoveSpeed(float speed);

  /**
   * returns the movement speed of the robot in wheel diameter units per second.
   * @return the movement speed of the robot in wheel diameter units per second.
   */
  public float getMoveSpeed();

  /**
   * Returns the maximal movement speed of the robot depending on battery voltage
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
   * Returns the turning speed of the robot in degrees per second.
   * @return the turning speed of the robot in degree per second.
   */
  public float getTurnSpeed();

  /**
   * Returns the maximum turning speed depending on battery voltage.
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
   * angle of rotation of the robot since last call to reset.  A negative
   * is a clockwise change of direction - the robot turne to the right.
   * @return the angle of rotation of the robot since last call to reset.
   */
  public float getAngle();

  /**
   * distance traveled  since the last call to reset.
   * @return the distance traveled  since last call to reset
   **/
  public float getTravelDistance();

  /**
   * Starts the robot moving along a curved path. This method is similar to the
   * {@link #arc(float radius)} method except it uses the <code> turnRate</code> parameter
   * do determine the curvature of the path and therefore has the ability to drive straight. This makes
   * it useful for line following applications.
   * <p>
   * The <code>turnRate</code> specifies the sharpness of the turn.  Use values  between -200 and +200.<br>
   * A negative value means that the robot turns to the left, and a positive value means that it turns to the right.
   * If the robot is facing the top of the page, a left turn is an arc like this: <b> ) </b> .
   * In robots that use 2 wheel differential steering, this parameter determines the  ratio of inner wheel speed to outer wheel speed <b>as a percent</b>.<br>
   * <I>Formula:</I> <code>ratio = 100 - abs(turnRate)</code>.<br>
   * When the ratio is negative, the outer and inner wheels rotate in
   * opposite directions.
   * <p>
   * If <code>turnRate</code> is positive, the center of the turning circle is on the left side of the robot.<br>
   * If <code>turnRate</code> is negative, the center of the turning circle is on the right side of the robot.<br>
   * If <code>turnRate</code> is zero, the robot travels in a straight line
   * <p>
   * Examples of how the formula works:
   * <UL>
   * <LI><code>steer(0)</code> -> inner and outer wheels turn at the same speed, travel  straight
   * <LI><code>steer(25)</code> -> the inner wheel turns at 75% of the speed of the outer wheel, turn left
   * <LI><code>steer(100)</code> -> the inner wheel stops and the outer wheel is at 100 percent, turn left
   * <LI><code>steer(200)</code> -> the inner wheel turns at the same speed as the outer wheel - a zero radius turn.
   * </UL>
   * <p>
   * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param turnRate If positive, the left side of the robot is on the inside of the turn. If negative, 
   * the left side is on the outside.
   */
  public void steer(float turnRate);

  /**
   * Moves the robot along a curved path through a specified turn angle. This method is similar to the
   * {@link #arc(float radius , float angle)} method except it uses a ratio of motor
   * speeds to determine the curvature of the  path and therefore has the ability to drive straight. This makes
   * it useful for line following applications. This method does not return until the robot has
   * completed moving <code>angle</code> degrees along the arc.<br>
   * The <code>turnRate</code> specifies the sharpness of the turn. Use values  between -200 and +200.<br>
   * For details about how this parameter works.See {@link #steer(float turnRate) }
   * <p>
   * The robot will stop when its heading has changed by the amount of the  <code>angle</code> parameter.<br>
   * If <code>angle</code> is positive, the robot will move in the direction that increases its heading (it turns left).<br>
   * If <code>angle</code> is negative, the robot will move in the directin that decreases its heading (turns right).<br>
   * If <code>angle</code> is zero, the robot will not move and the method returns immediately.
   * <p> The sign of the turn rate and the sign of the angle together determine if the robot will move forward
   * or backward. Assuming the robot is heading toward the top of the page. Then a positive turn rate means the
   * arc looks like this: <b> )</b>  .  If the angle is positive, the robot moves forward to increase its heading angle.
   * If negative,  it moves backward to decrease the heading.  <br> But if the turn rate is negative,
   * the arc looks like this: <b> (  </b> .So a positive angle (increase in heading) means the robot
   * moves backwards, while a negative angle means the robot moves forward to decrease
   * its heading.
   *
   * <p>
   * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param turnRate If positive, the left side of the robot is on the inside of the turn. If negative, 
   * the left side is on the outside.
   * @param angle The angle through which the robot will rotate. If negative, the robot will move in the directin that decreases its heading.
   */
  public void steer(float turnRate, float angle);

  /**
   * Moves the robot along a curved path for a specified angle of rotation. This method is similar to the
   * {@link #arc(float radius, float angle, boolean immediateReturn)} method except it uses the <code> turnRate()</code>
   * parameter to determine the curvature of the path and therefore has the ability to drive straight.
   * This makes it useful for line following applications. This method has the ability to return immediately
   * by using the <code>immediateReturn</code> parameter set to <b>true</b>.
   *
   * <p>
   * The <code>turnRate</code> specifies the sharpness of the turn. Use values between -200 and +200.<br>
   * For details about how this parameter works, see {@link #steer(float turnRate) }
   * <p>
    * The robot will stop when its heading has changed by the amount of the  <code>angle</code> parameter.<br>
   * If <code>angle</code> is positive, the robot will move in the direction that increases its heading (it turns left).<br>
   * If <code>angle</code> is negative, the robot will move in the direction that decreases its heading (turns right).<br>
   * If <code>angle</code> is zero, the robot will not move and the method returns immediately.<br>
   * For more details about this parameter, see {@link #steer(float turnRate, float angle)}
   * <p>
   * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param turnRate If positive, the left side of the robot is on the inside of the turn. If negative, 
   * the left side is on the outside.
   * @param angle The angle through which the robot will rotate. If negative, robot traces the turning circle backwards.
   * @param immediateReturn If immediateReturn is true then the method returns immediately.
   */
  public void steer(float turnRate, float angle, boolean immediateReturn);

  /**
   * Starts the  NXT robot moving along an arc with a specified radius. 
   * While a negative radius is not allowed in mathematics, this method uses the sign bit of the
   * <code>radius</code> parameter to determine the location of the center of the
   * turning circle relative to the robot heading.
   * <p>
   * If the sign of <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.
   * If the robot is travelling toward the top of the page, the arc looks like this  )
   * If the sign of  <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.
   * so the arc looks like this  <b> ( </b> <br>
   * If <code>radius</code> is zero, the robot rotates to the left in place .
   * <p>
   * The <code>arc(float)</code> method <b>can not drive a straight line</b>, which makes
   * it impractical for line following. A better solution for line following is  
   * {@link #steer(float)}, which uses proportional steering and can drive straight lines and arcs.
   * <p>
   * Postcondition: Motor speeds are unpredictable.
   * <p>
   * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
   *          side of the robot is on the outside of the turn.
   * @see #steer(float)
   */
  public void arc(float radius);

  /**
   * Moves the NXT robot along an arc with a specified radius and  angle,
   * after which the robot stops moving. This method does not return until the robot has
   * completed moving <code>angle</code> degrees along the arc, so its heading changes
   * by that amount.
   * <p>
   * While a negative radius is not allowed in mathematics, this method uses the sign bit of the
   * <code>radius</code> parameter to determine the location of the center of the
   * turning circle relative to the robot heading.
   * <p>
   * If the sign of <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.
   * If the robot is travelling toward the top of the page, the arc looks like this <b>) </b>
   * If the sign of  <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.
   * so the arc looks like this   <b>( </b> <br>
   * If <code>radius</code> is zero, is zero, the robot rotates in place; the direction of
   * rotation is determined by the <code>angle</code>.
   * <p>
   * The <code>arc(float, float, boolean)</code> method <b>can not drive a straight line</b>, which makes
   * it impractical for line following. A better solution for line following is
   * {@link #steer(float,float)}, which uses proportional steering and can drive straight lines and arcs.
   * <p>
   * The robot will stop when the degrees it has moved along the arc equals <code>angle</code>.<br>
   * If <code>angle</code> is positive, the robot will move so is heading - the direction it is facing -  increases.<br>
   * If <code>angle</code> is negative, the robot heading will decrease.
   * If <code>angle</code> is zero, the robot will not move and the method returns immediately.
   * The signs of the radius and angle together determine if the robot will move forwad or backward in the same way
   * that the signs of the <code>turnRate</code> and <code>angle</code> parameters work in
   * {@link #steer(float,float)}.
   * A positive radius causes the robot to move in an arc like this <b>) </b> -  but if the sign of the angle is negative,
   * it will travel backwards to decrease its heading.<br>
   * A radius with a negative sign travels an arc like this <b>( </b>and if the angle is positive, it will travel backward
   * in order to  increase its heading.   In summary, if the radius and angle have
   * the same sign, the robot will move forward.  If they have opposite signs, the robot will move backward.

   * <p>
   * Postcondition: Motor speeds are unpredictable.
   * <p>
   * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
   *          side of the robot is on the outside of the turn.
   * @param angle The sign of the angle determines the direction of robot motion. Positive drives the robot forward, negative drives it backward.
   * @see #steer(float, float)
   * @see #travelArc(float, float)
   */
  public void arc(float radius, float angle);

  /**
   * Moves the NXT robot along an arc with a specified radius and  angle,
   * after which the robot stops moving. This method has the ability to return immediately
   * by using the <code>immediateReturn</code> parameter. 
   * <p>
   * While a negative radius is not allowed in mathematics, this method uses the sign bit of the
   * <code>radius</code> parameter to determine the location of the center of the
   * turning circle relative to the robot heading.
   * <p>
   * If the sign of <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.
   * If the robot is travelling toward the top of the page, the arc looks like this <b> )</b>
   * If the sign of  <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.
   * so the arc looks like this  <b>( </b> <br>
   * If <code>radius</code> is zero, is zero, the robot rotates in place; the direction of
   * rotation is determined by the <code>angle</code>.
   * <p>
   * The <code>arc(float, float, boolean)</code> method <b>can not drive a straight line</b>, which makes
   * it impractical for line following. A better solution for line following is  
   * {@link #steer(float, float, boolean)}, which uses proportional steering and can drive straight lines and arcs.
   * <p>
   * The robot will stop when the degrees it has moved along the arc equals <code>angle</code>.<br> 
   * If <code>angle</code> is positive, the robot will move so is heading - the direction it is facing -  increases.<br>
   * If <code>angle</code> is negative, the robot heading will decrease.
   * If <code>angle</code> is zero, the robot will not move and the method returns immediately.
   * The sign bits of the radius and angle together determine if the robot will move forwad or backward.
   * A positive radius causes the robot to move in an arc like this <b> ) </b>  -  but if the sign of the angle is negative,
   * it will travel backwards to decrease its heading.<br>
   * A radius with a negative sign travels an arc like this <b>( </b>and if the angle is positive, it will travel backward
   * in order to  increase its heading.   In summary, if the radius and angle have
   * the same sign, the robot will move forward.  If they have opposite signs, the robot will move backward.
   * <p>
   * Postcondition: Motor speeds are unpredictable.
   * <p>
   * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
   * 
   * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
   *          side of the robot is on the outside of the turn.
   * @param angle The sign of the angle determines the direction of robot heading change .
   * @param immediateReturn If immediateReturn is true then the method returns immediately.
   * @see #steer(float, float, boolean)
   * @see #travelArc(float, float, boolean)
   */
  public void arc(float radius, float angle, boolean immediateReturn);

  /**
   * Moves the NXT robot a specified distance along an arc mof specified radius,
   * after which the robot stops moving. This method does not return until the robot has
   * completed moving <code>distance</code> along the arc. The units (inches, cm) for <code>distance</code> 
   * must be the same as the units used for <code>radius</code>.  The <code>radius</code> has the same
   * meaning as in {@link #arc(float)}
   * <p>
   * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
   * If the sign of  <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
   * If <code>radius</code> is zero, the robot rotates in place
   * <p>
   * The <code>travelArc(float, float)</code> method <b>can not drive a straight line</b>, which makes
   * it impractical for line following. A better solution for line following is  
   * {@link #steer(float)}, which uses proportional steering and can drive straight lines and arcs.
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
   * @see #steer(float, float)
   * @see #arc(float, float)
   * 
   */
  public void travelArc(float radius, float distance);

  /**
   * Moves the NXT robot a specified distance along an arc of specified radius,
   * after which the robot stops moving. This method has the ability to return immediately
   * by using the <code>immediateReturn</code> parameter.  
   * The units (inches, cm) for <code>distance</code> should be the same as the units used for <code>radius</code>.
   * The <code>radius</code> has the same
   * meaning as in {@link #arc(float)}
   * <p>
   * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
   * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
   * If <code>radius</code> is zero, ...
   * <p>
   * The <code>travelArc(float, float, boolean)</code> method <b>can not drive a straight line</b>, which makes
   * it impractical for line following. A better solution for line following is  
   * {@link #steer(float, float, boolean)}, which uses proportional steering and can drive straight lines and arcs.
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
  * @param immediateReturn If immediateReturn is true then the method returns immediately.
   * @see #steer(float, float, boolean)
   * @see #arc(float, float, boolean)
   * 
   */
  public void travelArc(float radius, float distance, boolean immediateReturn);

  /**
   * Reset traveled distance and rotated angle.
   */
  public void reset();
}
