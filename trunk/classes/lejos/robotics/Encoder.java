package lejos.robotics;

/**
 * Abstraction for the tachometer built into NXT motors.
 * 
 * @author Lawrie Griffiths
 *
 * <br/><br/>WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */
public interface Encoder {
	
	  /**
	   * Returns the tachometer count.
	   * 
	   * @return tachometer count in degrees
	   */
	  public int getTachoCount();

	  
	  /**
	   * Reset the tachometer count.
	   */
	  public void resetTachoCount();

}
