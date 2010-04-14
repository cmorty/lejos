package lejos.nxt;


import lejos.nxt.*;
import lejos.robotics.TachoMotor;
import lejos.robotics.TachoMotorListener;
import lejos.util.Delay;

/**
 * Abstraction for a NXT motor. Three instances of <code>Motor1</code>
 * are available: <code>Motor1.A</code>, <code>Motor1.B</code>
 * and <code>Motor1.C</code>.  The basic control methods are:
 *  <code>forward, backward, reverseDirection, stop</code>
 * and <code>flt</code>. To set each motor's speed, use {@link #setSpeed(int)
 * <code>setSpeed  </code> }.
 * The maximum speed of the motor limited by the battery voltage and load.
 * With no load, the maximum degrees per second is about 100 times the voltage.  <br>
 * The speed is regulated by comparing the tacho count with speed times elapsed
 * time, and adjusting motor power to keep these closely matched. The initial tacho count
 * and time used in this calculation are reset by most methods, so very frequent
 * method calls will degrade the accuracy of speed regulation. <br>
 * The methods <code>rotate(int angle) </code> and <code>rotateTo(int ange)</code>
 * use the tachometer to control the position at which the motor stops, usually within 1 degree
 * or 2.<br> 
 *  <br> <b> Listeners.</b>  An object implementing the {@link lejos.robotics.TachoMotorListener
 * <code> TachoMotorListener </code> } interface  may register with this class.
 * It will be informed each time the motor starts or stops.
 * <br> <b>Stall detection</b> If a stall is detected, the motor will stop, and
 * <code>isStalled()</code >  returns <b>true</b>.
 * <br>If you need the motor to hold its position against a load and you find that
 * still moves after stop() is called , you can use the {@link #lock()<code> lock()
 * </code> }method.
 * <br>
 * <p>
 * Example:<p>
 * <code><pre>
 *   Motor1.A.setSpeed(720);// 2 RPM
 *   Motor1.C.setSpeed(720);
 *   Motor1.A.forward();
 *   Motor1.C.forward();
 *   Thread.sleep (1000);
 *   Motor1.A.stop();
 *   Motor1.C.stop();
 *   Motor1.A.rotateTo( 360);
 *   Motor1.A.rotate(-720,true);
 *   while(Motor1.A.isRotating() :Thread.yield();
 *   int angle = Motor1.A.getTachoCount(); // should be -360
 *   LCD.drawInt(angle,0,0);
 * </pre></code>
 * @author Roger Glassey revised 9 Feb 2010. Smoother speed up , and stop at
 * end of rotation;
 */
public class Motor extends BasicMotor implements TachoMotor // implements TimerListener
{

  protected TachoMotorPort _tachoPort = (TachoMotorPort)super._port;
  //**
   /*
   * default speed  deg/sec
   */
  protected int _speed = 360;
  protected boolean _keepGoing = true;// for regulator
  protected boolean _rampUp = true; //used by regulator; true at start of movement and large speed change

  public final Regulator regulator = new Regulator();
  /**
   * sign of the rotation direction. set by forward(), backward()
   */

  protected byte _direction = 1;
  /*
   * angle at which rotation ends.  Set by rotateTo(), used by regulator
   */
  protected int _limitAngle; //set by rotate()


 /**
   * true when rotation to limit is in progress.  set by rotateTo(), used  and reset by regulator
  */
  protected boolean _rotating = false;


  /**
   * used by timedOut to calculate actual speed;
   */
  protected int _lastTacho = 0;
  /**
   * set by timedOut
   */
  protected int _actualSpeed;
  protected float _voltage = 0f;
  protected int _acceleration = 6000;// default acceleration 6000 deg/sec/sec
  protected boolean _lock = false;
  protected int _brakePower = 20;  //used while nudging toward final angle
  protected int _lockPower = 30;
  protected boolean _stalled = false;//set by regulator when stall is detected
  protected int _stallLimit = 1200;
  protected boolean _newOperation = false;// used by regulator.stopAtLimit
  protected Motor _thisMotor = this; // alias  for use by regulator
  /**
   * Motor1 A.
   */
  public static final Motor A = new Motor(MotorPort.A);
  /**
   * Motor1 B.
   */
  public static final Motor B = new Motor(MotorPort.B);
  /**
   * Motor1 C.
   */
  public static final Motor C = new Motor(MotorPort.C);
  /**
   * TODO: Currently only accepts one listener. We could expand this to multiple if needed.
   */
  protected TachoMotorListener listener = null;

