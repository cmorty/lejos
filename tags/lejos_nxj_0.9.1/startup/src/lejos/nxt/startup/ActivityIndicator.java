/**
 * Defines methods for indicating activity.
 */
package lejos.nxt.startup;

public interface ActivityIndicator
{
	/**
	 * Signal activity. Pulses timeout automatically.
	 */
	void pulse();
	
	/**
	 * Signal activity.
	 * This Activity has to be terminated by calling {@link #decCount()} exactly once.
	 * @see #decCount()
	 */
	void incCount();
	
	/**
	 * Signal the end of activity.
	 * The start of the activity must have been signaled by {@link #incCount()}.
	 * @see #incCount()
	 */
	void decCount();
}
