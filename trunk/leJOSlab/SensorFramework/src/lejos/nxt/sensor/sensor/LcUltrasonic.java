package lejos.nxt.sensor.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.sensor.api.*;

/**
 * Turns existing US sensor in a SensorDataProvider.<br>
 * Incomplete implementation, supports only continuous mode;
 * @author Aswin
 *
 */
public class LcUltrasonic extends UltrasonicSensor implements SensorDataProvider{

	public LcUltrasonic(I2CPort port) {
		super(port);
	}

	public int getMinimumFetchInterval() {
		// Hmm, not visible;
		//return DELAY_DATA_OTHER;
		return 30;
	}

	public float fetchData() {
		return getDistance();
	}

}
