package lejos.nxt;

/**
 * Abstraction for a Mindsensors (and possibly HiTechnic) 
 * acceleration (tilt) sensor.
 * 
 */
public class TiltSensor {
	Port port;
	byte[] buf;
	
	public TiltSensor(Port port)
	{
		this.port = port;
		buf = new byte[2];
		port.setPowerType(2);
		port.i2cEnable();
	}
	
	public int getXTilt() {		
		int ret = port.i2cStart(1, 0x42, 1, buf, 1, 0);
		
		if (ret == 0) {
			while (port.i2cBusy() != 0) {
				Thread.yield();
			}
			return (buf[0] & 0xff);
		} else return -1;
	}
	
	public int getYTilt() {		
		int ret = port.i2cStart(1, 0x43, 1, buf, 1, 0);
		
		if (ret == 0) {
			while (port.i2cBusy() != 0) {
				Thread.yield();
			}
			return (buf[0] & 0xff);
		} else return -1;
	}
	
	public int getZTilt() {		
		int ret = port.i2cStart(1, 0x44, 1, buf, 1, 0);
		
		if (ret == 0) {
			while (port.i2cBusy() != 0) {
				Thread.yield();
			}
			return (buf[0] & 0xff);
		} else return -1;
	}
}
