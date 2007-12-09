package lejos.nxt.comm;

import java.io.*;

/**
 * Support for LCP commands in user programs.
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
		
		while (true)
		{
			if (cmdMode) {
				btc = Bluetooth.waitForConnection();
				if (btc == null) continue;			
				cmdMode = false;
			}
			
			len = Bluetooth.readPacket(inMsg,64);
			
			if (len > 0)
			{
				int replyLen = LCP.emulateCommand(inMsg,len, reply);
				if ((inMsg[0] & 0x80) == 0) Bluetooth.sendPacket(reply, replyLen);
				if (inMsg[1] == (byte) 0x20) { // Disconnect
					Bluetooth.btSetCmdMode(1); // set Command mode
					try {
						btc.close();
					} catch (IOException ioe) {}
					cmdMode = true;
				}
			}
			Thread.yield();
		}
	}
}
