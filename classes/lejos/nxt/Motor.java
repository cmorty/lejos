package lejos.nxt;




import lejos.nxt.*;
import lejos.robotics.TachoMotor;
import lejos.robotics.TachoMotorListener;
import lejos.util.Delay;

import lejos.util.Datalogger;

/**
 * Abstraction for a NXT motor. Three instances of <code>Motor</code>
 * are available: <code>Motor.A</code>, <code>Motor.B</code>
 * and <code>Motor.C</code>.  The basic control methods are:
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
 * Motor has 2 modes : speedRegulation and smoothAcceleration
 * which only works if speed regulation is used. Both modes are set by default.
 * At the start of a rotate() task, smooth acceleration ramps the motor apeed  up
 * and then down at the end. The control algorithm assumes that the motor is not moving
 * at the start.
 * You can control the smoothness of the ramp by using
 * {@link #setAcceleration(int) <code> setAcceleration()</code>}   
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
 *   Motor.A.setSpeed(720);// 2 RPM
 *   Motor.C.setSpeed(720);
 *   Motor.A.forward();
 *   Motor.C.forward();
 *   Thread.sleep (1000);
 *   Motor.A.stop();
 *   Motor.C.stop();
 *   Motor.A.rotateTo( 360);
 *   Motor.A.rotate(-720,true);
 *   while(Motor.A.isRotating() :Thread.yield();
 *   int angle = Motor.A.getTachoCount(); // should be -360
 *   LCD.drawInt(angle,0,0);
 * </pre></code>
 * @author Roger Glassey revised 9 Feb 2008 - added lock() method. 
 */
public class Motor extends BasicMotor implements TachoMotor // implements TimerListener
{

  private  TachoMotorPort _port;//** private
   /*
   * default speed  deg/sec
   */
  private int _speed = 360;
  private boolean _keepGoing = true;// for regulator
  /**
   * Initially true; changed only by regulateSpeed().<br>
   * used by Regulator, updateState, reset
   */
  private boolean _regulate = true;
  public Regulator regulator = new Regulator();
  /**
   * sign of the rotation direction. set by forward(), backward()
   */
  private byte _direction = 1;
  /*
   * angle at which rotation ends.  Set by rotateTo(), used by regulator
   */
  private int _limitAngle; //set by rotate()
   /*
   * angle at which regulator begins to stop the motor
   */
//  private int _stopAngle;
  /*
   * true when rotation to limit is in progress.  set by rotateTo(), used  and reset by regulator
   */
  private boolean _rotating = false;
  /**
   * set by smoothAcceleration, forward(),backward(), setSpeed().  Only has effect if _regulate is true
   */
  private boolean _rampUp = true;
  /** initialized true (ramping enabled); changed only by smoothAcceleration
   * used by forward(),backward(), setSpeed() a value of _rampUp only if motor mode changes.
   */
  private boolean _useRamp = true;
  /**
   * used by timedOut to calculate actual speed;
   */
  private int _lastTacho = 0;
  /**
   * set by timedOut
   */
  private int _actualSpeed;
  private float _voltage = 0f;

  private int _acceleration = 6000;// default acceleration 6000 deg/sec/sec

  private boolean _lock = false;
  private int _brakePower = 20;  //used while nudging toward final angle
  private int _lockPower = 30;
  private boolean _stalled = false;//set by regulator when stall is detected
  private int _stallLimit = 1200;
  private boolean _newOperation = false;// used by regulator.stopAtLimit

  private Motor _thisMotor = this; // alias  for use by regulator
  /**
   * Motor A.
   */
  public static final Motor A = new Motor(MotorPort.A);
  /**
   * Motor B.
   */
  public static final Motor B = new Motor(MotorPort.B);
  /**
   * Motor C.
   */
  public static final Motor C = new Motor(MotorPort.C);
  /**
   * TODO: Currently only accepts one listener. We could expand this to multiple if needed.
   */
  private TachoMotorListener listener = null;

  /**
   * Use this constructor to assign a variable of type motor connected to a particular port.
   * @param port  to which this motor is connected
   */
  public Motor(TachoMotorPort port)
  {
    _port = port;
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
    if (_mode == FORWARD)
    {
      _rampUp = false;
    } else
    {
      _rampUp = _useRamp;
    }
    if (_mode == BACKWARD)
    {
      stop();
    }
    _direction = 1;
    updateState(FORWARD);
  }

  /**
   * @see lejos.nxt.BasicMotor#backward()
   */
  @Override
  public void backward()
  {
    if (_mode == BACKWARD)
    {
      _rampUp = false;
    } else
    {
      _rampUp = _useRamp;
    }
    if (_mode == FORWARD)
    {
      stop();
    }
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
  }

