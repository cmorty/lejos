package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

/**
 * Provides the widget dohicky, etc. //TODO kpt inserted javadoc placholder. Need real.
 * 
 * @author Sven
 *
 */
public class ScaleFilter extends AbstractFilter{
	
	private final float[] factors;
	private final float[] buffer;
	
	public ScaleFilter(VectorData source, float[] factors) {
		super(source);
		int ac = source.getElementCount();
		if (factors.length < ac)
			throw new IllegalArgumentException();
		
		this.buffer = new float[ac];
		this.factors = new float[ac];
		System.arraycopy(factors, 0, this.factors, 0, ac);
	}


	@Override
	public int getElementCount() {
		return this.buffer.length;
	}

	@Override
	public void fetchSample(float[] dst, int off) {
		this.source.fetchSample(this.buffer, 0);
		int len = this.buffer.length;
		for (int i=0; i<len; i++)
			dst[off + i] = this.buffer[i] * this.factors[i];
	}

}
