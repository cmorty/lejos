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
 * Motor has 2 modes : speedRegulation and smoothAcceleration. These are initially enabled. <>
 * They can be switched off/on by the methods regulateSpeed() and smoothAcceleration().
 * The actual maximum speed of the motor depends on battery voltage and load.. 
 * Speed regulation fails if the target speed exceeds the capability of the motor.
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
 * @author Roger Glassey revised 20 Dec 2007 - uses brake mode for better control
 */
public class Motor extends BasicMotor// implements TimerListener
{  
   private TachoMotorPort _port;
   /*
    * default speed
    */
   private int _speed = 360;
   private int _speed0 = 360;
   // used for speed regulation
   private boolean _keepGoing = true;// for regulator
   /**
    * Initially true; changed only by regulateSpeed(),<br>
    * used by Regulator, updteState, reset*
    */
   private boolean _regulate = true;
   private boolean _wasRegulating = false;
   public Regulator regulator = new Regulator();
   // used for control of angle of rotation
   private int _direction = 1; // +1 is forward ; used by rotate();
   private int _limitAngle;
   private int _stopAngle;
   private boolean _rotating = false;
   /**
    * used by stopAtLimit to save * restore state 
    */
   private boolean _wasRotating = false;

   private boolean _rampUp = true;
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
    */
   private boolean _noRamp = false;
     
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
      //while(_voltage < 1f );
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
            _rampUp = true;
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
         if (_wasRotating)// just in case this method is called while stopAtLimit is in progress
         {
            setSpeed(_speed0);//restore speed setting
            _wasRotating = false;
            _regulate = _wasRegulating;
         }
         _stopAngle = limitAngle;
         if(limitAngle > getTachoCount()) _mode = FORWARD;
         else _mode = BACKWARD;
         _port.controlMotor(_power, _mode);
         updateDirection(_mode);
         if(_regulate) regulator.reset();
            _stopAngle -= _direction * overshoot();
            _limitAngle = limitAngle;
         _rotating = true; // rotating to a limit
         _rampUp = !_noRamp && Math.abs(_stopAngle-getTachoCount())>40 && _speed>200;  //no ramp for small angles
         if(immediateReturn)return;
      }
      while(_rotating) Thread.yield();
   }

   /**
    *inner class to regulate speed; also stop motor at desired rotation angle
    **/
   private class Regulator extends Thread
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

      /**
       * helper method - used by reset and setSpeed()
       */
      int calcPower(int speed)
      {   
         float pwr = 100 -12*_voltage + 0.12f*_speed;
         if(pwr<0) return 0;
         if(pwr>100)return 92;
         else return (int)(.8f*pwr);
      }
 
      /**
       * called by forward() backward() and reverseDirection() <br>
       * resets parameters for speed regulation
       **/
      public void reset()
      {
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
//         float e0=0;
//         float accel = 0;// =8.f;// accel = _speed/(1000*ts);
         float power =  0;
         float ts = 300;//time to reach speed  /was 200
         int tock = 100+ (int)System.currentTimeMillis(); // 
         int tick = (int)System.currentTimeMillis();  // loop once per ms
         while(_keepGoing)
         { synchronized(this)
            { 
            if((int)System.currentTimeMillis()> tick)
            {
               tick = (int)System.currentTimeMillis();    
               if(tick >= tock)// simulate timer
               {
                  tock+=100;
                  timedOut();
               }
               if(_rotating && _direction*(getTachoCount() - _stopAngle)>=0)  stopAtLimit();  // was >0
               else if(_regulate && isMoving()) //regulate speed 
               {
                  int elapsed = (int)System.currentTimeMillis()-time0;
                  int angle = getTachoCount()-angle0;
                  int absA = angle;
                  if(angle<0)absA = -angle;
                  if(_rampUp)
                  {   
                     if(elapsed<ts)// not at speed yet
                     {
                        error = elapsed*elapsed/ts;  //assume acceleration decreases linearly
                        error = error * (1 - elapsed/(3.0f*ts))*(_speed/1000f);
                        error = error -absA;
                     }
                     else  // adjust elapsed time for acceleration time - don't try to catch up
                     {
                        error = ((elapsed - ts/3)* _speed)/1000f - absA;
                     }
                  }
                  else 	// no ramp
                     error = (elapsed*_speed/1000f)- absA;
                  power = basePower + 10f * error;// magic number from experiment - simple proportional control
                  if(power<0) power = 0;
//                  e0 = error;
                  float smooth = 0.008f;// another magic number from experiment.0025
                  basePower = basePower + smooth*(power-basePower); 
                  setPower((int)power);
               }// end speed regulation 
            }// end if tick
            }// end synchronized block
         Thread.yield();
         }	// end keep going loop
      }// end run
      /**
       * helper method for run()
       */
      void stopAtLimit()
      {
         _mode = STOP; // stop motor
         _port.controlMotor (0, STOP);
         int a = angleAtStop();//returns when motor has stopped
         int remaining = _limitAngle - a;
         if(_direction * remaining >2 ) // not yet done; don't call nudge for less than 3 deg
         {                                                            
            if(!_wasRotating)// initial call to stopAtLimit; save state variables
            {
               _wasRegulating = _regulate;
               _regulate = true;
               _speed0 = _speed;
               _wasRotating = true;
            }
            nudge(remaining,a); //another try
         }
         else //rotation complete;  restore state variables
         { 
            if (_wasRotating)
            {
               setSpeed(_speed0);//restore speed setting
               _wasRotating = false;
               _regulate = _wasRegulating;
            }
            _rotating = false;
         }  
      }
      /**
       *helper method for stopAtLimit() 
       **/
      private void nudge(int remaining,int tachoCount)
      {
         setSpeed(100);
         if(remaining > 0)_mode = FORWARD;
         else _mode = BACKWARD;
         _port.controlMotor(_power, _mode);
         updateDirection(_mode);
         _stopAngle = tachoCount + remaining/2;
         if(remaining < 2 && remaining > -2) _stopAngle += _direction; //nudge at least 1 deg
         _rotating = true;
         _rampUp = false;
         _regulate = true;
      }  
      /**
       *helper method for stopAtLimit
       **/
      int angleAtStop()
      {
         int a0 = getTachoCount();
         boolean turning = true;
         int a = 0;
         while(turning)
         {
            _port.controlMotor(0,STOP); // looks redundant, but controlMotor(0,3) fails, rarely.
            try{Thread.sleep(20);}// was 10
            catch(InterruptedException w){}
            a = getTachoCount();
            turning = Math.abs(a - a0)>0;
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
      _speed = speed;
      if(speed<0)_speed = - speed;
      setPower((int)regulator.calcPower(_speed));
      regulator.reset();
      _rampUp = false;
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
    * @return
    */
   private int overshoot()
   {
      return (int)(3+ _speed*0.072f);//60?
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