  /**
   * Applies power to hold motor in current position.  Use if stop() is not good enough<br>
   * to hold the motor in positi0n against a load.
   * @param power - a value between 1 and 100;
   */
  public void lock(int power)
  {
    if (power > 100)
    {
      _lockPower = 100;
    }
    if (power < 0)
    {
      _lockPower = 0;
    }
    _limitAngle = getTachoCount();
    _lock = true;
  }

  /**
   *calls controlMotor(), regulator.reset().  resets _rotating, _lock; updates _mode
   *called by forwsrd(), backward(),stop(),flt();
   */
  void updateState(int mode)
  {
    _newOperation = true;//cause exit from regulator.stopAtLimit
    synchronized (regulator)
    {
      _rotating = false; //regulator should stop testing for rotation limit  ASAP
      _lock = false;
      if (_mode == mode)
      {
        return;
      }
      _mode = mode;
      if (_mode == STOP || _mode == FLOAT)
      {
        _port.controlMotor(0, _mode);
        if (listener != null)
        {
          listener.rotationStopped(this, getTachoCount(), _stalled, System.currentTimeMillis());
        }
        return;
      } else
      {
        _stalled = false; // new motor motion has started
      }
      if (listener != null)
      {
        listener.rotationStarted(this, getTachoCount(), _stalled, System.currentTimeMillis());
      }
      _port.controlMotor(_power, _mode);
      if (_regulate)
      {
        regulator.reset();
      }
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
    _newOperation = true;
    synchronized (regulator)
    {
      _lock = false;
      if (limitAngle > getTachoCount())
      {
        forward();
      } else
      {
        backward();
      }
//        distanc to stop from current speed at constant deceleration.
      int stopDistance =   _speed * _speed / (2 * _acceleration);
      stopDistance = (int) Math.max(stopDistance,_speed * 0.075f);
      _limitAngle = limitAngle;
      _rotating = true;
      _newOperation = false;
      regulator.braking = false;
      regulator.speed = 0;
       regulator.timeConstant = 1000 * _speed / _acceleration;
    }
    if (immediateReturn)
    {
      return;
    }
    while (_rotating)
    {
      Thread.yield();
    }
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
     float gain = 4f; // feedback constant
     float extrap = 8f; // feedback constant
     int elapsed = 0; // elapsed time for this rotate() task , ms
     float target = 0;// computed target value of tacho count
     float timeConstant;  // time constand for acceleration exponential decay
     float speed = 0; // record speed at which ramp up quits (because limit angle is close)
     boolean braking = false; // true while braking to a stop
     int brakeStart = 0; // time braking started
     int brakeAngle = 0; // tacho count of braking start
     int tachoCount = 0;// save the tacho count
     float stopAngle; // angle at which braking should begin
    /**
     * helper method - used by reset and setSpeed() to calculate basePower
     */
    private float calcPower(int speed)
    {
      float pwr = 50 - (6.8f * _voltage) + 0.1f*_speed;
      if (pwr < 0) return 0;
      if (pwr > 100)return 100;
      return pwr;
    }

    /**
     * called by forward() backward() and reverseDirection() <br>
     * resets parameters for speed regulation
     **/
    public void reset()
    {
      _lock = false;
      if (!_regulate)
      {
        return;
      }
      time0 = (int) System.currentTimeMillis();
      angle0 = getTachoCount();
      basePower = calcPower(_speed);
      setPower((int) basePower);
      e0 = 0;
      err1 = 0;
      err2 = 0;
    }

    /**
     * Monitors time and tachoCount to regulate speed and stop motor rotation at limit angle
     */
    public void run()
    {
      int tick = 100 + (int) System.currentTimeMillis(); //
      while (_keepGoing)
      {
        synchronized (this)
        {
          if ((int) System.currentTimeMillis() >= tick)// simulate timer
          {
            tick += 100;
            timedOut();
          }
          if (_lock)
          {
            doLock();
          } else if (_regulate && isMoving())
    /*****  regulate speed ************/
          {
            elapsed = (int) System.currentTimeMillis() - time0;
            tachoCount = getTachoCount();
            int angle = tachoCount - angle0;
            int absA = angle;
            if (angle < 0) absA = -angle;
            stopAngle = _limitAngle - _direction *speed*speed/_acceleration;
            if (_rotating && _direction * (tachoCount - stopAngle) >= 0 && !braking)//Decelerate now
            {
//      set variables for beginning of deceleration
              braking = true;
              brakeStart = elapsed;
              brakeAngle = tachoCount - angle0;
            }
            if (braking)
            {
// close to limit angle ?  if so , stop  Note: speed is deg/sec - elapsed in ms
              if(Math.abs(_limitAngle - tachoCount ) < 2 ||
                      (elapsed -brakeStart ) >  4000 * speed/_acceleration)
              {
                stopAtLimit();
              }
              else // keep braking
              { // acceleration decays to zero with given time constant
                float factor = 1 - (float) Math.exp(-(elapsed -brakeStart)/ timeConstant);
                target = Math.abs(brakeAngle) + speed* timeConstant*factor/1000;
              }
            } else
       // not braking
            {
              if (elapsed < timeConstant * 4)// not at speed yet
              {
                float factor = 1 - (float) Math.exp(-elapsed / timeConstant);
                target = _speed * (elapsed - timeConstant * factor) / 1000f;
                speed = _speed * factor;  // decays toward _speed
              } else  // adjust elapsed time for acceleration time - don't try to catch up
              {
                target = ((elapsed - timeConstant) * _speed) / 1000f;
              }
            }
            error =target - absA;
            // chedk for stall
            if (error > _stallLimit)
            {
              _stalled = true;
              _thisMotor.stop();
            }
            else
            {
              // use smoothing to reduce the noize in frequrent tacho count readings
              err1 = 0.5f * err1 + 0.5f * error;  // fast smoothing
              err2 = 0.8f * err2 + 0.2f * error; // slow smoothing
              power = basePower + gain * (err1 + extrap * (err1 - err2));
              e0 = error;
              if (power <= 0)
              {
                  _port.controlMotor(0, STOP);  //apply brake
              }
              if (power > 100)
              {
                power = 100;
              }
              float smooth = 0.04f;// .04 another magic number from experimen;
              basePower = basePower + smooth * (power - basePower);
              setPower((int)power);
            }
            // end speed regulation
            }
          // end synchronized block

        }
        Delay.msDelay(4);

      }	// end keep going loop
      }// end run


    /**
     * helper method for run()
     */
    void stopAtLimit()  // converge to limit angle; reverse direction if necessary
    {
      _mode = STOP; // stop motor
      _port.controlMotor(0, STOP);
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
          if (err == 0)
          {
            pwr = _brakePower;
          }
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
          _port.controlMotor(0, STOP);
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
        _port.controlMotor(0, STOP);
        Delay.msDelay(10);
        a = getTachoCount();
        turning = a != a0;
        a0 = a;
      }
      return a;
    }

    private void doLock()
    {
      int count = getTachoCount();
      if (count < _limitAngle - 1)
      {
        _mode = FORWARD;
        _port.controlMotor(_lockPower, _mode);
      } else if (count > _limitAngle + 1)
      {
        _mode = BACKWARD;
        _port.controlMotor(_lockPower, _mode);
      } else
      {
        _port.controlMotor(0, STOP);
      }
    }
  }

