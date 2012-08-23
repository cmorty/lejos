package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;

public class MaximumFilter extends SampleBuffer {

	float[] max;
	float[] buf, oldest;

	
	@Override
	public void fetchSample(float[] sample,int off) {
		getOldest(oldest,0);
		super.fetchSample(buf, 0);
		for (int i=0;i<elements;i++) {
			// if the dropped sample happens to be the biggest sample, then rescan the buffer for a smallest value;
			if (oldest[i] == max[i] || buf[i] > max[i]) {
				max[i]=Float.NEGATIVE_INFINITY;
				for (int j=0;j<actualSize;j++) {
					max[i]=Math.max(sampleBuffer[toPos(i,j)],max[i]);
				}
				sample[i+off]=max[i];
			}
		}
	}
	
	public MaximumFilter(SampleProvider source, int bufferSize) {
		super(source, bufferSize);
		max=new float[elements];
		for (int i=0;i<elements;i++)
			max[i]=Float.NEGATIVE_INFINITY;
		buf=new float[elements];
		oldest=new float[elements];
		
	}

}
