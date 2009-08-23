package lejos.nxt;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Constants used to set Sensor types and modes.
 *
 */
public interface SensorConstants {

	public static final int TYPE_NO_SENSOR = 0;
	public static final int TYPE_SWITCH = 1;
	public static final int TYPE_TEMPERATURE = 2;
	public static final int TYPE_REFLECTION = 3;
	public static final int TYPE_ANGLE = 4;
	public static final int TYPE_LIGHT_ACTIVE = 5;
	public static final int TYPE_LIGHT_INACTIVE = 6;
	public static final int TYPE_SOUND_DB = 7; 
	public static final int TYPE_SOUND_DBA = 8;
	public static final int TYPE_CUSTOM = 9;
	public static final int TYPE_LOWSPEED = 10;
	public static final int TYPE_LOWSPEED_9V = 11;
    public static final int TYPE_HISPEED = 12;
    public static final int TYPE_COLORFULL = 13;
    public static final int TYPE_COLORRED = 14;
    public static final int TYPE_COLORGREEN = 15;
    public static final int TYPE_COLORBLUE = 16;
    public static final int TYPE_COLORNONE = 17;
	  
	public static final int MODE_RAW = 0x00;
	public static final int MODE_BOOLEAN = 0x20;
	public static final int MODE_TRANSITIONCNT = 0x40;
	public static final int MODE_PERIODCOUNTER = 0x60;
	public static final int MODE_PCTFULLSCALE = 0x80;
	public static final int MODE_CELSIUS = 0xA0;
	public static final int MODE_FARENHEIT = 0xC0;
	public static final int MODE_ANGLESTEP = 0xE0;
}
