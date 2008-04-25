package lejos.nxt.comm;

import java.io.*;
import javax.microedition.io.*;

/**
 * 
 * Represents a USB Stream Connection.
 *
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
        if ((mode & USB.RAW) == 0 && state == CS_CONNECTED)
            USB.usbWrite(new byte[0], 0, 0);
        USB.waitForDisconnect(USBC_CLOSETIMEOUT1);
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
          int ret = USB.usbRead(data, 0, len);
          if (ret == -2)
          {
              // eof indicator
              if ((mode & USB.RAW) == 0)
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
            int cnt = USB.usbWrite(data, written, len - written);
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
     * - RAW is supported. Using this mode indicates that the connection
     * will not use a zero length packet to indicate eof.
     * @param mode
     */
    public void setIOMode(int mode)
    {
        this.mode = mode;
    }

}

