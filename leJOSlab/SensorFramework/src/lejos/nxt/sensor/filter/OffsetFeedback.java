package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;

/**
 * This class provides a feedback loop for offset correction. To be used together with the calibrate class.
 * <p>
 * @author Aswin
 *
 */
public class OffsetFeedback extends AbstractFilter {
	float speed=0, endSpeed=0;
	float[] offset; 
	float[] reference;
	float error;
	int n=0;

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
	public OffsetFeedback(SampleProvider source, String calibrate, float[] reference, float speed) {
		super(source);
		CalibrationManager calman=new CalibrationManager();
		calman.setCurrent(calibrate);
		offset=calman.getOffset();
		this.speed=speed;
		this.reference=reference;
		endSpeed=speed;
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
	public OffsetFeedback(SampleProvider source, String calibrate, float[] reference, float speed, float endSpeed) {
		this(source,calibrate,reference,speed);
		this.endSpeed=endSpeed;
	}

	

	public void fetchSample(float[] dst, int off) {
		source.fetchSample(dst, off);
		for (int i=0;i<elements;i++) {
			error=dst[i+off]-reference[i];
			offset[i]=offset[i]*(1.0f-speed)+error*speed;
		}
		if (endSpeed<speed) {
			speed*=0.97f;
			if (speed<endSpeed) speed=endSpeed;
			System.out.print(n++);
			System.out.print(',');
			System.out.println(speed);
		}
		
	}
	
	public void setSpeed(float speed) {
		this.speed=speed;
		endSpeed=speed;
	}

}
