package lejos.nxt;

/**
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 * 
 * Abstraction for the tachometer built into NXT motors.
 * 
 * @author Lawrie Griffiths
 *
 */
public interface Tachometer {
	
	public int getTachoCount();
	
	public void resetTachoCount();

}
