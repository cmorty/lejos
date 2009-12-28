package lejos.nxt;



import lejos.robotics.MotorEvent;
import lejos.robotics.TachoMotor;
import lejos.robotics.TachoMotorListener;
import lejos.util.Delay;


/**
 * Abstraction for a NXT motor. Three instances of <code>Motor</code>
 * are available: <code>Motor.A</code>, <code>Motor.B</code>
 * and <code>Motor.C</code>. To control each motor use
 * methods <code>forward, backward, reverseDirection, stop</code>
 * and <code>flt</code>. To set each motor's speed, use
 * <code>setSpeed.  </code> Speed is in degrees per second.
 * Methods that use the tachometer:  regulateSpeed, rotate, rotateTo.
 * These rotate methods may not stop smoothly at the target angle if called when
 * the motor is already moving<br>
 * Motor has 2 modes : speedRegulation and smoothAcceleration,
 * which only works if speed regulation is used. These are initially enabled.
 * The speed is regulated by comparing the tacho count with speed times elapsed
 * time and adjusting motor power to keep these closely matched.
 * Smooth acceleration corrects the speed regulation to account for the acceleration time. 
 * They can be switched off/on by the methods regulateSpeed() and smoothAcceleration().
 * The actual maximum speed of the motor depends on battery voltage and load.
 * With no load, the maximum is about 100 times the voltage.
 * Speed regulation fails if the target speed exceeds the capability of the motor.
 * If you need the motor to hold its position and you find that still moves after
 * stop() is called , you can use the lock() method.
 *  
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
 *   while(Motor.A.isRotating();
 *   int angle = Motor.A.getTachoCount(); // should be -360
 * </pre></code>
 * @author Roger Glassey revised 9 Feb 2008 - added lock() method. 
 */
public class Motor extends BasicMotor implements TachoMotor // implements TimerListener
{   

   public TachoMotorPort _port;//** private
   /*
    * default speed
    */
   private int _speed = 360;
 
   private boolean _keepGoing = true;// for regulator
   /**
    * Initially true; changed only by regulateSpeed(),<br>
    * used by Regulator, updteState, reset*
    */
   private boolean _regulate = true;

   public Regulator regulator = new Regulator();

   /**
    * sign of the rotation direction. set by forward(), backward()
    */
   private byte  _direction = 1;
   /*
    * angle at which rotation ends.  Set by rotateTo(), used by regulator
    */
   private int _limitAngle; //set by rotate()
   /*
    * angle at which regulator begins to stop the motor
    */
   private int _stopAngle; 
   /*
    * true when rotation to limit is in progress.  set by rotateTo(), used  and reset by regulator
    */
   private boolean _rotating = false;
 
   /**  
    * set by smoothAcceleration, forward(),backward(), setSpeed().  Only has effect if _regulate is true
    */
   public boolean _rampUp = true; 
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
   
   private boolean _lock = false;
   
   private int _brakePower = 20;
   private int _lockPower = 30;
   private boolean _stalled = false;//set by regulator when stall is detected
   private boolean _newOperation = false;// used by regulator.stopAtLimit
  private Motor  _thisMotor = this; // alias  for use by regulator

   /**
    * Motor A.
    */
   public static final Motor A = new Motor (MotorPort.A);

   /**
    * Motor B.
    */
   public static final Motor B = new Motor (MotorPort.B);

   /**
    * Motor C.
    */
   public static final Motor C = new Motor (MotorPort.C);
   
   /**
    * TODO: Currently only accepts one listener. We could expand this to multiple if needed.
    */
   private TachoMotorListener listener = null;
   
   /**
    * Use this constructor to assign a variable of type motor connected to a particular port.
    * @param port  to which this motor is connected
    */
   public Motor (TachoMotorPort port)
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
    * more than one it will replace the previous listener.
    * @param listener The TachoMotorListener object that will be notified of motor events.
    */
   public void addListener(TachoMotorListener listener) {
	   this.listener = listener;
   }

   /**
    * Returns the tacho count at which the regulator thread begin to stop the
    * motor before reaching the desired angle. Usec by the rotate() methods.
    * @return angle
    */
   public int getStopAngle() { return _stopAngle;}

