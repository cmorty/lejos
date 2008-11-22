package lejos.pc.tools;

import lejos.pc.comm.*;
import java.io.*;

/**
 * Console output monitor class.
 * This class provides access to console output from a NXT program. The program
 * simply writes strings using the NXT RConsole class. These are sent to the
 * PC via the USB (or Bluetooth) connection.
 *
 */ 
public class Console {
	public static void main(String[] args) throws Exception {
		NXTConnector conn = new NXTConnector();
		conn.addLogListener(new ToolsLogger());
		int connected = conn.connectTo();
		if (connected != 0) {
			System.err.println("No NXT Found");
			return;
		}
		NXTComm nxtComm = conn.getNXTComm();
        System.out.println("Connected...");
        
        // Send handshake to NXT
        byte [] hello = new byte [] {'C', 'O', 'N'};
        nxtComm.write(hello);
        
        // Process output from the NXT
        InputStream is = nxtComm.getInputStream();
        int input;
		while ((input = is.read()) >= 0) 
           System.out.print((char)input);
		try {
            nxtComm.close();
		} catch (IOException ioe) {System.out.println("Exception in close");}
	}
}