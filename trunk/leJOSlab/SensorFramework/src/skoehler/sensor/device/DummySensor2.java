package skoehler.sensor.device;

import skoehler.sensor.api.Quantities;
import skoehler.sensor.api.VectorData;

/**
 * This sensor generates a nice triangle curve alternating between 0 and 100
 * @author skoehler
 */
public class DummySensor2 implements VectorData {
	
	private int x;

	public int getQuantity() {
		return Quantities.LENGTH;
	}

	public int getElementCount() {
		return 1;
	}

	public void fetchSample(float[] dst, int off) {
		int y = x;
		if (y > 100)
			y = 200-y;
		dst[off] = y;
		x = (x +1) % 200;
	}

}
