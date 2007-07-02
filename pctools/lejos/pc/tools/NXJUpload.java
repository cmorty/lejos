package lejos.pc.tools;

import lejos.pc.comm.*;
import org.apache.commons.cli.CommandLine;

public class NXJUpload {
	private NXJUploadCommandLineParser fParser;

	public NXJUpload() {
		fParser = new NXJUploadCommandLineParser();
	}

	public static void main(String[] args) {
		try {
			NXJUpload instance = new NXJUpload();
			instance.run(args);
		} catch(js.tinyvm.TinyVMException tvexc) {
	         System.err.println("Error: " + tvexc.getMessage());
		}
	}
	
	public void run(String[] args) throws js.tinyvm.TinyVMException {
		int protocols = 0;
		
		CommandLine commandLine = fParser.parse(args);
		boolean run = commandLine.hasOption("r");
		boolean blueTooth = commandLine.hasOption("b");
		boolean usb = commandLine.hasOption("u");
		String name = commandLine.getOptionValue("n");
		
		String fileName = commandLine.getArgs()[0];
		
		if (blueTooth) protocols |= NXTCommand.BLUETOOTH;
		if (usb) protocols |= NXTCommand.USB;
		
		Upload.upload(name, protocols, fileName, run);
	}	
}
