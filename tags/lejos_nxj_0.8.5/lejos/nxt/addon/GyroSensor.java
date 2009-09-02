package lejos.nxt.addon;
   
import lejos.nxt.ADSensorPort;
import lejos.nxt.SensorConstants;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Support the HiTechnic Gyro sensor - untested.
 * http://www.hitechnic.com/
 * 
 * @author Lawrie Griffiths
 *
 */
public class GyroSensor implements SensorConstants {
	ADSensorPort port;
	private int offset = 600;

	public GyroSensor(ADSensorPort port)
	{
		this.port = port;
		port.setTypeAndMode(TYPE_CUSTOM,
                            MODE_RAW);
	}
	
	/**
	 * Read the gyro value
	 * 
	 * @return gyro value
	 */
	public int readValue()
	{ 
		return (port.readRawValue() - offset); 
	}
	
	/**
	 * Set the offset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
}
