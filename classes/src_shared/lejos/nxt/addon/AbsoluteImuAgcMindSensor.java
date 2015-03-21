package lejos.nxt.addon;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.robotics.Accelerometer;
import lejos.robotics.Gyroscope;
import lejos.robotics.DirectionFinder;
import lejos.util.EndianTools;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 * 
 */

/**
 * This class works with the Mindsensors AbsoluteIMUAGC, a accelerometer,
 * gyroscope and compass.
 * 
 * TODO there are a lot of features not supported by this class
 */
public class AbsoluteImuAgcMindSensor extends I2CSensor implements
		Accelerometer, DirectionFinder {
	/*
	 * Documentation:
	 * http://www.mindsensors.com/index.php?module=documents&JAS_DocumentManager_op=viewDocument&JAS_Document_id=197
	 * ProductId: "Mindsensors"
	 * SensorType: "AbsoluteIMU-AGC."
	 * (confirmed for version " V1.12")
	 * 
	 * @author Hans W. Kramer
	 * @version 2013-10-29
	 *
	 */

	// this is the factory default address according to the Mindsensors.com
	// AbsoluteIMU User Guide, 2012
	protected static final int AGC_I2C_ADDRESS = 0x22;
	
	
	//system register addresses
	private static final byte SOFTWARE_VERSION = 0x00;
	private static final byte VENDOR_ID = 0x08;
	private static final byte DEVICE_ID = 0x10;
	
	//sensitivity
	private byte sensitivity = 1; //default, after power up
	
	//buffers
	//private byte[] buf = new byte[6];
	//private byte[] buf8 = new byte[8];
	private byte[] buf = new byte[8];

	// Accelerometer related constants
	private static final byte BASE_TILT = 0x42;
	private static final byte OFF_X_TILT = 0x00;
	private static final byte OFF_Y_TILT = 0x01;
	private static final byte OFF_Z_TILT = 0x02;

	private static final byte BASE_ACCEL = 0x45;
	private static final byte OFF_X_ACCEL = 0x00;
	private static final byte OFF_Y_ACCEL = 0x02;
	private static final byte OFF_Z_ACCEL = 0x04;

	public static final int ERROR = Integer.MIN_VALUE;

	// Gyroscope related constants
	private static final byte SENSITIVITY_BASE = 0x31;
	private static final byte GYRO_SENSITIVITY = 0x19;
	
	private static final byte BASE_GYRO = 0x53;
	private static final byte OFF_X_GYRO = 0x00;
	private static final byte OFF_Y_GYRO = 0x02;
	private static final byte OFF_Z_GYRO = 0x04;
	
	// Used by the getAxis() method:
	private Gyroscope x_gyroscope = null;
	private Gyroscope y_gyroscope = null;
	private Gyroscope z_gyroscope = null;

	private static final byte FILTER_GYRO = 0x5A;
	
	private float gyroMultiplier = 8.75f/1000; //default on power up

	// Direction finder related constants
	private float cartesianCalibrate = 0; // Used by both cartesian methods.
	private final static byte COMMAND = 0x41;
	private final static byte BEGIN_CALIBRATION = 0x43;
	private final static byte END_CALIBRATION = 0x63;
	private static final byte BASE_COMPASS = 0x4B;
	
	// Raw magnetic field related constants
	private static final byte BASE_RAW_MAGNETIC = 0x4D;
	private static final byte OFF_X_RAW_MAGNETIC = 0x00;
	private static final byte OFF_Y_RAW_MAGNETIC = 0x02;
	private static final byte OFF_Z_RAW_MAGNETIC = 0x04;

	
	//Constructors
	//============
	
	public AbsoluteImuAgcMindSensor(I2CPort port) {
		this(port, AGC_I2C_ADDRESS);
	}

	public AbsoluteImuAgcMindSensor(I2CPort port, int address) {
		super(port, address, I2CPort.LEGO_MODE, TYPE_LOWSPEED_9V);
	}
	
	//System methods
	//==============
	
	/**
	 *  Software version
	 */
	public String getVersion(){
		
		getData(SOFTWARE_VERSION, buf, 8);
		return new String(buf);
	}
	/**
	 *  Vendor Id
	 */
	public String getVendorID(){
		
		getData(VENDOR_ID, buf, 8);
		return new String(buf);
	}
	
	/**
	 *  Device Id
	 */
	public String getDeviceID(){
		
		getData(DEVICE_ID, buf, 8);
		return new String(buf);
	}
	/**
	 * Set the sensitivity
	 * 
	 * 1 0x31 Change Accelerometer Sensitivity to 2G ** Change sensitivity of
	 * Gyro to 250 degrees/sec 2 0x32 Change Accelerometer Sensitivity to 4G **
	 * 3 Change sensitivity of Gyro to 500 degrees/sec 3 0x33 Change
	 * Accelerometer Sensitivity to 8G ** Change sensitivity of Gyro to 2000
	 * degrees/sec 4 0x34 Change Accelerometer Sensitivity to 16G ** Change
	 * sensitivity of Gyro to 2000 degrees/sec
	 * 
	 */
	public void setSensitivity(byte n) {

		if (n > 0x00 && n < 0x05) {
			buf[0] = (byte) (SENSITIVITY_BASE + n - 1);
			super.sendData(COMMAND, buf, 1);
			
			sensitivity = n;
			
			switch(sensitivity)
			{
			case 1: 	gyroMultiplier = 8.75f/1000;break;
			case 2: 	gyroMultiplier = 17.5f/1000;break;
			case 3: 	gyroMultiplier = 70.0f/1000;break;
			case 4: 	gyroMultiplier = 70.0f/1000;break;
			default: 	gyroMultiplier = 8.75f/1000;break;
			
			}
			
			try{ 
				
				Thread.sleep(50);//
			} catch (Exception e){
				
				System.out.println("setSensitivity() " + e.getMessage());
			}
		}
	}
	/** *
	 * get the sensitivity
	 * 
	 */
	public byte getSensitivity(){
		
		/*
		 * Information
		 * From: Deepak Patil
		 * Wednesday, October 23, 2013 15:20
		 * Current sensitivity of the device is not available to read.
		 * Note, it gets reset to 1 when power is disconnected.
		 * Set it to desired level at the beginning of program, and keep track of it in the program.
		 * 
		 * */
		
		return (byte) sensitivity;
	}
	/*
	 * Set the gyro filter
	 */
	public void setGyroFilter(byte n) {

		if (n >= 0x00 && n <= 0x07) {
			buf[0] = n;
			super.sendData(FILTER_GYRO, buf, 1);
		}
	}
	/** *
	 * get the gyro filter
	 * 
	 */
	public byte getGyroFilter(){
		
		int ret = getData(FILTER_GYRO, buf, 1);
		if (ret != 0)
			return (byte)0xFF;

		return (byte)(buf[0] & 0xFF);
		
	}
	
	// Accelerometer methods
	// =====================
	
	/**
	 * Tilt of sensor along X-axis (see top of Mindsensors.com sensor for
	 * diagram of axis). 0 is level.
	 * 
	 * @return X tilt value in degrees, or {@link #ERROR} if call failed
	 */
	public int getXTilt() {
		int ret = getData(BASE_TILT + OFF_X_TILT, buf, 1);
		if (ret != 0)
			return ERROR;

		return (buf[0] & 0xFF) - 128;
	}
	/**
	 * Returns Y tilt value (see top of Mindsensors.com sensor for diagram of
	 * axis). 0 is level.
	 * 
	 * @return Y tilt value in degrees, or {@link #ERROR} if call failed
	 */
	public int getYTilt() {
		int ret = getData(BASE_TILT + OFF_Y_TILT, buf, 1);
		if (ret != 0)
			return ERROR;

		return (buf[0] & 0xFF) - 128;
	}
	/**
	 * Returns Z tilt value (see top of Mindsensors.com sensor for diagram of
	 * axis). 0 is level.
	 * 
	 * @return Z tilt value in degrees, or {@link #ERROR} if call failed
	 */
	public int getZTilt() {
		int ret = getData(BASE_TILT + OFF_Z_TILT, buf, 1);
		if (ret != 0)
			return ERROR;

		return (buf[0] & 0xFF) - 128;
	}
	/**
	 * Acceleration along X axis. Positive or negative values in mg. (g =
	 * acceleration due to gravity = 9.81 m/s^2)
	 * 
	 * @return Acceleration e.g. 9810 mg (falling on earth) or {@link #ERROR}.
	 */
	public int getXAccel() {
		int ret = getData(BASE_ACCEL + OFF_X_ACCEL, buf, 2);
		if (ret != 0)
			return ERROR;

		return EndianTools.decodeShortLE(buf, 0);
	}
	/**
	 * Acceleration along Y axis. Positive or negative values in mg. (g =
	 * acceleration due to gravity = 9.81 m/s^2)
	 * 
	 * @return Acceleration e.g. 9810 mg (falling on earth) or {@link #ERROR}.
	 */
	public int getYAccel() {
		int ret = getData(BASE_ACCEL + OFF_Y_ACCEL, buf, 2);
		if (ret != 0)
			return ERROR;

		return EndianTools.decodeShortLE(buf, 0);
	}
	/**
	 * Acceleration along Z axis. Positive or negative values in mg. (g =
	 * acceleration due to gravity = 9.81 m/s^2)
	 * 
	 * @return Acceleration e.g. 9810 mg (falling on earth) or {@link #ERROR}.
	 */
	public int getZAccel() {
		int ret = getData(BASE_ACCEL + OFF_Z_ACCEL, buf, 2);
		if (ret != 0)
			return ERROR;

		return EndianTools.decodeShortLE(buf, 0);
	}
	/**
	 * Reads all 3 tilt values into the given array. Elements off+0, off+1, and
	 * off+2 are filled with X, Y, and Z axis.
	 * 
	 * @param dst
	 *            destination array.
	 * @param off
	 *            offset
	 * @return true on success, false on error
	 */
	public boolean getAllTilt(int[] dst, int off) {
		int ret = getData(BASE_TILT, buf, 0, 3);
		if (ret != 0)
			return false;

		dst[off + 0] = (buf[0] & 0xFF) - 128;
		dst[off + 1] = (buf[1] & 0xFF) - 128;
		dst[off + 2] = (buf[2] & 0xFF) - 128;
		return true;
	}
	/**
	 * Reads all 3 acceleration values into the given array. Elements off+0,
	 * off+1, and off+2 are filled with X, Y, and Z axis.
	 * 
	 * @param dst
	 *            destination array.
	 * @param off
	 *            offset
	 * @return true on success, false on error
	 */
	public boolean getAllAccel(int[] dst, int off) {
		int ret = getData(BASE_ACCEL, buf, 0, 6);
		if (ret != 0)
			return false;

		dst[off + 0] = EndianTools.decodeShortLE(buf, OFF_X_ACCEL);
		dst[off + 1] = EndianTools.decodeShortLE(buf, OFF_Y_ACCEL);
		dst[off + 2] = EndianTools.decodeShortLE(buf, OFF_Z_ACCEL);
		return true;
	}

	// Gyroscope classes and methods
	// =============================

	/**
	 * Axis units supported by the sensor. Used with the gyro
	 * 
	 * @author BB
	 * 
	 */
	public enum Axis {
		X, Y, Z;
	}
	/**
	 * This method returns an instance of one of the three axes (X, Y, or Z)
	 * 
	 * @param axis
	 *            The axis (X, Y or Z) to retrieve Gyroscope object
	 * @return A Gyroscope object representing the axis you requested.
	 */
	public Gyroscope getAxis(Axis axis) {
		switch (axis) {
		case X:
			if (x_gyroscope == null)
				x_gyroscope = new GyroAxis(axis);
			return x_gyroscope;
		case Y:
			if (y_gyroscope == null)
				y_gyroscope = new GyroAxis(axis);
			return y_gyroscope;
		case Z:
			if (z_gyroscope == null)
				z_gyroscope = new GyroAxis(axis);
			return z_gyroscope;
		}
		return null; // actually impossible to reach this line of code
	}

	private class GyroAxis implements Gyroscope {

		private byte gyroAddress;
		
		public GyroAxis(Axis axis) {
			
			switch (axis) {
			case X:				
				gyroAddress = BASE_GYRO + OFF_X_GYRO;
				break;
			case Y:
				gyroAddress = BASE_GYRO + OFF_Y_GYRO;
				break;
			case Z:
				gyroAddress = BASE_GYRO + OFF_Z_GYRO;
				break;

			}
		}
		/**
		 * Implementor must calculate and return the angular velocity in degrees
		 * per second.
		 * 
		 * @return Angular velocity in degrees/second
		 */
		public float getAngularVelocity() {
			
			int ret = getData(gyroAddress, buf, 2);

			if (ret != 0)
				return ERROR;

			int iAngularVelocity = (0xFF & buf[0]) | ((0xFF & buf[1]) << 8);
			 //correct for sign rollover
			if(iAngularVelocity > 0x8FFF) iAngularVelocity  = iAngularVelocity - 0xFFFF - 1;
			float dAngularVelocity = iAngularVelocity * gyroMultiplier;

			return dAngularVelocity;
		}
		/**
		 * Implementor must calculate and set the offset/bias value for use in
		 * <code>getAngularVelocity()</code>.
		 */
		public void recalibrateOffset() {

			;// not implemented, seems factory calibration is sufficient

		}
	}

	// DirectionFinder methods
	// =======================

	/**
	 * Returns the directional heading in degrees. (0 to 359.9) 0 is due North
	 * (on Mindsensors circuit board a white arrow indicates the direction of
	 * compass). Reading increases clockwise.
	 * 
	 * @return Heading in degrees. Resolution is within 0.1 degrees
	 */
	public float getDegrees() {
		int ret = getData(BASE_COMPASS, buf, 2);
		if (ret != 0)
			return -1;

		// TODO: The following commented out code works when Mindsensors compass
		// in integer mode
		// Add ability to set to integer mode.
		
		 int iHeading = (0xFF & buf[0]) | ((0xFF & buf[1]) << 8); float
		 dHeading = iHeading / 10.00F;

		// Byte mode (default - will use Integer mode later)
		//int dHeading = (0xFF & buf[0]);
		//dHeading = dHeading * 360;
		//dHeading = dHeading / 255;
		return dHeading;

	}

	/**
	 * Compass readings increase clockwise from 0 to 360, but Cartesian
	 * coordinate systems increase counter-clockwise. This method returns the
	 * Cartesian compass reading. Also, the resetCartesianZero() method can be
	 * used to designate any direction as zero, rather than relying on North as
	 * being zero.
	 * 
	 * @return Cartesian direction.
	 */
	public float getDegreesCartesian() {
		float degrees = cartesianCalibrate - getDegrees();
		if (degrees >= 360)
			degrees -= 360;
		if (degrees < 0)
			degrees += 360;
		return degrees;
	}
	/**
	 * Changes the current direction the compass is facing into the zero angle.
	 * 
	 */
	public void resetCartesianZero() {
		cartesianCalibrate = getDegrees();
	}

	/**
	 * Starts calibration for Mindsensors.com compass. Must rotate *very* slowly
	 * ,taking at least 20 seconds per rotation. At least 2 full rotations. Must
	 * call stopCalibration() when done.
	 */
	public void startCalibration() {
		buf[0] = BEGIN_CALIBRATION;
		// Same value for HiTechnic and Mindsensors
		super.sendData(COMMAND, buf, 1);
	}

	/**
	 * Ends calibration sequence.
	 * 
	 */
	public void stopCalibration() {
		buf[0] = END_CALIBRATION;
		super.sendData(COMMAND, buf, 1);
	}
	
	/**
	 * Raw magnetic filed along X axis. 
	 */
	public int getXRawMagnetic() {
		int ret = getData(BASE_RAW_MAGNETIC + OFF_X_RAW_MAGNETIC, buf, 2);
		if (ret != 0)
			return ERROR;

		return EndianTools.decodeShortLE(buf, 0);
	}
	
	/**
	 * Raw magnetic filed along Y axis. 
	 */
	public int getYRawMagnetic() {
		int ret = getData(BASE_RAW_MAGNETIC + OFF_Y_RAW_MAGNETIC, buf, 2);
		if (ret != 0)
			return ERROR;

		return EndianTools.decodeShortLE(buf, 0);
	}
	
	/**
	 * Raw magnetic filed along Z axis. 
	 */
	public int getZRawMagnetic() {
		int ret = getData(BASE_RAW_MAGNETIC + OFF_Z_RAW_MAGNETIC, buf, 2);
		if (ret != 0)
			return ERROR;

		return EndianTools.decodeShortLE(buf, 0);
	}
	
}
