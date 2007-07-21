package lejos.pc.comm;

import java.io.*;

public class NXTCommBTInputStream extends InputStream {
	private NXTComm nxtComm;
	private byte buf[];
	private int bufIdx = 0, bufSize = 0;
	
	public NXTCommBTInputStream(NXTComm nxtComm) {
		this.nxtComm = nxtComm;
	}
	
    /**
     * Returns one byte as an integer between 0 and 255.  
     * Returns -1 if the end of the stream is reached.
     * Does not return till some bytes are available.
     */
	public int read() throws IOException
    {
	   if (bufIdx >= bufSize) bufSize = 0;
       while(bufSize == 0) bufSize = available();
	
       return buf[bufIdx++] & 0xFF;
	}
	
    /**
     * returns the number of bytes in the input buffer - can be read without blocking
     */
    public int available() throws IOException
    {
       if (bufIdx >= bufSize) bufSize = 0;
       if (bufSize == 0) {
    	   bufIdx = 0;
    	   buf = nxtComm.read();
    	   bufSize = buf.length;
       }
       return bufSize - bufIdx;
    }
    
    /**
     * Close the stream
     */
    public void close() throws IOException
    { 
       nxtComm.close();
    }
}
