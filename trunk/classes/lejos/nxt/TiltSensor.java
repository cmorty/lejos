package lejos.nxt;

/**
 * Abstraction for a Mindsensors
 * acceleration (tilt) sensor.
 * 
 */
public class TiltSensor extends I2CSensor {
	byte[] buf = new byte[3];
	
	private static byte X_TILT = 0x42;
	private static byte Y_TILT = 0x43;
	private static byte Z_TILT = 0x44;
	
	private static byte X_ACCEL_LSB = 0x45;
	private static byte Y_ACCEL_LSB = 0x47;
	private static byte Z_ACCEL_LSB = 0x49;
	
	public TiltSensor(I2CPort port)
	{
		super(port);
		port.setType(TYPE_LOWSPEED_9V);
	}
	
	/**
	 * Tilt of sensor along X-axis (see top of Mindsensors.com sensor for
	 * diagram of axis).  128 is level. 
	 * 
	 * @return X tilt value, or -1 if call failed
	 */
	public int getXTilt() {		
		int ret = getData(X_TILT, buf, 1);		
		return (ret == 0 ? (buf[0] & 0xff) : -1);
	}
	
	/**
	 * Returns Y tilt value.
	 * 
	 * @return Y tilt value, or -1 if call failed
	 */
	public int getYTilt() {		
		int ret = getData(Y_TILT, buf, 1);	
		return (ret == 0 ? (buf[0] & 0xff) : -1);
	}
	
	/**
	 * Returns Z tilt value.
	 * 
	 * @return Z tilt value, or -1 if call failed
	 */
	public int getZTilt() {		
		int ret = getData(Z_TILT, buf, 1);	
		return (ret == 0 ? (buf[0] & 0xff) : -1);
	}
	
	/**
	 * Acceleration along X axis. Positive or negative values in mg.
	 * (g = acceleration due to gravity = 9.81 m/s^2)
	 * @return Acceleration e.g. 9810 mg (falling on earth)
	 */
	public int getXAccel() {
		int ret = getData(X_ACCEL_LSB, buf, 2);
		int accel = (buf[0] & 0xFF) | ((buf[1]) << 8);
		return (ret == 0 ? accel : -1);
	}
	
	/**
	 * Acceleration along Y axis. Positive or negative values in mg.
	 * (g = acceleration due to gravity = 9.81 m/s^2)
	 * @return Acceleration e.g. 9810 mg (falling on earth)
	 */
	public int getYAccel() {
		int ret = getData(Y_ACCEL_LSB, buf, 2);
		int accel = (buf[0] & 0xFF) | ((buf[1]) << 8);
		return (ret == 0 ? accel : -1);
	}
	
	/**
	 * Acceleration along Z axis. Positive or negative values in mg.
	 * (g = acceleration due to gravity = 9.81 m/s^2)
	 * @return Acceleration e.g. 9810 mg (falling on earth)
	 */
	public int getZAccel() {
		int ret = getData(Z_ACCEL_LSB, buf, 2);
		int accel = (buf[0] & 0xFF) | ((buf[1]) << 8);
		return (ret == 0 ? accel : -1);
	}
}
