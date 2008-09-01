package lejos.nxt.comm;

import java.io.*;

/**
 * Implements an OutputStream over Bluetooth.
 *
 */
public class NXTOutputStream extends OutputStream {
	private byte[] buffer;
	private int numBytes = 0;
	private NXTConnection conn = null;
	
	NXTOutputStream(NXTConnection conn, int buffSize)
	{
		this.conn = conn;
        buffer = new byte[buffSize];
	}
	
    public void write(int b) throws IOException {
    	if (numBytes == buffer.length) {
    		flush();
    	}
    	buffer[numBytes] = (byte) b;
    	numBytes++;  	
    }
    
	public void flush() throws IOException{
		if (numBytes > 0) {
			if (conn.write(buffer, numBytes) < 0) throw new IOException();
			numBytes = 0;
		}
	}
}
