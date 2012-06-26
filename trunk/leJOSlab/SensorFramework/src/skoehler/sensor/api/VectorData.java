package skoehler.sensor.api;

import skoehler.sensor.filter.AveragingFilter;

/**
 * Axes are aligned according to right hand rules (axis order, direction, rotation).
 * Colors are either RGB or CMY(K).
 */
public interface VectorData {
	int getQuantity();
	int getAxisCount();
	
	/**
	 * Intentionally, this does not use Vector3f. See {@link AveragingFilter} for an example why.
	 * Samples are returned in the default unit of the quantity returned by {@link #getQuantity()}.
	 * Take a look {@link Quantities} to see which unit is the default unit.
	 * This method writes as many samples as this sensor has axes (see {@link #getAxisCount()})
	 * to the given destination array.
	 * 
	 * @param dst destination array 
	 * @param off index of first value 
	 */
	void fetchSample(float[] dst, int off);
}
