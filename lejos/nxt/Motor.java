package lejos.nxt;
import lejos.nxt.Battery;
import lejos.util.*;




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
 * @author Roger Glassey revised 26 March 2007
 */
public class Motor extends BasicMotor implements TimerListener
{
  private TachoMotorPort _port;
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
  private Timer timer = new Timer(100,this);
  // used for control of angle of rotation
  private int _direction = 1; // +1 is forward ; used by rotate();
  private int _limitAngle;
  private int _stopAngle;
  private boolean _rotating = false;
  private boolean _wasRotating = false;
  private boolean _rampUp = true;
  private int _lastTacho = 0;
  private int _actualSpeed;
  private float _voltage;


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
   
  public Motor (TachoMotorPort port)
  {
    _port = port;
    _voltage = Battery.getVoltage();
    regulator.setDaemon(true);
    regulator.start();
    timer.start();
  }
   public int getStopAngle() { return (int)_stopAngle;}
   
 	/**
	 * Causes motor to rotate forward.
	 */
	public void forward()
	{ 
		synchronized(regulator) 
		{
			_mode = 1;
			updateState();
		}
	}  
		
	/**
 	* Causes motor to rotate backwards.
	 */
	public void backward()
	{
		synchronized(regulator)
		{
			_mode = 2;
			updateState();
		}
	}

	/**
	 * Reverses direction of the motor. It only has
	 * effect if the motor is moving.
	 */
	public void reverseDirection()
	{
		synchronized(regulator)
		{
			if (_mode == 1 || _mode == 2)
		    {
				_mode = (3 - _mode);
				updateState();
		    }
		}
	}

   	/**
	 * Causes motor to float. The motor will lose all power,
	 * but this is not the same as stopping. Use this
	 * method if you don't want your robot to trip in
	 * abrupt turns.
	 */   
	public void flt()
	{
		synchronized(regulator)
		{
			_mode = 4;
			updateState();
		}
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
		synchronized(regulator)
		{
			_mode = 3;
		    updateState();
		}
	}
    

  /** 
   *calls controlMotor, startRegating;  updates _direction, _rotating, _wasRotating
   */
  void updateState()
  {
  	  _rotating = false; //regulator should stop testing for rotation limit  ASAP
  	synchronized(regulator)
  	{
  		if(_wasRotating)
  		{
  			setSpeed(_speed0);
			_regulate = _wasRegulating;
  		}
		_wasRotating = false; // perhaps redundant

	  	if(_mode>2) // stop or float
	  	{
	  		_port.controlMotor(0, _mode);
	  		return;
	  	}
		 _port.controlMotor(_power, _mode);
	
	   	if(_regulate)
	   	{
	   	  regulator.reset();
	   	  _rampUp = true;
	   	}
	   	 _direction = 3 - 2*_mode;
  	}
  }

