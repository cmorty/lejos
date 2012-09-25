package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;


/**
 * Provides a high-pass filter for samples.
 * @author Aswin
 *
 */
public class HighPassFilter extends AbstractFilter{
	float[] smoothed;
	float[] previous;
	long lastTime;
	float timeConstant;

	/**
	 * Constructor
	 * @param source
	 * Source for getting samples
	 * @param timeConstant
	 * Cut-off frequency off highpass filter
	 */
	public HighPassFilter(SampleProvider source, float timeConstant) {
		super(source);
		smoothed=new float[elements];
		previous=new float[elements];
		lastTime=System.currentTimeMillis();
		this.timeConstant=timeConstant;
	}

	/** 
	 * Fetches a sample from the source and high passes it. 
	 * @see http://en.wikipedia.org/wiki/High-pass_filter
	 */
	public void fetchSample(float[] dst, int off) {
		source.fetchSample(dst,off);
		float dt=(float) ((System.currentTimeMillis()-lastTime)/1000.0);
		lastTime=System.currentTimeMillis();
		float a=timeConstant/ (timeConstant+dt);
		for (int i=0;i<elements;i++) {
			smoothed[i] = a * smoothed[i] + a * (dst[off+i]-previous[i]);
			previous[i]=dst[i+off];
			dst[i+off]=smoothed[i];
		}
	}


}
