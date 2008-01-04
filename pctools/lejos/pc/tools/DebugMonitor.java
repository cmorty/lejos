package lejos.pc.tools;

import lejos.pc.comm.*;
import java.io.*;

/**
 * Debug output monitor class.
 * This class provides access to debug output from an nxt program. The program
 * simply write debug strings using the nxt Debug class. These are sent to the
 * PC via the USB connection.
 *
 * Note: The low level USB routines are used to allow for larger packet I/O. 
 *
 */ 
public class DebugMonitor {
	
	public static void main(String[] args) {
		NXTCommLibnxt nxtComm = new NXTCommLibnxt();
		NXTInfo[] nxtInfo = null;
		
		nxtInfo = nxtComm.search(null, NXTCommFactory.USB);

		
		if (nxtInfo.length == 0) {
			System.out.println("No NXT Found");
			System.exit(1);
		}

		nxtComm.open(nxtInfo[0]);
		//try {outDat.write(27);}catch(Exception e){}
		byte [] hello = {(byte)27};
		try {
			nxtComm.jlibnxt_send_data(nxtInfo[0].nxtPtr, hello);
		} catch(Exception e)
		{
			System.out.println("Exception writing hello " + e);
		}
		boolean connected = true;
		while (connected) 
		{
			byte b1 = 0;
			byte b2 = 0;
			byte [] buf=null;
			int errCnt = 0;
			while(true)
			{
	           try 
			   {
				   buf = nxtComm.jlibnxt_read_data(nxtInfo[0].nxtPtr, 64);
			   } 
			   catch(Exception e)
			   {
				   if (errCnt++ < 1000) 
					   continue;
				   connected = false;
			   }
			   break;
			}
			if (buf != null)
			{
				for (int i = 0; i < (int) buf[0]; i++)
				{
					char c = (char)((char)buf[i+1] & 0xff);
					if (c == (char)0xff)
					{
						connected = false;
						break;
					}
					else
						System.out.print(c);
				}
			}
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ie) {}
		
		try {
			nxtComm.close();
		} catch (IOException ioe) {}
	}

}
