package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

/**
 * Provides a low-pass filter for samples
 * @author Aswin
 *
 */
public class LowPass extends AbstractFilter{
	float[] smoothed;
	long lastTime;
	int axisCount;
	float timeConstant;

	/**
	 * Constructor
	 * @param source
	 * The source for getting samples
	 * @param timeConstant
	 * The cut-off frequency for the filter
	 */
	public LowPass(VectorData source, float timeConstant) {
		super(source);
		axisCount=source.getElementCount();
		smoothed=new float[axisCount];
		lastTime=System.currentTimeMillis();
		this.timeConstant=timeConstant;
	}

	@Override
	public int getElementCount() {
		return axisCount;
	}

	/**
	 * Fetches a sample from the source and low-passes it
	 * @see http://en.wikipedia.org/wiki/Low-pass_filter
	 * @see skoehler.sensor.filter.AbstractFilter#fetchSample(float[], int)
	 */
	@Override
	public void fetchSample(float[] dst, int off) {
		source.fetchSample(dst,off);
		float dt=(float) ((System.currentTimeMillis()-lastTime)/1000.0);
		lastTime=System.currentTimeMillis();
		float a=dt/(timeConstant+dt);
		for (int axis=0;axis<axisCount;axis++) {
			smoothed[axis]=smoothed[axis]+a*(dst[off+axis]-smoothed[axis]);
			dst[axis+off]=smoothed[axis];
		}
	}

}
