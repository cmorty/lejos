package lejos.nxt.sensor.sensor;

import lejos.nxt.sensor.api.*;

public class DummySensor implements SampleProvider{
	static float[] values={3,7,7,1,2,2,14};
	int index=-1;

	public float fetchSample() {
		index++;
		if (index==values.length) index=0;
		return values[index];
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
