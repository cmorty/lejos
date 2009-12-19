package lejos.robotics.proposal;



import lejos.robotics.navigation.*;
import lejos.nxt.Battery;
//import lejos.robotics.TachoMotor1;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * The TachoPilot class is a software abstraction of a NXT robot with two
 * independently controlled wheels, on opposite sides of the robot, with colinear axels.
 * This design permits the robot to rotate within its own footprint (i.e. turn on
 * one spot without changing its location).<br>
 * It contains methods to control basic  robot movements: travel forward or
 * backward in a straight line or a circular path or rotate to a new direction.<br>
 * It can be used with robots that have reversed motor design: the robot moves
 * in the direction opposite to the the direction of motor rotation. <br>
 * About angles:  TachoPilot uses the Navigation package  standard mathematical convention for angles in
 * the plane. The direction  of the X axis is 0 degrees, the direction  of
 * the Y axis is 90 degrees.  Therefore, a positive angle is a counter clockwise change of direction,
 * and a negative angle is clockwise.<br>
 *
 * Some methods optionally return immediately so the thread that called the
 * method can monitor sensors and call stop() if necessary.<br>
 Example:
 * <p>
 * <code><pre>
 * Pilot pilot = new TachoPilot(2.1f, 4.4f, Motor.A, Motor.C, true);  // parameters in inches
 * pilot.setTravelSpeed(10);                                           // inches per second
 * pilot.travel(12);                                                  // inches
 * pilot.rotate(-90);                                                 // degree clockwise
 * pilot.travel(-12,true);
 * while(pilot.isMoving())Thread.yield();
 * pilot.steer(-50,180,true);
 * while(pilot.isMoving())Thread.yield();
 * pilot.steer(100);
 * Delay.msDelay(1000);
 * pilot.stop();
 * </pre></code>
 * </p>
 * 
 * Note: if you are sure you do not want to use any other part of navigation you
 * can as well use "TachoPilot pilot = new TachoPilot(...)" instead of
 * "Pilot pilot = new TachoPilot(...)"
 **/
public class TachoPilot1 implements Pilot {

	/**
	 * Left motor.
	 */
	protected final TachoMotor1 _left;

	/**
	 * Right motor.
	 */
	protected final TachoMotor1 _right;
    
    /**
     * The motor at the inside of the turn. set by steer(turnRate) 
     * used by other steer methodsl
     */
    protected TachoMotor1 _inside;
     /**
     * The motor at the outside of the turn. set by steer(turnRate)
     * used by other steer methodsl
     */
    protected TachoMotor1 _outside;
    /**
     * ratio of inside/outside motor speeds
     * set by steer(turnRate)
     * used by other steer methods;
     */
    protected  float _steerRatio;

	/**
	 * Left motor degrees per unit of travel.
	 */
	protected final float _leftDegPerDistance;

	/**
	 * Right motor degrees per unit of travel.
	 */
	protected final float _rightDegPerDistance;

	/**
	 * Left motor revolutions for 360 degree rotation of robot (motors running
	 * in opposite directions). Calculated from wheel diameter and track width.
	 * Used by rotate() and steer() methods.
	 **/
	protected final float _leftTurnRatio;

	/**
	 * Right motor revolutions for 360 degree rotation of robot (motors running
	 * in opposite directions). Calculated from wheel diameter and track width.
	 * Used by rotate() and steer() methods.
	 **/
	protected final float _rightTurnRatio;

	/**
	 * Speed of robot for moving in wheel diameter units per seconds. Set by
	 * setSpeed(), setTravelSpeed()
	 */
	protected float _robotTravelSpeed;

	/**
	 * Speed of robot for turning in degree per seconds.
	 */
	protected float _robotRotateSpeed;

	/**
	 * Motor speed degrees per second. Used by forward(),backward() and steer().
	 */
	protected int _motorSpeed;

	/**
	 * Motor rotation forward makes robot move forward if parity == 1.
	 */
	private byte _parity;


	/**
	 * Distance between wheels. Used in steer() and rotate().
	 */
	protected final float _trackWidth;

	/**
	 * Diameter of left wheel.
	 */
	protected final float _leftWheelDiameter;

	/**
	 * Diameter of right wheel.
	 */
	protected final float _rightWheelDiameter;

