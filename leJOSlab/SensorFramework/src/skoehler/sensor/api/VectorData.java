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
	 * Intentially, this does not use Vector3f. See {@link AveragingFilter} for an example why.
	 */
	void fetchSamples(float[] dst, int off);
}