  /**
   * Use this constructor to assign a variable of type motor connected to a particular port.
   * @param port  to which this motor is connected
   */
  public Motor(TachoMotorPort port)
  {
    _tachoPort = port;
    port.setPWMMode(TachoMotorPort.PWM_BRAKE);
    regulator.setDaemon(true);
    regulator.setPriority(Thread.MAX_PRIORITY);
    regulator.start();
    _voltage = Battery.getVoltage();
  }

  // TODO: Technically addListener() should be added to TachoMotor interface. Not yet at that stage though.
  /**
   * Add a TachoMotorListener to this motor. Currently each motor can only have one listener. If you try to add
   * more than one it will replace the previous listener. Usually, the listener
   * will be a Pilot, and only one per program should be controlling motors 
   * @param listener The TachoMotorListener object that will be notified of motor events.
   */
  public void addListener(TachoMotorListener listener)
  {
    this.listener = listener;
  }

  /**
   * @see lejos.nxt.BasicMotor#forward()
   */
  @Override
  public void forward()
  {
    _direction = 1;
    updateState(FORWARD);
  }

  /**
   * @see lejos.nxt.BasicMotor#backward()
   */
  @Override
  public void backward()
  {
    updateState(BACKWARD);
    _direction = -1;
  }

  /**
   * Reverses direction of the motor. It only has
   * effect if the motor is moving.
   */
  public void reverseDirection()
  {
    if (_mode == FORWARD)
    {
      backward();
    } else if (_mode == BACKWARD)
    {
      forward();
    }
  }

  public void flt()
  {
    updateState(FLOAT);
  }

  /**
   * Causes motor to stop, pretty much
   * instantaneously. In other words, the
   * motor doesn't just stop; it will resist
   * any further motion.
   * Cancels any rotate() orders in progress
   */
  public void stop()
  {
    updateState(STOP);
    Delay.msDelay(50);
  }

  /**
   * Applies power to hold motor in current position.  Use if stop() is not good enough<br>
   * to hold the motor in positi0n against a load.
   * @param power - a value between 1 and 100;
   */
  public void lock(int power)
  {
    if (power > 100)  _lockPower = 100;
    if (power < 0)  _lockPower = 0;
    _limitAngle = getTachoCount();
    _lock = true;
  }

  /**
   * returns <b>true</b> if motor position is locked;
   * @return
   */
  public boolean isLocked(){ return _lock;}


  /**
   *calls controlMotor(), regulator.reset().  resets _rotating, _lock; updates _mode
   *called by forward(), backward(),stop(),flt();
   */
   void updateState(int mode)
  {
    _newOperation = true;//cause exit from regulator.stopAtLimit
    _rotating = false;
    synchronized (regulator)
    {
      _lock = false;
      if (_mode == mode) return; // state is not updated

      _rotating = false; //regulator should stop testing for rotation limit  ASAP
      _mode = mode;
      if (_mode == STOP || _mode == FLOAT)
      {
        _tachoPort.controlMotor(0, _mode);
        if (listener != null)
        {
          listener.rotationStopped(this, getTachoCount(), _stalled, System.currentTimeMillis());
        }
        return;
      } else _stalled = false; // new motor motion has started
      if (listener != null)
      {
        listener.rotationStarted(this, getTachoCount(), _stalled, System.currentTimeMillis());
      }
        regulator.reset();
      _tachoPort.controlMotor(_power, _mode);
    }
  }

  /**
   * This method returns <b>true </b> if the motor is attempting to rotate.
   * The return value may not correspond to the actual motor movement.<br>
   * For example,  If the motor is stalled, isMoving()  will return <b> true. </b><br>
   * After flt() is called, this method will return  <b>false</b> even though the motor
   * axle may continue to rotate by inertia.
   *If the motor is stalled, isMoving()  will return <b> true. </b> . A stall can
   * be detected  by calling {@link #getRotationSpeed()} or {@link #getError()};
   * @return true iff the motor if the motor is attempting to rotate.<br>
   */
  public boolean isMoving()
  {
    return (_mode == FORWARD || _mode == BACKWARD || _rotating);
  }

  public void rotate(int angle)
  {
    rotateTo(getTachoCount() + angle);
  }

  public void rotate(int angle, boolean immediateReturn)
  {
    int t = getTachoCount();
    rotateTo(t + angle, immediateReturn);
  }

