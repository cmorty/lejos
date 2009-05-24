package lejos.nxt.comm;

import lejos.nxt.*;
import java.io.*;


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
public class USBConnection extends NXTConnection 
{
    static final int HDRSZ = 1;
    private static final int CLOSETIMEOUT2 = 100;
    
    
 	public USBConnection(int mode)
	{
		state = CS_CONNECTED;
        bufSz = USB.BUFSZ;
        inBuf = new byte[USB.BUFSZ];
        outBuf = new byte[USB.BUFSZ];
        is = null;
        os = null;
        setIOMode(mode);
    }


    /**
     * Write all of the current output buffer to the device.
     * NOTE: To ensure correct operation of packet mode, this function should
     * only return 1 if all of the data will eventually be written. It should
     * avoid writing part of the data. 
     * @param wait if true wait until the output has been written
     * @return -ve if error 0 if not written +ve if written
     */
    int flushBuffer(boolean wait)
    {
        // assert(outCnt <= USB.BUFSZ)
        if (outCnt <= 0) return 1;
        int len;
        //LCD.drawString("                ", 8, 2);
        //LCD.drawInt(outCnt, 4, 0, 2);
        while ((len = USB.usbWrite(outBuf, 0, outCnt)) == 0 && wait && state >= CS_CONNECTED)
            //Thread.yield();
            try{wait(1);}catch(Exception e){}
        if (len <= 0) return len;
        //LCD.drawInt(len, 8, 2);
        // assert (len == outCnt || len == 0)
        outCnt = 0;
        return len;
    }

    /**
     * Get any available data into the input buffer.
     * @param wait if true wait for data to be available.
     * @return -ve if error, 0 if not read, +ve if read
     */
    int fillBuffer(boolean wait)
    {
        if (inCnt > 0) return inCnt;
        int cnt;
        while ((cnt = USB.usbRead(inBuf, 0, inBuf.length)) == 0 && wait && state >= CS_CONNECTED)
            //Thread.yield();
            try{wait(1);}catch(Exception e){}

        //if (cnt != 0) LCD.drawInt(cnt, 4, 8, 1);
        inOffset = 0;
        if (cnt > 0) inCnt = cnt;
        return cnt;
    }
         
    /**
     * Close the USB stream connection. 
     */
	void disconnect()
    { 
        USB.waitForDisconnect(this, CLOSETIMEOUT2);
        super.disconnect();
	}

   /**
     * Tell the lower levels that they can release any resources for this
     * connection.
     */
    void freeConnection()
    {
        USB.usbDisable();
    }


    /**
     * Set the IO mode to be used for this connection. 
     * USB has a 1 byte header, and does not use packet mode for LCP data.
     * @param mode
     */
    public void setIOMode(int mode)
    {
        // Only packet modes uses a header for USB
        if (mode == PACKET)
            setHeader(HDRSZ);
        else
            setHeader(0);
    }

}

