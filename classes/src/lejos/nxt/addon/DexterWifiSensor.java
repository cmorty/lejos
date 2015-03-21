package lejos.nxt.addon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.net.ServerSocketFactory;
import lejos.nxt.Button;
import lejos.nxt.comm.RS485;
import lejos.util.Delay;

/**
 * WiFi sensor by Dexter Industries<p>
 * 
 * <b>Sensor description:</b><br>
 * The Dexter Industries Wifi Sensor allows your robots to connect to the internet and 
 * send and receive data. The Dexter Industries Wifi sensor for LEGO� MINDSTORMS� NXT 
 * is a sensor designed to allow your LEGO MINDSTORMS to connect to the internet via a 
 * local Wifi network. 
 * 
 * The sensor can be used to communicate via HTTP, TCP, UDP protocols. The sensor can 
 * connect via WPA, WPA2-PSK, and WEP networks, as well as adhoc networks between the 
 * LEGO MINDSTORMS and a wifi enable device such as a cell phone or computer. 
 * 
 * The sensor operates with standard 802.11 b/g/n access and can log onto any home 
 * network. The Dexter Industries Wifi Sensor connects to the LEGO MINDSTORMS NXT via 
 * the high speed communications Port 4 and is powered by an external 9V battery. 
 * 
 * <p>
 * <b>Changelog:</b><br>
 * 	- V0.1: initial release!<p>
 * 
 *  
 * <b>Credits:</b><br>
 * Based on the RobotC driver by Xander Soldat
 * 
 * @version 0.1
 * @author Lasse S. Lauesen - BrickIt.dk - lasse@brickit.dk
 * @deprecated This driver is terribly broken! New driver cooming soon. 
 */
@Deprecated
public class DexterWifiSensor {
	
	private DexterWifiSensor wifi_instance = this; // Accessed by inner classes DexterServerSocketFactory and DexterServerSocket 
	
	public final static int 	BAUD0_9600 = 0,
								BAUD1_19200 = 1, 
								BAUD2_38400 = 2, 
								BAUD3_57600 = 3, 
								BAUD4_115200 = 4, 
								BAUD5_230400 = 5,
								BAUD6_460800 = 6, 
								BAUD7_921600 = 7;
	
	private final static int BUFFER_LENGTH = 2048;
	
	private final static int baudrates[] = {9600, 19200, 38400, 57600, 115200, 230400,460800, 921600};
	
	private final static byte[] CMD_ATE0 = new byte[] {'A','T','E','0','\r'};
	
	/** The &lt;<i>esc</i>&gt; character!*/
	private final static byte ESC = 27;
	
	private boolean rs485Enabled;
	

	/** The current baudrateIndex */
	private int currentBaudrateIndex;

	private DebugCallback debugger = null;
	
	public DexterWifiSensor() throws IOException {
		this(BAUD4_115200, null);
	}
	
	public DexterWifiSensor(int baudrate) throws IOException {
		this(baudrate, null);
	}
	
	public DexterWifiSensor(DebugCallback debugger) throws IOException {
		this(BAUD4_115200, debugger);
	}
	
	/**
	 * Create an instace of the dexter industries WiFi sensor
	 * 
	 * @param Baudrate selection from the baudrate array <i>baudrates</i>.
	 * @throws ConnectException - If the sensor could not be found, a ConnectException is thrown.
	 * @throws ArrayIndexOutOfBoundsException - If the baudrate index is not in the range 0..7
	 */
	public DexterWifiSensor(int baudrate, DebugCallback debugger) throws IOException{
		this.debugger = debugger;
		if(baudrate < 0 || baudrate >= baudrates.length) throw new ArrayIndexOutOfBoundsException("The index is not valid in the Baudrate array!");
		rs485Enabled = false;
		int curBaud = findSensor(baudrate);
		if(curBaud >= 0){
			clearReadBuffer();
			disableEcho();
			if(curBaud != baudrate){
				setBaudrate(baudrate);
			}
		}else{
			throw new IOException("Sensor could not be found!");
		}
	}
	
	
/*==========================================================================
 * Public Methods: 
 *==========================================================================*/
	private ServerSocketFactory dssf = new DexterServerSocketFactory();
	public ServerSocketFactory getServerSocketFactory() {
		return dssf;
	}
	
