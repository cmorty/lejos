package lejos.pc.comm;

import java.io.*;
import java.util.Vector;

/**
 * Implementation of NXTComm using the the LEGO Fantom API.
 * 
 * Currently only supports USB access. The Fantom read function when using
 * Bluetooth seems to be broken, it does not work if the amount of data
 * available to be read does not match the amount of data requested. So for now
 * we only support USB.
 * 
 * 
 * Notes
 * The Fantom read and write functions have a built in timeout period of
 * 20 seconds. This module assumes that this timeout exists and uses it to
 * timeout some requests.
 * 
 * Should not be used directly - use NXTCommFactory to create
 * an appropriate NXTComm object for your system and the protocol
 * you are using.
 *
 */
public class NXTCommFantom extends NXTCommUSB {
    private static final int MIN_TIMEOUT = 60000;
    private static final int MAX_ERRORS = 10;
	
	public native String[] jfantom_find();
	public native long jfantom_open(String nxt);
	public native void jfantom_close(long nxt);
	public native int jfantom_send_data(long nxt, byte [] message, int offset, int len);
	public native int jfantom_read_data(long nxt, byte[] data, int offset, int len);

	Vector<NXTInfo> devFind()
    {
		String[] nxtNames = jfantom_find();
        if (nxtNames == null) return new Vector<NXTInfo>();
		Vector<NXTInfo> nxtInfos = new Vector<NXTInfo>();
        for(int idx = 0; idx < nxtNames.length; idx++)
        {
            String addr = nxtNames[idx];
            NXTInfo info = new NXTInfo();
            // Use the default way to obtain the name
            info.name = null;
            info.btResourceString = addr;
            info.protocol = NXTCommFactory.USB;
            nxtInfos.addElement(info);
            // Extract the serial number from the device string
            int endSerial = addr.lastIndexOf("::");
            if (endSerial > 0)
            {
                int startSerial = addr.lastIndexOf("::", endSerial-2);
                if (startSerial >= 0)
                    info.btDeviceAddress = addr.substring(startSerial+2, endSerial);
            }
        }
        return nxtInfos;
    }
    
	long devOpen(NXTInfo nxt)
    {
        if (nxt.btResourceString == null) return 0;
        return jfantom_open(nxt.btResourceString);
    }
    
	void devClose(long nxt)
    {
        jfantom_close(nxt);
    }
    
	int devWrite(long nxt, byte [] message, int offset, int len)
    {
        return jfantom_send_data(nxt, message, offset, len);
    }
    
	int devRead(long nxt, byte[] data, int offset, int len)
    {
        int ret;
        // The Fantom lib does not seem to detect when the USB cable is 
        // disconnected very well. It does not give an error it just times out
        // very quickly. We treat fast timeouts as a potential disconnect.
        long startTime = System.currentTimeMillis();
        int errorCnt = 0;
        while((ret=jfantom_read_data(nxt, data, offset, len)) == 0)
        {
            long now = System.currentTimeMillis();
            if (now - startTime > MIN_TIMEOUT) return ret;
            if (errorCnt++ > MAX_ERRORS) return -1;
            startTime = now;
        }
        return ret;
    }
    
    
    boolean devIsValid(NXTInfo nxt)
    {
        return (nxt.btResourceString != null);
    }
	
	static {
		System.loadLibrary("jfantom");
	}

}

