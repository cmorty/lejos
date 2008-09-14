import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.util.Stopwatch;

import java.util.*;
import java.io.*;
import javax.bluetooth.*;
//import javax.microedition.location.*;
import lejos.gps.*;

/**
 * This example show how to:
 * 
 * + Connect with a GPS Device with a NXT brick with leJOS
 * + Get Data from GGA NMEA Sentence
 * + Get Data from RMC NMEA Sentence
 * + Get Data from VTG NMEA Sentence
 * + Get Data from GSV NMEA Sentence
 * + Get Data from GSA NMEA Sentence
 * + Use Date Objects with leJOS
 * 
 * This example is experimental. It is necessary to test more time
 * 
 * Click on left and right button to show to show more data about GPS.
 * 
 * Usage Notes:
 * Please, wait 20 seconds until GPS receiver get all data.
 * Sometimes, if you read GSV/GSA without this waiting, it is possible that
 * you see a exception.
 * 
 * @author BB
 * @author Juan Antonio Brenha Moral
 */
public class BTGPS{
	private static String appName = "GPS";
	private static String appVersion = "v6.7";

	//Inquire code
	private static byte[] cod = {0,0,0,0}; // 0,0,0,0 picks up every Bluetooth device regardless of Class of Device (cod).

	//Bluetooth
	private static RemoteDevice GPSDevice = null;
	private static GPS gps = null;
	private static InputStream in = null;

	//GPS Pin
	private static final byte[] pin = {(byte) '0', (byte) '0', (byte) '0', (byte) '0'};

	private static Date date;
	private static NMEASatellite ns;

	public static void main(String[] args) {

		//Detect GPS Device
		boolean GPSDetected = false;
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
		try {Thread.sleep(2000);} catch (Exception e) {}
		//credits(3);
		System.exit(0);
	}//End main
	
	/**
	 * This method, show all BT Devices with BT Services enable
	 * User choose a GPS device to connect
	 * 
	 * Developer note: This method has a bug when you click in exit button twice
	 */
	static boolean discoverBTDevices(){
		boolean GPSDetected = false;
		
		LCD.clear();
		LCD.drawString("Searching...", 0, 0);
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
		int sentenceCount = 0;
		
		Stopwatch sw;
		sw = new Stopwatch();

		boolean flag = true;
		int GPSDataQuality = 0;
		
		//Circular System
		int GPSScreens = 8;
		int GPSCurrentScreen = 1;

		LCD.drawString(appName + " " + appVersion, 0,0);

		while(!Button.ESCAPE.isPressed()){
			LCD.drawInt(++sentenceCount, 10, 0);
			GPSDataQuality = gps.getSatellitesTracked();

			if(sw.elapsed() >= 10000){
				sw.reset();
				if(GPSDataQuality >=4){
					Sound.twoBeeps();
				}else{
					Sound.beep();
				}
			}
			
			date = gps.getDate();

			//Circular System
			if (Button.LEFT.isPressed()){
				if(GPSCurrentScreen == 1){
					GPSCurrentScreen = GPSScreens;
				}else{
					GPSCurrentScreen--;
				}
			}

			if (Button.RIGHT.isPressed()){
				if(GPSCurrentScreen == GPSScreens){
					GPSCurrentScreen = 1;
				}else{
					GPSCurrentScreen++;
				}
			}

			//Reset
			if (Button.ENTER.isPressed()){
				GPSCurrentScreen  =1;
			}

			if(GPSCurrentScreen==1){
				showGGAUIBasic();
			}else if(GPSCurrentScreen == 2){
				showGGAUIAdvanced();
			}else if(GPSCurrentScreen == 3){
				showRMCUI();
			}else if(GPSCurrentScreen == 4){
				showVTGUI();
			}else if(GPSCurrentScreen == 5){
				showGPSTimeUI();
			}else if(GPSCurrentScreen == 6){
				showSatTableUI();
			}else if(GPSCurrentScreen == 7){
				showSatUI();
			}else if(GPSCurrentScreen == 8){
				showSatIDUI();
			}
			
			
			

			LCD.refresh();
			try {Thread.sleep(1000);} catch (Exception e) {}
		}
	}

	/**
	 * Show GGA Basic Data from GPS
	 */
	private static void showGGAUIBasic(){
		refreshSomeLCDLines();
		LCD.drawString("GGA Basic", 0, 2);

		LCD.drawString("Tim " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "", 0, 3);
		LCD.drawString("Lat " + gps.getLatitude().getDecimalDegrees(), 0, 4);
		LCD.drawString("" + gps.getLatitudeDirection() , 15, 4);
		LCD.drawString("Lon " + gps.getLongitude().getDecimalDegrees(), 0, 5);
		LCD.drawString("" + gps.getLongitudeDirection() , 15, 5);
		LCD.drawString("Alt " + gps.getAltitude(), 0, 6);
		LCD.refresh();
	}

	/**
	 * Show GGA Advanced Data from GPS
	 */
	private static void showGGAUIAdvanced(){
		refreshSomeLCDLines();
		LCD.drawString("GGA Advanced", 0, 2);
		
		LCD.drawString("Sat  " + gps.getSatellitesTracked(), 0, 3);
		LCD.drawString("HDOP " + gps.getGGAHDOP(), 0, 4);
		LCD.drawString("QOS  " + gps.getQuality(), 0, 5);
		LCD.refresh();
	}

