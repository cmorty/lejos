package lejos.nxt;

/**
 * Abstraction for a port that supports I2C sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
public interface I2CPort extends BasicSensorPort {

	public void i2cEnable();
	
	public void i2cDisable();
	
	public int i2cBusy();
	
	public int i2cStart(int address, int internalAddress,
            int numInternalBytes, byte[] buffer,
            int numBytes, int transferType);
	
}
