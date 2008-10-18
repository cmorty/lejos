
import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.nxt.socket.*;
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

	public  SocketTest() throws Exception{
		connect();
		while(true){
			ins = sock.getDataInputStream();
			outs = sock.getDataOutputStream();
			try {
				String s = ins.readLine();
				System.out.println(s);
				if (s.equals("bye")) break;
				s = "not " + s + '\n';
				outs.writeChars(s);
				outs.flush();
			}catch(IOException e){
				System.out.println("IO Exception");
			}
		}
		ins.close();
		outs.close();
		sock.close();
		
	}

	public void connect()throws IOException{
		LCD.drawString(waiting,0,0);
		btc = Bluetooth.waitForConnection();
		LCD.clear();
		sock = new NXTSocket("localhost", 8081, btc);
		LCD.drawString(connected,0,0);
	}

	public static void main(String [] args)  throws Exception
	{
		new SocketTest();
	}
}



