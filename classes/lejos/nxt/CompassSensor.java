package lejos.nxt;

/**
 * Abstraction for a HiTechnic or Mindsensors compass.
 * 
 */
public class CompassSensor extends I2CSensor {
	byte[] buf;
	
	public CompassSensor(Port port)
	{
		this.port = port;
		buf = new byte[2];
		port.setPowerType(2);
		port.i2cEnable();
	}
	
	/**
	 * Returns the directional heading in degrees. (0 to 359.9)
	 * 0 is due North (on Mindsensors circuit board a white arrow indicates
	 * the direction of compass). Reading increases clockwise.
	 * @return Heading in degrees. Resolution is within 0.1 degrees
	 */
	public float getDegrees() {		
		int ret = getData(0x42, buf, 2);
		if(ret != 0) return -1;
		int iHeading = (0xFF & buf[0]) | ((0xFF & buf[1]) << 8);
		float dHeading = iHeading / 10.00F; 
		return dHeading;	
	}
}