package lejos.nxt.remote;

import java.io.*;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Sends LCP requests to the NXT and receives replies.
 * Uses an object that implements the NXTComm interface 
 * for low-level communication.
 *
 */
public class NXTCommand implements NXTProtocol {
	
	public static final int MAX_FILENAMELENGTH = 20;

	private NXTCommRequest nxtComm = null;
	private static NXTCommand singleton = null;
	private boolean verifyCommand = false;
	private boolean open = false;
	private static final String hexChars = "01234567890abcdef";
	private static final int MAX_BUFFER_SIZE = 58;

	/**
	 * Create a NXTCommand object. 
	 */
	public NXTCommand() {
		super();
	}
	
	/**
	 * Set the NXTComm used to communicate with the NXT.
	 * 
	 * @param nxtComm a nxtComm instance which must be connected to a NXT
	 */
	public void setNXTComm(NXTCommRequest nxtComm) {
		open = true;
		this.nxtComm = nxtComm;
	}

	/**
	 * Toggle the verify flag.
	 * 
	 * @param verify true causes all commands to return a response.
	 */
	public void setVerify(boolean verify) {
		verifyCommand = verify;
	}

	/**
	 * Small helper method to send DIRECT COMMAND request to NXT and return
	 * verification result.
	 * 
	 * @param request
	 * @return
	 */
	private byte sendRequest(byte[] request, int replyLen) throws IOException {
		byte verify = 0; // default of 0 means success
		if (verifyCommand)
			request[0] = DIRECT_COMMAND_REPLY;

		byte[] reply = nxtComm.sendRequest(request,
				(request[0] == DIRECT_COMMAND_REPLY ? replyLen : 0));
		if (request[0] == DIRECT_COMMAND_REPLY) {
			verify = reply[2];
		}
		return verify;
	}

	/**
	 * Small helper method to send a SYSTEM COMMAND request to NXT and return
	 * verification result.
	 * 
	 * @param request the request
	 * @return the status
	 */
	private byte sendSystemRequest(byte[] request, int replyLen)
			throws IOException {
		byte verify = 0; // default of 0 means success
		if (verifyCommand)
			request[0] = SYSTEM_COMMAND_REPLY;

		byte[] reply = nxtComm.sendRequest(request,
				(request[0] == SYSTEM_COMMAND_REPLY ? replyLen : 0));
		if (request[0] == SYSTEM_COMMAND_REPLY) {
			verify = reply[2];
		}
		return verify;
	}

	/**
	 * Starts a program already on the NXT.
	 * 
	 * @param fileName the file name
	 * @return the status
	 */
	public byte startProgram(String fileName) throws IOException {
		byte[] request = { DIRECT_COMMAND_NOREPLY, START_PROGRAM };
		request = appendString(request, fileName);
        open = false;
		return sendRequest(request, 22);
	}
	
	/**
	 * Forces the currently executing program to stop.
	 * Not implemented by leJOS NXJ.
	 * 
	 * @return Error value
	 */
	public byte stopProgram() throws IOException {
		byte [] request = {DIRECT_COMMAND_NOREPLY, STOP_PROGRAM};
		return sendRequest(request, 3);
	}
	
	/**
	 * Name of current running program.
	 * Does not work with leJOS NXJ. 
	 * 
	 * @return the program name
	 */
	public String getCurrentProgramName() throws IOException {
		byte [] request = {DIRECT_COMMAND_REPLY, GET_CURRENT_PROGRAM_NAME};
		byte [] reply =  nxtComm.sendRequest(request, 23);
		
		return new StringBuffer(new String(reply)).delete(0, 2).toString();
	}

	/**
	 * Opens a file on the NXT for reading. Returns a handle number and file
	 * size, enclosed in a FileInfo object.
	 * 
	 * @param fileName
	 *            e.g. "Woops.wav"
	 * @return fileInfo object giving details of the file
	 */
	public FileInfo openRead(String fileName) throws IOException {
		byte[] request = { SYSTEM_COMMAND_REPLY, OPEN_READ };
		request = appendString(request, fileName); // No padding required
													// apparently
		byte[] reply = nxtComm.sendRequest(request, 8);
		FileInfo fileInfo = new FileInfo(fileName);
		fileInfo.status = reply[2];
		if (reply.length == 8) { // Check if all data included in reply
			fileInfo.fileHandle = reply[3];
			fileInfo.fileSize = (0xFF & reply[4]) | ((0xFF & reply[5]) << 8)
					| ((0xFF & reply[6]) << 16) | ((0xFF & reply[7]) << 24);
		}
		return fileInfo;
	}

