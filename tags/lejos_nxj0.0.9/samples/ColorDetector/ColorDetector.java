import lejos.nxt.*;
import lejos.nxt.addon.ColorSensorHT;
import lejos.robotics.Color;

/**
 * For testing the HiTechnic color sensor (see lejos.nxt.addon.ColorSensorHT).
 * @author BB
 */
public class ColorDetector {

	final static int INTERVAL = 200; // milliseconds
	
	public static void main(String [] args) throws Exception {
		ColorSensorHT cmps = new ColorSensorHT(SensorPort.S1);
		String color = "Color";
		String r = "R";
		String g = "G";
		String b = "B";
		
		while(!Button.ESCAPE.isPressed()) {
			LCD.clear();
			LCD.drawString(cmps.getProductID(), 0, 0);
			LCD.drawString(cmps.getSensorType(), 0, 1);
			LCD.drawString(cmps.getVersion(), 9, 1);
			LCD.drawString(color, 0, 3);
			LCD.drawInt((int)cmps.getColorID(),7,3);
			LCD.drawString(r, 0, 5);
			LCD.drawInt((int)cmps.getRGBComponent(Color.RED),1,5);
			LCD.drawString(g, 5, 5);
			LCD.drawInt((int)cmps.getRGBComponent(Color.GREEN),6,5);
			LCD.drawString(b, 10, 5);
			LCD.drawInt((int)cmps.getRGBComponent(Color.BLUE),11,5);
			LCD.refresh();
			Thread.sleep(INTERVAL);
		}
	}
}