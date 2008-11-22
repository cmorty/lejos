package lejos.pc.tools;

import java.io.*;
import lejos.pc.comm.*;

/**
 * Utility class used by the nxj and nxjupload command line tools.
 * It checks the file, connects to the NXT, uploads the file, optionally runs it,
 * and disconnects. This class is also used by the Eclipse plugin.
 * 
 * @author Lawrie Griffiths
 *
 */
public class Upload extends NXTCommLoggable {	
	private NXTCommand fNXTCommand;
	private NXTConnector fConnector;
	
	public Upload() {
		super();
		fConnector = new NXTConnector();
		fNXTCommand = new NXTCommand();
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

		int connected = fConnector.connectTo(name, address, protocols, false);
		
		if (connected != 0)
			throw new NXJUploadException(
					"No NXT found - is it switched on and plugged in (for USB)?");
		
		fNXTCommand.setNXTComm(fConnector.getNXTComm());

		try {
			log(fNXTCommand.uploadFile(f));
			
			if (run) {
				fNXTCommand.setVerify(false);
				fNXTCommand.startProgram(f.getName());
			}
			
			fNXTCommand.close();
		} catch (Throwable t) {
			throw new NXJUploadException("Exception during upload", t);
		}
	}
	
	/**
	 * register log listener
	 * 
	 * @param listener
	 */
	public void addLogListener(ToolsLogListener listener) {
		fLogListeners.add(listener);
		fNXTCommand.addLogListener(listener);
		fConnector.addLogListener(listener);
	}
	
	/**
	 * unregister log listener
	 * 
	 * @param listener
	 */
	public void removeLogListener(ToolsLogListener listener) {
		fLogListeners.remove(listener);
		fNXTCommand.removeLogListener(listener);
		fConnector.removeLogListener(listener);
	}
}