	/**
	 * Opens a file on the NXT for writing.
	 * 
	 * @param fileName
	 *            e.g. "Woops.wav"
	 *            
	 * @return File Handle number
	 */
	public byte openWrite(String fileName, int size) throws IOException {
		byte[] command = { SYSTEM_COMMAND_REPLY, OPEN_WRITE };
		byte[] asciiFileName = new byte[fileName.length()];
		for (int i = 0; i < fileName.length(); i++)
			asciiFileName[i] = (byte) fileName.charAt(i);
		command = appendBytes(command, asciiFileName);
		byte[] request = new byte[22];
		System.arraycopy(command, 0, request, 0, command.length);
		byte[] fileLength = { (byte) size, (byte) (size >>> 8),
				(byte) (size >>> 16), (byte) (size >>> 24) };
		request = appendBytes(request, fileLength);
		byte[] reply = nxtComm.sendRequest(request, 4);
		if (reply == null || reply.length != 4) {
			throw new IOException("Invalid return from OPEN WRITE");
		} else if (reply[2] != 0) {
			if (reply[2] == (byte) 0xFB) throw new IOException("NXJ Flash Memory Full");
			else if (reply[2] == (byte) 0xFC) throw new IOException("NXJ Directory Full");
			else throw new IOException("OPEN WRITE failed");
		}
		return reply[3]; // The handle number
	}

	/**
	 * Closes an open file.
	 * 
	 * @param handle
	 *            File handle number.
	 * @return Error code 0 = success
	 * @throws IOException
	 */
	public byte closeFile(byte handle) throws IOException {
		byte[] request = { SYSTEM_COMMAND_NOREPLY, CLOSE, handle };
		return sendSystemRequest(request, 4);
	}

	/**
	 * Delete a file on the NXT
	 * 
	 * @param fileName the name of the file
	 * @return the error code 0 = success
	 * @throws IOException
	 */
	public byte delete(String fileName) throws IOException {
		byte[] request = { SYSTEM_COMMAND_REPLY, DELETE };
		request = appendString(request, fileName);
		return sendSystemRequest(request, 23);
	}

	/**
	 * Find the first file on the NXT. This is a NXJ-specific version that returns the
	 * start page number as well as the other FileInfo data
	 * 
	 * @param wildCard
	 *            [filename].[extension], *.[extension], [filename].*, *.*
	 * @return fileInfo object giving details of the file
	 */
	public FileInfo findFirstNXJ(String wildCard) throws IOException {

		byte[] request = { SYSTEM_COMMAND_REPLY, NXJ_FIND_FIRST };
		request = appendString(request, wildCard);

		byte[] reply = nxtComm.sendRequest(request, 32);
		FileInfo fileInfo = null;
		if (reply[2] == 0 && reply.length == 32) {
			StringBuffer name = new StringBuffer(new String(reply))
					.delete(0, 4);
			int lastPos = name.indexOf("\0");
			if (lastPos < 0 || lastPos > 20) lastPos = 20;
			name.delete(lastPos, name.length());
			fileInfo = new FileInfo(name.toString());
			fileInfo.status = 0;
			fileInfo.fileHandle = reply[3];
			fileInfo.fileSize = (0xFF & reply[24]) | ((0xFF & reply[25]) << 8)
					| ((0xFF & reply[26]) << 16) | ((0xFF & reply[27]) << 24);
			fileInfo.startPage = (0xFF & reply[28]) | ((0xFF & reply[29]) << 8)
					| ((0xFF & reply[30]) << 16) | ((0xFF & reply[31]) << 24);

		}
		return fileInfo;
	}
	
