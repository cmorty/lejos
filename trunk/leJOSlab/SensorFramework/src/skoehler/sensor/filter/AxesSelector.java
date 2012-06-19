package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

public class AxesSelector implements VectorData {
	
	private final VectorData source;
	private final float[] buffer;
	private final int[] axes;
	
	public AxesSelector(VectorData source, int[] axes) {
		int ac = source.getAxisCount();
		for (int i=0; i<axes.length; i++)
			if (axes[i] < 0 || axes[i] >= ac)
				throw new IllegalArgumentException();
		
		this.source = source;
		this.buffer = new float[ac];
		this.axes = new int[axes.length];
		System.arraycopy(axes, 0, this.axes, 0, axes.length);
	}

	public int getQuantity() {
		return this.source.getQuantity();
	}

	public int getAxisCount() {
		return this.axes.length;
	}

	public void fetchSamples(float[] dst, int off) {
		this.source.fetchSamples(this.buffer, 0);
		for (int i=0; i<this.axes.length; i++)
			dst[off + i] = this.buffer[this.axes[i]];
	}

}
