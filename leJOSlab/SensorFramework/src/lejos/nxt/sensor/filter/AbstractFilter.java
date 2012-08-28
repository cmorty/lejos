package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;

/**
 * Common base for SampleProvider implementations
 * 
 * @author Kirk P. Thompson
 *
 */
public abstract class AbstractFilter implements SampleProvider{
	protected final SampleProvider source;
	protected int elements;
	private float[] buf;

	/**
	 * Create a filter passing a source to be decorated
	 * @param source The source sensor/filter to be used
	 */
	public AbstractFilter(SampleProvider source){
		this.source = source;
		elements=source.getElementsCount();
	}
	
	public int getQuantity() {
		return source.getQuantity();
	}

	public  int getElementsCount() {
		return elements;
	}

	public float fetchSample() {
		if (buf==null)
			buf=new float[elements];
		fetchSample(buf,0);
		return buf[0];
	}
	
	/**
	 * Utility method to format floats to 4 characters, used for testing
	 * @param in
	 * @return
	 * Formatted float value
	 */
	protected String fmt(float in) {
		//TODO: remove method 
		String tmp=Float.toString(in)+"00000";
		return tmp.substring(0, 4);
	}
	
}
