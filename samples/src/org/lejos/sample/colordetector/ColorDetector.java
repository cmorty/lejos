package org.lejos.sample.colordetector;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.ColorHTSensor;
import lejos.robotics.Color;

/**
 * For testing the HiTechnic color sensor (see lejos.nxt.addon.ColorHTSensor).
 * @author BB
 */
public class ColorDetector {

	final static int INTERVAL = 200; // milliseconds
	
	public static void main(String [] args) throws Exception {
		ColorHTSensor cmps = new ColorHTSensor(SensorPort.S1);
		String color = "Color";
		String r = "R";
		String g = "G";
		String b = "B";
		
		String[] colorNames = {"Red", "Green", "Blue", "Yellow", "Magenta", "Orange",
				             "White", "Black", "Pink", "Gray", "Light gray", "Dark Gray", "Cyan"			
		};
		
		while(!Button.ESCAPE.isDown()) {
			LCD.clear();
			LCD.drawString(cmps.getVendorID(), 0, 0);
			LCD.drawString(cmps.getProductID(), 0, 1);
			LCD.drawString(cmps.getVersion(), 9, 1);
			LCD.drawString(color, 0, 3);
			LCD.drawInt(cmps.getColorID(),7,3);
			LCD.drawString(colorNames[cmps.getColorID()], 0, 4);
			LCD.drawString(r, 0, 5);
			LCD.drawInt(cmps.getRGBComponent(Color.RED),1,5);
			LCD.drawString(g, 5, 5);
			LCD.drawInt(cmps.getRGBComponent(Color.GREEN),6,5);
			LCD.drawString(b, 10, 5);
			LCD.drawInt(cmps.getRGBComponent(Color.BLUE),11,5);
			LCD.refresh();
			Thread.sleep(INTERVAL);
		}
	}
}