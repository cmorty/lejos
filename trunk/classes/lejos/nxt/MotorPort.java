package lejos.nxt;

public class MotorPort implements TachoMotorPort {
	int _id;
	int _pwmMode = 0; // default to float mode
	
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
	
	public void controlMotor(int power, int mode)
	{
		// Convert lejos power and mode to NXT power and mode
		controlMotorById(_id - 'A', 
				         (mode >= 3 ? 0 : (mode == 2 ? -power: power)) ,
				         (mode == 3 ? 1 : (mode == 4 ? 0 : _pwmMode)));
	}

	public synchronized static native void controlMotorById(int id, int power, int mode);

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
	  
	public static synchronized native void resetTachoCountById(int aMotor);
}
