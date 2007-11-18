import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.*;
import lejos.nxt.comm.*;

public class SignalTest {

	public static void main(String [] args)  throws Exception 
	{
		String connected = "Connected";
        String waiting = "Waiting";

		while (true)
		{
			LCD.drawString(waiting,0,0);
			LCD.refresh();

	        BTConnection btc = Bluetooth.waitForConnection();
	        
			LCD.clear();
			LCD.drawString(connected,0,0);
			LCD.refresh();	
			
			InputStream is = btc.openInputStream();
			OutputStream os = btc.openOutputStream();
			DataInputStream dis = new DataInputStream(is);
			DataOutputStream dos = new DataOutputStream(os);
			
			while(true) {
				LCD.clear();
				LCD.refresh();
				LCD.drawInt(btc.getSignalStrength(), 3, 0 ,1);
				LCD.refresh();
				Thread.sleep(1000);
			}
		}
	}
}
