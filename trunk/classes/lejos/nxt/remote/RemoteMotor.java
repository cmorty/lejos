package lejos.nxt.remote;

import java.io.*;
import lejos.nxt.TachoMotor;

/**
 * Motor class. Contains three instances of Motor.
 * Usage: Motor.A.forward(500);
 *  
 * @author <a href="mailto:bbagnall@mts.net">Brian Bagnall</a>
 *
 */
public class RemoteMotor implements TachoMotor, NXTProtocol {
	
	private int id;
	private byte power;
	private int mode;
	private int regulationMode;
	public byte turnRatio;
	private int runState;	
	private boolean _rotating = false;
	private NXTCommand nxtCommand;
	
	public RemoteMotor(NXTCommand nxtCommand, int id) {
		this.id = id;
		this.power = 80; // 80% power by default. Is this speed too?
		this.mode = BRAKE + REGULATED; // Brake mode and regulation default
		this.regulationMode = REGULATION_MODE_MOTOR_SPEED;
		this.turnRatio = 0; // 0 = even power/speed distro between motors
		this.runState = MOTOR_RUN_STATE_IDLE;
		this.nxtCommand = nxtCommand;
	}
	
	/**
	* Get the ID of the motor. One of 'A', 'B' or 'C'.
	*/
	public final char getId() {
		
		char port = 'A';
		switch(id) {
			case 0:
				port='A';
				break;
			case 1:
				port='B';
				break;
			case 2:
				port='C';
				break;	
		}
		return port;
	}

	public void forward() {
		this.runState = MOTOR_RUN_STATE_RUNNING;
		try {
			nxtCommand.setOutputState(id, power, this.mode + MOTORON, regulationMode, turnRatio, runState, 0);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			//return -1;
		}
	}
	
	public void backward() {
		this.runState = MOTOR_RUN_STATE_RUNNING;
		try {
			nxtCommand.setOutputState(id, (byte)-power, this.mode + MOTORON, regulationMode, turnRatio, runState, 0);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			//return -1;
		}
	}
	
	public void setSpeed(int speed) {
		
		if(speed > 900|speed < 0)
			return;
		speed = (speed * 100) / 900;
		this.power = (byte)speed;
	}
	
	/**
	 * Sets the power of the motor
	 * @param power the power (-100 to +100)  
	 */
	public void setPower(int power) {
		this.power = (byte)power;
	}
	
	public int getSpeed() {
		return (this.power * 900) / 100;
	}
	
	/**
	 * Return the power that the motor is set to
	 * @return the power (-100 to +100)
	 */
	public int getPower() {
		return power;
	}
	
	public int getTachoCount() {
		try {
			OutputState state = nxtCommand.getOutputState(id);
			return state.rotationCount;
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return -1;
		}
	}
	
	/**
	 * Returns the rotation count for the motor. The rotation count is something
	 * like the trip odometer on your car.  This count is reset each time a new function
	 * is called in Pilot.
	 * @deprecated
	 * @return rotation count.
	 */
	public int getRotationCount() {
		// !! Consider making this protected to keep off limits from users.
		return getTachoCount();
	}
	
	/**
	 * Block Tachometer Count is the count used to synchronize motors
	 * with one another. 
	 * NOTE: If you are using leJOS NXJ firmware this will
	 * always return 0 because this variable is not used in 
	 * in leJOS NXJ firmware. Use getRotationCount() instead.
	 * @deprecated
	 * @return Block Tachometer count.
	 */
	public int getBlockTacho() {
		try {
			OutputState state = nxtCommand.getOutputState(id);
			return state.blockTachoCount;
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return 0;
		}	
	}
	
