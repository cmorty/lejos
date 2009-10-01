import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.GyroSensor;

/**
 * Test of Gyro Sensor
 * Records the minimum, maximum and current values
 * 
 * @author Lawrie Griffiths
 */
public class GyroTest {
	public static void main(String[] args) {
		GyroSensor gyro = new GyroSensor(SensorPort.S1);
		int minValue = 0, maxValue = 0;
		
		LCD.drawString("Gyro Test:", 0, 0);
		LCD.drawString("Min:", 0, 2);
		LCD.drawString("Max:", 0, 3);
		LCD.drawString("Current:", 0, 4);
		
		while(!Button.ESCAPE.isPressed()) {
			int value = gyro.readValue();
			
			minValue = Math.min(minValue, value);
			maxValue = Math.max(maxValue, value);
			
			LCD.drawInt(minValue, 6, 5, 2);
			LCD.drawInt(maxValue, 6, 5, 3);
			LCD.drawInt(value, 6, 9, 4);
		}
	}
}
