package lejos.nxt.sensor.api;


/**
 * Classes that support this interface are able to fetch a sample (data originating from 
 * a sensor), to process it and to pass it on. 
 * <p>
 * All sensors and filters in the LeJOS sensor framework must implement this
 * interface directly or via subclassing <code>AbstractFilter</code>.
 */
public interface SampleProvider {
	
	/**
	 * Returns the quantity of a sample.
	 * 
	 * @return The quantity
	 */
	int getQuantity();
	
	/**
	 * Returns the number of values that are in a sample.
	 * 
	 * @return The number of values
	 */
	int getElementsCount();
	
	/**
	 * Fetches a sample from the source and processes it
	 * 
	 * @param dst  array to store the processed values from a sample
	 * @param off  Offset to use;
	 * 
	 */
	void fetchSample(float[] dst, int off);
	
	/**
	 * Fetches a single sample from the source, processes it and returns its value. 
	 * <p>
	 * If the sample contains more than one element (i.e. triaxis accelerometer), only 
	 * the first element will be returned by this method.

	 * @return The first element of the sample
	 */
	float fetchSample();
}
