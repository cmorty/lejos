package lejos.nxt.addon;

import lejos.nxt.*;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * This class manages the sensor NXT Line Leader from Mindsensors.
 * The sensor add a sensor row to detect black/white lines.
 * 
 * This sensor is perfect to build a robot which has the mission to follow a line.
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class NXTLineLeader extends I2CSensor{
	byte[] buf = new byte[8];

	private final static byte COMMAND = 0x41;
	
	//private final static byte LL_WRITE_SETPOINT = 0x45;
	private final static byte LL_WRITE_KP = 0x46;
	private final static byte LL_WRITE_KI = 0X47;
	private final static byte LL_WRITE_KD = 0X48;

	private final static byte LL_READ_STEERING = 0x42;
	private final static byte LL_READ_AVERAGE = 0X43;
	private final static byte LL_READ_RESULT = 0X44;

	private final byte whiteReadingLimits[]= {0x51,0x52,0x53,0x54,0x55,0x56,0x57,0x58};

	/**
	 * Constructor
	 * 
	 * @param port
	 */
	public NXTLineLeader(I2CPort port){
		super(port);
		port.setType(TYPE_LOWSPEED_9V);
	}

	/**
	 * Send a single byte command represented by a letter
	 * @param cmd the letter that identifies the command
	 */
	public void sendCommand(char cmd) {
		sendData(COMMAND, (byte) cmd);
	}
	
	/**
	 * Sleep the sensor
	 * 
	 */
	public void sleep(){
		this.sendCommand('D');
	}

	/**
	 * Wake up the sensor
	 * 
	 */
	public void wakeUp(){
		this.sendCommand('P');
	}

	/**
	 * Get the steering value
	 * 
	 */
	public int getSteering(){
		int ret = getData(LL_READ_STEERING, buf, 2);
		int steering = 0;
		steering = (ret == 0 ? (buf[0] & 0xff) : -1);
		
		return steering;
	}

	/**
	 * Get the average value
	 * 
	 */
	public int getAverage(){
		int ret = getData(LL_READ_AVERAGE, buf, 2);
		int average = 0;
		average = (ret == 0 ? (buf[0] & 0xff) : -1);
		
		return average;
	}

	/**
	 * Get result value
	 * 
	 */
	public int getResult(){
		int ret = getData(LL_READ_RESULT, buf, 2);
		int result = 0;
		result = (ret == 0 ? (buf[0] & 0xff) : -1);
		return result;
	}

	/**
	 * Get KP value
	 * 
	 */
	public int getKP(){
		int ret = getData(LL_WRITE_KP, buf, 2);
		int KP = 0;
		KP = (ret == 0 ? (buf[0] & 0xff) : -1);
		return KP;
	}

	/**
	 * Set KP value
	 * 
	 */
	public void setKP(int KP){
		sendData(LL_WRITE_KP, (byte) KP);
	}

	/**
	 * Get KI value
	 * 
	 */
	public int getKI(){
		int ret = getData(LL_WRITE_KI, buf, 2);
		int KI = 0;
		KI = (ret == 0 ? (buf[0] & 0xff) : -1);
		return KI;
	}

	/**
	 * Set KI value
	 * 
	 */
	public void setKI(int KI){
		sendData(LL_WRITE_KP, (byte) KI);
	}

	/**
	 * Get KD value
	 * 
	 */
	public int getKD(){
		int ret = getData(LL_WRITE_KD, buf, 2);
		int KD = 0;
		KD = (ret == 0 ? (buf[0] & 0xff) : -1);
		return KD;
	}

	/**
	 * Set KD value
	 * 
	 */
	public void setKD(int KD){
		sendData(LL_WRITE_KD, (byte) KD);
	}

	/**
	 * Get status from each sensor in the raw
	 * 
	 */
	public int getSensorStatus(int index){
		int ret = getData(whiteReadingLimits[index], buf, 2);
		int status = 0;
		status = (ret == 0 ? (buf[0] & 0xff) : -1);
		return status;
	}
}