package lejos.nxt.addon;

import lejos.nxt.*;

/**
 * MServo, is a abstraction to model any RC Servo (continous and non continous)  plugged to
 * 
 * @author Juan Antonio Brenha Moral
 */
public class MServo extends I2CSensor{
	private SensorPort portConnected;//Where is plugged in NXT Brick
	private String name = "";//String to describe any Motor connected to LSC
	private int servoPosition = 0; //Position where Servo has been plugged
	private final byte servoPositions[] = {0x5A,0x5B,0x5C,0x5D,0x5E,0x5F,0x60,0x61};//The place where RC Servo has been plugged
	private final byte servoSpeeds[] = {0x52,0x53,0x54,0x55,0x56,0x57,0x58,0x59};

	//Default Values
	private int angle = 0;
	private int pulse = 0;
	private int minAngle = 0;//Degree
	private int maxAngle = 180;//Degrees
	private int minPulse = 500;//Ms
	private int maxPulse = 2500;//Ms

	/**
	 *
	 * The initial Constructor.
	 * This constructor establish where is plugged NXTServo on NXT Brick, 
	 * where the RC Servo is plugged into NXTServo
	 *
	 * @param port
	 * @param location
	 * @param servoName
	 *
	 */
	public MServo(SensorPort port, int location, String servoName){
		super(port);
		this.name = servoName;
		this.servoPosition = location;
	}
	
	/**
	 *
	 * Constructor with the feature to set min, max and init angle
	 *
	 * @param port
	 * @param location
	 * @param servoName
	 * @param min_angle
	 * @param max_angle
	 *
	 */
	public MServo(SensorPort port, int location, String servoName, int min_angle, int max_angle){
		this(port,location,servoName);
		
		this.minAngle = min_angle;
		this.maxAngle = max_angle;
	}

	/*
	 * Used to make a Lineal Interpolation
	 * 
	 * From the HP Calculator idea:
	 * http://h10025.www1.hp.com/ewfrf/wc/fastFaqLiteDocument?lc=es&cc=mx&docname=bsia5214&dlc=es&product=20037
	 *
	 */
	private float getLinealInterpolation(int x,int x1, int x2, int y1, int y2){
		float y;
		y = ((y2-y1)/(x2-x1))*(x-x1) + y1;
		
		return y;
	}

	/**
	 * This method set the pulse in a RC Servo.
	 * 
	 * Note:Pulse range is: 500-2500, but internally
	 * it is necessary to divide into 2
	 * 
	 * @param pulse
	 * 
	 */
	public void setPulse(int pulse){
		this.pulse = pulse;
		int internalPulse = Math.round(pulse/10);
		int I2C_Response = 0;
		this.setAddress(MSC.NXTSERVO_ADDRESS);
		int index = servoPosition - 1;
		I2C_Response = this.sendData((int)servoPositions[index], (byte)internalPulse);
	}
	
	/**
	 * Return the pulse used in last operation
	 * 
	 * @return
	 *
	 */
	public int getPulse(){
		return pulse;
	}

	/**
	 * Method to set an Angle in a RC Servo. 
	 * 
	 * @param angle
	 * 
	 */
	public void setAngle(int angle){
		this.angle = angle;
		this.pulse = Math.round(getLinealInterpolation(angle,minAngle,maxAngle,minPulse,maxPulse));
		this.setPulse(pulse);
	}

	/**
	 * Return the angle used in last operation
	 * 
	 * @return
	 *
	 */
	public int getAngle(){
		return angle;
	}

	
	/**
	 * Method to set the Speed in a RC Servo. 
	 * 
	 * @param angle
	 * 
	 */
	public void setSpeed(int speed){
		int I2C_Response = 0;
		this.setAddress(MSC.NXTSERVO_ADDRESS);
		int index = servoPosition - 1;
		I2C_Response = this.sendData((int)servoPositions[index], (byte)speed);
	}
}
