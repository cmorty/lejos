package lejos.robotics;

// TODO: This is wrong to have a lejos.robotics class using a lejos.nxt class.

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
	 * Turns the default LED light on or off. If the sensor has more than one lamp color, this will
	 * control the red LED.
	 * 
	 * @param floodlight true to turn on lamp, false for off (ambient light only).
	 */
	public void setFloodlight(boolean floodlight);
	
	public boolean isFloodlightOn();
	
	public Colors.Color getFloodlight();
	
	/**
	 * 
	 * @param color Use lejos.nxt.Colors constants to control lamp colors.
	 * @return True if lamp changed, false if lamp doesn't exist for this sensor. 
	 */
	 
	public boolean setFloodlight(Colors.Color color);
	
}
