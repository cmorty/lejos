package lejos.nxt.comm;

import lejos.nxt.*;
import java.io.*;

public class RemoteNXT {
	
	private NXTCommand nxtCommand = new NXTCommand();
	
	public Motor A, B, C; 
	public RemoteBattery Battery;
	public RemoteSensorPort S1, S2, S3, S4;
	
	public RemoteNXT(String name) throws IOException {
		nxtCommand.open(name);
		//nxtCommand.setVerify(true);
		A =  new Motor(new RemoteMotorPort(nxtCommand,0));
		A.shutdown();
		B = new Motor(new RemoteMotorPort(nxtCommand,1));
		B.shutdown();
		C = new Motor(new RemoteMotorPort(nxtCommand,2));
		C.shutdown();
		Battery = new RemoteBattery(nxtCommand);
		S1 = new RemoteSensorPort(nxtCommand, 0);
		S2 = new RemoteSensorPort(nxtCommand, 1);
		S3 = new RemoteSensorPort(nxtCommand, 2);
		S4 = new RemoteSensorPort(nxtCommand, 3);
	}
}
