package lejos.nxt.addon;
import lejos.nxt.*;
/**
* Supports Mindsensors NXTMMX. this device allows you to connect two 
* additional motors to your robot. Multiple NXTMMXs can be chained together.
* 
* @author Michael D. Smith mdsmitty@gmail.com
*
*/
public class NXTMMX extends I2CSensor{
	//registers
	private final int REG_MuxCommand = 0x41; //register to read input voltage
	private final int REG_KP_TACHO = 0x7A;
	private final int REG_KI_TACHO = 0x7C;
	private final int REG_KD_TACHO = 0x7E;
	private final int REG_KP_SPEED = 0x80;
	private final int REG_KI_SPEED = 0x82;
	private final int REG_KD_SPEED = 0x84;
	private final int REG_PID_PASS_COUNT = 0x86;
	private final int REG_TOLERANCE = 0x87;
	
	//commands
	private final int COMMAND_Reset = 'R';
	private final int COMMAND_StartMotors = 'S';
	private final int COMMAND_FloatMotors = 'c';
	private final int COMMAND_BreakMotors = 'C';
	
	private byte [] buffer = new byte[2];
	private boolean autoStart = true;

    public static final int DEFAULT_MMX_ADDRESS = 0x6;
	
	//motors
	public NXTMMXMotor A;
	public NXTMMXMotor B;
	
	/**
	 * Constructor for the NXTMMX
     * @param port - the port its plugged in to
     * @param address The I2C address for the device
	 */
	public  NXTMMX(I2CPort port, int address){
		super(port, address, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
		reset();
		A = new NXTMMXMotor(this, 0);
		B = new NXTMMXMotor(this, 1);
	}

	/**
	 * Constructor for the NXTMMX
	 * @param port - the port its plugged in to
	 */
	public  NXTMMX(I2CPort port){
        this(port, DEFAULT_MMX_ADDRESS);
	}

	/**
	 * resets mux values to default and stops all tasks. this includes zeroing the tachos.
	 */
	public void reset(){
		sendData(REG_MuxCommand, (byte) COMMAND_Reset);
	}
	
	/**
	 * Determines if motors will automatically start of not. by default they are on.
	 * @return true if on
	 */
	public boolean isAutoStart(){
		return autoStart;
	}
	
	/**
	 * turns autostart on or off. if you are going to start the motors
	 *  at same time turn autostart off and use the motor methods 
	 *  in the mux not the motors 
	 * @param autoStart
	 */
	public void setAutoStart(boolean autoStart){
		this.autoStart = autoStart;
	}
	
	/**
	 * Starts both motors at the same time. speed has to be set and 
	 * direction or tacho or time should be set on the motors.
	 */
	public void startMotors(){
		this.sendData(REG_MuxCommand, (byte) COMMAND_StartMotors);
	}
	
	/**
	 * floats both motors
	 */
	public void fltMotors(){
		this.sendData(REG_MuxCommand, (byte) COMMAND_FloatMotors);
	}
	
	/**
	 * breaks both motors
	 */
	public void breakMotors(){
		this.sendData(REG_MuxCommand, (byte) COMMAND_BreakMotors);
	}
	
	/**
	 * returns the voltage in mili amps
	 * @return
	 */
	public int getVoltage(){
		 getData(REG_MuxCommand, buffer, 1);
		 return (37*(0x00ff & buffer[0]));
	}
	
	//Proportional gain tacho
	public int getKpTacho(){
		getData(REG_KP_TACHO, buffer, 2);
		return byteArrayToInt(buffer);
	}
	
	public void setKpTacho(int kp){
		buffer = intToByteArray(kp);
		sendData(REG_KP_TACHO, buffer, 2);
	}
	
	//Integral gain tacho
	public int getKiTacho(){
		getData(REG_KI_TACHO, buffer, 2);
		return byteArrayToInt(buffer);
	}
	
	public void setKiTacho(int Ki){
		buffer = intToByteArray(Ki);
		sendData(REG_KI_TACHO, buffer, 2);
	}
	
	//Derivative gain tacho
	public int getKdTacho(){
		getData(REG_KD_TACHO, buffer, 2);
		return byteArrayToInt(buffer);
	}
	
	public void setKdTacho(int Kd){
		buffer = intToByteArray(Kd);
		sendData(REG_KD_TACHO, buffer, 2);		
	}
	
	//Proportional gain speed
	public int getKpSpeed(){
		getData(REG_KP_SPEED, buffer, 2);
		return byteArrayToInt(buffer);
	}
	
	public void setKpSpeed(int kp){
		buffer = intToByteArray(kp);
		sendData(REG_KP_SPEED, buffer, 2);
	}
	
	//Integral gain speed
	public int getKiSpeed(){
		getData(REG_KI_SPEED, buffer, 2);
		return byteArrayToInt(buffer);
	}
	
	public void setKiSpeed(int Ki){
		buffer = intToByteArray(Ki);
		sendData(REG_KI_SPEED, buffer, 2);
	}
	
	//Derivative gain speed
	public int getKdSpeed(){
		getData(REG_KD_SPEED, buffer, 2);
		return byteArrayToInt(buffer);
	}
	
	public void setKdSpeed(int Kd){
		buffer = intToByteArray(Kd);
		sendData(REG_KD_SPEED, buffer, 2);		
	}
	
	public byte getPassCount(){
		getData(REG_PID_PASS_COUNT, buffer, 1);
		return buffer[0];
	}
	
	public void setPassCount(byte count){
		sendData(REG_PID_PASS_COUNT, count);		
	}
	
	public byte getTolerance(){
		getData(REG_TOLERANCE, buffer, 1);
		return buffer[0];
	}
	
	public void setTolerance(byte count){
		sendData(REG_TOLERANCE, count);		
	}
	
	private byte[] intToByteArray(int value) {
        return new byte[] {
        	(byte)(value),
        	(byte)(value >>> 8)} ;
	}
	
	private int byteArrayToInt( byte[] buffer){
		return ((buffer[1] & 0xFF) << 8)
        	+ (buffer[0] & 0xFF); 
	}
}
