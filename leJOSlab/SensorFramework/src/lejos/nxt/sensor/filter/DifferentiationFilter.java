package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;

public class DifferentiationFilter extends AbstractFilter {
	float[] previous;
	float[] sample;
	long lastTime=0;
	

	public DifferentiationFilter(SampleProvider source) {
		super(source);
		previous=new float[elements];
		sample=new float[elements];
	}

	/* returns the defferential of the input signal. <P>
	 * for example: position becomes speed
	 * 
	 */
	public void fetchSample(float[] dst, int off) {
		float dt;
		source.fetchSample(sample,0);
		dt=(lastTime-System.currentTimeMillis())*.1000f;
		for (int i=0;i<elements;i++) {
			if (lastTime != 0) 
				dst[i+off]=(sample[i]-previous[i])*dt;
			previous[i]=sample[i];
		}
		lastTime=System.currentTimeMillis();
	}

}
