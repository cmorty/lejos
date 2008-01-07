package lejos.nxt;

/**
 * An abstraction for a port that supports Analog/Digital sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
public interface ADSensorPort extends BasicSensorPort {

	public boolean readBooleanValue();
	
	public int readRawValue();
	
	public int readValue();
}
