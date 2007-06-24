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
        String sending = "sending";
		InputStream is = null;
		OutputStream os = null;
        int count = 0;
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

            BTInputStream ss =(BTInputStream) is;
            int packetLength = 0;
            while(packetLength == 0)
            {
               packetLength = is.available();
               LCD.drawInt(packetLength,0,1);
               LCD.refresh();
               Thread.yield();
            }
            count++;
            int[] buff = new int[256];

            for(int i = 0; i<packetLength; i++)
            {  
               buff[i] = is.read();
               LCD.drawInt(buff[i], 14, i%8);
               LCD.refresh();
            }  
            is.close();
            try{Thread.sleep(1000);}
            catch(InterruptedException e){}
            for (int i = 0; i<packetLength; i++)  os.write((byte) 100-buff[i]);
            LCD.drawInt(count,0, 3);
            LCD.drawString(sending, 0,0);
            os.flush();
            Thread.yield();
		}
	}
}

