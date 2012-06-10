package lejos.nxt.sensor;

/**
 * @author Aswin Bouwmeester
 * @author Sven Kohler
 * @author Kirk P. Thompson
 *
 */
public interface SensorValueProvider {
	/**
	 * The refresh rate is the minumum delay between calls to query the sensor value. 
	 * Should probbaly be at least 4 ms for analog due to AVR constraints. I2C=?
	 * 
	 * @return the minumum delay in milliseconds
	 */
	int getRefreshRate();
	
	/**
	 * The value is what the sensor reads and returns
	 * @return the unit quantity read by the sensor
	 */
	float getValue();
	
	/**
	 * The raw value what the sensor reads parsed into a <code>int</code>.
	 * @return The unit quantity read by the sensor
	 */
	int getRawValue();
	
	/**
	 * The raw byte[] array what the sensor reads.
	 * @return The raw data read by the sensor
	 */
	byte[] getRaw();
	
	/**
	 * The SI base units. Return an array of unit: 
	 * m/s^2 would be:
	 *   e[0]=SensorSIUnits.LENGTH (0)
	 *   e[1]=SensorSIUnits.TIME (2)
	 *   e[2]=SensorSIUnits.TIME (2)
	 *  
	 *  temperature would be:
	 *    e[0]=SensorSIUnits.TEMPERATURE (4)
	 * @return
	 */
	int[] getUnits();
}
