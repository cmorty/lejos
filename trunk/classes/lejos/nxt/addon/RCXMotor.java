package lejos.nxt.addon;

import lejos.nxt.BasicMotor;
import lejos.nxt.BasicMotorPort;


/**
 * 
 * Abstraction for an RCX motor.
 *
 */
public class RCXMotor extends BasicMotor {
	public RCXMotor(BasicMotorPort port)
	{
		_port = port;
	}
}
