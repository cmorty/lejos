package lejos.nxt;

public abstract class AbstractI2CSensor implements I2CPort
{
	public synchronized int i2cSendData(int address, int internalAddress, int numInternalBytes,
			byte [] buffer, int offset, int numBytes)
	{
        int ret = this.i2cStart(address, internalAddress, numInternalBytes,
        		buffer, offset, numBytes, 1);
		if (ret < 0)
			return ret;
		
		while (this.i2cBusy() != 0) {
			Thread.yield();
		}
		
		return this.i2cComplete(null, 0, 0);
	}
	
	public synchronized int i2cReadData(int address, int internalAddress, int numInternalBytes,
			byte [] buf, int offset, int len)
	{	
		int ret = this.i2cStart(address, internalAddress, numInternalBytes,
				null, 0, len, 0);		
		if (ret < 0)
			return ret;

		while (this.i2cBusy() != 0) {
			Thread.yield();
		}
		
		return this.i2cComplete(buf, offset, len);
	}	
}
