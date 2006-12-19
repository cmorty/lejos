package josx.platform.nxt;

/**
 * Abstraction for a motor. Three instances of <code>Motor</code>
 * are available: <code>Motor.A</code>, <code>Motor.B</code>
 * and <code>Motor.C</code>. To control each motor use
 * methods <code>forward, backward, reverseDirection, stop</code>
 * and <code>flt</code>. To set each motor's power, use
 * <code>setPower</code>.  
 * <p>
 * Example:<p>
 * <code><pre>
 *   Motor.A.setPower(1);
 *   Motor.C.setPower(7);
 *   Motor.A.forward();
 *   Motor.C.forward();
 *   Thread.sleep (1000);
 *   Motor.A.stop();
 *   Motor.C.stop();
 * </pre></code>
 */
public class Motor
{
  private char  iId;
  private short iMode = 4;
  private short iPower = 3;

  /**
   * Motor A.
   */
  public static final Motor A = new Motor ('A');
  /**
   * Motor B.
   */
  public static final Motor B = new Motor ('B');
  /**
   * Motor C.
   */
  public static final Motor C = new Motor ('C');

  private Motor (char aId)
  {
    iId = aId;
  }

  /**
   * Get the ID of the motor. One of 'A', 'B' or 'C'.
   */
  public final char getId()
  {
    return iId;
  }

  /**
   * Sets motor power to a <i>value between 0 and 7</i>.
   * @param aPower value in the range [0-7].
   */
  public final void setPower (int aPower)
  {
    iPower = (short) aPower;
    controlMotor (iId, iMode, aPower);
  }

  /**
   * Causes motor to rotate forward.
   */
  public final void forward()
  {
    iMode = 1;
    controlMotor (iId, 1, iPower);
  }
  
  /**
   * Return true if motor is forward.
   */
  public final boolean isForward()
  {
    return (iMode == 1);
  }

  /**
   * Causes motor to rotate backwards.
   */
  public final void backward()
  {
    iMode = 2;
    controlMotor (iId, 2, iPower);
  }

  /**
   * Return true if motor is backward.
   */
  public final boolean isBackward()
  {
    return (iMode == 2);
  }

  /**
   * Reverses direction of the motor. It only has
   * effect if the motor is moving.
   */
  public final void reverseDirection()
  {
    if (iMode == 1 || iMode == 2)
    {
      iMode = (short) (3 - iMode);
      controlMotor (iId, iMode, iPower);
    }
  }

  /**
   * Returns the current motor power.
   */
  public final int getPower()
  {
    return iPower;	  
  }

  /**
   * @return true iff the motor is currently in motion.
   */
  public final boolean isMoving()
  {
    return (iMode == 1 || iMode == 2);	  
  }
  
  /**
   * @return true iff the motor is currently in float mode.
   */
  public final boolean isFloating()
  {
    return iMode == 4;	  
  }
  
  /**
   * Causes motor to stop, pretty much
   * instantaneously. In other words, the
   * motor doesn't just stop; it will resist
   * any further motion.
   */
  public final void stop()
  {
    iMode = 3;
    controlMotor (iId, 3, 7);
  }
  
  /**
   * Return true if motor is stopped.
   */
  public final boolean isStopped()
  {
    return (iMode == 3);
  }

  /**
   * Causes motor to float. The motor will lose all power,
   * but this is not the same as stopping. Use this
   * method if you don't want your robot to trip in
   * abrupt turns.
   */
  public final void flt()
  {
    iMode = 4;
    controlMotor (iId, 4, iPower);
  }

  /**
   * <i>Low-level API</i> for controlling a motor.
   * This method is not meant to be called directly.
   * If called, other methods such as isRunning() will
   * be unreliable.
   * @deprecated I've decided to remove this method.
   *             If you really need it, check its implementation
   *             in classes/josx/platform/rcx/Motor.java. 
   * @param aMotor The motor id: 'A', 'B' or 'C'.
   * @param aMode 1=forward, 2=backward, 3=stop, 4=float
   * @param aPower A value in the range [0-7].
   */
  public static void controlMotor (char aMotor, int aMode, int aPower)
  {
    ROM.call ((short) 0x1a4e, (short) (0x2000 + aMotor - 'A'), 
              (short) aMode, (short) aPower);
  }
  
  public int getTachoCount()
  {
	  return getTachoCountById((int) (getId() - 'A'));
  }
  
  public static native int getTachoCountById(int id);
}







