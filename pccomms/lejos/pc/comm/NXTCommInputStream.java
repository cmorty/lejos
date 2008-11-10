package lejos.pc.comm;

import java.io.*;

/**
 * Implementation of InputStream over NXTComm using Bluetooth.
 */
public class NXTCommInputStream extends InputStream {
	private NXTComm nxtComm;
	private byte buf[];
	private int bufIdx, bufSize;
	boolean endOfFile;
	
	public NXTCommInputStream(NXTComm nxtComm) {
		this.nxtComm = nxtComm;
		endOfFile = false;
		bufIdx = 0;
		bufSize = 0;
	}
	
    /**
     * Returns one byte as an integer between 0 and 255.  
     * Returns -1 if the end of the stream is reached.
     * Does not return till some bytes are available.
     */
	public int read() throws IOException
    {
	   if (endOfFile) return -1;
	   if (bufIdx >= bufSize) bufSize = 0;
       if (bufSize == 0) {
    	   bufIdx = 0;
    	   buf = nxtComm.read();
    	   if (buf == null || buf.length ==0) {
    		   endOfFile = true;
    		   return -1;
    	   }
    	   bufSize = buf.length;
       }
       return buf[bufIdx++] & 0xFF;
	}
	
    /**
     * returns the number of bytes in the input buffer - can be read without blocking
     */
    public int available() throws IOException
    {
       return bufSize - bufIdx;
    }
    
    /**
     * Close the stream
     */
    public void close() throws IOException
    { 
        endOfFile = true;
    }
}
