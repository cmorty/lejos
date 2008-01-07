package lejos.nxt;

/**
 * Abstraction for the tachometer built into NXT motors.
 * 
 * @author Lawrie Griffiths
 *
 */
public interface Tachometer {
	
	public int getTachoCount();
	
	public void resetTachoCount();

}
