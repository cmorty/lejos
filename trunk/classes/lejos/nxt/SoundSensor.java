package lejos.nxt;

/**
 * Abstraction for a NXT sound sensor.
 * 
 */public class SoundSensor {

	Port port;
	
	/**
	 * Create a sound sensor object attached to the specified port.
	 * The sensor will be set to DB mode.
	 * @param port port, e.g. Port.S1
	 */
	public SoundSensor(Port port)
	{
	   this.port = port;
	   port.setPowerType(0);
	   port.setADType(1); // Default to DB
	}
	
	/**
	 * Create a sound sensor object attached to the specified port,
	 * and set db or DBA mode.
	 * @param port port, e.g. Port.S1
	 * @param dba true to set DBA mode, false for DB mode.
	 */
	public SoundSensor(Port port, boolean dba)
	{
	   this.port = port;
	   port.setPowerType(0);
	   port.setADType((dba ? 2 : 1));
	}
	
	/**
	 * Set DB r DBA mode.
	 * @param dba true to set DBA mode, false for DB mode.
	 */
	public void setDBA(boolean dba)
	{
		port.setADType((dba ? 2 : 1));
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
