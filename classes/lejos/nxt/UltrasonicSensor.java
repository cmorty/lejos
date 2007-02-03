package lejos.nxt;

public class UltrasonicSensor extends I2CSensor {
	byte[] buf;

	public UltrasonicSensor(Port port)
	{
		this.port = port;
		buf = new byte[1];
		port.setPowerType(2);
		port.i2cEnable();
	}

	public int getDistance() {		
		int ret = getData(0x42, buf, 1);
		
		return (ret == 0 ? (buf[0] & 0xff) : 255);
	}

}
