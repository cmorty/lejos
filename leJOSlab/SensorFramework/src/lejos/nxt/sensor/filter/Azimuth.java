package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;

/**
 * This class converts magnetic field strengths from a magnetometer (compass sensor) into azimuth.<br>
 * Azimuth (expressed in radians) is the angle between North and the X-axis of the magnetometer.
 * <p>
 * When an instance of an accelerometer is provided in the constructor this class will apply tilt correction.
 * @author Aswin
 *
 */
public class Azimuth extends AbstractFilter{
	float[] m=new float[3];
	SampleProvider accelerometer;
	float[] t;

	public Azimuth(SampleProvider source) {
		super(source);
		elements=1;
	}

	public Azimuth(SampleProvider source, SampleProvider accelerometer) {
		super(source);
		elements=1;
		this.accelerometer=accelerometer;
		t=new float[3];
	}

	
	@Override
	public float fetchSample() {
		double direction;
		source.fetchSample(m,0);
		if (accelerometer ==null) 
			direction=Math.atan2(m[1], m[0]);
		else {
			// TODO: Test tilt correction 
			/** Apply tilt correction.
			 * Tilt correction is based on the fact that the dot product of two vectors gives a vector perpendicular to
			 * the other two vectors. A vector perpendicular to the gravity vector (down) and the magnetic field vector (north) points west.
			 */
			accelerometer.fetchSample(t, 0);
			direction=Math.atan2(m[0]*t[2] - m[2]*t[0], t[1]*m[2] - t[2]*m[1])+.5*Math.PI;
		}
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
