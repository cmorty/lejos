package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

public class OffsetFilter implements VectorData {
	
	private final VectorData source;
	private final float[] offsets;
	private final float[] buffer;
	
	public OffsetFilter(VectorData source, float[] offsets) {
		int ac = source.getAxisCount();
		if (offsets.length < ac)
			throw new IllegalArgumentException();
		
		this.source = source;
		this.buffer = new float[ac];
		this.offsets = new float[ac];
		System.arraycopy(offsets, 0, this.offsets, 0, ac);
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
			dst[off + i] = this.buffer[i] + this.offsets[i];
	}

}
