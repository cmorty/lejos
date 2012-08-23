package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;

public class MeanFilter extends SumFilter {

	public MeanFilter(SampleProvider source, int length) {
		super(source, length);
	}
	
	@Override
	public void fetchSample(float[] sample, int off) {
		super.fetchSample(sample,off);
		for (int i=0;i<elements;i++)
			sample[i+off]/=actualSize;
	}

}
