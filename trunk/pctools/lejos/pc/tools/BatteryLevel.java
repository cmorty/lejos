package lejos.pc.tools;

import lejos.pc.comm.*;
import java.io.*;

public class BatteryLevel {

	public static void main(String[] args) throws IOException {
		System.out.println("Battery Level Test");
		NXTCommand nxtCommand = NXTCommand.getSingleton();
		
		NXTInfo[] nxtInfo = nxtCommand.search(null, NXTCommFactory.USB);
		
		System.out.println("Found " + nxtInfo.length + " NXTs");
		
		if (nxtInfo.length > 0) {
			nxtCommand.open(nxtInfo[0]);
			System.out.println("Battery: " + nxtCommand.getBatteryLevel());
			nxtCommand.close();
		}

	}

}
