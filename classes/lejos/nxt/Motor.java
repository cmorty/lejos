package lejos.nxt;
import lejos.nxt.Battery;
import lejos.nxt.*;


/**
 * Abstraction for a NXT motor. Three instances of <code>Motor</code>
 * are available: <code>Motor.A</code>, <code>Motor.B</code>
 * and <code>Motor.C</code>. To control each motor use
 * methods <code>forward, backward, reverseDirection, stop</code>
 * and <code>flt</code>. To set each motor's speed, use
 * <code>setSpeed.  Speed is in degrees per second. </code>.\
 * Methods that use the tachometer:  regulateSpeed, rotate, rotateTo <br>
 * Motor has 2 modes : speedRegulation and smoothAcceleration which only works if speed regulation is used. These are initially enabled. <>
 * They can be switched off/on by the methods regulateSpeed() and smoothAcceleration().
 * The actual maximum speed of the motor depends on battery voltage and load. With no load, the maximum is about 100 times the voltage.  
 * Speed regulation fails if the target speed exceeds the capability of the motor.
 * If your motor is working against a heavy load, you might need to increase the brake power to insure that a rotation is completed.
 * If you need the motor to hold its position and you find that still moves after stop() is called , you can use the lock() method.
 * 
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
 *   Motor.A.regulateSpeed(true);
 *   Motor.A.rotateTo( 360);
 *   Motor.A.rotate(-720,true);
 *   while(Motor.A.isRotating();
 *   int angle = Motor.A.getTachoCount(); // should be -360
 * </pre></code>
 * @author Roger Glassey revised 9 Feb 2008 - added lock() method. 
 */
public class Motor extends BasicMotor// implements TimerListener
{  

   public TachoMotorPort _port;//** private
   /*
    * default speed
    */
   private int _speed = 360;
   /**
    * initial speed; set by setSpeed()  used by rotateTo
    */
   private int _speed0 = 360;
   private boolean _keepGoing = true;// for regulator
   /**
    * Initially true; changed only by regulateSpeed(),<br>
    * used by Regulator, updteState, reset*
    */
   private boolean _regulate = true;

   public Regulator regulator = new Regulator();
   // used for control of angle of rotation
   private int _direction = 1; // +1 is forward ; used by rotate();
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
    * used by stopAtLimit to save * restore state 
    */
   private boolean _wasRotating = false;
   /**  
    * set by smoothAcceleratio.  Only has effect if _regulate is true
    */
   public boolean _rampUp = true; 
   /**
    * used by timedOut to calculate actual speed;
    */
   private int _lastTacho = 0;
   /**
    * set by timedOut
    */
   private int _actualSpeed;
   private float _voltage = 0f;
   /** initialized to be false(ramping enabled); changed only by smoothAcceleration
    * used by upddateState(), rotateTo()
    */
   private boolean _noRamp = false;
   private boolean _lock = false;
   
   private int _brakePower = 20;
   private int _lockPower = 30;
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
    * Use this constructor to assign a variable of type motor connected to a particular port.
    * @param port  to which this motor is connected
    */
   public Motor (TachoMotorPort port)
   {
      _port = port;
      port.setPWMMode(TachoMotorPort.PWM_BRAKE);
      regulator.setDaemon(true);
      regulator.start();
      _voltage = Battery.getVoltage(); 
   }

   public int getStopAngle() { return (int)_stopAngle;}

   /**
    * Causes motor to rotate forward.
    */
   public void forward()
   { 
      if(_mode == BACKWARD)stop();
      updateState( FORWARD);
   }  

