package lejos.nxt.sensor.api;


/**
 * Classes that support this interface are able to fetch a sample (data originating from a sensor), to process it and to pass it on.
 */
public interface SampleProvider {
	
	/**
	 * Returns the quantity of a sample
	 * @return
	 */
	int getQuantity();
	
	/**
	 * Returns the number of values that are in a sample.
	 * @return
	 */
	int getElementsCount();
	
	/**
	 * Fetches a sample from the source and processes it
	 * @param dst
	 * array to store the processed values from a sample
	 * @param off
	 * Offset to use;
	 * 
	 */
	void fetchSample(float[] dst, int off);
	
	/**
	 * Fetches a sample from the source, processes it and returns its value. <p>
	 * If the sample contains more than one element (i.e. triaxis accelrometer) then the first element will be returned by this method.
	 */
	float fetchSample();
}
