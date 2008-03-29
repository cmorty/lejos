package lejos.nxt.comm;

import java.io.*;

/**
 * Extends InputStream for BlueTooth; implements available()
 * @author   Roger Glassey revised on june 23, 2007, modified for Bluetooth2
 */
public class NXTInputStream extends InputStream {
	private byte buf[];
	private int bufIdx = 0, bufSize = 0;
	private NXTConnection conn = null;
    
	NXTInputStream(NXTConnection conn, int buffSize)
	{
		this.conn = conn;
        buf = new byte[buffSize];
	}
    /**
     * Returns one byte as an integer between 0 and 255.  
     * Returns -1 if the end of the stream is reached.
     * Does not return till some bytes are available.
     */
	public int read() 
    {
	   if (bufIdx >= bufSize) bufSize = 0;
	   if (bufSize <= 0)
	   {
		   bufSize = conn.read(buf, buf.length, false);
		   if (bufSize <= 0) return -1;
		   bufIdx = 0;
	   }
       return buf[bufIdx++] & 0xFF;
	}
	
    /**
     * returns the number of bytes in the input buffer - can be read without blocking
     */
    public int available()
    {
       if (bufIdx >= bufSize) bufSize = 0;
       if (bufSize == 0) {
    	   bufIdx = 0;
    	   bufSize = conn.read(buf, buf.length);
       }
       return bufSize - bufIdx;
    }
    
    /**
     * the stream is restored to its original state - ready to receive more data.
     */
    public void close()
    { 
       bufIdx = 0;
       bufSize = 0;
    }
}
