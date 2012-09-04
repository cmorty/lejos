package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;

public class OffsetCorrection extends AbstractFilter {
	float speed=0, endSpeed=0;
	float[] offset; 
	float[] reference;
	float error;
	int n=0;
	
	
	public OffsetCorrection(SampleProvider source) {
		this(source,new float[]{0,0,0});
	}
	
	public OffsetCorrection(SampleProvider source, float[] reference) {
		this(source,reference,0.1f);
	}
	
	
	
	/**
	 * Constructor
	 * @param source
	 * Source for sample
	 * @param calibrate
	 * Name of the calibration set to use for offset feedback
	 * @param reference
	 * Array off reference values to calculate offset against (offset=sampleValue-referenceValue)
	 * @param speed
	 * Speed to update offset value
	 */
	public OffsetCorrection(SampleProvider source, float[] reference, float speed) {
		this(source,reference,1f,.0001f);
	}

	/**
	 * Constructor
	 * @param source
	 * Source for sample
	 * @param calibrate
	 * Name of the calibration set to use for offset feedback
	 * @param reference
	 * Array off reference values to calculate offset against (offset=sampleValue-referenceValue)
	 * @param speed
	 * Begin speed to update offset value
	 * @param endSpeed
	 * End speed to update offset value
	 */
	public OffsetCorrection(SampleProvider source, float[] reference, float speed, float endSpeed) {
		super(source);
		offset=new float[elements];
		this.speed=speed;
		this.reference=reference;
		this.endSpeed=endSpeed;
	}


	public void fetchSample(float[] dst, int off) {
		source.fetchSample(dst, off);
		for (int i=0;i<elements;i++) {
			error=dst[i+off]-reference[i];
			offset[i]=offset[i]*(1.0f-speed)+error*speed;
			dst[i+off]-=offset[i];
		}
		
		if (endSpeed<speed) {
			speed*=0.97f;
			if (speed<endSpeed) speed=endSpeed;
		}
		
	}
}
