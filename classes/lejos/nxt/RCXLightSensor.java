package lejos.nxt;

/**
 * Abstraction for an RCX light sensor.
 * 
 */
public class RCXLightSensor {
	Port port;
	
	/**
	 * Create an RCX light sensor object attached to the specified port.
	 * The sensor will be activated, i.e. the LED will be turned on.
	 * @param port port, e.g. Port.S1
	 */
	public RCXLightSensor(Port port)
	{
		this.port = port;
		port.setTypeAndMode(Port.TYPE_REFLECTION,
                            Port.MODE_PCTFULLSCALE);
	}
	
	/**
	  * Activates an RCX sensor. This method should be called
	  * if you want to get accurate values from an RCX
	  * sensor. In the case of RCX light sensors, you should see
	  * the LED go on when you call this method.
	  */
	public void activate()
	{
		port.setPowerType(1);
	}
	
	/**
     * Passivates an RCX sensor sensor. 
	 */
	public void passivate()
	{
		port.setPowerType(0);
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
