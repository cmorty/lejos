package lejos.nxt;

interface I2CPort extends SimpleSensorPort {

	public void i2cEnable();
	
	public void i2cDisable();
	
	public int i2cBusy();
	
	public int i2cStart(int address, int internalAddress,
            int numInternalBytes, byte[] buffer,
            int numBytes, int transferType);
	
}