	/**
	 * Get the array of availabe baudrates for the sensor (in bits pr. second).
	 * @return The array of available baudrates for the sensor.
	 */
	public static int[] getAvailableBaudrates(){
		return baudrates;
	}
	
	/**
	 * Get the current baudrate index that the sensor is communicating on.
	 * @return baudrate index that represents the current baudrate in the baudrate array: <i>baudrates</i>
	 */
	public int getBaudrateIndex(){
		return currentBaudrateIndex;
	}
	
	/**
	 * Get the curret baudrate that the sensor is communication on in bits pr. second
	 * @return the current baudrate in bits pr. second
	 */
	public int getBaudrate(){
		return baudrates[currentBaudrateIndex];
	}
	
	
	/**
	 * Method for getting an IP of a host
	 * @param host
	 * @return IP-Address as a test string
	 */
	public String getIpOf(String host){
		String command = "AT+DNSLOOKUP="+host;
		String response = commandTransaction(command);
		for (int i = 0; i < response.length(); i++) {
			if(response.substring(i).startsWith("OK\r\n")){
				int ipStart = response.indexOf("IP:")+3;
				int ipStop = response.indexOf("\r",ipStart);
				if(ipStart != -1 && ipStop != -1){
					return response.substring(ipStart, ipStop);
				}
			}
		}
		return null;
	}

	/**
	 * Ping a host to test the connection
	 * @param host - The host you want to ping, can be both hostname or IP-Address.
	 * @return the ping time in milliseconds. time is -1 if ping failed.
	 */
	public int ping(String host){
		int[] times = ping(host, 1);
		return times[0];
	}
	
	/**
	 * Ping a host to test the connection
	 * @param host - The host you want to ping, can be both hostname or IP-Address.
	 * @param trails - The number of times to ping
	 * @return the ping times in a int array. times are -1 if ping failed.
	 */
	public int[] ping(String host, int trails){
		int[] times = new int[trails];
		String ip;
		if(isIP(host)) ip = host;
		else ip = getIpOf(host);
		
		if(ip != null && trails > 0){
			String command = "AT+PING="+ip+","+trails+","+/*[<Interval>]*/","+/*[<Len>]*/","+/*[<TOS>]*/","+/*[<TTL>]*/","/*[<PAYLOAD>]*/;
			
			String response = commandTransaction(command,0);
			
			int index = -1;
			if((index = response.indexOf("OK\r\n")) >= 0){
				//We have received - command accepted!
				//Now we try to find the times:
				boolean more = true;
				int n = 0;
				while(more){
					index = response.indexOf("Reply",index);
					int indexFail = response.indexOf("Request",index);
					if(index >= 0 || indexFail >= 0){
						//there is a reply from host:
						if(indexFail > index){
							//this is a timeout
							times[n] = -1;
						}else{
							//This is a successful ping
							int start = response.indexOf("time=",index)+5;
							index = response.indexOf(" ms", start);
							times[n] = Integer.parseInt(response.substring(start,index));
						}
						n++;
						if(n == trails){
							more = false;
						}
					}else{
						response = readFully(true);
					}
				}
			}
			return times;
		}
		
		//We have an error, set all times to -1:
		for (int i = 0; i < times.length; i++) {
			times[i] = -1;
		}
		return times;
	}
	
	
	
	/**
	 * Connect the WiFi sensor to a WPA Network.
	 * 
	 * @param SSID - The name of the network
	 * @param passphrase - the passphrase for the network
	 * @param enableDHCP - Should the wifi use DHCP [true] or static ip [false]
	 * @return Status of the connection:<br/>
	 * 		- Successful connect returns: "OK:&lt;<i>ip</i>&gt;:&lt;<i>subnetmask</i>&gt;:&lt;<i>gateway</i>&gt;"<br/>
	 * 		- Unsuccessful connect returns: "ERROR&lt;<i>error description</i>&gt;" 
	 */
	public String connectWPAPSK(String SSID, String passphrase, boolean enableDHCP){
		debug(setWPAPSK(SSID, passphrase));
		debug(connectWLAN(SSID));
		return(setDHCPEnabled(enableDHCP));
	}
	

