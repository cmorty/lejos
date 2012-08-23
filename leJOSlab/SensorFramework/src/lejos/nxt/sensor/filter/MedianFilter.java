package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;

public class MedianFilter extends SampleBuffer {

	public MedianFilter(SampleProvider source, int bufferSize) {
		super(source, bufferSize);
	}

	@Override
	public void fetchSample(float[] sample, int off) {
		float current, smallest, value;
		int n;
		super.fetchSample(sample, off);
		for (int i=0;i<elements;i++) {
			current=Float.NEGATIVE_INFINITY;
			n=0;
			while(n<=Math.floor(actualSize/2.0)) {
			smallest=Float.POSITIVE_INFINITY;
			for (int j=0;j<actualSize;j++) {
				value=sampleBuffer[toPos(i,j)];
				if (value==smallest) 
					n++;
				else 
					if (value > current && value<smallest) smallest=value;
			}
			current=smallest;

			n++;
			}
			sample[i+off]=current;
		}
		
	}
	

}
