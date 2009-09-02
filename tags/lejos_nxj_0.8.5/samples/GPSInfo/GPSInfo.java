import java.io.*;
import java.util.Date;

import javax.bluetooth.*;
import javax.microedition.io.*;
import javax.microedition.location.*;

import lejos.addon.gps.*;
import lejos.nxt.*;

public class GPSInfo implements GPSListener {

	boolean doneInq = false;
	private static final int GPS_MAJOR = 0x1F00;
	DiscoveryAgent da = null;
	RemoteDevice myBTDevice = null;
	
	private static final int LOCATION_SCREEN = 0;
	private static final int ACCURACY_SCREEN = 1;
	private static final int SATELLITE_SCREEN = 2;
	
	private static int currentScreen = 0;

	private GPS gps = null;
	private static Coordinates target = null;
	
	DiscoveryListener dl = new DiscoveryListener() {
		public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
			if((cod.getMajorDeviceClass() & GPS_MAJOR) == GPS_MAJOR) {
				if(btDevice.isAuthenticated()) { // Check if paired.
					myBTDevice = btDevice;
					da.cancelInquiry(this);
				}
			}
		}

		public void inquiryCompleted(int arg0) {
			doneInq = true;
		}
	};
	
	public GPSInfo() throws LocationException {
		gps = new GPS(getInputStream());
		GPS.addListener(this);
	}
	
	public InputStream getInputStream() throws LocationException {
		try {
			da = LocalDevice.getLocalDevice().getDiscoveryAgent();
			da.startInquiry(DiscoveryAgent.GIAC, dl);
		} catch (BluetoothStateException e) {
			throw new LocationException(e.getMessage());
		}
		while(!doneInq) {Thread.yield();}
		if(myBTDevice == null) throw new LocationException("No device found");
		
		String address = myBTDevice.getBluetoothAddress();
		String btaddy = "btspp://" + address;
		
		try {
			StreamConnectionNotifier scn = (StreamConnectionNotifier)Connector.open(btaddy);
			StreamConnection c = scn.acceptAndOpen();
			return c.openInputStream();
		} catch(IOException e) {
			throw new LocationException(e.getMessage());
		}
	}
	
	public static void main(String [] args) {
		System.out.println("INSTRUCTIONS:");
		System.out.println("Use arrows to \nchange screen");
		System.out.println("Escape exits");
		System.out.println(" ");
		System.out.println("Connecting...");
		
		GPSInfo info = null;
		try {
			info = new GPSInfo();
			System.out.println("Connected");
		} catch (LocationException e) {
			System.out.println(e.getMessage());
			Button.waitForPress();
			System.exit(0);
		}
		
		while(true) {
			int press = Button.waitForPress();
			switch(press) {
			case Button.ID_ESCAPE:
				System.exit(0);
			case Button.ID_RIGHT:
				currentScreen++;
				LCD.clearDisplay();
				System.out.println("Refreshing...");
				break;
			case Button.ID_LEFT:
				currentScreen--;
				LCD.clearDisplay();
				System.out.println("Refreshing...");
				break;
			case Button.ID_ENTER:
				target = new Coordinates(info.gps.getLatitude(),info.gps.getLongitude());
				break;
			}
		}
	}

	public void sentenceReceived(NMEASentence sen) {
		if(sen.getHeader().equals(GGASentence.HEADER) & currentScreen == LOCATION_SCREEN) {
			LCD.clearDisplay();
			LCD.drawString(" GPS FIX DATA " , 0, 0, true);
			LCD.drawString("Lat: " + gps.getLatitude(), 0, 1);
			LCD.drawString("Long: " + gps.getLongitude(), 0, 2);
			LCD.drawString("Alt: " + gps.getAltitude(), 0, 3);
			LCD.drawString("Dir: " + gps.getCourse(), 0, 4);
			LCD.drawString("Speed: " + gps.getSpeed(), 0, 5);
			Coordinates curC = new Coordinates(gps.getLatitude(), gps.getLongitude());
			if(target != null) {
				LCD.drawString("Target: " + curC.distance(target), 0, 6); // TODO: Inverting text screws up
				LCD.drawString("Target Dir: " + curC.azimuthTo(target), 0, 7);
			} else {
				LCD.drawString("Hit ENTER to", 0, 6);
				LCD.drawString("set new target", 0, 7);
			}
			LCD.refresh();
			
			
		} else if(sen.getHeader().equals(GSASentence.HEADER) & currentScreen == ACCURACY_SCREEN) {
			Date date = gps.getDate();
			LCD.clearDisplay();
			
			LCD.drawString(" Accuracy Data ", 0, 0, true);
			LCD.drawString(date.getMonth() + "/" + date.getDay() + "/" + date.getYear(), 0, 1);
			// Grenwich Mean Time. Ensure two-digit segments:
			String minutes = "" + date.getMinutes();
			if(minutes.length() <=1) minutes = "0" + minutes;
			String seconds = "" + date.getSeconds();
			if(seconds.length() <=1) seconds = "0" + seconds;
			LCD.drawString(date.getHours() + ":" + minutes + ":" + seconds + " GMT", 0, 2);
			LCD.drawString("H-Acc (m): " + (gps.getHDOP()*6), 0, 3);
			LCD.drawString("(HDOP:" + gps.getHDOP() + ")", 0, 4);
			LCD.drawString("V-Acc (m): " + (gps.getVDOP()*6), 0, 5);
			LCD.drawString("(VDOP:" + gps.getVDOP() + ")", 0, 6);
			
			LCD.drawString(" Tracked Sats: " + gps.getSatellitesTracked() + " ", 0, 7);
			LCD.refresh();
		} else if(sen.getHeader().equals(GSVSentence.HEADER) & currentScreen >= SATELLITE_SCREEN) {
			LCD.clearDisplay();
			int [] sv = gps.getPRN();
			int svtot=0;
			for(int i=0;i<sv.length;i++) {
				if(sv[i] >=0)
					svtot++;
			}
			//LCD.drawString("SV size: " + svtot, 0, 2);
			
			int satIndex = currentScreen - 2;
			LCD.drawString("Satellite " + (satIndex + 1) + "/" + gps.getSatellitesInView(), 0, 0, true);
			Satellite s = gps.getSatellite(satIndex);
			LCD.drawString("AZ: " + s.getAzimuth(), 0, 2);
			LCD.drawString("Elev: " + s.getElevation(), 0, 3);
			LCD.drawString("PRN: " + s.getPRN(), 0, 4);
			LCD.drawString("Sig/Noise: " + s.getSignalNoiseRatio(), 0, 5);
			LCD.drawString("Tracked? " + s.isTracked(), 0, 6);
			LCD.refresh();
		}
	}
}
