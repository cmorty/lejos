package lejos.pc.tools;

import java.util.Collection;

import lejos.pc.comm.*;
import org.apache.commons.cli.CommandLine;

public class NXJUpload {

	private Collection<ToolsLogListener> fLogListeners;
	private NXJUploadCommandLineParser fParser;
	private Upload fUpload;

	public NXJUpload() {
		fParser = new NXJUploadCommandLineParser();
		fUpload = new Upload(); 
	}

	public static void main(String[] args) {
		try {
			NXJUpload instance = new NXJUpload();
			instance.addLogListener(new ToolsLogger());
			instance.run(args);
		} catch(Throwable t) {
			System.err.println("leJOSNXJ> an error occurred: " + t.getMessage());
		}
	}
	
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
	 * register log listener
	 * 
	 * @param listener
	 */
	public void addLogListener(ToolsLogListener listener) {
		fLogListeners.add(listener);
		fUpload.addLogListener(listener);
	}
	
	/**
	 * unregister log listener
	 * 
	 * @param listener
	 */
	public void removeLogListener(ToolsLogListener listener) {
		fLogListeners.remove(listener);
		fUpload.removeLogListener(listener);
	}

}
