package lejos.nxt;

/**
 * Abstract class that implements common methods for all I2C sensors.
 * 
 * Extend this class to implement new I2C sensors.
 * 
 * @author Lawrie Griffiths (lawrie.griffiths@ntlworld.com)
 *
 */
public abstract class I2CSensor implements SensorConstants {
	I2CPort port;
	int address = 1;
	String version = "        ";
	String productID = "        ";
	String sensorType = "        ";
	char [] versionChars = StringUtils.getCharacters(version);
	char [] productIDChars = StringUtils.getCharacters(productID);
	char [] sensorTypeChars = StringUtils.getCharacters(sensorType);
	byte[] byteBuff = new byte[8]; 
	
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
	 *  Executes an I2C write transaction - not yet working.
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
	 * Return the sensor version number.
	 * 
	 * @return 8-byte string
	 */
	public String getVersion() {
		int ret = getData(0x00, byteBuff, 8);

		for(int i=0;i<8;i++) {
			versionChars[i] = (ret == 0 ? (char) byteBuff[i] : ' ');
		}	
		return version;
	}
	
	/**
	 * Return the sensor product identifier.
	 * 
	 * @return 8-byte string
	 */
	public String getProductID() {
		int ret = getData(0x08, byteBuff, 8);

		for(int i=0;i<8;i++) {
			productIDChars[i] = (ret == 0 ? (char) byteBuff[i] : ' ');
		}	
		return productID;
	}
	
	/**
	 * Return the sensor type.
	 * 
	 * @return 8-byte string
	 */
	public String getSensorType() {
		int ret = getData(0x10, byteBuff, 8);

		for(int i=0;i<8;i++) {
			sensorTypeChars[i] = (ret == 0 ? (char) byteBuff[i] : ' ');
		}	
		return sensorType;
	}
}
