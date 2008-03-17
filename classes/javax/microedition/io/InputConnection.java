package javax.microedition.io;

import java.io.*;

public interface InputConnection extends Connection {
	/**
	 * Open and return a data input stream for a connection.
	 * @return
	 */ 
	public DataInputStream openDataInputStream();
    
	/**
	 * Open and return an input stream for a connection.
	 * @return
	 */
	public InputStream openInputStream();
     
}
