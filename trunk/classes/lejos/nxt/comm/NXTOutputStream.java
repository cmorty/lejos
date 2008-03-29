package lejos.nxt.comm;

import java.io.*;

/**
 * Implements an OutputStream over Bluetooth.
 *
 */public class NXTOutputStream extends OutputStream {
	private byte[] buffer;
	private int numBytes = 0;
	private NXTConnection conn = null;
	
	NXTOutputStream(NXTConnection conn, int buffSize)
	{
		this.conn = conn;
        buffer = new byte[buffSize];
	}
	
    public void write(int b) {
    	if (numBytes == buffer.length) {
    		flush();
    	}
    	buffer[numBytes] = (byte) b;
    	numBytes++;  	
    }
    
	public void flush() {
		if (numBytes > 0) {
			conn.write(buffer, numBytes);
			numBytes = 0;
		}
	}
}
