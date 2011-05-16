package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Interface for Acceleration sensors
 * 
 * @author Lawrie Griffiths
 *
 */
public interface Accelerometer {	
	public int getXAccel();
	
	public int getYAccel();
	
	public int getZAccel();
	
	public int getXTilt();
	
	public int getYTilt();
	
	public int getZTilt();
	
}
