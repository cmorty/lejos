package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

public class AveragingFilter implements VectorData {
	
	private final VectorData source;
	private final int axisCount;
	private final float[] buffer;
	private final float[] sum;
	private int currentPos;
	
	public AveragingFilter(VectorData source, int length) {
		if (length < 1)
			throw new IllegalArgumentException();
		
		this.axisCount = source.getAxisCount();
		this.source = source;
		this.buffer = new float[this.axisCount * length];
		this.sum = new float[this.axisCount];
	}

	public int getQuantity() {
		return this.source.getQuantity();
	}

	public int getAxisCount() {
		return this.axisCount;
	}

	public void fetchSamples(float[] dst, int off) {
		//TODO this filter might develop an offset over time, but is very fast.
		//Probably offer an offset free alternative.
		int pos = this.currentPos;
		for (int i=0; i<this.axisCount; i++)
			this.sum[i] -= this.buffer[pos + i];
		
		this.source.fetchSamples(this.buffer, pos);
		
		for (int i=0; i<this.axisCount; i++)
			this.sum[i] += this.buffer[pos + i];
		
		this.currentPos = (pos + this.axisCount) % this.buffer.length;
		
		float f = 1f / (this.buffer.length / this.axisCount);
		for (int i=0; i<this.axisCount; i++)
			dst[off + i] = this.sum[i] * f;
	}

}
