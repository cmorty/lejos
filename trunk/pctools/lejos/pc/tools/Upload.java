package lejos.pc.tools;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

import lejos.pc.comm.*;

public class Upload {
	
	private Collection<ToolsLogListener> fLogListeners;
	private NXTCommand fNXTCommand;
	
	public Upload() {
		fLogListeners = new ArrayList<ToolsLogListener>();
		fNXTCommand = NXTCommand.getSingleton();
	}

	public void upload(String name, String address, int protocols,
			String fileName, boolean run) throws NXJUploadException {

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
			fNXTCommand.setNXTCommBlueTooth();
			nxtInfo = new NXTInfo[1];
			nxtInfo[0] = new NXTInfo((name == null ? "Unknown" : name), address);
		} else {
			try {
				nxtInfo = fNXTCommand.search(name, protocols);
			} catch (Throwable t) {
				throw new NXJUploadException(t);
			}
		}

		boolean connected = false;

		try {
			for (int i = 0; i < nxtInfo.length; i++) {
				connected = fNXTCommand.open(nxtInfo[i]);
				if (!connected)
					continue;
				String result = SendFile.sendFile(fNXTCommand, f);
				for (ToolsLogListener listener : fLogListeners) {
					listener.logEvent(result);
				}
				if (run) {
					fNXTCommand.setVerify(false);
					fNXTCommand.startProgram(f.getName());
				}
				fNXTCommand.close();
				break;
			}
		} catch (Throwable t) {
			throw new NXJUploadException("Exception during upload", t);
		}
		if (!connected)
			throw new NXJUploadException(
					"No NXT found - is it switched on and plugged in (for USB)?");
	}
	
	/**
	 * register log listener
	 * 
	 * @param listener
	 */
	public void addLogListener(ToolsLogListener listener) {
		fLogListeners.add(listener);
		fNXTCommand.addLogListener(listener);
	}
	
	/**
	 * unregister log listener
	 * 
	 * @param listener
	 */
	public void removeLogListener(ToolsLogListener listener) {
		fLogListeners.remove(listener);
		fNXTCommand.removeLogListener(listener);
	}

}
