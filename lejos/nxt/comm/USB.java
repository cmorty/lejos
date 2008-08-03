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
    public static final String SERIAL_NO = "lejos.usb_serno";
    public static final String NAME = "lejos.usb_name";
    static final int USB_BUFSZ = 64;
    static final int USB_STREAM = 1;
    static final int USB_STATE_MASK = 0xf0000000;
    static final int USB_STATE_CONNECTED = 0x10000000;
    static final int USB_CONFIG_MASK = 0xf000000;
    static final int USB_WRITABLE = 0x100000;
    static final int USB_READABLE = 0x200000;

    // Private versions of LCP values. We don't want to pull in all of the
    // LCP code.
    private static final byte SYSTEM_COMMAND_REPLY = 0x01;
	private static final byte REPLY_COMMAND = 0x02;
	private static final byte GET_FIRMWARE_VERSION = (byte)0x88;
	private static final byte GET_DEVICE_INFO = (byte)0x9B;
    private static final byte NXJ_PACKET_MODE = (byte)0xFF;

    private static String serialNo = "123";
    private static String name = "xxx";
    
    /**
     * Static contstructor to force loading of system settings
     */
    static {
        loadSettings();
    }

	private USB()
	{		
	}
    
    private static void flushInput(byte [] buf)
    {
        // Discard any input that may have been left by a previous user of the
        // USB connection.
        while (usbRead(buf, 0, buf.length) > 0)
            ;
    }
    
    private static boolean isConnected(byte [] cmd)
    {
        // This method provides support for packet mode connections.
        // We wait for the PC to tell us that the connection has been established.
        // While waiting we support a small sub-set of LCP to allow identification
        // of the device.
        int len = 3;
        boolean ret = false;
        // Look for a system command
        if (usbRead(cmd, 0, cmd.length) >= 2 && cmd[0] == SYSTEM_COMMAND_REPLY)
        {
            cmd[2] = (byte)0xff;
            if (cmd[1] == GET_FIRMWARE_VERSION) 
            {
                cmd[2] = 0;
                cmd[3] = 2;
                cmd[4] = 1;
                cmd[5] = 3;
                cmd[6] = 1;			
                len = 7;
            }
		
            // GET DEVICE INFO
            if (cmd[1] == GET_DEVICE_INFO) 
            {
                cmd[2] = 0;
                // We only send back the device name.
                for(int i=0;i<name.length();i++) cmd[3+i] = (byte)name.charAt(i);
                len = 33;
            }	
             // Switch to packet mode
            if (cmd[1] == NXJ_PACKET_MODE)
            {
                // Send back special signature to indicate we have accepted packet
                // mode
                cmd[1] = (byte)0xfe;
                cmd[2] = (byte)0xef;
                ret = true;
                len = 3;
            }
            cmd[0] = REPLY_COMMAND;
            usbWrite(cmd, 0, len);
        }
        return ret;
    }
    
    
	/**
     * Wait for the USB interface to become available and for a PC side program
     * to attach to it.
     * @param timeout length of time to wait (in ms), if 0 wait for ever
     * @param mode The IO mode to be used for the connection. (see NXTConnection)
     * @return a connection object or null if no connection.
     */
    public static USBConnection waitForConnection(int timeout, int mode)
    {
        // Allocate buffer here for use by other methods. Saves repeated
        // allocations.
        byte [] buf = new byte [USB_BUFSZ];
        usbEnable(((mode & RESET) != 0 ? 1 : 0));
        mode &= ~RESET;
        // Discard any left over input
        flushInput(buf);
        if (timeout == 0) timeout = 0x7fffffff;
        while(timeout-- > 0)
        {
            int status = usbStatus();
            // Check for the interface to be ready and to be in a non control
            // configuration.
            if ((status & USB_STATE_MASK) == USB_STATE_CONNECTED && (status & USB_CONFIG_MASK) != 0)
            {
                if (mode == NXTConnection.RAW ||
                    (mode == NXTConnection.LCP && ((status & (USB_READABLE|USB_WRITABLE)) == (USB_READABLE|USB_WRITABLE))) ||
                    (mode == NXTConnection.PACKET && isConnected(buf)))
                    return new USBConnection(mode);
            }
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
        // Allocate buffer here for use by other methods. Saves repeated
        // allocations.
        byte [] buf = new byte [USB_BUFSZ];

        while(timeout-- > 0)
        {
            flushInput(buf);
            int status = usbStatus();
            // Wait for the interface to be down
            if ((status & USB_STATE_MASK) != USB_STATE_CONNECTED || (status & USB_CONFIG_MASK) == 0)
                break;
            try{Thread.sleep(1);}catch(Exception e){}          
        }
        usbDisable();
    }
    
    /**
     * Set the USB serial number. Should be a unique 12 character String
     * @param sn
     */
    public static void setSerialNo(String sn)
    {
        serialNo = sn;
        usbSetSerialNo(sn);
    }
    
    /**
     * Return the current USB serial number.
     * @return the serial number
     */
    public static String getSerialNo()
    {
        return serialNo;
    }
    
    /**
     * Set the USB name. Can be up to 16 character String
     * @param nam the mame
     */
    public static void setName(String nam)
    {
        name = nam;
        usbSetName(nam);
    }
    
    /**
     * Return the current USB name.
     * @return the name
     */
    public static String getName()
    {
        return name;
    }
    
    /**
     * Load the current system settings associated with this class. Called
     * automatically to initialize the class. May be called if it is required
     * to reload any settings.
     */
    public static void loadSettings()
    {
        setSerialNo(SystemSettings.getStringSetting(SERIAL_NO, "123456780090"));
        setName(SystemSettings.getStringSetting(NAME, "nxt"));
    }
    
	public static native void usbEnable(int reset);
	public static native void usbDisable();
	public static native void usbReset();
	public static native int usbRead(byte [] buf, int off, int len);
	public static native int usbWrite(byte [] buf, int off, int len);
    public static native int usbStatus();
    public static native void usbSetSerialNo(String serNo);
    public static native void usbSetName(String name);
    
}
