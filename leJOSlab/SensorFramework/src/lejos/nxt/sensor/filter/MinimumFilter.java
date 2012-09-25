package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;

public class MinimumFilter extends SampleBuffer {

	float[]	min;
	float[]	buf, oldest;

	@Override
	public void fetchSample(float[] sample, int off) {
		getOldest(oldest, 0);
		super.fetchSample(buf, 0);
		for (int i = 0; i < elements; i++) {
			// if the dropped sample happens to be the smallest sample, then rescan
			// the buffer for a smallest value;
			if (oldest[i] == min[i] || buf[i] < min[i]) {
				min[i] = Float.POSITIVE_INFINITY;
				for (int j = 0; j < actualSize; j++) {
					min[i] = Math.min(sampleBuffer[j*elements+i], min[i]);
				}
			}
			sample[i + off] = min[i];
		}
	}

	public MinimumFilter(SampleProvider source, int bufferSize) {
		super(source, bufferSize);
		min = new float[elements];
		buf = new float[elements];
		oldest = new float[elements];

	}

}
