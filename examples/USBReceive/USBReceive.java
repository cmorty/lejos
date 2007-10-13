import lejos.nxt.*;
import java.io.*;
import lejos.nxt.comm.*;


public class USBReceive {

	public static void main(String [] args) throws Exception 
	{
		USBConnection conn = new USBConnection();
		InputStream is = conn.openInputStream();
		OutputStream os = conn.openOutputStream();
		while (true) {
			int b = is.read();
			LCD.drawInt(b,3,0,1);
			LCD.refresh();
			os.write((byte) 100-b);
			os.flush();
		}
	}
}
