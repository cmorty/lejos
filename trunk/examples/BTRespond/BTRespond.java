import lejos.nxt.*;
import lejos.nxt.comm.*;

public class BTRespond {

	public static void main(String [] args)  throws Exception 
	{

		byte[] inMsg = new byte[64];
		byte [] outMsg = new byte[32];
		boolean cmdMode = true;
		BTConnection btc = null;
		int len;
		String connected = "Connected";
		
		while (true)
		{
			if (cmdMode) {
				btc = Bluetooth.waitForConnection();
				LCD.clear();
				LCD.drawString(connected,0,0);
				LCD.refresh();			
				cmdMode = false;
			}
			
			len = Bluetooth.readPacket(inMsg,64);
			
			if (len > 0)
			{
				LCD.drawInt(len,3,0,1);
				LCD.drawInt(inMsg[0] & 0xFF,3,3,1);
				LCD.drawInt(inMsg[1] & 0xFF,3,6,1);
				LCD.drawInt(inMsg[2] & 0xFF,3,9,1);
				LCD.drawInt(inMsg[3] & 0xFF,3,12,1);
				LCD.refresh();
				LCP.emulateCommand(inMsg,len);
				if (inMsg[1] == 0x09)
				{
					LCD.drawString("Message",0,2);
					LCD.refresh();
					btc.close();
					Thread.sleep(100);
					cmdMode = true;
				}
			}			
		}
	}
}
