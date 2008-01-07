package lejos.nxt.comm;

import java.io.*;

/**
 * Implements an OutputStream over Bluetooth.
 *
 */public class BTOutputStream extends OutputStream {
	private final int BUFFER_SIZE = 32;	
	private byte[] buffer = new byte[BUFFER_SIZE];
	private int numBytes = 0;
	private BTConnection conn = null;
	
	BTOutputStream(BTConnection conn)
	{
		this.conn = conn;
	}
	
    public void write(int b) {
    	if (numBytes == BUFFER_SIZE) {
    		flush();
    	}
    	buffer[numBytes] = (byte) b;
    	numBytes++;  	
    }
    
	public void flush() {
		if (numBytes > 0) {
			conn.write(buffer, numBytes, true);
			numBytes = 0;
		}
	}
}
