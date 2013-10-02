package lejos.nxt.addon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import lejos.nxt.comm.RS485;
import lejos.util.Delay;
//RConsole;

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
 * @author dbenedettelli
 * @author daniele@benedettelli.com
 * @version 1.1 Revision, correction, implementation completion, corrected bugs in readFully
 * 
 * @author BB
 * @author bbagnall@mts.net
 * @version 1.2 Added web event listener code (and coded DemoRobot sample)
 * 
 * @author dbenedettelli
 * @author daniele@benedettelli.com
 * @version 1.3 Revision, removed RConsole for lighter binary, fixed bug in Web Monitor that prevented other commands to be processed 
 * 
 */
public class NXT2WIFI {
	
	/** The current baudrateIndex */
	private int currentBaudrate;
	private final static int BUFFER_LENGTH = 100;
	
	private final static String RESP_PING = "!!!";
	
	// WIFI_ security modes
	public final static int WF_SEC_OPEN =			0;
	public static final int WF_SEC_WEP_40 =			1;
	public static final int WF_SEC_WEP_104 =		2;
	public static final int WF_SEC_WPA_KEY =		3;
	public static final int WF_SEC_WPA_PASSPHRASE = 4;
	public static final int WF_SEC_WPA2_KEY =		5;
	public static final int WF_SEC_WPA2_PASSPHRASE = 6;
	public static final int WF_SEC_WPA_AUTO_KEY = 7;
	public static final int WF_SEC_WPA_AUTO_PASSPHRASE = 8;

	// connection status codes
	public static final int NOT_CONNECTED = 0;
	public static final int CONNECTING = 1;
	public static final int CONNECTED = 2;
	public static final int CONNECTION_LOST = 3;
	public static final int CONNECTION_FAILED= 4;
	public static final int STOPPING=5;
	public static final int TURNED_OFF=6;
	
	// Web event codes
	public static final int WEB_CTRL_BTN = 0; 	/* Webpage Widget Button */
	public static final int WEB_CTRL_SLD = 1; 	/* Webpage Widget Slider */
	public static final int WEB_CTRL_CHK = 2; 	/* Webpage widget Checkbox */
	
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
	
	private static final int cmdDelay = 100; // how long to wait for a reply, in ms
	
	private final int NUM_SOCKETS = 4;	// maximum number of sockets supported on the NXT2WIFI
	public final static int TCP = 1;
	public final static int UDP = 0;
	private final static int INVALID = -1;
	private final String UDP_STRING = "UDP";
	private final String TCP_STRING = "TCP";	
	
	private boolean socketOpen[];
	private int socketType[];
	
	private static int defaultBaudRate = 230400;	// Change this to change the default baud rate
	private boolean debug = false;	// Wait for remote console if true

