package lejos.nxt;

/**
 * Abstract class that implements common methods for all I2C sensors.
 * 
 * Extend this class to implement new I2C sensors.
 * 
 * @author Lawrie Griffiths (lawrie.griffiths@ntlworld.com).
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

	public I2CSensor(I2CPort port, int mode)
	{
		this.port = port;
		port.i2cEnable(mode);
		port.setType(TYPE_LOWSPEED);
	}

	public I2CSensor(I2CPort port)
	{
        this(port, I2CPort.LEGO_MODE);
    }
	
	/**
	 * Executes an I2C read transaction and waits for the result.
	 * 
	 * @param register I2C register, e.g 0x41
	 * @param buf Buffer to return data
	 * @param len Length of the return data
	 * @return status == 0 success, != 0 failure
	 */
	public int getData(int register, byte [] buf, int len) {	
		int ret = port.i2cStart(address, register, 1, null, len, 0);
		
		if (ret != 0) return ret;
		
		while (port.i2cBusy() != 0) {
			Thread.yield();
		}
		
		ret = port.i2cComplete(buf, len);
        return (ret < 0 ? ret : (ret == len ? 0 : -1));
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
        int ret = port.i2cStart(address, register, 1, buf, len, 1);
		if (ret != 0) return ret;
		
		while (port.i2cBusy() != 0) {
			Thread.yield();
		}
		
		return port.i2cComplete(null, 0);
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
		return fetchString(0x00);
	}
	
	/**
	 * Return the sensor product identifier.
	 * 
	 * @return 8-byte string
	 */
	public String getProductID() {
		return fetchString(0x08);
	}
	
	/**
	 * Return the sensor type.
	 * 
	 * @return 8-byte string
	 */
	public String getSensorType() {
		return fetchString(0x10);
	}

    /**
     * Internal helper function, read a string from the device
     * @param register
     * @return the requested string
     */
	protected String fetchString(int register) {
		int ret = getData(register, byteBuff, 8);
		if(ret != 0)
			return BLANK;
		char [] charBuff = new char[8];
		for(int i=0;i<8;i++)
			charBuff[i] = (byteBuff[i] == 0 ? ' ' : (char)byteBuff[i]);
		return new String(charBuff, 0, 8);	
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
