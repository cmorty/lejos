package lejos.nxt.comm;
import lejos.util.Delay;
/**
 * Low-level USB access.
 * 
 * @author Lawrie Griffiths, extended to support streams by Andy Shaw
 *
 */
public class USB extends NXTCommDevice {
    public static final int RESET = 0x40000000;
    static final int BUFSZ = 64;
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

    static {
        loadSettings();
    }
    
    
	private USB()
	{		
	}

    private static void flushInput(NXTConnection conn)
    {
        conn.discardInput();
    }
    
    private static boolean isConnected(NXTConnection conn, byte [] cmd)
    {
        // This method provides support for packet mode connections.
        // We wait for the PC to tell us that the connection has been established.
        // While waiting we support a small sub-set of LCP to allow identification
        // of the device.
        int len = 3;
        boolean ret = false;
        if (conn.available() < 2) return false;
        // Look for a system command
        if (conn.read(cmd, cmd.length, false) >= 2 && cmd[0] == SYSTEM_COMMAND_REPLY)
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
                // We only send back the device devName.
                for(int i=0;i<devName.length();i++) cmd[3+i] = (byte)devName.charAt(i);
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
            conn.write(cmd, len, false);
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
        byte [] buf = new byte [BUFSZ];
        USBConnection conn = new USBConnection(NXTConnection.RAW);
        usbSetName(devName);
        usbSetSerialNo(devAddress);
        usbEnable(((mode & RESET) != 0 ? 1 : 0));
        mode &= ~RESET;
        // Discard any left over input
        flushInput(conn);
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
                    (mode == NXTConnection.PACKET && isConnected(conn, buf)))
                {
                    conn.setIOMode(mode);
                    return conn;
                }
            }
            Delay.msDelay(1);
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
     * @param conn The connection associated with this device.
     * @param timeout
     */
    public static void waitForDisconnect(USBConnection conn, int timeout)
    {
        while(timeout-- > 0)
        {
            flushInput(conn);
            int status = usbStatus();
            // Wait for the interface to be down
            if ((status & USB_STATE_MASK) != USB_STATE_CONNECTED || (status & USB_CONFIG_MASK) == 0)
                break;
            Delay.msDelay(1);
        }
        usbDisable();
    }
    
	public static native void usbEnable(int reset);
	public static native void usbDisable();
	public static native void usbReset();
	public static native int usbRead(byte [] buf, int off, int len);
	public static native int usbWrite(byte [] buf, int off, int len);
    public static native int usbStatus();
    public static native void usbSetSerialNo(String serNo);
    public static native void usbSetName(String name);


    /**
     * Class to provide polymorphic access to the connection methods.
     * Gets returned as a singleton by getConnector and can be used to create
     * connections.
     */
    static class Connector extends NXTCommConnector
    {
        /**
         * Open a connection to the specified name/address using the given I/O mode
         * @param target The name or address of the device/host to connect to.
         * @param mode The I/O mode to use for this connection
         * @return A NXTConnection object for the new connection or null if error.
         */
        public NXTConnection connect(String target, int mode)
        {
            return null;
        }

        /**
         * Wait for an incomming connection, or for the request to timeout.
         * @param timeout Time in ms to wait for the connection to be made
         * @param mode I/O mode to be used for the accpeted connection.
         * @return A NXTConnection object for the new connection or null if error.
         */
        public NXTConnection waitForConnection(int timeout, int mode)
        {
            return USB.waitForConnection(timeout, mode);
        }
    }
    
    static NXTCommConnector connector = null;

    /**
     * Provides access to the singleton connection object.
     * This object can be used to create new connections.
     * @return the connector object
     */
    public static NXTCommConnector getConnector()
    {
        if (connector == null)
            connector = new Connector();
        return connector;
    }
    
}
