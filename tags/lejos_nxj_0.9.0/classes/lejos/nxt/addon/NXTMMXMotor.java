package lejos.nxt.addon;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Supports Mindsensors NXTMMX. this device allows you to connect two 
 * additional motors to your robot. Multiple NXTMMXs can be chained together.
 * 
 * @author Michael D. Smith mdsmitty@gmail.com
 *
 */
public class NXTMMXMotor {

	private NXTMMX mux;
	private boolean rampUp = true;
	private boolean controlSpeed = true;
	private boolean tachoLock = false;
	private boolean tachoBreak = true;
	private int command = 0;
	private byte[] buffer = new byte[4];

	//bits for the command register 0-7   
	private final int CONTROL_SPEED =       0x01; //0
	private final int CONTROL_RAMP =        0x02; //1
	private final int CONTROL_RELATIVE =    0x04; //2
	private final int CONTROL_TACHO =       0x08; //3
	private final int CONTROL_TACHO_BREAK = 0x10; //4
	private final int CONTROL_TACHO_LOCK =  0x20; //5
	private final int CONTROL_TIME =        0x40; //6
	private final int CONTROL_GO =          0x80; //7
	
	//bits for status register 0-7
	private final int STATUS_SPEED =        0x01; //0
	private final int STATUS_RAMP =         0x02; //1
	private final int STATUS_POWERED =      0x04; //2
	private final int STATUS_POSIONAL =     0x08; //3
	private final int STATUS_BREAK =        0x10; //4
	private final int STATUS_OVERLOAD =     0x20; //5
	private final int STATUS_TIME =         0x40; //6
	private final int STATUS_STALL =        0x80; //7
	
	//motor registers                     A         B
	private int REG_RotateTo =           0x42;//   0x4A
	private int REG_MotorSpeed =         0x46;//   0x4e
	private int REG_MotorRunTime =       0x47;//   0x4F
	private int REG_CommandB =           0x48;//   0x50
	private int REG_CommandA =           0x49;//   0x51
	private int REG_TacPos =             0x62;//   0x66
	private int REG_Status =             0x72;//   0x73 
	private int REG_Tasks =              0x76;//   0x77
	
	//Commands for register 0x41
	private int COMMAND_ResetTaco =      0x72;//   0x73 
	private int COMMAND_Stop =           'A';//    'B'
	private int COMMAND_Float =          'a';//    'b' 
	
	//mux registers
	private int REG_MUX_Command =        0x41;
	
	/**
	 * Constructor, you don't have to worry about this its called two times by the constructor
	 * in the NXTMMX class to create each motor instance. 
	 * @param mux the motor multiplexor
	 * @param motor the index of the motor
	 */
	public NXTMMXMotor (NXTMMX mux, int motor){
		this.mux = mux;

		REG_RotateTo = REG_RotateTo + (motor * 8);
		REG_MotorSpeed = REG_MotorSpeed + (motor * 8);
		REG_MotorRunTime = REG_MotorRunTime + (motor * 8);
		REG_CommandB = REG_CommandB + (motor * 8);
		REG_CommandA = REG_CommandA + (motor * 8);
		REG_TacPos = REG_TacPos + (motor * 4);
		REG_Status = REG_Status + motor;
		REG_Tasks = REG_Tasks + motor;
		
		COMMAND_ResetTaco = COMMAND_ResetTaco + motor;
		COMMAND_Stop = COMMAND_Stop + motor;
		COMMAND_Float = COMMAND_Float + motor;
		
	}
	
	/**
	 * Causes motor to rotate backwards.
	 */
	public void backward() {
		int speed;
		command = 0;
		command |= CONTROL_SPEED;
		if(rampUp) command |= CONTROL_RAMP; //1
		if(mux.isAutoStart())command |= CONTROL_GO;
		speed = getSpeedInternal();
		if(speed > 0)speed = speed * -1;

		mux.sendData(REG_MotorSpeed, (byte) speed);
		mux.sendData(REG_CommandA, (byte) command);
		

	}

	/**
	 * Causes motor to float.
	 */
	public void flt() {
		mux.sendData(REG_MUX_Command, (byte) COMMAND_Float);
	}