	/**
	 * Find the first file on the NXT.
	 * 
	 * @param wildCard
	 *            [filename].[extension], *.[extension], [filename].*, *.*
	 * @return fileInfo object giving details of the file
	 */
	public FileInfo findFirst(String wildCard) throws IOException {

		byte[] request = { SYSTEM_COMMAND_REPLY, FIND_FIRST };
		request = appendString(request, wildCard);

		byte[] reply = nxtComm.sendRequest(request, 28);
		FileInfo fileInfo = null;
		if (reply[2] == 0 && reply.length == 28) {
			StringBuffer name = new StringBuffer(new String(reply))
					.delete(0, 4);
			int lastPos = name.indexOf("\0");
			if (lastPos < 0 || lastPos > 20) lastPos = 20;
			name.delete(lastPos, name.length());
			fileInfo = new FileInfo(name.toString());
			fileInfo.status = 0;
			fileInfo.fileHandle = reply[3];
			fileInfo.fileSize = (0xFF & reply[24]) | ((0xFF & reply[25]) << 8)
					| ((0xFF & reply[26]) << 16) | ((0xFF & reply[27]) << 24);
			fileInfo.startPage = -1;

		}
		return fileInfo;
	}

	/**
	 * Find the next file on the NXT. This is a NXJ-specific version that returns the
	 * start page number as well as the other FileInfo data
	 * 
	 * @param handle
	 *            Handle number from the previous found file or from the Find
	 *            First command.
	 * @return fileInfo object giving details of the file
	 */
	public FileInfo findNextNXJ(byte handle) throws IOException {

		byte[] request = { SYSTEM_COMMAND_REPLY, NXJ_FIND_NEXT, handle };

		byte[] reply = nxtComm.sendRequest(request, 32);
		FileInfo fileInfo = null;
		if (reply[2] == 0 && reply.length == 32) {
			StringBuffer name = new StringBuffer(new String(reply))
					.delete(0, 4);
			int lastPos = name.indexOf("\0");
			if (lastPos < 0 || lastPos > 20) lastPos = 20;
			name.delete(lastPos, name.length());
			fileInfo = new FileInfo(name.toString());
			fileInfo.status = 0;
			fileInfo.fileHandle = reply[3];
			fileInfo.fileSize = (0xFF & reply[24]) | ((0xFF & reply[25]) << 8)
					| ((0xFF & reply[26]) << 16) | ((0xFF & reply[27]) << 24);
			fileInfo.startPage = (0xFF & reply[28]) | ((0xFF & reply[29]) << 8)
					| ((0xFF & reply[30]) << 16) | ((0xFF & reply[31]) << 24);
		}
		return fileInfo;
	}
	
	/**
	 * Find the next file on the NXT
	 * 
	 * @param handle
	 *            Handle number from the previous found file or from the Find
	 *            First command.
	 * @return fileInfo object giving details of the file
	 */
	public FileInfo findNext(byte handle) throws IOException {

		byte[] request = { SYSTEM_COMMAND_REPLY, FIND_NEXT, handle };

		byte[] reply = nxtComm.sendRequest(request, 28);
		FileInfo fileInfo = null;
		if (reply[2] == 0 && reply.length == 28) {
			StringBuffer name = new StringBuffer(new String(reply))
					.delete(0, 4);
			int lastPos = name.indexOf("\0");
			if (lastPos < 0 || lastPos > 20) lastPos = 20;
			name.delete(lastPos, name.length());
			fileInfo = new FileInfo(name.toString());
			fileInfo.status = 0;
			fileInfo.fileHandle = reply[3];
			fileInfo.fileSize = (0xFF & reply[24]) | ((0xFF & reply[25]) << 8)
					| ((0xFF & reply[26]) << 16) | ((0xFF & reply[27]) << 24);
			fileInfo.startPage = -1;
		}
		return fileInfo;
	}

	/**
	 * Helper code to append a string and null terminator at the end of a
	 * command request. 
	 * 
	 * @param command the command
	 * @param str the string to append
	 * @return the concatenated command
	 */
	private byte[] appendString(byte[] command, String str) {
		byte[] buff = new byte[command.length + str.length() + 1];
		for (int i = 0; i < command.length; i++)
			buff[i] = command[i];
		for (int i = 0; i < str.length(); i++)
			buff[command.length + i] = (byte) str.charAt(i);
		buff[command.length + str.length()] = 0;
		return buff;
	}

