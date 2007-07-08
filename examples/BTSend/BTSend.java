
import lejos.pc.comm.*;
import java.io.*;

public class BTSend {
	
	public static void main(String[] args) {
		NXTComm nxtComm = new NXTCommBluez();
		
		NXTInfo[] nxtInfo = nxtComm.search(null,NXTCommand.BLUETOOTH);
		
		if (nxtInfo.length == 0) {
			System.out.println("No NXT Found");
			System.exit(1);
		}

		nxtComm.open(nxtInfo[0]);
		
		InputStream is = nxtComm.getInputStream();
		OutputStream os = nxtComm.getOutputStream();
		
		for(int i=0;i<100;i++) {
			try {
				os.write(i);
				os.flush();
				
			} catch (IOException ioe) {
				System.out.println("IO Exception writing bytes");
			}
			
			try {
				System.out.println("Received " + is.read());
			} catch (IOException ioe) {
				System.out.println("IO Exception reading bytes");
			}
		}
		
		try {
			nxtComm.close();
		} catch (IOException ioe) {}
	}

}
