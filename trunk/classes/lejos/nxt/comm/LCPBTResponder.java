package lejos.nxt.comm;

/**
 * Support for LCP commands over Bluetooth in user programs.
 * 
 * @author Lawrie Griffiths
 *
 */
public class LCPBTResponder extends Thread {
	
	public void run() 
	{
		byte[] inMsg = new byte[64];
		byte [] reply = new byte[64];
		boolean cmdMode = true;
		BTConnection btc = null;
		int len;
		
		// Wait for power on
		while (!Bluetooth.getPower())
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}				
		}
		
		while (true)
		{	
			if (cmdMode) {
				btc = Bluetooth.waitForConnection();
				if (btc == null) continue;			
				cmdMode = false;
			}
			
			len = btc.read(inMsg,64);
			
			if (len > 0)
			{
				int replyLen = LCP.emulateCommand(inMsg,len, reply);
				if ((inMsg[0] & 0x80) == 0) btc.write(reply, replyLen);
				if (inMsg[1] == LCP.NXJ_DISCONNECT) { 
					btc.close(); 
					cmdMode = true;
				}
			}
			Thread.yield();
		}
	}
}
