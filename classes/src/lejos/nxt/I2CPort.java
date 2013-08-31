package lejos.nxt;

/**
 * Abstraction for a port that supports I2C sensors.
 * <p>
 * from http://lejos.sourceforge.net/forum/viewtopic.php?f=7&t=4597&p=19491#p19491<p>
 * A little history about the rather odd Lego i2c implementation...
 *  <ol><li>
 * The Lego implementation is not standard i2c it has various oddities (like extra delays 
 * and strange handling of stop and restart cases).<li>
 * There have been several implementations of i2c in leJOS over the years, some less efficient than the current one.
 * </ol>
 * Anyway:<br>
 * LEGO_MODE turns on support for pretty much all of the strange i2c features of the LEGO firmware. You probably 
 * only need this if you are talking to the Lego Ultrasonic sensor, or some strange homebrew devices that was 
 * built to be compatible with the Lego protocol.<br>
 * STANDARD_MODE is the alternative to LEGO_MODE as it disables all of the Lego strange stuff and tries to be 
 * as close to normal i2c as possible (but remember this is a bit banged implementation, not hardware).
 * <br>The above two are mode settings. Use one or the other.
 * <p>
 * The remaining settings are flags that can be added to either of the above modes. 
 * <ul><li>HIGH_SPEED enables higher speed operation (approx 125KHz). If this bit is not set then 
 * the default speed of 9.6KHz is used.
 * <li>ALWAYS_ACTIVE enables compatibility with an older leJOS i2c driver feature which kept the 
 * timer interrupt running all of the time (even if no i2c operations are in progress).
 *  This adds considerable cpu load and can impact program timing. It is unlikely that any 
 *  program will need it unless they are using cpu based timing loops and are old.
 * <li>NO_RELEASE means that after the completion of an i2c operation the i2c bus will 
 * not be released, instead the clock line will be held low. I seem to remember that 
 * this mode was needed for some devices that required a series of i2c operations
 *  to be issued without the bus being released to reprogram them.
 *  </ul>

 * @author Lawrie Griffiths
 *
 */
public interface I2CPort extends BasicSensorPort {
    /** Use standard i2c protocol */
    public static final int STANDARD_MODE = 0;
    /** Use Lego compatible i2c protocol (default) */
    public static final int LEGO_MODE = 1;
    /** Keep the i2c driver active between requests */
    public static final int ALWAYS_ACTIVE = 2;
    /** Do not release the i2c bus between requests */
    public static final int NO_RELEASE = 4;
    /** Use high speed I/O (125KHz) */
    public static final int HIGH_SPEED = 8;
    /** Maximum read/write request length */
    public static final int MAX_IO = 32;

    /** Invalid port number, or port is not enabled */
    public static final int ERR_INVALID_PORT = -1;
    /** Port is busy */
    public static final int ERR_BUSY = -2;
    /** Data error during transaction */
    public static final int ERR_FAULT = -3;
    /** Read/Write request too large */
    public static final int ERR_INVALID_LENGTH = -4;
    /** Bus is busy */
    public static final int ERR_BUS_BUSY = -5;
    /** Operation aborted */
    public static final int ERR_ABORT = -6;



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
     * Check to see the status of the port/device
     * @return 0 if ready
     *         -1: Invalid device
     *         -2: Device busy
     *         -3: Device fault
     *         -4: Buffer size error.
     *         -5: Bus is busy
     */
	public int i2cStatus();

    /**
     * High level i2c interface. Perform a complete i2c transaction and return
     * the results. Writes the specified data to the device and then reads the
     * requested bytes from it. The address is given as an 8 bit value. Bit 0
     * must be always be zero. Bit 1 to 7 specify the 7 bit i2c address.
     * 
     * @param deviceAddress The I2C device address.
     * @param writeBuf The buffer containing data to be written to the device.
     * @param writeOffset The offset of the data within the write buffer
     * @param writeLen The number of bytes to write.
     * @param readBuf The buffer to use for the transaction results
     * @param readOffset Location to write the results to
     * @param readLen The length of the read
     * @return < 0 error otherwise the number of bytes read
     */
    public int i2cTransaction(int deviceAddress, byte[]writeBuf,
            int writeOffset, int writeLen, byte[] readBuf, int readOffset,
            int readLen);
}