	/**
	 * Causes motor to rotate forward. TODO 
	 */
	public void forward() {
		int speed;
		command = 0;
		command |= CONTROL_SPEED;
		if(rampUp) command |= CONTROL_RAMP; //1
		if(mux.isAutoStart())command |= CONTROL_GO;
		
		speed = getSpeedInternal();
		if(speed < 0)speed = speed * -1;

		mux.sendData(REG_MotorSpeed, (byte) speed);
		mux.sendData(REG_CommandA, (byte) command);

	}
	
	/**
	 * Return the angle that a Motor is rotating to.
	 * @return the limit angle
	 */
	public int getLimitAngle(){
		mux.getData(REG_RotateTo, buffer, 4);
		return byteArrayToInt(buffer);
	}
	
	/**
	 * Returns the mode
	 * @return the mode
	 * : 1 = forward, 2= backward, 3 = stop, 4 = float
	 */
	public int getMode(){
		int status = getStatus();
		if((status & STATUS_POWERED) == 1 && ((status & STATUS_POSIONAL) == 1||(status & STATUS_TIME) == 1)){
			if(getSpeedInternal()< 0) return 2; //backward
			return 1; //forward
		}
		if((status & STATUS_BREAK) == 1)return 3; //break
		return 4; //float
	}
	
	/**
	 * Determine whether speed control is on or not
	 * @return true if speed control is on, else false
	 */
	public boolean isRegulating(){
		return controlSpeed;
	}
	
	
	/**
	 * Returns true if the motor is in motion.
	 * @return true if the motor is currently in motion, else false
	 */
	public boolean isMoving() {
		int status;
		status = getStatus();
		if((status & STATUS_STALL) == 1) return false; //stalled
		if((status & STATUS_POWERED) == 1 && (status & STATUS_POSIONAL) == 1) return true;
		if((status & STATUS_POWERED) == 1 && (status & STATUS_TIME) == 1) return true;
		if((status & STATUS_POWERED) == 1 && (status & STATUS_SPEED) == 1) return true;
		return false;// not moving
	}
	
	/**
	 * Determines if the motor is stalled or not
	 * @return true if stalled, else false
	 */
	
	public boolean isStalled(){
		int status = getStatus();
		if((status & STATUS_STALL) == 1) return true;
		return false;
	}
	
	/**
	 * Determines if the motor stalled due to over load.
	 * @return true if overload, else false
	 */
	public boolean isOverloaded(){
		int status = getStatus();
		if((status & STATUS_OVERLOAD) == 1) return true;
		return false;
	}
	
	/**
	 * Determines if the motor is running a task. this does not mean the motor is moving.
	 * @return true if the motor is running a task, else false
	 */
	public boolean isTaskRunning(){
		mux.getData(REG_Tasks, buffer, 1);
		if(buffer[0] == 0) return false;
		return true;
	}
	
	/** 
	 * stops the motor
	 * 
	 */
	public void stop() {
		mux.sendData(REG_MUX_Command, (byte) COMMAND_Stop);
	}

	/**
	 * Returns the tachometer count.
	 * @return tachometer count in degrees
	 */
	public int getTachoCount() {
		int output = 0;
		mux.getData(REG_TacPos, buffer, 4);
		output = byteArrayToInt(buffer);       

		return output;
	}

	/**
	 * Resets the tachometer count to zero. 
	 */
	public void resetTachoCount() {
		mux.sendData(REG_MUX_Command, (byte) COMMAND_ResetTaco);
	}

	/**
	 * same as get speed I need to work on this still
	 */
	public int getRotationSpeed() {
		
		return getSpeed();
	}

	private int getSpeedInternal() {
		mux.getData(REG_MotorSpeed, buffer, 1);
		return buffer[0];
	}
	
	/**
	 * return the set speed of the motor.
	 * @return 0 - 100 has nothing to do with rpm or rps
	 */
	public int getSpeed() {
		int speed;
		speed = getSpeedInternal();
		if(speed > -1)return speed;
		return speed * -1;
	}

	
	/**
	 * turns speed regulation on and off for tacho and time methods. forwards
	 *  and backwards are not affected because if its off they just wont work.
	 * 
	 */
	public void regulateSpeed(boolean controlSpeed) {
		this.controlSpeed = controlSpeed;

	}

