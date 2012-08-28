package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;


/**
 * Select elements from a sample
 * Provides the widget dohicky, etc. //TODO kpt inserted javadoc placholder. Need real.
 * 
 * @author Sven
 *
 */
public class ElementsSelector extends AbstractFilter {
	
	private final float[] buffer;
	private final int[] axes;
	
	public ElementsSelector(SampleProvider source, int[] axes) {
		super(source);
		for (int i=0; i<axes.length; i++)
			if (axes[i] < 0 || axes[i] >= elements)
				throw new IllegalArgumentException();
		
		this.buffer = new float[elements];
		this.axes = new int[axes.length];
		System.arraycopy(axes, 0, this.axes, 0, axes.length);
	}


	@Override
	public int getElementsCount() {
		return this.axes.length;
	}

	public void fetchSample(float[] dst, int off) {
		this.source.fetchSample(this.buffer, 0);
		for (int i=0; i<this.axes.length; i++)
			dst[off + i] = this.buffer[this.axes[i]];
	}

}
