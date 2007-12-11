import lejos.pc.comm.*;
import java.io.*;

/**
 * This is a PC sample. It connects to the NXT, and then
 * sends an integer and waits for a reply, 100 times.
 * 
 * Compile this program with javac (not nxjc), and run it 
 * with java.
 * 
 * You need pccomm.jar on the CLASSPATH. On Windows you
 * will also need bluecove.jar on the CLASSPATH. On Linux, 
 * you will need libjbluez.so on the Java library path.
 * 
 * Run the program by:
 * 
 *   java BTSend <name> <address>
 *   
 * where <name> is the name of your NXT, and <address> is
 * its Bluetooth address. 
 * 
 * For example:
 * 
 *   java BTSend NXT 00:16:53:00:78:48
 *   
 * You can find the address for your NXT by running nxjbrowse
 *  - this lists the name and address of each NXT it finds.
 * 
 * See the comment in the code on how to do a Bluetooth 
 * inquiry to find your NXT, instead of using the address
 * parameter.
 * 
 * Your NXT should be running a sample such as BTReceive or
 * SignalTest. Run the NXT program first until it is
 * waiting for a connection, and then run the PC program. 
 * 
 * @author Lawrie Griffiths
 *
 */
public class BTSend {
	
	public static void main(String[] args) {
		NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		
		/* Another way to connect, by discovery:

		NXTInfo[] nxtInfo = nxtComm.search(args[0], NXTCommFactory.BLUETOOTH);
		
		if (nxtInfo.length == 0) {
			System.out.println("No NXT Found");
			System.exit(1);
		}
		*/
		
		// arg[0] = name, e.g NXT
		// arg[1] = address, with optional colons, e.g. 00:16:53:00:78:48
	
		if (args.length != 2) {
			System.out.println("Usage: BTSend name address");
			System.exit(1);
		}
		
		NXTInfo[] nxtInfo = new NXTInfo[1];
			
		nxtInfo[0] = new NXTInfo(args[0],args[1]);
		
		System.out.println("Connecting to " + nxtInfo[0].btResourceString);

		boolean opened = false;
		
		try {
			opened = nxtComm.open(nxtInfo[0]); 
		} catch (NXTCommException e) {
			System.out.println("Exception from open");
		}
		
		if (!opened) {
			System.out.println("Failed to open " + nxtInfo[0].name);
			System.exit(1);
		}
		
		System.out.println("Connected to " + nxtInfo[0].btResourceString);
		
		InputStream is = nxtComm.getInputStream();
		OutputStream os = nxtComm.getOutputStream();
		
		DataOutputStream dos = new DataOutputStream(os);
		DataInputStream dis = new DataInputStream(is);
				
		for(int i=0;i<100;i++) {
			try {
				System.out.println("Sending " + (i*30000));
				dos.writeInt((i*30000));
				dos.flush();			
				
			} catch (IOException ioe) {
				System.out.println("IO Exception writing bytes:");
				System.out.println(ioe.getMessage());
				break;
			}
			
			try {
				System.out.println("Received " + dis.readInt());
			} catch (IOException ioe) {
				System.out.println("IO Exception reading bytes:");
				System.out.println(ioe.getMessage());
				break;
			}
		}
		
		try {
			dis.close();
			dos.close();
			nxtComm.close();
		} catch (IOException ioe) {
			System.out.println("IOException closing connection:");
			System.out.println(ioe.getMessage());
		}
	}
}