	/**
	 * Allocates a TachoPilot object, and sets the physical parameters of the
	 * NXT robot.<br>
	 * Assumes Motor.forward() causes the robot to move forward.
	 * 
	 * @param wheelDiameter
	 *            Diameter of the tire, in any convenient units (diameter in mm
	 *            is usually printed on the tire).
	 * @param trackWidth
	 *            Distance between center of right tire and center of left tire,
	 *            in same units as wheelDiameter.
	 * @param leftMotor
	 *            The left Motor (e.g., Motor.C).
	 * @param rightMotor
	 *            The right Motor (e.g., Motor.A).
	 */
	public TachoPilot1(final float wheelDiameter, final float trackWidth,
			final TachoMotor1 leftMotor, final TachoMotor1 rightMotor) {
		this(wheelDiameter, trackWidth, leftMotor, rightMotor, false);
	}

	/**
	 * Allocates a TachoPilot object, and sets the physical parameters of the
	 * NXT robot.<br>
	 * 
	 * @param wheelDiameter
	 *            Diameter of the tire, in any convenient units (diameter in mm
	 *            is usually printed on the tire).
	 * @param trackWidth
	 *            Distance between center of right tire and center of left tire,
	 *            in same units as wheelDiameter.
	 * @param leftMotor
	 *            The left Motor (e.g., Motor.C).
	 * @param rightMotor
	 *            The right Motor (e.g., Motor.A).
	 * @param reverse
	 *            If true, the NXT robot moves forward when the motors are
	 *            running backward.
	 */
	public TachoPilot1(final float wheelDiameter, final float trackWidth,
			final TachoMotor1 leftMotor, final TachoMotor1 rightMotor,
			final boolean reverse) {
		this(wheelDiameter, wheelDiameter, trackWidth, leftMotor, rightMotor,
				reverse);
	}

	/**
	 * Allocates a TachoPilot object, and sets the physical parameters of the
	 * NXT robot.<br>
	 * 
	 * @param leftWheelDiameter
	 *            Diameter of the left wheel, in any convenient units (diameter
	 *            in mm is usually printed on the tire).
	 * @param rightWheelDiameter
	 *            Diameter of the right wheel. You can actually fit
	 *            intentionally wheels with different size to your robot. If you
	 *            fitted wheels with the same size, but your robot is not going
	 *            straight, try swapping the wheels and see if it deviates into
	 *            the other direction. That would indicate a small difference in
	 *            wheel size. Adjust wheel size accordingly. The minimum change
	 *            in wheel size which will actually have an effect is given by
	 *            minChange = A*wheelDiameter*wheelDiameter/(1-(A*wheelDiameter)
	 *            where A = PI/(TravelSpeed*360). Thus for a TravelSpeed of 25
	 *            cm/second and a wheelDiameter of 5,5 cm the minChange is about
	 *            0,01058 cm. The reason for this is, that different while sizes
	 *            will result in different motor speed. And that is given as an
	 *            integer in degree per second.
	 * @param trackWidth
	 *            Distance between center of right tire and center of left tire,
	 *            in same units as wheelDiameter.
	 * @param leftMotor
	 *            The left Motor (e.g., Motor.C).
	 * @param rightMotor
	 *            The right Motor (e.g., Motor.A).
	 * @param reverse
	 *            If true, the NXT robot moves forward when the motors are
	 *            running backward.
	 */
	public TachoPilot1(final float leftWheelDiameter,
			final float rightWheelDiameter, final float trackWidth,
			final TachoMotor1 leftMotor, final TachoMotor1 rightMotor,
			final boolean reverse) {
		// left
		_left = leftMotor;
		_leftWheelDiameter = leftWheelDiameter;
		_leftTurnRatio = trackWidth / leftWheelDiameter;
		_leftDegPerDistance = 360 / ((float) Math.PI * leftWheelDiameter);
		// right
		_right = rightMotor;
		_rightWheelDiameter = rightWheelDiameter;
		_rightTurnRatio = trackWidth / rightWheelDiameter;
		_rightDegPerDistance = 360 / ((float) Math.PI * rightWheelDiameter);
		// both
		_trackWidth = trackWidth;
		_parity = (byte) (reverse ? -1 : 1);
                setTravelSpeed(.8f*getMaxTravelSpeed());
		setRotateSpeed(.8f*getMaxRotateSpeed());
	}

	/**
     * Returns the left motor.
	 * @return left motor.
	 */
	public TachoMotor1 getLeft() {
		return _left;
	}

	/**
     * returns the right motor.
	 * @return right motor.
	 */
	public TachoMotor1 getRight() {
		return _right;
	}

	/**
     * Returnsthe tachoCount of the left motor
	 * @return tachoCount of left motor. Positive value means motor has moved
	 *         the robot forward.
	 */
	public int getLeftCount() {
		return _parity * _left.getTachoCount();
	}

