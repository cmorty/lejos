package lejos.pc.tools;

import lejos.pc.comm.*;

import java.io.*;

import org.apache.commons.cli.CommandLine;

public class NXJUpload {
	private static NXTCommand nxtCommand = null;
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

		File f = new File(fileName);
		
		if (!f.exists()) {
			System.err.println("Error: No such file");
			System.exit(1);
		}
		
		if (protocols == 0) protocols = NXTCommand.USB | NXTCommand.BLUETOOTH;
		
		nxtCommand = NXTCommand.getSingleton();
		
		NXTInfo[] nxtInfo = nxtCommand.search(name, protocols);
		
		//System.out.println("Found " + nxtInfo.length + " NXTs");
		
		boolean connected = false;
		
		for(int i=0;i<nxtInfo.length;i++) {
			connected = nxtCommand.open(nxtInfo[i]);
			if (!connected) continue;
			SendFile.sendFile(nxtCommand, f);
			if (run) {
				nxtCommand.setVerify(false);
				nxtCommand.startProgram(f.getName());
			}
			nxtCommand.close();
			break;
		} 
		if (!connected) System.out.println("No NXT found - is it switched on and plugged in (for USB)?");
	}
}
