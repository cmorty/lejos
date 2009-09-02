import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.*;
import lejos.nxt.comm.*;

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
		
		while(!Button.ESCAPE.isPressed()) {
			LCD.drawString(strength, 0, 3);
			LCD.drawInt(btc.getSignalStrength(), 3, 9 ,3);
			LCD.refresh();
			Thread.sleep(1000);
		}
		
		btc.close();
	}
}
