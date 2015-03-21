package lejos.util;

import java.io.DataInputStream;

/**
 * Abstraction for protocol passthrough message extension listener. The implementor is responsible
 * for all <code>DataInputStream</code> management (polling, parsing, passing to 
 * <code>LogMessageTypeHandler</code> implementations, etc.) except closing. 
 * The <code>NXTDataLogger</code> assumes
 * all responsiblity for closing the <code>DataInputStream</code>. 
 * <p>
 * 
 * <code>lejos.pc.charting.LoggerProtocolManager</code> is the PC-side coordinator
 * 
 * @author Kirk P. Thompson
 *
 */
interface LogMessageListener {

	/**
	 * Called by <code>NXTDataLogger</code> on registration via <code>registerTunnelManager</code> 
	 * and when <code>startRealtimeLog()</code> is called
	 * to set a reference to the <code>DataInputStream</code> so the implementing class
	 * can manage the receiving of data from NXT ChartingLOgger plugins sent through logger protocol 
	 * passthrough messages.
	 * <p>
	 * The the implementing class is responsible for all <code>DataInputStream</code> input management. On 
	 * <code>NXTDataLogger.registerTunnelManager()</code>, this method is called to set the <code>DataInputStream</code> reference 
	 * that <code>NXTDataLogger</code> holds.
	 * 
	 * @see NXTDataLogger#registerTunnelManager
	 * @see NXTDataLogger#startRealtimeLog(lejos.nxt.comm.NXTConnection)
	 * @param dis The <code>DataInputStream</code> from <code>NXTDataLogger</code>
	 */
	public void setInputStream(DataInputStream dis);
	
	/**
	 * Called by <code>NXTDataLogger</code> right before the data connection is closed. This allows the implementor
	 * to have a chance to do housekeeping, notify <code>LogMessageTypeHandler</code> implementations,
	 * etc. before the stream is closed.
	 */
	public void connectionClosing();
    
}
