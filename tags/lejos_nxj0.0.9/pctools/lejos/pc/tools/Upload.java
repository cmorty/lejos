package lejos.pc.tools;

import java.io.File;
import java.io.IOException;

import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommLoggable;
import lejos.pc.comm.NXTConnector;

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
			File f, String nxtFileName, boolean run) throws NXTNotFoundException, IOException {
		
		// Under some circumstances the filename might be a full package name
		// Remove all but the last two components
		
		if (nxtFileName.length() > NXTCommand.MAX_FILENAMELENGTH) {
			throw new IllegalArgumentException(nxtFileName
					+ ": Filename is more than 20 characters");
		}

		if (protocols == 0)
			protocols = NXTCommFactory.USB | NXTCommFactory.BLUETOOTH;

		boolean connected = fConnector.connectTo(name, address, protocols);
		
		if (!connected)
			throw new NXTNotFoundException(
					"No NXT found - is it switched on and plugged in (for USB)?");
		
		fNXTCommand.setNXTComm(fConnector.getNXTComm());

		log(fNXTCommand.uploadFile(f, nxtFileName));
		
		if (run) {
			fNXTCommand.setVerify(false);
			fNXTCommand.startProgram(nxtFileName);
		}
		
		fNXTCommand.close();
	}
	
	/**
	 * register log listener
	 * 
	 * @param listener
	 */
	public void addLogListener(ToolsLogListener listener) {
		fLogListeners.add(listener);
		fConnector.addLogListener(listener);
	}
	
	/**
	 * unregister log listener
	 * 
	 * @param listener
	 */
	public void removeLogListener(ToolsLogListener listener) {
		fLogListeners.remove(listener);
		fConnector.removeLogListener(listener);
	}
}
