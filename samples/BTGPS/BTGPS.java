import lejos.addon.gps.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.util.Stopwatch;
import lejos.util.TextMenu;

import java.util.*;
import java.io.*;
import javax.bluetooth.*;
import javax.microedition.location.*;

/**
 * This example show how to:
 * 
 * + Connect with a GPS Device with a NXT brick with leJOS
 * + Get Data from GGA NMEA Sentence
 * + Get Data from RMC NMEA Sentence
 * + Get Data from VTG NMEA Sentence
 * + Get Data from GSV NMEA Sentence
 * + Get Data from GSA NMEA Sentence
 * + Use JRS-179 Objects
 * + Use Date Objects with leJOS
 * 
 * 
 * This example is experimental. It is necessary to test more time
 * 
 * Click on left and right button to show to show more data about GPS.
 * 
 * @author BB
 * @author Juan Antonio Brenha Moral
 */
public class BTGPS{
	private static String appName = "GPS";
	private static String appVersion = "v6.8";

	//Inquire code
	private static int cod = 0; // 0 picks up every Bluetooth device regardless of Class of Device (cod).

	//Bluetooth
	private static RemoteDevice GPSDevice = null;
	private static GPS gps = null;
	private static InputStream in = null;

	//GPS Pin
	private static final byte[] pin = {(byte) '0', (byte) '0', (byte) '0', (byte) '0'};

