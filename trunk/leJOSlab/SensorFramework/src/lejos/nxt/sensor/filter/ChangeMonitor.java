package lejos.nxt.sensor.filter;

import java.util.ArrayList;

import lejos.nxt.sensor.api.SampleEventListener;
import lejos.nxt.sensor.api.SampleProvider;

/**
 * A change event notifier filter. Place in the filter chain and register 
 * <code>SampleEventListener</code> objects that will be notified when any  
 * channel value deltas (from previous pull) are detected during a call to the <code>fetchSample()</code> method.
 * <p>
 * The change detection occurs in the <code>{@link #fetchSample(float[], int)}</code> method so place the 
 * <code>ChangeMonitor</code> instance 
 * in between the data provider/filter and a sink (like <code>AutoSampler</code>).
 * 
 * @author Kirk P. Thompson
 * @see SampleEventListener
 */
public final class ChangeMonitor extends AbstractFilter {
	private float[] buffer;
	private ArrayList<SampleEventListener> listeners = new ArrayList<SampleEventListener>();
	
	public ChangeMonitor(SampleProvider source) {
		super(source); // will set elements to array size from source via getElementsCount()
		this.buffer=new float[elements];
	}

	/**
	 * Ensure your <code>SampleEventListener</code>s execute and return promptly as they will
	 * block this method from returning. 
	 * <p>
	 * If no changes in any of the values from previous pull or no <code>SampleEventListener</code>s are
	 * registered, no event notification
	 * callbacks are executed and this method immediately returns the fetched values in the passed
	 * <code>dst[]</code> reference.
	 * 
	 */
	
	public void fetchSample(float[] dst, int off) {
		this.source.fetchSample(dst, off);
		int i=0;
		// test for any delta
		for (;i<buffer.length;i++){
			if (Float.compare(buffer[i], dst[i+off]) != 0) break; 
		}
		
		// save new data and notify if changed
		if (i<buffer.length) {
			// save for next comparison
			System.arraycopy(dst, off, buffer, 0, elements);
			
			if (listeners.size()==0) return;
			for(SampleEventListener listener : listeners) {
				listener.onValueChanged(buffer);
			}
		}
	}
	
	/**
	 * Register a listener to be notified of value changes.
	 * @param listener A <code>SampleEventListener</code> object.
	 */
	public void registerListener(SampleEventListener listener){
		if (listeners.contains(listener)) return; //ensure no duplicate listeners
		listeners.add(listener);
	}
	
	/**
	 * Unregister a listener.
	 * 
	 * @param listener The <code>SampleEventListener</code> object to unregister.
	 */
	public void unregisterListener(SampleEventListener listener){
		listeners.remove(listener);
	}
}
