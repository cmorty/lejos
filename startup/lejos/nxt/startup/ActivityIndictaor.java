/**
 * 
 */
package lejos.nxt.startup;

interface ActivityIndictaor
{
	/**
	 * There is activity currently. Pulses time out automatically. 
	 */
	void pulse();
	
	/**
	 * A new activity has started. The end will be signaled using {@link #decCount()}.
	 */
	void incCount();
	
	/**
	 * One of the activities has ended.
	 */
	void decCount();
}