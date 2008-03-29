package lejos.pc.tools;
import lejos.pc.comm.*;
import java.io.*;
/**
 * Console output monitor class.
 * This class provides access to console output from an nxt program. The program
 * simply writes strings using the nxt RConsole class. These are sent to the
 * PC via the USB (or Bluetooth) connection.
 *
 */ 
public class Console {
	
	public static void main(String[] args) throws Exception {
        NXTCommand nxtCommand = NXTCommand.getSingleton();
		NXTComm nxtComm = null;
		NXTInfo[] nxtInfo = null;
		
        // Locate the nxt either via USB or Bluetooth
        nxtInfo = nxtCommand.search(null, NXTCommFactory.USB | NXTCommFactory.BLUETOOTH);
		if (nxtInfo.length == 0) {
			System.out.println("No NXT Found");
			System.exit(1);
		}
        try {
            nxtComm = NXTCommFactory.createNXTComm(nxtInfo[0].protocol);
            nxtComm.open(nxtInfo[0]);
        } catch (Exception e)
        {
            System.out.println("Failed to connect to NXT");
			System.exit(1);            
        }
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
		} catch (IOException ioe) {System.out.println("Got exception in close");}
	}

}