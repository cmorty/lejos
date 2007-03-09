package lejos.nxt;

/**
 * Abstraction for a NXT touch sensor.
 * Also works with RCX touch sensors.
 * 
 */
public class TouchSensor {
	Port port;
	
	/**
	 * Create a touch sensor object attached to the specified port.
	 * @param port port, e.g. Port.S1
	 */
	public TouchSensor(Port port)
	{
	   this.port = port;
	   port.setTypeAndMode(Port.TYPE_SWITCH,
	                       Port.MODE_BOOLEAN);
	}
	/**
	 * Check if the sensor is pressed.
	 * @return <code>true</code> if sensor is pressed, <code>false</code> otherwise.
	 */
	public boolean isPressed()
	{
		return (port.readRawValue() < 600);  
	}

}
