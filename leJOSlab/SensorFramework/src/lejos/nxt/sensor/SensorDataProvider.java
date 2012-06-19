package lejos.nxt.sensor;

/**
 * @author Aswin Bouwmeester
 * @author Sven Kohler
 * @author Kirk P. Thompson
 *
 */
public interface SensorDataProvider {

	/**
	 * The refresh rate is the minumum delay between calls to query the sensor value. 
	 * Should probbaly be at least 4 ms for analog due to AVR constraints. I2C=?
	 * 
	 * @return the minumum delay in milliseconds
	 */
	int getMinimumFetchInterval();
	
	/**
	 * The value is what the sensor reads and returns
	 * @return the unit quantity read by the sensor
	 */
	float fetchData();
	
}
