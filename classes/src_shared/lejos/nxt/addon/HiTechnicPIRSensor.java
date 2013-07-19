package lejos.nxt.addon;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;

/**
 * Driver for the HiTechnic PIR sensor (NIS1070).
 * @author contributed by forum user qus.
 */
public class HiTechnicPIRSensor extends I2CSensor {
	private byte[] buf = new byte[1];
	
	public static final int ERROR = Integer.MIN_VALUE;
	public static final int DEFAULT_DEADBAND = 12;

	public HiTechnicPIRSensor(I2CPort port) {
		super(port);
	}

	/**
	 * Returns the reading of the sensor. It's a value between -128 and 127.
	 * Look at HiTechnic's documentation on how to interpretate the value.
	 * If an errors occurs, the value special {@link #ERROR} is returned.
	 * 
	 * @return a value between -128 and 127 or {@link #ERROR}.
	 */
	public int getReading() {
		int ret = getData(0x42, buf, 0, 1);
		return (ret != 0) ? ERROR : buf[0];
	}

	/**
	 * Returns the sensor's deadband setting. Valid deadband values
	 * are 0 to 47. If invalid values are set, this method may return
	 * values between -128 and 127.
	 * 
	 * @return a value between -128 and 127 or {@link #ERROR}.
	 */
	public int getDeadband() {
		int ret = getData(0x41, buf, 0, 1);
		return (ret != 0) ? ERROR : buf[0];
	}

	/**
	 * Changes the sensor's deadband setting. Valid deadband values
	 * are 0 to 47. 
	 * 
	 * @param db a value from 0 to 47
	 * @throws IllegalArgumentException if parameter is less than 0 or greater than 47.
	 */
	public void setDeadband(int db) {
		if (db < 0 || db > 47)
			throw new IllegalArgumentException();
		
		this.sendData(0x41, (byte)db);
	}
}
