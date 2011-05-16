import lejos.nxt.*;
import lejos.nxt.addon.*;

/**
 * 
 * This example show how to use the device DSwitch
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class DSwitchTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DSwitch ds = new DSwitch(MotorPort.A);
		ds.turnOn();
		try {Thread.sleep(2000);} catch (Exception e) {}
		ds.turnOff();
	}

}
