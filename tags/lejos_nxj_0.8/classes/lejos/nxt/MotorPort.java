package lejos.nxt;

/**
 * 
 * Abstraction for a NXT output port.
 *
 */
public class MotorPort implements TachoMotorPort {
	int _id;
	int _pwmMode = PWM_FLOAT; // default to float mode
	
	private MotorPort(int id)
	{
		_id = id;
	}
	
	/**
	 * MotorPort A.
	 */
	public static final MotorPort A = new MotorPort ('A');
	
	/**
	 * MotorPort B.
	 */
	public static final MotorPort B = new MotorPort ('B');
	
	/**
	 * MotorPort C.
	 */
	public static final MotorPort C = new MotorPort ('C');
	
	/**
	 * Low-level method to control a motor.
	 * 
	 * @param power power from 0-100
	 * @param mode 1=forward, 2=backward, 3=stop, 4=float
	 */
	public void controlMotor(int power, int mode)
	{
		// Convert lejos power and mode to NXT power and mode
		controlMotorById(_id - 'A', 
				         (mode >= 3 ? 0 : (mode == 2 ? -power: power)) ,
				         (mode == 3 ? 1 : (mode == 4 ? 0 : _pwmMode)));
	}

	/**
	 * Low-level method to control a motor.
	 * 
	 * @param power power from -100 to =100
	 * @param mode 0=float, 1=brake
	 */
	synchronized static native void controlMotorById(int id, int power, int mode);

	/**
	 * returns tachometer count
	 */
	public  int getTachoCount()
	{
		return getTachoCountById(_id - 'A');
	}

	public static native int getTachoCountById(int aMotor);
	
    /**
	 *resets the tachometer count to 0;
	 */ 
	public void resetTachoCount()
	{
		resetTachoCountById( _id - 'A');
	}
	
	public void setPWMMode(int mode)
	{
		_pwmMode = mode;
	}
	  
	public static synchronized native void resetTachoCountById(int aMotor);
}
