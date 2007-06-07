package lejos.pc.tools;

import lejos.pc.comm.*;
import java.io.*;

public class SendFile {

	public static void sendFile(NXTCommand nxtCommand, File file, String baseFileName) {
	    byte[] data = new byte[60];
	    int len, sent = 0;
	    FileInputStream in = null;

	    long millis = System.currentTimeMillis();
	    
	    //System.out.println("Filename is " + file.getName());

	    try {
	      in = new FileInputStream(file);
	    } catch (FileNotFoundException e) {
	    	System.out.println("File not found");
	    }

	    nxtCommand.openWrite(baseFileName, (int) file.length());

	    try {
	      while ((len = in.read(data)) > 0) {
	        byte[] sendData = new byte[len];
	        for(int i=0;i<len;i++) sendData[i] = data[i];
	        // System.out.println("Sending " + len + " bytes");
	        sent += len;
	        nxtCommand.writeFile((byte) 0,sendData); // Handles not yet used
	      }
	    } catch (IOException ioe) {
	    	System.out.println("Failed to upload");
	    	System.exit(1);
	    }
	    //System.out.println("Sent " + sent + " bytes");
	    nxtCommand.setVerify(true);
	    nxtCommand.closeFile((byte) 0);
	    System.out.println("Upload successful in " + (System.currentTimeMillis() - millis) + " milliseconds");
	}
}