	/**
	 * Show RMC Data from GPS
	 */
	private static void showRMCUI(){
		refreshSomeLCDLines();
		LCD.drawString("RMC", 0, 2);
		
		LCD.drawString("Dat " + date.getDay() + "/" + date.getMonth() + "/" + date.getYear() + "", 0, 3);
		LCD.drawString("Azi " + gps.getAzimuth(), 0, 4);
		LCD.drawString("Com " + gps.getCompassDegrees(), 0, 5);
		LCD.refresh();
	}

	/**
	 * Show VTG Data from GPS
	 */
	private static void showVTGUI(){
		refreshSomeLCDLines();
		LCD.drawString("VTG", 0, 2);
		
		LCD.drawString("Spe " + gps.getSpeed(), 0, 3);
		LCD.refresh();
	}

	/**
	 * Show Time Data from GPS
	 */
	private static void showGPSTimeUI(){
		refreshSomeLCDLines();
		LCD.drawString("GPS Time data", 0, 2);

		LCD.drawString("Tim " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "", 0, 3);
		LCD.drawString("Dat " + date.getDay() + "/" + date.getMonth() + "/" + date.getYear() + "", 0, 4);
		LCD.refresh();
	}

	private static void showSatTableUI(){
		refreshSomeLCDLines();
		LCD.drawString("Sat Table", 0, 2);
		
		
		NMEASatellite ns1 = gps.getSatellite(0);
		NMEASatellite ns2 = gps.getSatellite(1);
		NMEASatellite ns3 = gps.getSatellite(2);
		NMEASatellite ns4 = gps.getSatellite(3);
		
		LCD.drawString(" PRN Ele Azi SRN",0,3);
		LCD.drawString("1" + ns1.getPRN(),0,4);
		LCD.drawString("2" + ns2.getPRN(),0,5);
		LCD.drawString("3" + ns3.getPRN(),0,6);
		LCD.drawString("4" + ns4.getPRN(),0,7);
		LCD.drawString("" + ns1.getElevation(),5,4);
		LCD.drawString("" + ns2.getElevation(),5,5);
		LCD.drawString("" + ns3.getElevation(),5,6);
		LCD.drawString("" + ns4.getElevation(),5,7);
		LCD.drawString("" + ns1.getAzimuth(),9,4);
		LCD.drawString("" + ns2.getAzimuth(),9,5);
		LCD.drawString("" + ns3.getAzimuth(),9,6);
		LCD.drawString("" + ns4.getAzimuth(),9,7);
		LCD.drawString("" + ns1.getSNR(),13,4);
		LCD.drawString("" + ns2.getSNR(),13,5);
		LCD.drawString("" + ns3.getSNR(),13,6);
		LCD.drawString("" + ns4.getSNR(),13,7);
		LCD.refresh();
	}

	/**
	 * Show Sat Data
	 */
	private static void showSatUI(){
		refreshSomeLCDLines();
		LCD.drawString("Sat data", 0, 2);

		LCD.drawString("Mode1 " + gps.getMode1(), 0, 3);
		LCD.drawString("Mode2 " + gps.getMode2(), 0, 4);
		LCD.drawString("PDOP  " + gps.getPDOP(), 0, 5);
		LCD.drawString("HDOP  " + gps.getHDOP(), 0, 6);
		LCD.drawString("VDOP  " + gps.getVDOP(), 0, 7);
		LCD.refresh();
	}

	/**
	 * Show Sat ID
	 */
	private static void showSatIDUI(){
		refreshSomeLCDLines();
		LCD.drawString("Satellite Table", 0, 2);

		int SV[] = gps.getSV();
		
		int cols[] = {0,5,9,13};
		int rows[] = {3,4,5};

		LCD.drawString("" + SV[0], cols[0], rows[0]);
		LCD.drawString("" + SV[1], cols[1], rows[0]);
		LCD.drawString("" + SV[2], cols[2], rows[0]);
		LCD.drawString("" + SV[3], cols[3], rows[0]);
		LCD.drawString("" + SV[4], cols[0], rows[1]);
		LCD.drawString("" + SV[5], cols[1], rows[1]);
		LCD.drawString("" + SV[6], cols[2], rows[1]);
		LCD.drawString("" + SV[7], cols[3], rows[1]);
		LCD.drawString("" + SV[8], cols[0], rows[2]);
		LCD.drawString("" + SV[9], cols[1], rows[2]);
		LCD.drawString("" + SV[10], cols[2], rows[2]);
		LCD.drawString("" + SV[11], cols[3], rows[2]);
		LCD.refresh();
	}

	/**
	 * Clear some LCD lines
	 */
	private static void refreshSomeLCDLines(){
		LCD.drawString("                     ", 0, 2);
		LCD.drawString("                     ", 0, 3);
		LCD.drawString("                     ", 0, 4);
		LCD.drawString("                     ", 0, 5);
		LCD.drawString("                     ", 0, 6);
		LCD.drawString("                     ", 0, 7);
	}
	
	private static void credits(int seconds){
		LCD.clear();
		LCD.drawString("LEGO Mindstorms",0,1);
		LCD.drawString("NXT Robots  ",0,2);
		LCD.drawString("run better with",0,3);
		LCD.drawString("Java leJOS",0,4);
		LCD.drawString("www.lejos.org",0,6);
		LCD.refresh();
		try {Thread.sleep(seconds*1000);} catch (Exception e) {}
	}
}//End Class
