package lejos.nxt;

/**
 * Abstraction for a NXT light sensor.
 * 
 */
public class LightSensor {
	Port port;
	
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
	}
	
	/**
	 * Set floodlighting on or off.
	 * @param floodlight true to set floodit mode, false for ambient light.
	 */
	public void setFloodlight(boolean floodlight)
	{
		port.setADType((floodlight ? 1 : 0));
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
