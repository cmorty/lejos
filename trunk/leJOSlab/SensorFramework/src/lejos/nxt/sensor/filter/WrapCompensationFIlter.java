package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;


/**
 * Compensates the wrap-around (e.g. from +180 to -180 or from 360 to 0 that
 * some sensors perform. Can be used for any wrap-around
 */
public class WrapCompensationFIlter extends AbstractFilter {
	private final float[] limits;
	private final float[] buffer;
	private final int[] wraps;

	/**
	 * Creates a new filter with compensates wrap arounds on every axis.
	 * 
	 * @param source
	 *            data source
	 * @param wrap
	 *            for each axis: maximal value - minimal value of the sensor
	 *            output, e.g. 360 for a sensor ranging from -180 to 180
	 */
	public WrapCompensationFIlter(SampleProvider source, float[] wrap) {
		super(source);
		if (wrap.length < elements)
			throw new IllegalArgumentException();

		this.wraps = new int[elements];
		this.limits = new float[elements];
		this.buffer = new float[2 * elements];
		System.arraycopy(wrap, 0, this.limits, 0, elements);
	}


	public void fetchSample(float[] dst, int off) {
		this.source.fetchSample(buffer, 0);
		for (int i = 0; i < elements; i++) {
			float limit = this.limits[i];
			float diff = 2 * (this.buffer[i + elements] - this.buffer[i]);
			if (diff >= limit)
				this.wraps[i]++;
			else if (diff <= -limit)
				this.wraps[i]--;

			dst[off + i] = this.wraps[i] * limit + this.buffer[i];
		}
		System.arraycopy(this.buffer, 0, this.buffer, elements, elements);
	}
}
