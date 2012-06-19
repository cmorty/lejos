package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

/**
 * This class serves the purpose of compensating for a group delay.
 * E.g. an averaging filter of length 5 causes a group delay of 2 samples.
 * This filter can be used to delay without filtering.
 */
public class DelayFilter implements VectorData {
	
	private final VectorData source;
	private final int axisCount;
	private final float[] buffer;
	private int currentPos;
	
	public DelayFilter(VectorData source, int length) {
		if (length < 1)
			throw new IllegalArgumentException();
		
		this.axisCount = source.getAxisCount();
		this.source = source;
		this.buffer = new float[this.axisCount * length];
	}

	public int getQuantity() {
		return this.source.getQuantity();
	}

	public int getAxisCount() {
		return this.axisCount;
	}

	public void fetchSamples(float[] dst, int off) {
		int pos = this.currentPos;
		for (int i=0; i<this.axisCount; i++)
			dst[off + i] = this.buffer[pos + i];
		
		this.source.fetchSamples(this.buffer, pos);
		this.currentPos = (pos + this.axisCount) % this.buffer.length;
	}

}
