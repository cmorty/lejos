package lejos.robotics;

/**
 * Abstraction for a Tachometer, which monitors speed of the encoder.
 *
 * @author BB
 *
 */
public interface Tachometer extends Encoder {
	
	
	  /**
	   * Returns the actual speed. This value is calculated every 100 ms on the NXT.
	   * TODO: getRotationSpeed() is an alternate method name, but then we are again competing with Motor.getSpeed()
	   * 
	   * @return speed in degrees per second, negative value means motor is rotating backward
	   */
	  int getRotationSpeed();

}