	/**
     * Returns the tachoCount of the right motor
	 * @return tachoCount of the right motor. Positive value means motor has
	 *         moved the robot forward.
	 */
	public int getRightCount() {
		return _parity * _right.getTachoCount();
	}

	/**
     * Returns the actual speed of the left motor
	 * @return actual speed of left motor in degrees per second. A negative
	 *         value if motor is rotating backwards. Updated every 100 ms.
	 **/
	public int getLeftActualSpeed() {
		return _left.getRotationSpeed();
	}

	/**
     * Returns the actual speed of right motor
	 * @return actual speed of right motor in degrees per second. A negative
	 *         value if motor is rotating backwards. Updated every 100 ms.
	 **/
	public int getRightActualSpeed() {
		return _right.getRotationSpeed();
	}

	/**
     * Returns the ratio of motor revolutions per 360 degree rotation of the robot
	 * @return ratio of motor revolutions per 360 degree rotation of the robot.
	 *         If your robot has wheels with different size, it is the average.
	 */
	public float getTurnRatio() {
		return (_leftTurnRatio + _rightTurnRatio) / 2.0f;
	}

/**
   * Sets drive motor speed.
   *
   * @param speed The speed of the drive motor(s) in degree per second.
   *
   * @deprecated in 0.8, use setRotateSpeed() and setTravelSpeed(). The method was deprecated, as this it requires knowledge
   *             of the robots physical construction, which this interface should hide!
   */
	public void setSpeed(final int speed) {
		_motorSpeed = speed;
		_robotTravelSpeed = speed
				/ Math.max(_leftDegPerDistance, _rightDegPerDistance);
		_robotRotateSpeed = speed / Math.max(_leftTurnRatio, _rightTurnRatio);
		setSpeed(speed, speed);
	}

	private void setSpeed(final int leftSpeed, final int rightSpeed)
    {
		_left.setSpeed(leftSpeed);
		_right.setSpeed(rightSpeed);
	}

	/**
	 * use setTravelSpeed()
	 */
       @Deprecated
	public void setMoveSpeed(float speed) {
		_robotTravelSpeed = speed;
		_motorSpeed = Math.round(0.5f * speed
				* (_leftDegPerDistance + _rightDegPerDistance));
		setSpeed(Math.round(speed * _leftDegPerDistance), Math.round(speed
				* _rightDegPerDistance));
	}
        /**
	 * also sets _motorSpeed
	 *
	 * @see lejos.robotics.navigation.Pilot#setTravelSpeed(float)
	 */
	public void setTravelSpeed(float travelSpeed) {
		_robotTravelSpeed = travelSpeed;
		_motorSpeed = Math.round(0.5f * travelSpeed
				* (_leftDegPerDistance + _rightDegPerDistance));
		setSpeed(Math.round(travelSpeed * _leftDegPerDistance), Math.round(travelSpeed
				* _rightDegPerDistance));
	}

	/**
	 * @see lejos.robotics.navigation.Pilot#getTravelSpeed()
	 */
	public float getTravelSpeed() {
		return _robotTravelSpeed;
	}
        /**
	 * use getTraveleSpeed()
	 */
        @Deprecated
	public float getMoveSpeed() {
		return _robotTravelSpeed;
	}

        /**
	 * @see lejos.robotics.navigation.Pilot#getTravelMaxSpeed()
	 */
	public float getMaxTravelSpeed() {
		// it is generally assumed, that the maximum accurate speed of Motor is
		// 100 degree/second * Voltage
		return Battery.getVoltage() * 100.0f
				/ Math.max(_leftDegPerDistance, _rightDegPerDistance);
		// max degree/second divided by degree/unit = unit/second
	}

	/**
	 *use getMoveMaxSpeed()
	 */
        @Deprecated
	public float getMoveMaxSpeed() {
		// it is generally assumed, that the maximum accurate speed of Motor is
		// 100 degree/second * Voltage
		return Battery.getVoltage() * 100.0f
				/ Math.max(_leftDegPerDistance, _rightDegPerDistance);
		// max degree/second divided by degree/unit = unit/second
	}
  public void setRotateSpeed(float rotateSpeed)
  {
    _robotRotateSpeed = rotateSpeed;
    setSpeed(Math.round(rotateSpeed * _leftTurnRatio), Math.round(rotateSpeed * _rightTurnRatio));
  }


