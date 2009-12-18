package lejos.robotics.proposal;

//package lejos.robotics.proposal;

//import lejos.nxt.Motor2;
//import lejos.robotics.navigation.TachoPilot;
//import lejos.robotics.TachoMotor;
//import lejos.robotics.TachoMotorListener;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * The DifferentialMoveControl class is a software abstraction of the Pilot mechanism of a
 * NXT robot. It contains methods to control robot movements: travel forward or
 * backward in a straight line or a circular path or rotate to a new direction.<br>
 * Note: this class will only work with two independently controlled motors to
 * steer differentially, so it can rotate within its own footprint (i.e. turn on
 * one spot).<br>
 * It can be used with robots that have reversed motor design: the robot moves
 * in the direction opposite to the the direction of motor rotation. Uses the
 * TachoMotor class, which regulates motor speed using the NXT motor's built in
 * tachometer.<br>
 * It automatically updates the Pose of a robot if the Pose calls the
 * addMoveListener() method on this class.
 * Some methods optionally return immediately so the thread that called the
 * method can monitor sensors, get current pose, and call stop() if necessary.<br>
 *  Example:
 * <p>
 * <code><pre>
 * PdifferentialMoveControl controller = new DifferentialMoveControl(2.1f, 4.4f, Motor.A, Motor.C, true);  // parameters in inches
 * controller.setRobotSpeed(10);                                           // inches per second
 * controller.travel(12);                                                  // inches
 * controller.rotate(-90);                                                 // degree clockwise
 * controller.travel(-12,true);
 * while(controller.isMoving())Thread.yield();
 * controller.rotate(-90);
 * controller.rotateTo(270);
 * controller.steer(-50,180,true);
 * while(controller.isMoving())Thread.yield();
 * controller.steer(100);
 * try{Thread.sleep(1000);}
 * catch(InterruptedException e){}
 * pilot.stop();
 * </pre></code>
 * </p>
 *
 **/
public class DifferentialMoveControl extends TachoPilot  implements 
        TachoMotorListener, MoveProvider

{

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
  public DifferentialMoveControl(final float wheelDiameter, final float trackWidth,
          final TachoMotor leftMotor, final TachoMotor rightMotor)
  {
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
  public DifferentialMoveControl(final float wheelDiameter, final float trackWidth,
          final TachoMotor leftMotor, final TachoMotor rightMotor,
          final boolean reverse)
  {
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
   *            where A = PI/(moveSpeed*360). Thus for a moveSpeed of 25
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
  public DifferentialMoveControl(final float leftWheelDiameter,
          final float rightWheelDiameter, final float trackWidth,
          final TachoMotor leftMotor, final TachoMotor rightMotor,
          final boolean reverse)
  {
    super(leftWheelDiameter,rightWheelDiameter,trackWidth, leftMotor,rightMotor,reverse);
    leftMotor.addListener(this);

  }


  public void addPose(Pose aPose)
  {
    _pose = aPose;
  }


  /**
   * Moves the NXT robot forward until stop() is called.
   */
  public void forward()
  {
    startMove(false);
  super.forward();
  }

  /**
   * Moves the NXT robot backward until stop() is called.
   */
  public void backward()
  {
     startMove(false);;
   super.backward();
  }

  /**
   * Rotates the NXT robot through a specific angle. Returns when angle is
   * reached. Wheels turn in opposite directions producing a zero radius turn.<br>
   * Note: Requires correct values for wheel diameter and track width.
   * calls rotate(angle,false)
   * @param angle
   *            The wanted angle of rotation in degrees. Positive angle rotate
   *            left (clockwise), negative right.
   */
  public void rotate(final float angle)
  {
    rotate(angle, false);
  }

  /**
   * Rotates the NXT robot through a specific angle. Returns when angle is
   * reached. Wheels turn in opposite directions producing a zero radius turn.<br>
   * Note: Requires correct values for wheel diameter and track width.
   * Side effect: inform listeners
   * @param angle
   *            The wanted angle of rotation in degrees. Positive angle rotate
   *            left (clockwise), negative right.
   * @param immediateReturn
   *            If true this method returns immediately.
   */
  public void rotate(final float angle, final boolean immediateReturn)
  {
    startMove(immediateReturn);
    super.rotate(angle,immediateReturn);
    if (!immediateReturn)
    {
      movementStop();
    }
  }


  /**
   * This method can be overridden by subclasses to stop the robot if a hazard
   * is detected
   *
   * @return true iff no hazard is detected
   */
  protected boolean continueMoving() {
	return true;
}

/**
   * Stops the NXT robot.
 *  side effect: inform listeners of end of movement
   */
  public void  stop()
  {
    _left.stop();
    _right.stop();
    while (isMoving()) Thread.yield();
    movementStop();
  }

  /**
   * Moves the NXT robot a specific distance in an (hopefully) straight line.<br>
   * A positive distance causes forward motion, a negative distance moves
   * backward. If a drift correction has been specified in the constructor it
   * will be applied to the left motor.
   * calls travel(distance, false)
   * @param distance
   *            The distance to move. Unit of measure for distance must be
   *            same as wheelDiameter and trackWidth.
   **/
  public void travel(final float distance)
  {
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
  public void travel(final float distance, final boolean immediateReturn)
  {
    startMove( immediateReturn);
      super.travel(distance,immediateReturn);
    if (!immediateReturn)
    {
      movementStop();
    }
  }
/**
 * called at start of a movement to inform listeners that a movement has started
 */
  protected void startMove( boolean alert)
  {
    if (isMoving())
    {
      movementStop();
    } 
    reset();
//    _moveType = type;
    _pose.movementStarted();
    _alert = alert;
  }

  public void arc(final float radius)
  {

      startMove(false);
     super.arc(radius);
  }

  public void  arc(final float radius, final float angle)
  {
     arc(radius, angle,false);
  }

  public void arc(final float radius, final float angle,
          final boolean immediateReturn)
  {
     startMove(immediateReturn);
     super.arc(radius,angle,immediateReturn);
     if(!immediateReturn)movementStop();
  }

  public void travelArc(float radius, float distance)
  {
     travelArc(radius, distance, false);
  }

  public  void travelArc(float radius, float distance, boolean immediateReturn)
  {
     startMove(immediateReturn);
     super.travelArc(radius,distance,immediateReturn);
     if(!immediateReturn)movementStop();
  }
/**
 * called by Arc() ,travel(),rotate(),stop() rotationStopped()
 * calls updatePose()
 */
  protected synchronized  void movementStop()
  {
      _alert = false;
      updatePose();
    }
/**
 * called by TachoMotor when a rotation. that returned immediately. is complete
 * calls movementStop()
 * @param motor
 * @param count
 * @param ts
 */
  public synchronized void rotationStopped(TachoMotor motor,int count, long ts )
  {
     if(_alert) movementStop(); //a motor has completed an immmediate return
  }


// /**
//  called by movementStop
//  * @return
//  */
  public void  updatePose()
  {
      _pose.update(getTravelDistance(), getAngle(),
              isMoving());
  }

public void setMinRadius(float radius){}

public float getMinRadius( ){return 0;}

public float getMovementIncrement(){return 0;}

public float getAngleIncrement(){return 0;}
  /**
   * should be true if an immediate return movement is in progress.
   * used by rotationStopped()
   */
  protected boolean _alert = false;
  /**
   * the pilot listeners
   */

  Pose _pose;

//  protected Pose.Move _moveType = Pose.Move.NONE;
//public enum Move {TRAVEL,ROTATE,ARC,NONE}




}
