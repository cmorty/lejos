import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.util.Stopwatch;

import java.util.*;
import java.io.*;
import javax.bluetooth.*;
import javax.microedition.location.*;
import lejos.devices.*;

/**
 * This example show how to:
 * 
 * + Connect with a GPS Device with a NXT brick with leJOS
 * + Get Data from GGA NMEA Sentence
 * + Get Data from RMC NMEA Sentence
 * + Get Data from VTG NMEA Sentence
 * + Use JRS-179 Objects
 * + Use Date Objects with leJOS
 * 
 * This example is experimental. It is necessary to test more time
 * 
 * Click on left and right button to show GGA, RMC & VTG Data
 * 
 * @author BB
 * @author Juan Antonio Brenha Moral
 */
public class BTGPS{
	private static String appName = "GPS";
	private static String appVersion = "v6.2";

	//Inquire code
	private static byte[] cod = {0,0,0,0}; // 0,0,0,0 picks up every Bluetooth device regardless of Class of Device (cod).

	//Bluetooth
	private static RemoteDevice GPSDevice = null;
	private static GPS gps = null;
	private static InputStream in = null;

	//2008/07/15: I don't know how to get a GPS using their Address
	//If you connect with a defined GPS Device
	private static String GPSPattern = "HOLUX GPSlim240";//BT Name
	private static final byte[] pin = {(byte) '0', (byte) '0', (byte) '0', (byte) '0'};//GPS Pin

	//private static BTGPSGUI GUIObj;
	private static Date date;
	private static Coordinates coor;
	
	public static void main(String[] args) {

		//Detect GPS Device
		boolean GPSDetected = false;;
		//GPSDetected = discoverBTDevice(GPSPattern);//A faster way when you work with the same GPS Receiver
		GPSDetected = discoverBTDevices();

		if(GPSDetected){
			//Connect with GPS Device
			int connectionStatus = 0;
			connectionStatus = connectGPS();

			if(connectionStatus == 2){
				//Show data from GPS Receiver
				showData();//GUI
			}else{
				if(connectionStatus == -1){
					LCD.drawString("No connection", 0, 7);
				}else if(connectionStatus == -2){
					LCD.drawString("Something goes bad", 0, 7);
				}
			}
			LCD.refresh();
		}else{
			LCD.drawString("No detected GPS", 0, 3);
			LCD.refresh();
		}
	}//End main

	/**
	 * Methods used to discover a predefined GPS receiver and you need
	 * to connect with it directly.
	 * 
	 * @param BTPatternName
	 * @return
	 */
	static boolean discoverBTDevice(String BTPatternName){
		boolean GPSDetected = false;
		RemoteDevice btrd = null;
		String BTDeviceName;

		//Discover BT GPS Devices
		LCD.drawString("Searching ...", 0, 0);
		LCD.refresh();
		//Vector devList = Bluetooth.inquire(5, 10,cod);
		Vector devList = Bluetooth.getKnownDevicesList();

		if(devList.size() > 0){
			for (int i = 0; i < devList.size(); i++) {
				btrd = ((RemoteDevice) devList.elementAt(i));

				BTDeviceName = btrd.getFriendlyName(true);
				if(BTDeviceName.indexOf(GPSPattern) != -1){
					GPSDevice = btrd;
					GPSDetected = true;
					break;
				}
				
			}
		}

		return GPSDetected;
	}

	/**
	 * This method, show all BT Devices with BT Services enable
	 * User choose a GPS device to connect
	 * 
	 * Developer note: This method has a bug when you click in exit button twice
	 */
	static boolean discoverBTDevices(){
		boolean GPSDetected = false;
		
		LCD.clear();
		LCD.drawString("Searching ...", 0, 0);
		LCD.refresh();
		//Make an BT inquire to get all Devices with BT Services enable
		Vector devList = Bluetooth.inquire(5, 10,cod);

		//If exist GPS Devices near
		if (devList.size() > 0){
			String[] names = new String[devList.size()];
			for (int i = 0; i < devList.size(); i++) {
				RemoteDevice btrd = ((RemoteDevice) devList.elementAt(i));
				names[i] = btrd.getFriendlyName(true);
			}
				
			TextMenu searchMenu = new TextMenu(names,1);
			String[] subItems = {"Connect"};
			TextMenu subMenu = new TextMenu(subItems,4);
			
			int selected;
			do {
				LCD.clear();
				LCD.drawString("Found",6,0);
				LCD.refresh();
				//Menu 1: Show all BT Devices
				selected = searchMenu.select();
				if (selected >=0){
					RemoteDevice btrd = ((RemoteDevice) devList.elementAt(selected));
					LCD.clear();
					LCD.drawString("Found",6,0);
					LCD.drawString(names[selected],0,1);
					LCD.drawString(btrd.getBluetoothAddress(), 0, 2);
					//Menu 2: Show GPS Device
					int subSelection = subMenu.select();
					if (subSelection == 0){
						GPSDetected = true;
						GPSDevice = btrd;
						break;
					}
				}
			} while (selected >= 0);
		}else{
			GPSDetected = false;
		}

		return GPSDetected;
	}

