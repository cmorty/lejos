package lejos.robotics;

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
