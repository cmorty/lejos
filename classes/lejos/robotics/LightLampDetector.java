package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Interface for a light sensor that also contains an LED light to provide illumination.
 * @author BB
 *
 */
public interface LightLampDetector extends LightDetector {

	/**
	 * Turns the LED light on or off.
	 * 
	 * @param floodlight true to turn on lamp, false for off (ambient light only).
	 */
	public void setFloodlight(boolean floodlight);
	
}
