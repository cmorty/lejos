import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;

public class BTReceive {

	public static void main(String [] args)  throws Exception 
	{
		BTConnection btc;
		String connected = "Connected";
        String waiting = "Waiting";
		InputStream is;
		OutputStream os;

		while (true)
		{
			LCD.drawString(waiting,0,0);
	        LCD.refresh();

			btc = Bluetooth.waitForConnection();
			LCD.clear();
			LCD.drawString(connected,0,0);
			LCD.refresh();			
			is = btc.openInputStream();
			os = btc.openOutputStream();

			for(int i=0;i<100;i++) {
				int b = is.read();
				LCD.drawInt(b,3,0,1);
				LCD.refresh();
				os.write((byte) 100-b);
				os.flush();
			}
			
			btc.close();
			LCD.clear();
		}
	}
}

