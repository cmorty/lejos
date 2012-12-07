package lejos.nxt.sensor.api;

/**
 * Used for receiving notifications from the <code>ChangeMonitor</code> when sensor/provider 
 * values have changed.
 *  
 * @author Kirk P. Thompson
 *
 */
public interface SampleEventListener {
	/**
	 * Called when at least one of the provider values has changed.
	 * 
	 * @param values The array of elements/channels.
	 * @see lejos.nxt.sensor.filter.ChangeMonitor#fetchSample(float[], int)
	 */
	void onValueChanged(float[] values);
}
