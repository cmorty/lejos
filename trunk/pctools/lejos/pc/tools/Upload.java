package lejos.pc.tools;

import java.io.*;
import lejos.pc.comm.*;

public class Upload {
	
	public static void upload(String name, String address, int protocols, String fileName, boolean run) {

		NXTCommand nxtCommand = NXTCommand.getSingleton();
		
		File f = new File(fileName);
		
		if (!f.exists()) {
			System.err.println("Error: No such file");
			System.exit(1);
		}
		
		if (f.getName().length() > 20) {
			System.err.println("Filename is more than 20 characters");
			System.exit(1);
		}
		
		if (protocols == 0) protocols = NXTCommFactory.USB | NXTCommFactory.BLUETOOTH;
		
		NXTInfo[] nxtInfo;
		
		if (address != null) {
			nxtCommand.setNXTCommBlueTooth();
			nxtInfo = new NXTInfo[1];
			nxtInfo[0] = new NXTInfo((name == null ? "Unknown" : name),address);	
			//System.out.println("Address: " + address);
		} else {
			nxtInfo = nxtCommand.search(name, protocols);
		}
		
		//System.out.println("Found " + nxtInfo.length + " NXTs");
		
		boolean connected = false;
		
		try {
			for(int i=0;i<nxtInfo.length;i++) {
				connected = nxtCommand.open(nxtInfo[i]);
				if (!connected) continue;
				SendFile.sendFile(nxtCommand, f);
				if (run) {
					nxtCommand.setVerify(false);
					nxtCommand.startProgram(f.getName());
				}
				nxtCommand.close();
				break;
			} 
			if (!connected) System.err.println("No NXT found - is it switched on and plugged in (for USB)?");
		} catch (IOException ioe) {
			System.err.println("IOException during upload");
		}
	}
}
