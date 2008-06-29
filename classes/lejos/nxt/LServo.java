package lejos.nxt;
//import lejos.nxt.*;

/**
 * LServo, Lattebox Servo, is a abstraction to model any RC Servo (continous and non continous)  plugged to
 * LSC, Lattebox Servo Controller. 
 * 
 * @author Juan Antonio Brenha Moral
 */
public class LServo extends LMotor{
	private int angle;
	private int min_angle = 0;
	private int max_angle = 2000;
	private int init_angle = 1000;
	
	/**
	 * Constructor
	 * 
	 * @param port
	 * @param location
	 * @param servoName
	 * @param SPI_PORT
	 *  
	 */
	public LServo(SensorPort port, int location, String servoName, byte SPI_PORT){
		super(port,location,servoName,SPI_PORT);
	}

	/**
	 *
	 * Constructor with the feature to set min and max angle
	 *
	 * @param port
	 * @param location
	 * @param servoName
	 * @param SPI_PORT
	 * @param min_angle
	 * @param max_angle
	 *
	 */
	public LServo(SensorPort port, int location, String servoName, byte SPI_PORT,int min_angle, int max_angle){
		super(port,location,servoName,SPI_PORT);
		
		this.min_angle = min_angle;
		this.max_angle = max_angle;		
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
	public LServo(SensorPort port, int location, String servoName, byte SPI_PORT,int min_angle, int max_angle,int init_angle){
		super(port,location,servoName,SPI_PORT);
		
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
		int I2C_Response;
		byte h_byte;
		byte l_byte;
		
		int servo = LSC_position;
		h_byte = (byte)(0x80 | ((servo<<3) | (angle >>8)));
	    l_byte = (byte)angle;
		
	    //High Byte Write
		I2C_Response = this.sendData((int)this.SPI_PORT, h_byte);

	    //Low Byte Write
		I2C_Response = this.sendData((int)this.SPI_PORT, l_byte);
	}
	
	/**
	 * 
	 * Method to know the angle
	 *
	 * @return
	 *
	 */
	public int getAngle(){
		int I2C_Response;
		byte[] bufReadResponse;
		bufReadResponse = new byte[8];
		byte h_byte;
		byte l_byte;		
		
		int servo = LSC_position;
	    //Write OP Code
	    h_byte  = (byte)(servo << 3);
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

	/**
	 * Set Minimal angle. Useful method to calibrate a Servo
	 * 
	 * @param minAngle
	 * 
	 */
	public void setMinAngle(int minAngle){
		this.min_angle = minAngle;
	}

	/**
	 * Set Maximum angle. Useful method to calibrate a Servo
	 * 
	 * @param maxAngle
	 * 
	 */	
	public void setMaxAngle(int maxAngle){
		this.max_angle = maxAngle;
	}	
	
	/**
	 * Method to set minimal angle
	 *  
	 */	
	public void goToMinAngle(){
		this.setAngle(this.min_angle);
	}

	/**
	 * Method to set maximum angle
	 * 
	 */	
	public void goToMaxAngle(){
		this.setAngle(this.max_angle);		
	}

	/**
	 * Method to set medium angle
	 * 
	 */		
	public void goToMiddleAngle(){
		float middle = (this.min_angle + this.max_angle) / 2;
		
		this.setAngle(Math.round(middle));		
	}

	/**
	 * Method to set medium angle
	 * 
	 */		
	public void goToInitAngle(){
		this.setAngle(this.init_angle);		
	}
	
	/**
	 * Classic forward method for continous RC Servos
	 * 
	 */
	public void forward(){
		this.setAngle(0);
	}

	/**
	 * Classic backward method for continous RC Servos
	 * 
	 */
	public void backward(){
		this.setAngle(2000);
	}	
}
