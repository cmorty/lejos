package lejos.pc.tools;

import lejos.pc.comm.*;
import java.io.*;

public class NXJUpload {
	private static NXTCommand nxtCommand = null;

	public static void main(String[] args) {
		boolean run = false, showUsage = false, invalidFlag = false;
		String name = null, arg = null, fileName = null;
		String baseFileName = null;
		int protocols = 0;
		
		for(int i=0;i<args.length;i++) {
			arg = args[i];
			//System.out.println("Parameter " + i + " = " + arg);
			if (arg.charAt(0) == '-') { // flags
				if (arg.length() != 2) invalidFlag = true;
				else {
					char flagChar = arg.charAt(1);
					if (flagChar == 'r') run = true;
					else if (flagChar == 'u') protocols |= NXTCommand.USB;
					else if (flagChar == 'b') protocols |= NXTCommand.BLUETOOTH;
					else if (flagChar == 'n') {
						if (i == args.length-1) {
							showUsage = true;
							break;
						} else {
							name = args[++i];
						}	
					} else {
						invalidFlag = true;
						break;
					}
				}
			} else { // not a flag - must be filename
				if (fileName != null) {
					showUsage = true;
					break;
				}
				fileName = arg;
			}
		}
		
		if (args.length == 0 || showUsage || fileName == null) {
			System.err.println("Usage: nxjupload [-u] [-b] [-r] [-n <name>] filename");
			System.exit(1);
		}
		
		if (invalidFlag) {
			System.err.println("Invalid flag: " + arg);
			System.exit(1);
		}
		
		int i;
		for (i=fileName.length()-1;i>0;i--) {
			char c = fileName.charAt(i);
			if (c == '\\' || c == '/') break;
		}
		
		if (i != 0) i++;
		
		baseFileName = fileName.substring(i);
		
		//System.out.println("Base file name is " + baseFileName);
		
		File f = new File(fileName);
		
		if (!f.exists()) {
			System.err.println("No such file");
			System.exit(1);
		}
		
		if (protocols == 0) protocols = NXTCommand.USB | NXTCommand.BLUETOOTH;
		
		nxtCommand = NXTCommand.getSingleton(protocols);
		
		NXTInfo[] nxtInfo = nxtCommand.search(name, protocols);
		
		System.out.println("Found " + nxtInfo.length + " NXTs");
		
		if (nxtInfo.length > 0) {
			nxtCommand.open(nxtInfo[0]);
			SendFile.sendFile(nxtCommand, f, baseFileName);
			if (run) {
				nxtCommand.setVerify(false);
				nxtCommand.startProgram(baseFileName);
			}
			nxtCommand.close();
		}
	}
}
