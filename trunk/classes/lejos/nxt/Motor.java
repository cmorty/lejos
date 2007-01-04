import lejos.nxt.Battery;



/**
 * Abstraction for a motor. Three instances of <code>Motor</code>
 * are available: <code>Motor.A</code>, <code>Motor.B</code>
 * and <code>Motor.C</code>. To control each motor use
 * methods <code>forward, backward, reverseDirection, stop</code>
 * and <code>flt</code>. To set each motor's speed, use
 * <code>setSpeed.  Speed is in degrees per second. </code>.\
 * Methods that use the tachometer:  regulateSpeed, rotate, rotateTo  
 * <p>
 * Example:<p>
 * <code><pre>
 *   Motor.A.setSpeed(720);// 2 RPM
 *   Motor.C.setSpeed(800);
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
  private int _speed = 0;
  private int _power = 0;
  // used for speed regulation
  private boolean keepGoing = true;// for regulator
  private boolean _regulate = true;
  
  public Regulator regulator = new Regulator();
  // used for control of angle of rotation
  private byte _direction = 1; // +1 is forward ; used by rotate();
  private int _limitAngle;
  private int _stopAngle;
  private boolean _limit = false;
  private boolean _rotating = false;



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

 private class Regulator extends Thread
  {
	
  	float basePower = 0;
  	int time0 = 0;// time regulation started
  	int angle0 = 0;//tacho reading when regulation started
  	float power = 0;

  	/* used by startRegulating and setSpeed()*/
 	int calcPower(int speed)
 	{   
		float pwr = 100 - 7.4f*Battery.getVoltage()+0.065f*speed;// no-load motor
 		if(pwr<0) return 0;
 		if(pwr>100)return 100;
 		else return (int)pwr;
 	}
 /*
  * called by forward() backward() and reverseDirection()
  **/
 	public void startRegulating()
	{
 		_regulate = true;
 		time0 = (int)System.currentTimeMillis();
 		angle0 = getTachoCount();
 	    basePower = calcPower(_speed);
    	setPower((int)basePower);
 	}
  	public void run()
  	{
	  	while(keepGoing)
	  	{	  	
	  		if(_regulate && isMoving())
	  		{
	  			float e0 = 0;
	  			float error = 0;
	  			int elapsed = (int)System.currentTimeMillis()-time0;
	  			int angle = getTachoCount()-angle0;
	  			error = (elapsed * _speed)/1000f - (float)Math.abs(angle);
	  			float power = basePower + 1.5f * error - 0.6f * e0;// magic numbers from experiment
	  			e0 = error;
	  			float smooth = 0.0015f;// another magic number from experiment
	  			basePower = basePower + smooth*(power-basePower); 
	  			setPower((int)power);
	  		}
			if(_limit && _direction*(getTachoCount() - _stopAngle)>=0)
			{
				halt();

				int spd = _speed;
				boolean more = true;
				setSpeed(400);
				while(more)
				{ 	 
					 int a = angleAtStop();
					 more = _direction * (a - _limitAngle)<-1;
					 if(more)
					 {

						if(_direction ==1) forward();
						else backward();
						_stopAngle = (a + _limitAngle)/2;
						while( _direction*(getTachoCount() - _stopAngle)<0);
						halt();
					 }
				}
				_limit = false;
				setSpeed(spd);
	  		}
	  	Thread.yield();
	  	}	
  	}
  	 int angleAtStop()
  	{
		int a0 = getTachoCount();
		boolean turning = true;
		int a = 0;
		while(turning)
		{
			try{Thread.sleep(15);}
			catch(InterruptedException w){}
			a = getTachoCount();
			turning = Math.abs(a - a0)>1;
			a0 = a;
		}
		return	a;
	}
  }

  private void halt(){stop();}
 public void shutdown(){keepGoing = false;}
  /**
   * Get the ID of the motor. One of 'A', 'B' or 'C'.
   */
  public final char getId()
  {
    return _id;
  }
 
  public void rotateTo(int limitAngle)
	{
		_limitAngle = limitAngle;
		int angle0 = getTachoCount();
		int over = Math.abs(_limitAngle - angle0);
		over = overshoot();
		if(limitAngle >angle0)
		{
			_direction = 1;
			_stopAngle = limitAngle - over;
			forward();
		}
		else
		{
			 _direction = -1;
			 _stopAngle = _limitAngle + over;
			 backward();
		}
		_limit = true; // rotating to a limit

	}
	private int overshoot()
	{
		return 5+ (int)( _speed*0.065f);
	}
/**
 * causes motor to rotate through angle;  This method returns immediately <br>
 * When the angle is reached, the method isRotating() returns false;
 * @param  angle through which the motor will rotate
 */
	public void rotate(int angle)
	{
		rotateTo(getTachoCount()+angle);
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
   * Sets motor speed , in degrees per second; Up to 900 is posssible with 8 volts.
   * @param speed value in degrees/sec  
   */
  public final void setSpeed (int speed)
  {
    _speed = speed;
     setPower((int)regulator.calcPower(_speed));
  }

	private void setPower(int power)
	{
	 _power = power;
	  controlMotor (_id - 'A', _mode,power);
	}


  /**
   * Causes motor to rotate forward.
   */
  public final void forward()
  {
    _mode = 1;
    controlMotor (_id - 'A', 1, _power);
    if(_regulate)regulator.startRegulating();
    _direction = 1;
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
    controlMotor (_id - 'A', 2, _power);
    if(_regulate)regulator.startRegulating();
    _direction = -1;
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
      _direction *=-1;
      controlMotor (_id - 'A', _mode, _power);
    }
    if(_regulate)regulator.startRegulating();
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

  /**
   * @return true iff the motor is currently in motion.
   */
  public final boolean isMoving()
  {
    return (_mode == 1 || _mode == 2);	  
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
   */
  public final void stop()
  {
    _mode = 3;
    controlMotor (_id - 'A', 3, 0);

  }
  
  /**
   * Return true if motor is stopped.
   */
  public final boolean isStopped()
  {
    return (_mode == 3);
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
 *returns true when motor is rotating towarad a specified angle
 */ 
  public final boolean isRotating()
  {
  	return  _limit;
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
   * @param aSpeed A value in the range [0-100].
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