	/**
	 * Enable or disable the WiFi sensors DHCP Client
	 * @param e - enable [true] or disable [false]
	 * @return the status of the command:<br/>
	 * 		- Successful returns: "OK:&lt;<i>ip</i>&gt;:&lt;<i>subnetmask</i>&gt;:&lt;<i>gateway</i>&gt;"<br/>
	 * 		- Unsuccessful returns: "ERROR&lt;<i>error description</i>&gt;" 
	 */
	public String setDHCPEnabled(boolean e){
		String cmd = "AT+NDHCP=";
		if(e) cmd += "1";
		else cmd += "0";
		String response = removeLeadingLineshift(commandTransaction(cmd));
		//Se if we got the OK response in the read:
		for (int i = 0; i < response.length(); i++) {
			if(response.substring(i).startsWith("OK\r\n")){
				String ipInfo = parseIpTable(response);
				if(ipInfo != null) return "OK:"+ipInfo+"\r\n";
			}
		}
		return response;
	}
	
	/**
	 * Set the ip of the WiFi sensor (If ip is null the DHCP is enabled)
	 * @param ip - The wanted ipaddress.
	 * @param mask - The wanted subnetmask
	 * @param gate - The wanted gateway
	 * @return result
	 */
	public String setIP(String ip, String mask, String gate){
		//AT+NSET=<Src Address>,<Net-mask>,<Gateway>
		if(ip == null){
			return setDHCPEnabled(true);
		}else if(mask != null && gate != null){
			return removeLeadingLineshift(commandTransaction("AT+NSET="+ip+","+mask+","+gate));
		}else{
			return null;
		}
	}
	
	
	/** 4.8.4 in Command manual
	 * The method to set the WPA-PSK and WPA2-PSK passphrase
	 * @param SSID
	 * @param passphrase
	 * @return Status of completion ("OK" if successful)
	 */
	public String setWPAPSK(String SSID, String passphrase){
		debug("Setting WPAPSK");
		String response = removeLeadingLineshift(commandTransaction("AT+WPAPSK="+SSID+","+passphrase));
		debug(response);
		//Se if we got the OK response in the first read:
		for (int i = 0; i < response.length(); i++) {
			if(response.substring(i).startsWith("OK\r\n")){
				return "OK\r\n";
			}
		}
		//If not, we read once more:
		Delay.msDelay(100);
		response = removeLeadingLineshift(readFully(true));
		if(response.startsWith("OK")){
			debug(response);
			return "OK\r\n";
		}
		return response;
	}
	
	
	//TODO This method should be devided into network types! This is for WPA (Maybe a class for WLAN networks?)
	/**
	 * Connect to a WLAN
	 * @param SSID - The SSID of the network that the sensor should connect to
	 * @return result of the connect
	 */
	public String connectWLAN(String SSID){
		String response = null;
		for (int attemts = 3; attemts > 0; attemts--) {
			response = removeLeadingLineshift(commandTransaction("AT+WA="+SSID));
			//See if we got the OK response in the read:
			for (int i = 0; i < response.length(); i++) {
				if(response.substring(i).startsWith("OK\r\n")){
					String ipInfo = parseIpTable(response);
					if(ipInfo != null) return "OK:"+ipInfo+"\r\n";
				}
			}
			debug("Retrying connect!");
			Delay.msDelay(1000);
		}
		
		
		debug(response);
		return response;
	}
	
	
	/*=========================================
	 * Connections:
	 */
	
	/**
	 * Method for closing all open connections
	 */
	public void closeAllConns() {
		String command = "AT+NCLOSEALL";
		commandTransaction(command);
	}
	
	/**
	 * Close specific connection
	 * @param cid - Connection identifier of the connection to close
	 */
	public void closeConn(int cid) {
		if(cid >= 0 && cid < 16){
			if(cid > 9) cid += 55; //int 65 = 'A'
			else cid += 48;
			String command = "AT+NCLOSE="+((char)cid);
			debug("Close:"+commandTransaction(command));
		}
	}
	
	
	//TODO
	/**Start a TCP Server on the WiFi sensor
	 * 
	 * @param port - The port the TCP server shall listen on
	 * @return The connection id of the tcpserver
	 */
	public int startTCPServer(int port){
		String command = "AT+NSTCP="+port;
		String input = commandTransaction(command);
		
		//Find our connection number:
		debug("HTTPServer:"+input);
		int i = input.indexOf("\nOK\r");
		if(i > 0){ //The server was started
			i = input.indexOf("\nCONNECT ");
			if(i > 0){ //We have information about the connection!
				i+= 9; //We look at the conId character
				i = conIdCharToInt(input.charAt(i));
				debug("HTTPServer started on connection "+i);
				return i;
			}
		}
		return -1;
	}
	
