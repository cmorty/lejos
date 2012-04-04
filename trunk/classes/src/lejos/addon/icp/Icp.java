package lejos.addon.icp;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * <b>Icp</b> encapsulates the iControlPad gaming device, allowing clients to send
 * commands to the device and poll its status.  Additionally, this class can create
 * polling threads that will automatically notify listeners when the state of the
 * control pad changes.
 * </p>
 *
 * <p>
 * <b>Firmware note:</b> this class has been tested with iControlPad firmware 1.0.1
 * and 2.3 (the getVersion() method of this class can tell you what version you
 * have installed).  The 1.0.1 version only supports the getVersion() method and
 * the button/control state query methods.  Version 2.3 supports the other methods
 * (such as battery charge level) as well.  If you're going to use anything other
 * than the control query methods, please verify that you've loaded the latest
 * firmware onto the iControlPad.
 * </p> 
 * 
 * <p>
 * Icp objects are created using factory methods (since there is only one physical
 * device, it must be shared by all objects in the system).  To get an instance
 * of the Icp object for a particular controller, you need two things.  First,
 * you need its Bluetooth name (if the device is already paired), or its
 * address (12 hex digits, no spaces or colons).  Second, you need a Connector
 * object written for your host that will set up the Bluetooth connection and
 * provide the necessary input and output streams to the Icp object.  We've
 * included the IcpConnectorNxt class for communications with the NXT brick.
 * </p>
 * 
 * <p>
 * For example, to connect an NXT brick to the iCP, you could do one of
 * the following:
 * </p>
 * 
 * <ol>
 * <li><p>Using the raw Bluetooth address (for an unpaired iCP):</p>
 * <pre>
 * Icp lejos.addon.icp = Icp.getInstance(new IcpConnectorNxt("DEADBEEFF00D"));
 * </pre>
 * </li>
 * 
 * <li><p>Using the name of a pre-paired device to obtain the address automatically:</p>
 * <pre>
 * javax.bluetooth.RemoteDevice btrd = lejos.nxt.comm.Bluetooth.getKnownDevice("iControlPad-F00D");
 * Icp lejos.addon.icp = Icp.getInstance(new IcpConnectorNxt(btrd.getDeviceAddr()));
 * </pre>
 * </li>
 * </ol>
 * 
 * <p>
 * Once you've obtained the Icp object, you can send commands directly to the device
 * to poll its status or get information from the device (battery charge, version, etc).
 * You may do so by using the methods of this class.  <b>Note:</b> the current version
 * does not support all get/set options for the controller, due to problems obtaining
 * accurate documentation from the manufacturer.
 * </p>
 * 
 * <p>
 * Most users will prefer to register a class as an Event Listener and be automatically
 * notified of button presses and other control events.  To do so, create a class that
 * implements either the IcpEventListener or IcpStateListener class, and then register it:
 * </p>
 * 
 * <ol>
 * <li><p>IcpEventListener (give keyboard-like events on press and release):</p>
 * <pre>
 * lejos.addon.icp.addEventListener(someIcpEventListener);
 * </pre>
 * </li>
 * 
 * <li><p>IcpStateListener (get raw access to the state of all controls in one shot):</p>
 * <pre>
 * lejos.addon.icp.addStateListener(someIcpStateListener);
 * </pre>
 * </li>
 * </ol>
 * 
 * <p>
 * See the individual method comments for additional details.
 * </p>
 * 
 * @author Jason Healy <jhealy@logn.net>
 *
 * @see <a href="http://www.icontrolpad.com/support/">iControlPad Support Site</a>
 */
public class Icp {

    //////////////////////////////////////////////////////////
    // Cache variables (for singleton factory methods)
    //////////////////////////////////////////////////////////
    
    // We could/should use a Map for this, but HashMap is deprecated in leJOS 0.9,
    // and it's also unlikely that people would use a large number of Icp
    // devices, so linear searches are an acceptable tradeoff
    /** Array of known bluetooth addresses that have an associated Icp instance */
    protected static List<String> cacheAddr = new ArrayList<String>(1);
    
