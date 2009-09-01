package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * A platform independent implementation for sensors that can detect white light levels.
 * @author BB
 *
 */
public interface LightDetector {
	
	/**
	 * Returns the calibrated and normalized brightness of the white light detected.
	 * @return A brightness value between 0 and 100, with 0 = darkness and 100 = sunlight
	 */
	public int getLightLevel();
	
	/**
	 * Returns the raw value of the brightness of the white light detected.
	 * @return A raw value, usually between 0 and getMaxLightLevel(). Usually 
	 * 1023 but can be anything depending on hardware.
	 */
	public int getRawLightLevel();
	
	/**
	 * The highest raw light value this sensor can return.
	 * @return
	 */
	public int getHigh();

	/**
	 * The lowest raw light value this sensor can return. Indicates pitch black.
	 * @return
	 */

	public int getLow();
}
