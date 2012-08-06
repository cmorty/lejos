package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

/**
 * Common base for VectorData implementations
 * 
 * @author Kirk P. Thompson
 *
 */
public abstract class AbstractFilter implements VectorData{
	protected final VectorData source;

	/**
	 * Create a filter passing a source to be decorated
	 * @param source The source sensor/filter to be used
	 */
	public AbstractFilter(VectorData source){
		this.source = source;
	}
	
	public int getQuantity() {
		return this.source.getQuantity();
	}

	/* (non-Javadoc)
	 * @see skoehler.sensor.api.VectorData#getAxisCount()
	 */
	public abstract int getElementCount();

	/* (non-Javadoc)
	 * @see skoehler.sensor.api.VectorData#fetchSamples(float[], int)
	 */
	public abstract void fetchSample(float[] dst, int off);
	
}
