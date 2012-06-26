package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

/**
 * Provides the widget dohicky, etc. //TODO kpt inserted javadoc placholder. Need real.
 * 
 * @author Sven
 *
 */
public class OffsetFilter extends AbstractFilter {
	
	private final float[] offsets;
	private final float[] buffer;
	
	public OffsetFilter(VectorData source, float[] offsets) {
		super(source);
		int ac = source.getAxisCount();
		if (offsets.length < ac)
			throw new IllegalArgumentException();
		
		this.buffer = new float[ac];
		this.offsets = new float[ac];
		System.arraycopy(offsets, 0, this.offsets, 0, ac);
	}

	@Override
	public int getAxisCount() {
		return this.buffer.length;
	}

	@Override
	public void fetchSample(float[] dst, int off) {
		this.source.fetchSample(this.buffer, 0);
		int len = this.buffer.length;
		for (int i=0; i<len; i++)
			dst[off + i] = this.buffer[i] + this.offsets[i];
	}

}
