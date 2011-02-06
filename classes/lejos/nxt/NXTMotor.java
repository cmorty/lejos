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
    protected Encoder encoderPort;

	public NXTMotor(TachoMotorPort port)
	{
		this.port = port;
        // We use extra var to avoid cost of a cast check later
        encoderPort = port;
	}
    
    public int getTachoCount()
    {
        return encoderPort.getTachoCount();
    }

    public void resetTachoCount()
    {
        encoderPort.resetTachoCount();
    }
}