  /**
   * use setRotateSpeed()
   */
  @Deprecated
  public void setTurnSpeed(float speed)
  {
    _robotRotateSpeed = speed;
    setSpeed(Math.round(speed * _leftTurnRatio), Math.round(speed * _rightTurnRatio));
  }

	/**
	 * use getRotateSpeed()
	 */
        @Deprecated
	public float getTurnSpeed() {
		return _robotRotateSpeed;
	}


        /**
	 * @see lejos.robotics.navigation.Pilot#getRotateSpeed()
	 */
	public float getRotateSpeed() {
		return _robotRotateSpeed;
	}

	/**
	 * @see lejos.robotics.navigation.Pilot#getRotateMaxSpeed()
	 */
	public float getMaxRotateSpeed() {
		// it is generally assumed, that the maximum accurate speed of Motor is
		// 100 degree/second * Voltage
		return Battery.getVoltage() * 100.0f
				/ Math.max(_leftTurnRatio, _rightTurnRatio);
		// max degree/second divided by degree/unit = unit/second
	}


	public void forward() {
		setSpeed(Math.round(_robotTravelSpeed * _leftDegPerDistance), Math
				.round(_robotTravelSpeed * _rightDegPerDistance));
		if (_parity == 1) {
			fwd();
		} else {
			bak();
		}
	}


	public void backward() {
		setSpeed(Math.round(_robotTravelSpeed * _leftDegPerDistance), Math
				.round(_robotTravelSpeed * _rightDegPerDistance));

		if (_parity == 1) {
			bak();
		} else {
			fwd();
		}
	}

	public void rotate(final float angle) {
		rotate(angle, false);
	}


	public void rotate(final float angle, final boolean immediateReturn) {
		setSpeed(Math.round(_robotRotateSpeed * _leftTurnRatio), Math
				.round(_robotRotateSpeed * _rightTurnRatio));
		int rotateAngleLeft = _parity * (int) (angle * _leftTurnRatio);
		int rotateAngleRight = _parity * (int) (angle * _rightTurnRatio);
		_left.rotate(-rotateAngleLeft, true);
		_right.rotate(rotateAngleRight, immediateReturn);
		if (!immediateReturn) {
			while (_left.isMoving() || _right.isMoving())
				// changed isRotating() to isMoving() as this covers what we
				// need and alows us to keep the interface small
				Thread.yield();
		}
	}

	/**
	 * @return the angle of rotation of the robot since last call to reset of
	 *         tacho count;
	 */
	public float getAngle() {
		return _parity
				* ((_right.getTachoCount() / _rightTurnRatio) - (_left
						.getTachoCount() / _leftTurnRatio)) / 2.0f;
	}

	/**
	 * Stops the NXT robot.
	 */
	public void stop() {
		_left.stop();
		_right.stop();
	}

	/**
	 * @return true if the NXT robot is moving.
	 **/
	public boolean isMoving() {
		return _left.isMoving() || _right.isMoving();
	}

	/**
	 * Resets tacho count for both motors.
	 **/
	public void reset() {
		_left.resetTachoCount();
		_right.resetTachoCount();
	}

	/**
	 * @return distance traveled since last reset of tacho count.
	 **/
	public float getTravelDistance() {
		float left = _left.getTachoCount() / _leftDegPerDistance;
		float right = _right.getTachoCount() / _rightDegPerDistance;
		return _parity * (left + right) / 2.0f;
	}

	/**
	 * Moves the NXT robot a specific distance in an (hopefully) straight line.<br>
	 * A positive distance causes forward motion, a negative distance moves
	 * backward. If a drift correction has been specified in the constructor it
	 * will be applied to the left motor.
	 * 
	 * @param distance
	 *            The distance to move. Unit of measure for distance must be
	 *            same as wheelDiameter and trackWidth.
	 **/
	public void travel(final float distance) {
		travel(distance, false);
	}

