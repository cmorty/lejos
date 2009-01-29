package lejos.navigation; 

/**
 *
 * @author owner
 */
public interface Pilot
{
 /**
   * Moves the NXT robot forward until stop() is called.
   *
   * @see Navigator#stop().
   */	
  public void forward();

  /**
   * Moves the NXT robot backward until stop() is called.
   *
   * @see Navigator#stop().
  */
  public void backward();

  /**
   * Halts the NXT robot and calculates new x, y coordinates.
   *
   * @see Navigator#forward().
   */
  public void stop();
/**
   *returns true iff the robot is moving under power
   */
  public boolean isMoving();

  /**
   *sets the of the robot, in wheel diameter units per second.
   */
  public void setRobotSpeed(float speed);
  /**
   * sets drive motor speed deg/sec
   * @param speed
   */
  public void setSpeed(float speed);

 /**
   * Moves the NXT robot a specific distance. A positive value moves it forward and
   * a negative value moves it backward. Method returns when movement is done.
   * @param distance The positive or negative distance to move the robot.
   */
  public void travel(float distance);

  /**
   * Moves the NXT robot a specific distance. A positive value moves it forwards and
   * a negative value moves it backwards.
   *  If immediateReturnis true, method returns immidiately and your code MUST call updatePostion()
   * when the robot has stopped.  Otherwise, the robot position is lost.
   * @param distance The positive or negative distance to move the robot, same units as _wheelDiameter
   * @param immediateReturn iff true, the method returns immediately, in which case the programmer <br>
   *  is responsible for calling updatePosition() before the robot moves again.
   */
  public void travel(float distance, boolean immediateReturn) ;
 /**
   * Rotates the NXT robot a specific number of degrees in a direction (+ or -).This
   * method will return once the rotation is complete.
   *
   * @param angle Angle to rotate in degrees. A positive value rotates left, a negative value right.
   */
  public void rotate(float angle);

  /**
   * Rotates the NXT robot a specific number of degrees in a direction (+ or -).
   * If immediateReturn is true, method returns immediately and your code MUST call updatePostion()
   * when the robot has stopped.  Otherwise, the robot position is lost.
   * @param angle Angle to rotate in degrees. A positive value rotates left, a negative value right.
   * @param immediateReturn iff true, the method returns immediately, in which case the programmer <br>
   *  is responsible for calling updatePosition() before the robot moves again.
   */
  public void rotate(float angle, boolean immediateReturn);

  /**
   * @return the angle of rotation of the robot since last call to reset ;
   */
  public int getAngle() ;

  /**
   * @return distance traveled since last reset.
   **/
 public float getTravelDistance() ;
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
   */
  public void steer(int turnRate) ;
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
  public void steer(int turnRate, int angle) ;
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
   * @param immediateReturn If true this method returns immediately.
   */
   public void steer(int turnRate, int angle, boolean immediateReturn) ;
/**
   * Moves the NXT robot in a circular path with a specified radius. <br>
   * The center of the turning circle is on the right side of the robot iff parameter radius is negative;  <br>
   * Postcondition:  Motor speeds are unpredictable.
   * @param radius of the circular path. If positive, the left wheel is on the inside of the turn.  If negative, the left wheel is on the outside.
   */
  public void turn(float radius);
 /**
   * Moves the NXT robot in a circular arc through the specificd angle;  <br>
   * The center of the turning circle is on the right side of the robot iff parameter radius is negative.
   * Robot will stop when total rotation equals angle. If angle is negative, robot will move travel backwards.
   * @param radius radius of the turning circle
   * @param angle The sign of the angle determines the direction of robot motion
   */
  public void turn(float radius, int angle);
 /**
   *  Move in a circular arc with specified radius; the center of the turning circle <br>
 * is on the right side of the robot if the radius is negative.
   * @param radius
   * @param angle
   * @param immediateReturn
   */
  public void turn(float radius, int angle, boolean immediateReturn);
  /**
   * reset the origin of travel distance and rotation angle
   */
  public void reset();
   
}
