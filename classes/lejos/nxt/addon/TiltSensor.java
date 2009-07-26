package lejos.nxt.addon;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * This class works with HiTechnic or Mindsensors acceleration (tilt) sensor.
 * 
 */
public class TiltSensor extends I2CSensor {
	byte[] buf = new byte[2];
	byte[] buf2 = new byte[1];
	
	private static final String MINDSENSORS_ID = "mndsnsrs";
	
	private static byte X_TILT = 0x42;
	private static byte Y_TILT = 0x43;
	private static byte Z_TILT = 0x44;
	
	private static byte MINDSTORMS_X_ACCEL_LSB = 0x45;
	private static byte MINDSTORMS_Y_ACCEL_LSB = 0x47;
	private static byte MINDSTORMS_Z_ACCEL_LSB = 0x49;
	
	private static byte HITECHNIC_X_ACCEL_2BITS = 0x45;
	private static byte HITECHNIC_Y_ACCEL_2BITS = 0x46;
	private static byte HITECHNIC_Z_ACCEL_2BITS = 0x47;
	
	private boolean isMindsensors; // For comparing HiTechnic vs. Mindsensors
	
	public TiltSensor(I2CPort port)
	{
		super(port);
		port.setType(TYPE_LOWSPEED_9V);
		isMindsensors = (this.getProductID().equals(MINDSENSORS_ID));
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
		if (isMindsensors) {
			int ret = getData(MINDSTORMS_X_ACCEL_LSB, buf, 2);
			int accel = (buf[0] & 0xFF) | ((buf[1]) << 8);
			return (ret == 0 ? accel : -1);
		} else {
			int ret = getData(X_TILT, buf, 1);
			if (ret != 0) return -1;
			ret = getData(HITECHNIC_X_ACCEL_2BITS, buf2, 1);
			if (ret != 0) return -1;
			return ((buf[0] & 0xFF) << 2) + (buf2[0] & 0xFF);
		}
	}
	
	/**
	 * Acceleration along Y axis. Positive or negative values in mg.
	 * (g = acceleration due to gravity = 9.81 m/s^2)
	 * @return Acceleration e.g. 9810 mg (falling on earth)
	 */
	public int getYAccel() {
		if (isMindsensors) {
			int ret = getData(MINDSTORMS_Y_ACCEL_LSB, buf, 2);
			int accel = (buf[0] & 0xFF) | ((buf[1]) << 8);
			return (ret == 0 ? accel : -1);
		} else {
			int ret = getData(Y_TILT, buf, 1);
			if (ret != 0) return -1;
			ret = getData(HITECHNIC_Y_ACCEL_2BITS, buf2, 1);
			if (ret != 0) return -1;
			return ((buf[0] & 0xFF) << 2) + (buf2[0] & 0xFF);
		}
	}
	
	/**
	 * Acceleration along Z axis. Positive or negative values in mg.
	 * (g = acceleration due to gravity = 9.81 m/s^2)
	 * @return Acceleration e.g. 9810 mg (falling on earth)
	 */
	public int getZAccel() {
		if (isMindsensors) {
			int ret = getData(MINDSTORMS_Z_ACCEL_LSB, buf, 2);
			int accel = (buf[0] & 0xFF) | ((buf[1]) << 8);
			return (ret == 0 ? accel : -1);
		} else {
			int ret = getData(Z_TILT, buf, 1);
			if (ret != 0) return -1;
			ret = getData(HITECHNIC_Z_ACCEL_2BITS, buf2, 1);
			if (ret != 0) return -1;
			return ((buf[0] & 0xFF) << 2) + (buf2[0] & 0xFF);
		}
	}
}
