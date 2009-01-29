package lejos.navigation;

import lejos.nxt.Motor;

/**
 * The TachoPilot class is a software abstraction of the Pilot mechanism of a NXT robot. It contains methods to control robot
 * movements: travel forward or backward in a straight line or a circular path or rotate to a new direction.<br>
 * Note: this class will only work with two independently controlled  motors to steer differentially, so it can
 * rotate within its own footprint (i.e. turn on one spot).<br>
 * It can be used with robots that have reversed motor design: the robot moves in the direction opposite to the the
 * direction of motor rotation. Uses the Motor class, which regulates motor speed using the NXT motor's built in
 * tachometer.<br>
 * Some methods optionally return immediately so the thread that called the method can monitor sensors and call stop()
 * if necessary.<br>
 * Uses the smoothAcceleration property of Motors to improve motor synchronization when starting a movement. Example:
 * <p>
 * <code><pre>
 * Pilot pilot = new Pilot(2.1f,4.4f,Motor.A, Motor.C,true);  parameters inches
 * pilot.setRobotSpeed(10);// inches/sec
 * pilot.travel(12);
 * pilot.rotate(-90);
 * pilot.travel(-12,true);
 * while(pilot.isMoving())Thread.yield();
 * pilot.rotate(-90);
 * pilot.rotateTo(270);
 * pilot.steer(-50,180,true);
 * while(pilot.isMoving())Thread.yield();
 * pilot.steer(100);
 * try{Thread.sleep(1000);}
 * catch(InterruptedException e){}
 * pilot.stop();
 * </pre></code>
 * </p>
 **/
public class TachoPilot implements Pilot
{

  /**
   * Left motor
   */
  protected Motor _left;
  /**
   * Right motor
   */
  protected Motor _right;
  /**
   * Motor degrees per unit of travel
   */
  public final float _degPerDistance;
  /**
   * Motor revolutions for 360 degree rotation of robot (motors running in opposite directions). Calculated from wheel
   * diameter and track width. Used by rotate() and steer() methods.
   **/
  private final float _turnRatio;
  /**
   * speed of robot in wheel diameter units per sec
   */
  protected float _robotSeed;
  /**
   * Motor speed degrees per second. Used by all methods that cause movement.
   */
  protected int _motorSpeed = 360;
  /**
   * Motor rotation forward makes robot move forward if parity == 1.
   */
  private byte _parity = 1;
  /**
   * If true, motor speed regulation is turned on.
   */
  private boolean _regulating = true;
  /**
   * Distance between wheels. Used in steer().
   */
  public final float _trackWidth;
  /**
   * Diameter of tires.
   */
  public final float _wheelDiameter;
  /**
   * A correction factor to compensate for small differences in wheel size (causing the robot to "drift" to one side).
   */
  private float _driftCorrection = 1;

  /**
   * Allocates a Pilot object, and sets the physical parameters of the NXT robot.<br>
   * Assumes Motor.forward() causes the robot to move forward.
   * 
   * @param wheelDiameter Diameter of the tire, in any convenient units (diameter in mm is usually printed on the tire).
   * @param trackWidth Distance between center of right tire and center of left tire, in same units as wheelDiameter.
   * @param leftMotor The left Motor (e.g., Motor.C).
   * @param rightMotor The right Motor (e.g., Motor.A).
   */
  public TachoPilot(float wheelDiameter, float trackWidth, Motor leftMotor, Motor rightMotor)
  {
    this(wheelDiameter, trackWidth, leftMotor, rightMotor, false);
  }