	//GPS Data
	private static Date connectionMoment;
	private static Date now;
	//private static Satellite ns;
	private static Coordinates origin;
	private static Coordinates current;

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
				try {Thread.sleep(2000);} catch (Exception e) {}
			}
			LCD.refresh();
		}else{
			LCD.drawString("No detected GPS", 0, 3);
			LCD.refresh();
			try {Thread.sleep(2000);} catch (Exception e) {}
		}
		credits(2);
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
		btGPS = Bluetooth.connect(GPSDevice.getDeviceAddr(), NXTConnection.RAW, pin);
		
		if(btGPS == null){
			result  = -1;//No connection
		}else{
			result = 1;//Connection Sucessful
		}

		try{
			in = btGPS.openInputStream();
			gps = new GPS(in);
			//gps.updateValues(true);

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
		//int sentenceCount = 0;
		
		Stopwatch sw;
		sw = new Stopwatch();

		//boolean flag = true;
		int NSAT = 0;
		int GPSDataQuality = 0;
		int checkTime = 10000;
		
		//Circular System
		int GPSScreens = 8;
		int GPSCurrentScreen = 1;

		LCD.drawString(appName + " " + appVersion, 0,0);
		
		//FirstConnection
		boolean firstMomentFlag = false;

		while(!Button.ESCAPE.isPressed()){
			NSAT = gps.getSatellitesTracked();
			GPSDataQuality = Math.round((NSAT * 100)/4);
			
			LCD.drawString("        ", 9, 0);
			LCD.drawString(GPSDataQuality + "%", 9, 0);
			LCD.drawString("OK", 13, 0);
			
			if(sw.elapsed() >= checkTime){
				sw.reset();
				if(GPSDataQuality >=8){
					Sound.twoBeeps();
				}else if(GPSDataQuality >=4){
					Sound.beep();
				}else{
					//Sound.buzz();
				}
			}
			
			if(!firstMomentFlag){
				Date tempDate = gps.getDate();
				int hours = tempDate.getHours();
				int minutes = tempDate.getMinutes();
				int seconds = tempDate.getSeconds();
				connectionMoment = new Date();
				connectionMoment.setHours(hours);
				connectionMoment.setMinutes(minutes);
				connectionMoment.setSeconds(seconds);
				
				
				origin = new Coordinates(gps.getLatitude(),gps.getLongitude(),gps.getAltitude());
				
				//Repeat the operation until you have valid data:
				if(
					(seconds != 0) && 
					(gps.getLatitude() != 0)){
					
					firstMomentFlag = true;
				}
			}
			
			now = gps.getDate();
			current = new Coordinates(gps.getLatitude(),gps.getLongitude(),gps.getAltitude());

			
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

			if(GPSCurrentScreen == 1){
				showGGAUI();
			}else if(GPSCurrentScreen == 2){
				showRMCUI();
			}else if(GPSCurrentScreen == 3){
				showVTGUI();
			}else if(GPSCurrentScreen == 4){
				showGPSTimeUI();
			}else if(GPSCurrentScreen == 5){
				//By Security
				if(gps.getSatellitesTracked() >= 4){
					showSatTableUI();
				}
			}else if(GPSCurrentScreen == 6){
				showSatUI();
			}else if(GPSCurrentScreen == 7){
				showSatIDUI();
			}else if(GPSCurrentScreen == 8){
				showCoordinatesUI();
			}

			LCD.refresh();
			try {Thread.sleep(1000);} catch (Exception e) {}
		}
	}

	/**
	 * Show GGA Basic Data from GPS
	 */
	private static void showGGAUI(){
		refreshSomeLCDLines();
		LCD.drawString("GGA", 0, 2);

		LCD.drawString("Tim " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds() + "", 0, 3);
		LCD.drawString("Lat " + gps.getLatitude(), 0, 4);
		LCD.drawString("" + gps.getLatitudeDirection() , 15, 4);
		LCD.drawString("Lon " + gps.getLongitude(), 0, 5);
		LCD.drawString("" + gps.getLongitudeDirection() , 15, 5);
		LCD.drawString("Alt " + gps.getAltitude(), 0, 6);
		LCD.drawString("Sat " + gps.getSatellitesTracked(), 0, 7);
		LCD.drawString("QOS " + gps.getFixMode(), 6, 7);
		LCD.refresh();
	}

	/**
	 * Show RMC Data from GPS
	 */
	private static void showRMCUI(){
		refreshSomeLCDLines();
		LCD.drawString("RMC", 0, 2);
		
		LCD.drawString("Dat " + now.getDay() + "/" + now.getMonth() + "/" + now.getYear() + "", 0, 3);
		LCD.drawString("Com " + gps.getCompassDegrees(), 0, 4);
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
		LCD.drawString("GPS time data", 0, 2);

		LCD.drawString("Tim " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds() + "", 0, 3);
		LCD.drawString("Dat " + now.getDay() + "/" + now.getMonth() + "/" + now.getYear() + "", 0, 4);
		LCD.refresh();
	}

	private static void showSatTableUI(){
		refreshSomeLCDLines();
		LCD.drawString("Sat table", 0, 2);
		
		
		Satellite ns1 = gps.getSatellite(0);
		Satellite ns2 = gps.getSatellite(1);
		Satellite ns3 = gps.getSatellite(2);
		Satellite ns4 = gps.getSatellite(3);
		
		LCD.drawString(" PRN Ele Azi SRN",0,3);
		LCD.drawString("1 " + ns1.getPRN(),0,4);
		LCD.drawString("2 " + ns2.getPRN(),0,5);
		LCD.drawString("3 " + ns3.getPRN(),0,6);
		LCD.drawString("4 " + ns4.getPRN(),0,7);
		LCD.drawString("" + ns1.getElevation(),5,4);
		LCD.drawString("" + ns2.getElevation(),5,5);
		LCD.drawString("" + ns3.getElevation(),5,6);
		LCD.drawString("" + ns4.getElevation(),5,7);
		LCD.drawString("" + ns1.getAzimuth(),9,4);
		LCD.drawString("" + ns2.getAzimuth(),9,5);
		LCD.drawString("" + ns3.getAzimuth(),9,6);
		LCD.drawString("" + ns4.getAzimuth(),9,7);
		LCD.drawString("" + ns1.getSignalNoiseRatio(),13,4);
		LCD.drawString("" + ns2.getSignalNoiseRatio(),13,5);
		LCD.drawString("" + ns3.getSignalNoiseRatio(),13,6);
		LCD.drawString("" + ns4.getSignalNoiseRatio(),13,7);
		LCD.refresh();
	}

	/**
	 * Show Sat Data
	 */
	private static void showSatUI(){
		refreshSomeLCDLines();
		LCD.drawString("Sat quality data", 0, 2);

		LCD.drawString("Mode " + gps.getSelectionType(), 0, 3);
		LCD.drawString("Value " + gps.getFixType(), 8, 3);
		LCD.drawString("NSat " + gps.getSatellitesTracked(), 0, 4);
		LCD.drawString("PDOP " + gps.getPDOP(), 0, 5);
		LCD.drawString("HDOP " + gps.getHDOP(), 0, 6);
		LCD.drawString("VDOP " + gps.getVDOP(), 0, 7);
		LCD.refresh();
	}

	/**
	 * Show Sat ID
	 */
	private static void showSatIDUI(){
		refreshSomeLCDLines();
		LCD.drawString("Sat detected", 0, 2);

		int SV[] = gps.getPRN();
		
		int cols[] = {0,4,8,12};
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
	 * Show Sat Data
	 */
	private static void showCoordinatesUI(){
		refreshSomeLCDLines();
		LCD.drawString("GPS Session", 0, 2);

		LCD.drawString("Ini " + connectionMoment.getHours() + ":" + connectionMoment.getMinutes() + ":" + connectionMoment.getSeconds() + "", 0, 3);
		LCD.drawString("Now " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds() + "", 0, 4);

		LCD.drawString("Dis " + Math.round((float)origin.distance(current)), 0, 5);
		LCD.drawString("Azi " + Math.round((float)origin.azimuthTo(current)), 0, 6);
		LCD.drawString("Com " + gps.getCompassDegrees(), 0, 7);
		LCD.drawString("N", 8, 6);
		LCD.drawString("N", 8, 7);
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
