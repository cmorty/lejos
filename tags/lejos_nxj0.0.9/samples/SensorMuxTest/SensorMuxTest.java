import lejos.nxt.*;
import lejos.nxt.addon.*;

/**
 * This example show how to use the device Sensor Mux from Hitechnic
 * 
 * @author Juan Antonio Brenha Moral
 * @author Xander Soldaat
 *
 */
public class SensorMuxTest {
	public static void main(String[] args) {
		SensorMux sm1 = new SensorMux(SensorPort.S1);
		sm1.configurate();
		
		LCD.drawString("HT SMUX Test" , 0, 0);
		
		LCD.drawString("" + sm1.getProductID(), 0,2);
		LCD.drawString("" + sm1.getVersion(), 0,3);
		LCD.drawString("" + sm1.getSensorType(), 0,4);
		LCD.drawString("Bat: " + sm1.isBatteryLow(), 0, 5);
		LCD.refresh();

		int value1 = 0;
		int value2 = 0;
		int value3 = 0;
		int value4 = 0;

		while(!Button.ESCAPE.isPressed()){
			value1 = sm1.isPressed(1);
			value2 = sm1.readValue(2);
			value3 = sm1.getDistance(3);
			value4 = sm1.getDistance(4);

			LCD.drawString("                         ",0,7);
			LCD.drawString("" + value1, 0,7);
			LCD.drawString("" + value2, 4,7);
			LCD.drawString("" + value3, 8,7);
			LCD.drawString("" + value4, 12,7);
			try{Thread.sleep(100);}catch(Exception e){}
		}
	}
}