  /**
   * Allocates a Pilot object, and sets the physical parameters of the NXT robot.<br>
   * 
   * @param wheelDiameter Diameter of the tire, in any convenient units (diameter in mm is usually printed on the tire).
   * @param trackWidth Distance between center of right tire and center of left tire, in same units as wheelDiameter.
   * @param leftMotor The left Motor (e.g., Motor.C).
   * @param rightMotor The right Motor (e.g., Motor.A).
   * @param reverse If true, the NXT robot moves forward when the motors are running backward.
   */
  public TachoPilot(float wheelDiameter, float trackWidth, Motor leftMotor, Motor rightMotor, boolean reverse)
  {
    _left = leftMotor;
    _right = rightMotor;
    _degPerDistance = 360 / ((float) Math.PI * wheelDiameter);
    _motorSpeed = 360;
    _robotSeed = _motorSpeed/_degPerDistance;
    _turnRatio = trackWidth / wheelDiameter;
    _trackWidth = trackWidth;
    _wheelDiameter = wheelDiameter;
    if (reverse)
    {
      _parity = -1;
    } else
    {
      _parity = 1;
    }
  }

  /**
   * 
   * Allocates a Pilot object, and sets the physical parameters of the NXT robot.<br>
   * About <i>drift correction</i>: If your robot has problems traveling long distances (many meters) without deviating
   * too much (>2%) this might be due to a <b>small</b> (sub millimeter) differences in wheel size. To correct this kind
   * of "drift" you can specify a drift correction factor (e.g., a 2 wheel robot (5,6cm wheel diameter and 11cm track
   * width) had a deviation of 16cm to the right after traveling 400cm in a "straight" line. A drift correction of
   * -0.001f did correct this).
   * 
   * @param wheelDiameter Diameter of the tire, in any convenient units (diameter in mm is usually printed on the tire).
   * @param trackWidth Distance between center of right tire and center of left tire, in same units as wheelDiameter.
   * @param driftCorrection A correction factor to compensate for <b>small</b> differences in wheel size.
   * @param leftMotor The left Motor (e.g., Motor.C).
   * @param rightMotor The right Motor (e.g., Motor.A).
   * @param reverse If true, the NXT robot moves forward when the motors are running backward.
   */
  public TachoPilot(float wheelDiameter, float trackWidth, float driftCorrection, Motor leftMotor, Motor rightMotor,
          boolean reverse)
  {
    this(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
    _driftCorrection = 1.0f + driftCorrection;
  }

  /**
   * @return left motor.
   */
  public Motor getLeft()
  {
    return _left;
  }

  /**
   * @return right motor.
   */
  public Motor getRight()
  {
    return _right;
  }

  /**
   * @return tachoCount of left motor. Positive value means motor has moved the robot forward.
   */
  public int getLeftCount()
  {
    return _parity * _left.getTachoCount();
  }

  /**
   * @return tachoCount of the right motor. Positive value means motor has moved the robot forward.
   */
  public int getRightCount()
  {
    return _parity * _right.getTachoCount();
  }

  /**
   * @return actual speed of left motor in degrees per second. A negative value if motor is rotating backwards. Updated
   *         every 100 ms.
   **/
  public int getLeftActualSpeed()
  {
    return _left.getActualSpeed();
  }

  /**
   * @return actual speed of right motor in degrees per second. A negative value if motor is rotating backwards. Updated
   *         every 100 ms.
   **/
  public int getRightActualSpeed()
  {
    return _right.getActualSpeed();
  }

  /**
   * @return ratio of motor revolutions per 360 degree rotation of the robot.
   */
  public float getTurnRatio()
  {
    return _turnRatio;
  }

  /**
   * @return current robot speed.
   */
  public float getRobotSpeed()
  {
    return _robotSeed;
  }
/**
 * sets robot speed in wheel diameter units per second
 * @param speed
 */
  public void setRobotSpeed(float speed)
  {
    setSpeed(_degPerDistance*_robotSeed);
  }
  /**
   * Sets speed of both motors and sets regulate speed to true. If a drift correction has been specified in the
   * constructor it will be applied to the left motor.
   * 
   * @param speed The wanted speed in degrees per second.
   */
  public void setSpeed(float speed)
  {
    _robotSeed = speed/_degPerDistance;
    _motorSpeed = (int)(speed);
    _left.regulateSpeed(_regulating);
    _left.smoothAcceleration(!isMoving());
    _right.regulateSpeed(_regulating);
    _right.smoothAcceleration(!isMoving());
    _left.setSpeed((int) (_motorSpeed * _driftCorrection));
    _right.setSpeed(_motorSpeed);
  }

  /**
   * Moves the NXT robot forward until stop() is called.
   */
  public void forward()
  {
    setSpeed(_motorSpeed);
    if (_parity == 1)
    {
      fwd();
    } else
    {
      bak();
    }
  }

  /**
   * Moves the NXT robot backward until stop() is called.
   */
  public void backward()
  {
    setSpeed(_motorSpeed);
    if (_parity == 1)
    {
      bak();
    } else
    {
      fwd();
    }
  }

  /**
   * Rotates the NXT robot through a specific angle. Returns when angle is reached. Wheels turn in opposite directions
   * producing a zero radius turn.<br>
   * Note: Requires correct values for wheel diameter and track width.
   * 
   * @param angle The wanted angle of rotation in degrees. Positive angle rotate left (clockwise), negative right.
   */
  public void rotate(float angle)
  {
    rotate(angle, false);
  }

  /**
   * Rotates the NXT robot through a specific angle. Returns when angle is reached. Wheels turn in opposite directions
   * producing a zero radius turn.<br>
   * Note: Requires correct values for wheel diameter and track width.
   * 
   * @param angle The wanted angle of rotation in degrees. Positive angle rotate left (clockwise), negative right.
   * @param immediateReturn If true this method returns immediately.
   */
  public void rotate(float  angle, boolean immediateReturn)
  {
    setSpeed(_motorSpeed);
    int ta = _parity * (int) (angle * _turnRatio);
    _left.rotate(-ta, true);
    _right.rotate(ta, immediateReturn);
  }

  /**
   * @return the angle of rotation of the robot since last call to reset of tacho count;
   */
  public int getAngle()
  {
    return _parity * Math.round((getRightCount() - getLeftCount()) / (2 * _turnRatio));
  }

  /**
   * Stops the NXT robot.
   */
  public void stop()
  {
    _left.stop();
    _right.stop();
  }

  /**
   * @return true if the NXT robot is moving.
   **/
  public boolean isMoving()
  {
    return _left.isMoving() || _right.isMoving() || _left.isRotating() || _right.isRotating();
  }

  /**
   * Resets tacho count for both motors.
   **/
  public void reset()
  {
    _left.resetTachoCount();
    _right.resetTachoCount();
  }

  /**
   * @return distance traveled since last reset of tacho count.
   **/
  public float getTravelDistance()
  {
    int avg = (_left.getTachoCount() + _right.getTachoCount()) / 2;
    return _parity * avg / _degPerDistance;
  }

  /**
   * Moves the NXT robot a specific distance in an (hopefully) straight line.<br>
   * A positive distance causes forward motion, a negative distance moves backward. If a drift correction has been
   * specified in the constructor it will be applied to the left motor.
   * 
   * @param distance The distance to move. Unit of measure for distance must be same as wheelDiameter and trackWidth.
   **/
  public void travel(float distance)
  {
    travel(distance, false);
  }

  /**
   * Moves the NXT robot a specific distance in an (hopefully) straight line.<br>
   * A positive distance causes forward motion, a negative distance moves backward. If a drift correction has been
   * specified in the constructor it will be applied to the left motor.
   * 
   * @param distance The distance to move. Unit of measure for distance must be same as wheelDiameter and trackWidth.
   * @param immediateReturn If true this method returns immediately.
   */
  public void travel(float distance, boolean immediateReturn)
  {
    setSpeed(_motorSpeed);// both motors at same speed
    _left.rotate((int) (_parity * distance * _degPerDistance * _driftCorrection), true);
    _right.rotate((int) (_parity * distance * _degPerDistance), immediateReturn);
  }

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
  public void steer(int turnRate)
  {
    steer(turnRate, Integer.MAX_VALUE, true);
  }

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
  public void steer(int turnRate, int angle)
  {
    steer(turnRate, angle, false);
  }

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
  public void steer(int turnRate, int angle, boolean immediateReturn)
  {
    Motor inside;
    Motor outside;
    int rate = turnRate;
    if (rate < -200)
    {
      rate = -200;
    }
    if (rate > 200)
    {
      rate = 200;
    }
    if (rate == 0)
    {
      if (angle < 0)
      {
        backward();
      } else
      {
        forward();
      }
      return;
    }
    if (turnRate < 0)
    {
      inside = _right;
      outside = _left;
      rate = -rate;
    } else
    {
      inside = _left;
      outside = _right;
    }
    outside.setSpeed(_motorSpeed);
    float steerRatio = 1 - rate / 100.0f;
    inside.setSpeed((int) (_motorSpeed * steerRatio));
    if (angle == Integer.MAX_VALUE) // no limit angle for turn
    {
      if (_parity == 1)
      {
        outside.forward();
      } else
      {
        outside.backward();
      }
      if (_parity * steerRatio > 0)
      {
        inside.forward();
      } else
      {
        inside.backward();
      }
      return;
    }
    float rotAngle = angle * _trackWidth * 2 / (_wheelDiameter * (1 - steerRatio));
    inside.rotate(_parity * (int) (rotAngle * steerRatio), true);
    outside.rotate(_parity * (int) rotAngle, immediateReturn);
    if (immediateReturn)
    {
      return;
    }
    inside.setSpeed(outside.getSpeed());
  }

  /**
   * @return true if either motor actual speed is zero.
   */
  public boolean stalled()
  {
    return (0 == _left.getActualSpeed()) || (0 == _right.getActualSpeed());
  }

  /**
   * Motors backward. This is called by forward() and backward().
   */
  private void bak()
  {
    _left.backward();
    _right.backward();
  }

  /**
   * Sets motor speed regulation (default is true).<br>
   * Allows steer() method to be called by (for example) a line tracker or compass navigator so direction control is
   * from sensor inputs.
   * 
   * @param yes Set motor speed regulation on = true or off = false.
   */
  public void regulateSpeed(boolean yes)
  {
    _regulating = yes;
    _left.regulateSpeed(yes);
    _right.regulateSpeed(yes);
  }

  /**
   * Motors forward. This is called by forward() and backward().
   */
  private void fwd()
  {
    _left.forward();
    _right.forward();
  }
 /**
   * Moves the NXT robot in a circular path with a specified radius. <br>
   * The center of the turning circle is on the right side of the robot iff parameter radius is negative;  <br>
   * Postcondition:  Motor speeds are unpredictable.
   * @param radius of the circular path. If positive, the left wheel is on the inside of the turn.  If negative, the left wheel is on the outside.
   */
  public void turn(float radius)
  {
    steer(turnRate(radius));
  }

 /**
   * Moves the NXT robot in a circular arc through the specificd angle;  <br>
   * The center of the turning circle is on the right side of the robot iff parameter radius is negative.
   * Robot will stop when total rotation equals angle. If angle is negative, robot will move travel backwards.
   * @param radius radius of the turning circle
   * @param angle The sign of the angle determines the direction of robot motion
   */
  public void turn(float radius, int angle)
  {
    steer(turnRate(radius),angle);
  }
  /**
   *  Move in a circular arc with specified radius; the center of the turning circle <br>
 * is on the right side of the robot if the radius is negative.
   * @param radius
   * @param angle
   * @param immediateReturn
   */
  public void turn(float radius, int angle, boolean immediateReturn)
  {
    steer(turnRate(radius),angle,immediateReturn);
  }
  /**
   * Calculates the turn rate corresponding to the turn radius; <br>
   * use as the parameter for steer()
   * negative argument means center of turn is on right, so angle of turn is negative
   * @param radius
   * @return
   * steer()
   */
  private int turnRate(float radius)
  {
    int direction = 1;
    if (radius < 0)
    {
      direction = -1;
      radius = -radius;
    }
    float ratio = (2 * radius - _trackWidth) / (2 * radius + _trackWidth);
    return Math.round(direction * 100 * (1 - ratio));
  }
}
