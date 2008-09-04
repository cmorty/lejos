import lejos.nxt.*;
import lejos.nxt.addon.*;

/**
 * Simple test of compass sensors.
 * 
 * Works with Mindsensors and HiTechnic compass sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
 public class CompassTest {
	
	public static void main(String[] args) throws Exception {
		CompassSensor compass = new CompassSensor(SensorPort.S1);
					
		while(!Button.ESCAPE.isPressed()) {
			LCD.clear();
			LCD.drawInt((int) compass.getDegrees(), 0, 0);
			LCD.refresh();
			Thread.sleep(500);
		}
	}	
}
