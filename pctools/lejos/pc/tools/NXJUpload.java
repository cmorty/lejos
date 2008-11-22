package lejos.pc.tools;

import lejos.pc.comm.*;
import org.apache.commons.cli.CommandLine;

/**
 * Command-line utility to upload a linked binary to the NXT.
 * 
 * @author Lawrie Griffiths
 *
 */
public class NXJUpload extends NXTCommLoggable {
	private NXJUploadCommandLineParser fParser;
	private Upload fUpload;

	/** 
	 * Create a NXJUpload object
	 */
	public NXJUpload() {
		super();
		fParser = new NXJUploadCommandLineParser();
		fUpload = new Upload(); 
	}

	/**
	 * Main entry point
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		try {
			NXJUpload instance = new NXJUpload();
			instance.addLogListener(new ToolsLogger());
			instance.run(args);
		} catch(Throwable t) {
			System.err.println("An error occurred: " + t.getMessage());
		}
	}
	
	/** 
	 * Run the utility.
	 * Note that this method can be called from other tools such as the Eclipse plug-in.
	 * 
	 * @param args the command line arguments
	 * 
	 * @throws js.tinyvm.TinyVMException
	 * @throws NXJUploadException
	 */
	public void run(String[] args) throws js.tinyvm.TinyVMException, NXJUploadException {
		int protocols = 0;
		
		CommandLine commandLine = fParser.parse(args);
		boolean run = commandLine.hasOption("r");
		boolean blueTooth = commandLine.hasOption("b");
		boolean usb = commandLine.hasOption("u");
		String name = commandLine.getOptionValue("n");
		String address = commandLine.getOptionValue("d");
		
		String fileName = commandLine.getArgs()[0];
		
		if (blueTooth) protocols |= NXTCommFactory.BLUETOOTH;
		if (usb) protocols |= NXTCommFactory.USB;
		
		fUpload.upload(name, address, protocols, fileName, run);
	}	
	
	/**
	 * Register log listener
	 * 
	 * @param listener
	 */
	public void addLogListener(ToolsLogListener listener) {
		fLogListeners.add(listener);
		fUpload.addLogListener(listener);
	}
	
	/**
	 * Unregister log listener
	 * 
	 * @param listener
	 */
	public void removeLogListener(ToolsLogListener listener) {
		fLogListeners.remove(listener);
		fUpload.removeLogListener(listener);
	}
}
