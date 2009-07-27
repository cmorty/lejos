package lejos.nxt.remote;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * LEGO Communication Protocol constants.
 *
 */
public interface NXTProtocol {
	// Command types constants. Indicates type of packet being sent or received.
	public static byte DIRECT_COMMAND_REPLY = 0x00;
	public static byte SYSTEM_COMMAND_REPLY = 0x01;
	public static byte REPLY_COMMAND = 0x02;
	public static byte DIRECT_COMMAND_NOREPLY = (byte)0x80; // Avoids ~100ms latency
	public static byte SYSTEM_COMMAND_NOREPLY = (byte)0x81; // Avoids ~100ms latency
	
	// System Commands:
	public static byte OPEN_READ = (byte)0x80;
	public static byte OPEN_WRITE = (byte)0x81;
	public static byte READ = (byte)0x82;
	public static byte WRITE = (byte)0x83;
	public static byte CLOSE = (byte)0x84;
	public static byte DELETE = (byte)0x85;
	public static byte FIND_FIRST = (byte)0x86;
	public static byte FIND_NEXT = (byte)0x87;
	public static byte GET_FIRMWARE_VERSION = (byte)0x88;
	public static byte OPEN_WRITE_LINEAR = (byte)0x89;
	public static byte OPEN_READ_LINEAR = (byte)0x8A;
	public static byte OPEN_WRITE_DATA = (byte)0x8B;
	public static byte OPEN_APPEND_DATA = (byte)0x8C;
	// Many commands could be hidden between 0x8D and 0x96!
	public static byte BOOT = (byte)0x97;
	public static byte SET_BRICK_NAME = (byte)0x98;
	// public static byte MYSTERY_COMMAND = (byte)0x99;
	// public static byte MYSTERY_COMMAND = (byte)0x9A;
	public static byte GET_DEVICE_INFO = (byte)0x9B;
	// commands could be hidden here...
	public static byte DELETE_USER_FLASH = (byte)0xA0;
	public static byte POLL_LENGTH = (byte)0xA1;
	public static byte POLL = (byte)0xA2;
	
	public static byte NXJ_FIND_FIRST = (byte)0xB6;
	public static byte NXJ_FIND_NEXT = (byte)0xB7;
    public static byte NXJ_PACKET_MODE = (byte)0xff;
	
	// Poll constants:
	public static byte POLL_BUFFER = (byte)0x00;
	public static byte HIGH_SPEED_BUFFER = (byte)0x01;
		
	// Direct Commands
	public static byte START_PROGRAM = 0x00;
	public static byte STOP_PROGRAM = 0x01;
	public static byte PLAY_SOUND_FILE = 0x02;
	public static byte PLAY_TONE = 0x03;
	public static byte SET_OUTPUT_STATE = 0x04;
	public static byte SET_INPUT_MODE = 0x05;
	public static byte GET_OUTPUT_STATE = 0x06;
	public static byte GET_INPUT_VALUES = 0x07;
	public static byte RESET_SCALED_INPUT_VALUE = 0x08;
	public static byte MESSAGE_WRITE = 0x09;
	public static byte RESET_MOTOR_POSITION = 0x0A;	
	public static byte GET_BATTERY_LEVEL = 0x0B;
	public static byte STOP_SOUND_PLAYBACK = 0x0C;
	public static byte KEEP_ALIVE = 0x0D;
	public static byte LS_GET_STATUS = 0x0E;
	public static byte LS_WRITE = 0x0F;
	public static byte LS_READ = 0x10;
	public static byte GET_CURRENT_PROGRAM_NAME = 0x11;
	// public static byte MYSTERY_OPCODE = 0x12; // ????
	public static byte MESSAGE_READ = 0x13;
	// public static byte POSSIBLY_MORE_HIDDEN = 0x14; // ????
	
	// NXJ additions
	public static byte NXJ_DISCONNECT = 0x20; 
	public static byte NXJ_DEFRAG = 0x21;
	
	// Output state constants 
	// "Mode":
	/** Turn on the specified motor */
	public static byte MOTORON = 0x01;
	/** Use run/brake instead of run/float in PWM */
	public static byte BRAKE = 0x02;
	/** Turns on the regulation */
	public static byte REGULATED = 0x04; 

	// "Regulation Mode":
	/** No regulation will be enabled */
	public static byte REGULATION_MODE_IDLE = 0x00;
	/** Power control will be enabled on specified output */
	public static byte REGULATION_MODE_MOTOR_SPEED = 0x01;
	/** Synchronization will be enabled (Needs enabled on two output) */
	public static byte REGULATION_MODE_MOTOR_SYNC = 0x02; 

	// "RunState":
	/** Output will be idle */
	public static byte MOTOR_RUN_STATE_IDLE = 0x00;
	/** Output will ramp-up */
	public static byte MOTOR_RUN_STATE_RAMPUP = 0x10;	
	/** Output will be running */
	public static byte MOTOR_RUN_STATE_RUNNING = 0x20; 
	/** Output will ramp-down */
	public static byte MOTOR_RUN_STATE_RAMPDOWN = 0x40;
	
	// Input Mode Constants
	// "Port Type":
	/**  */
	public static byte NO_SENSOR = 0x00;
	/**  */
	public static byte SWITCH = 0x01;
	/**  */
	public static byte TEMPERATURE = 0x02;
	/**  */
	public static byte REFLECTION = 0x03;
	/**  */
	public static byte ANGLE = 0x04;
	/**  */
	public static byte LIGHT_ACTIVE = 0x05;
	/**  */
	public static byte LIGHT_INACTIVE = 0x06;
	/**  */
	public static byte SOUND_DB = 0x07;
	/**  */
	public static byte SOUND_DBA = 0x08;
	/**  */
	public static byte CUSTOM = 0x09;
	/**  */
	public static byte LOWSPEED = 0x0A;
	/**  */
	public static byte LOWSPEED_9V = 0x0B;
	/**  */
	public static byte NO_OF_SENSOR_TYPES = 0x0C;

	// "Port Mode":
	/**  */
	public static byte RAWMODE = 0x00;
	/**  */
	public static byte BOOLEANMODE = 0x20;
	/**  */
	public static byte TRANSITIONCNTMODE = 0x40;
	/**  */
	public static byte PERIODCOUNTERMODE = 0x60;
	/**  */
	public static byte PCTFULLSCALEMODE = (byte)0x80;
	/**  */
	public static byte CELSIUSMODE = (byte)0xA0;
	/**  */
	public static byte FAHRENHEITMODE = (byte)0xC0;
	/**  */
	public static byte ANGLESTEPSMODE = (byte)0xE0;
	/**  */
	public static byte SLOPEMASK = 0x1F;
	/**  */
	public static byte MODEMASK = (byte)0xE0;
}


