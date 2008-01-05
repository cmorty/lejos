package lejos.nxt.remote;

import lejos.nxt.comm.*;
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
		A.regulateSpeed(false);
		A.shutdown();
		B = new Motor(new RemoteMotorPort(nxtCommand,1));
		B.regulateSpeed(false);
		B.shutdown();
		C = new Motor(new RemoteMotorPort(nxtCommand,2));
		C.regulateSpeed(false);
		C.shutdown();
		Battery = new RemoteBattery(nxtCommand);
		S1 = new RemoteSensorPort(nxtCommand, 0);
		S2 = new RemoteSensorPort(nxtCommand, 1);
		S3 = new RemoteSensorPort(nxtCommand, 2);
		S4 = new RemoteSensorPort(nxtCommand, 3);
	}
	
	/**
	 * Get the  name of the remote brick
	 * 
	 * @return name of remote brick
	 */
	public String getBrickName()  {
		try {
			DeviceInfo i = nxtCommand.getDeviceInfo();
			return i.NXTname;
		} catch (IOException ioe) {
			return null;
		}	
	}
	
	/**
	 * Get the bluetooth address of the remorte device
	 * 
	 * @return address with hex pairs separated by colons
	 */
	public String getBluetoothAddress()  {
		try {
			DeviceInfo i = nxtCommand.getDeviceInfo();
			return i.bluetoothAddress;
		} catch (IOException ioe) {
			return null;
		}	
	}
	
	/**
	 * 
	 * @return Free memory remaining in FLASH
	 */
	public int getFlashMemory() {
		try {
			DeviceInfo i = nxtCommand.getDeviceInfo();
			return i.freeFlash; 
		} catch (IOException ioe) {
			return 0;
		}	
	}
	
	/**
	 * Return Lego firmware vserion
	 * 
	 * @return <major>.<minor>
	 */
	public String getFirmwareVersion() {
		try {
			FirmwareInfo f = nxtCommand.getFirmwareVersion();
			return f.firmwareVersion;
		} catch (IOException ioe) {
			return null;
		}		
	}
	
	/**
	 * Return LCP protocol version
	 * 
	 * @return <major>.<minor>
	 */
	public String getProtocolVersion() {
		try {
			FirmwareInfo f = nxtCommand.getFirmwareVersion();
			return f.protocolVersion;
		} catch (IOException ioe) {
			return null;
		}		
	}
	
	/**
	 * Deletes all user programs and data in FLASH memory
	 * @return zero for success
	 */
	public byte deleteFlashMemory() {
		try {
			return nxtCommand.deleteUserFlash(); 
		} catch (IOException ioe) {
			return -1;
		}
	}
}

