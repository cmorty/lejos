import lejos.nxt.*;
import lejos.nxt.addon.*;

import java.awt.Rectangle;

/**
 * For testing the Mindsensors NXTCam.
 * @author Lawrie Griffiths
 */
public class NXTCamTest {

	final static int INTERVAL = 1000; // milliseconds
	
	public static void main(String [] args) throws Exception {
		NXTCam camera = new NXTCam(SensorPort.S1);
		String objects = "Objects: ";
		int numObjects;
		
		camera.sendCommand('A'); // sort objects by size
		camera.sendCommand('E'); // start tracking
	
		while(!Button.ESCAPE.isPressed()) {
			LCD.clear();
			LCD.drawString(camera.getProductID(), 0, 0);
			LCD.drawString(camera.getSensorType(), 0, 1);
			LCD.drawString(camera.getVersion(), 9, 1);
			LCD.drawString(objects, 0, 2);
			LCD.drawInt(numObjects = camera.getNumberOfObjects(),1,9,2);
			
			if (numObjects >= 1 && numObjects <= 8) {
				for (int i=0;i<numObjects;i++) {
					Rectangle r = camera.getRectangle(i);
					if (r.height > 30 && r.width > 30) {
						LCD.drawInt(camera.getObjectColor(i), 3, 0, 3+i);
						LCD.drawInt(r.width, 3, 4, 3+i);
						LCD.drawInt(r.height, 3, 8, 3+i);
					}
					
				}
			}

			LCD.refresh();
			Thread.sleep(INTERVAL);
		}
	}
}