
import lejos.pc.comm.*;
import java.io.*;

public class BTSend {
	
	public static void main(String[] args) {
		NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		
		/*NXTInfo[] nxtInfo = nxtComm.search(args[0], NXTCommFactory.BLUETOOTH);
		
		if (nxtInfo.length == 0) {
			System.out.println("No NXT Found");
			System.exit(1);
		}*/
		
		NXTInfo[] nxtInfo = new NXTInfo[1];
		
		nxtInfo[0] = new NXTInfo("NOISY","00:16:53:00:78:48");
		
		System.out.println("Connecting to " + nxtInfo[0].btResourceString);

		boolean opened = nxtComm.open(nxtInfo[0]); 
		
		if (!opened) {
			System.out.println("Failed to open " + nxtInfo[0].name);
			System.exit(1);
		}
		
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