	/**
	 * Helper method to concatenate two byte arrays
	 * @param array1 the first array (e.g. a request)
	 * @param array2 the second array (e.g. an extra parameter)
	 * 
	 * @return the concatenated array
	 */
	private byte[] appendBytes(byte[] array1, byte[] array2) {
		byte[] array = new byte[array1.length + array2.length];
		System.arraycopy(array1, 0, array, 0, array1.length);
		System.arraycopy(array2, 0, array, array1.length, array2.length);
		return array;
	}

	/**
	 * Get the battery reading
	 * 
	 * @return the battery level in millivolts
	 * @throws IOException
	 */
	public int getBatteryLevel() throws IOException {
		byte[] request = { DIRECT_COMMAND_REPLY, GET_BATTERY_LEVEL };
		byte[] reply = nxtComm.sendRequest(request, 5);
		int batteryLevel = (0xFF & reply[3]) | ((0xFF & reply[4]) << 8);
		return batteryLevel;
	}

	/**
	 * Call the close() command when your program ends, otherwise you will have
	 * to turn the NXT brick off/on before you run another program.
	 * 
	 */
	public void close() throws IOException {
		if (!open) return;
		open = false;
		byte[] request = { SYSTEM_COMMAND_REPLY, NXJ_DISCONNECT };
		nxtComm.sendRequest(request, 3); // Tell NXT to disconnect
		nxtComm.close();
	}

	/**
	 * Put the NXT into SAMBA mode, ready to update the firmware
	 *
	 * @throws IOException
	 */
    public void boot() throws IOException {
        byte[] request = {SYSTEM_COMMAND_NOREPLY, BOOT};
        request = appendString(request, "Let's dance: SAMBA");
        nxtComm.sendRequest(request, 0);
        // Connection cannot be used after this command so we close it
        nxtComm.close();
        open = false;
    }
    
    /**
     * Write data to the file
     * 
     * @param handle the file handle
     * @param data the data to write
     * @return the status value
     * 
     * @throws IOException
     */
	public byte writeFile(byte handle, byte[] data) throws IOException {
		byte[] command = { SYSTEM_COMMAND_NOREPLY, WRITE, handle };		
		int remaining = data.length;
		int chunkStart = 0;
		while (remaining > 0) {
			int chunkLen = MAX_BUFFER_SIZE;
			if (remaining < chunkLen) chunkLen = remaining;
			byte [] request = new byte[chunkLen + 3];
			System.arraycopy(command, 0, request, 0, command.length);
			System.arraycopy(data, chunkStart, request, 3, chunkLen);

			byte status = sendSystemRequest(request, 6);
			if (status != 0) return status;
			remaining -= chunkLen;
			chunkStart += chunkLen;
		}
		return 0;
	}
	
	/**
	 * Upload a file to the NXT
	 * 
	 * @param file the file to upload
	 * @param nxtFileName the name of the file on the NXT
	 * @return a message saying how long it took to upload the file
	 * 
	 * @throws IOException
	 */
	public String uploadFile(File file, String nxtFileName) throws IOException {
	    long millis = System.currentTimeMillis();
	    FileInputStream in = new FileInputStream(file);
	    try
	    {
			byte handle = openWrite(nxtFileName, (int) file.length());
			try
			{
				byte[] data = new byte[MAX_BUFFER_SIZE];
				int len;
				while ((len = in.read(data)) > 0)
				{
					byte[] sendData = new byte[len];
					System.arraycopy(data, 0, sendData, 0, len);
					writeFile(handle, sendData);
				}
			}
			catch (IOException ioe)
			{
				throw new IOException("Failed to upload");
			}
			setVerify(true);
			closeFile(handle);
			return "Upload successful in " + (System.currentTimeMillis() - millis) + " milliseconds";
	    }
	    finally
	    {
	    	in.close();
	    }
	}

