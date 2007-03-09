package lejos.nxt;

/**
 * Abstraction for a NXT light sensor.
 * The light sensor can be calibrated to low and high values. 
 */
public class LightSensor
{
	Port port;
	private int _zero = 1023;
	private int _hundred = 0;
	
	/**
	 * Create a light sensor object attached to the specified port.
	 * The sensor will be set to floodlit mode, i.e. the LED will be turned on.
	 * @param port port, e.g. Port.S1
	 */
	public LightSensor(Port port)
	{
		this.port = port;
		port.setPowerType(0);
		port.setADType(1); // Default to LED on
		port.setTypeAndMode(Port.TYPE_LIGHT_ACTIVE,
                            Port.MODE_PCTFULLSCALE);
	}
	
	/**
	 * Create a light sensor object attached to the specified port,
	 * and sets floodlighting on or off.
	 * @param port port, e.g. Port.S1
	 * @param floodlight true to set floodit mode, false for ambient light.
	 */
	public LightSensor(Port port, boolean floodlight)
	{
	   this.port = port;
	   port.setPowerType(0);
	   port.setADType((floodlight ? 1 : 0));
       port.setTypeAndMode(
    		   (floodlight ? Port.TYPE_LIGHT_ACTIVE
    				        : Port.TYPE_LIGHT_INACTIVE),
    		   Port.MODE_PCTFULLSCALE);
		        
	   
	}
	
	/**
	 * Set floodlighting on or off.
	 * @param floodlight true to set floodit mode, false for ambient light.
	 */
	public void setFloodlight(boolean floodlight)
	{
		port.setADType((floodlight ? 1 : 0));
		port.setType((floodlight ? Port.TYPE_LIGHT_ACTIVE
		                         : Port.TYPE_LIGHT_INACTIVE));
	}

	/**
	 * Read the current sensor value.
	 * Use calibrateLow() to set the zero level, and calibrateHigh to set the 100 level.
	 * @return Value as a percentage of difference between the low and high calibration values. 
	 */
	public int readValue()
	{
//		return ((1023 - port.readRawValue()) * 100/ 1023); 
		if(_hundred == _zero)return 1023;
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
}
