package skoehler.sensor.api;

public interface Quantities {
	
	///////////////// SI base units (http://en.wikipedia.org/wiki/SI_base_unit)

	/** Meters */
	public final static int LENGTH = 1;
	/** Kilograms */
	public final static int MASS = 2;
	/** Seconds */
	public final static int TIME = 3;
	/** Ampere */
	public final static int ELECTRIC_CURRENT = 4;
	/** Kelvin */
	public final static int TEMPERATURE = 5;
	/** Mole */
	public final static int AMOUNT_OF_SUBSTANCE = 6;
	/** Candela */
	public final static int LUMINOUS_INTENSITY = 7;
	
	///////////////// SI derived units (http://en.wikipedia.org/wiki/SI_derived_units)
	
	////////////// SI derived units with special names

	/** Hertz = 1/s */
	public final static int FREQUENCY = 10;
	/** Radian = m/m */
	public final static int ANGLE = 11;
	/** Steradian = (m/m)^2 */
	public final static int SOLID_ANGLE = ANGLE;
	/** Newton = kg*m/s^2 */
	public final static int FORCE = 12;
	/** Newton = kg*m/s^2 */
	public final static int WEIGHT = FORCE;
	/** Pascal = N/m^2 */
	public final static int PRESSURE = 13;
	/** Pascal = N/m^2 */
	public final static int STRESS = PRESSURE;
	/** Joule = N*m = C*V = W*s */
	public final static int ENERGY = 14;
	/** Joule = N*m = C*V = W*s */
	public final static int WORK = ENERGY;
	/** Joule = N*m = C*V = W*s */
	public final static int HEAT = ENERGY;
	
	//TODO more, but rename unit to quantity
//	public final static int WATT = 15;
//	public final static int COULOMB = 16;
//	public final static int VOLT = 17;
//	public final static int FARAD = 18;
//	public final static int OHM = 19;
//	public final static int SIEMENS = 20;
//	public final static int WEBER = 21;
//	public final static int TESLA = 22;
//	public final static int HENRY = 23;
//	// Celsius is omitted because of the offset
//	//public final static int CELSIUS = 0;
//	public final static int LUMEN = CANDELA;
//	public final static int LUX = 25;
//	public final static int BECQUEREL = HERTZ;
//	public final static int GRAY = 26;
//	public final static int SIEVERT = GRAY;
//	public final static int KATAL = 27;
	
	////////////// SI derived units without special names

	/** m^2 */
	public final static int AREA = 40;
	/** m^3 */
	public final static int VOLUME = 41;
	/** m/s */
	public final static int SPEED = 42;
	/** m/s */
	public final static int VELOCITY = SPEED;
	/** m^3/s */
	public final static int VOLUMETRIC_FLOW = 43;
	/** m/s^2 */
	public final static int ACCELERATION = 44;
	/** m/s^3 */
	public final static int JERK = 45;
	/** m/s^3 */
	public final static int JOLT = JERK;
	/** m/s^4 */
	public final static int SNAP = 46;
	/** m/s^4 */
	public final static int JOUNCE = SNAP;
	/** rad/s */
	public final static int ANGULAR_VELOCITY = FREQUENCY;
	/** N*s */
	public final static int MOMENTUM = 47;
	/** N*s */
	public final static int IMPULSE = MOMENTUM;
	/** N*m*s */
	public final static int ANGULAR_MOMENTUM = 48;
	/** N*m = J/rad */
	public final static int TORQUE = 49;
	/** N*m = J/rad */
	public final static int MOMENT_OF_FORCE = TORQUE;
	/** Newton/second */
	public final static int YANK = 50;
	
	//TODO Should it hold ANGULAR_VELOCITY=FREQUENCY or WORK=ENERGY? IMHO yes
	//TODO what to do about color, return values of LightSensor, etc.? I suggest adding COLOR_RGB, COLOR_CMYK, PERCENT, RAW?, UNKNOWN? 
}
