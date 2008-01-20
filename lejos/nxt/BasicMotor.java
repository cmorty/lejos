package lejos.nxt;

/**
 * An abstraction for a motor without a tachometer,
 * such as an RCX motor.
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class BasicMotor 
{
	final int FORWARD = 1;
	final int BACKWARD = 2;
	final int STOP = 3;
	final int FLOAT = 4;

	int _mode = FLOAT;
	int _power = 50;
	BasicMotorPort _port;

	/**
	 * Sets power.
	 * 
	 * @param power power setting: 0 - 100
	 */
	public void setPower(int power)
	{
		_power = power;
		_port.controlMotor(_power, _mode);
	}
	 
	/**
	 * Returns the current power setting.
	 * 
	 * @return power value 0-100
	 */
	public int getPower()
	{
		return _power;
	}

	/**
	 * Causes motor to rotate forward.
	 */
	public void forward()
	{ 
		updateState( FORWARD);
	}
	  
	/**
	 * Return true if motor is forward.
	 */
	public boolean isForward()
	{
		return (_mode == FORWARD);
	}

	/**
	 * Causes motor to rotate backwards.
	 */
	public void backward()
	{
		updateState( BACKWARD);
	}

	/**
	 * Return true if motor is backward.
	 */
	public boolean isBackward()
	{
		return (_mode == BACKWARD);
	}

	/**
	 * Reverses direction of the motor. It only has
	 * effect if the motor is moving.
	 */
	public void reverseDirection()
	{
		if (_mode == FORWARD)
			updateState( BACKWARD);
		else
		if (_mode == BACKWARD)
			updateState( FORWARD);
	}

	/**
	 * Returns true iff the motor is in motion.
	 * 
	 * @return true iff the motor is currently in motion.
	 */
	public boolean isMoving()
	{
		return (_mode == FORWARD || _mode == BACKWARD);   
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
	 * Returns true iff the motor is in float mode.
	 * 
	 * @return true iff the motor is currently in float mode.
	 */
	public boolean isFloating()
	{
		return _mode == FLOAT;	  
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
	 * Return true if motor is stopped.
	 */
	public boolean isStopped()
	{
		return (_mode == STOP);
	}

	void updateState( int mode)
	{
		if( _mode != mode)
		{
			_mode = mode;
			_port.controlMotor(_power, _mode);
		}
	}
	  
	/**
	 * Returns the mode.
	 * 
	 * @return mode 1=forward, 2=backward, 3=stopped, 4=floating
	 */
	public int getMode()
	{ 
		return _mode;
	}
}

