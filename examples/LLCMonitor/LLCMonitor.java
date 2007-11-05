import lejos.nxt.*;
import lejos.rcxcomm.LLC;


public class LLCMonitor {
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