	/** Send TCP data as String
	 * 
	 * @param connectionID - The connection ID of the connection we want to send over
	 * @param data - The data we want to send
	 */
	public void sendTCPData(int connectionID, String data){
		byte[] txBuf = data.getBytes();
		debug("Sending tcp:"+data);
		sendTCPData(connectionID, txBuf, 0, txBuf.length);
	}
	
	/** Send TCP data TODO: Currently writes whole data array rather than offset length
	 * 
	 * @param connectionID - The connection ID of the connection we want to send over
	 * @param data - The data we want to send
	 * @param offset TODO: NOT FUNCTIONAL
	 * @param length TODO: NOT FUNCTIONAL
	 */
	public void sendTCPData(int connectionID, byte[] data, int offset, int length) {
		// 1) send ESC+S+connectionID
		String d1 = ((char)ESC)+"S"+connectionID;
		byte[] txBuf = d1.getBytes();
		sendRS485Data(txBuf);
		
		// 2) read response and check for ESC+O/F
		String result = readFully(true);
		if(result.charAt(1) == 'O') {
			// TODO: Not sure what proper behavior is if F occurs
		}
		
		// 3) send data (actually replace each ESC with ESC+ESC)
		sendRS485Data(data);
		
		// 4) send ESC+E
		String d2 = ((char)ESC)+"E";
		txBuf = d2.getBytes();
		sendRS485Data(txBuf);
		
		// 5) read response and check for ESC+O/F
		// TODO: Check for ESC+O/F. Ignore for now.
		String input = readFully(true);
	}
	
	/**
	 * Helper method used by sendTCPData() to make sure all bytes are properly written to WiFi using RS485.hsWrite().
	 * @param data
	 */
	private void sendRS485Data(byte [] data) {
		int start = 0;
		int bytes_length = data.length;
		do {
			int written = RS485.hsWrite(data, start, bytes_length);
			start += written;
			bytes_length -= start; 
		} while(bytes_length > 0);
	}
	
	/**
	 * Convert connection-id character [0..9,a..f] to 0..15
	 * @param c - Connection id character (As received from device)
	 * @return connection id number [0..15]
	 */
	public static int conIdCharToInt(char c){
		if(c >= '0' && c <= '9') return c-'0';
		else if(c >= 'a' && c<= 'f') return (c -'W'); // 'a'-10 = 'W'
		else return -1;
	}
	
	/**
	 * Convert integer to connection-id character.
	 * 
	 * @param id number [0..15]
	 * @return connection-id character [0..9,a..f]
	 */
	public static char intToConIDChar(int id){
		if(id >= 0 && id<= 9){
			return (char)('0'+id);
		}else if(id >=10 && id<= 15){
			return (char)('W'+id);
		}else{
			return 'n';
		}
		
		
	}
	
	/**
	 * This method reads the all available data from the RS485 rx buffer
	 * 
	 * @param wait - Wait for answer if the buffer is empty!
	 * @return The string from the RS485 rx buffer
	 */
	
	public String readFully(boolean wait){
		String response = "";
		byte[] buf = new byte[1];
		boolean done = false;
		
		while(!done){
			if(RS485.hsRead(buf, 0, 1) > 0){
				response += (char)buf[0];
				Delay.msDelay(5);
			}else{
				if(!wait || (wait && response.length()>0)){
					done = true;
				}
			}
		}
		return response;
	}
	
	private boolean recordData = false; // global so that next time readTCPData is called it will continue adding data.
	private boolean escape_flag = false; // used in case buffer data breaks mid-escape sequence
	private boolean s_char_flag = false;
	
