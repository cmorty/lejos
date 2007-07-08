import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;

public class BTReceive {

	public static void main(String [] args)  throws Exception 
	{
		boolean cmdMode = true;
		BTConnection btc = null;
		String connected = "Connected";
        String waiting = "Waiting";
		InputStream is = null;
		OutputStream os = null;
		LCD.drawString(waiting,0,0);
        LCD.refresh();
        
		while (true)
		{
			if (cmdMode) {
				btc = Bluetooth.waitForConnection();
				LCD.clear();
				LCD.drawString(connected,0,0);
				LCD.refresh();			
				cmdMode = false;
				is = btc.openInputStream();
				os = btc.openOutputStream();
			}

			int b = is.read();
			LCD.drawInt(b,3,0,1);
			LCD.refresh();
			os.write((byte) 100-b);
		    os.flush();
		}
	}
}

