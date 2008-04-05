package lejos.nxt.comm;
import lejos.nxt.*;

/**
 * Low-level USB access.
 * 
 * @author Lawrie Griffiths, extended to support streams by Andy Shaw
 *
 */
public class USB {
    public static final int RESET = 0x40000000;
    public static final int RAW = 0x20000000;
    static final int USB_BUFSZ = 64;
    static final int USB_STREAM = 1;

	private USB()
	{		
	}
    
    private static void flushInput()
    {
        // Discard any input that may have been left by a previous user of the
        // USB connection.
        byte [] buf = new byte [USB_BUFSZ];
        while (usbRead(buf, 0, buf.length) > 0)
            ;
    }
	/**
     * Wait for the USB interface to become available and for a PC side program
     * to attach to it.
     * @param timeout length of time to wait (in ms), if 0 wait for ever
     * @param mode The IO mode to be used for the connection. (see USBConnection)
     * @return a connection object or null if no connection.
     */
    public static USBConnection waitForConnection(int timeout, int mode)
    {
        usbEnable(((mode & RESET) != 0 ? 1 : 0));
        mode &= ~RESET;
        // Discard any left over input
        flushInput();
        if (timeout == 0) timeout = 0x7fffffff;
        int features = 0;
        if ((mode & RAW) == 0) features = USB_STREAM;
        while(timeout-- > 0)
        {
            int status = usbStatus();
            // Check for the inetrface to be ready, to be in a non control
            // configuration and for it to have the required features
            if ((status & 0xf0000000) == 0x10000000 && (status & 0x0f000000) != 0 &&
                 (status & features) == features )
                return new USBConnection(mode);
            if (timeout == 0 || timeout-- > 0)
            try{Thread.sleep(1);}catch(Exception e){}          
        }
        usbDisable();
        return null;
    }
    
    /**
     * Wait for ever for the USB connection to become available.
     * @return a connection object or null if no connection.
     */
    public static USBConnection waitForConnection()
    {
        return waitForConnection(0, 0);
    }
    
    /**
     * Wait for the remote side of the connection to close down.
     * @param timeout
     */
    public static void waitForDisconnect(int timeout)
    {
        flushInput();
        while(timeout-- > 0)
        {
            int status = usbStatus();
            // Wait for the interface to be down or for the remote end to signal
            // a disconnect.
            if ((status & 0xf0000000) != 0x10000000 || (status & 0x0f000000) == 0
                    || (status & 0xffff) == 0)
                break;
            try{Thread.sleep(1);}catch(Exception e){}          
        }
        usbDisable();
    }
    
	public static native void usbEnable(int reset);
	public static native void usbDisable();
	public static native void usbReset();
	public static native int usbRead(byte [] buf, int off, int len);
	public static native int usbWrite(byte [] buf, int off, int len);
    public static native int usbStatus();
    
}