   /**
    * @see lejos.nxt.BasicMotor#forward()
    */
   @Override
   public void forward()
   { 
     if(_mode == FORWARD)_rampUp = false;
     else _rampUp = _useRamp;
     if(_mode == BACKWARD)stop();
     _direction = 1;
     updateState( FORWARD);
   }  

   /**
    * @see lejos.nxt.BasicMotor#backward()
    */
   @Override
   public void backward()
   {
     if(_mode == BACKWARD )_rampUp = false;
     else _rampUp = _useRamp;
     if(_mode == FORWARD)stop();
     updateState( BACKWARD);
     _direction = -1;
   }

   /**
    * Reverses direction of the motor. It only has
    * effect if the motor is moving. 
    */
   public void reverseDirection()
   {
      if (_mode == FORWARD)backward();
      else
         if (_mode == BACKWARD)forward();
   }

   public void flt() {
      updateState( FLOAT);
   }

   /**
    * Causes motor to stop, pretty much
    * instantaneously. In other words, the
    * motor doesn't just stop; it will resist
    * any further motion.
    * Cancels any rotate() orders in progress
    */
   public void stop() {
      updateState( STOP);
   }
   /**
    * Applies power to hold motor in current position.  Use if stop() is not good enough<br>
    * to hold the motor in positi0n against a load.
    * @param power - a value between 1 and 100;
    */
   public void lock(int power)
   {
      if(power > 100 )_lockPower = 100;
      if(power < 0 )_lockPower = 0;
      _limitAngle = getTachoCount();
      _lock = true;
   }


