package lejos.nxt;
//import lejos.nxt.*;

/**
 * LDCMotor, Lattebox DC Motor, is a abstraction to model any DCMotor connected to
 * LSC, Lattebox Servo Controller. 
 * 
 * @author Juan Antonio Brenha Moral
 */
public class LDCMotor extends LMotor{

	private int speed;
	private int min_speed = 1020;
	private int max_speed = 700;

	/**
	 * Constructor
	 * 
	 * @param port
	 * @param location
	 * @param DCMotorName
	 * @param SPI_PORT
	 *  
	 */
	public LDCMotor(SensorPort port, int location, String DCMotorName, byte SPI_PORT){
		super(port,location,DCMotorName,SPI_PORT);
	}

	public LDCMotor(SensorPort port, int location, String DCMotorName, byte SPI_PORT,int min_speed,int max_speed){
		super(port,location,DCMotorName,SPI_PORT);
		
		this.min_speed = min_speed;
		this.max_speed = max_speed;
	}	
	
	/**
	 * Method to set the speed in a DC Motor 
	 * 
	 * @param angle
	 * 
	 */
	public void setSpeed(int speed){
		int I2C_Response;
		byte h_byte;
		byte l_byte;
		
		int DCMotor = LSC_position;
		h_byte = (byte)(0x80 | ((DCMotor<<3) | (speed >>8)));
	    l_byte = (byte)speed;
		
	    //High Byte Write
		I2C_Response = this.sendData((int)this.SPI_PORT, h_byte);

	    //Low Byte Write
		I2C_Response = this.sendData((int)this.SPI_PORT, l_byte);
	}
	
	/**
	 * 
	 * Method to get speed from the DC Motor
	 *
	 * @return
	 * 
	 */
	public int getSpeed(){
		int I2C_Response;
		byte[] bufReadResponse;
		bufReadResponse = new byte[8];
		byte h_byte;
		byte l_byte;		
		
		int DCMotor = LSC_position;
	    //Write OP Code
	    h_byte  = (byte)(DCMotor << 3);
		I2C_Response = this.sendData((int)this.SPI_PORT, h_byte);
		
	    //Read High Byte
	    //I2CBytes(IN_3, bufReadValue, buflen, bufReadResponse);
		I2C_Response = this.sendData((int)this.SPI_PORT, (byte)0x00);		
		I2C_Response = this.getData((int)this.SPI_PORT, bufReadResponse, 1);
		
	    h_byte = bufReadResponse[0];
	    
	    //Read Low Byte
		I2C_Response = this.sendData((int)this.SPI_PORT, (byte)0x00);
		I2C_Response = this.getData((int)this.SPI_PORT, bufReadResponse, 1);
	    l_byte = bufReadResponse[0];
	    
	    return  ((h_byte & 0x07 ) << 8) +  (l_byte & 0x00000000FF);
	}	
	
	public void setMinSpeed(int min_speed){
		this.min_speed = min_speed;
	}

	public void setMaxSpeed(int max_speed){
		this.max_speed = max_speed;
	}	
	
}
