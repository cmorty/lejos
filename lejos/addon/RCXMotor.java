package lejos.addon;

import lejos.nxt.BasicMotor;
import lejos.nxt.BasicMotorPort;


/**
 * 
 * Abstraction for an RCX motor.
 *
 */
public class RCXMotor extends BasicMotor {
	BasicMotorPort _port;
	public RCXMotor(BasicMotorPort port)
	{
		_port = port;
	}
}