    /** Array of Icp instances that have already been created */
    protected static List<Icp> cacheIcp = new ArrayList<Icp>(1);
    
    
    //////////////////////////////////////////////////////////
    // Constants used to define communications with the iCP
    //////////////////////////////////////////////////////////
    
    /** Amount of time to wait before attempting a re-connection, in milliseconds */
    public static final int RECONNECT_TIMEOUT = 5000; // shutdown + startup time for iCP
    
    /** Command timeout to clear bad commands or lost output, in milliseconds */
    public static final int TIMEOUT_MS = 125;
    
    /** Number of sequential timeouts before we assume the connection is dead */
    public static final int TIMEOUT_MAX = 16; // about 2 seconds
    
    /** Response from the iControlPad signifying "OK" */
    public static final int OK = 0x80;

    /** Response from the iControlPad signifying "NOT OK" */
    public static final int BAD = 0x81;
    
    /** Response from the iControlPad signifying "ON" */
    public static final int ON = 0x01;

    /** Response from the iControlPad signifying "OFF" */
    public static final int OFF = 0x00;
    
    /** Battery Level Constant: Full */
    public static final int BATTERY_FULL = 6;

    /** Battery Level Constant: Almost Full */
    public static final int BATTERY_ALMOST_FULL = 5;

    /** Battery Level Constant: 3/4 Full */
    public static final int BATTERY_3_4_FULL = 4;

    /** Battery Level Constant: 1/2 Full */
    public static final int BATTERY_HALF_FULL = 3;

    /** Battery Level Constant: 1/4 Full */
    public static final int BATTERY_1_4_FULL = 2;

    /** Battery Level Constant: Almost Dead */
    public static final int BATTERY_ALMOST_DEAD = 1;

    /** Battery Level Constant: Dead */
    public static final int BATTERY_DEAD = 0;

    /** LED Behavior setting: Normally off; blink once every 5 seconds; double blink if low battery */
    public static final int LED_PULSE_DOUBLE = 0;

    /** LED Behavior setting: Normally on; blink once every 5 seconds when low battery */
    public static final int LED_PULSE_INVERSE = 2;

    /** LED Behavior setting: Normally off; turns on and stays on when low battery */
    public static final int LED_LOW_BATTERY_INDICATOR = 4;

    /** LED Behavior setting: Normally off; blink once every second; double-blink if low battery */
    public static final int LED_PULSE_DOUBLE_QUICK = 5;
    
    /** Communications baud rate: 9600 */
    public static final int BAUD_RATE_9600 = 4;

    /** Communications baud rate: 19200 */
    public static final int BAUD_RATE_19200 = 3;

    /** Communications baud rate: 38400 */
    public static final int BAUD_RATE_38400 = 2;

    /** Communications baud rate: 57600 */
    public static final int BAUD_RATE_57600 = 1;


    //////////////////////////////////////////////////////////
    // Data fields for each individual instance
    //////////////////////////////////////////////////////////
    
    /** Connector that actually establishes the link to the iControlPad */
    protected IcpConnector connector;
    
    /** Number of connection attempts */
    protected int connectionAttempts = 0;
    
    /** Input bytes read from the iControlPad */
    protected BufferedInputStream is;
    
    /** Output bytes sent to the iControlPad */
    protected BufferedOutputStream os;
    
    /** Number of timeout errors we've experienced */
    protected int timeouts = 0;
    
    /** Whether this object is still "live" or not */
    protected boolean valid = false;
    
    

    //////////////////////////////////////////////////////////
    // Constructors and Factories
    //////////////////////////////////////////////////////////
    

