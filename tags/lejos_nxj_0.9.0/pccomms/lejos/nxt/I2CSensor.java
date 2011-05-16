package lejos.nxt;

import java.io.IOException;
import lejos.nxt.remote.*;
import lejos.pc.comm.*;

/**
 * A sensor wrapper to allow easy access to I2C sensors, like the ultrasonic sensor.
 * 
 * This version of this class supports remote execution of I2C.
 * 
 * @author Brian Bagnall and Lawrie Griffiths
 */
public class I2CSensor implements SensorConstants {
	private static final NXTCommand nxtCommand = NXTCommandConnector.getSingletonOpen();
		
	private static byte STOP = 0x00; // Commands don't seem to use this?
	private static String BLANK = "       ";
	
	/**
	 * Returns the version number of the sensor. e.g. "V1.0" Reply length = 8.
	 */
	protected static byte REG_VERSION = 0x00;
	/**
	 * Returns the product ID of the sensor.  e.g. "LEGO" Reply length = 8.
	 */
	protected static byte REG_PRODUCT_ID = 0x08;
	/**
	 * Returns the sensor type. e.g. "Sonar" Reply length = 8.
	 */
	protected static byte REG_SENSOR_TYPE = 0x10;
	
	protected static int DEFAULT_I2C_ADDRESS = 0x02;
	
	protected byte port;
	protected int address;
	
	public I2CSensor(I2CPort port)
	{
        this(port, DEFAULT_I2C_ADDRESS, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
    }
	
	/**
	 * @param port
	 * @param mode will not work on PC side
	 */
	public I2CSensor(I2CPort port, int mode)
	{
		this(port, DEFAULT_I2C_ADDRESS, mode, TYPE_LOWSPEED);
	}
	
	/**
	 * @param port
	 * @param address
	 * @param mode will not work on PC side
	 * @param type
	 */
	public I2CSensor(I2CPort port, int address, int mode, int type)
	{
		port.setTypeAndMode(type, NXTProtocol.RAWMODE);
		this.port = (byte)port.getId();
        this.address = address;
		// Flushes out any existing data
		try {
			nxtCommand.LSGetStatus(this.port); 
			nxtCommand.LSRead(this.port); 
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	public int getId() {
		return port;
	}
	
	public int getData(int register, byte [] buf, int length) {
		return this.getData(register, buf, 0, length);
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
	 * @return the status
	 */
	public int getData(int register, byte [] buf, int offset, int length) {
		byte [] txData = {(byte)address, (byte) register};
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
			if (ret != null) System.arraycopy(ret, 0,buf, offset, ret.length);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return -1;
		}
		return status[0];
	}

	
	/**
	 * Helper method to return a single register byte.
	 * @param register
	 * @return the byte of data
	 */
	public int getData(int register) {
		byte [] buf1 = new byte[1];
		return getData(register, buf1 ,1);
	}
	
	/**
	 * Sets a single byte in the I2C sensor. 
	 * @param register A data register in the I2C sensor. e.g. ACTUAL_ZERO
	 * @param value The data value.
	 */
	public int sendData(int register, byte value) {
		byte [] txData = {(byte)address, (byte) register, value};
		try {
			int ret = nxtCommand.LSWrite(this.port, txData, (byte)0);
            return ret;
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return -1;
		}
	}
	
	public int sendData(int register, byte [] data, int length) {
		return this.sendData(register, data, 0, length);
	}
	
	/**
	 * Send data top the sensor
	 * @param register A data register in the I2C sensor.
	 * @param data The byte to send.
	 * @param length the number of bytes
	 */
	public int sendData(int register, byte [] data, int offset, int length) {
		byte [] txData = {(byte)address, (byte) register};
		byte [] sendData = new byte[length+2];
		System.arraycopy(txData,0,sendData,0,2);
		System.arraycopy(data,offset,sendData,2,length);
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
		return fetchString(REG_VERSION, 8);
	}
	
	/**
	 * Returns the Product ID as a string.
	 * NOTE: A little unreliable at the moment due to a bug in firmware. Keep
	 * trying if it doesn't get it the first time.
	 * @return The product ID. e.g. "LEGO"
	 */
	public String getProductID() {
		return fetchString(REG_PRODUCT_ID, 8);
	}
	
	/**
	 * Returns the type of sensor as a string.
	 * NOTE: A little unreliable at the moment due to a bug in firmware. Keep
	 * trying if it doesn't get it the first time.
	 * @return The sensor type. e.g. "Sonar"
	 */
	public String getSensorType() {
		return fetchString(REG_SENSOR_TYPE, 8);
	}
	
    /**
     * Read a string from the device.
     * This functions reads the specified number of bytes
     * and returns the characters before the zero termination byte.
     * 
     * @param reg
     * @param len maximum length of the string, including the zero termination byte
     * @return the string containing the characters before the zero termination byte
     */
	protected String fetchString(byte reg, int len) {
		byte[] buf = new byte[len];
		int ret = getData(reg, buf, 0, len);
		if (ret != 0)
			return "";
		
		int i;
		char[] charBuff = new char[len];		
		for (i=0; i<len && buf[i] != 0; i++)
			charBuff[i] = (char)(buf[i] & 0xFF);
		
		return new String(charBuff, 0, i);
	}

	/**
	 * Set the address of the port 
	 * Note that addresses are from 0x01 to 0x7F not
	 * even numbers from 0x02 to 0xFE as given in some I2C device specifications.
	 * They are 7-bit addresses not 8-bit addresses.
	 * 
	 * @param addr 0x02 to 0xfe
	 * @deprecated If the device has a changeable address, then constructor of the class should have an address parameter. If not, please report a bug.
	 */
	public void setAddress(int addr) {
        if ((address & 1) != 0) throw new IllegalArgumentException("Bad address format");
		address = addr;
	}
}