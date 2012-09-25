package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;

// TODO: think about the build up of numerical errors over time


public class SumFilter extends SampleBuffer {
	float[] sum;
	float[] buf;

	public SumFilter(SampleProvider source, int length) {
		super(source, length);
		sum=new float[elements];
		buf=new float[elements];
	}
	
	@Override
	public void fetchSample(float[] sample,int off) {
		// substract oldest sample from sum
		getOldest(buf,0);
		for (int i=0;i<elements;i++) {
			sum[i]-=buf[i];
		}
		super.fetchSample(buf, 0);
		// add the newest sample to sum
		for (int i=0;i<elements;i++) {
			sum[i]+=buf[i];
			sample[i+off]=sum[i];
		}
	}

}
