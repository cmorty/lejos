package lejos.nxt.sensor.api;

/**
 * Base SI units to be returned by 
 * @author Kirk
 *
 */
public interface SensorSIUnits {
	/**
	 * metre
	 */
	public static final int LENGTH = 0;
	/**
	 * kilogram
	 */
	public static final int MASS = 1;
	/**
	 * second
	 */
	public static final int TIME = 2;
	/**
	 * ampere
	 */
	public static final int ELECTRIC_CURRENT = 3;
	/**
	 * kelvin
	 */
	public static final int TEMPERATURE = 4;
	/**
	 * candela
	 */
	public static final int LUMINOUS_INTENSITY = 5;
	/**
	 * mole
	 */
	public static final int AMOUNT_OF_SUBSTANCE = 6;
}