	/**
	 * Returns requested number of bytes from a file. File must first be opened
	 * using the openRead() command.
	 * 
	 * @param handle
	 *            File handle number (from openRead method)
	 * @param length
	 *            Number of bytes to read.
	 * @return the bytes requested
	 */
	public byte[] readFile(byte handle, int length) throws IOException {
		int remaining = length;
		int chunkStart = 0;
		byte[] reply = new byte[length];
		while (remaining > 0) {
			int chunkLen = MAX_BUFFER_SIZE;
			if (chunkLen > remaining) chunkLen = remaining;
			byte[] request = { SYSTEM_COMMAND_REPLY, READ, handle, (byte) chunkLen,
					(byte) (chunkLen >>> 8) };
			byte[] reply1 = nxtComm.sendRequest(request, chunkLen + 6);
			int dataLen = (reply1[4] & 0xFF) + ((reply1[5] << 8) & 0xFF);
			for (int i = 0; i < dataLen; i++)
				reply[chunkStart+i] = reply1[i + 6];
			chunkStart += chunkLen;
			remaining -= chunkLen;
		}

		return reply;
	}

	/**
	 * A NXJ extension to defrag the file system
	 * 
	 * @return the status byte
	 * @throws IOException
	 */
	public byte defrag() throws IOException {
		byte[] request = { SYSTEM_COMMAND_NOREPLY, NXJ_DEFRAG };
		return sendSystemRequest(request, 3);
	}

	/**
	 * Get the friendly name of the NXT
	 * @return the friendly name
	 * @throws IOException
	 */
	public String getFriendlyName() throws IOException {
		byte[] request = { SYSTEM_COMMAND_REPLY, GET_DEVICE_INFO };
		byte[] reply = nxtComm.sendRequest(request, 33);
		char nameChars[] = new char[16];
		int len = 0;

		for (int i = 0; i < 15 && reply[i + 3] != 0; i++) {
			nameChars[i] = (char) reply[i + 3];
			len++;
		}

		return new String(nameChars, 0, len);
	}

	/**
	 * Set the friendly name of the NXT
	 * 
	 * @param name the friendly name
	 * @return the status byte
	 * @throws IOException
	 */
	public byte setFriendlyName(String name) throws IOException {
		byte[] request = { SYSTEM_COMMAND_NOREPLY, SET_BRICK_NAME };
		request = appendString(request, name);

		return sendSystemRequest(request, 3);
	}

	/**
	 * Get the local address of the NXT.
	 * 
	 * @return the address (used by USB and Bluetooth)
	 * @throws IOException
	 */
	public String getLocalAddress() throws IOException {
		byte[] request = { SYSTEM_COMMAND_REPLY, GET_DEVICE_INFO };
		byte[] reply = nxtComm.sendRequest(request, 33);
		char addrChars[] = new char[14];

		for (int i = 0; i < 7; i++) {
			addrChars[i * 2] = hexChars.charAt((reply[i + 18] >> 4) & 0xF);
			addrChars[i * 2 + 1] = hexChars.charAt(reply[i + 18] & 0xF);
		}
		
		return new String(addrChars);
	}
	
	/**
	 * Get input values for a specific NXT sensor port
	 * 
	 * @param port the port number
	 * @return the InputValues structure
	 * @throws IOException
	 */
	public InputValues getInputValues(int port) throws IOException {
		byte [] request = {DIRECT_COMMAND_REPLY, GET_INPUT_VALUES, (byte)port};
		byte [] reply = nxtComm.sendRequest(request, 16);
		InputValues inputValues = new InputValues();
		inputValues.inputPort = reply[3];
		// 0 is false, 1 is true.
		inputValues.valid = (reply[4] != 0);
		// 0 is false, 1 is true. 
		inputValues.isCalibrated = (reply[5] == 0);
		inputValues.sensorType = reply[6];
		inputValues.sensorMode = reply[7];
		inputValues.rawADValue = (0xFF & reply[8]) | ((0xFF & reply[9]) << 8);
		inputValues.normalizedADValue = (0xFF & reply[10]) | ((0xFF & reply[11]) << 8);
		inputValues.scaledValue = (short)((0xFF & reply[12]) | (reply[13] << 8));
		inputValues.calibratedValue = (short)((0xFF & reply[14]) | (reply[15] << 8));
		
		return inputValues;
	}
	
