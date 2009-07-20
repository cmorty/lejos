package lejos.robotics.proposal;

/**
 * An enhanced Pilot that is capable of traveling in arcs.
 * @author NXJ Team
 *
 */
public interface ArcPilot extends Pilot {
	
	/**
	 * The minimum steering radius this vehicle is capable of when traveling in an arc.
	 * Theoretically this should be identical for both forward and reverse travel. In practice?
	 * @return
	 */
	public int getMinRadius();
	
	/**
	 * 
	 * @param radius A value between 0 (differential steering) and infinity (not able to do sharp turns) 
	 */
	public void setMinRadius(int radius);
	
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
