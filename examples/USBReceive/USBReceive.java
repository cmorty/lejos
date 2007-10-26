import lejos.nxt.*;
import java.io.*;
import lejos.nxt.comm.*;


public class USBReceive {

	public static void main(String [] args) throws Exception 
	{
		USBConnection conn = new USBConnection();
		InputStream is = conn.openInputStream();
		OutputStream os = conn.openOutputStream();
//		DataOutputStream dOut = conn.openDataOutputStream();
		DataOutputStream dOut = new DataOutputStream(os);
//		DataInputStream dIn = conn.openDataInputStream();
		DataInputStream dIn = new DataInputStream(is);
		LCD.drawString("waiting", 0, 0);
		LCD.refresh();
		int b;
		while (true) 
		{
			b = dIn.readInt();
//		    b = is.read();
			dOut.writeInt(b);
			dOut.flush();
	         LCD.drawInt((int)b,8,0,1);
	          LCD.refresh();
//			os.write( b);
//			os.flush();
	
		}
	}
}