	/**
	 * Retrieves the current output state for a port.
	 * @param port - 0 to 3
	 * @return OutputState - returns a container object for output state variables.
	 */
	public OutputState getOutputState(int port) throws IOException {
		// !! Needs to check port to verify they are correct ranges.
		byte [] request = {DIRECT_COMMAND_REPLY, GET_OUTPUT_STATE, (byte)port};
		byte [] reply = nxtComm.sendRequest(request,25);

		OutputState outputState = new OutputState(port);
		outputState.status = reply[2];
		outputState.outputPort = reply[3];
		outputState.powerSetpoint = reply[4];
		outputState.mode = reply[5];
		outputState.regulationMode = reply[6];
		outputState.turnRatio = reply[7];
		outputState.runState = reply[8];
		outputState.tachoLimit = (0xFF & reply[9]) | ((0xFF & reply[10]) << 8)| ((0xFF & reply[11]) << 16)| ((0xFF & reply[12]) << 24);
		outputState.tachoCount = (0xFF & reply[13]) | ((0xFF & reply[14]) << 8)| ((0xFF & reply[15]) << 16)| ((0xFF & reply[16]) << 24);
		outputState.blockTachoCount = (0xFF & reply[17]) | ((0xFF & reply[18]) << 8)| ((0xFF & reply[19]) << 16)| ((0xFF & reply[20]) << 24);
		outputState.rotationCount = (0xFF & reply[21]) | ((0xFF & reply[22]) << 8)| ((0xFF & reply[23]) << 16)| ((0xFF & reply[24]) << 24);
		return outputState;
	}
	
	/**
	 * Retrieves tacho count.
	 * @param port - 0 to 3
	 * @return tacho count
	 */
	public int getTachoCount(int port) throws IOException {
		synchronized(this) {
			byte [] request = {DIRECT_COMMAND_REPLY, GET_OUTPUT_STATE, (byte)port};
			byte [] reply = nxtComm.sendRequest(request, 25);
	
			int tachoCount = (0xFF & reply[13]) | ((0xFF & reply[14]) << 8)| ((0xFF & reply[15]) << 16)| ((0xFF & reply[16]) << 24);
			return tachoCount;
		}
	}
	
	/**
	 * Tells the NXT what type of sensor you are using and the mode to operate in.
	 * @param port - 0 to 3
	 * @param sensorType - Enumeration for sensor type (see NXTProtocol) 
	 * @param sensorMode - Enumeration for sensor mode (see NXTProtocol)
	 */
	public byte setInputMode(int port, int sensorType, int sensorMode) throws IOException {
		// !! Needs to check port to verify they are correct ranges.
		byte [] request = {DIRECT_COMMAND_NOREPLY, SET_INPUT_MODE, (byte)port, (byte)sensorType, (byte)sensorMode};
		return sendRequest(request, 3);
	}
	
	/**
	 * Returns the status for an Inter-Integrated Circuit (I2C) sensor (the 
	 * ultrasound sensor) via the Low Speed (LS) data port. The port must first 
	 * be configured to type LOWSPEED or LOWSPEED_9V.
	 * @param port 0-3
	 * @return byte[0] = status, byte[1] = Bytes Ready (count of available bytes to read)
	 */
	public byte [] LSGetStatus(byte port) throws IOException{
		byte [] request = {DIRECT_COMMAND_REPLY, LS_GET_STATUS, port};
		byte [] reply = nxtComm.sendRequest(request,4);
		byte [] returnData = {reply[2], reply[3]}; 
		return returnData;
	}
	
	/**
	 * Reads data from an Inter-Integrated Circuit (I2C) sensor (the 
	 * ultrasound sensor) via the Low Speed (LS) data port. The port must 
	 * first be configured to type LOWSPEED or LOWSPEED_9V.
	 * Data lengths are limited to 16 bytes per command. The response will
	 * also contain 16 bytes, with invalid data padded with zeros.
	 * @param port
	 * @return the response
	 */
	public byte [] LSRead(byte port) throws IOException {
		byte [] request = {DIRECT_COMMAND_REPLY, LS_READ, port};
		byte [] reply = nxtComm.sendRequest(request, 20);
		
		byte rxLength = reply[3];
		if(reply[2] == 0 && rxLength >= 0) {
            byte [] rxData = new byte[rxLength];
			System.arraycopy(reply, 4, rxData, 0, rxLength);
            return rxData;
		} else {
			return null;
		}
	}
	
