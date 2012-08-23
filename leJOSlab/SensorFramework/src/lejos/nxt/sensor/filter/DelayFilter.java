package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;

/**
 * This class serves the purpose of compensating for a group delay.
 * E.g. an averaging filter of length 5 causes a group delay of 2 samples.
 * This filter can be used to delay without filtering.
 */
public class DelayFilter extends SampleBuffer {

	private float[] buf;

	public DelayFilter(SampleProvider source, int length) {
		super(source, length);
		buf=new float[elements];
	}

	@Override
	public void fetchSample(float[] sample, int off) {
		getOldest(sample,off);
		super.fetchSample(buf,0);
	}

}
