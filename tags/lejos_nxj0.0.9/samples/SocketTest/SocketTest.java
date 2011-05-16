
import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;
import java.net.*;

/**
* Simple test program. Echoes data back to the socket server
* 
* @author Ranulf Green and Lawrie Griffiths
*/
public class SocketTest {

	private DataInputStream ins;
	private DataOutputStream outs;
	private BTConnection btc = null;
	private Socket sock = null;
	private String connected = "connected";
	private String waiting = "waiting";

	public  SocketTest() throws Exception{
		try {
			connect();
		} catch (IOException e) {
			System.out.println("Failed to connect to server");
			System.exit(1);
		}
		while(true){
			ins = new DataInputStream(sock.getInputStream());
			outs = new DataOutputStream(sock.getOutputStream());
			try {
				String s = readLine();
				System.out.println(s);
				if (s.equals("bye")) break;
				s = "Received " + s + '\n';
				outs.writeChars(s);
				outs.flush();
			} catch(EOFException e){
				System.out.println("End of file");
				break;
			} catch(IOException e){
				System.out.println("IO Exception");
				break;
			}
		}
		ins.close();
		outs.close();
		sock.close();	
	}

	public void connect() throws IOException {
		LCD.drawString(waiting,0,0);
		btc = Bluetooth.waitForConnection();
		LCD.clear();
		sock = new Socket("localhost", 8081, btc);

		LCD.drawString(connected,0,0);
	}

	public static void main(String [] args)  throws Exception
	{
		new SocketTest();
	}
	
	private String readLine() throws IOException{
		StringBuffer sb = new StringBuffer();
		
		while(true) {
			char c = ins.readChar();
			if (c == '\n') break;
			sb.append(c);
		}
		return sb.toString();
	}
}



