package skoehler.sensor.device;

import skoehler.sensor.api.ScalarData;

public abstract class AbstractScalarData implements ScalarData {

	public final int getAxisCount() {
		return 1;
	}

	public final void fetchSamples(float[] dst, int off) {
		dst[off] = this.fetchSample();
	}

}
