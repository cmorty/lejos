package lejos.robotics.proposal;

//import lejos.robotics.navigation.*;

import java.util.ArrayList;
//import lejos.nxt.Battery;
import lejos.robotics.MoveListener;
import lejos.robotics.Movement;
import lejos.robotics.TachoMotor;
import lejos.robotics.MovementProvider;
import lejos.robotics.proposal.ArcRotatePilot;
import lejos.robotics.navigation.TachoPilot;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * The DifferentialPilot class is a software abstraction of the Pilot mechanism of a
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
 * PdifferentialPilot pilot = new DifferentialPilot(2.1f, 4.4f, Motor.A, Motor.C, true);  // parameters in inches
 * pilot.setRobotSpeed(10);                                           // inches per second
 * pilot.travel(12);                                                  // inches
 * pilot.rotate(-90);                                                 // degree clockwise
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
 * 
 **/
public class DifferentialPilot1 extends TachoPilot implements MovementProvider
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
  public DifferentialPilot1(final float wheelDiameter, final float trackWidth,
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
  public DifferentialPilot1(final float wheelDiameter, final float trackWidth,
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
  public DifferentialPilot1(final float leftWheelDiameter,
          final float rightWheelDiameter, final float trackWidth,
          final TachoMotor leftMotor, final TachoMotor rightMotor,
          final boolean reverse)
  {
    super(leftWheelDiameter,rightWheelDiameter,trackWidth,
            leftMotor,rightMotor,reverse); 
  }

  public void addMoveListener(MoveListener aListener)
  {
    listeners.add(aListener);
  }

 
  /**
   * Moves the NXT robot forward until stop() is called.
   */
  public void forward()
  {
    startMove(Movement.MovementType.TRAVEL,false);
  super.forward();
  }

  /**
   * Moves the NXT robot backward until stop() is called.
   */
  public void backward()
  {
    startMove(Movement.MovementType.TRAVEL,false);
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
    startMove( Movement.MovementType.ROTATE,immediateReturn);
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
    startMove(Movement.MovementType.TRAVEL, immediateReturn);
      super.travel(distance,immediateReturn);
    if (!immediateReturn)
    {
      movementStop();
    }
  }
/**
 * called at start of a movement to inform listeners that a movement has started
 */
	protected void startMove(Movement.MovementType type, boolean alert)
    {
     if(isMoving())movementStop();
    _moveType = type;
    reset();
    movementStart();
    _alert = alert;
	}

  public void arc(final float radius)
  {
      startMove(Movement.MovementType.ARC,false);
     super.arc(radius);
  }

  public void  arc(final float radius, final float angle)
  {
     arc(radius, angle,false);
  }

  public void arc(final float radius, final float angle,
          final boolean immediateReturn)
  {
     startMove(Movement.MovementType.ARC,immediateReturn);
     super.arc(radius,angle,immediateReturn);
     if(!immediateReturn)movementStop();
  }

  public void travelArc(float radius, float distance)
  {
     travelArc(radius, distance, false);
  }

  public  void travelArc(float radius, float distance, boolean immediateReturn)
  {
     startMove(Movement.MovementType.ARC,immediateReturn);
     super.travelArc(radius,distance,immediateReturn);
     if(!immediateReturn)movementStop();
  }

 
  protected void movementStart()
  {
    for (MoveListener p : listeners)
    {
      p.movementStarted(new Movement(_moveType, 0, 0, true ));
    }
  }

  protected void movementStop()
  {
    for (MoveListener p : listeners)
    {
      _alert = false;
      Movement move = new Movement(_moveType, getTravelDistance(), getAngle(), false);
      p.movementStopped(move);
    }
  }
  
 /**
  * Returns a new Movement object with current type, travel distance, angle, isMoving)
  * @return
  */
  public Movement getMovement()
  {
    return new Movement(_moveType, getTravelDistance(), getAngle(), isMoving());
  }

  /**
   * The only purpose of this inner class is to detect when an immediate return
   * method exits because the distance or angle has been reached. If this happens
   * listeners are informed of the movemt end.
   */
  private class Monitor extends Thread
  {

    public void run()
    {
      while (true)
      {
        // note:  the pilot may stop or  _alert may be cancelled by the main
        // thread at any time. Do not call movementStop if either happens
        while (!_alert)
        {
          Thread.yield();//no movement in progress
        }
        while (!isMoving())
        {
          Thread.yield();
        }

        while (isMoving())
        {
          Thread.yield();
        }
        synchronized (monitor)
        {
          if (_alert)
          {
            movementStop(); // updates listeners
          }
        }//end synchronized
      }
    }
  }
  /**
   * should be true if an immediate return movement is in progress.
   * used by monitor
   */
  protected boolean _alert = false;
  /**
   * the pilot listeners
   */
  protected ArrayList<MoveListener> listeners = new ArrayList<MoveListener>();
  /**
   * type of the current movement
   */
  protected Movement.MovementType _moveType;
  private Monitor monitor = new Monitor();

  

}
