import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;

/**
* Gets the time from a time server
* @author Lawrie Griffiths
*
*/
public class TimeTest {
	
	private String host = "time.nist.gov";
	private int port = 13;
	private DataInputStream ins;
	private DataOutputStream outs;
	private BTConnection btc = null;
	private NXTSocket sock = null;
	private String connected = "connected";
	private String waiting = "waiting";
	boolean con = false;

	public TimeTest() throws Exception{
		while(true){
			connect();
			ins = sock.getDataInputStream();
			outs = sock.getDataOutputStream();
			for(int i=0;i<23;i++) {
				try{
					char[] c = new char[1];
					c[0] = (char) ins.read();
					if (i >= 7) {
					  String s = new String(c,0,1);
					  print(s,i-7);
					}
				}catch(IOException e){
					LCD.drawString("ERROR",0,1);
					LCD.refresh();
				}				
			}
			ins.close();
			outs.close();
		}
	}

	public void connect()throws IOException{
		if(!con){
			LCD.clear();
			LCD.drawString(waiting,0,0);
			LCD.refresh();
			btc = Bluetooth.waitForConnection();
			LCD.clear();
			sock = new NXTSocket(host,port,btc);
			con = true;
			LCD.drawString(connected,0,0);
			LCD.refresh();
		}
	}

	public void print(String i, int n){
		LCD.drawString("Received", 0, 0);
		LCD.drawString(i,n,1);
		LCD.refresh();
	}

	public static void main(String [] args)  throws Exception
	{
		new TimeTest();
	}
}



