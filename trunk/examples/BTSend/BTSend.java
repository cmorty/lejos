
import lejos.pc.comm.*;
import java.io.*;

public class BTSend {
	
	public static void main(String[] args) {
		NXTCommBluecove nxtComm = new NXTCommBluecove();
		
		NXTInfo[] nxtInfo = nxtComm.search(null,NXTCommand.BLUETOOTH);
		
		if (nxtInfo.length == 0) {
			System.out.println("No NXT Found");
			System.exit(1);
		}

		nxtComm.open(nxtInfo[0]);
		
		OutputStream os = nxtComm.getOutputStream();
		
		for(int i=0;i<100;i++) {
			try {
				os.write(99);
				os.flush();
			} catch (IOException ioe) {
				System.out.println("IO Exception writing bytes");
			}
		}
		
		nxtComm.close();
	}

}
