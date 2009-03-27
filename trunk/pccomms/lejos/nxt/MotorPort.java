package lejos.nxt;

import lejos.nxt.remote.*;
import lejos.pc.comm.NXTCommandConnector;

import java.io.*;

/**
 * Supports a motor connected to a remote NXT
 * 
 * @author Lawrie Griffiths
 *
 */
public class MotorPort implements NXTProtocol, TachoMotorPort
{
	private static final NXTCommand nxtCommand = NXTCommandConnector.getSingletonOpen();
	private int id;
	
	public MotorPort(int id) {
		this.id = id;
	}
	
	public static MotorPort A = new MotorPort(0);
	public static MotorPort B = new MotorPort(1);
	public static MotorPort C = new MotorPort(2);
	
	public void controlMotor(int power, int mode)
	{
		int lcpMode = 0, lcpPower = power, runState = 0;
		
		if (mode == 1) { // forward
			lcpMode = MOTORON;
			runState = MOTOR_RUN_STATE_RUNNING;
		} else if (mode == 2) { // backward
			lcpMode = MOTORON;
			lcpPower = -lcpPower;
			runState = MOTOR_RUN_STATE_RUNNING;
		} else if (mode == 3) { // stop
			lcpPower = 0;
			lcpMode = BRAKE;
			runState = MOTOR_RUN_STATE_IDLE;
		} else { // float
			lcpPower = 0;
			lcpMode = 0;
			runState = MOTOR_RUN_STATE_IDLE;			
		}
		try {
			nxtCommand.setOutputState((byte) id, (byte) lcpPower, lcpMode, REGULATION_MODE_IDLE, 0, runState, 0);
		} catch (IOException ioe) {}	
	}
	
	public  int getTachoCount()
	{
		try {
			return nxtCommand.getTachoCount(id);
		} catch (IOException ioe) {
			return 0;
		}
	}
	
	public void resetTachoCount()
	{
		try {
			nxtCommand.resetMotorPosition(id, false);
		} catch (IOException ioe) { }
	}
	
	public void setPWMMode(int mode) {
	}
}

