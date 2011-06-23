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
	private NXTConnector fConnector;
	
	public Upload() {
		super();
		fConnector = new NXTConnector();
	}

	public void upload(String name, String address, int protocols,
			File f, String nxtFileName, boolean run) throws NXTNotFoundException, IOException {
		
		if (nxtFileName.length() > NXTCommand.MAX_FILENAMELENGTH) {
			throw new IllegalArgumentException(nxtFileName
					+ ": Filename is more than 20 characters");
		}

		if (protocols == 0)
			protocols = NXTCommFactory.USB | NXTCommFactory.BLUETOOTH;

		boolean connected = fConnector.connectTo(name, address, protocols);		
		if (!connected)
			throw new NXTNotFoundException("No NXT found - is it switched on and plugged in (for USB)?");
		
		try
		{
			NXTCommand nxtCommand = new NXTCommand();
			nxtCommand.setNXTComm(fConnector.getNXTComm());
	
			log(nxtCommand.uploadFile(f, nxtFileName));
			
			if (run)
			{
				nxtCommand.setVerify(false);
				nxtCommand.startProgram(nxtFileName);
			}
			else
			{
				nxtCommand.disconnect();
			}			
		}
		finally
		{
			fConnector.close();
		}
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
