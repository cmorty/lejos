package lejos.nxt;

import lejos.robotics.Encoder;
/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Abstraction for an NXT motor with no speed regulation.
 * 
 */
public class NXTMotor extends BasicMotor implements Encoder{
    TachoMotorPort port;
    int power;
    
	public NXTMotor(TachoMotorPort port)
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
     * Update the internal state tracking the motor direction
     * @param newMode
     */
    protected void updateState( int newMode)
    {
        if (newMode == mode) return;
        mode = newMode;
        port.controlMotor(power, newMode);
    }

    public int getTachoCount()
    {
        return port.getTachoCount();
    }

    public void resetTachoCount()
    {
        port.resetTachoCount();
    }
}
