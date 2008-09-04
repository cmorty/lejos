package lejos.nxt.remote;

import java.io.*;

/**
 * 
 * Remote access to a NXT via Bluetooth using LCP.
 *
 */
public class NXTCommand implements NXTProtocol {
	
	private NXTComm nxtComm = new NXTComm();
	private boolean verifyCommand = false;
	
	public void open(String name) throws IOException {
		boolean open = nxtComm.open(name);
		if (!open) throw new IOException("Open failed");
	}
	
	/**
	 * Small helper method to send request to NXT and return verification result.
	 * @param request
	 * @return 0 for success
	 */
	private byte sendRequest(byte [] request) throws IOException {
		byte verify = 0; // default of 0 means success
		if(verifyCommand)
			request[0] = DIRECT_COMMAND_REPLY;
		
		nxtComm.sendData(request);
		if(verifyCommand) {
			byte [] reply = nxtComm.readData();
			verify = reply[2];
		}
		return verify;
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
	 */
	public byte setOutputState(int port, byte power, int mode, int regulationMode, int turnRatio, int runState, int tachoLimit) throws IOException {
		synchronized(this) {
			byte [] request = {DIRECT_COMMAND_NOREPLY, SET_OUTPUT_STATE, (byte)port, power, (byte)mode, (byte)regulationMode, (byte)turnRatio, (byte)runState, (byte)tachoLimit, (byte)(tachoLimit>>>8), (byte)(tachoLimit>>>16), (byte)(tachoLimit>>>24)};
			return sendRequest(request);
		}
	}
	
	/**
	 * Retrieves tacho count.
	 * @param port - 0 to 3
	 * @return tacho count
	 */
	public int getTachoCount(int port) throws IOException {
		synchronized(this) {
			byte [] request = {DIRECT_COMMAND_REPLY, GET_OUTPUT_STATE, (byte)port};
			nxtComm.sendData(request);
			byte [] reply = nxtComm.readData();
	
			int tachoCount = (0xFF & reply[13]) | ((0xFF & reply[14]) << 8)| ((0xFF & reply[15]) << 16)| ((0xFF & reply[16]) << 24);
			return tachoCount;
		}
	}
	
	/**
	 * Resets the tachometer
	 * @param port Output port (0-2)
	 * @param relative TRUE: position relative to last movement, FALSE: absolute position
	 */
	public byte resetMotorPosition(int port, boolean relative) throws IOException {
		synchronized(this) {
			byte boolVal = 0;
			if(relative) boolVal = (byte)0xFF;
			byte [] request = {DIRECT_COMMAND_NOREPLY, RESET_MOTOR_POSITION, (byte)port, boolVal};
			return sendRequest(request);
		}
	}
	
	public void setVerify(boolean verify) {
		verifyCommand = verify;
	}
	
	/**
	 * Call the close() command when your program ends, otherwise you
	 * will have to turn the NXT brick off/on before you run another
	 * program using iCommand.
	 *
	 */
	public void close() throws IOException {
		nxtComm.close();
	}
	public int getBatteryLevel() throws IOException {
		synchronized (this) {
			byte [] request = {DIRECT_COMMAND_REPLY, GET_BATTERY_LEVEL};
			nxtComm.sendData(request);
			byte [] reply = nxtComm.readData();
			int batteryLevel = (0xFF & reply[3]) | ((0xFF & reply[4]) << 8);
			return batteryLevel;
		}
	}
	
	public InputValues getInputValues(int port) throws IOException {
		synchronized (this) {
			byte [] request = {DIRECT_COMMAND_REPLY, GET_INPUT_VALUES, (byte)port};
			nxtComm.sendData(request);
			byte [] reply = nxtComm.readData();
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
	}
	
	/**
	 * Tells the NXT what type of sensor you are using and the mode to operate in.
	 * @param port - 0 to 3
	 * @param sensorType - Enumeration for sensor type (see NXTProtocol) 
	 * @param sensorMode - Enumeration for sensor mode (see NXTProtocol)
	 */
	public byte setInputMode(int port, int sensorType, int sensorMode) throws IOException {
		synchronized(this) {
			byte [] request = {DIRECT_COMMAND_NOREPLY, SET_INPUT_MODE, (byte)port, (byte)sensorType, (byte)sensorMode};
			return sendRequest(request);
		}
	}
	
	/**
	 * Plays a tone on NXT speaker. If a new tone is sent while the previous tone is playing,
	 * the new tone command will stop the old tone command.
	 * @param frequency 
	 * @param duration - In milliseconds.
	 * @return - Returns true if command worked, false if it failed.
	 */
	public byte playTone(int frequency, int duration) throws IOException {
		synchronized (this) {
			byte [] request = {DIRECT_COMMAND_NOREPLY, PLAY_TONE, (byte)frequency, (byte)(frequency>>>8), (byte)duration, (byte)(duration>>>8)};
			return sendRequest(request);
		}		
	}
	
	public DeviceInfo getDeviceInfo() throws IOException {
		synchronized (this) {
			byte [] request = {SYSTEM_COMMAND_REPLY, GET_DEVICE_INFO};
			char[] name = new char[14];
			nxtComm.sendData(request);
			byte [] reply = nxtComm.readData();
			DeviceInfo d = new DeviceInfo();
			d.status = reply[2];
			int i = 0;
			for(;reply[3+i] != 0 && i<14;i++) name[i] = (char) reply[3+i]; 
			d.NXTname = new String(name,0,i);
			d.bluetoothAddress = getAddressString(reply);
			d.signalStrength = (0xFF & reply[25]) | ((0xFF & reply[26]) << 8)| ((0xFF & reply[27]) << 16)| ((0xFF & reply[28]) << 24);
			d.freeFlash = (0xFF & reply[29]) | ((0xFF & reply[30]) << 8)| ((0xFF & reply[31]) << 16)| ((0xFF & reply[32]) << 24);
			return d;
		}
	}
	
	private static final char[] cs = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

	private String getAddressString(byte [] reply) {
		char[] caddr = new char[20];		
		int ci = 0;
		
		for(int i=0; i<7; i++) {
			int nr = reply[i+18] & 0xFF;	
			caddr[ci++] = cs[nr / 16];
			caddr[ci++] = cs[nr % 16];
			if (i != 6) caddr[ci++] = ':';
		}
		return new String(caddr, 0, 20);
	}
	
	public FirmwareInfo getFirmwareVersion() throws IOException {
		synchronized (this) {
			byte [] request = {SYSTEM_COMMAND_REPLY, GET_FIRMWARE_VERSION};
			nxtComm.sendData(request);
			byte [] reply = nxtComm.readData();
			FirmwareInfo info = new FirmwareInfo();
			info.status = reply[2];
			char[] cc = new char[3];
			if(info.status == 0) {
				cc[1] = '.';
				cc[0] = (char) ('0' + reply[4]);
				cc[2] = (char) ('0' + reply[3]);
				info.protocolVersion = new String(cc,0,3);
				cc[0] = (char) ('0' + reply[6]);
				cc[2] = (char) ('0' + reply[5]);
				info.firmwareVersion = new String(cc,0,3);
			}
			return info;
		}
	}
	
	/**
	 * Deletes user flash memory (not including system modules).
	 * @return 0 for success
	 */
	public byte deleteUserFlash() throws IOException {
		synchronized (this) {
			byte [] request = {SYSTEM_COMMAND_REPLY, DELETE_USER_FLASH};
			nxtComm.sendData(request);
			byte [] reply = nxtComm.readData();
			return reply[2];
		}
	}
}
