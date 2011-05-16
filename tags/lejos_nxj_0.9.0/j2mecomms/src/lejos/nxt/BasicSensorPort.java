package lejos.nxt;

/**
 * An abstraction for a sensor port that supports 
 * setting and retrieving types and modes of sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
public interface BasicSensorPort extends SensorConstants {

	public int getMode();
	
	public int getType();
	
	public void setMode(int mode);
	
	public void setType(int type);
	
	public void setTypeAndMode(int type, int mode);

}

