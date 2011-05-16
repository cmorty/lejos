package lejos.nxt.addon;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.util.Delay;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * This Class manages the Micro Infinity CruizcoreGyro.
 * 
 * @author Daniele Benedettelli, February 2011
 * @version 1.0
 */
public class CruizcoreGyro extends I2CSensor {

	private byte[] inBuf = new byte[11];
	private static final byte GYRO_ADDRESS = 0x01; // actually 0x02 >> 1
	
	// values returned are signed short integers multiplied by 100
	private static final byte ANGLE = 0x42; // 0x43 (2 Bytes)
	
	private static final byte RATE = 0x44; // 0x45 (2 Bytes)
	
	private static final byte ACCEL_X = 0x46; // 0x47 (2 Bytes)
//	private static final byte ACCEL_Y = 0x48; // 0x49 (2 Bytes)
//	private static final byte ACCEL_Z = 0x4A; // 0x4B (2 Bytes)
	
	// the commands are issued by just reading these registers 

	private static final byte RESET = 0x60;

	private static final byte SELECT_2G = 0x61;
	
	private static final byte SELECT_4G = 0x62;
	
	private static final byte SELECT_8G = 0x63;
	
	// last read data
	private int angle = 0;
	
	private int rate = 0;
	
	private int[] accel = new int[3];
	
	/**
	 * Instantiates a new Cruizcore Gyro sensor.
	 *
	 * @param port the port the sensor is attached to
	 */
	public CruizcoreGyro(I2CPort port) {
		super(port);
		port.setType(TYPE_LOWSPEED);
		this.address = GYRO_ADDRESS;
	}
	
	/**
	 * Read all data from the sensor and save values in the private properties of the class. 
	 * Use {@link #getLastAccel} and similar methods to retrieve values.
	 *
	 * @return true, if successful
	 */
	public boolean readAllData() {
		int ret = getData(ANGLE,inBuf,10);
		if (ret==0) {
			angle = inBuf[1]*256 + inBuf[0];
			rate = inBuf[3]*256 + inBuf[2];
			accel[0] = inBuf[5]*256 + inBuf[4];
			accel[1] = inBuf[7]*256 + inBuf[6];
			accel[2] = inBuf[9]*256 + inBuf[8];
		} 
		return ret==0;
	}
	
	/**
	 * Gets the last acceleration read from the 3 axes.
	 * See {@link #readAllData}
	 *
	 * @return the last acceleration
	 */
	public int[] getLastAccel() {
		return accel;
	}
	
	/**
	 * Gets the last accel.
	 * See {@link #readAllData}
	 *
	 * @param axis the axis (0 for X, 1 for Y, 2 for Z)
	 * @return the last acceleration
	 */
	public int getLastAccel(int axis) {
		if ( axis>=0 && axis<=2) 
			return accel[axis];
		return 0;
	}
	
	/**
	 * Gets the last pitch.
	 * See {@link #readAllData}
	 *
	 * @return the last pitch
	 */
	public float getLastPitch() {
		float sinPitch = accel[1]/1000;
		return (float) Math.asin(sinPitch);
	}
	
	/**
	 * Gets the last roll.
	 * See {@link #readAllData}
	 *
	 * @return the last roll
	 */
	public float getLastRoll() {
		float sinRoll = (float) (accel[0]/(1000*Math.cos(getLastPitch())));
		return (float) Math.asin(sinRoll);
	}
	
	/**
	 * Gets the last rate.
	 * See {@link #readAllData}
	 *
	 * @return the last rotation rate
	 */
	public int getLastRate() {
		return rate;
	}
	
	/**
	 * Gets the last angle.
	 * See {@link #readAllData}
	 *
	 * @return the last angle
	 */
	public int getLastAngle() {
		return angle;
	}
	
	/**
	 * Gets the accel.
	 * See {@link #readAllData}
	 *
	 * @return the acceleration
	 */
	public int[] getAccel() {
		int ret = getData(ACCEL_X,inBuf,6);
		if (ret==0) {
			accel[0] = inBuf[1]*256 + inBuf[0];
			accel[1] = inBuf[3]*256 + inBuf[2];
			accel[2] = inBuf[5]*256 + inBuf[4];
		} 
		return accel;
	}
	
	// 0 -> X
	// 1 -> Y
	// 2 -> Z
	/**
	 * Gets the acceleration in the specified axis.
	 *
	 * @param axis the axis (0 for X, 1 for Y, 2 for Z)
	 * @return the acceleration
	 */
	public int getAccel(int axis) {
		int ret = 0;
		if ( axis>=0 && axis<=2 ) 
			ret = getData(ACCEL_X+axis,inBuf,2);
		if (ret==0) {
			accel[axis] = inBuf[1]*256 + inBuf[0];
		} 
		return accel[axis];
	}	
	
	/**
	 * Gets the accumulated angle (heading).
	 *
	 * @return the angle
	 */
	public int getAngle() {
		int ret = getData(ANGLE,inBuf,2);
		if (ret==0) {
			angle =  inBuf[1]*256 + inBuf[0];
			return angle;
		}
		return 0;
	}
	
	/**
	 * Gets the rate.
	 *
	 * @return the rotation rate
	 */
	public int getRate() {
		int ret = getData(RATE,inBuf,2);
		if (ret==0) {
			return inBuf[1]*256 + inBuf[0];
		}
		return 0;
	}
	
	/**
	 * Sets the acc scale.
	 *
	 * @param sf the scale factor 
	 * 0 for +/- 2G
	 * 1 for +/- 4G
	 * 2 for +/- 8g
	 * @return true, if successful
	 */
	public boolean setAccScale(byte sf)
	{
		int res = sendData(SELECT_2G+sf, (byte)0);
		return res==0;
	}
	
	/**
	 * Sets the acceleration scale factor to 2G.
	 *
	 * @return true, if successful
	 */
	public boolean setAccScale2G()
	{
		int res = sendData(SELECT_2G, (byte)0);
		return res==0;
	}	

	/**
	 * Sets the acceleration scale factor to 4G.
	 *
	 * @return true, if successful
	 */
	public boolean setAccScale4G()
	{
		int res = sendData(SELECT_4G, (byte)0);
		return res==0;
	}	
	
	/**
	 * Sets the acceleration scale factor to 8G.
	 *
	 * @return true, if successful
	 */
	public boolean setAccScale8G()
	{
		int res = sendData(SELECT_8G, (byte)0);
		return res==0;
	}		
	
	/**
	 * Resets the accumulated angle (heading).
	 *
	 * @return true, if successful
	 */
	public boolean reset() {
		int res = sendData(RESET, (byte)0);
		Delay.msDelay(750);
		return res==0;		
	}
}
