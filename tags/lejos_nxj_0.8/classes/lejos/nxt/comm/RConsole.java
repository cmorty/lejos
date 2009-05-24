package lejos.nxt.comm;
import lejos.nxt.*;
import java.io.*;

/**
 * This class provides a simple way of sending output for viewing on a 
 * PC. The output is transmitted via the nxt USB connection or via Bluetooth.
 * If open is not called or if the connection to the PC is timed out, then
 * the output is dicarded.
 */
public class RConsole {
	static PrintStream ps;
    static NXTConnection conn;
	
	private static void init(NXTConnection c)
	{
        if (c == null)
        {
            LCD.drawString("No connection   ", 0, 0);
            return;
        }
        conn = c;
		try {
            LCD.drawString("Got connection  ", 0, 0);
            byte [] hello = new byte[32];
            int len = conn.read(hello, hello.length);
            if (len != 3 || hello[0] != 'C' || hello[1] != 'O' || hello[2] != 'N')
            {
                LCD.drawString("Console no h/s    ", 0, 0);
                conn.close();
                return;
            }
            LCD.drawString("Console open    ", 0, 0);
            if (conn == null) return;
            ps = new PrintStream(conn.openOutputStream());
			LCD.refresh();
			println("Console open");
		}
		catch (Exception e)
		{
			LCD.drawString("Console error " + e.getMessage(), 0, 0);
			LCD.refresh();
		}
	}
	
	public static void openUSB(int timeout)
	{
        LCD.drawString("USB Console...  ", 0, 0);
        init(USB.waitForConnection(timeout, 0));

	}
	
    public static void open()
    {
        openUSB(0);
    }
    
    public static void openBluetooth(int timeout)
    {
        LCD.drawString("BT Console...   ", 0, 0);
        init(Bluetooth.waitForConnection(timeout, NXTConnection.PACKET, null));
    }
    
	public static void print(String s)
	{
		if (ps == null) return;
		synchronized (ps){
            ps.print(s);
            ps.flush();
        }
	}
    
    public static void println(String s)
    {
        if (ps == null) return;
        synchronized(ps){
            ps.println(s);
        }
    }
	
	public static void close()
	{
		if (conn == null) return;
		try {
            println("Console closed");
            conn.close();
			LCD.drawString("Console closed  ", 0, 0);
			LCD.refresh();			
			Thread.sleep(2000);
		}
		catch (Exception e)
		{
		}
	}
	
	public static boolean isOpen() {
		return (ps != null);
	}
    
    public static OutputStream openOutputStream()
    {
        return (conn != null ? conn.openOutputStream() : null);
    }
}


