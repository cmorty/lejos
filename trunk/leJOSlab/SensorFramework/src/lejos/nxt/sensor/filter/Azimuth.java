package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;

/**
 * This class converts magnetic field strengths from a magnetometer (compass sensor) into azimuth.<br>
 * Azimuth is the angle between North and the X-axis of the magnetometer.
 * It is expressed in radians.
 * 
 * @author Aswin
 *
 */
public class Azimuth extends AbstractFilter{
	float[] dummy=new float[3];

	public Azimuth(SampleProvider source) {
		super(source);
		elements=1;
	}

	@Override
	public float fetchSample() {
		source.fetchSample(dummy,0);
		double direction=Math.atan2(dummy[1], dummy[0]);
		if (direction<0) direction+=2*Math.PI;
		return (float) direction;
	}

	@Override
	public int getQuantity() {
		return Quantities.ANGLE;
	}


	public void fetchSample(float[] dst, int off) {
		dst[off]=fetchSample();
	}
}