   /** 
    *calls controlMotor(), regulator.reset().  resets _rotating, _lock; updates _mode
    *called by forwsrd(), backward(),stop(),flt();
    */
   void updateState( int mode)
   {
     _newOperation = true;//cause exit from regulator.stopAtLimit
      synchronized(regulator)
      {
         _rotating = false; //regulator should stop testing for rotation limit  ASAP
         _lock = false;
         if( _mode == mode)
            return;
         _mode = mode;
         if(_mode == STOP || _mode == FLOAT)
         {
            _port.controlMotor(0, _mode);
            if(listener != null) listener.rotationStopped(new MotorEvent(this, getTachoCount()));
            return;
         }
         else _stalled = false; // new motor motion has started
         if(listener != null) listener.rotationStarted(new MotorEvent(this, getTachoCount()));
         _port.controlMotor(_power, _mode);
         if(_regulate)regulator.reset();
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
      rotateTo(getTachoCount()+angle);
   }

   public void rotate(int angle, boolean immediateReturn)
   {
     int t = getTachoCount();
     rotateTo(t+angle,immediateReturn);
   }

   public void rotateTo(int limitAngle)
   {
      rotateTo(limitAngle,false);
   }
   
   public void rotateTo(int limitAngle,boolean immediateReturn)
   {
       _newOperation = true;
      synchronized(regulator)
      {
         _lock = false;
         _limitAngle = limitAngle;
         if (_limitAngle == Integer.MAX_VALUE || _limitAngle == Integer.MIN_VALUE)
                 _limitAngle = _limitAngle >> 1;
         if(_limitAngle > getTachoCount()) forward();
         else backward();
         int os = overshoot(limitAngle - getTachoCount()); 
         _stopAngle -= _direction * os;//overshoot(limitAngle - getTachoCount());  
         _limitAngle = limitAngle;
         _rotating = true;
         _newOperation = false;
      }
      if(immediateReturn)return;
      while(_rotating || _stalled)
      {
        if(_stalled)throw new MotorStalledException(getName()+" stalled ");
        Thread.yield();
      }
   }

   /**
    *inner class to regulate speed; also stop motor at desired rotation angle
    **/
   public class Regulator extends Thread
   {
      /**
       *tachoCount when regulating started
       */
      int angle0 = 0;
      /**
       * set by reset, used  to regulate  motor speed
       */ 
      int basePower = 0; // power x 10 for accurate integer arithmetic
      /**
       * time regulating started
       */
      int time0 = 0;
     int error = 0;
      int e0 = 0;
      /**
       * helper method - used by reset and setSpeed()
       */
      int calcPower(int speed)
      {   
         int pwr = 100 -(int)(11*_voltage) +11*_speed/100;
         if(pwr<0) return 0;
         if(pwr>100)return 100;
         else return pwr;
      }

      /**
       * called by forward() backward() and reverseDirection() <br>
       * resets parameters for speed regulation
       **/
      public void reset()
      {
         _lock = false;
         if(!_regulate)return;
         time0 = (int)System.currentTimeMillis();
         angle0 = getTachoCount();
         basePower = calcPower(_speed);
         setPower(basePower);
         basePower *=10;  // scale for better integer arithmetic
         e0 = 0;
      }

      /**
       * Monitors time and tachoCount to regulate speed and stop motor rotation at limit angle
       */
      public void run()
      {
         int power =  0;
        int ts = 120;//time to reach speed
         int tick = 100+ (int)System.currentTimeMillis(); // 
         while(_keepGoing)
         { synchronized(this)
            {    
               if((int)System.currentTimeMillis()>= tick)// simulate timer
               {
                  tick += 100;
                  timedOut();
               }
               if(_lock)
               {
                  int tc = getTachoCount();
                  if( tc < _limitAngle -1)
                  {
                     _mode = FORWARD;
                     _port.controlMotor(_lockPower, _mode);
                  }
                  else if (tc > _limitAngle +1)
                  {
                     _mode = BACKWARD;
                     _port.controlMotor(_lockPower, _mode);
                  }
                  else _port.controlMotor(0, STOP);
               }
               else  if(_rotating && _direction*(getTachoCount() - _stopAngle)>=0)  stopAtLimit();  // was >0
               else if(_regulate && isMoving()) //regulate speed 
               {
                  int elapsed = (int)System.currentTimeMillis()-time0;
                  int angle = getTachoCount()-angle0;
                  int absA = angle;
                  if(angle<0)absA = -angle;
                  if(_rampUp)  // smooth start
                  {   
                     if(elapsed<ts)// not at speed yet
                     {
                        error = elapsed*elapsed*_speed*7/(ts*2000); //constant acceleration
                        error = error+  elapsed*_speed*3/2000;//constant speed
                        error = error - 10*absA;
                     }
                     else  // adjust elapsed time for acceleration time - don't try to catch up
                     {
                        error = ((elapsed - ts/2)* _speed)/100 - 10*absA;
                     }
                  } //end if ramp up
                  else 	error = (elapsed*_speed/100)- 10*absA;// no ramp
                  if(error > 400 )
                  {
                    _stalled = true;
                    _thisMotor.stop();
                  }
                 int gain = 5;
                  int extrap = 4;
                  power = basePower/10 + gain*(error + extrap*(error - e0))/10;
                  e0 = error;                 
                  if(power < 0) power = 0;
                  if(power > 100) power = 100;
                  int smooth = 12;//  /.012 another magic number from experiment
                  basePower = basePower +smooth*(10*power-basePower)/1000;
                  setPower(power);
               }// end speed regulation 
            }// end synchronized block
         Delay.msDelay(4);
         }	// end keep going loop
      }// end run
      /**
       * helper method for run()
       */
      void stopAtLimit()  // converge to limit angle; reverse direction if necessary
      {  
         _mode = STOP; // stop motor
         _port.controlMotor (0, STOP);
         int err0 = 0;// former error
         err0 = angleAtStop();//returns when motor speed < 100 deg/sec
         err0 -= _limitAngle;
         int k = 0; // time within limit angle +=1
         int t1 = 0; // time since change in tacho count
         int err = 0;
         int pwr = _brakePower;// local power
         // exit within +-1  for 40 ms  or motor mathod is called
         while ( k < 40 && !Motor.this._newOperation)
         {
            err = _limitAngle - getTachoCount();
            if (err == err0)  // no change in tacho count
            {  
               t1++;
               if( t1 > 20)// speed < 50 deg/sec so increase brake power
                  {
                  pwr += 10;  
                  if(pwr > 120)
                  {
                    _stalled = true;
                    _thisMotor.stop();
                  }

                  t1 = 0;
                  }                           
               // don't let motor stall if outside limit angle +- 1 deg
            }
            else // tacho count changed
            {
               t1 = 0;
               if( err == 0) pwr = _brakePower;
               err0 = err;
            }
            if(err < -1)
            {
               _mode = BACKWARD;
               setPower(pwr);
               k = 0;
            }
            else if (err > 1 )
            {
               _mode = FORWARD ;
               setPower(pwr);
               k = 0;
            }
            else 
            {
               _mode = STOP;
               _port.controlMotor (0, STOP);
               k++;
            } 
            Delay.msDelay(1);
         }
         _rotating = false;
         setPower(calcPower(_speed));
         if(listener != null) listener.rotationStopped(new MotorEvent(Motor.this, getTachoCount()));
      }

      /**
       *helper method for stopAtLimit - returns when speed   < 100 deg/sec
       **/
      int angleAtStop()
      {
         int a0 = getTachoCount();
         boolean turning = true;
         int a = 0;
         while(turning)
         {
            _port.controlMotor(0,STOP);
            Delay.msDelay(10);
            a = getTachoCount();
            turning = a != a0;
            a0 = a;        
         }
         return	a;
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
      if(yes)regulator.reset();
   }

   /**
    * enables smoother acceleration.  Motor speed increases gently,  and does not <>
    * overshoot when regulate Speed is used. 
    * 
    */
   public void smoothAcceleration(boolean yes) 
   {
      _rampUp = yes;
      _useRamp =  yes;
   }

   /**
    * Sets desired motor speed , in degrees per second;
    * The maximum reliably sustaniable speed is  100 x battery voltage under
    * moderate load, such as a direct drive robot on the level.
    * If the parameter is larger than that, the maximum sustaniable value will
    * be used instead.
    * @param speed value in degrees/sec  
    */
   public void setSpeed (int speed)
   {
      if(speed > 100*_voltage) speed = (int)(100*_voltage);// no faster than motor can sustain
      if(speed > _speed + 300 && isMoving()) _rampUp = _useRamp;
      else _rampUp = false;
      _speed = speed;
      if(speed<0)_speed = - speed;
      setPower(regulator.calcPower(_speed));
      regulator.reset();
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
      _port.controlMotor (_power, _mode);
   }

   /**
    * Returns the current desired motor speed in degrees per second
    */
   public int getSpeed()
   {
      return _speed;	  
   } 
   /**
    * @return : 1 = forward, 2= backward, 3 = stop, 4 = float
    */
   public int getMode() {return _mode;}
   public int getPower() { return _power;}
   /**
    * used by rotateTo to calculate stopAngle from limitAngle
    * @return absolute value of overshoot
    */
   private int overshoot(int angle)
   {
      float ratio =0.06f; // overshoot/speed  - magic number from experiments  .064
      if(!_regulate)ratio = -0.173f + 0.029f * _voltage;// more magic numbers - fit to data
      if (angle < 0 ) angle = -angle;

       float endRamp = _speed*0.12f;
     if( angle < endRamp)
     { // more complicated calculation in this case
      float a  = angle/endRamp;// normalized angle
      ratio = .05f*( 1 - (1 - a)*(1 - a)); // quadratic in normalized angle
     }
      return (int) (ratio* _speed);
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
   /* calculates  actual speed and updates battery voltage every 100 ms
    */
   private void timedOut()
   {
      int angle = getTachoCount();
      _actualSpeed = 10*(angle - _lastTacho);
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
      public boolean isStalled(){return _stalled;}

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
    * based on speed * elapsed time.
    * @return regulator error
    */
   public float getError()
   {
      return regulator.error/10;
   }

   /**
    * Returns estimated power to maintain motor speed.
    * @return base power of regulator
    */
   public float getBasePower()
   {
      return regulator.basePower/10;
   }
   /**
    * Sets the power used by the regulator thread to stop the motor at the desired
    * angle of rotation ;  Use by the rotate() methods;
    * @param pwr
    */
   public void setBrakePower(int pwr) {_brakePower = pwr;}

   public String getName()
   {
     String n = "Motor.C ";
     if(this == Motor.A) n =  "Motor.A ";
     else if(this == Motor.B ) n = "Motor.B ";
     return n;
   }

  public  class MotorStalledException extends RuntimeException
  {
    MotorStalledException(String message)
    {
     super(message);
    }
  }
}







