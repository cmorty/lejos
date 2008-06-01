package lejos.nxt.comm;

import java.io.*;
import javax.microedition.io.*;

/**
 * 
 * Provides a USB connection
 * Supports both packetized, raw and stream based communincation.
 * Blocking and non-blocking I/O.
 *
 * Notes
 * When using the low level read/write functions no buffering is provided. This
 * means that for read operations te entire packet must be read or data will
 * be lost. A USB packet has a max size of 64 bytes. The Stream based functions
 * take care of this automatically.
 * 
 * When operating in RAW mode low level USB packets may be read and written.
 * however this mode has no concept of EOF or start of a connection.
 * When using PACKET mode each packet has a single byte header added to it. This
 * is used to provide a simple start of connection/EOF model.
 */
public class USBConnection implements NXTConnection {
  
    private static final byte CS_IDLE = 0;
    private static final byte CS_DISCONNECTED = 1;
    private static final byte CS_CONNECTED = 2;
    private static final int USBC_CLOSETIMEOUT1 = 1000;
    private static final int USBC_CLOSETIMEOUT2 = 100;
	byte state;
	NXTInputStream is; 
	NXTOutputStream os;
    int mode;
    byte [] ioBuf = new byte[USB.USB_BUFSZ];
    
    /**
     * Helper function. Write a single low level USB packet. Add header
     * byte if in stream mode.
     * @param data
     * @param len
     * @return length written.
     */
    private synchronized int writePacket(byte [] data, int offset, int len)
    {
        if (mode != PACKET)
            return USB.usbWrite(data, offset, len);
        else
        {
            // Need to add header byte
            if (len >= USB.USB_BUFSZ) len = USB.USB_BUFSZ - 1; 
            ioBuf[0] = (byte)len;
            if (len > 0) System.arraycopy(data, offset, ioBuf, 1, len);
            int written = USB.usbWrite(ioBuf, 0, len + 1);
            if (written <= 0) return written;
            if (written != len + 1) return -1;
            return len;
        }
    }
	
    private synchronized int readPacket(byte[] data, int offset, int len)
    {
        if (mode != PACKET)
            return USB.usbRead(data, 0, len);
        else
        {
            // Need to process the header
            int ret = USB.usbRead(ioBuf, 0, len);
            if (ret <= 0) return ret;
            int plen = (int)ioBuf[0] & 0xff;
            if (plen != ret - 1) return -1;
            if (plen == 0) return -2;
            if (len < plen) plen = len;
            if (plen > 0) 
                System.arraycopy(ioBuf, 1, data, offset, plen);
            return plen;
        }
    }
    
    private void waitEOF(int timeout)
    {
        while(timeout-- > 0)
        {
            if (readPacket(null, 0, 0) == -2) break;
            try{Thread.sleep(1);}catch(Exception e){}          
        }
    }
    
         
	public USBConnection(int mode)
	{
		state = CS_CONNECTED;
        is = null;
        os = null;
        this.mode = mode;
	}

    /**
     * Close the USB stream connection. 
     */
	public void close() { 
        // Write eof marker if required
        if (state == CS_IDLE) return;
        if (mode == PACKET)
        {
            writePacket(null, 0, 0);
            if (state == CS_CONNECTED)
                waitEOF(USBC_CLOSETIMEOUT1);
        }
        USB.waitForDisconnect(USBC_CLOSETIMEOUT2);
        state = CS_IDLE;
	}

	/**
	 * Return the InputStream for this connection.
	 * 
	 * @return the input stream
	 */
	public InputStream openInputStream() {
		return (is != null ? is : new NXTInputStream(this, USB.USB_BUFSZ));
	}

	/**
	 * Return the OutputStream for this connection
	 * 
	 * @return the output stream
	 */
	public OutputStream openOutputStream() {
		return (os != null ? os : new NXTOutputStream(this, USB.USB_BUFSZ));
	}

	/**
	 * Return the DataInputStream for this connect
	 * 
	 * @return the data input stream
	 */
	public DataInputStream openDataInputStream() {
		return new DataInputStream(openInputStream());
	}

	/**
	 * Return the DataOutputStream for this connection.
	 * 
	 * @return the data output stream
	 */
	public DataOutputStream openDataOutputStream() {
		return new DataOutputStream(openOutputStream());
	}
 
    /**
     * Perform an optionally blocking read on the USB connection
     * @param data byte array to store the results.
     * @param len max number of bytes to read
     * @param wait set true to block waiting for data
     * @return actual number of bytes read, return -2 for eof
     */
    public int read(byte [] data, int len, boolean wait)
    {
        if (state != CS_CONNECTED) return -1;
        for(;;)
        {
          int ret = readPacket(data, 0, len);
          if (ret == -2)
          {
              // eof indicator
              if (mode == PACKET)
                  state = CS_DISCONNECTED;
              else
                  ret = 0;
          }
          if (ret != 0 || !wait) return ret;
          Thread.yield();
        }
    }

    /**
     * Perform an blocking read on the USB connection
     * @param data byte array to store the results.
     * @param len max number of bytes to read
     * @return actual number of bytes read, return -2 for eof
     */

	public int read(byte [] data, int len)
	{
		return read(data, len, true);
	}

    /**
     * Write the requested bytes to the USB connection
     * @param data data to be written
     * @param len number of bytes to write
     * @param wait true to block to write the data.
     * @return number of bytes written or < 0 if error
     */
    public int write(byte [] data, int len, boolean wait)
    {
        if (state != CS_CONNECTED) return -1;
        if (len == 0) return 0;
        int written = 0;
        while (written < len)
        {
            int cnt = writePacket(data, written, len - written);
            if (cnt < 0) return (written > 0 ? written : -1);
            if (cnt == 0)
            {
                if (!wait) break;
                Thread.yield();
            }
            written += cnt;
        }
        return written;
    }

   	public int write(byte [] data, int len)
	{
		return write(data, len, true);
	}
    
    /**
     * Set the IO mode to be used for this connection. Currently only one mode
     * @param mode
     */
    public void setIOMode(int mode)
    {
        this.mode = mode;
    }

}