	/**
	 * Loop that looks for ESC character, then S, then parses data until ESC-E.
	 * 	
	 * @param buffer
	 * @param length
	 * @param wait
	 * @return
	 */
	public int readTCPData(byte [] buffer, int length, boolean wait) {
		// STEP 1: Get some bytes from the buffer
		byte [] tempBuf = new byte[length];
		int bytesRead = -1;
		do {
			bytesRead = RS485.hsRead(tempBuf, 0, length);
			if(bytesRead > 0) wait = false;
			Delay.msDelay(50); // TODO: This introduces latency when we might want to minimize latency. Ask Sven.
		} while(wait);
		
		int tempIndex = 0;
		int bufIndex = 0;
		
		// TODO: If ESC-ESC is sent, and only the first ESC makes it, this could screw up. Not going to worry about it
		// right now since it would be very rare situation.
		
		//for(int i=0;i<bytesRead;i++)
		//	System.out.print(tempBuf[i] + ", ");
		//System.out.println(" PRESS ENTER");
		//Button.ENTER.waitForPressAndRelease();
		
		// STEP 2: Analyze the bytes for escape character sequences and pull out raw data.
		// This loop will copy bytes to the buffer, ignoring escape characters (except ESC-ESC). It will also
		// ignore data not sandwiched between ESC-S and ESC-E. Will work even if tempBuffer does not contain full packet.
		// If packet is not complete with ESC-start and ESC-end characters, it will still continue when next
		// packet received.
		analyze_loop:while(tempIndex < bytesRead) {
			if(tempBuf[tempIndex] == ESC||escape_flag) {
				if(!escape_flag) {
					tempIndex++;
					escape_flag = true;
				}
				if(tempIndex >= bytesRead) { // this means it ran out of data right after an escape sequence
					break analyze_loop;
				}
				// Set mode to begin copying characters into buffer
				if (tempBuf[tempIndex] == 'S'||s_char_flag) {
					recordData = true; // start
					if(!s_char_flag) {
						tempIndex++;
						s_char_flag = true;
					}
					if(tempIndex >= bytesRead) { // this means it ran out of data right after an escape sequence
						break analyze_loop;
					}
					// Get connection ID. Not currently used for anything. 
					int connID = conIdCharToInt((char)tempBuf[tempIndex]);
					// TODO: Multiple server connections will need to use connection ID to sort out who sent data to which InputStream.
					tempIndex++;
				}
				if (tempBuf[tempIndex] == 'E') {
					recordData = false; // stop
				}
				escape_flag = false;
				s_char_flag = false;
			} 
			
			// Ignore data unless in recordData mode.
			// NOTE: This WILL receive ESC character properly if ESC-ESC sent.
			if(recordData) {
				buffer[bufIndex++] = tempBuf[tempIndex];
			}
			tempIndex++;
		}
		
		return bufIndex; // bufIndex will equal bytes received when done
	}
	
	public String commandTransaction(String command){
		return commandTransaction(command, 100);
	}
	
	public String commandTransaction(String command, int readDelay){
		debug("Sending command: "+command);
		String ret = null;
		
		//We make sure we have a \r or \n at the end of the command:
		char lastChar = command.charAt(command.length()-1);
		if(lastChar != '\r'){
			command += "\r";
		}
		
		//We send one byte at a time (This way it doesn't matter if the sensor is in echo mode!)
		byte[] txBuf = command.getBytes();
		RS485.hsWrite(txBuf, 0, txBuf.length);
		
		Delay.msDelay(readDelay);
		
		ret = readFully(true);
		
		return ret;
	}
	
	
	
/*==========================================================================
 * Private Methods: 
 *==========================================================================*/
	
	/**
	 * This method scans the possible baud-rate for the wifi sensor!
	 * 
	 * @param 	initialBaudrate - The baudrateIndex in <i>baudrates</i> where 
	 * 			we start the search for the sensor 
	 * 
	 * @return	The sensors baudrate index in <i>baudrates</i> or -1 if the sensor was not found 
	 */
	private int findSensor(int initialBaudrate){ //TODO
		debug("Starting search for the sensor");
		for (int i = 0; i < baudrates.length; i++) {
			disableRS485();
			Delay.msDelay(10);
			int index = i+initialBaudrate;
			if(index >= baudrates.length) index -= baudrates.length;
			debug("Searching "+baudrates[index]+"bit/s");
			enableRS485(index);
			clearReadBuffer();
			Delay.msDelay(200);
			byte[] buf = {'+','+','+','\r'};
			for (int j = 0; j < buf.length; j++) {
				RS485.hsWrite(buf, j, 1);
				Delay.msDelay(20);
			}
			Delay.msDelay(100);
			String response = removeLeadingLineshift(readFully(false));
			debug(response);
			if(response.indexOf("ERR") > -1 || response.indexOf("OK") > -1 || response.indexOf("0") > -1 || response.indexOf("2") > -1){
				clearReadBuffer();
				debug("Sensor found at "+baudrates[index]+"bit/s");
				return index;
			}
		}
		clearReadBuffer();
		debug("Could not find the sensor!");
		return -1;
	}
	
