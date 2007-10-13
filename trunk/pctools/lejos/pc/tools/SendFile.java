package lejos.pc.tools;

import lejos.pc.comm.*;
import java.io.*;

public class SendFile {

	public static String sendFile(NXTCommand nxtCommand, File file) throws IOException {
	    byte[] data = new byte[60];
	    int len, sent = 0;
	    FileInputStream in = null;

	    long millis = System.currentTimeMillis();
	    
	    //System.out.println("Filename is " + file.getName());

	    try {
	      in = new FileInputStream(file);
	    } catch (FileNotFoundException e) {
	    	throw new IOException("File not found");
	    }

	    nxtCommand.openWrite(file.getName(), (int) file.length());

	    try {
	      while ((len = in.read(data)) > 0) {
	        byte[] sendData = new byte[len];
	        for(int i=0;i<len;i++) sendData[i] = data[i];
	        // System.out.println("Sending " + len + " bytes");
	        sent += len;
	        nxtCommand.writeFile((byte) 0,sendData); // Handles not yet used
	      }
	    } catch (IOException ioe) {
	    	throw new IOException("Failed to upload");
	    }
	    //System.out.println("Sent " + sent + " bytes");
	    nxtCommand.setVerify(true);
	    nxtCommand.closeFile((byte) 0);
	    return "Upload successful in " + (System.currentTimeMillis() - millis) + " milliseconds";
	}
}
