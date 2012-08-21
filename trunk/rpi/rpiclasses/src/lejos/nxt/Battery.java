package lejos.nxt;

/**
 * Dummy battery class.
 * 
 * Usage: int x = Battery.getVoltageMilliVolt();
 * 
 * @author Lawrie Griffiths
 *
 */
public class Battery {
	
	// Ensure no one tries to instantiate this.
	private Battery() {}
		
	/**
	 * The NXT uses 6 batteries of 1500 mV each.
	 * @return Battery voltage in mV. ~9000 = full.
	 */
	public static int getVoltageMilliVolt() {
		return 9;
	}

	/**
	 * The NXT uses 6 batteries of 1.5 V each.
	 * @return Battery voltage in Volt. ~9V = full.
	 */
	public static float getVoltage()  {
	   return 9.0f;
	}
}

