package lejos.nxt.sensor.sensor;

import lejos.nxt.sensor.api.*;

public class DummySensor implements SampleProvider{

	public float fetchSample() {
		return (float)Math.random()*15.0f;
	}

	public int getQuantity() {
		return Quantities.RAW;
	}

	public int getElementsCount() {
		return 1;
	}

	public void fetchSample(float[] dst, int off) {
		dst[off]=fetchSample();
	}

}
