import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;

public class BTReceive {

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
			
			for(int i=0;i<100;i++) {
				int ii = dis.readInt();
				LCD.drawInt(ii,3,0,1);
				LCD.refresh();
				dos.writeInt(-ii);
				dos.flush();
			}
			
			dis.close();
			dos.close();
			btc.close();
			LCD.clear();
		}
	}
}

