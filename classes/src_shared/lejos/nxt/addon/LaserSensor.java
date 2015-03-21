package lejos.nxt.addon;

import lejos.nxt.ADSensorPort;
import lejos.nxt.SensorConstants;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * <p>This class represents a Dexter Industries Laser Sensor. The sensor contains a laser and a photodiode to read
 * ambient light values. This sensor can be calibrated to low and high values.</p>
 * 
 *  <p>The Dexter Industries laser can turn on and off very rapidly, with the following characteristics:</p>
 *  <li>it takes about 8-10 ms to turn on and reach full power
 *  <li>it takes about 5 ms to turn off
 * 
 */
public class LaserSensor implements SensorConstants
{
	ADSensorPort port;
	private int _zero = 1023;
	private int _hundred = 0;
	private boolean laser = false;
	
	/**
	 * Create a laser sensor object attached to the specified port.
	 * The laser will be turned off by default.
	 * 
	 * @param port port, e.g. Port.S1
	 */
	public LaserSensor(ADSensorPort port)
	{
		this(port, false);
	}
	
	/**
	 * Create a laser sensor object attached to the specified port,
	 * and sets the laser on or off.
	 * @param port port, e.g. Port.S1
	 * @param laser true to turn on the laser, false for laser off.
	 */
	public LaserSensor(ADSensorPort port, boolean laserState)
	{
	   this.port = port;
	   this.laser = laserState;
       port.setTypeAndMode(
    		   (laserState ? TYPE_LIGHT_ACTIVE
    				       : TYPE_LIGHT_INACTIVE),
    		   MODE_PCTFULLSCALE); 
	}
	
	public void setLaser(boolean laserState)
	{	
		port.setType(laserState ? TYPE_LIGHT_ACTIVE
		                         : TYPE_LIGHT_INACTIVE);
		this.laser = laserState;
	}
	
	public int getLightValue()
	{ 
		if(_hundred == _zero) return 0;
		return 100*(port.readRawValue() - _zero)/(_hundred - _zero); 
	}
	
	/**
	 * Get the light reading
	 * 
	 * @return the light level
	 */
	public int readValue() {
		// TODO: Deprecate some of these read methods.
		return getLightValue();
	}
	
	/**
	 * 
	 * Get the normalized light reading
	 * 
	 * @return the raw light level
	 */
	public int readNormalizedValue() {
		return getNormalizedLightValue();
	}
	
	/* TODO: Options:
	 * getLightLevel()
	 * readLightLevel()
	 * getIntensity()
	 * readIntensity()
	 * getBrightness()
	 * readBrightness()
	 * getLight()
	 */
	
	/**
	 * Get the normalized light reading
	 * 
	 * @return normalized raw value (0 to 1023) LEGO NXT light sensor values typically range from 
	 * 145 (dark) to 890 (sunlight).
	 */
	public int getNormalizedLightValue() {
		return 1023 - port.readRawValue();
	}

/**
 * call this method when the light sensor is reading the low value - used by readValue
 **/
	public void calibrateLow()
	{
		// TODO: Should these methods save calibrated data in static memory?
		_zero = port.readRawValue();
	}
/** 
 *call this method when the light sensor is reading the high value - used by readValue
 */	
	public void calibrateHigh()
	{
		_hundred = port.readRawValue();
	}
	/** 
	 * set the normalized value corresponding to readValue() = 0%
	 * @param low the low value
	 */
	public void setLow(int low) { _zero = 1023 - low;}
	  /** 
     * set the normalized value corresponding to  readValue() = 100%
     * @param high the high value
     */
    public void setHigh(int high) { _hundred = 1023 - high;}
    /**
    * return  the normalized value corresponding to readValue() = 0%
    */
   public int getLow() { return 1023 - _zero;}
    /** 
    * return the normalized value corresponding to  readValue() = 100%
    */
   public int  getHigh() {return 1023 - _hundred;}

	public boolean isLaserOn() {
		return this.laser;
	}
}
