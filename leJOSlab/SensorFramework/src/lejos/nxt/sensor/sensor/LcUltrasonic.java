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
public class LcUltrasonic extends UltrasonicSensor implements SampleProvider, SensorInfo{

	public LcUltrasonic(I2CPort port) {
		super(port);
	}


	public float fetchSample() {
		return getDistance();
	}


	public int getQuantity() {
		return Quantities.LENGTH;
	}


	public int getElementsCount() {
		return 1;
	}


	public void fetchSample(float[] dst, int off) {
		dst[off]=fetchSample();
	}


	public float getSampleRate() {
		return 20;
	}


	public float getMaximumRange() {
		return 254;
	}

}