  public void rotateTo(int limitAngle)
  {
    rotateTo(limitAngle, false);
  }

  public void rotateTo(int limitAngle, boolean immediateReturn)
  {
//    if(_rotating && (_direction *(limitAngle - getTachoCount())>0))
    if(isMoving() && (_direction *(limitAngle - getTachoCount())>0))
    {  // rotation in progress so do not reset regulator
      _limitAngle = limitAngle;  // just change the limitAngle
              _rotating = true;
    }
    else
    {
      _newOperation = true;
      synchronized (regulator)
      {
        _lock = false;
        if (limitAngle > getTachoCount()) forward();
        else backward();
        _limitAngle = limitAngle;
        _rotating = true;
        _newOperation = false;
        regulator.braking = false;
        regulator.speed = 0;
        regulator.reset();
      }
    }
    if (immediateReturn) return;
 
    while (_rotating) Thread.yield();
 
  }
  
  /**
   * @deprecated  this class always uses smooth acceleration
   */
  public void smoothAcceleration(boolean yes) { }

   /**
   *causes run() to exit
   */
  public void shutdown()
  {
    _keepGoing = false;
  }

  /**
   * @deprecated  this class always uses speed regulation.
   * @param  yes is true for speed regulation on
   */
  public void regulateSpeed(boolean yes)
  {
  }



  /** backward();
   * Sets desired motor speed , in degrees per second;
   * The maximum reliably sustaniable speed is  100 x battery voltage under
   * moderate load, such as a direct drive robot on the level.
   * If the parameter is larger than that, the maximum sustaniable value will
   * be used instead.
   * @param speed value in degrees/sec
   */
  public void setSpeed(int speed)
  {
    if (speed > 100 * _voltage)
    {
      speed = (int) (100 * _voltage);// no faster than motor can sustain
    }
    boolean smallChange = Math.abs(speed - _speed)< 200 && isMoving();
    _speed = speed;
    if (speed < 0)
    {
      _speed = -speed;
    }
    setPower((int) regulator.calcPower(_speed));
    regulator.reset(); // reset sets _rampUp = true;
    if(smallChange)_rampUp = false;
  }

  /**
   * sets the acceleration rate of this motor in degrees/sec/sec <br>
   * The default value is 6000; Smaller values will make speeding up. or stopping
   * at the end of a rotate() task, smoother;
   * @param acceleration
   */
  public void setAcceleration(int acceleration)
  {
    _acceleration = Math.abs(acceleration);
  }

  /**
   * returns acceleration in degrees/second/second
   * @return the value of acceleration 
   */
  public int getAcceleration()
  {
    return _acceleration;
  }

/**
 * Sets the motor power.   A negative power will cause the motor to stop and
 * then be treated as 0;   A power greater than 100 will be treated as 100;
 * @param power value should be between 0 and 100;
 */
  public synchronized void setPower(int power)
  {
    if(power <0 )
    {
      _power = 0;
      _tachoPort.controlMotor(0,STOP);
    } else
    {
      if (power > 100) power = 100;
      _power = power;
      _tachoPort.controlMotor(_power, _mode);
    }
  }

  /**
   * Returns the  desired motor speed in degrees per second
   */
  public int getSpeed()
  {
    return _speed;
  }

  /**
   * @return : 1 = forward, 2= backward, 3 = stop, 4 = float
   */
  public int getMode()
  {
    return _mode;
  }

  public int getPower()
  {
    return _power;
  }

  /**
   * Return the angle that this Motor1 is rotating to.
   * @return angle in degrees
   */
  public int getLimitAngle()
  {
    return _limitAngle;
  }

  /**
   * @deprecated this class always uses speed regulation
   */
  public boolean isRegulating()
  {
    return true;
  }

  /**
   * calculates  actual speed and updates battery voltage every 100 ms
   */
  protected void timedOut()
  {
    int angle = getTachoCount();
    _actualSpeed = _direction * 10 * (angle - _lastTacho);
    _lastTacho = angle;
    _voltage = Battery.getVoltage();
  }

  /**
   * @see lejos.robotics.TachoMotor#getRotationSpeed()
   */
  public int getRotationSpeed()
  {
    return _actualSpeed;
  }

  /**
   * Returns true if the motor has stalled
   * @return true if motor has stalled
   */
  public boolean isStalled()
  {
    return _stalled;
  }

