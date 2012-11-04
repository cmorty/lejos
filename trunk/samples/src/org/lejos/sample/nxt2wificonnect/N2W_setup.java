package org.lejos.sample.nxt2wificonnect;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;

/**
 * Connect the NXT2WIFI to a WPA2 wifi network. NXT2WIFI is connected on port 4
 * @author Daniele Benedettelli
 */
public class N2W_setup {

	////////////////////////////////////////
	// PUT YOUR WIFI DETAILS HERE
	////////////////////////////////////////
	private static String MYSSID = "Blessed";
	private static String MYPASSPHRASE = "abcdef12";
	private static String MYPASSKEY = "d4d3a089b20d91ef62bd6045467556a9294355bf63e936e0bb0e952f31071f55";

	/**
	 * Connect to a WPA2 protected wifi network and obtain an IP address
	 * using the NXT2WIFI
	 * @param args
	 */
	public static void main(String[] args) {
		//RConsole.open();
		NXT2WIFI wifi = new NXT2WIFI();
		LCD.clear();
		LCD.drawString("NXT2WIFI SETUP", 0, 0);
		LCD.drawString("IP   SETUP   MAC", 0, 7);
		wifi.setTerminalDebug(true); // enable debug stream on computer terminal
		LCD.drawString("FW="+wifi.getFirmwareVersion(), 0, 1);

		while(!Button.ESCAPE.isDown()) {

			// PRESS RIGHT BUTTON TO RETRIEVE MAC ADDRESS
			if (Button.RIGHT.isDown()) {
				String mac = wifi.getMACAddress();
				LCD.drawString(mac+"  ", 0, 5);
				while(Button.RIGHT.isDown()) Delay.msDelay(1);
			}

			// PRESS CENTER BUTTON TO TEST NETWORK CREATION AND CONNECTIVITY
			if (Button.ENTER.isDown()) {
				wifi.disconnect();
				//wifi.connectToWPAAutoWithPassphrase(MYSSID, MYPASSPHRASE, true);
				wifi.connectToWPAAutoWithKey(MYSSID, MYPASSKEY, true);
				// now poll the sensor until I get a connected status back
				int status;
				while( Button.ENTER.isUp() && (status = wifi.connectionStatus()) != NXT2WIFI.CONNECTED) {
					LCD.clear(3);
					LCD.drawString(wifi.connectionStatusToString(status), 0, 3);
					Delay.msDelay(500);
				}

				LCD.drawString(wifi.connectionStatusToString(wifi.connectionStatus())+ "   ", 0, 3);
				Sound.beepSequenceUp();
				Delay.msDelay(3000);
				String ipAddr = wifi.getIPAddress();

				LCD.drawString(ipAddr, 0, 4);
				RConsole.println("IP Address: " + ipAddr);		

				while(Button.ENTER.isDown()) Delay.msDelay(1);
			}			
			// PRESS LEFT BUTTON TO RETRIEVE IP ADDRESS
			if (Button.LEFT.isDown()) {
				String ip = wifi.getIPAddress();
				LCD.drawString(ip+"   ", 0, 4);
				while(Button.LEFT.isDown()) Delay.msDelay(1);
			}
		}
	}
}
