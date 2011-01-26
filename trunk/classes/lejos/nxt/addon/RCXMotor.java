package lejos.nxt.addon;

import lejos.nxt.BasicMotor;
import lejos.nxt.BasicMotorPort;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Abstraction for an RCX motor.
 * 
 */
public class RCXMotor extends BasicMotor {
    BasicMotorPort port;
    int power;
    
	public RCXMotor(BasicMotorPort port)
	{
		this.port = port;
	}
    
    public void setPower(int power)
    {
        this.power = power;
        port.controlMotor(power, mode);
    }
    
    public int getPower()
    {
        return power;
    }
    
    /**
     * Update the internal stack tracking the motor direction
     * @param newMode
     */
    protected void updateState( int newMode)
    {
        if (newMode == mode) return;
        mode = newMode;
        port.controlMotor(power, newMode);
    }
}
