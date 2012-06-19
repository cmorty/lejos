package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

public class ScaleFilter implements VectorData {
	
	private final VectorData source;
	private final float[] factors;
	private final float[] buffer;
	
	public ScaleFilter(VectorData source, float[] factors) {
		int ac = source.getAxisCount();
		if (factors.length < ac)
			throw new IllegalArgumentException();
		
		this.source = source;
		this.buffer = new float[ac];
		this.factors = new float[ac];
		System.arraycopy(factors, 0, this.factors, 0, ac);
	}

	public int getQuantity() {
		return this.source.getQuantity();
	}

	public int getAxisCount() {
		return this.buffer.length;
	}

	public void fetchSamples(float[] dst, int off) {
		this.source.fetchSamples(this.buffer, 0);
		int len = this.buffer.length;
		for (int i=0; i<len; i++)
			dst[off + i] = this.buffer[i] * this.factors[i];
	}

}
