package lejos.pc.comm;

import java.io.IOException;
import lejos.nxt.remote.*;

public class NXTCommandConnector extends NXTCommLoggable {
	private static NXTConnector conn = new NXTConnector();
	
	/**
	 * Open any available NXT.
	 * 
	 * @return true if connected
	 */
	public static NXTComm open() throws IOException {
		boolean connected = conn.connectTo(NXTComm.LCP);

		if  (connected) {
			return conn.getNXTComm();
		} else {
			return null;
		}
	}
	
	/**
	 * Get the singleton NXTCommand object. Use of this is optional.
	 * 
	 * @return the singleton NXTCommand instance
	 */
	public static NXTCommand getSingletonOpen() {
		NXTCommand singleton = NXTCommand.getSingleton();
		if (!singleton.isOpen()) {
			try {
				NXTComm nxtComm = open();
				if (nxtComm == null) throw new IOException();
				singleton.setNXTComm(nxtComm);
			} catch (IOException ioe) {
				System.err.println("Failed to open connection to the NXT");
				System.exit(1);
			}
		}
		return singleton;
	}
	/**
	 * Open a connection to the NXT 
	 * 
	 * @param nxt the NXTInfo object returned by search or constructed 
	 * @return nxtComm object or null
	 * @throws NXTCommException if a comms driver could not be loaded
	 */
	public NXTComm open(NXTInfo nxt) throws NXTCommException {
	    boolean connected = conn.connectTo(nxt,NXTComm.LCP);
	    if (connected) return conn.getNXTComm();
	    else return null;
	}
	
	/**
	 * register log listener
	 * 
	 * @param listener
	 */
	public void addLogListener(NXTCommLogListener listener) {
		fLogListeners.add(listener);
		conn.addLogListener(listener);
	}
	
	/**
	 * unregister log listener
	 * 
	 * @param listener
	 */
	public void removeLogListener(NXTCommLogListener listener) {
		fLogListeners.remove(listener);
		conn.removeLogListener(listener);
	}
}
