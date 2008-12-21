package lejos.nxt.addon;

import lejos.nxt.*;
import java.util.ArrayList;

/**
 * 
 * This class has been designed to manage the device
 * MSC8, Mindsensors NXT Servo which
 * manage until 8 RC Servos
 * 
 * Many thanks to Luis Bunuel (bunuel66@hotmail.com) in Testing process 
 * 
 * @author Juan Antonio Brenha Moral
 */
public class MSC extends I2CSensor {

	//Servo Management
	public static MServo servo1;
	public static MServo servo2;
	public static MServo servo3;
	public static MServo servo4;
	public static MServo servo5;
	public static MServo servo6;
	public static MServo servo7;
	public static MServo servo8;
	private MServo[] arrServo;//ServoController manage until 10 RC Servos

	//I2C	
	private SensorPort portConnected;
	public static final byte NXTSERVO_ADDRESS = (byte)0x58;

	/**
	 * 
	 * Constructor
	 * 
	 * @param port
	 * 
	 */
	public MSC(SensorPort port){
		super(port);
		port.setType(TYPE_LOWSPEED_9V);
		this.setAddress(NXTSERVO_ADDRESS);
		
		this.portConnected = port;
		//arrServo = new ArrayList();
		
		servo1 = new MServo(this.portConnected,1);
		servo2 = new MServo(this.portConnected,2);
		servo3 = new MServo(this.portConnected,3);
		servo4 = new MServo(this.portConnected,4);
		servo5 = new MServo(this.portConnected,5);
		servo6 = new MServo(this.portConnected,6);
		servo7 = new MServo(this.portConnected,7);
		servo8 = new MServo(this.portConnected,8);

		arrServo = new MServo[8];
		arrServo[0] = servo1;
		arrServo[1] = servo2;
		arrServo[2] = servo3;
		arrServo[3] = servo4;
		arrServo[4] = servo5;
		arrServo[5] = servo6;
		arrServo[6] = servo7;
		arrServo[7] = servo8;
	}

	/**
	 * Method to get an RC Servo in from NXTServo
	 * 
	 * @param index in the array
	 * @return the MServo object
	 * 
	 */
	public MServo getServo(int index){
		return (MServo) this.arrServo[index-1];
	}

	
	/**
	 * Read the battery voltage data from
	 * NXTServo module (in milli-volts)
	 * 
	 * @return the battery voltage in millivolts
	 */
	public int getBattery(){
		int I2C_Response = 0;
		byte[] bufReadResponse;
		bufReadResponse = new byte[8];
		byte kSc8_Vbatt = 0x41;//The I2C Register to read the battery 

		I2C_Response = this.getData(kSc8_Vbatt, bufReadResponse, 1);

		// 37 is calculated fromsupply from NXT =4700 mv /128
		return(37*(0x00FF & bufReadResponse[0]));
	}
}