    /**
     * <p>
     * Protected Constructor.  Normal clients should use one of the factory methods
     * of this class to create an Icp instance.  Multiple instances of this class
     * connected to a single iControlPad may result in undefined behaviour, which
     * is why we require factory methods to obtain an instance.
     * </p>
     * 
     * <p>
     * Takes a connector object and uses it to create an initial connection to
     * the Icp.  Additionally, stores the connector for later in case it's needed.
     * </p>
     * 
     * @param ic The connector to use when (re)establishing communications with the iControlPad
     * 
     * @see #getInstance(IcpConnector)
     * @see #findInstance(String)
     */
    protected Icp(IcpConnector ic) {
	connector = ic;
	connect();
    }

    
    /**
     * <p>
     * Given a connector object capable of connecting to an iControlPad, this
     * method either finds an existing Icp object with the same target device,
     * or constructs a new one and returns it.  Be aware that the returned
     * object may be shared with other instances in the same program.
     * </p>
     * 
     * @param ic The connector to use when (re)establishing a connection to the iControlPad

     * @return Icp The Icp instance associated with the given iControlPad (ic.getTarget())
     */
    public static Icp getInstance(IcpConnector ic) {
	if (null == ic) {
	    throw new IllegalArgumentException("Connector for Icp cannot be null");
	}

	// search for a cached instance
	Icp instance = findInstance(ic.getTarget());
	
	if (null == instance) { // didn't find a cached copy
	    instance = new Icp(ic);

	    cacheAddr.add(ic.getTarget());
	    cacheIcp.add(instance);
	}
	
	return instance;	
    }
    
    
    /**
     * <p>
     * Given a target iControlPad (name or address), this method returns the
     * existing Icp object associated with that target.  If no such object has
     * already been created, the method returns null.  If you wish to create
     * a new Icp object when one does not already exist, us the getInstance()
     * method instead.
     * </p>
     * 
     * @param target The target iControlPad name or address
     * 
     * @return Icp The Icp object associated with this target, or null if one does not already exist
     * 
     * @see #getInstance(IcpConnector)
     */
    public static Icp findInstance(String target) {
	// search for an existing instance
	for (int i = 0; i < cacheAddr.size(); i++) {
	    if (cacheAddr.get(i).equals(target)) {
		return cacheIcp.get(i);
	    }
	}

	// default to null
	return null;
    }
    
    
    
    
    //////////////////////////////////////////////////////////
    // Error handling and Connection establishment methods
    //////////////////////////////////////////////////////////

    
    /**
     * Helper method to ask the connector object for its input and
     * output streams, verify they exist, and cast or wrap them
     * into buffered streams.
     */
    protected synchronized void connectStreams() {
	InputStream cis = connector.getInputStream();
	OutputStream cos = connector.getOutputStream();
	
	if (null != cis && null != cos) {
	    
	    if (cis instanceof BufferedInputStream) {
		is = (BufferedInputStream)cis;
	    }
	    else {
		is = new BufferedInputStream(cis);
	    }
	    if (cos instanceof BufferedOutputStream) {
		os = (BufferedOutputStream)cos;
	    }
	    else {
		os = new BufferedOutputStream(cos);
	    }
	    
	    valid = true;
	    connectionAttempts = 0;
	    timeouts = 0;
	}

	connectionAttempts++;
	
    }
    
    
    /**
     * <p>
     * Attempts to connect to the iControlPad using the IcpConnector
     * provided at construction time.  Resets valid to true if
     * the connection succeeded.
     * </p>
     */
    protected synchronized void connect() {
	connector.connect();
	connectStreams();
    }
    
    
    /**
     * <p>
     * Attempts to reconnect to the iControlPad using the IcpConnector
     * provided at construction time.  Resets valid to true if
     * the reconnection succeeded.
     * </p>
     */
    protected synchronized void reconnect() {
	// wait a few seconds before reconnecting
	System.out.println("iCP " + connector.getTarget()
		          + "\nlost,attempt "
			  + connectionAttempts);
	try {
	    Thread.sleep(RECONNECT_TIMEOUT);
	}
	catch (InterruptedException ie) {}

	connector.reconnect();
	connectStreams();
    }
    
    
    /**
     * <p>
     * Returns whether this Icp object is still valid (that is, if
     * it has not suffered from any IO errors that would indicate that
     * it's disconnected from the control pad).  Normally the class attempts
     * to re-validate itself automatically, but it may give up if too many
     * connections fail.
     * </p>
     * 
     * @return boolean True, if this object is still valid
     */
    public boolean isValid() {
	return valid;
    }


