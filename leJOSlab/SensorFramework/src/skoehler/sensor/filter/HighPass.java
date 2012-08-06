package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

/**
 * Provides a high-pass filter for samples.
 * @author Aswin
 *
 */
public class HighPass extends AbstractFilter{
	float[] smoothed;
	float[] previous;
	long lastTime;
	int axisCount;
	float timeConstant;

	/**
	 * Constructor
	 * @param source
	 * Source for getting samples
	 * @param timeConstant
	 * Cut-off frequency off highpass filter
	 */
	public HighPass(VectorData source, float timeConstant) {
		super(source);
		axisCount=source.getElementCount();
		smoothed=new float[axisCount];
		previous=new float[axisCount];
		lastTime=System.currentTimeMillis();
		this.timeConstant=timeConstant;
	}

	@Override
	public int getElementCount() {
		return axisCount;
	}

	/** 
	 * Fetches a sample from the source and high passes it. 
	 * @see http://en.wikipedia.org/wiki/High-pass_filter
	 * @see skoehler.sensor.filter.AbstractFilter#fetchSample(float[], int)
	 */
	@Override
	public void fetchSample(float[] dst, int off) {
		source.fetchSample(dst,off);
		float dt=(float) ((System.currentTimeMillis()-lastTime)/1000.0);
		lastTime=System.currentTimeMillis();
		float a=timeConstant/ (timeConstant+dt);
		for (int axis=0;axis<axisCount;axis++) {
			smoothed[axis] = a * smoothed[axis] + a * (dst[off+axis]-previous[axis]);
			previous[axis]=dst[axis+off];
			dst[axis+off]=smoothed[axis];
		}
	}


}