  /**
   * @see lejos.robotics.TachoMotor#getTachoCount()
   */
  public int getTachoCount()
  {
    return _tachoPort.getTachoCount();
  }

  /**
   * Resets the tachometer count to zero.
   */
  public void resetTachoCount()
  {
    _tachoPort.resetTachoCount();
     regulator.reset();
  }

  /**
   * Returns the difference between actual tacho count and  predicted count,
   * @return regulator error
   */
  public float getError()
  {
    return regulator.error;
  }

  /**
   * Returns estimated power to maintain motor speed.
   * @return base power of regulator
   */
  public float getBasePower()
  {
    return regulator.basePower;
  }

  /**
   * Sets the power used by the regulator thread to stop the motor at the desired
   * angle of rotation ;  Use by the rotate() methods;
   * @param pwr
   */
  public void setBrakePower(int pwr)
  {
    _brakePower = pwr;
  }

  /**
   *inner class to regulate speed; also stop motor at desired rotation angle
   **/
  class Regulator extends Thread
  {

    /**
     *tachoCount when regulating started
     */
    int angle0 = 0;
    /**
     * set by reset, used  to regulate  motor speed
     */
    float basePower = 0; //used to calculate power
    int time0 = 0; // time regulating started
    float error = 0; // used in calculating power
    float err1 = 0; // used in smoothing
    float err2 = 0; // used in smoothing
    float e0 = 0; // used in smoothing
    float power = 0; // calculated for speed control
    int elapsed = 0; // elapsed time for this rotate() task , ms
    float target = 0;// computed target value of tacho count
    float timeConstant;  // time constand for acceleration exponential decay
    float speed = 0; // record speed at which ramp up quits (because limit angle is close)
    int brakeAngle = 0 ;
    boolean braking = false; // true while braking to a stop
   int brakeStart = 0; // time braking started
    float stopAngle; // angle at which braking should begin

    /**
     * helper method - used by reset and setSpeed() to calculate basePower
     */
    protected float calcPower(int speed)
    {
      float pwr = 50 - (6.8f * _voltage) + 0.1f * _speed;
      if (pwr < 0)
      {
        return 0;
      }
      if (pwr > 100)
      {
        return 100;
      }
      return pwr;
    }

    /**
     * called by forward() backward() and reverseDirection() and setSpeed() <br>
     * resets parameters for speed regulation
     **/
    public void reset()
    {
      _lock = false;
      time0 = (int) System.currentTimeMillis();
      angle0 = getTachoCount();
      basePower = calcPower(_speed);
      setPower((int) basePower);
      e0 = 0;
      err1 = 0;
      err2 = 0;
      timeConstant = 1000 * _speed / _acceleration;
      speed = 0;
      braking = false;
      _rampUp = true;
    }

