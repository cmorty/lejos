package lejos.nxt;

import lejos.nxt.Battery;




/**
 * Abstraction for a motor. Three instances of <code>Motor</code>
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
 *   Motor.A.setSpeed(720);// 2 RPS
 *   Motor.C.setSpeed(7200);
 *   Motor.A.forward();
 *   Motor.C.forward();
 *   Thread.sleep (1000);
 *   Motor.A.stop();
 *   Motor.C.stop();
 *   Motor.A.regulateSpeed(true);
 *   Motor.A.rotateTo( 360);
 *   while(Motor.A.isRotating());
 *   Motor.A.rotate(-720);
 *   while(Motor.A.isRotating();
 *   int angle = Motor.A.getTachoCount(); // should be -360
 * </pre></code>
 *  Roger Glassey 3 Jan 2006
 */
public class Motor
{
  private char  _id;
  private int _mode = 4;
  private int _speed = 360;
  private int _power = 0;
  // used for speed regulation
  private boolean _keepGoing = true;// for regulator
  /**
   * Initially true; changed only by regulateSpeed(),<br>
   * used by Regulator, updteState, startRegulating*
   */
  private boolean _regulate = true;
  
  public Regulator regulator = new Regulator();
  // used for control of angle of rotation
  private int _direction = 1; // +1 is forward ; used by rotate();
  private int _limitAngle;
  private int _stopAngle;
  private boolean _rotating = false;
  private boolean _wasRotating = false;
  private boolean _rampUp = true;
  /**
   * initialized to be false(ramping enabled); changed only by smoothAcceleration
   */
  private boolean _noRamp = false;
 
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
    _id = aId;
    regulator.start();
    regulator.setDaemon(true); 
  }

/**
   * Get the ID of the motor. One of 'A', 'B' or 'C'.
   */
  public final char getId()
  {
    return _id;
  }

  /**
   * Causes motor to rotate forward.
   */
  public final void forward()
  { 
    _mode = 1;
    updateState();
  }
  
  /**
   * Return true if motor is forward.
   */
  public final boolean isForward()
  {
    return (_mode == 1);
  }

  /**
   * Causes motor to rotate backwards.
   */
  public final void backward()
  {
    _mode = 2;
    updateState();
  }

  /**
   * Return true if motor is backward.
   */
  public final boolean isBackward()
  {
    return (_mode == 2);
  }

  /**
   * Reverses direction of the motor. It only has
   * effect if the motor is moving.
   */
   
  public final void reverseDirection()
  {
    if (_mode == 1 || _mode == 2)
    {
      _mode = (3 - _mode);
		updateState();
    }
  }
