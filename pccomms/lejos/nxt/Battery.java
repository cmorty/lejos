package lejos.nxt;

import lejos.pc.comm.*;
import java.io.*;

/**
 * Battery class.
 * Usage: int x = Battery.getVoltageMilliVolt();
 * @author <a href="mailto:bbagnall@mts.net">Brian Bagnall</a>
 *
 */

public class Battery implements NXTProtocol {
	
	private static final NXTCommand nxtCommand = NXTCommand.getSingletonOpen();
	
	// Ensure no one tries to instantiate this.
	private Battery() {}
		
	/**
	 * The NXT uses 6 batteries of 1500 mV each.
	 * @return Battery voltage in mV. ~9000 = full.
	 */
	public static int getVoltageMilliVolt() {
		/*
	     * calculation from LEGO firmware
	     */
		int voltage;
		try {
			voltage = nxtCommand.getBatteryLevel();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return 0;
		}
		return voltage;
	}

	/**
	 * The NXT uses 6 batteries of 1.5 V each.
	 * @return Battery voltage in Volt. ~9V = full.
	 */
	public static float getVoltage()  {
	   return (float)(getVoltageMilliVolt() * 0.001);
	}
}