	/**
	 * Moves the NXT robot a specific distance in an (hopefully) straight line.<br>
	 * A positive distance causes forward motion, a negative distance moves
	 * backward. If a drift correction has been specified in the constructor it
	 * will be applied to the left motor.
	 * 
	 * @param distance
	 *            The distance to move. Unit of measure for distance must be
	 *            same as wheelDiameter and trackWidth.
	 * @param immediateReturn
	 *            If true this method returns immediately.
	 */
	public void travel(final float distance, final boolean immediateReturn) {
		setSpeed(Math.round(_robotTravelSpeed * _leftDegPerDistance), Math
				.round(_robotTravelSpeed * _rightDegPerDistance));
		_left.rotate((int) (_parity * distance * _leftDegPerDistance), true);
		_right.rotate((int) (_parity * distance * _rightDegPerDistance),
				immediateReturn);
		if (!immediateReturn) {
			while (_left.isMoving() || _right.isMoving())
				// changed isRotating() to isMoving() as this covers what we
				// need and alows us to keep the interface small
				Thread.yield();
		}
	}
/**
 * helper method used by steer(float) and steer(float,float,boolean)
 * @param turnRate
 */
	protected void steerPrep(final float turnRate)
    {

         if (turnRate == 0)
      {
        forward();
        return;
      }
		float rate = turnRate;
		if (rate < -200) {
			rate = -200;
		}
		if (rate > 200) {
			rate = 200;
		}
	  
		if (turnRate < 0)
        {
			_inside = _right;
			_outside = _left;
			rate = -rate;
		} else
        {
			_inside = _left;
			_outside = _right;
		}        
		_outside.setSpeed(_motorSpeed);
		_steerRatio = 1 - rate / 100.0f;
		_inside.setSpeed((int) (_motorSpeed * _steerRatio));
	}

    public void steer(float turnRate)
   {
      if(turnRate == 0)
      {
        forward();
        return;
      }
    steerPrep(turnRate);
    _outside.forward();
    if (_parity * _steerRatio > 0)
    {
      _inside.forward();
    } else
    {
      _inside.backward();
    }
  }
	public void steer(final float turnRate,float angle) {
		steer(turnRate, angle, false);
	}

	public void steer(final float turnRate, final float  angle,
			final boolean immediateReturn) 
    {
      if(angle == 0)return;
      if(turnRate == 0)
      {
        forward();
        return;
      }
      steerPrep(turnRate);
       int side = (int) Math.signum(turnRate);
		int rotAngle = (int)(angle * _trackWidth * 2
				/ (_leftWheelDiameter * (1 - _steerRatio)));
		_inside.rotate((int)(_parity *side*  rotAngle * _steerRatio), true);
		_outside.rotate(_parity * side* rotAngle, immediateReturn);
		if (immediateReturn) {
			return;
		}
		while (_inside.isMoving() || _outside.isMoving())
			Thread.yield();
		_inside.setSpeed(_outside.getSpeed());
	}

	/*
     * Returns true if the actual speed of either motor is zero.
	 * @return true if either motor actual speed is zero.
	 */
	public boolean stalled() {
		return (0 == _left.getRotationSpeed()) || (0 == _right.getRotationSpeed());
	}

	/**
	 * Motors backward. This is called by forward() and backward(), demending in parity.
	 */
	 void bak() {
		_left.backward();
		_right.backward();
	}

	/**
	 * Motors forward. This is called by forward() and backward() depending in parity.
	 */
	private void fwd() {
		_left.forward();
		_right.forward();
	}

	public void arc(final float radius) {
		steer(turnRate(radius));
	}

	public void arc(final float radius, final float angle) {
		steer(turnRate(radius), angle);
	}

	public void arc(final float radius, final float angle,
			final boolean immediateReturn) {
		steer(turnRate(radius), angle, immediateReturn);
	}

	/**
	 * Calculates the turn rate corresponding to the turn radius; <br>
	 * use as the parameter for steer() negative argument means center of turn
	 * is on right, so angle of turn is negative
	 * 
	 * @param radius
	 * @return steer()
	 */
	private float turnRate(final float radius) {
		int direction;
		float radiusToUse;
		if (radius < 0) {
			direction = -1;
			radiusToUse = -radius;
		} else {
			direction = 1;
			radiusToUse = radius;
		}
		float ratio = (2 * radiusToUse - _trackWidth)
				/ (2 * radiusToUse + _trackWidth);
		return (direction * 100 * (1 - ratio));
	}

	public void travelArc(float radius, float distance) {
		travelArc(radius, distance, false);
	}

	public void travelArc(float radius, float distance, boolean immediateReturn) {
		float angle = (distance * 180) / ((float)Math.PI * radius);
		arc(radius, angle, immediateReturn); 
	}
    
   /**
    * 
    * @param  if false, turns speed regulation off - it is on by default
   * @deprecated - motor speed regulation is on by default and all the movement
    * control methods in this class work well with it on.  If you realy want
    * to change motor speed regulation, use getLeft().regulateSpeed()
    */
    public void regulateSpeed(boolean yes)
    {
      _left.regulateSpeed(yes);
      _right.regulateSpeed(yes);
    }
}
