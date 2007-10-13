import lejos.pc.comm.*;
import java.io.*;

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
		
		for(int i=0;i<100;i++) {
			try {
				os.write(i);
				os.flush();
				System.out.println("Received " + is.read());
			} catch (IOException ioe) {
				System.out.println("IO Exception writing bytes");
			}
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ioe) {}
		
		try {
			nxtComm.close();
		} catch (IOException ioe) {}
	}

}
