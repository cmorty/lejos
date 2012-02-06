package org.lejos.sample.rfidtest;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.RFIDSensor;

/**
 * Simple test of compass sensors.
 * 
 * Works with Mindsensors and HiTechnic compass sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
 public class RFIDTest {
	
	public static void main(String[] args) throws Exception {
		RFIDSensor rfid = new RFIDSensor(SensorPort.S1);

        LCD.drawString("Type " + rfid.getProductID(), 0, 0);
        LCD.drawString("PID " + rfid.getVendorID(), 0, 1);
        LCD.drawString("Version " + rfid.getVersion(), 0, 2);
        Thread.sleep(5000);
		while(!Button.ESCAPE.isDown()) {
            LCD.clear();
            LCD.drawString("Reading...", 0, 1);
            long id = rfid.readTransponderAsLong(true);
            if (id != 0)
            {
                LCD.clear();
                LCD.drawString("ID = " + id, 0, 1);
                Thread.sleep(5000);
            }
		}
	}	
}
