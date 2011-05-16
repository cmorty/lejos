import lejos.nxt.*;
import lejos.nxt.addon.*;
import lejos.robotics.Accelerometer;

/**
 * Simple test of Acceleration (Tilt) sensors.
 * 
 * This should work with Mindsensors and HiTechnic Acceleration
 * sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
public class TiltTest {
	
	public static void main(String[] args) throws Exception {
		Accelerometer tilt = SensorSelector.createAccelerometer(SensorPort.S1);
			
		while(!Button.ESCAPE.isPressed()) {
			LCD.clear();
			LCD.drawString("Tilt", 0, 0);
			LCD.drawInt(tilt.getXTilt(), 6, 0, 1);
			LCD.drawInt(tilt.getYTilt(), 6, 0, 2);
			LCD.drawInt(tilt.getZTilt(), 6, 0, 3);
			LCD.drawString("Accel", 0, 4);
			LCD.drawInt(tilt.getXAccel(), 6, 0, 5);
			LCD.drawInt(tilt.getYAccel(), 6, 0, 6);
			LCD.drawInt(tilt.getZAccel(), 6, 0, 7);
			Thread.sleep(500);
		}
	}	
}
