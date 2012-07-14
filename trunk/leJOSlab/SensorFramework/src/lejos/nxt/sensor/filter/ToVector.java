package lejos.nxt.sensor.filter;

import lejos.nxt.LCD;
import lejos.nxt.sensor.api.*;

/**
 * Converts a scalar into a vector.
 * Two of the vector axes will be set to zero, the third, user specified, vector axis will get the scalars value.
 * @author Aswin
 *
 */
public class ToVector implements SampleProviderVector{
	private SampleProvider	source;
	private String										axis	= "X";
	private float									buf		;

	/**
	 * Constructor
	 * 
	 * @param source
	 *          The source where the filter gets its data from
	 */
	public ToVector(SampleProvider source) {
		this.source = source;
	}

	public void fetchSample(Vector3f data) {
		// also support not to override non-selected axes? 
		data.x=0;
		data.y=0;
		data.z=0;
		buf=source.fetchSample();
		if (axis.equals("X")) 
			data.x=buf;
		if (axis.equals("Y"))
			data.y=buf;
		if (axis.equals("Z"))
			data.z=buf;
	}

	public int getMinimumFetchInterval() {
		return source.getMinimumFetchInterval();
	}

	/**
	 * Returns the vector axis that is used to store the scalar
	 * 
	 * @return The axis being X, Y or Z.
	 */
	public String getAxis() {
		return axis;
	}

	/**
	 * Sets the vector axis that is used to store the scalar
	 * 
	 * @param axis
	 *          The axis to axtract, valid values are X, Y or Z.
	 */
	public void setAxis(String axis) {
		axis = axis.toUpperCase();
		if ("XYZ".indexOf(axis) == -1)
			throw new IllegalArgumentException("Invalid axis");
		this.axis = axis;
	}

}
