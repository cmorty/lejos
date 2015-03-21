package java.net;

import lejos.nxt.comm.NXTConnection;

/**
 * NXT specific methods for Sockets
 * 
 * @author Lawrie Griffiths
 *
 */
public class NXTSocketUtils {
	
	private static NXTConnection conn = null;

	public static NXTConnection getNXTConnection() {
		return conn;
	}
	
	public static void setNXTConnection(NXTConnection conn) {
		NXTSocketUtils.conn = conn;
	}
}
