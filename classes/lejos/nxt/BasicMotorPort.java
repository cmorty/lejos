package lejos.nxt;

/**
 * An abstraction for a motor port that supports RCX
 * type motors, but not NXT motors with tachometers.
 * 
 * @author Lawrie Griffiths
 *
 */
public interface BasicMotorPort {
	static public final int PWM_FLOAT = 0;
	static public final int PWM_BRAKE = 1;
	
	public void controlMotor(int power, int mode);
	
	public void setPWMMode(int mode);
}
