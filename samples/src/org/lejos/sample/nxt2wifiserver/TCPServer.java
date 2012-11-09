package org.lejos.sample.nxt2wifiserver;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;
import java.io.*;
import lejos.nxt.addon.*;

/**
 * Open a TCP echo server on port 88 on the NXT2WIFI device attached to port 4. Receive
 * characters and echo them back with a time message. Exit when "quit" is typed.
 * Assumes that the wifi parameters have been defined in the NXT2WIFI.
 *
 * @author Mark Crosbie mark@mastincrosbie.com
*/
public class TCPServer {

	private static final int TCPSERVER_PORT = 88;
	private static final int TCPSERVER_SOCKET = 1;
	
	/**
	 * Start a TCP server on the NXT2WIFI device on port 88
	 * Listens for a connection and echoes back a friendly string
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RConsole.openUSB(3000);
		
		LCD.clear();
		
		RConsole.println("TCP Server");
		
		try {
			NXT2WIFI wifi = new NXT2WIFI();	
			wifi.setConsoleDebug(true);				// enable verbose debug messages to the RConsole
			
			LCD.drawString("TCP Echo Server", 0, 0);
			LCD.drawString("Press enter", 0, 1);
			LCD.drawString("to start", 0, 2);
			LCD.drawString("Escape to", 0, 4);
			LCD.drawString("connect wifi", 0, 5);
			
			Delay.nsDelay(500);
			int b = Button.waitForAnyPress();

			LCD.clear();
			LCD.drawString("TCP Echo Server", 0, 0);
			
			if(b == Button.ID_ESCAPE) {
			
				// disconnect from any existing network
				LCD.drawString("Disconnecting", 0, 3);
				wifi.disconnect();
				Delay.nsDelay(500);
				LCD.clear(3);
				
				// reconnect to previously defined network
				LCD.drawString("Reconnect", 0, 3);
				wifi.connect(true);
				Delay.nsDelay(500);
				LCD.clear(3);
				
				// now poll the sensor until I get a connected status back
				int status;
				while( Button.ENTER.isUp() && (status = wifi.connectionStatus()) != NXT2WIFI.CONNECTED) {
					LCD.drawString(wifi.connectionStatusToString(status), 0, 3);
						RConsole.println("Connection status : " + wifi.connectionStatusToString(status));
					Delay.msDelay(500);
				}
	
				LCD.drawString(wifi.connectionStatusToString(wifi.connectionStatus()), 0, 3);
				
				// once we're connected allow the sensor to obtain an IP address
				Delay.msDelay(1000);
				
				String ipAddr = wifi.getIPAddress();
				
				LCD.drawString(ipAddr, 0, 4);
				RConsole.println("IP Address: " + ipAddr);
				Sound.beepSequenceUp();
			}
			
			LCD.clear();
			LCD.drawString("TCP Server", 0, 0);

			// Close all existing sockets
			wifi.closeSocket(0);
		
			// Start the TCP server
			wifi.openServerSocket(NXT2WIFI.TCP, TCPSERVER_PORT, TCPSERVER_SOCKET);
			
			LCD.drawString(wifi.getIPAddress(), 0, 1);
			LCD.drawString("Port " + TCPSERVER_PORT, 0, 2);
									
			InputStream in = wifi.getInputStream(TCPSERVER_SOCKET);
			OutputStream out = wifi.getOutputStream(TCPSERVER_SOCKET, NXT2WIFI.TCP);
			
			//DataInputStream dis = new DataInputStream(in);
			//DataOutputStream dos = new DataOutputStream(out);
			
			String msg;
			// start the echo server
			while(Button.ENTER.isUp()) {
				// read from the socket
				int avail = in.available();
				if(avail > 0) {
					byte data[] = new byte[avail];

					in.read(data, 0, avail);
					
					String s = new String(data);
					
					LCD.drawString(s, 0, 7);
					LCD.scroll();
					RConsole.println("=========> " + s);
					
					if(s.startsWith("quit")) {
						msg = "Goodbye!\n";
						out.write(msg.getBytes());
						break;
					}
					
					String reply = "Hello:" + s +"\n";
					long ticks = System.currentTimeMillis();
					msg = "The time is " + ticks + "\n\n";

					out.write(reply.getBytes());
					out.write(msg.getBytes());
				}
				Delay.msDelay(500);

			}
			
			LCD.clear(7);
			LCD.drawString("Done", 0, 7);
			
			wifi.closeSocket(TCPSERVER_SOCKET);
			
		} catch (IOException e) {
			LCD.clear();
			LCD.drawString("### Exception",0,0);
			LCD.drawString(e.getMessage(),0,1);
			RConsole.println("EXCEPTION: " + e.getMessage());
			Sound.buzz();
			Delay.msDelay(5000);
		}
		RConsole.close();

	}

}
