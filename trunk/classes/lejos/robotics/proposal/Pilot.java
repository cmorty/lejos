package lejos.robotics.proposal;

/**
 * Lawrie criticisms:
 * Q1. I think you have far too many methods in the Pilot interface, which will stop it being usable for a wide variety of 
 * mobile robots. We will lose one of the major reasons for doing this restructure if we keep all this stuff like steer and 
 * arc in the Pilot interface. I would put forward, stop, isMoving and probably nothing else in the basic Pilot interface 
 * and introduce either a hierarchy of interfaces or separate optional interfaces.
 * 
 * Obviously a pilot with just this is not useful for 2D navigation, but some pilots can do rotate and some cannot, 
 * and can do steer (or arc) instead. Most can probably do backward, so we might consider that in the basic interface 
 * rather than making it on option.
 * 
 * A1. If we had one method, move(Movement) does that help? Do we lose the ability to differentiate what Pilots can
 * perform which movements.
 * 
 * Q2. Renaming rotate as setHeading for pilots is confusing and makes no sense. Pilots do not know the heading. 
 * Similarly having an interface called getHeading is wrong. The current method that gets the angle turned since 
 * the last reset is different, and mainly exists to support updatePostion in SimpleNavigator.

 * 
 * 
 * The Pilot is theoretically there to perform vector movements, such as 
forward(), travel(100) and setHeading(90). The vehicle type should really be 
largely inconsequential to this interface (of course *implementations* like 
DifferentialPilot are vehicle specific). After all, robots are really just 
objects with vectors. As long as Pilot handles different vectors (including 
arc, which isn't a vector per se) we should be good to handle any moving 
object, no matter what the design specifics. I think we should make Pilot 
heavily vector oriented in the Javadoc language and method names.

 * @author NXJ Team
 */
public interface Pilot {

	/**
	 * Adds a PilotListener that will be notified of all Pilot movement events.
	 * @param p
	 */
	public void addPilotListener(PilotListener p);
	
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
	   *
	   * @return The movement it just achieved?
	   */
	   public Movement stop();

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
	   * There is no guarantee that after you call these methods it is actually capable of completing the full movement.
	   *  Many things might prevent the movement from being completed, and the API should be informed about what 
	   *  *actual* movement occured, to the best of the Pilot's ability. For example, if the Pilot is a walker, 
	   *  perhaps it can only move forward in increments of 5 cm. So if you tell it to move 17 cm, perhaps it only moves 
	   *  15 cm and then returns. Or maybe it can only rotate in increments of 15. It should report the actual movement 
	   *  it completed.
	   *  Note: If you call setHeading() on a Pilot that is omnidirectional (and hence doesn't need to physically 
	   *  rotate) returns the same movement angle you specified.
	   * @param vector
	   * @return
	   */
	  public Movement move(Movement vector);
	  
	  /**
	   * Moves the NXT robot a specific distance. A positive value moves it forward and a negative value moves it backward.
	   * Method returns when movement is done.
	   * 
	   * @param distance The positive or negative distance to move the robot.
	   */
	  public Movement travel(float distance);

	  /**
	   * Moves the NXT robot a specific distance. A positive value moves it forward and a negative value moves it backward.
	   * @param distance The positive or negative distance to move the robot, in wheel diameter units.
	   * @param immediateReturn If immediateReturn is true then the method returns immediately.
	   */
	  public Movement travel(float distance, boolean immediateReturn);

	  /**
	   * Rotates the NXT robot the specified number of degrees; direction determined by the sign of the parameter.
	   * Method returns when rotation is done.
	   * 
	   * @param angle The angle to rotate in degrees. A positive value rotates left, a negative value right (clockwise).
	   */
	  public Movement changeHeading(float angle);

