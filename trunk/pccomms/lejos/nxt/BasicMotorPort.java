package lejos.nxt;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * An abstraction for a motor port that supports RCX
 * type motors, but not NXT motors with tachometers.
 * 
 * @author Lawrie Griffiths.
 * 
 */
public interface BasicMotorPort {
	static public final int PWM_FLOAT = 0;
	static public final int PWM_BRAKE = 1;
	public final static int FORWARD = 1;
	public final static int BACKWARD = 2;
	public final static int STOP = 3;
	public final static int FLOAT = 4;
    public final static int MAX_POWER = 100;
	
	public void controlMotor(int power, int mode);
	
	public void setPWMMode(int mode);
}
