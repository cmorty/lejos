package org.lejos.sample.timetest;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.NXTSocketUtils;
import java.net.Socket;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

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
	private Socket sock = null;
	private String connected = "Connected";
	private String waiting = "Waiting...";

	public TimeTest() throws Exception{
		connect();
		ins = new DataInputStream(sock.getInputStream());
		outs = new DataOutputStream(sock.getOutputStream());
		StringBuffer sb = new StringBuffer();
		char c;
		int b;
		while (true) {
			try {
				b = ins.read();
				if (b < 0) break;
				c =  (char) b;
				if (c == '*') break;
				sb.append(c);
			} catch(IOException e){
				System.out.println("IO Exception");
			}				
		}
		System.out.println(sb.toString());
		ins.close();
		outs.close();
		sock.close();
		Button.waitForAnyPress();
	}

	public void connect()throws IOException{
		LCD.clear();
		LCD.drawString(waiting, 0, 0);
		btc = Bluetooth.waitForConnection();
		LCD.clear();
		// Set the connection to be used by Socket
		NXTSocketUtils.setNXTConnection(btc);
		sock = new Socket(host, port);
		LCD.drawString(connected, 0, 0);
	}

	public static void main(String [] args)  throws Exception
	{
		new TimeTest();
	}
}



