package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;


/**
 * Provides a low-pass filter for samples
 * @author Aswin
 *
 */
public class LowPass extends AbstractFilter{
	float[] smoothed;
	long lastTime;
	float timeConstant;

	/**
	 * Constructor
	 * @param source
	 * The source for getting samples
	 * @param timeConstant
	 * The cut-off frequency for the filter
	 */
	public LowPass(SampleProvider source, float timeConstant) {
		super(source);
		smoothed=new float[elements];
		lastTime=System.currentTimeMillis();
		this.timeConstant=timeConstant;
	}

	/**
	 * Fetches a sample from the source and low-passes it
	 * @see http://en.wikipedia.org/wiki/Low-pass_filter
	 */
	public void fetchSample(float[] dst, int off) {
		source.fetchSample(dst,off);
		float dt=(float) ((System.currentTimeMillis()-lastTime)/1000.0);
		lastTime=System.currentTimeMillis();
		float a=dt/(timeConstant+dt);
		for (int axis=0;axis<elements;axis++) {
			smoothed[axis]=smoothed[axis]+a*(dst[off+axis]-smoothed[axis]);
			dst[axis+off]=smoothed[axis];
		}
	}

}
