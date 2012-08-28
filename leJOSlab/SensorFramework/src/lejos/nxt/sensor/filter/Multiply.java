package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;


/**
 * Provides the widget dohicky, etc. //TODO kpt inserted javadoc placholder. Need real.
 * 
 * @author Sven
 *
 */
public class Multiply extends AbstractFilter{
	
	private final float[] factors;
	private final float[] buffer;
	
	
	public Multiply(SampleProvider source, float factor) {
		super(source);
		this.buffer = new float[elements];
		this.factors = new float[elements];
		for (int i=0;i<elements;i++)
			factors[i]=factor;
	}
	
	public Multiply(SampleProvider source, float[] factors) {
		super(source);
		if (factors.length < elements)
			throw new IllegalArgumentException();
		
		this.buffer = new float[elements];
		this.factors = new float[elements];
		System.arraycopy(factors, 0, this.factors, 0, elements);
	}


	public void fetchSample(float[] dst, int off) {
		this.source.fetchSample(this.buffer, 0);
		int len = this.buffer.length;
		for (int i=0; i<len; i++)
			dst[off + i] = this.buffer[i] * this.factors[i];
	}

}
