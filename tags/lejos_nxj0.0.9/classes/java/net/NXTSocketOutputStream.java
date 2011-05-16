package java.net;

import java.io.*;
import lejos.nxt.comm.*;

/**
 * A version of NXTOutputStream that implements simple escape sequences
 * so that it can indicate when a socket has been closed.
 *
 */
class NXTSocketOutputStream extends OutputStream {
	private byte[] buffer;
	private int numBytes = 0;
	private NXTConnection conn = null;
	
	private static final byte ESCAPE = (byte) 0xFF;
	private static final byte ESCAPE_ESCAPE = 0;
	private static final byte ESCAPE_CLOSE = 1;
	
	NXTSocketOutputStream(NXTConnection conn, int buffSize)
	{
		this.conn = conn;
        buffer = new byte[buffSize];
	}
	
    public void write(int b) throws IOException {
    	if (numBytes == buffer.length) {
    		flush();
    	}
    	buffer[numBytes++] = (byte) b;
    	if (b == ESCAPE) write(ESCAPE_ESCAPE);
    }
   
   /**
    * Sends a close escape sequence to the Socket Proxy
    * 
    * @throws IOException
    */
   public void writeClose() throws IOException {
    	if (numBytes > buffer.length - 2) {
    		flush();
    	}
    	buffer[numBytes++] = ESCAPE;
    	buffer[numBytes++] = ESCAPE_CLOSE;
    	flush();
    }
    
	public void flush() throws IOException{
		if (numBytes > 0) {
			if (conn.write(buffer, numBytes) < 0) throw new IOException();
			numBytes = 0;
		}
	}
}
