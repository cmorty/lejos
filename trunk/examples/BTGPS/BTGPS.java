import lejos.nxt.*;
import lejos.nxt.comm.*;

import java.util.*;
import java.io.*;
import javax.bluetooth.devices*;

/**
 * This sample allows you to connect to a Bluetooth GPS and
 * read latitude, longitude, and altitude on the NXT LCD.
 * 1. Turn on your Bluetooth GPS
 * 2. Turn on the NXT and run this program
 * 3. It will search out Bluetooth devices. Select your GPS
 * 4. After it connects it will output the data to the LCD.
 * @author BB
 *
 */
public class BTGPS {

	static String found = "Found";
	
	public static void main(String[] args) {
		
		byte[] cod = {0,0,0,0}; // 0,0,0,0 picks up every Bluetooth device regardless of Class of Device (cod).
				
		final byte[] pin = {(byte) '0', (byte) '0', (byte) '0', (byte) '0'};
		
		int sentenceCount = 0; // DELETE ME
		
		InputStream in = null;
		
		LCD.clear();
		LCD.drawString("Searching ...", 0, 0);
		LCD.refresh();
		Vector devList = Bluetooth.inquire(5, 10,cod);
		
		if (devList.size() > 0) {
			String[] names = new String[devList.size()];
			for (int i = 0; i < devList.size(); i++) {
				BTRemoteDevice btrd = ((BTRemoteDevice) devList.elementAt(i));
				names[i] = btrd.getFriendlyName();
			}
				
			TextMenu searchMenu = new TextMenu(names,1);
			String[] subItems = {"Connect"};
			TextMenu subMenu = new TextMenu(subItems,4);
			
			int selected;
			do {
	    		LCD.clear();
				LCD.drawString(found,6,0);
				LCD.refresh();
				selected = searchMenu.select();
				if (selected >=0) {
					BTRemoteDevice btrd = ((BTRemoteDevice) devList.elementAt(selected));
					LCD.clear();
					LCD.drawString(found,6,0);
					LCD.drawString(names[selected],0,1);
					LCD.drawString(btrd.getAddressString(), 0, 2);
					int subSelection = subMenu.select();
					if (subSelection == 0) Bluetooth.addDevice(btrd);
					
					LCD.clear();
					
					BTConnection btGPS = null;
					btGPS = Bluetooth.connect(btrd.getDeviceAddr(), pin);
					
					if(btGPS == null)
						LCD.drawString("No Connection", 0, 1);
					else
						LCD.drawString("Connected!", 0, 1);
					LCD.refresh();
					
					GPS gps = null;
					
					try {
						in = btGPS.openInputStream();
						gps = new GPS(in);
						LCD.drawString("GPS Online", 0, 6);
						LCD.refresh();
					} catch(Exception e) {
						LCD.drawString("Something bad", 0, 6);
						LCD.refresh();
					}
					
					while(true) {
						LCD.drawInt(++sentenceCount, 0, 0); // DELETE
						LCD.drawString("Lat " + gps.getLatitude(), 0, 1);
						LCD.drawString("Long " + gps.getLongitude(), 0, 2);
						LCD.drawString("Alt " + gps.getAltitude(), 0, 3);
						LCD.refresh();
						try {Thread.sleep(500);} catch (Exception e) {}
					}
				}
			} while (selected >= 0);

		} else {
			LCD.clear();
			LCD.drawString("no devices", 0, 0);
			LCD.refresh();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {}
		}
	}
}
