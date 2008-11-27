package lejos.nxt.addon;

import lejos.nxt.*;

/**
 * Abstraction for an RCX light sensor.
 * 
 */
public class RCXLightSensor implements SensorConstants {
	LegacySensorPort port;
	
	/**
	 * Create an RCX light sensor object attached to the specified port.
	 * The sensor will be activated, i.e. the LED will be turned on.
	 * @param port port, e.g. Port.S1
	 */
	public RCXLightSensor(LegacySensorPort port)
	{
		this.port = port;
		port.setTypeAndMode(TYPE_REFLECTION,
                            MODE_PCTFULLSCALE);
	}
	
	/**
	  * Activates an RCX light sensor. This method should be called
	  * if you want to get accurate values from an RCX
	  * sensor. In the case of RCX light sensors, you should see
	  * the LED go on when you call this method.
	  */
	public void activate()
	{
		port.activate();
	}
	
	/**
     * Passivates an RCX light sensor. 
	 */
	public void passivate()
	{
		port.passivate();
	}
	
	/**
	 * Read the current sensor value.
	 * @return Value as a percentage.
	 */
	public int readValue()
	{
		return ((1023 - port.readRawValue()) * 100/ 1023);  
	}
}
