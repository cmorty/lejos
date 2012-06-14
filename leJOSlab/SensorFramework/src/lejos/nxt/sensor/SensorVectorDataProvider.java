package lejos.nxt.sensor;

import lejos.nxt.vecmath.Vector3f;

/**
 * @author Aswin Bouwmeester
 */
public interface SensorVectorDataProvider {
	

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
		void fetchData(Vector3f data);

}
