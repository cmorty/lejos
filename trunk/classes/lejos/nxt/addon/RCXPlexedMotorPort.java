package lejos.nxt.addon;

import lejos.nxt.BasicMotorPort;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Supports a motor connected to the Mindsensors RCX Motor Multiplexer
 * 
 * @author Lawrie Griffiths
 *
 */
public class RCXPlexedMotorPort implements BasicMotorPort {
	private RCXMotorMultiplexer plex;
	private int id;
	
	public RCXPlexedMotorPort(RCXMotorMultiplexer plex, int id) {
		this.plex = plex;
		this.id = id;
	}
	
	public void controlMotor(int power, int mode) {
		int mmMode = mode;
		if (mmMode == 4) mmMode = 0; // float
		int mmPower = (int) ((float)power * 2.55f);
		if (mmMode == 3) mmPower = 255; // Maximum breaking
		plex.setDirection(mmMode, id);
		plex.setSpeed(mmPower, id);
	}
	
	public void setPWMMode(int mode) {
	}
}