    /**
     * Monitors time and tachoCount to regulate speed and stop motor rotation at limit angle
     */
    public void run()
    {
      int tick = 100 + (int) System.currentTimeMillis(); //
      while (_keepGoing)
      {
        if ((int) System.currentTimeMillis() >= tick)// simulate timer
          {
            tick += 100;
            timedOut();
          }
        synchronized (this) // the main speed control loop
        {        
          if (_lock)
          {
            doLock();
          } else if (isMoving())
          /*****  regulate speed ************/
          {
            elapsed = (int) System.currentTimeMillis() - time0;
            int tachoCount = getTachoCount();
            int angle = tachoCount - angle0;
            int absA = Math.abs(angle);
            stopAngle = _limitAngle - _direction * speed * speed / _acceleration;
            if(!_rampUp)stopAngle =  _limitAngle - _direction * _speed * _speed / _acceleration;
            if (_rotating && _direction * (tachoCount - stopAngle) >= 0 && !braking)//Decelerate now
            {
//      set variables for beginning of deceleration
              braking = true;
              brakeStart = elapsed;
              brakeAngle = getTachoCount() - angle0;
            }
            if (braking)
            {
              int timeFactor = 3000;
// close to limit angle ?  if so , stop  Note: speed is deg/sec ; but  elapsed in ms
              if (Math.abs(_limitAngle - getTachoCount()) < 2 ||
                      (elapsed - brakeStart) > timeFactor*speed/_acceleration)
              {
                stopAtLimit();
                braking = false;
              } else // keep braking
              { // acceleration decays to zero with given time constant
                float factor = 1 - (float) Math.exp(-(elapsed - brakeStart) / timeConstant);
                target = Math.abs(brakeAngle) + speed * timeConstant * factor / 1000;
              }
            } else
  // not braking so calculate power to regulate speed
            {
              if (elapsed < timeConstant * 4 &&_rampUp)// not at speed yet
              {
                float factor = 1 - (float) Math.exp(-elapsed / timeConstant);
                target = _speed * (elapsed - timeConstant * factor) / 1000f;
                speed = _speed * factor;  // decays toward _speed
              } else  // adjust elapsed time for acceleration time - don't try to catch up
              {
                if(_rampUp)target = ((elapsed - timeConstant) * _speed) / 1000f;// timeConstant should be 0 of no ramp
                else target = elapsed *_speed/1000f;
              }
            }
            error = target - absA;
            // check for stall
            if (error > _stallLimit)
            {
              _stalled = true;
              _thisMotor.stop();
            } else
              calcPower(error);
         
            // end speed regulation
            }
          // end synchronized block
        }
        Delay.msDelay(4);

      }	// end keep going loop
      }// end run
/**
 * helper method for speed regulation.
 * calculates power from error using double smoothing.
 * sets basePoser and calles setPower();
 * @param error
 */
    private void calcPower(float error)
    {
      // use smoothing to reduce the noise in frequrent tacho count readings
      err1 = 0.5f * err1 + 0.5f * error;  // fast smoothing
      err2 = 0.8f * err2 + 0.2f * error; // slow smoothing
      float gain = 4f; // feedback constant
      float extrap = 8f; // feedback constant
      power = basePower + gain * (err1 + extrap * (err1 - err2));
      e0 = error;
      float smooth = 0.04f;// .04 another magic number from experimen;
      basePower = basePower + smooth * (power - basePower);
      setPower((int) power);
    }
    /**
     * helper method for run()
     */
    void stopAtLimit()  // converge to limit angle; reverse direction if necessary
    {
      _mode = STOP; // stop motor
      _tachoPort.controlMotor(0, STOP);
      int err0 = 0;// former error

      err0 = angleAtStop();//returns when motor speed < 100 deg/sec
      err0 -= _limitAngle;
      int k = 0; // time within limit angle +=1
      int t1 = 0; // time since change in tacho count
      int err = 0;
      int pwr = _brakePower;// local power
      // exit within +-1  for 40 ms  or motor mathod is called
      while (k < 40 && !Motor.this._newOperation)
      {
        err = _limitAngle - getTachoCount();
        if (err == err0)  // no change in tacho count
        {
          t1++;
          if (t1 > 20)// speed < 50 deg/sec so increase brake power
          {
            pwr += 10; // increase power  if outside limit angle +- 1 deg
            if (pwr > 120)  // chekd for stall
            {
              _stalled = true;
              _thisMotor.stop();
            }
            t1 = 0;
          }

        } else // tacho count changed
        {
          t1 = 0;
          if (err == 0)pwr = _brakePower;
          err0 = err;
        }
        if (err < -1)
        {
          _mode = BACKWARD;
          setPower(pwr);
          k = 0;
        } else if (err > 1)
        {
          _mode = FORWARD;
          setPower(pwr);
          k = 0;
        } else
        {
          _mode = STOP;
          _tachoPort.controlMotor(0, STOP);
          k++;
        }
        Delay.msDelay(1);
      }
      _rotating = false;
      setPower((int) calcPower(_speed));
      if (listener != null)
      {
        listener.rotationStopped(Motor.this, getTachoCount(), _stalled, System.currentTimeMillis());
      }
    }

    /**
     *helper method for stopAtLimit - returns when speed   < 100 deg/sec
     **/
    int angleAtStop()
    {
      int a0 = getTachoCount();
      boolean turning = true;
      int a = 0;
      while (turning)
      {

        _tachoPort.controlMotor(0, STOP);
        Delay.msDelay(10);
        a = getTachoCount();
        turning = a != a0;
        a0 = a;
      }
      return a;
    }

    protected void doLock()
    {
      int count = getTachoCount();
      if (count < _limitAngle - 1)
      {
        _mode = FORWARD;
        _tachoPort.controlMotor(_lockPower, _mode);
      } else if (count > _limitAngle + 1)
      {
        _mode = BACKWARD;
        _tachoPort.controlMotor(_lockPower, _mode);
      } else
      {
        _tachoPort.controlMotor(0, STOP);
      }
    }
  }

 
}