	/**
	 * Causes motor to rotate by a specified angle. The resulting tachometer count 
	 * should be within +- 2 degrees on the NXT. This method does not return until 
	 * the rotation is completed. 
	 * 
	 * @param deg - by which the motor will rotate.
	 */
	public void rotate(int deg) {
		command = 0;
		int status = 0;
		if(controlSpeed) command |= CONTROL_SPEED; //0
		if(rampUp) command |= CONTROL_RAMP; //1
		command |= CONTROL_RELATIVE; //2
		command |= CONTROL_TACHO; //3
		if(tachoBreak) command |= CONTROL_TACHO_BREAK; //4
		if(tachoLock) command |= CONTROL_TACHO_LOCK; //5
		command |= CONTROL_GO; // 7
		
		buffer = intToByteArray(deg);
		mux.sendData(REG_RotateTo, buffer, 4);
		mux.sendData(REG_CommandA, (byte) command);
		while(true){
			status = getStatus();
			if((status & STATUS_POSIONAL) == 0)return;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}

	/**
	 * same as rotate but returns immediately
	 * 
	 * @param deg - number of deg to rotate.
	 * @param immediateReturn - does not matter if true or false it will return after mux is programmed.
	 */
	public void rotate(int deg, boolean immediateReturn) {
		command = 0;
		if(controlSpeed) command |= CONTROL_SPEED; //0
		if(rampUp) command |= CONTROL_RAMP; //1
		command |= CONTROL_RELATIVE; //2
		command |= CONTROL_TACHO; //3
		if(tachoBreak) command |= CONTROL_TACHO_BREAK; //4
		if(tachoLock) command |= CONTROL_TACHO_LOCK; //5
		if(mux.isAutoStart())command |= CONTROL_GO; // 7
		
		buffer = intToByteArray(deg);
		mux.sendData(REG_RotateTo, buffer, 4);
		mux.sendData(REG_CommandA, (byte) command);
		return;
	}

	/**
	 * rotates to the tacho position indicated.
	 * @param deg - the tacho position that you want.
	 */
	public void rotateTo(int deg) {
		int status = 0;
		command = 0;
		if(controlSpeed) command |= CONTROL_SPEED; //0
		if(rampUp) command |= CONTROL_RAMP; //1
		command |= CONTROL_TACHO; //3
		if(tachoBreak) command |= CONTROL_TACHO_BREAK; //4
		if(tachoLock) command |= CONTROL_TACHO_LOCK; //5
		command |= CONTROL_GO; // 7
		
		buffer = intToByteArray(deg);
		mux.sendData(REG_RotateTo, buffer, 4);
		mux.sendData(REG_CommandA, (byte) command);
		while(true){
			status = getStatus();
			if((status & STATUS_POSIONAL) == 0)return;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}

	/**
	 * rotates to the tacho position that you want and immediately returns.
	 */
	public void rotateTo(int deg, boolean immediateReturn) {
		command = 0;
		if(controlSpeed) command |= CONTROL_SPEED; //0
		if(rampUp) command |= CONTROL_RAMP; //1
		command |= CONTROL_TACHO; //3
		if(tachoBreak) command |= CONTROL_TACHO_BREAK; //4
		if(tachoLock) command |= CONTROL_TACHO_LOCK; //5
		if(mux.isAutoStart())command |= CONTROL_GO; // 7

		buffer = intToByteArray(deg);
		mux.sendData(REG_RotateTo, buffer, 4);
		mux.sendData(REG_CommandA, (byte) command);
		return;
	}
	
	/**
	 * Rotates the motor for specified amount of time
	 * @param time 1 - 255 sec
	 * @param direction true is forward false is backward.
	 */
	public void rotateTime(int time, boolean direction){
		command = 0;
		int status, speed;
		if(controlSpeed) command |= CONTROL_SPEED; //0
		if(rampUp) command |= CONTROL_RAMP; //1
		command |= CONTROL_TIME;//6
		command |= CONTROL_GO; // 7

		if(time < 1) time = 1;
		if(time > 255)time = 255;
		
		speed = getSpeedInternal();
		if(direction == true && speed < 0) mux.sendData(REG_MotorSpeed, (byte) (speed * -1));
		if(direction == false && speed > -1) mux.sendData(REG_MotorSpeed, (byte) (speed * -1));

		
		
		mux.sendData(REG_MotorRunTime, (byte) time);
		mux.sendData(REG_CommandA, (byte) command);
		while(true){
			status = getStatus();
			if((status & STATUS_TIME) == 0)return;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}

	/**
	 * runs motor for specified amount of time and immediately returns
	 * @param time = 1 - 255 sec
	 * @param direction true is forward false is backward.
	 * @param immediateReturn - does not matter if true or false
	 */
	public void rotateTime(int time,boolean direction ,boolean immediateReturn){
		int speed;
		command = 0;
		if(controlSpeed) command |= CONTROL_SPEED; //0
		if(rampUp) command |= CONTROL_RAMP; //1
		command |= CONTROL_TIME;//6
		if(mux.isAutoStart())command |= CONTROL_GO; // 7

		if(time < 1) time = 1;
		if(time > 255)time = 255;
		
		speed = getSpeedInternal();
		if(direction == true && speed < 0) mux.sendData(REG_MotorSpeed, (byte) (speed * -1));
		if(direction == false && speed > -1) mux.sendData(REG_MotorSpeed, (byte) (speed * -1));
		mux.sendData(REG_MotorRunTime, (byte) time);
		mux.sendData(REG_CommandA, (byte) command);
		return;
	}
	
	/**
	 * sets motor speed. this has nothing to do with rpm or rps its just
	 *  a percentage speed type of value
	 * 
	 * @param speed 0 - 100
	 */
	public void setSpeed(int speed) {
		if(speed < 0) speed = 0;
		if (speed > 100) speed = 100;
		mux.sendData(REG_MotorSpeed, (byte) speed);
	}

	/**
	 * enables and disables speed ramping
	 * @param rampUp true turns it on false turns it off
	 */
	public void smoothAcceleration(boolean rampUp) {
		this.rampUp = rampUp;
	}
	
	/**
	 * Determines if speed ramping is enabled
	 * @return true if smooth acceleration is on, else false
	 */
	public boolean isSmoothAcceleration(){
		return this.rampUp;
	}
	
	/**
	 * Determines if the motor is ramping up or down. This is different
	 *  than isSmoothAcceleration() as it returns the current status
	 *   not what is enabled.
	 * @return if ramping up or down, else false
	 */
	public boolean isRamping(){
		int status;
		status = getStatus();
		if((status & STATUS_RAMP) == 1)return true;
		return false;
		
	}
	
	/**
	 * when using this bit after tacho methods force feed back
	 *  will be used to hold the motor in place. if the motor is 
	 *  moved it will move back to the specified position.
	 * @param tachoLock true turns it on
	 */
	public void setTachoLock(boolean tachoLock){
		this.tachoLock = tachoLock;
	}
	
	/**
	 * when this bit is set after tacho methods the motor will break.
	 * @param tachoBreak turns it on.
	 */
	public void setTachoBreak(boolean tachoBreak){
		this.tachoBreak = tachoBreak;
	}
	
	/**
	 * Locks the motor in current position. Uses active feed back to hold it. 
	 */
	public void lock(){
		int position = this.getTachoCount();
		this.setTachoBreak(true);
		this.setTachoLock(true);
		this.rotateTo(position, true);
	}
	
	private byte[] intToByteArray(int value) {
        return new byte[] {
        		(byte)(value),
        		(byte)(value >>> 8),
        		(byte)(value >>> 16),
                (byte)(value >>> 24)} ;
	}
	
	private int byteArrayToInt( byte[] buffer){
		return (buffer[3] << 24)
        + ((buffer[2] & 0xFF) << 16)
        + ((buffer[1] & 0xFF) << 8)
        + (buffer[0] & 0xFF); 
	}
	
	private int getStatus(){
		mux.getData(REG_Status, buffer, 1);
		return buffer[0];
	}
}
