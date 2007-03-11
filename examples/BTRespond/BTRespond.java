import lejos.nxt.*;

public class BTRespond {

	public static void main(String [] args)  throws Exception 
	{

		byte[] inMsg = new byte[32];
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
			
			len = Bluetooth.readPacket(inMsg,32);
			
			if (len > 0)
			{
				LCP.emulateCommand(inMsg,len);
				if (inMsg[1] == 0x09)
				{
					LCD.drawString("Message",0,1);
					LCD.refresh();
					btc.close();
					Thread.sleep(100);
					cmdMode = true;
				}
			}			
		}
	}
}
