package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;


/**
 * Adds a value to the elements of a sample
 * 
 * @author Sven
 *
 */
public class Add extends AbstractFilter {
	
	private final float[] offsets;
	private final float[] buffer;
	
	
	public Add(SampleProvider source, float offset) {
		super(source);
		
		this.buffer = new float[elements];
		this.offsets = new float[elements];
		for (int i=0;i<elements;i++)
			offsets[i]=offset;
	}

	
	public Add(SampleProvider source, float[] offsets) {
		super(source);
		if (offsets.length < elements)
			throw new IllegalArgumentException();
		
		this.buffer = new float[elements];
		this.offsets = new float[elements];
		System.arraycopy(offsets, 0, this.offsets, 0, elements);
	}


	public void fetchSample(float[] dst, int off) {
		this.source.fetchSample(this.buffer, 0);
		int len = this.buffer.length;
		for (int i=0; i<len; i++)
			dst[off + i] = this.buffer[i] + this.offsets[i];
	}

}