  /**
   *causes run() to exit
   */
  public void shutdown()
  {
    _keepGoing = false;
  }

  /**
   * turns speed regulation on/off; default is on. <br>
   * When turned on,  the cumulative speed error is typically  within about 1
   * degree after initial acceleration.
   * @param  yes is true for speed regulation on
   */
  public void regulateSpeed(boolean yes)
  {
    _regulate = yes;
    if (yes)
    {
      regulator.reset();
    }
  }

  /**
   * enables smoother acceleration; default is on.  Motor speed increases gently,  and does not <>
   * overshoot when regulate Speed is used.
   *
   */
  public void smoothAcceleration(boolean yes)
  {
    _rampUp = yes;
    _useRamp = yes;
  }

  /**
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
    if (speed > _speed + 300 && isMoving())
    {
      _rampUp = _useRamp;
    } else
    {
      _rampUp = false;
    }
    _speed = speed;
    if (speed < 0)
    {
      _speed = -speed;
    }
    setPower((int) regulator.calcPower(_speed));
    regulator.reset();
  }
/**
 * sets the acceleration rate of this motor in degrees/sec/sec <br>
 * The default value is 6000;
 * @param acceleration
 */
  public void setAcceleration(int acceleration)
  {
    _acceleration = Math.abs(acceleration);
  }
  
/**
 * returns acceleration
 * @return the value of acceleration used by smoothAcceleration
 */
public int getAcceleration()
{
  return _acceleration;
}

  /**
   *sets motor power.  This method is used by the Regulator thread to control motor speed.
   *Warning:  negative power will cause the motor to run in reverse but without updating the _direction
   *field which is used by the Regulator thread.  If the speed regulation is enabled, the results are
   *unpredictable.
   */
  public synchronized void setPower(int power)
  {
    _power = power;
    _port.controlMotor(_power, _mode);
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
   * Return the angle that this Motor is rotating to.
   * @return angle in degrees
   */
  public int getLimitAngle()
  {
    return _limitAngle;
  }

  /**
   * @return <b>true</b> if speed regulation is turned on;
   */
  public boolean isRegulating()
  {
    return _regulate;
  }

  /**
  * calculates  actual speed and updates battery voltage every 100 ms
   */
  private void timedOut()
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
    return _port.getTachoCount();
  }

  /**
   * Resets the tachometer count to zero.
   */
  public void resetTachoCount()
  {
    regulator.reset();
    _port.resetTachoCount();
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
}
