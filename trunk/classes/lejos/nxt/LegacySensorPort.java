package lejos.nxt;


import lejos.nxt.ADSensorPort;
import lejos.nxt.SensorConstants;

/**
 * Abstraction for a port that supports legacy RCX sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
public interface LegacySensorPort extends ADSensorPort {
	
	public void activate();
	
	public void passivate();

}
