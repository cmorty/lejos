package skoehler.sensor.device;

import skoehler.sensor.api.Quantities;
import skoehler.sensor.api.VectorData;

/**
 * This sensor generates a nice triangle curve alternating between 0 and 100
 * @author skoehler
 */
public class DummySensor2 implements VectorData {
	
	public int getQuantity() {
		return Quantities.LENGTH;
	}

	public int getElementCount() {
		return 1;
	}

	public void fetchSample(float[] dst, int off) {
		int y = (int)(System.currentTimeMillis() % 200);
		if (y > 100)
			y = 200-y;
		dst[off] = y;
	}

}
