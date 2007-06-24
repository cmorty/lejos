package lejos.nxt.comm;


import java.io.*;
import lejos.nxt.*;

/**
 * extends InputStream for BlueTooth; implements available()
 * @author   Roger Glassey revised on june 23, 2007
 */
public class BTInputStream extends InputStream {
	private byte buf[] = new byte[256];
	private int bufIdx = 0, bufSize = 0;
	private BTConnection connection = null;
    
/**
 * returns one byte as an integer between 0 and 255.  Returns -1 if the end of the stream is reached.
 * Does not return till some bytes are available.
 */
	public int read() 
    {
       while(bufSize == 0) bufSize = available();
       if (bufIdx < bufSize) return buf[bufIdx++] & 0xFF;
       else 
          {
             bufIdx = 0;
             bufSize = 0;
             return -1;
          }
	}
   /**
    * returns the number of bytes in the input buffer - can be read without blocking
    */
    public int available()
    {
       bufSize = Bluetooth.readPacket(buf, 256);
       return bufSize;
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
