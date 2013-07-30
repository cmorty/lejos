package lejos.nxt.addon;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.util.EndianTools;

/**
 * This class support the Digital Pneumatic Pressure Sensor (PPS58-Nx)
 * by MindSensors.
 */
public class MindSensorsPressureSensor extends I2CSensor {

	/*
	 * Code contributed and tested by fussel_dlx on the forums:
	 * http://lejos.sourceforge.net/forum/viewtopic.php?f=6&t=4329
	 * 
	 * Comment: the sensor can pressure in various units. However, using those
	 * units results in a loss of precision. And furthermore, the conversion to PSI or
	 * whatever can be done in Java. The obvious advantage is, that float can be used.
	 */
	
	private static final int ADDRESS = 0x18; 
	private final byte[] buf = new byte[4];
	
	public MindSensorsPressureSensor(I2CPort port) {
		// also works with high speed mode
		super(port, ADDRESS, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
	}
	
	//TODO sensor doesn't support getVendor, getProduct, etc. - what to do?

	/**
	 * Reads the current pressure from the sensor.
	 * Return a negative value on error.
	 * @return the pressure in pascal (1 N/m²)
	 */
	public int readPressure() {
		int r = this.getData(0x53, buf, 0, 4);
		if (r < 0)
			return r;
		
		// according to mindsensor's usermanual, the sensor
		// can measure up to 58PSI, which is about 400000 pascal
		// which is nowhere near the integer wrap around.
		return EndianTools.decodeIntLE(buf, 0);
	}
}
