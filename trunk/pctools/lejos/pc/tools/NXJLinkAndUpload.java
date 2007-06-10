package lejos.pc.tools;

import java.io.File;
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
	private static NXTCommand nxtCommand = null;
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
		String name = commandLine.getOptionValue("name");
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
		String baseFileName = null;
		int protocols = 0;
		
		if (blueTooth) protocols |= NXTCommand.BLUETOOTH;
		if (usb) protocols |= NXTCommand.USB;
				
		int i;
		for (i=binName.length()-1;i>0;i--) {
			char c = binName.charAt(i);
			if (c == '\\' || c == '/') break;
		}
		
		if (i != 0) i++;
		
		baseFileName = binName.substring(i);
		
		//System.out.println("Base file name is " + baseFileName);
		
		File f = new File(binName);
		
		if (!f.exists()) {
			System.err.println("No such file");
			System.exit(1);
		}
		
		if (protocols == 0) protocols = NXTCommand.USB | NXTCommand.BLUETOOTH;
		
		nxtCommand = NXTCommand.getSingleton();
		
		NXTInfo[] nxtInfo = nxtCommand.search(name, protocols);
		
		//System.out.println("Found " + nxtInfo.length + " NXTs");
		
		if (nxtInfo.length > 0) {
			nxtCommand.open(nxtInfo[0]);
			SendFile.sendFile(nxtCommand, f);
			if (run) {
				nxtCommand.setVerify(false);
				nxtCommand.startProgram(baseFileName);
			}
			nxtCommand.close();
		} else System.out.println("No NXT found - is it switched on and plugged in (for USB)?");
	}
}

