package lejos.robotics;

public interface ColorDetector {

	/**
	 * 
	 * @return The calibrated/normalized red value (0-255)
	 */
	public int getRed();
	
	/**
	 * 
	 * @return The calibrated/normalized green value (0-255)
	 */
	public int getGreen();
	
	/**
	 * 
	 * @return The calibrated/normalized blue value (0-255)
	 */
	public int getBlue();
	
	/**
	 * Return the Red, Green and Blue values together in one array. All values are 0-255.
	 * @return All three color values. The array index for Red is 0, Green is 1, Blue is 2.
	 */
	public int [] getRGB();
	
}
