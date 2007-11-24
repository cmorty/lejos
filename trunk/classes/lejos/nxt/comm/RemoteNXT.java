package lejos.nxt.comm;

import lejos.nxt.*;
import java.io.*;

public class RemoteNXT {
	
	private NXTCommand nxtCommand = new NXTCommand();
	
	public Motor A, B, C; 
	
	public RemoteNXT(String name) throws IOException {
		nxtCommand.open(name);
		//nxtCommand.setVerify(true);
		A =  new Motor(new RemoteMotorPort(nxtCommand,0));
		A.shutdown();
		B = new Motor(new RemoteMotorPort(nxtCommand,1));
		B.shutdown();
		C = new Motor(new RemoteMotorPort(nxtCommand,2));
		C.shutdown();
	}
}
