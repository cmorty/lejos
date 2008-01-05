package lejos.nxt;

public interface BasicMotorPort {
	static public final int PWM_FLOAT = 0;
	static public final int PWM_BRAKE = 1;
	
	public void controlMotor(int power, int mode);
	
	public void setPWMMode(int mode);
}
