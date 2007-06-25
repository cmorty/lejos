package lejos.pc.comm;

import java.util.*;
import java.io.*;

public class NXTCommand implements NXTProtocol {	
	private NXTComm nxtComm = null,
	                nxtCommUSB = null,
	                nxtCommBluetooth = null;
	
	private static String HOME = System.getProperty("nxj.home");;
	private static String SEP = System.getProperty("file.separator");
	private static String PROP_FILE = HOME + SEP + "bin" + SEP + "nxj.properties";
	private static NXTCommand singleton = null;
	
	private boolean verifyCommand = false;
	
    public static final int USB = 1;
    public static final int BLUETOOTH = 2;
    private boolean open = false;

    public NXTInfo[] search(String name, int protocol) {
    	NXTInfo[] nxtInfos;
    	
    	if (nxtComm == null) {
	    	Properties props = new Properties();
	    	
	    	try {
	    		//System.out.println("Loading " + PROP_FILE);
	    		props.load(new FileInputStream(PROP_FILE));
	    	} catch (FileNotFoundException e) {
	    		//System.out.println("No nxj.properties file");
	    	} catch (IOException e) {
	    		System.out.println("Failure to read nxj.properties file");
	    	}

	    	String os = System.getProperty("os.name");
	    	boolean windows = false;
	    	
	    	if (os.length() >= 7 && os.substring(0,7).equals("Windows"))
	    		windows = true;
	    	
	    	// Look for USB comms driver first
	    	
	    	if ((protocol & USB) != 0) {
	    		String nxtCommName = props.getProperty("NXTCommUSB", "lejos.pc.comm.NXTCommLibnxt");
	    		//System.out.println("NXTCommUSB = " + nxtCommName);
	    		try {
	        		Class c = Class.forName(nxtCommName);
	        		nxtCommUSB = (NXTComm) c.newInstance();
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        	}
	    	}
	        		        	
        	// Look for a Bluetooth one
        	
	    	String defaultDriver = (windows  ? "lejos.pc.comm.NXTCommBluecove"
	    			                         : "lejos.pc.comm.NXTCommBluez");
        	
	    	if ((protocol & BLUETOOTH) != 0) {
        		String nxtCommName = props.getProperty("NXTCommBluetooth", defaultDriver);
        		//System.out.println("NXTCommBluetooth = " + nxtCommName);
        		try {
            		Class c = Class.forName(nxtCommName);
            		nxtCommBluetooth = (NXTComm) c.newInstance();
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
        	}
        	
        	if (nxtCommUSB == null && nxtCommBluetooth == null) {
        		System.out.println("Cannot load a comms driver");
        		System.exit(1);
        	}

    	}

    	// Look for a USB one first
    	
    	if ((protocol & USB) != 0 && nxtCommUSB != null) {
    		nxtInfos = nxtCommUSB.search(name, protocol);
    		if (nxtInfos.length > 0) {
    			nxtComm = nxtCommUSB;
    			return nxtInfos;
    		}
    	}
    	
    	// If not found, look for a Bluetooth one
    	
    	if ((protocol & BLUETOOTH) != 0 && nxtCommBluetooth != null){
    		nxtInfos = nxtCommBluetooth.search(name, protocol);
    		if (nxtInfos.length > 0) {
    			nxtComm = nxtCommBluetooth;
    			return nxtInfos;
    		}   		
    	}
    	
    	return new NXTInfo[0];
	}

	public boolean open(NXTInfo nxt) {
		return open = nxtComm.open(nxt);
	}

	public void setVerify(boolean verify) {
		verifyCommand = verify;
	}

	/**
	 * Small helper method to send DIRECT COMMAND request to NXT and return verification result.
	 * @param request
	 * @return
	 */
	private byte sendRequest(byte [] request, int replyLen) {
		byte verify = 0; // default of 0 means success
		if(verifyCommand)
			request[0] = DIRECT_COMMAND_REPLY;
		
		byte [] reply = nxtComm.sendRequest(request,
				                (request[0] == DIRECT_COMMAND_REPLY ? replyLen : 0));
		if(request[0] == DIRECT_COMMAND_REPLY) {
			verify = reply[2];
		}
		return verify;
	}
	
	/**
	 * Small helper method to send a SYSTEM COMMAND request to NXT and return verification result.
	 * @param request
	 * @return
	 */
	private byte sendSystemRequest(byte [] request, int replyLen) {
		byte verify = 0; // default of 0 means success
		if(verifyCommand)
			request[0] = SYSTEM_COMMAND_REPLY;
		
		byte [] reply = nxtComm.sendRequest(request,
				                (request[0] == SYSTEM_COMMAND_REPLY ? replyLen : 0));
		if(request[0] == SYSTEM_COMMAND_REPLY) {
			verify = reply[2];
		}
		return verify;
	}

	/**
	 * Starts a program already on the NXT.
	 * @param fileName
	 * @return
	 */
	public byte startProgram(String fileName) {
		byte [] request = {DIRECT_COMMAND_NOREPLY, START_PROGRAM};
		request = appendString(request, fileName);
		return sendRequest(request,22);
	}	

	/**
	 * Opens a file on the NXT for reading. Returns a handle number and file size,
	 * enclosed in a FileInfo object.
	 * 
	 * @param fileName e.g. "Woops.rso"
	 * @return
	 */
	public FileInfo openRead(String fileName) {
		byte [] request = {SYSTEM_COMMAND_REPLY, OPEN_READ};
		request = appendString(request, fileName); // No padding required apparently
		byte [] reply = nxtComm.sendRequest(request,8);
		FileInfo fileInfo = new FileInfo(fileName);
		fileInfo.status = reply[2];
		if(reply.length == 8) { // Check if all data included in reply
			fileInfo.fileHandle = reply[3];
			fileInfo.fileSize = (0xFF & reply[4]) | ((0xFF & reply[5]) << 8)| ((0xFF & reply[6]) << 16)| ((0xFF & reply[7]) << 24);
		}
		return fileInfo;
	}

	/**
	 * Opens a file on the NXT for writing.
	 * @param fileName e.g. "Woops.rso"
	 * @return File Handle number
	 */
	public byte openWrite(String fileName, int size) {
		byte [] command = {SYSTEM_COMMAND_REPLY, OPEN_WRITE};
        byte[] asciiFileName = new byte[fileName.length()];
        for(int i=0;i<fileName.length();i++) asciiFileName[i] = (byte) fileName.charAt(i);
		command = appendBytes(command, asciiFileName);
		byte [] request = new byte[22];
		System.arraycopy(command, 0, request, 0, command.length);
		byte [] fileLength = {(byte)size, (byte)(size>>>8), (byte)(size>>>16), (byte)(size>>>24)};
		request = appendBytes(request, fileLength);
		byte [] reply = nxtComm.sendRequest(request, 4);
		return reply[3]; // The handle number
	}

	/**
	 * Closes an open file.
	 * @param handle File handle number.
	 * @return Error code 0 = success
	 */
	public byte closeFile(byte handle) {
		byte [] request = {SYSTEM_COMMAND_NOREPLY, CLOSE, handle};			
		return sendSystemRequest(request, 4);
	}
	
	public byte delete(String fileName) {		
		byte [] request = {SYSTEM_COMMAND_REPLY, DELETE};
		request = appendString(request, fileName);
		return sendSystemRequest(request, 23);
	}

	/**
	 * @param wildCard [filename].[extension], *.[extension], [filename].*, *.*
	 * @return
	 */
	public FileInfo findFirst(String wildCard) {

		byte [] request = {SYSTEM_COMMAND_REPLY, NXJ_FIND_FIRST};
		request = appendString(request, wildCard);

		byte [] reply = nxtComm.sendRequest(request, 32);
		FileInfo fileInfo = null;
		if(reply[2] == 0  && reply.length == 32) {
			StringBuffer name= new StringBuffer(new String(reply)).delete(0, 4);
			int lastPos = name.indexOf("\0"); 
			name.delete(lastPos, name.length());
			fileInfo = new FileInfo(name.toString());
			fileInfo.status = 0;
			fileInfo.fileHandle = reply[3];
			fileInfo.fileSize = (0xFF & reply[24]) | ((0xFF & reply[25]) << 8)| ((0xFF & reply[26]) << 16)| ((0xFF & reply[27]) << 24);
			fileInfo.startPage = (0xFF & reply[28]) | ((0xFF & reply[29]) << 8)| ((0xFF & reply[30]) << 16)| ((0xFF & reply[31]) << 24);

		}
		return fileInfo;
	}
	
	/**
	 * @param handle Handle number from the previous found file or fromthe Find First command.
	 * @return
	 */
	public FileInfo findNext(byte handle) {

		byte [] request = {SYSTEM_COMMAND_REPLY, NXJ_FIND_NEXT, handle};
		
		byte [] reply = nxtComm.sendRequest(request, 32);
		FileInfo fileInfo = null;
		if(reply[2] == 0 && reply.length == 32) {
			StringBuffer name= new StringBuffer(new String(reply)).delete(0, 4);
			int lastPos = name.indexOf("\0");
			name.delete(lastPos, name.length());
			fileInfo = new FileInfo(name.toString());
			fileInfo.status = 0;
			fileInfo.fileHandle = reply[3];
			fileInfo.fileSize = (0xFF & reply[24]) | ((0xFF & reply[25]) << 8)| ((0xFF & reply[26]) << 16)| ((0xFF & reply[27]) << 24);
			fileInfo.startPage = (0xFF & reply[28]) | ((0xFF & reply[29]) << 8)| ((0xFF & reply[30]) << 16)| ((0xFF & reply[31]) << 24);
		}
		return fileInfo;
	}

	/**
	 * Helper code to append a string and null terminator at the end of a command request.
	 * Should use String.concat if I could add a zero to end somehow.
	 * @param command
	 * @param str
	 * @return
	 */
	private byte[] appendString(byte [] command, String str) {
		byte[] buff = new byte[command.length + str.length() + 1];
		for(int i=0;i<command.length;i++) buff[i] = command[i];
		for(int i=0;i<str.length();i++) buff[command.length+i] = (byte) str.charAt(i);
		buff[command.length + str.length()] = 0;
		return buff;
	}

	private byte[] appendBytes(byte [] array1, byte [] array2) {
		byte [] array = new byte[array1.length + array2.length];
		System.arraycopy(array1, 0, array, 0, array1.length);
		System.arraycopy(array2, 0, array, array1.length, array2.length);
		return array;
	}

	public int getBatteryLevel() {
		byte [] request = {DIRECT_COMMAND_REPLY, GET_BATTERY_LEVEL};
		byte [] reply = nxtComm.sendRequest(request, 5);
		int batteryLevel = (0xFF & reply[3]) | ((0xFF & reply[4]) << 8);
		return batteryLevel;
	}

	/**
	 * Call the close() command when your program ends, otherwise you
	 * will have to turn the NXT brick off/on before you run another
	 * program.
	 *
	 */
	public void close() {
		if (!open) return;
		open = false;
		byte [] request = {DIRECT_COMMAND_NOREPLY, NXJ_DISCONNECT};
		nxtComm.sendRequest(request,0); // Tell NXT to disconnect
		nxtComm.close();
	}

	public byte writeFile(byte handle, byte [] data) {
		byte [] request = new byte[data.length + 3];
		byte [] command = {SYSTEM_COMMAND_NOREPLY, WRITE, handle};
		System.arraycopy(command, 0, request, 0, command.length);
		System.arraycopy(data, 0, request, 3, data.length);
						
		return sendSystemRequest(request, 6);
	}

	/**
	 * Returns requested number of bytes from a file. File must first be opened
	 * using the openRead() command.
	 * @param handle File handle number (from openRead method)
	 * @param length Number of bytes to read.
	 * @return
	 */
	public byte [] readFile(byte handle, int length) {
		byte [] request = {SYSTEM_COMMAND_REPLY, READ, handle, (byte)length, (byte)(length>>>8)};
		byte [] reply1 =  nxtComm.sendRequest(request, length+6);
		int dataLen = (reply1[4] & 0xFF) + ((reply1[5] << 8) & 0xFF);
		byte [] reply = new byte[dataLen];
		for(int i=0;i<dataLen;i++) reply[i] = reply1[i+6];
		return reply;
	}
	
	public byte defrag() {
		byte [] request = {DIRECT_COMMAND_NOREPLY, NXJ_DEFRAG};		
        return sendRequest(request,3);
	}
	
	public String getFriendlyName() {
		byte [] request = {DIRECT_COMMAND_REPLY, GET_DEVICE_INFO};
		
		byte [] reply = nxtComm.sendRequest(request,33);
		
		char nameChars[] = new char[16];
		int len = 0;
		
		for(int i=0;i<16 && reply[i+3] != 0;i++) {
			nameChars[i] = (char) reply[i+3];
			len++;
		}
		
		return new String(nameChars,0,len);
	}
	
	public byte setFriendlyName(String name) {
		byte [] request = {DIRECT_COMMAND_NOREPLY, SET_BRICK_NAME};
		request = appendString(request, name);
		
		return sendSystemRequest(request,3);
	}

	public static NXTCommand getSingleton() {
    	if (singleton == null) singleton = new NXTCommand();
		return singleton;
	}
}


