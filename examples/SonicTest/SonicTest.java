
import lejos.nxt.*;

public class SonicTest {
	
	public static void main(String[] args) throws Exception {
		UltraSonicSensor sonic = new UltraSonicSensor(Port.S1);
		
		
		while(!Button.ESCAPE.isPressed()) {
			LCD.clear();
			LCD.drawInt(sonic.getDistance(), 0, 0);
			LCD.refresh();
			Thread.sleep(500);
		}
	}	
}
