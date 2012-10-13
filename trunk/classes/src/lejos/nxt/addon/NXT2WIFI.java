package lejos.nxt.addon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.comm.RConsole;
import lejos.nxt.comm.RS485;
import lejos.util.Delay;

/**
 * Interface class for the NXT2WIFI sensor device developed by Danny Benedettelli. The NXT2WIFI is a self-contained
 * wifi sensor that attaches to the NXT on port 4 and provides an embedded graphic webserver and TCP/UDP connectivity. 
 * This class provides basic wifi connection features to connect to a WPA2 network. It provides some low-level
 * data read and write methods. The recommended method for reading and writing data is to use the 
 * InputStream and OutputStream interface methds.
 * 
 * @author mcrosbie
 * @author mark@mastincrosbie.com
 * @version 1.0 Initial release
 * 
 */
public class NXT2WIFI {
	
	/** The current baudrateIndex */
	private int currentBaudrate;
	private final static int BUFFER_LENGTH = 100;

	private final static byte[] CMD_PING = new byte[] {'$','?','?','?', 0x0A};
	private final static byte[] CMD_FW = new byte[] {'$','F','W', 0x0A};
	private final static byte[] CMD_IPADDR = new byte[] {'$','W','F','I','P', 0x0A};
	private final static byte[] CMD_MACADDR = new byte[] {'$','M','A','C', 0x0A};
	private final static byte[] CMD_SCANFORNETWORKS = new byte[] {'$','W','F','S','C','A','N', 0x0A};
	private final static byte[] CMD_VERBOSE = new byte[] {'$','S','C','V', '0', 0x0A};
	private final static byte[] CMD_DEBUG = new byte[] {'$','D','B','G', '0', 0x0A};
	private final static byte[] CMD_ECHO = new byte[] {'$','S','C','E', '0', 0x0A};
	
	private final static String RESP_PING = "!!!";
	
	// WIFI_ security modes
	private final static int WF_SEC_OPEN =			0;
	private static final int WF_SEC_WEP_40 =		1;
	private static final int WF_SEC_WEP_104 =		2;
	private static final int WF_SEC_WPA_KEY =		3;
	private static final int WF_SEC_WPA_PASSPHRASE = 4;
	private static final int WF_SEC_WPA2_KEY =		5;
	private static final int WF_SEC_WPA2_PASSPHRASE = 6;
	private static final int WF_SEC_WPA_AUTO_KEY = 7;
	private static final int WF_SEC_WPA_AUTO_PASSPHRASE = 8;

	// connection status codes
	public static final int NOT_CONNECTED = 0;
	public static final int CONNECTING = 1;
	public static final int CONNECTED = 2;
	public static final int CONNECTION_LOST = 3;
	public static final int CONNECTION_FAILED= 4;
	public static final int STOPPING=5;
	public static final int TURNED_OFF=6;
		
	private static final String connectionStatuses[] = {
		"Not Connected",
		"Connecting",
		"Connected",
		"Connection Lost",
		"Connection Failed",
		"Stopping",
		"Turned Off"
	};
	
	public static final boolean AD_HOC = true;
	public static final boolean INFRASTRUCTURE = false;
	
	private static final int cmdDelay = 250;
	
	private int bufferSize = 500;
		
	private int readTimeout = 250; // how long to wait for a reply, in ms
	
	private int NUM_SOCKETS = 4;	// maximum number of sockets supported on the NXT2WIFI

	private boolean socketOpen[];
	
	private static int defaultBaudRate = 230400;	// Change this to change the default baud rate
	private boolean debug = false;	// Wait for remote console if true
	
	
	/**
	 * Default constructor. Assumes baud rate of 230400 and no debug output to RConsole.
	 */
	public NXT2WIFI() {
		this(defaultBaudRate);
	}
	
	public NXT2WIFI(int baudrate) {
		currentBaudrate = baudrate;
		RS485.hsEnable(baudrate, BUFFER_LENGTH);

			// clear out any characters in the RS485 buffer
		clearReadBuffer();
		socketOpen = new boolean[NUM_SOCKETS];
		int i;
		for(i=0; i < NUM_SOCKETS; i++) 
			socketOpen[i] = false;
		
		if(debug) RConsole.println("NXT2WIFI initialised");
	}
	
	/**
	 * Enable or disable debug output on USB console. Output is written to the RConsole which must be opened
	 * prior to calling.
	 * @param debugMode Boolean flag if true debug output is enabled, if false it is disabled
	 */
	public void setConsoleDebug(boolean debugMode) {
		
		debug = debugMode;
		if(debug) {
			RConsole.println("NXT2WIFI: DEBUG ENABLED at " + System.currentTimeMillis());
		}
	}
	
