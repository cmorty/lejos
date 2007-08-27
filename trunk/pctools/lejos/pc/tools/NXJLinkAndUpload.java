package lejos.pc.tools;

import org.apache.commons.cli.CommandLine;
import js.common.CLIToolProgressMonitor;
import js.tinyvm.TinyVM;
import lejos.pc.comm.*;

/**
 * Links and uploads NXJ programs in one call
 * @author Lawrie Griffiths
 *
 */
 public class NXJLinkAndUpload {
	private NXJCommandLineParser fParser;

	public NXJLinkAndUpload() {
		fParser = new NXJCommandLineParser();
	}

	/**
	 * Main entry point for command line usage
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			NXJLinkAndUpload instance = new NXJLinkAndUpload();
			instance.run(args);
		} catch(js.tinyvm.TinyVMException tvexc) {
	         System.err.println("Error: " + tvexc.getMessage());
		}
	}
	
	private void run(String[] args) throws js.tinyvm.TinyVMException {
		// process arguments
		CommandLine commandLine = fParser.parse(args);
		String binName = commandLine.getOptionValue("o");
		boolean run = commandLine.hasOption("r");
		boolean blueTooth = commandLine.hasOption("b");
		boolean usb = commandLine.hasOption("u");
		String name = commandLine.getOptionValue("n");
		String address = commandLine.getOptionValue("d");
		String tinyVMArgs[];
		
		String firstArg = commandLine.getArgs()[0];
        
        int argCount = 0;
        
        // Count the arguments for the linker
		for(int i=0;i<args.length;i++) {
			if (args[i].equals("-b")) continue;
			if (args[i].equals("--bluetooth")) continue;
			if (args[i].equals("-u")) continue;
			if (args[i].equals("--usb")) continue;
			if (args[i].equals("-n")) {i++; continue;}
			if (args[i].equals("--name")) {i++; continue;}
			if (args[i].equals("-d")) {i++; continue;}
			if (args[i].equals("--address")) {i++; continue;}
			if (args[i].equals("-r")) continue;
			if (args[i].equals("--run")) continue;
			argCount++;
		}
		
		// System.out.println("Arg count is " + argCount);
		
		// Build the linker arguments
		int index = 0;
	    tinyVMArgs = new String[argCount+2];
	    
	    if (binName == null) binName = firstArg + ".nxj";
	    
		for(int i=0;i<args.length;i++) {
			if (args[i].equals("-b")) continue;
			if (args[i].equals("--bluetooth")) continue;
			if (args[i].equals("-u")) continue;
			if (args[i].equals("--usb")) continue;
			if (args[i].equals("-n")) {i++; continue;}
			if (args[i].equals("--name")) {i++; continue;}
			if (args[i].equals("-d")) {i++; continue;}
			if (args[i].equals("--address")) {i++; continue;}
			if (args[i].equals("-r")) continue;
			if (args[i].equals("--run")) continue;
			tinyVMArgs[index++] = args[i];
		}
		tinyVMArgs[argCount] = "-o";
		tinyVMArgs[argCount+1] = binName;
 
		// create progress monitor
		CLIToolProgressMonitor monitor = new CLIToolProgressMonitor();
		
		// link
		System.out.println("Linking..."); 
		TinyVM tinyVM = new TinyVM(monitor);
		tinyVM.start(tinyVMArgs);
		
		// upload         
		System.out.println("Uploading...");;
		int protocols = 0;
		
		if (blueTooth) protocols |= NXTCommFactory.BLUETOOTH;
		if (usb) protocols |= NXTCommFactory.USB;
		
		Upload.upload(name, address, protocols, binName, run);
	}
}

