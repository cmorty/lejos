package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;

/**
 * Thes class extracts a single axis from a vector returned by a Triaxis sensor,
 * 
 * @author Aswin Bouwmeester
 */
public class ToScalar implements SensorDataProvider {
	private SensorVectorDataProvider	source;
	private String										axis	= "X";
	private Vector3f									buf		= new Vector3f();

	/**
	 * Constructor
	 * 
	 * @param source
	 *          The source where the filter gets its data from
	 */
	public ToScalar(SensorVectorDataProvider source) {
		this.source = source;
	}

	public float fetchData() {
		source.fetchData(buf);
		if (axis.equals("X"))
			return buf.x;
		if (axis.equals("Y"))
			return buf.y;
		return buf.z;
	}

	public int getMinimumFetchInterval() {
		return source.getMinimumFetchInterval();
	}

	/**
	 * Returns the axis that is extracted upon a fetchData call
	 * 
	 * @return The axis being X, Y or Z.
	 */
	public String getAxis() {
		return axis;
	}

	/**
	 * Sets the axis that should be extracted from the vector
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
