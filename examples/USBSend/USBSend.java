import lejos.pc.comm.*;
import java.io.*;
 
/**
 * This is a PC sample. It connects to the NXT, and then
 * sends an integer and waits for a reply, 100 times.
 * 
 * Compile this program with javac (not nxjc), and run it 
 * with java.
 * 
 * You need pccomm.jar on the CLASSPATH and the jlibnxt
 * DLL or shared library on the Java library path.
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
		NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
		
		NXTInfo[] nxtInfo = null;
		
		try {
			nxtInfo = nxtComm.search(null, NXTCommFactory.USB);
		} catch (NXTCommException e) {
			System.out.println("Exception in search");
		}
		
		if (nxtInfo.length == 0) {
			System.out.println("No NXT Found");
			System.exit(1);
		}

		try {
			nxtComm.open(nxtInfo[0]);
		} catch (NXTCommException e) {
			System.out.println("Exception in open");
		}
		
		InputStream is = nxtComm.getInputStream();
		OutputStream os = nxtComm.getOutputStream();
		DataInputStream inDat = new DataInputStream(is);
		DataOutputStream outDat = new DataOutputStream(os);
		int x = 0;
		for(int i=0;i<100;i++) 
		{
			try {
			   outDat.writeInt(i);
			   outDat.flush();
	
			} catch (IOException ioe) {
				System.out.println("IO Exception writing bytes");
			}
	         try {x = inDat.readInt();}
	         catch (IOException ioe) {
	           System.out.println("IO Exception reading reply");
	         }            
	       System.out.println("Sent "+i+ " Received "+x);
		}
		
		try {
			inDat.close();
			outDat.close();
		} catch (IOException ioe) {
			System.out.println("IO Exception Closing connection");
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ie) {}
		
		try {
			nxtComm.close();
		} catch (IOException ioe) {}
	}

}