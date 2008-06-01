package lejos.pc.comm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * Sends LCP requests to the NXT and receives replies.
 * Uses an object that implements the NXTComm interface 
 * for low-level communication.
 *
 */
public class NXTCommand implements NXTProtocol {

	private Collection<NXTCommLogListener> fLogListeners;
	private NXTComm nxtComm = null, nxtCommUSB = null, nxtCommBluetooth = null;
	private static NXTCommand singleton = null;
	private boolean verifyCommand = false;
	private boolean open = false;
	private static String hexChars = "01234567890abcdef";

	private NXTCommand() {
		fLogListeners = new ArrayList<NXTCommLogListener>();
	}

	public NXTInfo[] search(String name, int protocol) throws NXTCommException {
		NXTInfo[] nxtInfos;

		if (nxtComm == null) {
			try {
				// Look for USB comms driver first				
				if ((protocol & NXTCommFactory.USB) != 0) {
					nxtCommUSB = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
				}
			} catch (NXTCommException e) {
				log(e);
			}

			try {
				// Look for Bluetooth Comms driver				
				if ((protocol & NXTCommFactory.BLUETOOTH) != 0) {
					nxtCommBluetooth = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
				}
			} catch (NXTCommException e) {
				log(e);
			}

			if (nxtCommUSB == null && nxtCommBluetooth == null) {
				throw new NXTCommException("Cannot load a comm driver");
			}
		}

		// Look for a USB one first

		if ((protocol & NXTCommFactory.USB) != 0 && nxtCommUSB != null) {
			nxtInfos = nxtCommUSB.search(name, protocol);
			if (nxtInfos.length > 0) {
				nxtComm = nxtCommUSB;
				return nxtInfos;
			}
		}

		// If not found, look for a Bluetooth one

		if ((protocol & NXTCommFactory.BLUETOOTH) != 0
				&& nxtCommBluetooth != null) {
			nxtInfos = nxtCommBluetooth.search(name, protocol);
			if (nxtInfos.length > 0) {
				nxtComm = nxtCommBluetooth;
				return nxtInfos;
			}
		}

		return new NXTInfo[0];
	}

	public void setNXTCommBlueTooth() throws NXTCommException {
		if (nxtComm == null) {
			nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		}
	}

	public boolean open(NXTInfo nxt) throws NXTCommException {
		return open = nxtComm.open(nxt);
	}

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
	 * @param request
	 * @return
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
	 * @param fileName
	 * @return the status
	 */
	public byte startProgram(String fileName) throws IOException {
		byte[] request = { DIRECT_COMMAND_NOREPLY, START_PROGRAM };
		request = appendString(request, fileName);
		return sendRequest(request, 22);
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
	 *            e.g. "Woops.rso"
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
	 */
	public byte closeFile(byte handle) throws IOException {
		byte[] request = { SYSTEM_COMMAND_NOREPLY, CLOSE, handle };
		return sendSystemRequest(request, 4);
	}

	public byte delete(String fileName) throws IOException {
		byte[] request = { SYSTEM_COMMAND_REPLY, DELETE };
		request = appendString(request, fileName);
		return sendSystemRequest(request, 23);
	}

	/**
	 * @param wildCard
	 *            [filename].[extension], *.[extension], [filename].*, *.*
	 * @return fileInfo object giving details of the file
	 */
	public FileInfo findFirst(String wildCard) throws IOException {

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
	 * @param handle
	 *            Handle number from the previous found file or fromthe Find
	 *            First command.
	 * @return fileInfo object giving details of the file
	 */
	public FileInfo findNext(byte handle) throws IOException {

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
	 * Helper code to append a string and null terminator at the end of a
	 * command request. Should use String.concat if I could add a zero to end
	 * somehow.
	 * 
	 * @param command
	 * @param str
	 * @return
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

	private byte[] appendBytes(byte[] array1, byte[] array2) {
		byte[] array = new byte[array1.length + array2.length];
		System.arraycopy(array1, 0, array, 0, array1.length);
		System.arraycopy(array2, 0, array, array1.length, array2.length);
		return array;
	}

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
		if (!open)
			return;
		open = false;
		byte[] request = { SYSTEM_COMMAND_NOREPLY, NXJ_DISCONNECT };
		nxtComm.sendRequest(request, 0); // Tell NXT to disconnect
		nxtComm.close();
	}

	public byte writeFile(byte handle, byte[] data) throws IOException {
		byte[] request = new byte[data.length + 3];
		byte[] command = { SYSTEM_COMMAND_NOREPLY, WRITE, handle };
		System.arraycopy(command, 0, request, 0, command.length);
		System.arraycopy(data, 0, request, 3, data.length);

		return sendSystemRequest(request, 6);
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
		byte[] request = { SYSTEM_COMMAND_REPLY, READ, handle, (byte) length,
				(byte) (length >>> 8) };
		byte[] reply1 = nxtComm.sendRequest(request, length + 6);
		int dataLen = (reply1[4] & 0xFF) + ((reply1[5] << 8) & 0xFF);
		byte[] reply = new byte[dataLen];
		for (int i = 0; i < dataLen; i++)
			reply[i] = reply1[i + 6];
		return reply;
	}

	public byte defrag() throws IOException {
		byte[] request = { SYSTEM_COMMAND_NOREPLY, NXJ_DEFRAG };
		return sendSystemRequest(request, 3);
	}

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

	public byte setFriendlyName(String name) throws IOException {
		byte[] request = { SYSTEM_COMMAND_NOREPLY, SET_BRICK_NAME };
		request = appendString(request, name);

		return sendSystemRequest(request, 3);
	}

	public String getLocalAddress() throws IOException {
		byte[] request = { SYSTEM_COMMAND_REPLY, GET_DEVICE_INFO };
		byte[] reply = nxtComm.sendRequest(request, 33);
		char addrChars[] = new char[14];

		for (int i = 0; i < 7; i++) {
			// log("Addr char " + i + " = " + (reply[i+18] &
			// 0xFF));
			addrChars[i * 2] = hexChars.charAt((reply[i + 18] >> 4) & 0xF);
			addrChars[i * 2 + 1] = hexChars.charAt(reply[i + 18] & 0xF);
		}

		return new String(addrChars);
	}
	
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
		
		if(reply[1] != GET_OUTPUT_STATE) {
			System.out.println("Oops! Error in NXTCommand.getOutputState.");
			System.out.println("Return data did not match request.");
			System.out.println("reply[0] = " + reply[0] + "  reply[1] = " + reply[1] +"  reply[2] = " + reply[2]);
		}
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
	
	public static NXTCommand getSingleton() {
		if (singleton == null)
			singleton = new NXTCommand();
		return singleton;
	}

	/**
	 * register log listener
	 * 
	 * @param listener
	 */
	public void addLogListener(NXTCommLogListener listener) {
		fLogListeners.add(listener);
	}

	/**
	 * unregister log listener
	 * 
	 * @param listener
	 */
	public void removeLogListener(NXTCommLogListener listener) {
		fLogListeners.remove(listener);
	}

	private void log(String message) {
		for (NXTCommLogListener listener : fLogListeners) {
			listener.logEvent(message);
		}
	}

	private void log(Throwable t) {
		for (NXTCommLogListener listener : fLogListeners) {
			listener.logEvent(t);
		}
	}
	
}