	/**
	 * Sets the baudrate on the sensor, and searches for it afterwards to make sure we still can communicate with it.
	 * 
	 * @param baudrateIndex
	 * @return the baudrateIndex that repressents the baudrate that the sensor was found on
	 */
	private int setBaudrate(int baudrateIndex){
		String cmd = "ATB="+baudrates[baudrateIndex]+"\r";
		debug("Set baudrate: "+cmd);
		
		byte[] txBuf = cmd.getBytes();
		RS485.hsWrite(txBuf, 0, txBuf.length);
		Delay.msDelay(50);
		disableRS485();
		enableRS485(baudrateIndex);
		
		clearReadBuffer();
		
		return findSensor(baudrateIndex);
	}
	
	/**
	 * Disables the echo on the sensor
	 */
	private void disableEcho(){
		debug("Disableling echo!");
		boolean echoDisabled = false;
		while(!echoDisabled){
			for (int i = 0; i < CMD_ATE0.length; i++) {
				RS485.hsWrite(CMD_ATE0, i, 1); //We send one char at a time
				Delay.msDelay(50);
			}
			
			//Receive the sensor response
			String response = readFully(true);
			response = removeLeadingLineshift(response);
			for (int i = 0; i < response.length(); i++) {
				if(response.substring(i).startsWith("OK")) echoDisabled = true;
			}
		}
		debug("Echo disabled!");
	}
	
	private void enableRS485(int baudrateIndex){
		RS485.hsEnable(baudrates[baudrateIndex], BUFFER_LENGTH);
		currentBaudrateIndex = baudrateIndex;
		rs485Enabled = true;
	}
	
	private void disableRS485(){
		RS485.hsDisable();
		currentBaudrateIndex = 0;
		rs485Enabled = false;
	}
	
	private void clearReadBuffer(){
		if(rs485Enabled){
			byte[] data = new byte[] {13};
			RS485.hsWrite(data, 0, 1);
			Delay.msDelay(100);
			
			//Empty buffer:
			while (RS485.hsRead(data, 0, 1) > 0);
			Delay.msDelay(100);
		}
	}
	
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
	

	private static String removeLeadingLineshift(String s){
		//Remove leading \r og \n:
		while(s.startsWith("\r") || s.startsWith("\n" )) s = s.substring(1);
		return s;
	}
	
	
	/**
	 * Method to parse the IP-Table that some commands return:
	 * @param response from the sensor
	 * @return String with ipInfo: [<i>IP-address</i>+":"+<i>net-mask</i>+":"+<i>gateway</i>"]
	 */
	private static String parseIpTable(String response){
		int ipStart = response.indexOf("\r\n ")+3;
		int ipStop = response.indexOf("\r",ipStart);
		int sep1 = response.indexOf(":",ipStart);
		int sep2 = response.indexOf(":",sep1+1);

		if(ipStart != -1 && ipStop != -1 && sep1 != -1 && sep2 != -1){
			return 	response.substring(ipStart, sep1)+":"+
					response.substring(sep1+2, sep2)+":"+
					response.substring(sep2+2, ipStop);
		}
		return null;
	}
	
	private void debug(String s){
		if(debugger != null){
			for (char c : s.toCharArray()) {
				switch(c){
					case '\n':
						debugger.print("<\\n>");
						break;
					case '\r':
						debugger.print("<\\r>");
						break;
					case '\t':
						debugger.print("<\\t>");
						break;
					default:
						debugger.print(""+c);
						break;
				}
			}
			debugger.println("");
		}
	}
	
	public interface DebugCallback {
		public void print(String m);
		public void println(String m);
	}
	
	private class DexterServerSocketFactory extends ServerSocketFactory {

		@Override
		public ServerSocket createServerSocket(int port) {
			DexterServerSocket ss = null;
			try {
				ss = new DexterServerSocket(port, wifi_instance);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return ss;
		}
	}
	
	/**
	 * ServerSocket for the Dexter WiFi Sensor. In order to use this, the WiFi must be connected to a WiFi router
	 * otherwise it will throw an exception.,
	 * 
	 * @author BB
	 *
	 */
	private class DexterServerSocket extends ServerSocket {
		private int serverConID; //The connection ID of our TCP Server
		private int clientConID;//The connection ID of the incoming connection.
		private int port;
		DexterWifiSensor wifi;
		private boolean isClosed = true; // If any TCP connections have been established, will be false.  
		
