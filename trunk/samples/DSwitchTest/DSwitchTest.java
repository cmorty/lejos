import lejos.nxt.*;
import lejos.nxt.addon.*;

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
