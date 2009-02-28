/**
 * Motor class for PFMate class
 * 
 * @author Michael Smith <mdsmitty@gmail.com>
 **/

package lejos.nxt.addon;

public class PFMateMotor{
	private PFMate recever;
	private int operReg, speedReg;
	private byte [] buffer = new byte[1];
	private final static byte FLT = 0, FORWARD = 1, BACKWARD = 2, STOP = 3;

	/**
	 * @param recever PFMate object reference
	 * @param operReg Motor register
	 * @param speedReg Speed Register
	 */
	PFMateMotor(PFMate recever, int operReg, int speedReg){
		this.recever = recever;
		this.operReg = operReg;
		this.speedReg = speedReg;
	}
	
	//motor operations
	/**
	 * Floats the motor
	 */
	public void flt(){
		recever.sendData(operReg, FLT);
	}
	
	/**
	 * Runs the motor forward
	 *
	 */
	public void forward(){
		recever.sendData(operReg, FORWARD);
	}
	
	/**
	 * Runs the motor backward
	 *
	 */
	public void backward(){
		recever.sendData(operReg, BACKWARD);
	}
	
	/**
	 * Stops the Motor
	 *
	 */
	public void stop(){
		recever.sendData(operReg, STOP);
	}

	/**
	 * Sets the motors speed
	 * @param speed 1 = 7
	 */
	public void setSpeed(int speed){
		if(speed < 1) speed = 1;
		if (speed > 7) speed = 7;
		recever.sendData(speedReg, (byte) speed);
	}
	
	/**
	 * returns the speed
	 * @return 1 - 7
	 */
	public int getSpeed(){
		recever.getData(speedReg, buffer, 1);
		return buffer[0];
	}
	
	/**
	 * Determines if motor is floating this is based on what the receiver has in its registers
	 * @return boolean
	 */
	public boolean isFlt(){
		recever.getData(operReg, buffer, 1);
		if(buffer[0]== FLT) return true;
		return false;
	}
	
	/**
	 * Determines if motor is moving forward this is based on what the receiver has in its registers
	 * @return boolean
	 */
	public boolean isForward(){
		recever.getData(operReg, buffer, 1);
		if(buffer[0]== FORWARD) return true;
		return false;
	}
	
	/**
	 * Determines if motor is moving backwards this is based on what the receiver has in its registers
	 * @return boolean
	 */
	public boolean isBackward(){
		recever.getData(operReg, buffer, 1);
		if(buffer[0]== BACKWARD) return true;
		return false;
	}
	
	/**
	 * Determines if motor is stopped this is based on what the receiver has in its registers
	 * @return boolean
	 */
	public boolean isStop(){
		recever.getData(operReg, buffer, 1);
		if(buffer[0]== STOP) return true;
		return false;
	}
}
