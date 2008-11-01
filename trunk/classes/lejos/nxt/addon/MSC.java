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
	private ArrayList arrServo;//ServoController manage until 10 RC Servos
	private final int MAXIMUM_SERVOS_DCMOTORS = 8;//MSC Suports until 10 RC Servos
	
	//Exception handling
	private final String ERROR_SERVO_DEFINITION =  "Error with Servo definition";
	private final String ERROR_SERVO_LOCATION =  "Error with Servo location";
	
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
		this.setAddress(MSC.NXTSERVO_ADDRESS);
		
		this.portConnected = port;
		arrServo = new ArrayList();
	}
	
	/**
	 * Method to add  a RC servo to current LSC
	 * 
	 * @param location the locatoion
	 * @param name of the servo
	 * @throws ArrayIndexOutOfBoundsException
	 *
	 */
	public void addServo(int location, String name) throws ArrayIndexOutOfBoundsException{
		if(arrServo.size() <=MAXIMUM_SERVOS_DCMOTORS){
			MServo s = new MServo(this.portConnected,location, name);
			arrServo.add(s);
		}else{
			//throw new ArrayIndexOutOfBoundsException(ERROR_SERVO_DEFINITION);
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	
	/**
	 * Method to get an RC Servo in a LSC
	 * 
	 * @param index in the array
	 * @return the MServo object
	 * 
	 */
	public MServo getServo(int index){
		return (MServo) this.arrServo.get(index);
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
