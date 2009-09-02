package lejos.nxt;

/**
 * 
 * Constants used to set Sensor types and modes.
 *
 */
public interface SensorConstants {

	public static final int TYPE_NO_SENSOR = 0x00;
	public static final int TYPE_SWITCH = 0x01;
	public static final int TYPE_TEMPERATURE = 0x02;
	public static final int TYPE_REFLECTION = 0x03;
	public static final int TYPE_ANGLE = 0x04;
	public static final int TYPE_LIGHT_ACTIVE = 0x05;
	public static final int TYPE_LIGHT_INACTIVE = 0x06;
	public static final int TYPE_SOUND_DB = 0x07; 
	public static final int TYPE_SOUND_DBA = 0x08;
	public static final int TYPE_CUSTOM = 0x09;
	public static final int TYPE_LOWSPEED = 0x0A;
	public static final int TYPE_LOWSPEED_9V = 0x0B;
	  
	public static final int MODE_RAW = 0x00;
	public static final int MODE_BOOLEAN = 0x20;
	public static final int MODE_TRANSITIONCNT = 0x40;
	public static final int MODE_PERIODCOUNTER = 0x60;
	public static final int MODE_PCTFULLSCALE = 0x80;
	public static final int MODE_CELSIUS = 0xA0;
	public static final int MODE_FARENHEIT = 0xC0;
	public static final int MODE_ANGLESTEP = 0xE0;

}