	/**
	 * Enable or disable debug output on USB console. Output is written to the RConsole which must be opened
	 * prior to calling.
	 * @param debugMode Boolean flag if true computer debug output is enabled, if false it is disabled
	 */
	public void setDebug(boolean e) throws IOException {
		if (e)
			tryCommand("$DBG1\n","Cannot enable debug stream");
		else
			tryCommand("$DBGo\n","Cannot disable debug stream");
	}
	
	/**
	 * Return an Input stream for receiving data from the socket. If this socket isn't opened then you'll get
	 * garbage back. If the remote side has closed the connection then you'll get nothing from the socket.
	 * @param socketID The socket to read data from
	 * @return an input stream for the NXTBee
	 */
	public InputStream getInputStream(int socketID) throws IOException {
		
		if( (socketID < 0) || (socketID > NUM_SOCKETS) ) {
			throw new IOException("Invalid Socket ID");
		}
	
		return new NXT2WIFIInputStream(socketID);
	}

	/**
	 * Return an Output stream for writing to the socket
	 * @param socketID The socket to write data to
	 * @return an output stream for the NXTBee
	 */
	public OutputStream getOutputStream(int socketID) throws IOException {
		if( (socketID < 0) || (socketID > NUM_SOCKETS) ) {
			throw new IOException("Invalid Socket ID");
		}
		
		return new NXT2WIFIOutputStream(socketID);
	}
	
	/**
	 * Return the current time of day and date string
	 * @param k is the offset from GMT (positive or negative)
	 * @return YYYY-MM-DD/hh:mm:ss or exception is thrown if an error occurs
	 */
	public String getTimeOfDay(int k) throws IOException {
		String cmd = "$GTIM?"+k+"\n";
		RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
		Delay.msDelay(50);
		
		String reply = readFully(false);
		
		String time;
		
		if(reply.startsWith("GTIM") && reply.length() > 5) {
			int equalsSign = reply.indexOf('=');
			time = reply.substring(equalsSign+1);
		} else {
			throw new IOException("Invalid time format");
		}

		return time;

	}
		
	/**
	 * Start a TCP server listening on a given port. Once the socket is opened the InputStream and OutputStream
	 * methods should be used to read/write data from the socket.
	 * 
	 * @param port The port to open the server on
	 * @param socketID The socket to associate with this port (1-4)
	 * @throws IOException if the server socket could not be opened
	 */
	public void serverSocket(int port, int socketID) throws IOException {
		if( (port < 0) || (port > 65535)) {
			throw new IOException("Invalid port number");
		}
		
		if( (socketID < 1) || (socketID > 4)) {
			throw new IOException("Invalid socket number");
		}
		
		if(debug) RConsole.println("serverSocket: port="+port + " socketID=" + socketID);
		
		String cmd = "$TCPOS"+socketID+"?"+port+"\n";
		String reply = tryCommand(cmd, "Failed to open TCP server socket");
		
		if(reply.length() <= 0) {
			throw new IOException("Invalid reply to open TCP server socket");
		}
		
		if(!reply.equalsIgnoreCase("1")) {
			throw new IOException("Socket open failed");
		}
	}
		

	/**
	 * Open a client socket to a given host on a given port. Use the InputStream and OutputStream
	 * methods to read and write data from the connection.
	 * 
	 * @param host The host name or IP address to open a socket on
	 * @param port The port of the remote host to to
	 * @param socketID The socket to associate with the connection (1-4)
	 * @throws IOException if the socket cannot be opened
	 */
	public void clientSocket(String host, int port, int socketID) throws IOException {
		if( (port < 0) || (port > 65535)) {
			throw new IOException("Invalid port number");
		}
		
		if( (socketID < 1) || (socketID > 4)) {
			throw new IOException("Invalid socket number");
		}

		if(debug) RConsole.println("clientSocket: host="+host + " port="+port + " socketID=" + socketID);

		String cmd = "$TCPOC"+socketID+"?"+host+","+port+"\n";
		tryCommand(cmd, "Failed to open TCP client socket");

	}
	
	/**
	 * Send a string to a remote client on a given socket. Low-level method, recommend the 
	 * OutputStream methods be used instead.
	 * Assumes that the socket is open.
	 * @param msg String to send
	 * @param socketID Socket ID previously opened to send on
	 */
	public void sendString(String msg, int socketID) throws IOException {
		if( (socketID < 1) || (socketID > 4)) {
			throw new IOException("Invalid socket number");
		}

		String cmd = "$TCPW"+socketID+"?"+msg.length()+","+msg;
		tryCommand(cmd, "Failed to open TCP client socket");
		
	}
	
	/**
	 * Close a socket. 
	 * @param socketID The ID of the socket to close (1-4) socketID=0 closes all sockets
	 */
	public void closeSocket(int socketID) throws IOException {
		if( (socketID < 0) || (socketID > 4)) {
			throw new IOException("Invalid socket number");
		}

		String cmd = "$TCPX"+socketID+"\n";
		tryCommand(cmd, "Failed to close socket");
		
	}
	