  /**
   * @return true iff the motor is currently in motion.
   */
  public boolean isMoving()
  {
    return (_mode == 1 || _mode == 2 || _rotating);	  
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
   * Then getTachoCount should be within +- 2 degrees if the limit angle
   * When the angle is reached, the method isRotating() returns false;
   * @param  limitAngle to which the motor will rotate, and then stop. 
   * @param immediateReturn iff true, method returns immediately, thus allowing monitoring of sensors in the calling thread. 
    */
  public void rotateTo(int limitAngle,boolean immediateReturn)
  {
  	synchronized(regulator)
  	{
  		if(_wasRotating)
  		{
  			setSpeed(_speed0);
			_regulate = _wasRegulating;
			_wasRotating = false;
  		}
		_stopAngle = limitAngle;
		if(limitAngle > getTachoCount()) _mode = 1;
		else _mode = 2;
	    _port.controlMotor(_power, _mode);
	    _direction = 3 - 2*_mode;
	   	if(_regulate) regulator.reset();
		if(!_wasRotating)
		{
		  _stopAngle -= _direction * overshoot();
		  _limitAngle = limitAngle;
		}
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
//		float pwr = 100 - 7.4f*Battery.getVoltage()+0.065f*speed;// no-load motor
      float pwr = 100 - 7.4f*_voltage+0.065f*speed;
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
//  		int limit = 0;

	  	float e0 = 0;
	  	float accel =5f;// deg/sec/ms  was 1.5
        float power =  0;
	  	int ts = 0;//time to reach speed
	  	while(_keepGoing)
	  	{ synchronized(this)
	  	{	
	  		if(_regulate && isMoving()) //regulate speed 
	  		{
	  			int elapsed = (int)System.currentTimeMillis()-time0;
	  			int angle = getTachoCount()-angle0;
	  			while( angle < 2 && angle> -2 )
	  			{
	  			   setPower(100);
	  			   Thread.yield();
	  			 angle = getTachoCount()-angle0;
	  			}
	  			if(_rampUp)
	  			{   

	  				ts = (int)(_speed/accel);
//	  				ts = 100;
                    if(elapsed<ts)// not at speed yet
		  			{
 
		  				// target distance = a * t * t/ 2 - maintain constant acceleration
		 				error = accel*elapsed * elapsed/2000 - (float)Math.abs(angle);
		  			}
		  			else  // adjust elapsed time for acceleration time - don't try to catch up
		  			{
                     error = ((elapsed - ts/2)* _speed)/1000f - (float)Math.abs(angle);
		  			}
	  			}
		  		else 	
					error = (elapsed*_speed/1000f)- (float)Math.abs(angle);
	  			power = basePower + 2f * error;// +5f*e0;// -0.1f * e0;// magic numbers from experiment .75
	  			if(power<0) power = 0;
	  			e0 = error;
	  			float smooth = 0.001f;// another magic number from experiment.0025
	  			basePower = basePower + smooth*(power-basePower); 
	  			setPower((int)power);
	  		}
	  // stop at rotation limit angle
			if(_rotating && _direction*(getTachoCount() - _stopAngle)>0)
			{
				//if(!_wasRotating)_speed0 = _speed;
				_mode = 3; // stop motor
				_port.controlMotor (0, 3);
				int a = angleAtStop();//returns when motor has stopped
				int remaining = _limitAngle - a;
				if(_direction * remaining >3 ) // not yet done; don't call nudge for less than 3 deg
				{
					if(!_wasRotating)// initial call to rotate(); save state variables
					{
						_wasRegulating = _regulate;
						_regulate = true;
						_speed0 = _speed;
						setSpeed(300);//was 150
						_wasRotating = true;
//						limit = _limitAngle;
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
                    _mode = 3; // stop motor  maybe redundant
					_port.controlMotor (0, _mode);
					_rotating = false;

				}
	  		}
	  	}
	  	Thread.yield();
	  	}	
  	}
  /**
   *helper method for run  - stop at limit angle branch
   */ 
  	private void nudge(int remaining,int tachoCount)
  	{
 
  		if(remaining>0)_mode = 1;
  		else _mode = 2;	
	    _port.controlMotor(_power, _mode);
	    _direction = 3 - 2*_mode;
 		_stopAngle = tachoCount + remaining/3;
 		if(remaining < 3 && remaining > -3) _stopAngle += _direction; //nudge at least 1 deg
	    _rotating = true;
	    _rampUp = false;
	    _regulate = true;
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
	  		_port.controlMotor(0,3); // looks redundant, but controlMotor(0,3) fails, rarely.
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
  public void setSpeed (int speed)
  {

    _speed = Math.abs(speed);
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
  public synchronized void  setPower(int power)
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
	public int getMode() {return _mode;}
	public int getPower() { return _power;}
	
  private int overshoot()
  {
	return   (int)(5+ _speed*0.060f);//60?
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
   *returns true when motor is rotating towarad a specified angle
   */ 
  public boolean isRotating()
  {
  	return  _rotating;
  }
  public boolean isRegulating(){return _regulate;}
  /**
   * requred by TimerListener interface
   */
  public void timedOut()
  {
	int angle = getTachoCount();
	_actualSpeed = 10*(angle - _lastTacho);
	_lastTacho = angle;
    _voltage = Battery.getVoltage();
  }
	
  /** 
   *returns actualSpeed degrees per second,  calculated every 100 ms; negative value means motor is rotating backward
   */
  public int getActualSpeed() { return _actualSpeed;}	
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

  public float getError() {return regulator.error;}
  public float getBasePower() {return regulator.basePower;}
}







