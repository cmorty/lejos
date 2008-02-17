package lejos.nxt.comm;
import lejos.nxt.*;
import java.io.*;

/**
 * This class provides a simple way of sending debug output for viewing on a 
 * PC. The output is transmitted via the nxt USB connection. If open is not
 * called or if the connection to the PC is timed out, then the debug output
 * is dicarded.
 */
public class Debug {
	static USBConnection conn;
	static byte [] buf = new byte[64];
	
	public static void open(int timeout)
	{
		try {
			conn = new USBConnection();
			LCD.drawString("Debug waiting", 0, 0);
			LCD.refresh();
			// Wait for monitor to connect
			int end = (int) System.currentTimeMillis() + timeout;
			while ((USB.usbRead(buf, buf.length) != 1) || (buf[0] != (byte)27))
			{
				Thread.yield();
				if ((int)System.currentTimeMillis() > end)
				{
					conn.close();
					conn = null;
					return;
				}
			}
			LCD.drawString("Debug starting", 0, 0);
			LCD.refresh();
			out("Debug connected...\n");
		}
		catch (Exception e)
		{
			LCD.drawString("Debug error " + e.getMessage(), 0, 0);
			LCD.refresh();
		}
	}
	
	public static void open()
	{
		open(0x7fffffff);
	}
	
	public static void out(String s)
	{
		if (conn == null) return;
		int len = s.length();
		if (len > 63) len = 63;
		synchronized (conn){
		try
		{
			for(int i = 0; i < len; i++)
				buf[i+1] = (byte)s.charAt(i);

			buf[0] = (byte) len;
			USB.usbWrite(buf, len+1);
		}
		catch (Exception e)
		{
			LCD.drawString("Debug error " + e.getMessage(), 0, 0);
			LCD.refresh();
		}
		}
	}
	
	public static void close()
	{
		if (conn == null) return;
		try {
			buf[0] = 1;
			buf[1] = (byte)0xff;
			USB.usbWrite(buf, 2);
			conn.close();
			LCD.drawString("Debug closed        ", 0, 0);
			LCD.refresh();			
			Thread.sleep(5000);
		}
		catch (Exception e)
		{
		}
	}
	
	public static boolean isOpen() {
		return (conn != null);
	}
}


