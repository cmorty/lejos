import lejos.nxt.*;
import lejos.nxt.comm.*;

public class BlueStats {
	public static void main(String[] args) {
		String versionString = "Version";
		String nameString = "Name";
		String freeString = "Free mem";
		String visString = "Visibility";
		String statusString = "Status";
		String portString = "Port Open";
		String opString = "Op Mode";

		while(!Button.ESCAPE.isPressed()) {
			byte[] name = Bluetooth.getFriendlyName();
			byte[] version = Bluetooth.getVersion();
			String fn = byteArrayToString(name);
			byte [] connections = Bluetooth.getConnectionStatus();
			LCD.drawString(nameString,0,0);
			LCD.drawString(fn, 5, 0);
			LCD.drawString(versionString,0,1);
			LCD.drawInt(version[0] & 0xFF, 3, 8, 1);
			LCD.drawInt(version[1] & 0xFF, 3, 12, 1);
			for(int i=0;i<4;i++) LCD.drawInt(connections[i], 3, i*4, 2);
			//LCD.drawString(freeString, 0, 2);
			//LCD.drawInt((int) Runtime.getRuntime().freeMemory(), 5, 9, 2);
			LCD.drawString(visString, 0, 3);
			LCD.drawInt(Bluetooth.getVisibility(), 11, 3);
			LCD.drawString(statusString, 0, 4);
			LCD.drawInt(Bluetooth.getStatus() & 0xFF, 7, 4);
			byte [] addr = Bluetooth.getLocalAddress();
			String la = getAddressString(addr);
			LCD.drawString(la, 0, 5);
			LCD.drawString(portString, 0, 6);
			LCD.drawInt(Bluetooth.getPortOpen(), 10, 6);
			LCD.drawString(opString, 0, 7);
			LCD.drawInt(Bluetooth.getOperatingMode(), 8, 7);
			LCD.refresh();			
		} 	
    }
	
	private static String byteArrayToString(byte [] ba) {
		StringBuffer sb = new StringBuffer(ba.length);
		for(int i=0;i<ba.length;i++) {
			sb.append((char) ba[i]);
		}
		return sb.toString();
	}

	private static final char[] cs = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

	private static String getAddressString(byte [] addr) {
		char[] caddr = new char[12];
		
		int ci = 0;
		int nr = 0;
		int addri = 0;
		
		for(int i=0; i<6; i++) {
			addri = (int)addr[i];
			nr = (addri>=0) ? addri : (256 + addri);	
			caddr[ci++] = cs[nr / 16];
			caddr[ci++] = cs[nr % 16];
		}
		return new String(caddr, 0, 12);
	}

}
