import java.io.IOException;

import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;

public class BTConnectTest {
	public static void main(String[] args) throws Exception {
		String name = "NXT";
		
		LCD.drawString("Connecting...", 0, 0);
		LCD.refresh();
		
		BTRemoteDevice btrd = Bluetooth.getKnownDevice(name);

		if (btrd == null) {
			LCD.clear();
			LCD.drawString("No such device", 0, 0);
			LCD.refresh();
			Button.ESCAPE.waitForPressAndRelease();
			System.exit(1);
		}
		
		BTConnection btc = Bluetooth.connect(btrd);
		
		if (btc == null) {
			LCD.clear();
			LCD.drawString("Connect fail", 0, 0);
			LCD.refresh();
			Button.ESCAPE.waitForPressAndRelease();
			System.exit(1);
		}
		
		LCD.clear();
		LCD.drawString("Connected", 0, 0);
		LCD.refresh();
		
		DataInputStream dis = btc.openDataInputStream();
		DataOutputStream dos = btc.openDataOutputStream();
				
		for(int i=0;i<100;i++) {
			try {
				LCD.drawInt(i*30000, 0, 2);
				LCD.refresh();
				dos.writeInt(i*30000);
				dos.flush();			
			} catch (IOException ioe) {
				LCD.drawString("Exception", 0, 0);
				LCD.refresh();
			}
			
			try {
				LCD.drawInt(dis.readInt(),0,3);
				LCD.refresh();
			} catch (IOException ioe) {
			}
		}
		
		try {
			dis.close();
			dos.close();
			btc.close();
		} catch (IOException ioe) {
		}
		
		Button.ESCAPE.waitForPressAndRelease();
	}
}
