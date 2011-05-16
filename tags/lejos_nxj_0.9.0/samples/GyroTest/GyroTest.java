import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.GyroSensor;
import lejos.robotics.Gyroscope;

/**
 * Test of Gyro Sensor
 * Records the minimum, maximum and current values
 * 
 * @author Lawrie Griffiths
 */
public class GyroTest {
	public static void main(String[] args) {
		Gyroscope gyro = new GyroSensor(SensorPort.S1);
		float minValue = 0, maxValue = 0;
		
		LCD.drawString("Gyro Test:", 0, 0);
		LCD.drawString("Min:", 0, 2);
		LCD.drawString("Max:", 0, 3);
		LCD.drawString("Current:", 0, 4);
		
		while(!Button.ESCAPE.isPressed()) {
			float value = gyro.getAngularVelocity();
			
			minValue = Math.min(minValue, value);
			maxValue = Math.max(maxValue, value);
			
			LCD.drawInt((int)minValue, 6, 5, 2);
			LCD.drawInt((int) maxValue, 6, 5, 3);
			LCD.drawInt((int) value, 6, 9, 4);
		}
	}
}