	/**
	 * Default constructor. Assumes baud rate of 230400 and no debug output to //RConsole.
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
		socketType = new int[NUM_SOCKETS];
		int i;
		for(i=0; i < NUM_SOCKETS; i++) { 
			socketOpen[i] = false;
			socketType[i] = INVALID;
		}

		//if(debug) RConsole.println("NXT2WIFI initialized");
	}
	
///////////////////////////////////////////////////////////////////////////////
////////////////////////// LOW LEVEL COMMUNICATION ////////////////////////////
///////////////////////////////////////////////////////////////////////////////

	
	/**
	 * Reads the all available data from the RS485 rx buffer. Assumes that the data is in binary format
	 * so can be used to receive both ASCII and binary data.
	 * 
	 * @param wait Wait for answer if the buffer is empty
	 * @param cbuf Buffer to hold the data read in
	 * @param off Offset to start reading data into the cbuf array at
	 * @param len The number of bytes to read into cbuf. Bytes are stored from cbuf[0] onwards
	 * @return The number of bytes read
	 */
	private int readBytesFully(boolean wait, byte[] cbuf, int off, int len) {
		boolean done = false;
		int p = off;
		int bytesRemaining = len;
		int avail;
		long time = System.currentTimeMillis();
		
		while(!done && (bytesRemaining > 0)){
			if( (avail = RS485.hsRead(cbuf, p, bytesRemaining)) > 0){
				bytesRemaining -= avail;
				p += avail;
				time = System.currentTimeMillis();
				Delay.msDelay(5);
			}else{
				if(!wait || (wait && (p>0 || System.currentTimeMillis()-time>cmdDelay ))){
					done = true;
					//if (wait) System.out.println("timeout!");					
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
		long time = System.currentTimeMillis();
		
		boolean done = false;
		bytesRead = 0;
		int br = 0;
		
		while(!done && (bytesRead < bytesToRead)){
			br = RS485.hsRead(buf, 0, 1);
			if(br > 0){
				response += (char)buf[0];
				bytesRead++;
				time = System.currentTimeMillis();
				Delay.msDelay(5);
			}else{
				if(!wait || (wait && ( response.length()>0 || (System.currentTimeMillis()-time>cmdDelay) ))){
					done = true;
				}
			}
			Thread.yield();
		}
		return response;
	}

	/**
	 * Clear data from the read buffer on the NXT
	 * Code copied from BrickIt.dk
	 */
	public void clearReadBuffer(){
		byte[] data = new byte[] {13};
		RS485.hsWrite(data, 0, 1);
		Delay.msDelay(100);
		
		//Empty buffer:
		while (RS485.hsRead(data, 0, 1) > 0);
		Delay.msDelay(100);
	}	
	
	protected int send(byte[] out, int len) {
		int written = 0;
		int bo = 1;
		
		while (written<len && bo>0) {
			bo = RS485.hsWrite(out, written, len);
			written += bo;
			Delay.msDelay(1);
		}
		return written;
	}
	
	protected int send(String out) {
		return send(out.getBytes(),out.length());
	}

	private String commandWithReply(String cmd) {
		return this.commandWithReply(cmd, cmdDelay);
	}

	/**
	 * Run a command and get the reply 
	 * @param cmd The string to send to the NXT2WIFI 
	 * @param timeout How long to wait for a reply (in ms)
	 */
	private String commandWithReply(String cmd, int timeout) {
		suspendWebMonitor = true;
		send(cmd);
		Delay.msDelay(timeout);
		String reply = readFully(false);
		suspendWebMonitor = false;
		// parse the reply to locate the '=' and extract everything from that point on as the reply
		int equals = reply.indexOf("=")+1;
		String ret = reply.substring(equals);
			
		return ret;
	}	
	

	/**
	 * Find the "=" char in a replied string and returns the integer value.
	 * @param str the string that contains the integer to be retrieved 
	 * @return the integer in the reply, or -1 if failed
	 */
	int parseIntResult(String str) {
		int val = -1;
		int equalsSign = str.indexOf('=');
		String valStr = str.substring(equalsSign+1);
		try {
			val = Integer.parseInt(valStr);
		} catch (NumberFormatException e) {};
		return val;
	}
	
	/**
	 * Find the "=" char in a replied string and returns the integer value.
	 * @param str the string that contains the integer to be retrieved 
	 * @return the integer in the reply, or -1 if failed
	 */
	String parseStringResult(String str) {
		String valStr = "";
		int equalsSign = str.indexOf('=');
		valStr = str.substring(equalsSign+1);
		return valStr;
	}	

///////////////////////////////////////////////////////////////////////////////
////////////////////////////WEB EVENT CODE/////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
	private MonitorWebEvents mon = null;
	private ArrayList<NXT2WiFiListener> listeners= new ArrayList<NXT2WiFiListener>();
	private boolean suspendWebMonitor = false;
	
	/**
	 * Registers this class to listen for Web Events from an NXT2WiFiListener.
	 * @param listener
	 */
	public void addListener(NXT2WiFiListener listener) {
		if(mon == null) {
			mon = new MonitorWebEvents();
			mon.setDaemon(true);
			mon.start();
		}
		listeners.add(listener);
		suspendWebMonitor = false;
	}
	
	// When listener first added, create and start monitoring thread? Better to be always running and consuming?
	private class MonitorWebEvents extends Thread {
		byte[] cbuf = new byte[20];
		public void run() {
			while(true) {
				if (!suspendWebMonitor) {
					readBytesFully(true, cbuf, 0, 20); // true = blocking. Why 20? Why not 7?
					if (isWebEvent(cbuf)) { // Indicates web event
						byte controlType = cbuf[3]; // e.g button = 0
						byte controlID = cbuf[4]; // e.g. 1 = button 1 on page
						byte event = cbuf[5]; // e.g. 0 = button down, 1 = button up
						byte value = cbuf[6]; // value of the widget
						for(NXT2WiFiListener ls:listeners) 
							ls.webEventReceived(controlType, controlID, event, value);
					}
				} else {
					Delay.msDelay(100);
				}
			}
		}
	}
	
///////////////////////////////////////////////////////////////////////////////
//////////////////////////// UTILITIES ////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

	

	/**
	 * Returns true if a given string is in IP address dotted-decimal format
	 * Code copied from BrickIt.dk
	 */
	private boolean checkIPFormat(String s){
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
	
	/**
	 * Enable or disable debug output on LejOS USB console. Output is written to the //RConsole which must be opened
	 * prior to calling.
	 * @param debugMode Boolean flag if true debug output is enabled, if false it is disabled
	 */
	public void setConsoleDebug(boolean debugMode) {
		debug = debugMode;
		if(debug) {
			//RConsole.println("NXT2WIFI: DEBUG ENABLED at " + System.currentTimeMillis());
		}
	}
	
	/**
	 * Enable or disable debug output on virtual serial terminal (115200, 8N1)
	 * @param e Boolean flag, if true computer debug output is enabled, if false it is disabled
	 */
	public void setTerminalDebug(boolean e) {
		if (e)
			commandWithReply("$DBG1\n");
		else
			commandWithReply("$DBG0\n");
	}
	
	/**
	 * Return the current time of day and date string
	 * @param k is the offset from GMT (positive or negative)
	 * @return YYYY-MM-DD/hh:mm:ss or blank string if an error occurs
	 */
	public String getTimeAndDate(int k) {
		String reply = commandWithReply("$GTIM?"+k+"\n",50);
		if(reply.startsWith("GTIM")) {
			return parseStringResult(reply);
		} else {
			return "";
		}
	}	
	
	public String getTimeOfDay(int k) {
		String reply = commandWithReply("$GTIM?"+k+"\n",50);
		
		if(reply.startsWith("GTIM")) {
			int sep = reply.indexOf('/');
			return reply.substring(sep+1);
		} else {
			return "";
		}
	}	
	
	public String getDate(int k) {
		String reply = commandWithReply("$GTIM?"+k+"\n",50);
		
		if(reply.startsWith("GTIM")) {
			int eq = reply.indexOf('=');
			int sep = reply.indexOf('/');
			return reply.substring(eq+1,sep);
		} else {
			return "";
		}
	}		
	
	/**
	 * Return true if the NXT2WIFI sensor is alive and can respond to a basic PING command
	 * 
	 * @return true if the sensor is attached and communicating, false otherwise.
	 */
	public boolean isAlive() {
		String reply = commandWithReply("$???\n",50);
				
		if(reply.equalsIgnoreCase(RESP_PING)) {
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
		return parseStringResult(commandWithReply("$FW\n",50));
	}	

///////////////////////////////////////////////////////////////////////////////
////////////////////////////// WEBSERVER MANAGEMENT ///////////////////////////
///////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Update a Label on the webpage
	 * @param id the label id
 	 * @param data the string to be written in the label
 	 * @param datalen the number of bytes to be written (0 for automatic string length)
 	 * @return true if successful
	 */		
	public boolean updateWebLabel(int id, String data, int datalen) {
		if (datalen == 0) datalen = data.length();
		return (parseIntResult(commandWithReply("$WEBLBL"+id+"?"+datalen+","+data+"\n"))==0);
	}

	/**
	 * Update a Button on the webpage
	 * @param id the button id
 	 * @param state true for pressed, false for released
 	 * @return true if successful
	 */	
	public boolean updateWebButton(int id, boolean state) {
		return (parseIntResult(commandWithReply("$WEBBTN"+id+"?"+(state?"1":"0")+"\n"))==0);
	}

	/**
	 * Update a Checkbox on the webpage
	 * @param id the checkbox id
 	 * @param state true for checked, false for unchecked
 	 * @return true if successful
	 */	
	public boolean updateWebCheckBox(int id, boolean state) {
		return (parseIntResult(commandWithReply("$WEBCHK"+id+"?"+(state?"1":"0")+"\n"))==0);
	}

	/**
	 * Update a Bar graph on the webpage
	 * @param id the bar graph id
 	 * @param value the bar graph value to display (0-100)
 	 * @return true if successful
	 */
	public boolean updateWebBargraph(int id, int value) {
		return (parseIntResult(commandWithReply("$WEBBAR"+id+"?"+value+"\n"))==0);
	}

	/**
	 * Update a Slider on the webpage
	 * @param id the bar graph id
 	 * @param value the slider position
 	 * @return true if successful
	 */
	public boolean updateWebSlider(int id, int value) {
		return (parseIntResult(commandWithReply("$WEBLSD"+id+"?"+value+"\n"))==0);
	}

	/**
	 * Enable/disable webserver functionality <br>
	 * (spontaneous messages generated by events on the webpage)
	 * @return true if successful
	 */
	public boolean enableWebserver(boolean en) {
		return (parseIntResult(commandWithReply("$SRV"+(en?"1":"0")+"\n"))==0);
	}

	/**
	 * Reset and clear all widgets on the webpage
	 */
	public boolean clearAllWebFields() {
		return (parseIntResult(commandWithReply("$WEBCLR\n"))==0);
	}	
	
	/**
	 * Accepts a byte buffer filled by serial port data and returns true
	 * if the data contains a Webpage Event Direct Command.
	 * 0x00 0x80 0x14 [ID] [EVENT] [VAL] 
	 * [ID] = 0...2 type of widget that generated the event 
	 * [EVENT] = 0...255 event
	 * [VAL] = 0...255 value of the widget
	 * 
	 */
	public boolean isWebEvent(byte cmd[]) {
		int len = cmd.length;
		if (len<5) return false;
		if (cmd[0]==0x00 && cmd[2]==0x14) return true;
		else return false;
	}		
	

///////////////////////////////////////////////////////////////////////////////
//////////////////////////////////SOCKETS /////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Return an Input stream for receiving data from the socket. If this socket isn't opened then you'll get
	 * garbage back. If the remote side has closed the connection then you'll get nothing from the socket.
	 * @param socketID The socket to read data from
	 * @return an input stream for the NXT2WIFI (returns null if id is invalid)
	 */
	public InputStream getInputStream(int socketID) {
		
		if( (socketID < 1) || (socketID > NUM_SOCKETS) ) {
			return null;
		}
		
		if ( socketType[socketID]!=UDP && socketType[socketID]!=TCP) {
			return null;
		}
	
		return new NXT2WIFIInputStream(socketID,socketType[socketID]);
	}

	/**
	 * Return an Output stream for writing to the socket
	 * @param socketID The socket to write data to
	 * @param kind The socket kind (0 for UDP, 1 for TCP)
	 * @return an output stream for the NXT2WIFI
	 */
	public OutputStream getOutputStream(int socketID, int kind) {
		if( (socketID < 0) || (socketID > NUM_SOCKETS) ) {
			return null;
		}
		
		return new NXT2WIFIOutputStream(socketID,kind);
	}

	private String socketTypeString(int kind) {
		if (kind==UDP) return UDP_STRING;
		if (kind==TCP) return TCP_STRING;
		return "";
	}
	
	/**
	 * Start a server listening on a given port. Once the socket is opened the InputStream and OutputStream
	 * methods should be used to read/write data from the socket.
	 * 
	 * @param kind 0 for UDP, 1 for TCP
	 * @param port The port to open the server on
	 * @param socketID The socket to associate with this port (1-4)
	 * @return true if socket was opened successfully, false otherwise
	 */
	public boolean openServerSocket(int kind, int port, int socketID) {
		if( (port < 0) || (port > 65535)) {
			return false;
		}
		
		if( (socketID < 1) || (socketID > NUM_SOCKETS)) {
			return false;
		}
		
		//if(debug) RConsole.println("serverSocket: port="+port + " socketID=" + socketID);
		
		String cmd = "$"+socketTypeString(kind)+"OS"+socketID+"?"+port+"\n";
		
		if (parseIntResult(commandWithReply(cmd))>0) {
			socketType[socketID] = kind;
			socketOpen[socketID] = true;
			return true;
		} else return false;
	}

	/**
	 * Detach remote TCP client (valid for server socket only)
	 * @param id socket internal id 1,2,3,4 (must be a server socket)
	 * @return true if successful, false otherwise
	*/
	public boolean detachClientSocket(int socketID) {
		if( (socketID < 1) || (socketID > NUM_SOCKETS)) {
			return false;
		}
		return parseIntResult(commandWithReply("$TCPD"+socketID+"\n"))>0;
	}	
	
	/**
	 * Open a client socket to a given host on a given port. Use the InputStream and OutputStream
	 * methods to read and write data from the connection.
	 * 
	 * @param kind 0 for UDP, 1 for TCP
	 * @param host The host name or IP address to open a socket on
	 * @param port The port of the remote host to to
	 * @param socketID The socket to associate with the connection (1-4)
	 * @return true if successfully created, false otherwise
	 */
	public boolean openClientSocket(int kind, String host, int port, int socketID) {
		if( (port < 0) || (port > 65535)) {
			return false;
		}
		
		if( (socketID < 1) || (socketID > NUM_SOCKETS)) {
			return false;
		}

		//if(debug) RConsole.println("clientSocket: host="+host + " port="+port + " socketID=" + socketID);

		String cmd = "$"+socketTypeString(kind)+"OC"+socketID+"?"+host+","+port+"\n";
		if (parseIntResult(commandWithReply(cmd))>0) {
			socketType[socketID] = kind;
			socketOpen[socketID] = true;
			return true;
		} else return false;
	}
	
	/**
	 * Open a UDP broadcast socket on a given port. Use the InputStream and OutputStream
	 * methods to read and write data from the connection.
	 * 
	 * @param port The port of the remote host to to
	 * @param socketID The socket to associate with the connection (1-4)
	 * @return true if successfully created, false otherwise
	 */
	public boolean openBroadcastSocket(int port, int socketID) {
		if( (port < 0) || (port > 65535)) {
			return false;
		}
		
		if( (socketID < 1) || (socketID > NUM_SOCKETS)) {
			return false;
		}

		if (parseIntResult(commandWithReply("$UDPOB"+socketID+"?"+port+"\n"))>0) {
			socketType[socketID] = UDP;
			socketOpen[socketID] = true;
			return true;
		} else return false;
	}	
	
	/**
	 * Send a string to a remote client on a given socket. Low-level method, recommend the 
	 * OutputStream methods be used instead.
	 * Assumes that the socket is open.
	 * @param msg String to send
	 * @param socketID Socket ID previously opened to send on
	 * @return the number of bytes written
	 */
	public int socketWrite(String msg, int socketID) {
		if( (socketID < 1) || (socketID > NUM_SOCKETS)) return 0;
		if (!socketOpen[socketID] || socketType[socketID]==INVALID) return 0;
		String cmd = "$"+socketTypeString(socketType[socketID])+"W"+socketID+"?"+msg.length()+","+msg;
		return parseIntResult(commandWithReply(cmd));
	}
	
	/**
	 * Close a socket. 
	 * @param socketID The ID of the socket to close (1-4)
	 */
	public boolean closeSocket(int socketID) {
		if( (socketID < 1) || (socketID > NUM_SOCKETS)) {
			return false;
		}
		if (!socketOpen[socketID] || socketType[socketID]==INVALID) return false;
		
		if (parseIntResult(commandWithReply("$"+socketTypeString(socketType[socketID])+"X"+socketID+"\n"))>0) {
			socketType[socketID] = INVALID;
			socketOpen[socketID] = false;
		return true;
		} else return false;
	}
	
	/**
	 * Check UDP socket input buffer overflow. If overflow occurs, some data might be lost.
	 *
	 * @param socketID the socket id
	 * @return true, if overflow occurred (, false 
	 * @throws IOException Signals that the socket id was invalid
	 */
	public boolean checkUDPOverflow(int socketID) {
		if( (socketID < 1) || (socketID > NUM_SOCKETS)) {
			return false;
		}		
		return parseIntResult(commandWithReply("$UDPV"+socketID+"\n"))>0;
	}

	/**
	 * Flush the UDP socket input buffer.
	 * @param id socket internal id 1,2,3,4
	 * @return true upon success
	 * @throws IOException 
	 */
	public boolean flushUDPSocket(int socketID) {
		if( (socketID < 1) || (socketID > NUM_SOCKETS)) {
			return false;
		}		
		return parseIntResult(commandWithReply("$UDPF"+socketID+"\n"))>0;
	}	

	/**
	 * Gets the MAC address of the remote TCP socket. 
	 *
	 * @param socketID the socket ID (1 to 4)
	 * @return the TCP remote mac
	 * @throws IOException Signals that the socket ID was invalid
	 */
	public String getTCPRemoteMAC(int socketID) {
		if( (socketID < 1) || (socketID > NUM_SOCKETS)) {
			return "wrong ID!";
		}		
		String reply = commandWithReply("$TCPSM"+socketID);
		if(reply.startsWith("TCPSM")) {
			return parseStringResult(reply);
		} else return "";
	}
	
	/**
	 * Gets the IP address of the remote TCP socket. 
	 *
	 * @param socketID the socket ID (1 to 4)
	 * @return the TCP remote mac
	 */
	public String getTCPRemoteIP(int socketID) {
		if( (socketID < 1) || (socketID > NUM_SOCKETS)) {
			return "wrong ID!";
		}		
		String reply = commandWithReply("$TCPSI"+socketID);
		if(reply.startsWith("TCPSI")) {
			return parseStringResult(reply);
		} else return "";
	}	
	
	/**
	 * Close all sockets 
	 */
	public void closeAllSockets() {
		for (int i = 1; i<=NUM_SOCKETS; i++) {
			closeSocket(i);
		}
	}	
	
	/**
	 * Close all TCP sockets 
	 */
	public boolean closeAllTCPSockets() {
		boolean ret = parseIntResult(commandWithReply("$TCPX0\n"))==0;
		for (int i = 1; i<=NUM_SOCKETS; i++) {
			socketType[i] = INVALID;
			socketOpen[i] = false;
		}
		return ret;
	}	

	/**
	 * Close all UDP sockets 
	 */
	public boolean closeAllUDPSockets() {
		boolean ret = parseIntResult(commandWithReply("$UDPX0\n"))==0;
		for (int i = 1; i<=NUM_SOCKETS; i++) {
			socketType[i] = INVALID;
			socketOpen[i] = false;
		}
		return ret;
	}		
	
///////////////////////////////////////////////////////////////////////////////
//////////////////////////////WIFI NETWORKING /////////////////////////////////
///////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Connect to custom or default profile.
	 * 
	 * @param profile - true for CUSTOM, false for DEFAULT
	 */	
	public boolean connect(boolean profile) {
		return parseIntResult(commandWithReply("$WFC"+(profile?"1":"0")+"\n"))==0;
	}
	
	/**
	 * Delete custom connection profile.
	 */	
	public boolean deleteProfile() {
		return parseIntResult(commandWithReply("$WFKD\n"))==0;
	}		
	
	/**
	 * Save custom connection profile.
	 */	
	public boolean saveProfile() {
		return parseIntResult(commandWithReply("$WFKS\n"))==0;
	}	
	
	/**
	 * Sets the Wi-Fi custom connection profile security. 
	 *
	 * @param mode the mode (see constants)
	 * @param passkey the passphrase or the passkey
	 * @return true, if command sent successfully
	 */
	public boolean setSecurity (int mode, String passkey) {
		return setSecurity(mode,passkey,0);
	}	
	
	/**
	 * Sets the Wi-Fi custom connection profile security. 
	 *
	 * @param mode the mode (see constants)
	 * @param passkey the passphrase or the passkey
	 * @param index the WEP key index
	 * @return true, if command sent successfully
	 */	
	public boolean setSecurity (int mode, String passkey, int index) {
		return parseIntResult(commandWithReply("$WFS?" + mode + ":" + passkey + ":"+ index + "\n",500))==0;
	}
	
	/**
	 * Connect to a given SSID WPA Auto network with a passkey. This method simply initiates the connection
	 * sequence and does not wait for the connection to complete or fail. You need to call connectionStatus()
	 * isConnected() or waitConnection() to poll the connection to see if it succeeds or fails. 
	 * 
	 * @param ssid - SSID of the network
	 * @param key - the passkey to use
	 * @param saveConfig - If true then the WPA configuration is saved as the default Custom Profile on the device.
	 * @return true if all commands were issued correctly 
	 */	
	public boolean connectToWPAAutoWithKey(String ssid, String key, boolean saveConfig) {
		boolean ret = true;
		ret &= setSecurity(WF_SEC_WPA_AUTO_KEY,key);
		ret &= setSSID(ssid);
		ret &= setType(INFRASTRUCTURE);
		if(saveConfig) {
			ret &= saveProfile(); 
		}
		ret &= connect(true);
		return ret;
	}		
	
	/**
	 * Connect to a given SSID WPA2 network with a passphrase. This method simply initiates the connection
	 * sequence and does not wait for the connection to complete or fail. You need to call connectionStatus()
	 * isConnected() or waitConnection() to poll the connection to see if it succeeds or fails. 
	 * Connecting can take time since the WPA2 key needs to be generated at the first connection and then stored 
	 * on the NXT2WIFI. Allow up to 30 seconds for this to complete.
	 * 
	 * @param ssid - SSID of the network
	 * @param passphrase - the passphrase to use
	 * @param saveConfig - If true then the WPA configuration is saved as the default Custom Profile on the device.
	 * @return true if all commands were issued correctly 
	 */
	public boolean connectToWPA2WithPassphrase(String ssid, String passphrase, boolean saveConfig) {
		boolean ret = true;
		ret &= setSecurity(WF_SEC_WPA2_PASSPHRASE,passphrase);
		ret &= setSSID(ssid);
		ret &= setType(INFRASTRUCTURE);
		if(saveConfig) {
			ret &= saveProfile(); 
		}
		ret &= connect(true);
		return ret;	
	}
	
	/**
	 * Create an Ad-Hoc network
	 * Shortcut to set the custom profile. 
	 */
	public void createAdHoc(String IPAddress, String netmask, String DNS, String gateway, String SSID) {
				
		setIPAddress(IPAddress);
		setMask(netmask);
		setDNS1(DNS);
		setGateway(gateway);
		setSSID(SSID);
	}
	
	
	/**
	 * Wait for the NXT2WIFI to get connected.
	 *
	 * @param timeout the timeout in milliseconds (0 means no timeout) 
	 * @return true if connected, false if timed out 
	 */
	public boolean waitConnection(int timeout) {
		long timer = System.currentTimeMillis();
		while( !isConnected() && (timeout==0 || (System.currentTimeMillis()-timer<timeout)) ) {
			Delay.msDelay(500);
		}
		return isConnected();
	}
	
	/**
	 * Checks if the device is connected.
	 *
	 * @return true, if is connected
	 */
	public boolean isConnected() {
		return connectionStatus()==CONNECTED;
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
		suspendWebMonitor = true;
		send("$WFGS\n");
		Delay.msDelay(50);
		String reply = readFully(false);
		suspendWebMonitor = false;
		
		if(reply.length() > 5 && reply.startsWith("WFGS") ) {
			status = parseIntResult(reply);
		} else {
			status = -1;
		}
		
		//if(debug) RConsole.println("connectionStatus returning " + status);
		
		return status;
	}
	
	/**
	 * Checks if DHCP assigned a new address.
	 *
	 * @return true, if DHCP address is assigned
	 */
	public boolean isDHCPAddressAssigned() {
		return parseIntResult(commandWithReply("$WFAA\n"))>0;
	}
	
	/**
	 *  Returns true if the custom Wi-Fi profile has been previously set
	 */
	public boolean customExists() {
		return parseIntResult(commandWithReply("$WFKE\n"))>0;
	}	
	
	/**
	 * Convert the connection status value into a string that can be printed
	 * @param status - The status returned by connectionStatus
	 * @return A string representing the connection status value
	 * @throws IllegalArgumentException if an invalid status code is provided
	 */
	public String connectionStatusToString(int status) {
		
		if(status < 0 || status > connectionStatuses.length) {
			return "unknown("+status+")";
		}
		
		return connectionStatuses[status];
	}
	
	/**
	 * Stop an in-progress connection to a wifi network. Use this if the NXT2WIFI is in the process
	 * of connecting to a network and you want to halt that.
	 * @return true if successful
	 */
	public boolean stopConnecting() {
		return parseIntResult(commandWithReply("$WFQ\n"))==0;
	}
	
	/**
	 * Disconnect from a wireless network. 
	 * @return true if successful
	 */
	public boolean disconnect() {
		return parseIntResult(commandWithReply("$WFX\n"))==0;
	}
	
	/** Put Wi-Fi module to hibernation (power saving mode)
	 * or turn it on (reconnection needed).
	 * @param h true hibernate, false exit hibernation.
	 * @return true if successful
	 */
	public boolean setHibernation(boolean h) {
		return parseIntResult(commandWithReply("$"+(h?"H":"O")+"\n"))==0;		
	}	
	
	/**
	 * Set an IP address manually on the NXT2WIFI. Takes the IP address in dotted-decimal format
	 * as a string.
	 * @param address The IP address to set. E.g. 192.168.1.23
	 * @return true if successful
	 */
	public boolean setIPAddress(String address) {
		return parseIntResult(commandWithReply("$WFE?IPAD="+address+"\n"))==0;
	}
	
	/**
	 * Set the netmask on the NXT2WIFI. Takes the netmask in dotted-decimal format as a string
	 * @param address The netmask address to use. E.g. 255.255.255.0
	 * @return true if successful
	 */
	public boolean setMask(String address) {
		return parseIntResult(commandWithReply("$WFE?MASK="+address+"\n"))==0;
	}

	/**
	 * Set the default gateway on the NXT2WIFI. Takes the gateway as a dotted-decimal format
	 * string. E.g. 192.168.1.1
	 * @param address The gateway address to set
	 * @return true if successful
	 */
	public boolean setGateway(String address) {
		return parseIntResult(commandWithReply("$WFE?GWAY="+address+"\n"))==0;
	}
	
	/**
	 * Set the DNS server 1 on the NXT2WIFI. Takes the address as a dotted-decimal format
	 * string. E.g. 192.168.1.1
	 * @param address The DNS server 1 address to set
	 * @return true if successful
	 */
	public boolean setDNS1(String address) {
		return parseIntResult(commandWithReply("$WFE?DNS1="+address+"\n"))==0;
	}
	
	/**
	 * Set the DNS server 2 on the NXT2WIFI. Takes the address as a dotted-decimal format
	 * string. E.g. 192.168.1.1
	 * @param address The DNS server 2 address to set
	 * @return true if successful
	 */
	public boolean setDNS2(String address) {
		return parseIntResult(commandWithReply("$WFE?DNS2="+address+"\n"))==0;
	}
	
	/**
	 * Set the SSID of the wifi network to connect to. 
	 * @param name The name of the wifi network to connect to.
	 * @return true if successful
	 */
	public boolean setSSID(String name) {
		return parseIntResult(commandWithReply("$WFE?SSID="+name+"\n"))==0;
	}
	
	/**
	 * Enable the DHCP for the wifi network connection. 
	 * @param e if true, enables DHCP, if false maintains specified IP.
	 * @return true if successful
	 */	
	public boolean setDHCP(boolean e) {
		return parseIntResult(commandWithReply("$WFE?DHCP="+(e?"1":"0")+"\n"))==0;
	}	
	
	/**
	 * Set the type for the wifi network connection. 
	 * @param e if true is adhoc network, false is infrastructure
	 * @return true if successful
	 */	
	public boolean setType(boolean e) {
		return parseIntResult(commandWithReply("$WFE?TYPE="+(e?"1":"0")+"\n"))==0;
	}	

	/**
	 * Set Infrastructure type for the wifi network connection. 
	 * @return true if successful
	 */	
	public boolean setInfrastructure() {
		return setType(INFRASTRUCTURE);
	}
	
	/**
	 * Set AdHoc type for the wifi network connection. 
	 * @return true if successful
	 */	
	public boolean setAdHoc() {
		return setType(AD_HOC);
	}	
	
	/**
	 * Set the NetBIOS name of the wifi network. 
	 * @param name The NetBIOS name to set.
	 * @return true if successful
	 */
	public boolean setNetbiosName(String name) {
		return parseIntResult(commandWithReply("$WFE?NAME="+name+"\n"))==0;
	}
	
	/**
	 * Return the current IP address of the device
	 * @return A string containing the current IP address
	 */
	public String getIPAddress() {
		suspendWebMonitor = true;		
		send("$WFIP\n");
		Delay.msDelay(50);

		String reply = readFully(false);
		suspendWebMonitor = false;
		
//		System.out.println(">>"+reply);
		
		if(reply.startsWith("WFIP=")) {
			reply = reply.substring(5);
		} else {
			reply = "reply err";
		}
		
		return reply;
	}
	
	/**
	 * Return the current MAC address of the device
	 * @return A string containing the current MAC address
	 */
	public String getMACAddress() {
		suspendWebMonitor = true;	
		send("$MAC\n");
		Delay.msDelay(50);
		
		String reply = readFully(false);
		suspendWebMonitor = false;	
		////RConsole.println("Reply = " + reply);
		
		// we need to trim the first chunk off the reply as it should be
		// MAC=
		// If this is not found then throw an exception
		if(reply.startsWith("MAC=")) {
			reply = reply.substring(4);
		} else {
			reply = "reply err";
		}
		
		return reply;
	}

	/**
	 * Force ARP request for specified IP address
	 * @param ip the IP for the ARP request
	 */
	public boolean sendARPRequest(String ip) {
		return parseIntResult(commandWithReply("$ARP\n"))==0;
	}	

///////////////////////////////////////////////////////////////////////////////
//////////////////////////////IO STREAM SUBCLASSES ////////////////////////////
///////////////////////////////////////////////////////////////////////////////
		
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
		private String type = "TCP";
		
		/**
		 * Create the Input Stream on NXT2WIFI socket
		 * @param s socket ID (1,2,3,4) 
		 * @param kind (0 for UDP, 1 for TCP)
		 * @return the number of bytes that can be read from this input stream without blocking.
		 * @throws IOException if socket type is invalid
		 */
		NXT2WIFIInputStream(int s, int kind) {
			socketID = s;
			if (kind==UDP) type = UDP_STRING;
			if (kind==TCP) type = TCP_STRING;
		}
		
		/**
		 * Returns the number of bytes that can be read (or skipped over) from this
		 * input stream without blocking by the next caller of a method for this input
		 * stream. The next caller might be the same thread or or another thread.
		 *
		 * @return the number of bytes that can be read from this input stream without blocking.
		 *
		 */
		public int available() {
			synchronized (NXT2WIFI.this){
				
				int status = -1;
				
				if(!NXT2WIFI.this.socketOpen[socketID])
					return 0;
				
				String reply = commandWithReply("$"+type+"L" + socketID + "\n",30);
				
				if(reply.startsWith(type+"L") && reply.length() > 6) {
					int equalsSignPos = reply.indexOf('=');
					status = Integer.parseInt(reply.substring(equalsSignPos+1));
				} else {
					status = -1;
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
				commandWithReply("$"+type+"X" + socketID + "\n",50);
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
		public int read() {
			
			synchronized (NXT2WIFI.this){
				if(!NXT2WIFI.this.socketOpen[socketID])
					return 0;
				
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
		public int read(byte[] cbuf) {
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
		public int read(byte[] cbuf, int off, int len) {
			
			synchronized(NXT2WIFI.this) {
			
				if(!NXT2WIFI.this.socketOpen[socketID])
					return -1;
				
				send("$"+type+"R" + socketID + "?"+ len +"\n");
				Delay.msDelay(50);
		
				// next we read in the reply and parse the header to know how many bytes were returned
				String replyHeader = readFully(true, 6);
								
				// Reply in format TCPR<S>=<len>,<data>,
				// E.g. TCPR1=1,h
				// Always expect <len> to be 1, as we only asked for 1 byte...
				// discard anything beyond the first character returned
				if(replyHeader.length()>3 && !replyHeader.startsWith(type+"R")) {
					return -2;
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
				int byteCount = 0; 
				try {
					byteCount = Integer.parseInt(replyByteCount);
					// read the remainder of the data
					return readBytesFully(true, cbuf, off, byteCount);
				}
				catch (Exception e) {
					return 0;
				}
			}
		}
		
		/**
		 * Flush the stream.
		 */
		public void flush() {
			synchronized (NXT2WIFI.this){
				if(!NXT2WIFI.this.socketOpen[socketID])
					return;
				commandWithReply("$"+type+"F" + socketID + "\n",40);
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
		private String type = "TCP";		

		/**
		 * Create the Output Stream on NXT2WIFI socket
		 * @param s socket ID (1,2,3,4) 
		 * @param kind (0 for UDP, 1 for TCP)
		 * @return the number of bytes that can be read from this input stream without blocking.
		 * @throws IOException if socket type is invalid
		 *
		 */
		NXT2WIFIOutputStream(int s, int kind) {
			socketID = s;
			if (kind==UDP) type = UDP_STRING;
			if (kind==TCP) type = TCP_STRING;
		}

//		void setSocketID(int s) {
//			socketID = s;
//		}		
//		
//		void setSocketType(int kind) {
//			if (kind==0) type = UDP_TYPE;
//			if (kind==1) type = TCP_TYPE;
//		}			
		
		/**
		 * Close the output stream socket
		 *
		 */
		@Override
		public void close() {
			synchronized (NXT2WIFI.this){
				commandWithReply("$"+type+"X" + socketID + "\n");
				NXT2WIFI.this.socketOpen[socketID] = false;
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
				
				// build the full data packet in one array
				String cmd = "$"+type+"W" + socketID + "?"+ len +",";
				
				byte cmdBytes[] = new byte[cmd.length() + len];
				System.arraycopy(cmd.getBytes(), 0, cmdBytes, 0, cmd.length());
				
				// now copy in the data chunk
				System.arraycopy(cbuf, off, cmdBytes, cmd.length(), len);
				
				RS485.hsWrite(cmdBytes, 0, cmdBytes.length);
				Delay.msDelay(50);
				
				// next we read in the reply and parse the header to know how many bytes were actually written
				String replyHeader = readFully(true);
				//if(debug) RConsole.println("write: replyHeader="+replyHeader);
				
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
