package lejos.nxt;

/**
 * Abstraction for a Mindsensors (and possibly HiTechnic) 
 * acceleration (tilt) sensor.
 * 
 */
public class TiltSensor extends I2CSensor {
	byte[] buf = new byte[2];
	
	public TiltSensor(I2CPort port)
	{
		super(port);
	}
	
	/**
	 * Returns X tilt value.
	 */
	public int getXTilt() {		
		int ret = getData(0x42, buf, 1);
		
		return (ret == 0 ? (buf[0] & 0xff) : -1);
	}
	
	/**
	 * Returns Y tilt value.
	 */
	public int getYTilt() {		
		int ret = getData(0x43, buf, 1);
		
		return (ret == 0 ? (buf[0] & 0xff) : -1);
	}
	
	/**
	 * Returns Z tilt value.
	 */
	public int getZTilt() {		
		int ret = getData(0x44, buf, 1);
		
		return (ret == 0 ? (buf[0] & 0xff) : -1);
	}
}
