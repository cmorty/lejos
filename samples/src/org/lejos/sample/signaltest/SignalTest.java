package org.lejos.sample.signaltest;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * Open a connection to the NXT using the BTSend sample
 * and then walk round the house measuring the signal
 * strength.
 * 
 * @author Lawrie Griffiths
 *
 */
public class SignalTest {

	public static void main(String [] args)  throws Exception 
	{
		String connected = "Connected";
        String waiting = "Waiting";
        String strength = "Signal: ";

		LCD.drawString(waiting,0,0);
		LCD.refresh();

        BTConnection btc = Bluetooth.waitForConnection();
        
		LCD.clear();
		LCD.drawString(connected,0,0);
		LCD.refresh();	
		
		while(!Button.ESCAPE.isDown()) {
			LCD.drawString(strength, 0, 3);
			LCD.drawInt(btc.getSignalStrength(), 3, 9 ,3);
			LCD.refresh();
			Thread.sleep(1000);
		}
		
		btc.close();
	}
}
