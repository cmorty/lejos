package lejos.pc.comm;

import java.util.*;
import java.io.*;

public class NXTCommand implements NXTProtocol {	
	private NXTComm nxtComm;	
	private boolean verifyCommand = false;
	private static String HOME = System.getProperty("user.home");
	private static String WORKING_DIR = System.getProperty("user.dir");
	private static String SEP = System.getProperty("file.separator");
	private static String USER_PROP_FILE = HOME + SEP + "nxj.properties";
	private static String WORKING_PROP_FILE = WORKING_DIR + SEP + "nxj.properties";
	private static NXTCommand singleton = new NXTCommand();
	
    public static final int USB = 1;
    public static final int BLUETOOTH = 2;
    
    public NXTCommand() {
    	Properties props = new Properties();
    	try {
    		System.out.println("Loading " + USER_PROP_FILE);
    		props.load(new FileInputStream(USER_PROP_FILE));
    	} catch (FileNotFoundException e) {
    		System.out.println("No user prop file");
    	} catch (IOException e) {
    		System.out.println("Failure to read user prop file");
    	}
    	try {
    		System.out.println("Loading " + WORKING_PROP_FILE);
    		props.load(new FileInputStream(WORKING_PROP_FILE));
    	} catch (FileNotFoundException e) {
    		System.out.println("No working directory prop file");
    	} catch (IOException e) {
    		System.out.println("Failure to read working directory prop file");
    	}
    	
    	String nxtCommName = props.getProperty("NXTComm", "lejos.pc.comm.NXTCommBluecove");
    	System.out.println("NXTComm = " + nxtCommName);
    	try {
    		Class c = Class.forName(nxtCommName);
    		nxtComm = (NXTComm) c.newInstance();
    	} catch (ClassNotFoundException e) {
    		e.printStackTrace();
    	} catch (IllegalAccessException e) {
    		e.printStackTrace();
    	} catch (InstantiationException e) {
    		e.printStackTrace();
    	}
    }
	

    public NXTInfo[] search(String name, int protocol) {
		return nxtComm.search(name, protocol);
	}

	public void open(NXTInfo nxt) {
		nxtComm.open(nxt);
	}

	public void setVerify(boolean verify) {
		verifyCommand = verify;
	}

	/**
	 * Small helper method to send request to NXT and return verification result.
	 * @param request
	 * @return
	 */
	private byte sendRequest(byte [] request, int replyLen) {
		byte verify = 0; // default of 0 means success
		if(verifyCommand)
			request[0] = DIRECT_COMMAND_REPLY;
		
		byte [] reply = nxtComm.sendRequest(request,
				                (verifyCommand ? replyLen : 0));
		if(verifyCommand) {
			verify = reply[2];
		}
		return verify;
	}
	
