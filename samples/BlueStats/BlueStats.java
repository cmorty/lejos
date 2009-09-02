import lejos.nxt.*;
import lejos.nxt.comm.*;

/**
 * BlueStats: display local device Bluetooth information.
 * 
 * @author Lawrie Griffiths
 *
 */
public class BlueStats {
	public static void main(String[] args) {
		String versionString = "BC4 version ";
		String nameString = "Name";
		String visString = "Visibility";
		String statusString = "Status";
		String portString = "Port Open";
		String opString = "Op Mode";
		String addrString = "Adr";
		String connsString = "Conns";

        while(!Button.ESCAPE.isPressed()) {
			byte[] version = Bluetooth.getVersion();
			String fn = Bluetooth.getFriendlyName();
			byte [] connections = Bluetooth.getConnectionStatus();
			String addr = Bluetooth.getLocalAddress();
			
			// Friendly name of local device
			LCD.drawString(nameString,0,0);
			LCD.drawString(fn, 5, 0);
			
			// Version of BlueCore software
			LCD.drawString(versionString + version[0] + "." + version[1],0,1);

			// Local address
            LCD.drawString(addrString,0,2);
			LCD.drawString(addr, 4, 2);
			
			// Visibility
			LCD.drawString(visString, 0, 3);
			LCD.drawInt(Bluetooth.getVisibility(), 11, 3);
			
			// Status byte
			LCD.drawString(statusString, 0, 4);
			LCD.drawInt(Bluetooth.getStatus() & 0xFF, 7, 4);
			
			// Connections
			LCD.drawString(connsString, 0, 5);
			for(int i=0;i<4;i++) LCD.drawInt(connections[i], 2, 5 + i*3, 5);

			// Port open
			LCD.drawString(portString, 0, 6);
			LCD.drawInt(Bluetooth.getPortOpen(), 10, 6);
			
			// Operating mode
			LCD.drawString(opString, 0, 7);
			LCD.drawInt(Bluetooth.getOperatingMode(), 8, 7);
			try {Thread.sleep(100);} catch (InterruptedException ie){}
			LCD.refresh();			
		} 	
    }
}
