
import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;

/**
* Simple test program. Echoes something similar back to
* the socket server
* @author Ranulf Green
*
*/
public class SocketTest {

	private DataInputStream ins;
	private DataOutputStream outs;
	private BTConnection btc = null;
	private NXTSocket sock = null;
	private String connected = "connected";
	private String waiting = "waiting";
	boolean con = false;

	public  SocketTest() throws Exception{
		while(true){
			connect();
			ins = sock.getDataInputStream();
			outs = sock.getDataOutputStream();
			try{
				String s = ins.readLine();
				print(s);
				s = "not " + s + '\n';
				outs.writeChars(s);
				outs.flush();
			}catch(IOException e){
				LCD.drawString("ERROR",0,1);
				LCD.refresh();
			}
			closeStream();
		}
	}

	public void connect()throws IOException{
		if(!con){
			LCD.clear();
			LCD.drawString(waiting,0,0);
			LCD.refresh();
			btc = Bluetooth.waitForConnection();
			LCD.clear();
			sock = new NXTSocket("localhost",8081,btc);
			con = true;
			LCD.drawString(connected,0,0);
			LCD.refresh();
		}
	}

	public void print(String i){
		LCD.clear();
		LCD.drawString(connected,0,0);
		LCD.drawString(i,0,1);
		LCD.refresh();
	}

	public void closeStream() throws IOException{
		ins.close();
		outs.close();
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e){
		}
	}

	public static void main(String [] args)  throws Exception
	{
		new SocketTest();
	}
}