	public void rotate(int count, boolean returnNow) {
		this.runState = MOTOR_RUN_STATE_RUNNING;
		// ** Really this can accept a ULONG value for count. Too lazy to properly convert right now:
		byte status =  0;
		// !! This used to say power > 0, apparently not working.
		//if(power > 0)
		try {
			if(count > 0)
				nxtCommand.setOutputState(id, power, this.mode + MOTORON, regulationMode, turnRatio, runState, count); // Note using tachoLimit with Lego FW
			else
				nxtCommand.setOutputState(id, (byte)-power, this.mode + MOTORON, regulationMode, turnRatio, runState, Math.abs(count)); // Note using tachoLimit with Lego FW			
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		if(returnNow) {
			//return status;
		} else {
			// Check if mode is moving until done
			while(isMoving()) {Thread.yield();}
			//return status;
		}
	}
	
	public boolean isMoving() {
		try {
			OutputState o = nxtCommand.getOutputState(id);
			// return ((MOTORON & o.mode) == MOTORON);
			return o.runState != MOTOR_RUN_STATE_IDLE; // Peter's bug fix
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return false;
		}
	}
	
	/**
	 * CURRENTLY NOT IMPLEMENTED! Use isMoving() for now.
	   *returns true when motor is rotating toward a specified angle
	   */ 
	  public boolean isRotating()
	  {
		  // Should probably use Tacho Limit value from
		  // get output state
	  	return  _rotating;
	  }
	
	public void rotate(int count) {
		rotate(count, false);
	}
	
	/**
	 * This method determines if and how the motor will be regulated.
	 * REGULATION_MODE_IDLE turns off regulation
	 * REGULATION_MODE_MOTOR_SPEED regulates the speed (I think)
	 * REGULATION_MODE_MOTOR_SYNC synchronizes this and any other motor with SYNC enabled.
	 * @param mode See NXTProtocol for enumerations: REGULATION_MODE_MOTOR_SYNC, 
	 *  REGULATION_MODE_MOTOR_SPEED,  REGULATION_MODE_IDLE
	 */
	public void setRegulationMode(int mode) {
		// !! Consider removing this method! No need, confusing, makes other forward methods unreliable.
		this.regulationMode = mode;
	}
	
	/**
	 * Rotates to a desired tacho count. Does not return until rotation done.
	 * Note: The tachocount can not be reset to zero.
	 * @param target
	 */
	public void rotateTo(int target) {
		rotateTo(target, false);
		
	}
	
	/**
	 * Rotates to a desired tacho count. Returns before the rotation is done
	 * if you include true as the argument.
	 * @param target
	 */
	public void rotateTo(int target, boolean returnNow) {
		// !! Probably inaccuracy can creep into this if
		// rotateTo is called while motor moving.
		int tachometer = this.getTachoCount();
		rotate(target - tachometer, returnNow);
	}
	
	public void resetTachoCount() {
		try {
			nxtCommand.resetMotorPosition(this.id, false);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			//return -1;
		}
	}
	
	/**
	 * Resets the block tachometer.
	 * NOTE: If you are using leJOS NXJ firmware this will not do anything
	 * because BlockTacho is not used in the leJOS NXJ firmware.
	 * Use resetRotationCounter() instead.
	 * @deprecated
	 * @return Error value. 0 means success. See lejos.pc.comm.ErrorMessages for details.
	 */
	public int resetBlockTacho() {
		// Note: This method can also reset tachometer relative to last position.
		// I didn't include this because it seems unintuitive, but the 
		// functionality could be added, maybe with a resetTachoRelative() method.
		// Just change false to true in statement below for relative reset.
		// @param relative TRUE: position relative to last movement, FALSE: absolute position
		 
		try {
			nxtCommand.resetMotorPosition(this.id, true);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return -1;
		}
		return 0;
	}
	
	public void stop() {
		this.runState = MOTOR_RUN_STATE_RUNNING;
		//this.regulationMode = REGULATION_MODE_MOTOR_SPEED;
		try {
			// NOTE: Setting power to 0 seems to make it lock motor, not float it.
			nxtCommand.setOutputState(id, (byte)0, BRAKE + MOTORON + REGULATED, regulationMode, turnRatio, runState, 0);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			//return -1;
		}
	}
	
	/**
	 * Stops the motor without using brakes. 
	 * @return Error value. 0 means success.
	 */
	public int flt() {
		this.runState = MOTOR_RUN_STATE_IDLE;
		//this.regulationMode = REGULATION_MODE_MOTOR_SPEED;
		this.mode = MOTOR_RUN_STATE_IDLE;
		try {
			return nxtCommand.setOutputState(id, (byte)0, 0x00, regulationMode, turnRatio, runState, 0);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return -1;
		}
	}
	
	public void regulateSpeed(boolean yes) {
		// TODO Currently a dummy for remote motors.
	}
	
	public void smoothAcceleration(boolean yes) {
		// TODO Currently a dummy for remote motors.
	}
	
	public int getActualSpeed()	{
		// TODO Currently dummy for remote motors - returns the speed that has been set.
	     return getSpeed();
	}
	
	
}