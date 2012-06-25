package skoehler.sensor.api;

/**
 * I don't care, if this omitted. Do we care about a convenience method for easy
 * single-axis data access?
 */
public interface ScalarData extends VectorData {

	/**
	 * Samples are returned in the default unit of the quantity returned by {@link #getQuantity()}.
	 * Take a look {@link Quantities} to see which unit is the default unit.
	 * @return a single sample
	 */
	float fetchSample();
}
