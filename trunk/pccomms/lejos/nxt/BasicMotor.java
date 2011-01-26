package lejos.nxt;

import lejos.robotics.DCMotor;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/** 
 * Abstraction for basic motor operations.
 * 
 * @author Lawrie Griffiths.
 *
 */
public abstract class BasicMotor implements DCMotor
{
	public final static int FORWARD = 1;
	public final static int BACKWARD = 2;
	public final static int STOP = 3;
	public final static int FLOAT = 4;
    public final static int MAX_POWER = 100;

	protected int mode = FLOAT;
	abstract protected void updateState( int newMode);

	/**
	 * Causes motor to rotate forward.
	 */
	public void forward()
	{ 
		updateState( FORWARD);
	}
	  
	/**
	 * Return true if motor is forward.
     *
     * @return true if the motor is running forwards
     */
	public boolean isForward()
	{
		return (mode == FORWARD);
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
     *
     * @return true if the motor is running backwards
     */
	public boolean isBackward()
	{
		return (mode == BACKWARD);
	}

	/**
	 * Reverses direction of the motor. It only has
	 * effect if the motor is moving.
	 */
	public void reverseDirection()
	{
		if (mode == FORWARD)
			backward();
        else if (mode == BACKWARD)
			forward();
	}

	/**
	 * Returns true iff the motor is in motion.
	 * 
	 * @return true iff the motor is currently in motion.
	 */
	public boolean isMoving()
	{
		return (mode == FORWARD || mode == BACKWARD);   
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
		return mode == FLOAT;	  
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
     *
     * @return true if the motor is stopped
     */
	public boolean isStopped()
	{
		return (mode == STOP);
	}

	/**
	 * Returns the mode.
	 * 
	 * @return mode 1=forward, 2=backward, 3=stopped, 4=floating
	 */
	public int getMode()
	{ 
		return mode;
	}
}

