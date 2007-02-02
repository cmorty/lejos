package lejos.nxt;

/**
 * Abstraction for a NXT utltrasonic  sensor.
 * 
 */
public class UltraSonicSensor {
	Port port;
	byte[] buf;
	
	public UltraSonicSensor(Port port)
	{
		this.port = port;
		buf = new byte[1];
		port.setPowerType(2);
		port.i2cEnable();
	}
	
	public int getDistance() {		
		int ret = port.i2cStart(1, 0x42, 1, buf, 1, 0);
		
		if (ret == 0) {
			while (port.i2cBusy() != 0) {
				Thread.yield();
			}
			return (buf[0] & 0xff);
		} else return 255;
	}
}
