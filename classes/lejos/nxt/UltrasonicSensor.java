package lejos.nxt;

/**
 * 
 * Abstraction for a NXT Ultrasonic Sensor.
 *
 */
public class UltrasonicSensor extends I2CSensor {
	byte[] buf = new byte[1];
	
	public UltrasonicSensor(I2CPort port) {
		super(port);
	}

	/**
	 * Return distance of object.
	 * 
	 * @return distance or 255 if no object in range
	 */
	public int getDistance() {		
		int ret = getData(0x42, buf, 1);
		
		return (ret == 0 ? (buf[0] & 0xff) : 255);
	}

}
