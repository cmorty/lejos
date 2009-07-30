package lejos.nxt;

/**
 * Abstraction for a port that supports I2C sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
public interface I2CPort extends BasicSensorPort {
    public static final int STANDARD_MODE = 0;
    public static final int LEGO_MODE = 1;
    public static final int ALWAYS_ACTIVE = 2;
    public static final int NO_RELEASE = 4;

    /**
     * Enable the low level device
     * @param mode One or more of the mode bits above.
     */
	public void i2cEnable(int mode);

    /**
     * Disable the device.
     */
	public void i2cDisable();

    /**
     * Check to see if the device is busy
     * @return 1 if busy 0 if idle
     */
	public int i2cBusy();

    /**
     * Start an i2c transaction.
     *
     * @param address The i2C address (note this is a 7 bit address)
     * @param internalAddress The internal register address
     * @param numInternalBytes The number of bytes in the internal address
     * @param buffer The data to write to the device (null for reads)
     * @param numBytes The number of bytes to transfer
     * @param transferType 0 == read 1 == write
     * @return  0: no error
     *         -1: Invalid device
     *         -2: Device busy
     *         -4: Buffer size error.
     *         -5: Invalid register address size.
     */
	public int i2cStart(int address, int internalAddress,
            int numInternalBytes, byte[] buffer,
            int numBytes, int transferType);

    /**
     * Complete the i2c transaction, and read any returned data.
     * @param buffer The buffer for a read (null for write)
     * @param numBytes Number of bytes to transfer
     * @return >= 0 number of bytes returned
     *         -1: Invalid device
     *         -2: Device busy
     *         -3: I2C error
     *         -4: Buffer size error.
     */
    public int i2cComplete(byte[] buffer, int numBytes);
	
}