/** 
 *calls controlMotor, startRegating;  updates _direction, _rotating
 * precondition:  mode == 1 or 2
 */
	private void updateState()
	{
	    controlMotor (_id - 'A', _mode, _power);
	   _rotating = false;
   	   if(_regulate)
   	   {
   	   	regulator.startRegulating();
   	   	_rampUp = true;
   	   }
   	   	 _direction = 3 - 2*_mode;
	}

   /**
   * @return true iff the motor is currently in motion.
   */
  public final boolean isMoving()
  {
    return (_mode == 1 || _mode == 2 || _rotating);	  
  }

    /**
   * Causes motor to float. The motor will lose all power,
   * but this is not the same as stopping. Use this
   * method if you don't want your robot to trip in
   * abrupt turns.
   */
   
  public final void flt()
  {
    _mode = 4;
    controlMotor (_id - 'A', 4, 0);
  }

  /**
   * @return true iff the motor is currently in float mode.
   */
  public final boolean isFloating()
  {
    return _mode == 4;	  
  }
  
  /**
   * Causes motor to stop, pretty much
   * instantaneously. In other words, the
   * motor doesn't just stop; it will resist
   * any further motion.
   * Cancels any rotate() orders in progress
   */
  public final void stop()
  {
    _mode = 3;
    controlMotor (_id - 'A', 3, 0);
    _rotating = false;
    _rampUp = false;
  }
  
  /**
   * Return true if motor is stopped.
   */
  public final boolean isStopped()
  {
    return (_mode == 3);
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
 * When the angle is reached, the method isRotating() returns false;

 * @param  angle through which the motor will rotate
 * *@param immediateReturn; iff true, method returns immediately, thus allowing monitoring of sensors in the calling thread. 

 */

	public void rotate(int angle, boolean immediateReturn)
	{
		rotateTo(getTachoCount()+angle,immediateReturn);
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
 * Then getTachoCount should be within +- 2 degrees if the limit angle
 * When the angle is reached, the method isRotating() returns false;
 * @param  limitAngle to which the motor will rotate, and then stop. 
 *@param immediateReturn; iff true, method returns immediately, thus allowing monitoring of sensors in the calling thread. 
 */
  public void rotateTo(int limitAngle,boolean immediateReturn)
	{
		_stopAngle = limitAngle;
		if(limitAngle > getTachoCount())
		{
			_direction = 1;
			forward();
		}
		else
		{
			 _direction = -1;
			 backward();
		}
		if(!_wasRotating)
		{
			 _stopAngle -= _direction * overshoot();
			 _limitAngle = limitAngle;
		}
		_rotating = true; // rotating to a limit
		_rampUp = !_noRamp && Math.abs(_stopAngle-getTachoCount())>40 && _speed>200;  //no ramp for small angles
		if(immediateReturn)return;
		while(isMoving()) Thread.yield();
		
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
  * set by startRegulating, used  to regulate  motor speed
  */ 
  	float basePower = 0;
  /**
   * time regulating started
   */
  	int time0 = 0;

/* *
* helper method - used by startRegulating and setSpeed()
*/
 	int calcPower(int speed)
 	{   
		float pwr = 100 - 7.4f*Battery.getVoltage()+0.065f*speed;// no-load motor
 		if(pwr<0) return 0;
 		if(pwr>100)return 100;
 		else return (int)pwr;
 	}
 
 /**
  * called by forward() backward() and reverseDirection()
  **/
 	public void startRegulating()
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
	  	int speed0 = 0;
  		int limit = 0;
  		float error = 0;
	  	float e0 = 0;
	  	int speed = 0;
	  	float accel =1.25f;// deg/sec/ms 
	  	int td = 100;
	  	float ts = 0;  //time to stabilize
	  	while(_keepGoing)
	  	{	
	  		if(_regulate && isMoving()) //regulate speed 
	  		{
	  			int elapsed = (int)System.currentTimeMillis()-time0;
	  			int angle = getTachoCount()-angle0;
	  			if(_rampUp)
	  			{   
	  				ts = _speed/accel;
					if (elapsed +td<ts)// not yet up to speed
		  			{
		  				elapsed +=td;
		  				// target distance = a * t * t/ 2 - maintain constant acceleration
		 				error = accel*elapsed * elapsed/2000 - (float)Math.abs(angle);
		 				basePower = calcPower((int)Math.max(elapsed*accel,400));
		  			}
		  			else  // adjust elapsed time for acceleration time - don't try to catch up
		  			{
		  				error = ((elapsed + td-ts/2)* _speed)/1000f - (float)Math.abs(angle);
		  			}
	  			}
		  		else 	
					error = (elapsed*_speed/1000f)- (float)Math.abs(angle);
	  			float power = basePower + 2 * error -1 * e0;// magic numbers from experiment
	  			e0 = error;
	  			float smooth = 0.0015f;// another magic number from experiment
	  			basePower = basePower + smooth*(power-basePower); 
	  			setPower((int)power);
	  		}
	  // stop at rotation limit angle
			if(_rotating && _direction*(getTachoCount() - _stopAngle)>-1)
			{
				_mode = 3; // stop motor
				controlMotor (_id - 'A', 3, 0);
				int a = angleAtStop();//returns when motor has stopped
				int remaining = _limitAngle - a;
				if(_direction * remaining >0 ) // not yet done
				{
					if(!_wasRotating)// initial call to rotate(); save state variables
					{
						speed0 = _speed;
						_wasRotating = true;
						limit = _limitAngle;
					}
 					setSpeed(100);
				 	rotateTo(limit - remaining/3,true); //another try
				}
				else //rotation complete;  reset state variables
				{
					_rotating = false;
					_wasRotating = false;
					setSpeed(speed0);
				}
	  		}
	  	Thread.yield();
	  	}	
  	}
  
/**
*helper method for run
**/
  	 int angleAtStop()
  	{
		int a0 = getTachoCount();
		boolean turning = true;
		int a = 0;
		while(turning)
		{
			try{Thread.sleep(10);}// was 10
			catch(InterruptedException w){}
			a = getTachoCount();
			turning = Math.abs(a - a0)>0;
			a0 = a;
		}
		return	a;
	}
  }
/**
 * cant use stop() in a thread
 */
 private void halt(){stop();}
 
 /**
  *causes run() to exit
  */
 public void shutdown(){_keepGoing = false;}
  
 
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
 		{_noRamp = ! yes;}
 
  /**
   * Sets motor speed , in degrees per second; Up to 900 is posssible with 8 volts.
   * @param speed value in degrees/sec  
   */
  public final void setSpeed (int speed)
  {
    _speed = Math.abs(speed);
     setPower((int)regulator.calcPower(_speed));
  }

/**
 *sets motor power.  This method is used by the Regulator thread to control motor speed.
 *Warning:  negative power will cause the motor to run in reverse but without updating the _direction 
 *field which is used by the Regulator thread.  If the speed regulation is enabled, the rusults are 
 *unpredictable. 
 */
	private void setPower(int power)
	{
	 _power = power;
	  controlMotor (_id - 'A', _mode,power);
	}
/**
 * Returns current motor power.
 */
	public final int getPower(){return _power;}


  /**
   * Returns the current motor speed in degrees per second
   */
  public final int getSpeed()
  {
    return _speed;	  
  }

  
	private int overshoot()
	{
		return  10 + (int)( _speed*0.067f);//0.067 from regression - extra margin for high speed
	}

	public int getLimitAngle()
	{
		return _limitAngle;
	}


/**
 *returns true when motor is rotating towarad a specified angle
 */ 
  public final boolean isRotating()
  {
  	return  _rotating;
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
   * @param aPower A value in the range [0-100].
   */
  public static native void controlMotor (int aMotor, int aMode, int aPower);
/**
 * returns tachometer count
 */
  public int getTachoCount()
  {
	  return getTachoCountById(_id - 'A');
  }


  public static native int getTachoCountById(int aMotor);

 /**
  *resets the tachometer count to 0;
  */ 
  public void resetTachoCount()
  {
	  resetTachoCountById( _id - 'A');
  }
  
  public static native void resetTachoCountById(int aMotor);
}