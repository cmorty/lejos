package lejos.nxt;

/**
 * Abstract class that implements common methods for all I2C sensors.
 * 
 * Extend this class to implement new I2C sensors.
 * 
 * @author Lawrie Griffiths (lawrie.griffiths@ntlworld.com)
 *
 */
public class I2CSensor implements SensorConstants {
	I2CPort port;
	int address = 1;
	String version = null;
	String productID = null;
	String sensorType = null;
	byte [] byteBuff = new byte[8];
	byte [] buf1 = new byte[1];
	String BLANK = "        ";
	
	public I2CSensor(I2CPort port)
	{
		this.port = port;
		port.i2cEnable();
		port.setType(TYPE_LOWSPEED);
	}
	
	/**
	 * Executes an I2C read transaction and waits for the result.
	 * 
	 * @param register I2C register, e.g 0x41
	 * @param buf Buffer to return data
	 * @param len Length of the return data
	 * @return status zero=success, non-zero=failure
	 */
	public int getData(int register, byte [] buf, int len) {	
		int ret = port.i2cStart(address, register, len, buf, len, 0);
		
		if (ret != 0) return ret;
		
		while (port.i2cBusy() != 0) {
			Thread.yield();
		}
		
		return 0;
	}
	
	/**
	 *  Executes an I2C write transaction.
	 *  
	 * @param register I2C register, e.g 0x42
	 * @param buf Buffer containing data to send
	 * @param len Length of data to send
	 * @return status zero=success, non-zero=failure
	 */
	public int sendData(int register, byte [] buf, int len) {
        int ret = port.i2cStart(address, register, len, buf, len, 1);
		if (ret != 0) return ret;
		
		while (port.i2cBusy() != 0) {
			Thread.yield();
		}
		
		return 0;
	}
	
	/**
	 *  Executes an I2C write transaction.
	 *  
	 * @param register I2C register, e.g 0x42
	 * @param value single byte to send
	 * @return status zero=success, non-zero=failure
	 */
	public int sendData(int register, byte value) {
		buf1[0] = value;
		return sendData(register, buf1, 1);
	}
	
	/**
	 * Return the sensor version number.
	 * 
	 * @return 8-byte string
	 */
	public String getVersion() {
	/* NOTE! getVersion(), getProductID(), getSensorType() and
	 * UltrasonicSensor.getUnits() are all about the same. 
	 * Should probably make one helper method with appropriate arguments.
	 * - BB
	 */	
		int ret = getData(0x00, byteBuff, 8);
		if(ret != 0)
			return BLANK;
		char [] charBuff = new char[8];
		for(int i=0;i<8;i++)
			charBuff[i] = (char)byteBuff[i];
		version = new String(charBuff, 0, 8);
			
		return version;
	}
	
	/**
	 * Return the sensor product identifier.
	 * 
	 * @return 8-byte string
	 */
	public String getProductID() {
		int ret = getData(0x08, byteBuff, 8);
		if(ret != 0)
			return BLANK;
		char [] charBuff = new char[8];
		for(int i=0;i<8;i++)
			charBuff[i] = (char)byteBuff[i];
		productID = new String(charBuff, 0, 8);
					
		return productID;
	}
	
	/**
	 * Return the sensor type.
	 * 
	 * @return 8-byte string
	 */
	public String getSensorType() {
		int ret = getData(0x10, byteBuff, 8);
		if(ret != 0)
			return BLANK;
		char [] charBuff = new char[8];
		for(int i=0;i<8;i++)
			charBuff[i] = (char)byteBuff[i];
		sensorType = new String(charBuff, 0, 8);
					
		return sensorType;
	}
	
	/**
	 * Set the address of the port 
	 * Note that addresses are from 0x01 to 0x7F not
	 * even numbers from 0x02 to 0xFE as given in some I2C device specifications.
	 * They are 7-bit addresses not 8-bit addresses.
	 * 
	 * @param addr 1 to 0x7F 
	 */
	public void setAddress(int addr) {
		address = addr;
	}
	
	/**
	 * Get the port that the sensor is attached to
	 * @return the I2CPort
	 */
	public I2CPort getPort() {
		return port;
	}
}
