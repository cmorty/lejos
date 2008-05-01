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
 *  * SERVO 01        SERVO 06 *
 *  * SERVO 02  CHIP  SERVO 07 *
 *  * SERVO 03  CHIP  SERVO 08 *
 *  * SERVO 04  CHIP  SERVO 09 *
 *  * SERVO 05  CHIP  SERVO 10 *
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
	 * @author Juan Antonio Brenha Moral 
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
	 * @author Juan Antonio Brenha Moral
	 */
	public void addServo(int index, String name) throws Exception{
		if(arrServo.size() <=MAXIMUM_SERVOS){
			LServo s = new LServo(this.portConnected,index, name,this.SPI_PORT);
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
	 * @author Juan Antonio Brenha Moral
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
	 * @author Juan Antonio Brenha Moral
	 */
	public LServo Servo(int index){
		return (LServo) this.arrServo.get(index);
	}
	
	//I2C Methods
	
	/**
	 * This method check LSC connected with NXTe
	 * Currently I am debugging
	 * 
	 * @author JAB
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
	 * 
	 * @author Juan Antonio Brenha Moral
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
}