	/**
	 * This method connect with a RemoteDevice.
	 * If the connection has success then the method create an instance of
	 * the class GPS which manages an InputStream
	 * 
	 * @return
	 */
	static int connectGPS(){
		int result;
		Bluetooth.addDevice(GPSDevice);

		BTConnection btGPS = null;
		btGPS = Bluetooth.connect(GPSDevice.getDeviceAddr(), NXTConnection.RAW,pin);
		
		if(btGPS == null){
			result  = -1;//No connection
		}else{
			result = 1;//Connection Sucessful
		}

		try{
			in = btGPS.openInputStream();
			gps = new GPS(in);
			gps.updateValues(true);

			result = 2;//
		}catch(Exception e) {
			result = -2;
		}
		
		return result;
	}

	/**
	 * Show the example GUI
	 */
	static void showData(){
		LCD.clear();
		Stopwatch sw;

		int sentenceCount = 0;
		sw = new Stopwatch();
		int CMD = 1;
		int GPSDataQuality = 0;

		LCD.drawString(appName + " " + appVersion, 0,0);
		while(!Button.ESCAPE.isPressed()){
			LCD.drawInt(++sentenceCount, 10, 0);
			GPSDataQuality = gps.getSatellitesTracked();
			//LCD.drawInt((int)Runtime.getRuntime().freeMemory(), 0, 0);

			if(sw.elapsed() >= 10000){
				sw.reset();
				if(GPSDataQuality >=4){
					Sound.twoBeeps();
				}else{
					Sound.beep();
				}
			}
			
			date = gps.getDate();
			coor = new Coordinates(gps.getLatitude(),gps.getLongitude(),gps.getAltitude());

			if (Button.LEFT.isPressed()){
				CMD = 1;
			}

			if (Button.ENTER.isPressed()){
				CMD = 2;
			}

			if (Button.RIGHT.isPressed()){
				CMD = 3;
			}
			
			if(CMD==1){
				showGGAUI();
			}else if(CMD == 2){
				showRMCUI();
			}else{
				showVTGUI();
			}

			LCD.refresh();
			try {Thread.sleep(1000);} catch (Exception e) {}
		}
		
		System.exit(0);
	}
	
	/**
	 * Show GGA Data from GPS
	 */
	private static void showGGAUI(){
		LCD.drawString("GGA", 0, 2);

		LCD.drawString("Tim " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "", 0, 3);
		LCD.drawString("Lat " + coor.getLatitude(), 0, 4);
		LCD.drawString("" + gps.getLatitudeDirection() , 15, 4);
		LCD.drawString("Lon " + coor.getLongitude(), 0, 5);
		LCD.drawString("" + gps.getLongitudeDirection() , 15, 5);
		LCD.drawString("Alt " + coor.getAltitude(), 0, 6);
		LCD.drawString("Sat " + gps.getSatellitesTracked(), 0, 7);
		LCD.refresh();
	}

	/**
	 * Show RMC Data from GPS
	 */
	private static void showRMCUI(){
		LCD.drawString("RMC", 0, 2);
		refreshSomeLCDLines();
		LCD.drawString("Dat " + date.getDay() + "/" + date.getMonth() + "/" + date.getYear() + "", 0, 3);
		LCD.drawString("Azi " + gps.getAzimuth(), 0, 4);
		LCD.refresh();
	}

	/**
	 * Show VTG Data from GPS
	 */
	private static void showVTGUI(){
		LCD.drawString("VTG", 0, 2);
		refreshSomeLCDLines();
		LCD.drawString("Spe " + gps.getSpeed(), 0, 3);
		LCD.refresh();
	}
	
	/**
	 * Clear some LCD lines
	 */
	private static void refreshSomeLCDLines(){
		//LCD.drawString("                     ", 0, 2);
		LCD.drawString("                     ", 0, 3);
		LCD.drawString("                     ", 0, 4);
		LCD.drawString("                     ", 0, 5);
		LCD.drawString("                     ", 0, 6);
		LCD.drawString("                     ", 0, 7);
	}
}//End Class
