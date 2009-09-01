package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

public interface ColorDetector {

	/**
	 * 
	 * @return The calibrated/normalized red value (0-255)
	 */
	public int getRedComponent();
	
	/**
	 * 
	 * @return The calibrated/normalized green value (0-255)
	 */
	public int getGreenComponent();
	
	/**
	 * 
	 * @return The calibrated/normalized blue value (0-255)
	 */
	public int getBlueComponent();
	
	/**
	 * Return the Red, Green and Blue values together in one array. All values are 0-255.
	 * @return All three color values. The array index for Red is 0, Green is 1, Blue is 2.
	 */
	public int [] getColor();
	
}
