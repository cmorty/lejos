package org.lejos.sample.nxt2wificonnect;

import java.io.IOException;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;
import lejos.nxt.addon.*;

/**
 * Connect the NXT2WIFI to a WPA2 wifi network. NXT2WIFI is connected on port 4
 * @author Mark Crosbie mark@mastincrosbie.com
*/
public class ConnectWPA2 {

	////////////////////////////////////////
	// PUT YOUR WIFI DETAILS HERE
	////////////////////////////////////////
	private static String MYSSID = "";
	private static String MYPASSPHRASE = "";
	
	/**
	 * Connect to a WPA2 protected wifi network and obtain an IP address
	 * using the NXT2WIFI
	 * @param args
	 */
	public static void main(String[] args) {
		RConsole.openUSB(3000);
		
		LCD.clear();
		
		RConsole.println("Connect to WPA2");
		
		NXT2WIFI wifi = new NXT2WIFI();

		LCD.drawString("WPA2 Connect", 0, 0);
		LCD.drawString("Press enter", 0, 1);
		LCD.drawString("to connect", 0, 2);
			
		Button.ENTER.waitForPressAndRelease();					
			
		Delay.nsDelay(500);
		
		// disconnect from any existing network
		wifi.disconnect();
		Delay.nsDelay(500);
		
		wifi.connectToWPA2WithPassphrase(MYSSID, MYPASSPHRASE, true);

		// now poll the sensor until I get a connected status back
		int status;
		while( Button.ENTER.isUp() && (status = wifi.connectionStatus()) != NXT2WIFI.CONNECTED) {
			LCD.clear(3);
			LCD.drawString(wifi.connectionStatusToString(status), 0, 3);
			RConsole.println("Connection status : " + wifi.connectionStatusToString(status));
			Delay.msDelay(500);
		}

		LCD.drawString(wifi.connectionStatusToString(wifi.connectionStatus()), 0, 3);
		

		// once we're connected allow the sensor to obtain an IP address
		Delay.msDelay(3000);
		
		String ipAddr = wifi.getIPAddress();
		
		LCD.drawString(ipAddr, 0, 5);
		RConsole.println("IP Address: " + ipAddr);		
		
		Sound.beepSequenceUp();

		Button.ENTER.waitForPressAndRelease();					

		RConsole.close();
	}

}
