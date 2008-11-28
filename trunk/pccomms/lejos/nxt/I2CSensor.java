package lejos.nxt;

import java.io.IOException;

import lejos.pc.comm.*;

/**
 * A sensor wrapper to allow easy access to I2C sensors, like the ultrasonic sensor.
 * Currently uses the default I2C address of 0x02, but some sensors can
 * be connected to same port lines and use different addresses using the
 * Auto Detecting Parallel Architecture (ADPA). Currently unsure if there are
 * commercial port expanders yet to use this function, or whether the 
 * Lego UltrasonicSensor sensor is ADPA compatible.
 * @author BB
 */
public class I2CSensor implements SensorConstants {
	private static final NXTCommand nxtCommand = NXTCommand.getSingletonOpen();
		
	protected static byte DEFAULT_ADDRESS = 0x02; // the default I2C address for a port. You can change address of compass sensor (see docs) and then communicate with multiple sensors on same physical port.
	protected static byte STOP = 0x00; // Commands don't seem to use this?
	protected static String BLANK = "       ";
	
	// Port information (constants)
	/**
	 * Returns the version number of the sensor. e.g. "V1.0" Reply length = 8.
	 */
	protected static byte VERSION = 0x00;
	/**
	 * Returns the product ID of the sensor.  e.g. "LEGO" Reply length = 8.
	 */
	protected static byte PRODUCT_ID = 0x08;
	/**
	 * Returns the sensor type. e.g. "Sonar" Reply length = 8.
	 */
	protected static byte SENSOR_TYPE = 0x10;
	/**
	 * Returns the "zero position" set at the factory for this sensor. e.g. 0 Reply length = 1.
	 *
	 */
	
	byte port;
	
	/**
	 * 
	 * @param s A sensor. e.g. Port.S1
	 */
	public I2CSensor(I2CPort s, byte sensorType) {
		port = (byte)s.getId();
		s.setTypeAndMode(sensorType, NXTProtocol.RAWMODE);
		try {
			nxtCommand.LSGetStatus(this.port); // Dick says to flush out data with Poll?
			nxtCommand.LSRead(this.port); // Dick says to flush out data with Poll?
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	public I2CSensor(I2CPort s) {
		this(s, NXTProtocol.LOWSPEED);
	}
	
	
	public int getId() {
		return port;
	}
	
	/**
	 * Method for retrieving data values from the sensor. BYTE0 (
	 * is usually the primary data value for the sensor.
	 * Data is read from registers in the sensor, usually starting at 0x00 and ending around 0x49.
	 * Just supply the register to start reading at, and the length of bytes to read (16 maximum).
	 * NOTE: The NXT supplies UBYTE (unsigned byte) values but Java converts them into
	 * signed bytes (probably more practical to return short/int?)
	 * @param register e.g. FACTORY_SCALE_DIVISOR, BYTE0, etc....
	 * @param length Length of data to read (minimum 1, maximum 16) 
	 * @return
	 */
	protected int getData(int register, byte [] buf, int length) {
		byte [] txData = {DEFAULT_ADDRESS, (byte) register};
		try {
			nxtCommand.LSWrite(port, txData, (byte)length);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		
		byte [] status = null;
		do {
			try {
				status = nxtCommand.LSGetStatus(port);
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
				return -1;
			}
		} while(status[0] == ErrorMessages.PENDING_COMMUNICATION_TRANSACTION_IN_PROGRESS|status[0] == ErrorMessages.SPECIFIED_CHANNEL_CONNECTION_NOT_CONFIGURED_OR_BUSY);
				
		try {
			byte [] ret = nxtCommand.LSRead(port);
			System.arraycopy(ret, 0,buf, 0, ret.length);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return -1;
		}
		return status[0];
	}

	
	/**
	 * Helper method to return a single register byte.
	 * @param register
	 * @return
	 */
	protected int getData(byte register) {
		byte [] buf1 = new byte[1];
		return getData(register, buf1 ,1);
	}
	
	/**
	 * Sets a single byte in the I2C sensor. 
	 * @param register A data register in the I2C sensor. e.g. ACTUAL_ZERO
	 * @param value The data value.
	 */
	protected int sendData(int register, byte value) {
		byte [] txData = {DEFAULT_ADDRESS, (byte) register, value};
		try {
			return nxtCommand.LSWrite(this.port, txData, (byte)0);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return -1;
		}
	}
	
	/**
	 * Sets a single byte in the I2C sensor. 
	 * @param register A data register in the I2C sensor. e.g. ACTUAL_ZERO
	 * @param value The data value.
	 */
	protected int sendData(int register, byte [] data, int length) {
		byte [] txData = {DEFAULT_ADDRESS, (byte) register};
		byte [] sendData = new byte[length+2];
		System.arraycopy(txData,0,sendData,0,2);
		System.arraycopy(txData,0,sendData,2,length);
		try {
			return nxtCommand.LSWrite(this.port, sendData, (byte)0);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return -1;
		}
	}
	/**
	 * Returns the version number of the sensor hardware.
	 * NOTE: A little unreliable at the moment due to a bug in firmware. Keep
	 * trying if it doesn't get it the first time.
	 * @return The version number. e.g. "V1.0"
	 */
	public String getVersion() {
		return fetchString(VERSION, 8);
	}
	
	/**
	 * Returns the Product ID as a string.
	 * NOTE: A little unreliable at the moment due to a bug in firmware. Keep
	 * trying if it doesn't get it the first time.
	 * @return The product ID. e.g. "LEGO"
	 */
	public String getProductID() {
		return fetchString(PRODUCT_ID, 8);
	}
	
	/**
	 * Returns the type of sensor as a string.
	 * NOTE: A little unreliable at the moment due to a bug in firmware. Keep
	 * trying if it doesn't get it the first time.
	 * @return The sensor type. e.g. "Sonar"
	 */
	public String getSensorType() {
		return fetchString(SENSOR_TYPE, 8);
	}
	
	/**
	 * Helper method for retrieving string constants using I2C protocol.
	 * @param constantEnumeration e.g. I2CProtocol.VERSION
	 * @return
	 */
	protected String fetchString(byte constantEnumeration, int rxLength) {
		byte [] stringBytes = new byte[rxLength];
		getData(constantEnumeration, stringBytes, rxLength);
		
		// Get rid of everything after 0.
		int zeroPos = 0;
		for(zeroPos = 0;zeroPos < rxLength;zeroPos++) {
			if(stringBytes [zeroPos] == 0) break;
		}
		String s = new String(stringBytes).substring(0,zeroPos);
		return s;
	}
}