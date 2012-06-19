package skoehler.sensor.api;

/**
 * I don't care, if this omitted. Do we care about a convenience method for easy
 * single-axis data access?
 */
public interface ScalarData extends VectorData {
	int getQuantity();
	float fetchSample();
}
