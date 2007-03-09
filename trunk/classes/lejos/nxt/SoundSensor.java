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
	   port.setTypeAndMode(Port.TYPE_SOUND_DB,
                           Port.MODE_PCTFULLSCALE);
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
       port.setTypeAndMode(
    		   (dba ? Port.TYPE_SOUND_DBA
    				: Port.TYPE_SOUND_DB),
    		   Port.MODE_PCTFULLSCALE);   
	}
	
	/**
	 * Set DB r DBA mode.
	 * @param dba true to set DBA mode, false for DB mode.
	 */
	public void setDBA(boolean dba)
	{
	    port.setType((dba ? Port.TYPE_SOUND_DBA
	    				  : Port.TYPE_SOUND_DB));
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
