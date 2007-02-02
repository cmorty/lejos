package lejos.nxt;

/**
 * Abstraction for a HiTechnic or Mindsensors compass.
 * 
 */
public class CompassSensor {
	Port port;
	byte[] buf;
	
	public CompassSensor(Port port)
	{
		this.port = port;
		buf = new byte[2];
		port.setPowerType(2);
		port.i2cEnable();
	}
	
	public int getDegrees() {		
		int ret = port.i2cStart(1, 0x42, 2, buf, 2, 0);
		
		if (ret == 0) {
			while (port.i2cBusy() != 0) {
				Thread.yield();
			}
			return (buf[0] & 0xff + ((buf[1] & 0xff) << 8));
		} else return -1;
	}
}