	private byte sendSystemRequest(byte [] request, int replyLen) {
		byte verify = 0; // default of 0 means success
		if(verifyCommand)
			request[0] = SYSTEM_COMMAND_REPLY;
		
		byte [] reply = nxtComm.sendRequest(request,
				                (verifyCommand ? replyLen : 0));
		if(verifyCommand) {
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
	
	// !! Something might be wrong with this because I can't run a program
	// twice in a row after calling this. But other calls work after this.
	public FileInfo openRead(String fileName) {
		byte [] request = {SYSTEM_COMMAND_REPLY, OPEN_READ};
		request = appendString(request, fileName); // No padding required apparently
		byte [] reply = nxtComm.sendRequest(request,8);
		FileInfo fileInfo = new FileInfo(fileName);
		fileInfo.status = reply[2];
		if(reply.length > 3) { // Check if all data included in reply
			fileInfo.fileHandle = reply[3];
			fileInfo.fileSize = (0xFF & reply[4]) | ((0xFF & reply[5]) << 8)| ((0xFF & reply[6]) << 16)| ((0xFF & reply[7]) << 24);
		}
		return fileInfo;
	}

	/**
	 * Opens a file on the NXT for writing.
	 * UNFINISHED
	 * UNTESTED
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
	 * When no files exist within the system, an error message is returned in
	 * the package saying "File not found".
	 * When this command returns a success, a close command is required for "closing
	 * the handle" within the brick when handle is not needed anymore. If an error
	 * is returned, the firmware will close the handle automatically.
	 * @param wildCard [filename].[extension], *.[extension], [filename].*, *.*
	 * @return
	 */
	public FileInfo findFirst(String wildCard) {

		byte [] request = {SYSTEM_COMMAND_REPLY, FIND_FIRST};
		request = appendString(request, wildCard);
		
		// !! Below should be a method shared by System Commands and Direct Commands.
		byte [] reply = nxtComm.sendRequest(request, 28);
		FileInfo fileInfo = null;
		if(reply[2] == 0) {
			fileInfo = new FileInfo("");
			fileInfo.status = reply[2];
			if(reply.length > 3) { // Check if all data included in reply
				fileInfo.fileHandle = reply[3];
				StringBuffer name= new StringBuffer(new String(reply)).delete(24,27).delete(0, 4);
				int lastPos = name.indexOf(".") + 4; // find . in filename, index of last char.
				name.delete(lastPos, name.length());
				fileInfo.fileName = name.toString();
				fileInfo.fileSize = (0xFF & reply[24]) | ((0xFF & reply[25]) << 8)| ((0xFF & reply[26]) << 16)| ((0xFF & reply[27]) << 24);
			}
		}
		return fileInfo;
	}
	
	/**
	 * When no files exist within the system, an error message is returned in
	 * the package saying "File not found".
	 * When this command returns a success, a close command is required for "closing
	 * the handle" within the brick when handle is not needed anymore. If an error
	 * is returned, the firmware will close the handle automatically.
	 * @param handle Handle number from the previous found file or fromthe Find First command.
	 * @return
	 */
	public FileInfo findNext(byte handle) {

		byte [] request = {SYSTEM_COMMAND_REPLY, FIND_NEXT, handle};
		
		// !! Below should be a method shared by System Commands and Direct Commands.
		byte [] reply = nxtComm.sendRequest(request, 28);
		FileInfo fileInfo = null;
		if(reply[2] == 0) {
			fileInfo = new FileInfo("");
			fileInfo.status = reply[2];
			if(reply.length > 3) { // Check if all data included in reply
				fileInfo.fileHandle = reply[3];
				StringBuffer name= new StringBuffer(new String(reply)).delete(24,27).delete(0, 4);
				int lastPos = name.indexOf(".") + 4; // find . in filename, index of last char.
				name.delete(lastPos, name.length());
				fileInfo.fileName = name.toString();
				fileInfo.fileSize = (0xFF & reply[24]) | ((0xFF & reply[25]) << 8)| ((0xFF & reply[26]) << 16)| ((0xFF & reply[27]) << 24);
			}
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
		if(reply[1] != GET_BATTERY_LEVEL)
			System.out.println("Weird data reply received.");
		if(reply[2] != 0)
			System.out.println("NXT reports the check battery command did not work.");
		int batteryLevel = (0xFF & reply[3]) | ((0xFF & reply[4]) << 8);
		return batteryLevel;
	}

	/**
	 * Call the close() command when your program ends, otherwise you
	 * will have to turn the NXT brick off/on before you run another
	 * program using iCommand.
	 *
	 */
	public void close() {
		nxtComm.close();
	}

	public byte writeFile(byte handle, byte [] data) {
		byte [] request = new byte[data.length + 3];
		byte [] command = {SYSTEM_COMMAND_NOREPLY, WRITE, handle};
		System.arraycopy(command, 0, request, 0, command.length);
		System.arraycopy(data, 0, request, 3, data.length);
						
		return sendSystemRequest(command, 6);
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
		return nxtComm.sendRequest(request, length+6); 
	}

	public static NXTCommand getSingleton() {
		return singleton;
	}
}


