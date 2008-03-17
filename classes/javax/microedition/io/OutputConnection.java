package javax.microedition.io;

import java.io.*;

public interface OutputConnection extends Connection {
	/**
	 * Open and return a data output stream for a connection.
	 * @return
	 */ 
	public DataOutputStream openDataOutputStream();
     
	/**
	 * Open and return an output stream for a connection.
	 * @return
	 */
	OutputStream openOutputStream();
     
}