   /**
    * Causes motor to rotate backwards.
    */
   public void backward()
   {
      if(_mode == FORWARD)stop();
      updateState( BACKWARD);
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

   /**
    * Causes motor to float. The motor will lose all power,
    * but this is not the same as stopping. Use this
    * method if you don't want your robot to trip in
    * abrupt turns.
    */   
   public void flt()
   {
      updateState( FLOAT);
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

   void updateDirection( int mode)
   {
      if( mode == FORWARD)
         _direction = 1;
      else if( mode == BACKWARD)
         _direction = -1;
   }

   /** 
    *calls controlMotor, startRegating;  updates _direction, _rotating, _wasRotating
    *called by forwsrd(), backward(),stop(),flt();
    */
   void updateState( int mode)
   {
      synchronized(regulator)
      {
         _rotating = false; //regulator should stop testing for rotation limit  ASAP
         if( _mode == mode)
            return;
         _mode = mode;
         if(_mode == STOP || _mode == FLOAT)
         {
            _port.controlMotor(0, _mode);

            if(_regulate)
            {
               // give it some time to stop, it may depend on power used however, for now 20 ms is working
               try{Thread.sleep( 20);}
               catch(InterruptedException e){}
            }
            return;
         }
         _port.controlMotor(_power, _mode);
         updateDirection( _mode);

         if(_regulate)
         {
            regulator.reset();
            _rampUp = !_noRamp;
         }
      }
   }

   /**
    * @return true iff the motor is currently in motion.
    */
   public boolean isMoving()
   {
      return (_mode == FORWARD || _mode == BACKWARD || _rotating);
   }


   /**
    * causes motor to rotate through angle. <br>
    * @param  angle through which the motor will rotate
    */
   public void rotate(int angle)
   {
      rotateTo(getTachoCount()+angle);
   }

   /**
    * causes motor to rotate through angle; <br>
    * iff immediateReturn is true, method returns immediately and the motor stops by itself <br>
    * If any motor method is called before the limit is reached, the rotation is canceled. 
    * When the angle is reached, the method isRotating() returns false;<br>
    * 
    * @param  angle through which the motor will rotate
    * @param immediateReturn iff true, method returns immediately, thus allowing monitoring of sensors in the calling thread. 
    */
   public void rotate(int angle, boolean immediateReturn)
   {
      int t = getTachoCount();
      rotateTo(t+angle,immediateReturn);
   }

   /**
    * causes motor to rotate to limitAngle;  <br>
    * Then getTachoCount should be within +- 2 degrees of the limit angle when the method returns
    * @param  limitAngle to which the motor will rotate
    */
   public void rotateTo(int limitAngle)
   {
      rotateTo(limitAngle,false);
   }

   /**
    * causes motor to rotate to limitAngle; <br>
    * if immediateReturn is true, method returns immediately and the motor stops by itself <br> 
    * and getTachoCount should be within +- 2 degrees if the limit angle
    * If any motor method is called before the limit is reached, the rotation is canceled. 
    * When the angle is reached, the method isRotating() returns false;<br>
    * @param  limitAngle to which the motor will rotate, and then stop. 
    * @param immediateReturn iff true, method returns immediately, thus allowing monitoring of sensors in the calling thread. 
    */
   public void rotateTo(int limitAngle,boolean immediateReturn)
   {
      synchronized(regulator)
      {
         _lock = false;
         if (_wasRotating)// just in case this method is called while stopAtLimit is in progress
         {
            setSpeed(_speed0);//restore speed setting
         }
         _stopAngle = limitAngle;
         if(limitAngle > getTachoCount()) _mode = FORWARD;
         else _mode = BACKWARD;
         _port.controlMotor(_power, _mode);
         updateDirection(_mode);
         if(_regulate) regulator.reset();
         int os = overshoot(limitAngle - getTachoCount()); 
         _stopAngle -= _direction * os;//overshoot(limitAngle - getTachoCount());  
         _limitAngle = limitAngle;
         _rotating = true; // rotating to a limit
         _rampUp = !_noRamp;// && Math.abs(_stopAngle-getTachoCount())>40 && _speed>200;  //no ramp for small angles
         if(immediateReturn)return;
      }
      while(_rotating) Thread.yield();
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
      float basePower = 0;
      /**
       * time regulating started
       */
      int time0 = 0;
      float error = 0;
      float e0 = 0;
      /**
       * helper method - used by reset and setSpeed()
       */
      int calcPower(int speed)
      {   
         float pwr = 100 -11*_voltage + 0.11f*_speed;
         if(pwr<0) return 0;
         if(pwr>100)return 100;
         else return (int)pwr;
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
         setPower((int)basePower);
      }

      /**
       * Monitors time and tachoCount to regulate speed and stop motor rotation at limit angle
       */
      public void run()
      {
         float e0 = 0;// for differential control
         float power =  0;
         float ts = 120;//time to reach speed 
         int tick = 100+ (int)System.currentTimeMillis(); // 
         float accel = 0;
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
                        error = elapsed*elapsed*_speed*.77f/(ts*2000); //constant acceleration
                        error = error+  elapsed*_speed*.15f/1000;//constant speed
                        error = error -absA;
                     }
                     else  // adjust elapsed time for acceleration time - don't try to catch up
                     {
                        error = ((elapsed - ts/2)* _speed)/1000f - absA;
                     }
                  } //end if ramp up
                  else 	error = (elapsed*_speed/1000f)- absA;// no ramp
                  float gain = 5f;
                  float extrap = 4f;
                  power = basePower + gain*(error + extrap*(error - e0));
                  e0 = error;
                  
                  
//                  power = basePower + 15f * error;// - 5f * e0;// 10 magic number from experiment - simple proportional control
                  if(power < 0) power = 0;
                  if(power > 100) power = 100;
                  e0 = error;
                  float smooth = 0.012f;// another magic number from experiment
                  basePower = basePower + smooth*(power-basePower); 
                  setPower((int)power);
               }// end speed regulation 
            }// end synchronized block
         try {sleep(1);} catch(InterruptedException ie ) {}
//         Thread.yield();
         }	// end keep going loop
      }// end run
      /**
       * helper method for run()
       */
      void stopAtLimit()
      {    
         _mode = STOP; // stop motor
         _port.controlMotor (0, STOP);
         int a = angleAtStop();//returns when motor speed < 100 deg/sec
         int k = 0;
         int error = 0;
         while ( k < 4)
         {
            error = _limitAngle - getTachoCount();
            if(error < -1)
            {
               _mode = BACKWARD;
               _port.controlMotor(_brakePower,_mode);  
               k = 0;
            }
            else if (error > 1 )
            {
               _mode = FORWARD ;
               _port.controlMotor(_brakePower,_mode); 
               k = 0;
            }
            else 
            {
               _mode = STOP;
               _port.controlMotor (0, STOP);
               k++;
            } 
            try { Thread.sleep(1);} catch(InterruptedException ie) {};
         }
         _rotating = false;

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
            _port.controlMotor(0,STOP); // looks redundant, but controlMotor(0,3) fails, rarely.
            try{Thread.sleep(10);}//
            catch(InterruptedException w){}
            a = getTachoCount();
            turning = a != a0; ;
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
    * turns speed regulation on/off; <br>
    * Cumulative speed error is within about 1 degree after initial acceleration.
    * @param  yes is true for speed regulation on
    */
   public void regulateSpeed(boolean yes) 
   {
      _regulate = yes;
//    _wasRegulating = yes;
   }

   /**
    * enables smoother acceleration.  Motor speed increases gently,  and does not <>
    * overshoot when regulate Speed is used. 
    * 
    */
   public void smoothAcceleration(boolean yes) 
   {
      _noRamp = ! yes;
   }

   /**
    * Sets motor speed , in degrees per second; Up to 900 is posssible with 8 volts.
    * @param speed value in degrees/sec  
    */
   public void setSpeed (int speed)
   {
      _rampUp = !_noRamp;
      if(speed < _speed - 100) _rampUp = false;
      _speed = speed;
      if(speed<0)_speed = - speed;
      setPower((int)regulator.calcPower(_speed));
      _speed0 = _speed;
      regulator.reset();
   }

   /**
    *sets motor power.  This method is used by the Regulator thread to control motor speed.
    *Warning:  negative power will cause the motor to run in reverse but without updating the _direction 
    *field which is used by the Regulator thread.  If the speed regulation is enabled, the rusults are 
    *unpredictable. 
    */
   public synchronized void setPower(int power)
   {
      _power = power;
      _port.controlMotor (_power, _mode);
   }

   /**
    * Returns the current motor speed in degrees per second
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
      int spd = _speed;
      float ratio =0.068f;
      if (angle < 0 ) angle = -angle;
      if(angle < spd / 15) spd = 15 * angle;
      if(!_regulate)ratio = -0.173f + 0.029f * _voltage;
      return (int)(ratio * spd);
   }

   /**
    * Return the angle that a Motor is rotating to.
    * 
    * @return angle in degrees
    */
   public int getLimitAngle()
   {
      return _limitAngle;
   }

   /**
    *returns true when motor rotation task is not yet complete a specified angle
    */ 
   public boolean isRotating()
   {
      return _rotating;
   }

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
    *returns actualSpeed degrees per second,  calculated every 100 ms; negative value means motor is rotating backward
    */
   public int getActualSpeed()
   {
      return _actualSpeed;
   }

   /**
    * Returns the tachometer count.
    * 
    * @return tachometer count in degrees
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
      _port.resetTachoCount();
   }
   public void setBrakePower(int power)
   {
      if (power>100)power = 100;
      if (power < 0 )power = 0;
      _brakePower = power;
   }
   /**
    * for degugging
    * @return regulator error
    */
   public float getError()
   {
      return regulator.error;
   }

   /**
    * for debugging
    * @return base power of regulator
    */
   public float getBasePower()
   {
      return regulator.basePower;
   }
}







