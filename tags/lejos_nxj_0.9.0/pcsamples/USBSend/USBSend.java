import lejos.pc.comm.*;

import java.io.*;
 
/**
 * This is a PC sample. It connects to the NXT, and then
 * sends an integer and waits for a reply, 100 times.
 * 
 * Compile this program with javac (not nxjc), and run it 
 * with java.
 * 
 * You need pccomm.jar on the CLASSPATH and the jfantom.dll
 * DLL or liblibnxt.so shared library on the Java library path.
 * 
 * Run the program by:
 * 
 *   java USBSend
 * 
 * Your NXT should be running a sample such as USBReceive. 
 * 
 * @author Lawrie Griffiths
 *
 */
public class USBSend {	
	public static void main(String[] args) {
		NXTConnector conn = new NXTConnector();
		
		conn.addLogListener(new NXTCommLogListener(){

			public void logEvent(String message) {
				System.out.println("USBSend Log.listener: "+message);
				
			}

			public void logEvent(Throwable throwable) {
				System.out.println("USBSend Log.listener - stack trace: ");
				 throwable.printStackTrace();
				
			}
			
		} 
		);
		
		if (!conn.connectTo("usb://")){
			System.err.println("No NXT found using USB");
			System.exit(1);
		}
		
		DataInputStream inDat = conn.getDataIn();
		DataOutputStream outDat = conn.getDataOut();
		
		int x = 0;
		for(int i=0;i<100;i++) 
		{
			try {
			   outDat.writeInt(i);
			   outDat.flush();
	
			} catch (IOException ioe) {
				System.err.println("IO Exception writing bytes");
			}
	        
			try {
	        	 x = inDat.readInt();
	        } catch (IOException ioe) {
	           System.err.println("IO Exception reading reply");
	        }            
	        System.out.println("Sent " +i + " Received " + x);
		}
		
		try {
			inDat.close();
			outDat.close();
			System.out.println("Closed data streams");
		} catch (IOException ioe) {
			System.err.println("IO Exception Closing connection");
		}
		
		try {
			conn.close();
			System.out.println("Closed connection");
		} catch (IOException ioe) {
			System.err.println("IO Exception Closing connection");
		}
	}
}