	/**
	 * Return true if the NXT2WIFI sensor is alive and can respond to a basic PING command
	 * 
	 * @return true if the sensor is attached and communicating. false if no sensor is attached.
	 */
	public boolean isAlive() {
		send("$???\n");
		//RS485.hsWrite(CMD_PING, 0, CMD_PING.length);
		Delay.msDelay(50);
		
		String reply = readFully(false);
				
		if(reply.length()>3 && reply.equalsIgnoreCase(RESP_PING)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Return a string containing the firmware version running on the NXT2WIFI device
	 * 
	 * @return A string containing the firmware version
	 */
	public String getFirmwareVersion() {
		
		if(debug) RConsole.println("firmwareVersion sending CMD_FW");
		send("$FW\n");
		//RS485.hsWrite(CMD_FW, 0, CMD_FW.length);
		Delay.msDelay(50);
		
		String reply = readFully(false);
			
		return reply;
	}
	
	/**
	 * Return the current IP address of the device
	 * @return A string containing the current IP address
	 */
	public String getIPAddress() throws IOException {
				
		send("$WFIP\n");
		//RS485.hsWrite(CMD_IPADDR, 0, CMD_IPADDR.length);
		Delay.msDelay(50);
		
		String reply = readFully(false);
		
		RConsole.println("Reply = " + reply);
		
		// we need to trim the first chunk off the reply as it should be
		// WFIP=
		// If this is not found then throw an exception
		if(reply.startsWith("WFIP=")) {
			reply = reply.substring(5);
		} else {
			throw new IOException("IPAddress Malformed response: " + reply);
		}
		
		return reply;
	}
	
	/**
	 * Return the current MAC address of the device
	 * @return A string containing the current MAC address
	 */
	public String getMACAddress() throws IOException {
				
		RS485.hsWrite(CMD_MACADDR, 0, CMD_MACADDR.length);
		Delay.msDelay(50);
		
		String reply = readFully(false);
		
		RConsole.println("Reply = " + reply);
		
		// we need to trim the first chunk off the reply as it should be
		// MAC=
		// If this is not found then throw an exception
		if(reply.startsWith("MAC=")) {
			reply = reply.substring(4);
		} else {
			throw new IOException("MACAddress Malformed response: " + reply);
		}
		
		return reply;
	}


	private String tryCommand(String cmd, String exception) throws IOException {
		return this.tryCommand(cmd, cmdDelay, exception);
	}

	
	/**
	 * Run a command or throw an exception
	 * @param cmd The string to send to the NXT2WIFI 
	 * @param timeout How long to wait for a reply (in ms)
	 * @param exception A string to use as the exception if the command fails
	 * @throws An IOException with the string text from the exception parameter
	 */
	private String tryCommand(String cmd, int timeout, String exception) throws IOException {
	
		RConsole.println("tryCommand: cmd = " + cmd);
		RS485.hsWrite(cmd.getBytes(), 0, cmd.length());

		Delay.msDelay(timeout);
		String reply = readFully(false);
		RConsole.println("tryCommand reply <" + reply +">");
	
		// parse the reply to locate the '=' and extract everything from that point on as the reply
		int equals = reply.indexOf("=");
		equals++;
		String ret = reply.substring(equals);
		
		if(debug) RConsole.println("tryCommand returning " + ret);
		
		return ret;
	}
	
	/**
	 * Connect to custom or default profile.
	 * 
	 * @param profile - true for CUSTOM, false for DEFAULT
	 * @throws IOException if an error occurred in connecting to the network
	 */	
	public void connect(boolean profile) throws IOException {
		if (profile)
			tryCommand("$WFC1\n", "Cannot connect using CUSTOM profile");
		else 
			tryCommand("$WFC0\n", "Cannot connect using DEFAULT profile");
	}
	
	/**
	 * Delete custom connection profile.
	 * @throws IOException if an error occurred in deleting the profile
	 */	
	public void deleteProfile() throws IOException {
		tryCommand("$WFKD\n", "Cannot delete CUSTOM profile");
	}		
	
	/**
	 * Save custom connection profile.
	 * @throws IOException if an error occurred in saving the profile
	 */	
	public void saveProfile() throws IOException {
		tryCommand("$WFKS\n", 500, "Cannot save CUSTOM profile");
	}	
	
	public void setSecurity (int mode, String passkey) throws IOException {
		setSecurity(mode,passkey,0);
	}	
	
	public void setSecurity (int mode, String passkey, int index) throws IOException {
		String cmd = "$WFS?" + mode + ":" + passkey + ":"+ index + "\n";
		tryCommand(cmd, 500,"Cannot set security type and passphrase");		
	}
	
	public void connectToWPAAutoWithKey(String ssid, String key, boolean saveConfig) throws IOException {
		
		String cmd;
		if(debug) RConsole.println("connectToWPAAuto: SSID= "+ssid+" passphrase= "+key);

		if(saveConfig) {
			deleteProfile(); 
		}
		
		// Set the security type and passphrase
		setSecurity(WF_SEC_WPA_AUTO_KEY,key);

		// Set the SSID
		setSSID(ssid);
		
		// Set infrastructure mode
		setType(INFRASTRUCTURE);

		if(saveConfig) {
			saveProfile(); 
		}
		
		// Connect to the wifi
		connect(true);
	}		
	
	/**
	 * Connect to a given SSID WPA Auto network with a passphrase. This method simply initiates the connection
	 * sequence and does not wait for the connection to complete or fail. You need to call connectionStatus()
	 * to poll the connection to see if it succeeds or fails. Connecting can take time if the WPA2 key
	 * needs to be re-generated and stored on the NXT2WIFI. Allow up to 30 seconds for this to complete.
	 * 
	 * @param ssid - SSID of the network
	 * @param passphrase - the passphrase to use
	 * @param saveConfig - If true then the WPA configuration is saved as the default Custom Profile on the device.
	 * @throws IOException if an error occurred in connecting to the network
	 */
	public void connectToWPAAutoWithPassphrase(String ssid, String passphrase, boolean saveConfig) throws IOException {
		
		String cmd;
		
		if(debug) RConsole.println("connectToWPAAuto: SSID= "+ssid+" passphrase= "+passphrase);

		if(saveConfig) {
			// Delete the custom profile $WFKD
			cmd = "$WFKD\n";
			tryCommand(cmd, "Cannot delete custom profile");
		}

		// Set infrastructure mode
		cmd = "$WFE?TYPE=0\n";
		tryCommand(cmd, "Cannot set infrastructure mode");		

		// Set the SSID
		cmd = "$WFE?SSID=" + ssid + "\n";
		tryCommand(cmd, "Cannot set SSID");		
		
		// Set the security type and passphrase
		cmd = "$WFS?" + WF_SEC_WPA_AUTO_PASSPHRASE + ":" + passphrase + "\n";
		tryCommand(cmd, 300, "Cannot set security type and passphrase");

		if(saveConfig) {
			// Save the profile
			cmd = "$WFKS\n";
			tryCommand(cmd, 400, "Cannot save profile");
		}
		
		// Connect to the wifi
		connect(true);

	}	
	
	/**
	 * Connect to a given SSID WPA2 network with a passphrase. This method simply initiates the connection
	 * sequence and does not wait for the connection to complete or fail. You need to call connectionStatus()
	 * to poll the connection to see if it succeeds or fails. Connecting can take time if the WPA2 key
	 * needs to be re-generated and stored on the NXT2WIFI. Allow up to 30 seconds for this to complete.
	 * 
	 * @param ssid - SSID of the network
	 * @param passphrase - the passphrase to use
	 * @param saveConfig - If true then the WPA configuration is saved as the default Custom Profile on the device.
	 * @throws IOException if an error occurred in connecting to the network
	 */
	public void connectToWPA2WithPassphrase(String ssid, String passphrase, boolean saveConfig) throws IOException {
		
		String cmd;
		
		if(debug) RConsole.println("connectToWPA2: SSID= "+ssid+" passphrase= "+passphrase);

		if(saveConfig) {
			// Delete the custom profile $WFKD
			cmd = "$WFKD\n";
			tryCommand(cmd, "Cannot delete custom profile");
		}
		
		// Set the security type and passphrase
		cmd = "$WFS?";
		cmd = cmd + WF_SEC_WPA2_PASSPHRASE + ":" + passphrase + "\n";
		tryCommand(cmd, "Cannot set security type and passphrase");

		// Set the SSID
		cmd = "$WFE?SSID=" + ssid + "\n";
		tryCommand(cmd, "Cannot set SSID");
		
		// Set infrastructure mode
		setType(INFRASTRUCTURE);
		cmd = "$WFE?TYPE=0\n";
		tryCommand(cmd, "Cannot set infrastructure mode");

		if(saveConfig) {
			// Save the profile
			cmd = "$WFKS\n";
			tryCommand(cmd, "Cannot save profile");
		}
		
		// Connect to the wifi
		cmd = "$WFC1\n";
		tryCommand(cmd, "Cannot connect");

	}
	
	/**
	 * Create an Ad-Hoc network
	 * Not yet implemented.
	 */
	public void createAdHoc(String IPAddress, String netmask, String DNS, String gateway, String SSID) throws IOException {
				
		// not yet implemented
	}
	
	/**
	 * Connect to the wifi defined in the custom profile stored on the device. This will attempt to re-connect to the last
	 * known wifi settings saved in the custom profile. This allows you to rapidly re-start communications by reconnecting
	 * to the last known good wifi.
	 * @throws IOException if the connection fails
	 */
	public void reconnect() throws IOException {
		String cmd = "$WFC1\n";
		tryCommand(cmd, 20000, "Cannot reconnect");
	}
	
	/**
	 * Get the current connection status. Returns the following values:
	 * NOT_CONNECTED	0 
	 * CONNECTING	1 
	 * CONNECTED	2 
	 * CONNECTION_LOST 3 
	 * CONNECTION_FAILED 4 
	 * STOPPING	5 
	 * TURNED_OFF	6
	 * @return The current status, or -1 if an error occurred in parsing
	 */
	public int connectionStatus() {
		
		int status = -1;
		
		String cmd = "$WFGS\n";
		RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
		Delay.msDelay(50);
		String reply = readFully(false);
		
		if(reply.length() > 5 && reply.startsWith("WFGS=") ) {
			status = reply.charAt(5) - 48;
		} else {
			status = -1;
		}
		
		if(debug) RConsole.println("connectionStatus returning " + status);
		
		return status;
	}
	
	/**
	 * Convert the connection status value into a string that can be printed
	 * @param status - The status returned by connectionStatus
	 * @return A string representing the connection status value
	 * @throws IllegalArgumentException if an invalid status code is provided
	 */
	public String connectionStatusToString(int status) throws IllegalArgumentException {
		
		if(status < 0 || status > connectionStatuses.length) {
			throw new IllegalArgumentException("Index out of range");
		}
		
		return connectionStatuses[status];
	}
	
	/**
	 * Stop an in-progress connection to a wifi network. Use this if the NXT2WIFI is in the process
	 * of connecting to a network and you want to halt that.
	 * @returns The reply from the NXT2WIFI
	 */
	public String stopConnecting() {
		String cmd = "$WFQ\n";
		RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
		Delay.msDelay(50);
		String reply = readFully(false);
		if(debug) RConsole.println("stopConnecting Reply = " + reply);
		
		return reply;	
	}
	
	/**
	 * Disconnect from a wireless network. 
	 * @returns The reply string from the NXT2WIFI sensor
	 */
	public String disconnectFromWifi() {
		
		String cmd = "$WFX\n";
		RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
		Delay.msDelay(50);
		String reply = readFully(false);
		if(debug) RConsole.println("disconnectFromWifi Reply = " + reply);
		
		return reply;
	}
	
	/**
	 * Set an IP address manually on the NXT2WIFI. Takes the IP address in dotted-decimal format
	 * as a string.
	 * @param address The IP address to set. E.g. 192.168.1.23
	 * @return The reply from the NXT2WIFI sensor
	 */
	public String setIPAddress(String address) {
		String cmd = "$WFE?IPAD="+address+"\n";
		RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
		Delay.msDelay(50);
		String reply = readFully(false);		
		return reply;
	}
	
	/**
	 * Set the netmask manually on the NXT2WIFI. Takes the netmask in dotted-decimal format as a string
	 * @param address The netmask address to use. E.g. 255.255.255.0
	 * @return The reply from the NXT2WIFI sensor
	 */
	public String setMask(String address) {
		String cmd = "$WFE?MASK="+address+"\n";
		RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
		Delay.msDelay(50);
		String reply = readFully(false);		
		return reply;
	}

	/**
	 * Set the default gateway manually on the NXT2WIFI. Takes the gateway as a dotted-decimal format
	 * string. E.g. 192.168.1.1
	 * @param address The gateway address to set
	 * @return The reply from the NXT2WIFI
	 */
	public String setGateway(String address) {
		String cmd = "$WFE?GWAY="+address+"\n";
		RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
		Delay.msDelay(50);
		String reply = readFully(false);		
		return reply;
	}
	
	/**
	 * Set the DNS server manually on the NXT2WIFI. Takes the address as a dotted-decimal format
	 * string. E.g. 192.168.1.1
	 * @param address The DNS server address to set
	 * @return The reply from the NXT2WIFI
	 */
	public String setDNS1(String address) {
		String cmd = "$WFE?DNS1="+address+"\n";
		RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
		Delay.msDelay(50);
		String reply = readFully(false);		
		return reply;
	}
	
	/**
	 * Set the DNS server manually on the NXT2WIFI. Takes the address as a dotted-decimal format
	 * string. E.g. 192.168.1.1
	 * @param address The DNS server address to set
	 * @return The reply from the NXT2WIFI
	 */
	public String setDNS2(String address) {
		String cmd = "$WFE?DNS2="+address+"\n";
		RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
		Delay.msDelay(50);
		String reply = readFully(false);		
		return reply;
	}
	
	/**
	 * Set the SSID of the wifi network to connect to manually. 
	 * @param name The name of the wifi network to connect to.
	 * @return The reply from the NXT2WIFI
	 */
	public String setSSID(String name) {
		String cmd = "$WFE?SSID="+name+"\n";
		RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
		Delay.msDelay(50);
		String reply = readFully(false);		
		return reply;
	}
	
	/**
	 * Enable the DHCP for the wifi network connection. 
	 * @param e if true, enables DHCP, if false maintains specified IP.
	 * @return The reply from the NXT2WIFI
	 */	
	public String setDHCP(boolean e) {
		String cmd = "$WFE?DHCP="+(e?"1":"0")+"\n";
		RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
		Delay.msDelay(50);
		String reply = readFully(false);		
		return reply;		
	}	
	
	/**
	 * Set the type for the wifi network connection. 
	 * @param e if true is adhoc network, false is infrastructure
	 * @return The reply from the NXT2WIFI
	 */	
	public String setType(boolean e) {
		String cmd = "$WFE?TYPE="+(e?"1":"0")+"\n";
		RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
		Delay.msDelay(50);
		String reply = readFully(false);		
		return reply;		
	}	
	
	/**
	 * Set the NetBIOS name of the wifi network. 
	 * @param name The NetBIOS name to set.
	 * @return The reply from the NXT2WIFI
	 */
	public String setNetbiosName(String name) {
		String cmd = "$WFE?NAME="+name+"\n";
		RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
		Delay.msDelay(50);
		String reply = readFully(false);		
		return reply;
	}

	/**
	 * Reads the all available data from the RS485 rx buffer. Assumes that the data is in binary format
	 * so can be used to receive both ASCII and binary data.
	 * 
	 * @param wait Wait for answer if the buffer is empty
	 * @param cbuf Buffer to hold the data read in
	 * @param off Offset to start reading data into the cbuf array at
	 * @param len The number of bytes to read into cbuf. Bytes are stored from cbuf[0] onwards
	 * @return The number of bytes read
	 * @throws ArrayIndexOutOfBoundsException if off+len > length of the cbuf array
	 */
	private int readBytesFully(boolean wait, byte[] cbuf, int off, int len) throws ArrayIndexOutOfBoundsException {
		boolean done = false;
		int p = off;
		int bytesRemaining = len;
		int avail;
		
		if((off + len) > cbuf.length)
			throw new ArrayIndexOutOfBoundsException();
		
		while(!done && (bytesRemaining > 0)){
			if( (avail = RS485.hsRead(cbuf, p, bytesRemaining)) > 0){
				bytesRemaining -= avail;
				p += avail;
				Delay.msDelay(5);
			}else{
				if(!wait || (wait && p>0)){
					done = true;
				}
			}
			Thread.yield();
		}
		
		return p;
	}

	/**
	 * This method reads the all available data from the RS485 rx buffer
	 * Assumes that the data is in String format, does not read binary data
	 * 
	 * @param wait - Wait for answer if the buffer is empty!
	 * @return The string from the RS485 rx buffer
	 */
	private String readFully(boolean wait) {
		return this.readFully(wait, 512);	// danger, magic number
	}
	
	/**
	 * This method reads the all available data from the RS485 rx buffer
	 * Assumes that the data is in String format, does not read binary data
	 * Will only read up to bytesToRead bytes from the RS485 line
	 * Should be used for reading status replies from the NXT2WIFI and not
	 * for reading data sent by a remote caller as it will not handle binary data.
	 * 
	 * @param wait - Wait for answer if the buffer is empty
	 * @param bytesToRead Number of bytes to return in the string. If 0 attempts to read all available bytes
	 * @return The string from the RS485 rx buffer
	 */
	private String readFully(boolean wait, int bytesToRead){
		String response = "";
		int bytesRead;
		byte[] buf = new byte[1];
		
		boolean done = false;
		bytesRead = 0;
		
		while(!done && (bytesRead < bytesToRead)){
			if(RS485.hsRead(buf, 0, 1) > 0){
				response += (char)buf[0];
				bytesRead++;
				Delay.msDelay(5);
			}else{
				if(!wait || (wait && response.length()>0)){
					done = true;
				}
			}
			Thread.yield();
		}
		//RConsole.println("readFully returning " + response);
		return response;
	}

	/**
	 * Clear data from the read buffer on the NXT
	 * Code copied from BrickIt.dk
	 */
	private void clearReadBuffer(){
		byte[] data = new byte[] {13};
		RS485.hsWrite(data, 0, 1);
		Delay.msDelay(100);
		
		//Empty buffer:
		while (RS485.hsRead(data, 0, 1) > 0);
		Delay.msDelay(100);
	}
	
	/**
	 * Returns true if a given string is in IP address dotted-decimal format
	 * Code copied from BrickIt.dk
	 */
	private boolean isIP(String s){
		int dotCount = 0, digitCount = 0;
		for (char c : s.toCharArray()) {
			if(c >= '0' && c <= '9') digitCount++;
			else if(c == '.'){
				if(digitCount > 0 && digitCount <= 3){
					digitCount = 0;
					if(dotCount < 3){
						dotCount++;
					}else return false;
				}else return false;
			}else return false;
		}
		if(dotCount == 3 && digitCount > 0 && digitCount <= 3) return true;
		return false;
	}
	
	private int send(byte[] out, int len) {
		int written = 0;
		int bo = 1;
		//RConsole.println("Send to Flyport: "+out);
		while (written<len && bo>0) {
			bo = RS485.hsWrite(out, written, len);
			written += bo;
			Delay.msDelay(1);
		}
		//bo = RS485.hsWrite(terminator, written+1, 1);
		//written += bo;
		return written;
	}
	
	private int send(String out) {
		return send(out.getBytes(),out.length());
	}
		
	/**
	 * Class for reading from the NXT2WIFI, returns a standard Java InputStream that can be
	 * used as an input source. Preserves the Java 1.5 standard InputStream contract as far
	 * as is possible given the NXT2WIFI implementation.
	 * 
	 * @author mcrosbie
	 *
	 */
	protected class NXT2WIFIInputStream extends InputStream {

		private int socketID;
		
		NXT2WIFIInputStream(int s) {
			socketID = s;
		}
		
		/**
		 * Returns the number of bytes that can be read (or skipped over) from this
		 * input stream without blocking by the next caller of a method for this input
		 * stream. The next caller might be the same thread or or another thread.
		 *
		 * @return the number of bytes that can be read from this input stream without blocking.
		 *
		 */
		public int available() throws IOException {
			synchronized (NXT2WIFI.this){
				
				int status;
				
				if(!NXT2WIFI.this.socketOpen[socketID])
					throw new IOException("Socket closed");
				
				String cmd = "$TCPL" + socketID + "\n";
				RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
				Delay.msDelay(50);
				
				String reply = readFully(false);
				
				if(reply.startsWith("TCPL") && reply.length() > 6) {
					int equalsSign = reply.indexOf('=');
					String bytesAvailable = reply.substring(equalsSign+1);
					status = Integer.parseInt(bytesAvailable);
				} else {
					status = 0;
				}
								
				return status;
			}
		}

		/**
		 * Close the stream. Once a stream has been closed, further read(), available(),
		 * mark(), or reset() invocations will throw an IOException. Closing a
		 * previously-closed stream, however, has no effect.
		 * Detaches the client from the socket and closes the socket.
		 */
		public void close()  {
			synchronized (NXT2WIFI.this){

				String cmd = "$TCPX" + socketID + "\n";
				RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
				Delay.msDelay(50);
				
				String reply = readFully(false);
				
				NXT2WIFI.this.socketOpen[socketID] = false;
			}
		}
		
		/**
		 * Not implemented - read ahead is not supported
		 */
		public void mark(int readAheadLimit) {
		}

		/**
		 * Tell whether this stream supports the mark() operation.
		 *
		 * @return false, mark is NOT supported.
		 */
		public boolean markSupported() {
			return false;
		}

		/**
		 * Read a single byte.
		 * This method will block until a byte is available, an I/O error occurs,
		 * or the end of the stream is reached.
		 *
		 * @return The byte read, as an integer in the range 0 to 255 (0x00-0xff)
		 *
		 */
		public int read()  throws IOException {
			
			synchronized (NXT2WIFI.this){
				if(!NXT2WIFI.this.socketOpen[socketID])
					throw new IOException("Socket closed");
				
				byte buf[] = new byte[1];			
				this.read(buf, 0, 1);		
				return buf[0] & 0xFF;
			}
		}
		
		/**
		 * Read bytes into an array.
		 * This method will block until some input is available,
		 * an I/O error occurs, or the end of the stream is reached.
		 *
		 * @param cbuf Destination buffer.
		 * @return The number of bytes read
		 *
		 */
		@Override
		public int read(byte[] cbuf) throws IOException {
			synchronized (NXT2WIFI.this){
				return read(cbuf, 0, cbuf.length);
			}
		}

		/**
		 * Read bytes into a portion of an array.
		 * This method will block until some input is available,
		 * an I/O error occurs, or the end of the stream is reached.
		 *
		 * @param cbuf Destination buffer.
		 * @param off Offset at which to start storing bytes.
		 * @param len Maximum number of bytes to read.
		 * @return The number of bytes read, or -1 if the end of
		 *   the stream has been reached
		 * @throws IOException if the stream is closed.
		 *
		 */
		@Override
		public int read(byte[] cbuf, int off, int len) throws IOException {
			
			synchronized(NXT2WIFI.this) {
			
				if(!NXT2WIFI.this.socketOpen[socketID])
					throw new IOException("Socket closed");
					
				String cmd = "$TCPR" + socketID + "?"+ len +"\n";
				RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
				Delay.msDelay(50);
		
				// next we read in the reply and parse the header to know how many bytes were returned
				String replyHeader = readFully(true, 6);
								
				// Reply in format TCPR<S>=<len>,<data>,
				// E.g. TCPR1=1,h
				// Always expect <len> to be 1, as we only asked for 1 byte...
				// discard anything beyond the first character returned
				if(!replyHeader.startsWith("TCPR")) {
					throw new IOException("Parse error");
				}
				
				// Next we consume bytes until a comma is reached
				String replyByteCount = "";
				byte buf[] = new byte[1];
				int bytesRead = RS485.hsRead(buf, 0, 1);
				while( bytesRead > 0) {
					if(buf[0] == ',') {
						// reached a comma, so stop reading
						break;
					}
					// otherwise build up the reply length string
					replyByteCount += (char)buf[0];
					bytesRead = RS485.hsRead(buf, 0, 1);
				}
				
				int byteCount = Integer.parseInt(replyByteCount);
				
				// read the remainder of the data
				return readBytesFully(true, cbuf, off, byteCount);
			}
		}
			
		/**
		 * Reset the stream. Not implemented.
		 *
		 */
		public void reset()  {
			
		}

		/**
		 * Skip bytes.
		 * Not implemented
		 */
		public long skip(long n) {
			return 0;
		}

	}

	/**
	 * Class for writing data to an output stream connected to a socket on the NXT2WIFI
	 */

	protected class NXT2WIFIOutputStream extends OutputStream {
		
		private int socketID;
		
		NXT2WIFIOutputStream(int s) {
			socketID = s;
			NXT2WIFI.this.socketOpen[s] = true;
		}
		
		void setSocketID(int s) {
			socketID = s;
		}

		/**
		 * Close the output stream socket
		 *
		 */
		@Override
		public void close() throws IOException {
			synchronized (NXT2WIFI.this){

				String cmd = "$TCPX" + socketID + "\n";
				RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
				Delay.msDelay(50);
				
				String reply = readFully(false);
				
				NXT2WIFI.this.socketOpen[socketID] = false;
			}
		}
		
		/**
		 * Flush the stream.
		 */
		@Override
		public void flush() throws IOException {
			synchronized (NXT2WIFI.this){
				
				if(!NXT2WIFI.this.socketOpen[socketID])
					throw new IOException("Socket closed");
				
				String cmd = "$TCPF" + socketID + "\n";
				RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
				Delay.msDelay(50);
				
				String reply = readFully(false);				
			}
		}

		/**
		 * Write an array of bytes.
		 * If the buffer allows blocking writes, this method will make a best effort
		 * to write the data.
		 * @param cbuf Array of bytes to be written
		 * @return The number of bytes written
		 * @throws IOException if the stream is closed, or the write is interrupted.
		 *
		 */
		@Override
		public void write(byte[] cbuf) throws IOException {
			this.write(cbuf, 0, cbuf.length);	
		}

		/**
		 * Write a portion of an array of bytes.
		 * If the buffer allows blocking writes, this method will block until
		 * all the data has been written rather than throw an IOException.
		 *
		 * @param cbuf Array of bytes
		 * @param off Offset from which to start writing bytes
		 * @param len - Number of bytes to write
		 * @return The number of bytes written (as reported by the NXT2WIFI
		 * @throws IOException if the stream is closed, or the write is blocked
		 */
		@Override
		public void write(byte[] cbuf, int off, int len) throws IOException, ArrayIndexOutOfBoundsException {
			synchronized(NXT2WIFI.this) {
			
				if(!NXT2WIFI.this.socketOpen[socketID])
					throw new IOException("Socket closed");
				
				if((off+len) > cbuf.length) {
					throw new ArrayIndexOutOfBoundsException();
				}
				
				RConsole.println("write: off="+off+" len="+len);
				
				// build the full data packet in one array
				String cmd = "$TCPW" + socketID + "?"+ len +",";
				
				byte cmdBytes[] = new byte[cmd.length() + len];
				System.arraycopy(cmd.getBytes(), 0, cmdBytes, 0, cmd.length());
				
				// now copy in the data chunk
				System.arraycopy(cbuf, off, cmdBytes, cmd.length(), len);
				
				RS485.hsWrite(cmdBytes, 0, cmdBytes.length);
				Delay.msDelay(50);
				
				// next we read in the reply and parse the header to know how many bytes were actually written
				String replyHeader = readFully(true);
				
				if(debug) RConsole.println("write: replyHeader="+replyHeader);
				
			}			
		}
			
		/**
		 * Write a single byte.
		 * 
		 * @param c The byte value to write
		 * @throws IOException if the stream is closed, or the write is interrupted.
		 *
		 */
		@Override
		public void write(int c) throws IOException {
		
			synchronized(NXT2WIFI.this) {
				byte buf[] = new byte[1];
				buf[0] = (byte)(c & 0xFF);
				this.write(buf, 0, 1);		
			}
		}		
	}

}
