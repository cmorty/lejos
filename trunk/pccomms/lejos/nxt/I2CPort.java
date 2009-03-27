package lejos.nxt;

/**
 * Abstraction for a port that supports I2C sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
public interface I2CPort extends BasicSensorPort {
	
    public static final int STANDARD_MODE = 0;
    public static final int LEGO_MODE = 1;
    public static final int ALWAYS_ACTIVE = 2;
    
	public int getId();
}
