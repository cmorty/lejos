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
			NXTCommand nxtCommand = new NXTCommand(fConnector.getNXTComm());
	
			log(nxtCommand.uploadFile(f, nxtFileName));
			
			if (run)
			{
				nxtCommand.setVerify(false);
				nxtCommand.startProgram(nxtFileName);
			}
			else
			{
				nxtCommand.disconnect();
				
				//TODO remove this and remove all code that causes data loss on NXT and PC-side. 
				// Give the NXT some time to recover to avoid a race.
				// Presumably, this is needed since the NXT flushes some buffers or disables and
				// re-enables USB after a disconnect. This causes the next
				// "what's your name?"-command to be ignored.
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// ignore
				}
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