	/**
	 * Used to request data from an Inter-Integrated Circuit (I2C) sensor (the 
	 * ultrasound sensor) via the Low Speed (LS) data port. The port must first 
	 * be configured to type  LOWSPEED or LOWSPEED_9V.
	 * Data lengths are limited to 16 bytes per command.
	 * Rx (receive) Data Length MUST be specified in the write
	 * command since reading from the device is done on a 
	 * master-slave basis.
	 * @param txData Transmitted data.
	 * @param rxDataLength Receive data length.
	 * @param port 0-3
	 * @return the status (0 = success)
	 */
	public byte LSWrite(byte port, byte [] txData, byte rxDataLength) throws IOException {
		byte [] request = {DIRECT_COMMAND_NOREPLY, LS_WRITE, port, (byte)txData.length, rxDataLength};
		request = appendBytes(request, txData);
		return sendRequest(request, 3);
	}
	
	/**
	 * @param remoteInbox 0-9
	 * @param localInbox 0-9
	 * @param remove True clears the message from the remote inbox.
	 * @return the message as an array of bytes
	 */
	public byte[] messageRead(byte remoteInbox, byte localInbox, boolean remove) throws IOException {
		byte [] request = {DIRECT_COMMAND_REPLY, MESSAGE_READ, remoteInbox, localInbox, (remove ? (byte) 1 : (byte) 0)};
		byte [] reply = nxtComm.sendRequest(request, 64);
		byte[] message = new byte[reply[4]];
		System.arraycopy(reply, 5, message, 0, reply[4]);
		return message;
	}
	
	/**
	 * Sends a message to an inbox on the NXT for storage(?)
	 * For future reference, message size must be capped at 59 for USB.
	 * UNTESTED
	 * @param message String to send. A null termination is automatically appended.
	 * @param inbox Inbox Number 0 - 9
	 * @return the status (0 = success)
	 */
	public byte messageWrite(byte [] message, byte inbox) throws IOException {
		byte [] request = {DIRECT_COMMAND_NOREPLY, MESSAGE_WRITE, inbox, (byte)(message.length)};
		request = appendBytes(request, message);
		return sendRequest(request, 3);
	}
	
	/**
	 * Plays a tone on NXT speaker. If a new tone is sent while the previous tone is playing,
	 * the new tone command will stop the old tone command.
	 * @param frequency - 100 to 2000?
	 * @param duration - In milliseconds.
	 * @return - Returns true if command worked, false if it failed.
	 */
	public byte playTone(int frequency, int duration) throws IOException {
		byte [] request = {DIRECT_COMMAND_NOREPLY, PLAY_TONE, (byte)frequency, (byte)(frequency>>>8), (byte)duration, (byte)(duration>>>8)};
		return sendRequest(request, 3);
	}
	
	public byte playSoundFile(String fileName, boolean repeat) throws IOException {
		
		byte boolVal = 0;
		if(repeat) boolVal = (byte)0xFF; // Convert boolean to number
		
		byte [] request = {DIRECT_COMMAND_NOREPLY, PLAY_SOUND_FILE, boolVal};
		byte[] encFileName = null;
		try {
			encFileName = AsciizCodec.encode(fileName);
		} catch (UnsupportedEncodingException e) {
			System.err.println("Illegal characters in filename");
			return -1;
		}
		request = appendBytes(request, encFileName);
		return sendRequest(request, 3);
	}
	
	/**
	 * Stops sound file playing.
	 * @return the status (0 = success)
	 */
	public byte stopSoundPlayback() throws IOException {
		byte [] request = {DIRECT_COMMAND_NOREPLY, STOP_SOUND_PLAYBACK};
		return sendRequest(request, 3);
	}
	
	/**
	 * Resets either RotationCount or BlockTacho
	 * @param port Output port (0-2)
	 * @param relative TRUE: BlockTacho, FALSE: RotationCount
	 * @return the status (0 = success)
	 */
	public byte resetMotorPosition(int port, boolean relative) throws IOException {
		// !! Needs to check port to verify they are correct ranges.
		// !!! I'm not sure I'm sending boolean properly
		byte boolVal = 0;
		if(relative) boolVal = (byte)0xFF;
		byte [] request = {DIRECT_COMMAND_NOREPLY, RESET_MOTOR_POSITION, (byte)port, boolVal};
		return sendRequest(request, 3);
	}
	
