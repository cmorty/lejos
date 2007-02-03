
import lejos.nxt.*;

public class TiltTest {
	
	public static void main(String[] args) throws Exception {
		TiltSensor tilt = new TiltSensor(Port.S1);
		
		
		while(!Button.ESCAPE.isPressed()) {
			LCD.clear();
			LCD.drawInt(tilt.getXTilt(), 0, 0);
			LCD.drawInt(tilt.getYTilt(), 0, 1);
			LCD.drawInt(tilt.getZTilt(), 0, 2);
			LCD.refresh();
			Thread.sleep(500);
		}
	}	
}
