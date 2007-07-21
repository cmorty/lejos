import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.util.*;

public class BTInquiryTest {

	public static void main(String[] args) throws Exception {
		LCD.drawString("Searching ...", 0, 0);
		LCD.refresh();
		
		byte[] cod = {0,0,8,4}; // Toy, Robot
		Vector devList = Bluetooth.inquire(5, 10,cod);

		if (devList.size() > 0) {
			String[] names = new String[devList.size()];
			for (int i = 0; i < devList.size(); i++) {
				BTRemoteDevice btrd = ((BTRemoteDevice) devList.elementAt(i));
				names[i] = btrd.getFriendlyName();
			}
				
			TextMenu menu = new TextMenu(names);
			String[] subItems = {"Add", "Remove"};
			TextMenu subMenu = new TextMenu(subItems,3);
			
			int selected;
			do {
				LCD.clear();
				selected = menu.select();
				if (selected >=0) {
					BTRemoteDevice btrd = ((BTRemoteDevice) devList.elementAt(selected));
					LCD.clear();
					LCD.drawString(names[selected],0,0);
					LCD.drawString(btrd.getAddressString(), 0, 1);
					int subSelection = subMenu.select();
					if (subSelection == 0) Bluetooth.addDevice(btrd);
					else if (subSelection == 1) Bluetooth.removeDevice(btrd);
				}
			} while (selected >= 0);

		} else {
			LCD.clear();
			LCD.drawString("no devices", 0, 0);
			LCD.refresh();
		}
				
		Button.ESCAPE.waitForPressAndRelease();
	}
}
