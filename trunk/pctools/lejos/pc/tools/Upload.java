package lejos.pc.tools;

import java.io.*;
import lejos.pc.comm.*;

public class Upload {

	public static void upload(String name, String address, int protocols,
			String fileName, boolean run) throws NXJUploadException {

		NXTCommand nxtCommand = NXTCommand.getSingleton();

		File f = new File(fileName);

		if (!f.exists()) {
			throw new NXJUploadException(fileName + ": No such file");
		}

		if (f.getName().length() > 20) {
			throw new NXJUploadException(fileName
					+ ": Filename is more than 20 characters");
		}

		if (protocols == 0)
			protocols = NXTCommFactory.USB | NXTCommFactory.BLUETOOTH;

		NXTInfo[] nxtInfo;

		if (address != null) {
			nxtCommand.setNXTCommBlueTooth();
			nxtInfo = new NXTInfo[1];
			nxtInfo[0] = new NXTInfo((name == null ? "Unknown" : name), address);
		} else {
			nxtInfo = nxtCommand.search(name, protocols);
		}

		boolean connected = false;

		try {
			for (int i = 0; i < nxtInfo.length; i++) {
				connected = nxtCommand.open(nxtInfo[i]);
				if (!connected)
					continue;
				SendFile.sendFile(nxtCommand, f);
				if (run) {
					nxtCommand.setVerify(false);
					nxtCommand.startProgram(f.getName());
				}
				nxtCommand.close();
				break;
			}
			if (!connected)
				throw new NXJUploadException("No NXT found - is it switched on and plugged in (for USB)?");
		} catch (IOException ioe) {
			throw new NXJUploadException("IOException during upload", ioe);
		}
	}
}
