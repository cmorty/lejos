import lejos.nxt.*;
import lejos.nxt.rcxcomm.LLC;


/**
 * Shows Lego IR byes received on the LCD.
 * 
 * Requires a Mindsensors NRLink adapter connected to
 * sensor port S1.
 * 
 * Point the RCX remote control at the NRLink and see
 * the bytes received, or use any other Lego IR source. 
 * 
 * @author Lawrie Griffiths
 *
 */public class RCXLLCMonitor {
	public static void main(String[] args) throws Exception {
		LLC.init(SensorPort.S1);
		
		while (true) {
			int b = LLC.read();
			
			if (b >= 0) {
				LCD.clear();
				LCD.drawInt(b & 0xFF, 3, 0, 0);
				LCD.refresh();
				Thread.sleep(500);				
			}
		}
	}
}