	  /**
	   * Rotates the NXT robot the specifed number of degress; direction determined by the sign of the parameter.
	   * Motion stops  when rotation is done.
	   * 
	   * @param angle The angle to rotate in degrees. A positive value rotates left, a negative value right (clockwise).
	   * @param immediateReturn If immediateReturn is true then the method returns immediately
	   */
	  public Movement changeHeading(float angle, boolean immediateReturn);

	  public float getHeading();

	  /**
	   * Starts the robot moving along a curved path. This method is similar to the
	   * {@link #arc(float radius)} method except it uses a ratio of motor
	   * speeds to determine the curvature of the path and therefore has the ability to drive straight. This makes
	   * it usrful for line following applications.
	   * <p>
	   * The <code>turnRate</code> specifies the sharpness of the turn, between -200 and +200.<br>
	   * The <code>turnRate</code> is used to calculate the  ratio of inner wheel speed to outer wheel speed <b>as a percent</b>.<br>
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
	  public void steer(int turnRate);

	  /**
	   * Moves the robot along a curved path through a specified turn angle. This method is similar to the
	   * {@link #arc(float radius , int angle)} method except it uses a ratio of motor
	   * speeds to determine the curvature of the  path and therefore has the ability to drive straight. This makes
	   * it useful for line following applications. This method does not return until the robot has
	   * completed moving <code>angle</code> degrees along the arc.<br>
	   * The <code>turnRate</code> specifies the sharpness of the turn, between -200 and +200.<br>
	   * For details about how this paramet works.See {@link #steer(int turnRate) }
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
	  public Movement steer(int turnRate, int angle);

	  /**
	   * Moves the robot along a curved path for a specified angle of rotation. This method is similar to the
	   * {@link #arc(float radius, int angle, boolean immediateReturn)} method except it uses a ratio of motor
	   * speeds to speeds to determine the curvature of the path and therefore has the ability to drive straight. 
	   * This makes it useful for line following applications. This method has the ability to return immediately
	   * by using the <code>immediateReturn</code> parameter set to <b>true</b>.
	   *
	   * <p>
	   * The <code>turnRate</code> specifies the sharpness of the turn, between -200 and +200.<br>
	   * For details about how this paramet works, see {@link #steer(int turnRate) }
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
	  public Movement steer(int turnRate, int angle, boolean immediateReturn);

	  /**
	   * Starts the  NXT robot moving along an arc with a specified radius.
	   * <p>
	   * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
	   * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
	   * If <code>radius</code> is zero, the robot rotates in place.
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
	   * Moves the NXT robot along an arc with a specified radius and  angle,
	   * after which the robot stops moving. This method does not return until the robot has
	   * completed moving <code>angle</code> degrees along the arc.
	   * <p>
	   * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
	   * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
	   * If <code>radius</code> is zero, is zero, the robot rotates in place.
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
	   * @see #travelArc(float, float)
	   */
	  public Movement arc(float radius, int angle);

	  /**
	   * Moves the NXT robot along an arc with a specified radius and  angle,
	   * after which the robot stops moving. This method has the ability to return immediately
	   * by using the <code>immediateReturn</code> parameter. 
	   * <p>
	   * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
	   * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
	   * If <code>radius</code> is zero, is zero, the robot rotates in place.
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
	   * @see #travelArc(float, float, boolean)
	   */
	  public Movement arc(float radius, int angle, boolean immediateReturn);

	  /**
	   * Moves the NXT robot a specified distance along an arc mof specified radius,
	   * after which the robot stops moving. This method does not return until the robot has
	   * completed moving <code>distance</code> along the arc. The units (inches, cm) for <code>distance</code> 
	   * must be the same as the units used for <code>radius</code>.
	   * <p>
	   * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
	   * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
	   * If <code>radius</code> is zero, the robot rotates in place
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
	  public Movement travelArc(float radius, float distance);

	  /**
	   * Moves the NXT robot a specified distance along an arc of specified radius,
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
	  public Movement travelArc(float radius, float distance, boolean immediateReturn);
	
}
