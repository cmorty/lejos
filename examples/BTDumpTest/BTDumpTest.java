
import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.util.*;

public class BTDumpTest {

	public static void main(String[] args) throws Exception {	
		Vector devList = Bluetooth.getKnownDevicesList();
		if (devList.size() > 0) {
			for (int i = 0; i < devList.size(); i++) {
				BTRemoteDevice btrd = ((BTRemoteDevice) devList.elementAt(i));
				LCD.drawString(btrd.getFriendlyName(),0,i*3);
				LCD.drawString(btrd.getAddressString(),0,i*3+1);
				for(int j = 0;j<4; j++) LCD.drawInt(btrd.getDeviceClass()[j],3*j,i*3+2);
				LCD.refresh();
			}

		} else {
			LCD.drawString("no known devices", 0, 0);
			LCD.refresh();
		}
				
		Button.ENTER.waitForPressAndRelease();
	}
}