	/**
	 * 
	 * @param port - Output port (0 - 2 or 0xFF for all three)
	 * @param power - Setpoint for power. (-100 to 100)
	 * @param mode - Setting the modes MOTORON, BRAKE, and/or REGULATED. This parameter is a bitfield, so to put it in brake mode and regulated, use BRAKEMODE + REGULATED
	 * @param regulationMode - see NXTProtocol for enumerations 
	 * @param turnRatio - Need two motors? (-100 to 100)
	 * @param runState - see NXTProtocol for enumerations
	 * @param tachoLimit - Number of degrees(?) to rotate before stopping.
	 * @return the status (0 = success)
	 */
	public byte setOutputState(int port, byte power, int mode, int regulationMode, int turnRatio, int runState, int tachoLimit) throws IOException {
		// !! Needs to check port, power to verify they are correct ranges.
		byte [] request = {DIRECT_COMMAND_NOREPLY, SET_OUTPUT_STATE, (byte)port, power, (byte)mode, (byte)regulationMode, (byte)turnRatio, (byte)runState, (byte)tachoLimit, (byte)(tachoLimit>>>8), (byte)(tachoLimit>>>16), (byte)(tachoLimit>>>24)};
		return sendRequest(request, 3);
	}
	
	/**
	 * Gets device information
	 * 
	 * @return a DeviceInfo structure
	 * @throws IOException
	 */
	public DeviceInfo getDeviceInfo() throws IOException {
		// !! Needs to check port to verify they are correct ranges.
		byte [] request = {SYSTEM_COMMAND_REPLY, GET_DEVICE_INFO};
		byte [] reply = nxtComm.sendRequest(request, 33);
		DeviceInfo d = new DeviceInfo();
		d.status = reply[2];
		d.NXTname = new StringBuffer(new String(reply)).delete(18,33).delete(0, 3).toString();
		d.bluetoothAddress = Integer.toHexString(reply[18]) + ":" + Integer.toHexString(reply[19]) + ":" + Integer.toHexString(reply[20]) + ":" + Integer.toHexString(reply[21]) + ":" + Integer.toHexString(reply[22]) + ":" + Integer.toHexString(reply[23]) + ":" + Integer.toHexString(reply[24]);
		d.signalStrength = (0xFF & reply[25]) | ((0xFF & reply[26]) << 8)| ((0xFF & reply[27]) << 16)| ((0xFF & reply[28]) << 24);
		d.freeFlash = (0xFF & reply[29]) | ((0xFF & reply[30]) << 8)| ((0xFF & reply[31]) << 16)| ((0xFF & reply[32]) << 24);
		return d;
	}
	
	/**
	 * Get the fimrware version.
	 * leJOS NXJ returns the version of the LEGO firmware that it emulates,
	 * not its own version number.
	 * 
	 * @return a FirmwareInfo structure.
	 * @throws IOException
	 */
	public FirmwareInfo getFirmwareVersion() throws IOException {
		byte [] request = {SYSTEM_COMMAND_REPLY, GET_FIRMWARE_VERSION};
		byte [] reply = nxtComm.sendRequest(request, 7);
		FirmwareInfo info = new FirmwareInfo();
		info.status = reply[2];
		if(info.status == 0) {
			info.protocolVersion = reply[4] + "." + reply[3];
			info.firmwareVersion = reply[6] + "." + reply[5];
		}
		return info;
	}
	
	/**
	 * Deletes user flash memory.
	 * Not implemented by leJOS NXJ.
	 * @return the status (0 = success)
	 */
	public byte deleteUserFlash() throws IOException {
		byte [] request = {SYSTEM_COMMAND_REPLY, DELETE_USER_FLASH};
		byte [] reply = nxtComm.sendRequest(request, 3);
		return reply[2];
	}
	
	/**
	 * Get the singleton NXTCommand object. Use of this is optional.
	 * 
	 * @return the singleton NXTCommand instance
	 */
	public static NXTCommand getSingleton() {
		if (singleton == null) singleton = new NXTCommand();
		return singleton;
	}
	
	/**
	 * Test is connection is open
	 * 
	 * @return true iff the connection is open
	 */
	public boolean isOpen() {
		return open;
	}
}