    /**
     * <p>
     * Marks this Icp object as "invalid" due to the underlying connection
     * being lost or another unrecoverable state being reached.  Attempts
     * to clean up resources and then reconnect to the iControlPad.
     * </p>
     * 
     * @see #reconnect()
     */
    protected synchronized void invalidate() {
	valid = false;

	if (null != is) {
	    try {
		is.close();
	    } catch (Exception e) {}
	}
	if (null != os) {
	    try {
		os.close();
	    } catch (Exception e) {}
	}
	
	is = null;
	os = null;

	// now try to reconnect
	reconnect();
    }
    

    
    //////////////////////////////////////////////////////////
    // Methods to send/receive data to/from the iCP
    //////////////////////////////////////////////////////////

    
    /**
     * <p>
     * Executes a single iControlPad command by sending one or more bytes
     * and waiting for a response of the given number of bytes.  Attempts
     * to recover from bad commands by timing out when data fails to be read.
     * IO failures (e.g., closed streams) result in a reconnection attempt.
     * </p>
     * 
     * @param cmd The bytes to send to the iControlPad (see developer docs for coding)
     * @param expected The number of bytes to read back
     * 
     * @return int[] The raw bytes returned from the iControlPad (see developer docs for coding)
     * 
     * @throws IcpTimeoutException If we timed out while waiting for a response from the iCP
     *                             (can be caused by a bad command or a communication error)
     *                               
     * @see <a href="http://www.icontrolpad.com/support/">iControlPad Support Site</a>
     */
    protected synchronized int[] command(byte[] cmd, int expected) throws IcpTimeoutException {
	int[] result = null;
		
	while (null == result) {
	    result = new int[expected];
	    int i = 0;

	    try {

		// don't try IO on a dead object
		if (!valid) {
		    throw new IllegalStateException("iCP: " + connector.getTarget() + " not valid");
		}
		
		if (cmd.length > 0) {
		    os.write(cmd);
		    os.flush();
		}

		// the iCP times out after 125ms if a bad command is received
		long timeout = System.currentTimeMillis() + TIMEOUT_MS;

		int c = 0;

		// poll for response until we get it, or we run out of time
		while (i < expected) {
		    c++;
		    if (System.currentTimeMillis() > timeout) {
			throw new InterruptedException("iCP: " + connector.getTarget()
							+ " timeout after " + c + " attempts ("
							+ i + "/" + expected + ")");
		    }

		    int available = is.available();
		    int read = available > expected - i ? expected - i : available;
		    for (int j = 0; j < read; j++) {
			int raw = is.read();
			if (raw < 0) {
			    throw new IOException("iCP: " + connector.getTarget()
				    		+ " " + raw + " received; stream was closed");
			}
			result[i] = (byte)raw;
			i++;
		    }

		    /*if (i < expected) {
			try {
			    Thread.sleep(1); // pause before trying to read more
			}
			catch (InterruptedException nap) {}

		    }*/
		}

		// read was successful, so reset timeouts
		timeouts = 0;
	    }
	    catch (IllegalStateException ise) {  // still invalid after a previous failure...
		//System.out.println(ise.getMessage());
		
		result = null;
		invalidate();
	    }
	    catch (IOException ioe) { // stream closed unexpectedly
		//System.out.println(ioe.getMessage());
		result = null;
		invalidate();
	    }
	    catch (InterruptedException ie) { // timeout, but not necessarily fatal
		//System.out.println(ie.getMessage());
		result = null;
		timeouts++;

		if (timeouts > TIMEOUT_MAX) {
		    invalidate();
		    System.out.println("About to throw IcpTimeoutException");
		    return null;
		    //throw new IcpTimeoutException(ie);
		}
		else {
		    try {
			Thread.sleep(TIMEOUT_MS); // let the controller flush any bad data
		    }
		    catch (InterruptedException ietoo) {}
		}
	    }
	}

	return result;
    }

    
    /**
     * <p>
     * Abstract command to get a linefeed-terminated string (note, the developer
     * docs say CR/LF, but this appears to be false), rather than a
     * specific byte count.
     * </p>
     * 
     * @param cmd The bytes to send to the iControlPad (see developer docs for coding)
     * 
     * @return String The response bytes converted to a CR/LF-terminated string
     * 
     * @throws IcpTimeoutException If we do not receive a response from the iCP
     *                             in time (not necessarily fatal, but no response
     *                             is available at this time)
     */
    protected String stringCommand(byte[] cmd) throws IcpTimeoutException {
	// cmd gets the first character
	final byte[] nul = new byte[0];  // gets remaining chars
	final int LF = 10; // line feed

	int[] t = null;
	int curr = -1;

	// The get ID hardware function returns a string
	// terminated with CR/LF, so we must read an
	// arbitrary number of characters until we get
	// that sequence
	
	StringBuilder sb = new StringBuilder();
	t = command(cmd, 1);
	curr = t[0];
	
	while (LF != curr) {
	    sb.append((char)curr);
	    t = command(nul, 1); // read the next char in the sequence
	    curr = t[0];
	}
	
	return sb.toString();	
    }
    
    
    /**
     * <p>
     * Gets a three-byte response to a command and converts the value into a float
     * by treating the values as readable chars with a decimal after the first
     * digit.
     * </p>
     * 
     * @param cmd The bytes to send to the iControlPad (see developer docs for coding)
     * 
     * @return float The response converted to a floating-point number, or
     *               NaN if the value can't be decoded.
     * 
     * @throws IcpTimeoutException If we do not receive a response from the iCP
     *                             in time (not necessarily fatal, but no response
     *                             is available at this time)
     */
    protected float chfltCommand(byte[] cmd) throws IcpTimeoutException {
	int[] c = command(cmd, 3);
	String f = "";
	for (int i = 0; i < c.length; i++) {
	    f += (char)c[i];
	}
	try {
	    return Float.parseFloat(f) / 100.0f;
	}
	catch (Exception e) {
	    return Float.NaN;
	}
    }
    
    
    /**
     * <p>
     * Sets a boolean value on the iControlPad and checks the return status.
     * </p>
     * 
     * @param cmd The byte to send to the iControlPad (see developer docs for coding)
     * @param state True (ON/0x01) or False (OFF/0x00)
     * 
     * @return boolean True, if the setting was acknowledged by the iControlPad
     *                 as being successfully set
     * 
     * @throws IcpTimeoutException If we do not receive a response from the iCP
     *                             in time (not necessarily fatal, but no response
     *                             is available at this time)
     */
    protected boolean setFlag(byte cmd, boolean state) throws IcpTimeoutException {
	int[] result = command(new byte[] {cmd,
		state ? (byte)ON : (byte)OFF},
		1);
	return result[0] == OK;
    }
    
    
    /**
     * <p>
     * Gets the raw bytes associated with the digital control pad (left, up,
     * right, down) and returns them as an array in the order specified by the
     * iControlPad documentation ("byteA" and "byteB").
     * </p>
     * 
     * @return int[] Two raw byte values; byteA is [0] and byteB is [1]
     * 
     * @throws IcpTimeoutException If the underlying command to fetch the bytes fails
     * 
     * @see #getControlState()
     */
    public int[] getDigitals() throws IcpTimeoutException {
	final byte[] cmd = {(byte)0xa5};
	return command(cmd, 2);
    }
    
    
    /**
     * <p>
     * Gets the raw bytes associated with the analog input joysticks (x1, y1,
     * x2, y2) and returns them in an array.
     * </p>
     * 
     * @return int[] 4 raw byte values; x1, y1, x2, y2 (range is -32 to 32 for each)
     *
     * @throws IcpTimeoutException If the underlying command to fetch the bytes fails
     * 
     * @see #getControlState()
     */
    public int[] getAnalogs() throws IcpTimeoutException {
	final byte[] cmd = {(byte)0x87};
	return command(cmd, 4);
    }
    
    
    /**
     * <p>
     * Gets the version string from the iControlPad.
     * </p>
     * 
     * @return String The version string (<em>e.g.</em>, "<code>iControlPad FW 2.3</code>")
     *
     * @throws IcpTimeoutException If the underlying command to fetch the bytes fails
     */
    public synchronized String getVersion() throws IcpTimeoutException {
	final byte[] cmd = {(byte)0x39};
	return stringCommand(cmd);
    }
    
    
    /**
     * <p>
     * Gets the voltage of the internal battery from the iControlPad.
     * </p>
     * 
     * @return float The voltage of the battery
     *
     * @throws IcpTimeoutException If the underlying command to fetch the bytes fails
     */
    public float getBatteryVoltage() throws IcpTimeoutException {
	final byte[] cmd = {(byte)0xaa};
	return chfltCommand(cmd);
    }
    
    
    /*
      
     ==========================================================================
     ==========================================================================
     ===================RAW COMMAND MODES COMMENTED OUT========================
     ==========================================================================
     ==========================================================================

	// jhealy: 2012-02-29: the iCP documentation is not correct on several
	// of these methods, and I am running into trouble debugging them
	// Currently awaiting a response from the iCP dev team for better docs.
	// before testing and debugging thoroughly.  Meanwhile, the control
	// pad methods appear to work correctly.


    public int getBatteryLevel() throws IcpTimeoutException {
	final byte[] cmd = {(byte)0x55};
	int[] t = command(cmd, 1);
	return t[0];
    }
    
    
    public int getChargerStatus() throws IcpTimeoutException {
	final byte[] cmd = {(byte)0xde};
	int[] t = command(cmd, 1);
	return t[0];
    }
    
    
    public float getChargerVolts() throws IcpTimeoutException {
	final byte[] cmd = {(byte)0x6f};
	return chfltCommand(cmd);
    }
    

    public boolean setCharger(boolean state) throws IcpTimeoutException {
	final byte cmd = (byte)0x2a;
	return setFlag(cmd, state);
    }
    
    
    // only use internally?
    protected boolean setLedControl(boolean state) throws IcpTimeoutException {
	final byte cmd = (byte)0x6d;
	return setFlag(cmd, state);
    }
    
    
    public synchronized boolean setLed(boolean state) throws IcpTimeoutException {
	final byte cmd = (byte)0xff;

	// first, take control of the LED
	if (setLedControl(true)) {
	    return setFlag(cmd, state);
	}
	return false;
    }
    
    
    public boolean setLedMode(int mode) throws IcpTimeoutException {
	final byte cmd = (byte)0xe4;
	
	if (mode != LED_PULSE_DOUBLE &&
		mode != LED_PULSE_INVERSE &&
		mode != LED_LOW_BATTERY_INDICATOR &&
		mode != LED_PULSE_DOUBLE_QUICK) {
	    throw new IllegalArgumentException("LED mode must be an LED_ constant");
	}

	int[] t = command(new byte[] { cmd, (byte)mode }, 1);
	return t[0] == OK;
    }
    
    
    public int getEeprom(int address) throws IcpTimeoutException {
	final byte[] cmd = new byte[3];
	cmd[0] = (byte)0x9a;
	cmd[1] = (byte)(address & 0xff);        // low byte of address
	cmd[2] = (byte)((address >> 8) & 0xff); // high byte of address

	int[] t = command(cmd, 1);
	return t[0];
    }
    
    
    public boolean setEeprom(int address, byte value) throws IcpTimeoutException {
	final byte[] cmd = new byte[5];
	cmd[0] = (byte)0x7a;
	cmd[1] = (byte)(address & 0xff);        // low byte of address
	cmd[2] = (byte)((address >> 8) & 0xff); // high byte of address
	cmd[3] = (byte)0x19; // security byte (required to validate write)
	cmd[4] = value;

	int[] t = command(cmd, 1);
	return t[0] == OK;
    }
    
    
    // warning, this might cut you off from the device!!
    public synchronized boolean setBaudRate(int rate) throws IcpTimeoutException {
	final byte cmd = (byte)0xc2;
	
	if (rate != LED_PULSE_DOUBLE &&
		rate != LED_PULSE_INVERSE &&
		rate != LED_LOW_BATTERY_INDICATOR &&
		rate != LED_PULSE_DOUBLE_QUICK) {
	    throw new IllegalArgumentException("Baud rate must be a BAUD_RATE_ constant");
	}

	command(new byte[] { cmd, (byte)rate }, 0);

	try {
	    Thread.sleep(25);
	}
	catch (InterruptedException ie) {}
	
	// the baud set either returns success, or nothing
	try {
	    int available = is.available();
	    if (available > 0) {
		int result = is.read();
		return result == OK;
	    }
	}
	catch (IOException ioe) {}
	
	return false;
    }
    
    
    public boolean powerOff() throws IcpTimeoutException {
	final byte[] cmd = {(byte)0x94, (byte)0x27, (byte)0x6a, (byte)0xfe};
	int[] t = command(cmd, 1);
	return t[0] == OK;
    }
    
    
    public boolean setAutonomous() {
	throw new UnsupportedOperationException("Autonomous Mode Not Supported");
    }
    */

    
    /**
     * <p>
     * Queries the iCP for all of its digital and analog input states,
     * and returns them as a single IcpState object (which encapsulates
     * all of this information).  Allows for a one-shot query that you
     * can call other methods on to see if a particular button is pressed.
     * </p>
     * 
     * @return IcpState A state object encapsulating all the inputs
     * 
     * @throws IcpTimeoutException If the underlying command to fetch the bytes fails
     * 
     * @see IcpState
     */
    public IcpState getControlState() throws IcpTimeoutException {
	int[] digitals = null;
	int[] analogs = null;
	
	synchronized(this) {
	    digitals = getDigitals();
	    analogs = getAnalogs();
	}
	
	return new IcpState(digitals[0], digitals[1], analogs[0], analogs[1], analogs[2], analogs[3]);
    }
    

    
    //////////////////////////////////////////////////////////
    // Methods to add listeners for state changes on this Icp
    //////////////////////////////////////////////////////////

    
    /**
     * <p>
     * Registers a listener that wishes to receive a new IcpState
     * object at a given interval.  Note that the raw IcpState objects
     * may not vary from one to the next (if the control pad hasn't
     * had any changes initiated by the user).  If you wish only to
     * receive notification of <em>changes</em> to the state of the
     * device, consider the addEventListener methods instead.
     * </p>
     * 
     * @param l The listener to notify with IcpState objects
     * @param interval The time, in milliseconds, to poll the control
     *                 pad for state information
     * 
     * @see #addEventListener(IcpEventListener, int)
     * @see IcpStatePoller
     * @see IcpStateListener
     */
    public void addStateListener(IcpStateListener l, int interval) {
	IcpStatePoller.addListener(this, l, interval);
    }
    

