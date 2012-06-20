package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

/**
 * Common base for VectorData implementations
 * 
 * @author Kirk p> Thompson
 *
 */
public abstract class AbstractFilter implements VectorData{
	protected final VectorData source;

	public AbstractFilter(VectorData source){
		this.source = source;
	}
	
	public int getQuantity() {
		return this.source.getQuantity();
	}

	public abstract int getAxisCount();

	public abstract void fetchSamples(float[] dst, int off);
	
}
