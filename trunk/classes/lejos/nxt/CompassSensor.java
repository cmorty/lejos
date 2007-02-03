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
	
	public int getDegrees() {		
		int ret = getData(0x42, buf, 2);

		return (ret == 0 
				 ? (((buf[0] & 0xff)<< 1) + buf[1])
				 : -1);
	}
}

