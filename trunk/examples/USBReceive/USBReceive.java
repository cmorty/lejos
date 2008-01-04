import lejos.nxt.*;
import java.io.*;
import lejos.nxt.comm.*;

/**
 * Test of Java streams over USB.
 * Run the PC example, USBSend, to send data.
 * 
 * @author Lawrie Griffiths
 *
 */
public class USBReceive {

	public static void main(String [] args) throws Exception 
	{
		USBConnection conn = new USBConnection();
		DataOutputStream dOut = conn.openDataOutputStream();
		DataInputStream dIn = conn.openDataInputStream();
		LCD.drawString("waiting", 0, 0);
		
		while (true) 
		{
			int b = dIn.readInt();
			dOut.writeInt(-b);
			dOut.flush();
	        LCD.drawInt((int)b,8,0,1);
		}
	}
}

