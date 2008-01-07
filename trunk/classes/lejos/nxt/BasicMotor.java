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
	
	int _mode = 4;
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
		_mode = 1;
		updateState();
	}
	  
	/**
	 * Return true if motor is forward.
	 */
	public boolean isForward()
	{
		return (_mode == 1);
	}

	/**
	 * Causes motor to rotate backwards.
	 */
	public void backward()
	{
		_mode = 2;
		updateState();
	}

	/**
	 * Return true if motor is backward.
	 */
	public boolean isBackward()
	{
		return (_mode == 2);
	}

	/**
	 * Reverses direction of the motor. It only has
	 * effect if the motor is moving.
	 */
	public void reverseDirection()
	{
		if (_mode == 1 || _mode == 2)
	    {
			_mode = (3 - _mode);
			updateState();
	    }
	}

	/**
	 * Returns true iff the motor is in motion.
	 * 
	 * @return true iff the motor is currently in motion.
	 */
	public boolean isMoving()
	{
		return (_mode == 1 || _mode == 2);	  
	}

	/**
	 * Causes motor to float. The motor will lose all power,
	 * but this is not the same as stopping. Use this
	 * method if you don't want your robot to trip in
	 * abrupt turns.
	 */   
	public void flt()
	{
		_mode = 4;
		updateState();
	}

	/**
	 * Returns true iff the motor is in float mode.
	 * 
	 * @return true iff the motor is currently in float mode.
	 */
	public boolean isFloating()
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
	public void stop()
	{
		_mode = 3;
	    updateState();
	}
	  
	/**
	 * Return true if motor is stopped.
	 */
	public boolean isStopped()
	{
		return (_mode == 3);
	}

	void updateState()
	{
		_port.controlMotor(_power, _mode);
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

