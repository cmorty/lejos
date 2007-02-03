package lejos.nxt;

/**
 * Abstraction for a Mindsensors (and possibly HiTechnic) 
 * acceleration (tilt) sensor.
 * 
 */
public class TiltSensor extends I2CSensor {
	byte[] buf;
	
	public TiltSensor(Port port)
	{
		this.port = port;
		buf = new byte[2];
		port.setPowerType(2);
		port.i2cEnable();
	}
	
	public int getXTilt() {		
		int ret = getData(0x42, buf, 1);
		
		return (ret == 0 ? (buf[0] & 0xff) : -1);
	}
	
	public int getYTilt() {		
		int ret = getData(0x43, buf, 1);
		
		return (ret == 0 ? (buf[0] & 0xff) : -1);
	}
	
	public int getZTilt() {		
		int ret = getData(0x44, buf, 1);
		
		return (ret == 0 ? (buf[0] & 0xff) : -1);
	}
}