		/**
		 * 
		 * @param port
		 * @param wifi An instance of the wifi sensor, must be connected.
		 * @throws IOException No connection with WiFi causes exception.
		 */
		public DexterServerSocket(int port, DexterWifiSensor wifi)  throws IOException {
			// TODO: Check if connected. If not, throw exception.
			super();
			//Start a TCP server on the wifi sensor:
			this.wifi = wifi;
			this.port = port;
			// TODO: It's possible this will need to be called at start of accept() every time, rather than here.
			// TODO: The previous code seems to indicate that.
			serverConID = wifi.startTCPServer(port);
		}

		public void close() {
			wifi.closeAllConns(); 
			this.isClosed = true;
		}
		
		public boolean isClosed() {
			return isClosed;
		}
		
		public Socket accept() throws IOException {
			String input = wifi.readFully(true); // TODO: Probably change true to false and loop? Because might not get CONNECT. 
			
			if(input.length() > 0){
				//Search input for incoming connection to our TCP-Server:
				int i = input.indexOf("CONNECT "+DexterWifiSensor.intToConIDChar(serverConID)); 
					
				if(i >= 0){
					//Found incoming connection:
						
					//Get the connection id of the incoming connection:
					clientConID = DexterWifiSensor.conIdCharToInt(input.charAt(i+10));
					if(clientConID >= 0){
						//We have a valid connection from a client
						isClosed = false;
					}
						
				}
			}
			
			return new DexterSocket(clientConID, wifi_instance);
		}
		
	}
	
	/**
	 * 
	 * @author BB
	 *
	 */
	private class DexterSocket extends Socket {
		public static final int BUFFER_SIZE = 64; 
		
		private int clientConID;
		private DexterWifiSensor wifi = null;
		
		DexterSocket(int clientConID, DexterWifiSensor wifi) {
			this.clientConID = clientConID;
			this.wifi = wifi;
		}
		
		public InputStream getInputStream() {
			// TODO: Technically there should be only one of each of these objects? Return same object each time this is called.
			return new DexterInputStream(BUFFER_SIZE);
			
		}
		
		public OutputStream getOutputStream() {
			return new DexterOutputStream();
			
		}
		
		private class DexterOutputStream extends OutputStream {

			@Override
			public void write(int b) throws IOException {
				// TODO: Inefficient to send whole array with one byte each time. 
				// Instead, just add byte to internal buffer. Override write(byte[], int, int). 
				// Find optimal packet size for TCP/IP. Send data only when it reaches that size 
				// or when flush() called. Flush needs to be implemented. 
				byte [] data = {(byte)b};
				wifi.sendTCPData(clientConID, data,0,0);
			}
		}
		
		private class DexterInputStream extends InputStream {

			private byte buf[];
			private int bufIdx = 0; // Current index of the buffer
			private int bufSize = 0; // Current number of bytes sitting in buf (the data buffer)
					
			public DexterInputStream(int bufferSize) {
				buf = new byte[bufferSize];
			}
			
			@Override
			public int read() throws IOException {
				// Internal explanation: It reads all available bytes from wifi. Then it doesn't read anything until
				// the buffer is used up, at which time it reads more. So in other words, if it reads 20 (or up to 64),
				// it chews on that until all 20 read, then it retrieves more data from the WiFi.
				if (bufIdx >= bufSize) bufSize = 0;
				if (bufSize <= 0) {
				   bufSize = wifi.readTCPData(buf, buf.length, true);
		           System.out.println("bufSize " + bufSize);
				   if (bufSize < -1) throw new IOException();
				   if (bufSize <= 0) return -1;
				   bufIdx = 0;
			   }
				return buf[bufIdx++] & 0xFF;
			
			}
			
			public int available() throws IOException {
				if (bufIdx >= bufSize) bufSize = 0;
			       if (bufSize == 0) {
			    	   bufIdx = 0;
			    	   bufSize = wifi.readTCPData(buf, buf.length, false);
			           if (bufSize < -1) throw new IOException();
			           if (bufSize < 0) bufSize = 0;
			       }
			       return bufSize - bufIdx;
			}
		}
	}

}
