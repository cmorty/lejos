package lejos.nxt;
//import lejos.nxt.*;

import java.util.ArrayList;

/**
 * 
 * This class has been defined to manage the device
 * LSC, Lattebox Servo Controller which
 * manage until 10 RC Servos / DC Motors
 * 
 * @author Juan Antonio Brenha Moral
 */

public class LSC extends I2CSensor {

	//Servo Management
	private ArrayList arrServo;//ServoController manage until 10 RC Servos
	private ArrayList arrDCMotor;//ServoController manage until 10 DC Motors
	private final int MAXIMUM_SERVOS_DCMOTORS = 10;//LSC Suports until 10 RC Servos
	
	//Exception handling
	private final String ERROR_SERVO_DEFINITION =  "Error with Servo definition";
	private final String ERROR_SERVO_LOCATION =  "Error with Servo location";
	
	//I2C
	private byte SPI_PORT;	
	private SensorPort portConnected;
	
	/**
	 * 
	 * Constructor
	 * 
	 * @param port
	 * @param SPI_PORT
	 * 
	 */
	public LSC(SensorPort port,byte SPI_PORT){
		super(port);
		this.portConnected = port;
		this.SPI_PORT = SPI_PORT;
		
		arrServo = new ArrayList();
		arrDCMotor = new ArrayList();
		
		this.setAddress((int) NXTe.NXTE_ADDRESS);
	}
	
	/**
	 * Method to add  a RC servo to current LSC
	 * 
	 * @param index
	 * @param name
	 * @throws ArrayIndexOutOfBoundsException
	 *
	 */
	public void addServo(int location, String name) throws ArrayIndexOutOfBoundsException{
		if(arrServo.size() <=MAXIMUM_SERVOS_DCMOTORS){
			LServo s = new LServo(this.portConnected,location, name,this.SPI_PORT);
			arrServo.add(s);
		}else{
			//throw new ArrayIndexOutOfBoundsException(ERROR_SERVO_DEFINITION);
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	/**
	 * Method to add  a RC servo to current LSC
	 * 
	 * @param index
	 * @param name
	 * @throws ArrayIndexOutOfBoundsException
	 *
	 */
	public void addServo(int location, String name,int min_angle, int max_angle) throws ArrayIndexOutOfBoundsException{
		if(arrServo.size() <=MAXIMUM_SERVOS_DCMOTORS){
			LServo s = new LServo(this.portConnected,location, name,this.SPI_PORT,min_angle,max_angle);
			arrServo.add(s);
		}else{
			//throw new ArrayIndexOutOfBoundsException(ERROR_SERVO_DEFINITION);
			throw new ArrayIndexOutOfBoundsException();
		}
	}	
	
	/**
	 * Method to get a rRC Servo in a LSC
	 * 
	 * @param index
	 * @return
	 * 
	 */
	public LServo getServo(int index){
		return (LServo) this.arrServo.get(index);
	}	 

	/**
	* Method to add a DC Motor
	*
	* @param location
	* @param name
	*
	*/	
	public void addDCMotor(int location, String name) throws ArrayIndexOutOfBoundsException{
		if(arrDCMotor.size() <=MAXIMUM_SERVOS_DCMOTORS){
			LDCMotor dcm = new LDCMotor(this.portConnected,location, name,this.SPI_PORT);
			arrDCMotor.add(dcm);
		}else{
			//throw new ArrayIndexOutOfBoundsException(ERROR_SERVO_DEFINITION);
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	/**
	* Method to add a DC Motor
	*
	* @param location
	* @param name
	*
	*/	
	public void addDCMotor(int location, String name,int min_speed,int max_speed) throws ArrayIndexOutOfBoundsException{
		if(arrDCMotor.size() <=MAXIMUM_SERVOS_DCMOTORS){
			LDCMotor dcm = new LDCMotor(this.portConnected,location, name,this.SPI_PORT,min_speed,max_speed);
			arrDCMotor.add(dcm);
		}else{
			//throw new ArrayIndexOutOfBoundsException(ERROR_SERVO_DEFINITION);
			throw new ArrayIndexOutOfBoundsException();
		}
	}	
	
	/**
	 * Method to get a rRC Servo in a LSC
	 * 
	 * @param index
	 * @return
	 * 
	 */
	public LDCMotor getDCMotor(int index){
		return (LDCMotor) this.arrDCMotor.get(index);
	}
	
	//I2C Methods
	
	/**
	 * This method check LSC connected with NXTe
	 * Currently I am debugging
	 * 
	 */
	public void calibrate(){
		int I2C_Response;
		byte[] bufReadResponse;
		bufReadResponse = new byte[8];
		byte h_byte;
		byte l_byte;		
		
		I2C_Response = this.sendData((int)this.SPI_PORT, (byte)0x00);
		I2C_Response = this.getData((int)this.SPI_PORT, bufReadResponse, 1);
		
		while(bufReadResponse[0] != 99){
			I2C_Response = this.sendData((int)this.SPI_PORT, (byte)0xFF);
			I2C_Response = this.sendData((int)this.SPI_PORT, (byte)0xFF);
			I2C_Response = this.sendData((int)this.SPI_PORT, (byte)0x7E);			

			I2C_Response = this.sendData((int)this.SPI_PORT, (byte)0x00);
			I2C_Response = this.getData((int)this.SPI_PORT, bufReadResponse, 1);
			
			if((int)bufReadResponse[0] == 99){
				break;
			}
		}
	}
	
	/**
	 * Load all servos connected this this LSC 
	 */
	public void loadAllServos(){
		int I2C_Response;
		byte h_byte;
		byte l_byte;		
		
		int channel = 1023;
		h_byte = (byte)0xe0; //0xe0 | (0x00 >>(byte)8); //?? 
		l_byte = (byte)channel;
	     
	    //High Byte Write
		I2C_Response = this.sendData((int)this.SPI_PORT, h_byte);

	    //Low Byte Write
		I2C_Response = this.sendData((int)this.SPI_PORT, l_byte);
	}
	
	/**
	 * Unload all servos connected in a LSC
	 */
	public void unloadAllServos(){
		int I2C_Response;
		byte h_byte;
		byte l_byte;
		
		int channel = (int)0x00;
		h_byte = (byte)0xe0; //0xe0 | (0x00 >>(byte)8); //?? 
		l_byte = (byte)channel;
	     
	    //High Byte Write
		I2C_Response = this.sendData((int)this.SPI_PORT, h_byte);

	    //Low Byte Write
		I2C_Response = this.sendData((int)this.SPI_PORT, l_byte);		
	}
}
