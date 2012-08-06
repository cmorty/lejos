package skoehler.sensor.device;

import skoehler.sensor.api.Constants;
import skoehler.sensor.api.Quantities;
import skoehler.sensor.api.VectorData;

public class DummySensor implements VectorData {

	public int getQuantity() {
		return Quantities.ACCELERATION;
	}

	public int getElementCount() {
		return 3;
	}

	public void fetchSample(float[] dst, int off) {
		dst[off+0] = 0;
		dst[off+1] = 0;
		dst[off+2] = -Constants.G;
	}

}
