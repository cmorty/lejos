package lejos.nxt;
import java.lang.IllegalArgumentException;
/**
 * Class that implements common methods for all I2C sensors.
 * 
 * Extend this class to implement new I2C sensors.
 * 
 * @author Lawrie Griffiths (lawrie.griffiths@ntlworld.com).
 *
 */
public class I2CSensor implements SensorConstants {
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
	I2CPort port;
	int address = 2;
	String version = null;
	String productID = null;
	String sensorType = null;
	byte [] byteBuff = new byte[8];
	byte [] ioBuf = new byte[32];

	public I2CSensor(I2CPort port, int mode)
	{
		this.port = port;
		port.setType(TYPE_LOWSPEED);
		port.i2cEnable(mode);
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
	public synchronized int getData(int register, byte [] buf, int len) {
        // need to write the internal address.
        ioBuf[0] = (byte)register;
		int ret = port.i2cTransaction(address, ioBuf, 0, 1, buf, 0, len);
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
	public synchronized int sendData(int register, byte [] buf, int len) {
        if (len >= ioBuf.length) throw new IllegalArgumentException();
        ioBuf[0] = (byte)register;
        System.arraycopy(buf, 0, ioBuf, 1, len);
        return port.i2cTransaction(address, ioBuf, 0, len+1, null, 0, 0);
	}
	
	/**
	 *  Executes an I2C write transaction.
	 *  
	 * @param register I2C register, e.g 0x42
	 * @param value single byte to send
	 * @return status zero=success, non-zero=failure
	 */
	public synchronized int sendData(int register, byte value) {
        ioBuf[0] = (byte)register;
        ioBuf[1] = value;
        return port.i2cTransaction(address, ioBuf, 0, 2, null, 0, 0);
	}
	
	/**
	 * Return the sensor version number.
	 * 
	 * @return 8-byte string
	 */
	public String getVersion() {
		return fetchString(VERSION, 8);
	}
	
	/**
	 * Return the sensor product identifier.
	 * 
	 * @return 8-byte string
	 */
	public String getProductID() {
		return fetchString(PRODUCT_ID, 8);
	}
	
	/**
	 * Return the sensor type.
	 * 
	 * @return 8-byte string
	 */
	public String getSensorType() {
		return fetchString(SENSOR_TYPE, 8);
	}

    /**
     * Internal helper function, read a string from the device
     * @param register
     * @param len The length of the space padded reply.
     * @return the requested string
     */
	protected String fetchString(int register, int len) {
		int ret = getData(register, byteBuff, 8);
		if (ret != 0) return "";
		char [] charBuff = new char[len];
		int newlen = 0;
		while(newlen < 8 && byteBuff[newlen] > ' ') {
			charBuff[newlen] = (char) byteBuff[newlen];
			newlen++;
		}			
		return new String(charBuff, 0, newlen);
	}
	
	/**
	 * Set the address of the port
     * Addresses use the standard Lego/NXT format and are in the range 0x2-0xfe.
     * The low bit must always be zero. Some data sheets (and older versions
     * of leJOS) may use i2c 7 bit format (0x1-0x7f) in which case this address
     * must be shifted left on place to be used with this function.
	 * 
	 * @param addr 0x2 to 0xfe
	 */
	public void setAddress(int addr) {
        if ((address & 1) != 0) throw new IllegalArgumentException("Bad address format");
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
