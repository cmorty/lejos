package lejos.nxt;

/**
 * Abstraction for a port that supports I2C sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
public interface I2CPort extends BasicSensorPort {
    public static final int LEGO_MODE = 1;
	public void i2cEnable(int mode);
	
	public void i2cDisable();
	
	public int i2cBusy();
	
	public int i2cStart(int address, int internalAddress,
            int numInternalBytes, byte[] buffer,
            int numBytes, int transferType);

    public int i2cComplete(byte[] buffer, int numMbytes);
	
}
