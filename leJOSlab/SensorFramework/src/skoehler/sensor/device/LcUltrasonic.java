package skoehler.sensor.device;

import lejos.nxt.I2CPort;
import lejos.nxt.UltrasonicSensor;
import skoehler.sensor.api.*;

public class LcUltrasonic extends UltrasonicSensor implements VectorData{

	public LcUltrasonic(I2CPort port) {
		super(port);
	}

	public int getQuantity() {
		return Quantities.LENGTH;
	}

	public int getElementCount() {
		return 1;
	}

	public void fetchSample(float[] dst, int off) {
		dst[off]=getDistance();
	}

}
