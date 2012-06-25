package lejos.nxt.sensor.api;

/**
 * @author ?
 * @deprecated
 *
 */
public class SensorProviderUnit {
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
	
//	public static int getSensorUnit(SensorValueProvider sensor){
//		if (sensor instanceof LengthProvider) {
//			return LENGTH;
//		} else if (sensor instanceof MassProvider) {
//			return MASS;
//		} else if (sensor instanceof TimeProvider) {
//			return TIME;
//		} else if (sensor instanceof MassProvider) {
//			return ELECTRIC_CURRENT;
//		} else if (sensor instanceof TemperatureProvider) {
//			return TEMPERATURE;
//		} else if (sensor instanceof LightProvider) {
//			return LUMINOUS_INTENSITY;
//		} else if (sensor instanceof MoleProvider) {
//			return AMOUNT_OF_SUBSTANCE;
//		} else {
//			throw new NotAFreakinTypeException("invalid sensor unit");
//		}
//	}
}

//class myFilter {
//	public Myfilter(SensorValueProvider theSensor){
//		...
//	}
//}
//
//public int getType() {
//	return SensorProviderUnit.getSensorType(this);
//}