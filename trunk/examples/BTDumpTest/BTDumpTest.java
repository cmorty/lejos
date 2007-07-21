import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.util.*;

public class BTDumpTest {

	public static void main(String[] args) throws Exception {	
		Vector devList = Bluetooth.getKnownDevicesList();
		if (devList.size() > 0) {
			String[] names = new String[devList.size()];
			for (int i = 0; i < devList.size(); i++) {
				BTRemoteDevice btrd = ((BTRemoteDevice) devList.elementAt(i));
				names[i] = btrd.getFriendlyName();
			}
				
			TextMenu menu = new TextMenu(names);
			String[] subItems = {"Remove"};
			TextMenu subMenu = new TextMenu(subItems,4);

			int selected;
			do {
				LCD.clear();
				selected = menu.select();
				if (selected >=0) {
					BTRemoteDevice btrd = ((BTRemoteDevice) devList.elementAt(selected));
					LCD.clear();
					LCD.drawString(names[selected],0,0);
					LCD.drawString(btrd.getAddressString(), 0, 1);
					for(int i=0;i<4;i++) LCD.drawInt(btrd.getDeviceClass()[i], 3, i*4, 2);
					int subSelection = subMenu.select();
					if (subSelection == 0) Bluetooth.removeDevice(btrd);
				}
			} while (selected >= 0);

		} else {
			LCD.drawString("no known devices", 0, 0);
			LCD.refresh();
		}
	}
}
