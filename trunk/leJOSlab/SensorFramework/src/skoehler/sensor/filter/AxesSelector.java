package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

/**
 * Provides the widget dohicky, etc. //TODO kpt inserted javadoc placholder. Need real.
 * 
 * @author Sven
 *
 */
public class AxesSelector extends AbstractFilter {
	
	private final float[] buffer;
	private final int[] axes;
	
	public AxesSelector(VectorData source, int[] axes) {
		super(source);
		int ac = source.getElementCount();
		for (int i=0; i<axes.length; i++)
			if (axes[i] < 0 || axes[i] >= ac)
				throw new IllegalArgumentException();
		
		this.buffer = new float[ac];
		this.axes = new int[axes.length];
		System.arraycopy(axes, 0, this.axes, 0, axes.length);
	}


	@Override
	public int getElementCount() {
		return this.axes.length;
	}

	@Override
	public void fetchSample(float[] dst, int off) {
		this.source.fetchSample(this.buffer, 0);
		for (int i=0; i<this.axes.length; i++)
			dst[off + i] = this.buffer[this.axes[i]];
	}

}
