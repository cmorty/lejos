package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

/**
 * This class serves the purpose of compensating for a group delay.
 * E.g. an averaging filter of length 5 causes a group delay of 2 samples.
 * This filter can be used to delay without filtering.
 */
public class DelayFilter extends AbstractFilter {

	private final int axisCount;
	private final float[] buffer;
	private int currentPos;
	
	public DelayFilter(VectorData source, int length) {
		super(source);
		if (length < 1)
			throw new IllegalArgumentException();
		
		this.axisCount = source.getAxisCount();
		this.buffer = new float[this.axisCount * length];
	}

	@Override
	public int getAxisCount() {
		return this.axisCount;
	}

	@Override
	public void fetchSample(float[] dst, int off) {
		int pos = this.currentPos;
		for (int i=0; i<this.axisCount; i++)
			dst[off + i] = this.buffer[pos + i];
		
		this.source.fetchSample(this.buffer, pos);
		this.currentPos = (pos + this.axisCount) % this.buffer.length;
	}

}
