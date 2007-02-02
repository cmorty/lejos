package lejos.nxt;

/**
 * Abstraction for a NXT utltrasonic  sensor.
 * 
 */
public class UltraSonicSensor {
	Port port;
	
	public UltraSonicSensor(Port port)
	{
		this.port = port;
		port.setPowerType(2);
		port.i2cEnable();
	}
	
	public int getDistance() {
		byte buf[] = new byte[1];
		
		int ret = port.i2cStart(1, 0x42, 1, buf, 1, 0);
		
		if (ret == 0) {
			while (port.i2cBusy() != 0) {
				Thread.yield();
			}
			return (buf[0] & 0xff);
		} else return 255;
	}
}
