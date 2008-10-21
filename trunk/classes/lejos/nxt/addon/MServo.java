package lejos.nxt.addon;

import lejos.nxt.*;

/**
 * MServo, is a abstraction to model any RC Servo (continous and non continous)  plugged to
 * 
 * @author Juan Antonio Brenha Moral
 */
public class MServo extends I2CSensor{
	private String name = "";//String to describe any Motor connected to LSC
	private int MSC_position; //Position where Servo has been plugged
	private byte servoPosition[] = {0x5A,0x5B,0x5C,0x5D,0x5E,0x5F,0x60,0x61};
	//Servo ID
	private SensorPort portConnected;//What

	private int angle;
	private int min_angle = 0;
	private int max_angle = 2500;//Ms
	private int init_angle = 500;//Ms

	/**
	 *
	 * Constructor with the feature to set min, max and init angle
	 *
	 * @param port
	 * @param location
	 * @param servoName
	 * @param SPI_PORT
	 * @param min_angle
	 * @param max_angle
	 * @param init_angle
	 *
	 */
	public MServo(SensorPort port, int location, String servoName){
		super(port);
		this.name = servoName;
		this.MSC_position = location;
	}
	
	/**
	 *
	 * Constructor with the feature to set min, max and init angle
	 *
	 * @param port
	 * @param location
	 * @param servoName
	 * @param SPI_PORT
	 * @param min_angle
	 * @param max_angle
	 * @param init_angle
	 *
	 */
	public MServo(SensorPort port, int location, String servoName, int min_angle, int max_angle,int init_angle){
		this(port,location,servoName);
		
		this.min_angle = min_angle;
		this.max_angle = max_angle;		
		this.init_angle = init_angle;
	}

	/**
	 * Method to set an Angle in a RC Servo. 
	 * 
	 * @param angle
	 * 
	 */
	public void setAngle(int angle){
		int I2C_Response = 0;
		this.setAddress(MSC.NXTSERVO_ADDRESS);
		int index = MSC_position - 1;
		I2C_Response = this.sendData((int)servoPosition[index], (byte)angle);
	}

}