    /**
     * <p>
     * Registers a listener just as with the two-argument version of
     * this method, but uses the default polling time instead of
     * setting one explicitly.
     * </p>
     * 
     * @param l The listener to notify with IcpState objects
     * 
     * @see #addStateListener(IcpStateListener, int)
     */
    public void addStateListener(IcpStateListener l) {
	IcpStatePoller.addListener(this, l);
    }
    

    /**
     * <p>
     * Registers a listener that wishes to receive a new IcpState
     * object whenever there is a <em>change</em> in the control
     * pad's state (<em>e.g.</em>, a button is pressed or released,
     * or an analog joystick is moved).  The interval determines the
     * minimum time between polls to check if the state is changed;
     * the actual time between fired events will depend on when any
     * changes take place.
     * </p>
     * 
     * @param l The listener to notify with IcpState objects
     * @param interval The time, in milliseconds, between polling
     *                 checks on the iCP (the actual time between
     *                 fired events will vary)
     *
     * @see IcpEventPoller
     * @see IcpEventListener
     */
    public void addEventListener(IcpEventListener l, int interval) {
	IcpEventPoller.addListener(this, l, interval);
    }
    

    /**
     * <p>
     * Registers a listener just as with the two-argument version of
     * this method, but uses the default polling time instead of
     * setting one explicitly.
     * </p>
     * 
     * @param l The listener to notify with IcpState objects
     * 
     * @see #addEventListener(IcpEventListener, int)
     */
    public void addEventListener(IcpEventListener l) {
	IcpEventPoller.addListener(this, l);
    }
  

}
