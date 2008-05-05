package lejos.nxt;

import lejos.nxt.*;
import java.util.ArrayList;

/**
 * 
 * Servo Controller manage until 10 RC Servos.
 * This class has been defined to manage the device
 * Lattebox Servo Controller
 * 
 * The physical design  is:
 * 
*  ****************************
*  * SERVO 08        SERVO 09 *
*  * SERVO 06  CHIP  SERVO 07 *
*  * SERVO 05  CHIP  SERVO 06 *
*  * SERVO 03  CHIP  SERVO 04 *
*  * SERVO 01  CHIP  SERVO 02 *
*  *                          *
*  * USB    DC   NXTe PINS    *
*  * USB    DC                *
*  ****************************
 *  
 * @author Juan Antonio Brenha Moral
 */

public class LSC extends I2CSensor {

	//Servo Management
	private final int MAXIMUM_SERVOS = 10;//LSC Suports until 10 RC Servos
	private ArrayList arrServo;//ServoController manage until 10 RC Servos
	public static final byte arrServoID[] = {0x01,0x02,0x04,0x08};
	
	//Exception handling
	private final String ERROR_SERVO_DEFINITION =  "Error with Servo definition";
	private final String ERROR_SERVO_LOCATION =  "Error with Servo location";
	
	//I2C
	private byte SPI_PORT;	
	private SensorPort portConnected;
	int I2C_Response;
	private byte[] bufReadResponse;
	private byte h_byte;
	private byte l_byte;
	
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
		
		bufReadResponse = new byte[8];
		
		arrServo = new ArrayList();
		
		this.setAddress((int) NXTe.NXTE_ADDRESS);
	}
	
	/**
	 * Method to add servo to current LSC
	 * 
	 * @param index
	 * @param name
	 * @throws Exception
	 *
	 */
	public void addServo(int location, String name) throws Exception{
		if(arrServo.size() <=MAXIMUM_SERVOS){
			LServo s = new LServo(this.portConnected,location, name,this.SPI_PORT);
			arrServo.add(s);
		}else{
			throw new Exception(ERROR_SERVO_DEFINITION);
		}
	}

	/**
	 * Method to get a Servo in a LSC
	 * 
	 * @param index
	 * @return
	 * 
	 */
	public LServo getServo(int index){
		return (LServo) this.arrServo.get(index);
	}

	/**
	 * Method to get a Servo in a LSC
	 * 
	 * @param index
	 * @return
	 * 
	 */
	public LServo Servo(int index){
		return (LServo) this.arrServo.get(index);
	}
	
	//I2C Methods
	
	/**
	 * This method check LSC connected with NXTe
	 * Currently I am debugging
	 * 
	 */
	public void calibrate() throws Exception{
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
	 * 
	 * @throws Exception
	 */
	public void loadAllServos()  throws Exception{
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
		int channel = (int)0x00;
		h_byte = (byte)0xe0; //0xe0 | (0x00 >>(byte)8); //?? 
		l_byte = (byte)channel;
	     
	    //High Byte Write
		I2C_Response = this.sendData((int)this.SPI_PORT, h_byte);

	    //Low Byte Write
		I2C_Response = this.sendData((int)this.SPI_PORT, l_byte);		
	}
	
	/**
	 * Load Servo located in a position X
	 * 
	 * @param location
	 */
	public void loadServo(int location) throws Exception{
		int channel = (int)0x00;
		
		if((location > 0) && (location <= 10)){
			if(location == 1){
				channel = (int) 0x01;
			}else if(location == 2){
				channel = (int) 0x02;
			}else if(location == 3){
				channel = (int) 0x04;
			}else if(location == 4){
				channel = (int) 0x08;
			}else if(location == 5){
				channel = (int) 0x10;
			}else if(location == 6){
				channel = (int) 0x20;
			}else if(location == 7){
				channel = (int) 0x40;
			}else if(location == 8){
				channel = (int) 0x80;
			}else if(location == 9){
				channel = (int) 0x100;
			}else if(location == 10){
				channel = (int) 0x200;
			}
		}else{
			throw new Exception(ERROR_SERVO_LOCATION);
		}
		
		h_byte = (byte)0xe0; //0xe0 | (0x00 >>(byte)8); //?? 
		l_byte = (byte)channel;
	     
	    //High Byte Write
		I2C_Response = this.sendData((int)this.SPI_PORT, h_byte);

	    //Low Byte Write
		I2C_Response = this.sendData((int)this.SPI_PORT, l_byte);		
	}
}
