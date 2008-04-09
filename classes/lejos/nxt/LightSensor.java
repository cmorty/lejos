package lejos.nxt;

/**
 * Abstraction for a NXT light sensor.
 * The light sensor can be calibrated to low and high values. 
 */
public class LightSensor implements SensorConstants
{
	ADSensorPort port;
	private int _zero = 1023;
	private int _hundred = 0;
	
	/**
	 * Create a light sensor object attached to the specified port.
	 * The sensor will be set to floodlit mode, i.e. the LED will be turned on.
	 * @param port port, e.g. Port.S1
	 */
	public LightSensor(ADSensorPort port)
	{
		this.port = port;
		port.setTypeAndMode(TYPE_LIGHT_ACTIVE,
                            MODE_PCTFULLSCALE);
	}
	
	/**
	 * Create a light sensor object attached to the specified port,
	 * and sets floodlighting on or off.
	 * @param port port, e.g. Port.S1
	 * @param floodlight true to set floodit mode, false for ambient light.
	 */
	public LightSensor(ADSensorPort port, boolean floodlight)
	{
	   this.port = port;
       port.setTypeAndMode(
    		   (floodlight ? TYPE_LIGHT_ACTIVE
    				       : TYPE_LIGHT_INACTIVE),
    		   MODE_PCTFULLSCALE); 
	}
	
	/**
	 * Set floodlighting on or off.
	 * @param floodlight true to set floodit mode, false for ambient light.
	 */
	public void setFloodlight(boolean floodlight)
	{
		port.setType((floodlight ? TYPE_LIGHT_ACTIVE
		                         : TYPE_LIGHT_INACTIVE));
	}

	/**
	 * Read the current sensor value.
	 * Use calibrateLow() to set the zero level, and calibrateHigh to set the 100 level.
	 * @return Value as a percentage of difference between the low and high calibration values. 
	 */
	public int readValue()
	{ 
		if(_hundred == _zero) return 0;
		return 100*(port.readRawValue() - _zero)/(_hundred - _zero); 
	}
	
	/**
	 * Read the current sensor normalized value. Allows more accuracy
	 * than readValue(). For LEGO sensor, values typically range from 
	 * 145 (dark) to 890 (sunlight). 
	 * @return Value as raw normalized (0 to 1023)
	 */
	public int readNormalizedValue() {
		return 1023 - port.readRawValue();
	}

/**
 * call this method when the light sensor is reading the low value - used by readValue
 **/
	public void calibrateLow()
	{
		_zero = port.readRawValue();
	}
/** 
 *call this method whtn the light sensor is reading the high value - used by reaeValue
 */	
	public void calibrateHigh()
	{
		_hundred = port.readRawValue();
	}
	/** 
	 * set the normalized value corresponding to readValue() = 0
	 * @param low
	 */
	public void setLow(int low) { _zero = 1023 - low;}
	  /** 
     * set the normalized value corresponding to  readValue() = 100;
     * @param low
     */
    public void setHigh(int high) { _hundred = 1023 - high;}
    /**
    * return  the normalized value corresponding to readValue() = 0
    */
   public int getLow() { return 1023 - _zero;}
    /** 
    * return the normalized value corresponding to  readValue() = 100;
    * @param low
    */
   public int  getHigh() {return 1023 - _hundred;}
}
