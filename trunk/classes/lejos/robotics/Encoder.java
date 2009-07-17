package lejos.robotics;

/**
 * Abstraction for an encoder..
 * 
 * @author BB
 *
 */
public interface Encoder {
	
	/**
	   * Returns the tachometer count.
	   * 
	   * @return tachometer count in degrees
	   */
	  int getTachoCount();

	  /**
	   * Returns the actual speed. This value is calculated every 100 ms on the NXT.
	   * 
	   * @return speed in degrees per second, negative value means motor is rotating backward
	   */
	  int getActualSpeed();

	  /**
	   * Reset the tachometer count.
	   */
	  void resetTachoCount();
}
