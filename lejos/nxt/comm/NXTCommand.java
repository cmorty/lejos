package lejos.nxt.comm;

import java.io.*;

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
}
