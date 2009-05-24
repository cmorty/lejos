package lejos.nxt;

/**
 * Abstraction for a port that supports I2C sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
public interface I2CPort extends BasicSensorPort {
	public int getId();
}
