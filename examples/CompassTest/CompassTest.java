import lejos.nxt.*;

	public class CompassTest {
		
		public static void main(String[] args) throws Exception {
			CompassSensor compass = new CompassSensor(Port.S1);
						
			while(!Button.ESCAPE.isPressed()) {
				LCD.clear();
				LCD.drawInt(compass.getDegrees(), 0, 0);
				LCD.refresh();
				Thread.sleep(500);
			}
		}	
	}
