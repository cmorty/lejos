import lejos.nxt.*;

/**
 * For testing the HiTechnic color sensor (see lejos.nxt.ColorSensor).
 * @author BB
 */
public class ColorDetector {

	final static int INTERVAL = 200; // milliseconds
	
	public static void main(String [] args) throws Exception {
		ColorSensor cmps = new ColorSensor(SensorPort.S1);
		
		while(!Button.ESCAPE.isPressed()) {
			LCD.clear();
			LCD.drawString(cmps.getProductID(), 0, 0);
			LCD.drawString(cmps.getSensorType(), 0, 1);
			LCD.drawString(cmps.getVersion(), 9, 1);
			LCD.drawString("Color", 0, 3);
			LCD.drawInt((int)cmps.getColorNumber(),7,3);
			LCD.drawString("R", 0, 5);
			LCD.drawInt((int)cmps.getRed(),1,5);
			LCD.drawString("G", 5, 5);
			LCD.drawInt((int)cmps.getGreen(),6,5);
			LCD.drawString("B", 10, 5);
			LCD.drawInt((int)cmps.getBlue(),11,5);
			LCD.refresh();
			Thread.sleep(INTERVAL);
		}
	}
}