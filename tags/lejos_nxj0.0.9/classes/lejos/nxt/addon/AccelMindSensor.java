package lejos.nxt.addon;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.robotics.Accelerometer;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * This class works with the Mindsensors acceleration (tilt) sensor.
 * 
 */
public class AccelMindSensor extends I2CSensor implements Accelerometer {
	byte[] buf = new byte[2];
	byte[] buf2 = new byte[1];
	
	private static byte X_TILT = 0x42;
	private static byte Y_TILT = 0x43;
	private static byte Z_TILT = 0x44;
	
	private static byte MINDSTORMS_X_ACCEL_LSB = 0x45;
	private static byte MINDSTORMS_Y_ACCEL_LSB = 0x47;
	private static byte MINDSTORMS_Z_ACCEL_LSB = 0x49;
	
	public AccelMindSensor(I2CPort port) {
		this(port, DEFAULT_I2C_ADDRESS);
	}

	public AccelMindSensor(I2CPort port, int address) {
		super(port, address, I2CPort.LEGO_MODE, TYPE_LOWSPEED_9V);
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
			int ret = getData(MINDSTORMS_X_ACCEL_LSB, buf, 2);
			int accel = (buf[0] & 0xFF) | ((buf[1]) << 8);
			return (ret == 0 ? accel : -1);
	}
	
	/**
	 * Acceleration along Y axis. Positive or negative values in mg.
	 * (g = acceleration due to gravity = 9.81 m/s^2)
	 * @return Acceleration e.g. 9810 mg (falling on earth)
	 */
	public int getYAccel() {
			int ret = getData(MINDSTORMS_Y_ACCEL_LSB, buf, 2);
			int accel = (buf[0] & 0xFF) | ((buf[1]) << 8);
			return (ret == 0 ? accel : -1);
	}
	
	/**
	 * Acceleration along Z axis. Positive or negative values in mg.
	 * (g = acceleration due to gravity = 9.81 m/s^2)
	 * @return Acceleration e.g. 9810 mg (falling on earth)
	 */
	public int getZAccel() {
			int ret = getData(MINDSTORMS_Z_ACCEL_LSB, buf, 2);
			int accel = (buf[0] & 0xFF) | ((buf[1]) << 8);
			return (ret == 0 ? accel : -1);
	}
}
