package lejos.nxt.remote;

import lejos.nxt.*;

import java.io.*;
import java.util.ArrayList;
import lejos.nxt.comm.*;

/**
 * 
 * Provides an API similar to the leJOS API for accessing
 * motors, sensors etc. on a remote NXT accessed over
 * Bluetooth using LCP.
 *
 */
public class RemoteNXT {
	
	private NXTCommand nxtCommand = new NXTCommand();
	private NXTComm nxtComm;
	
	public Motor A, B, C; 
	public RemoteBattery Battery;
	public RemoteSensorPort S1, S2, S3, S4;
	
	public RemoteNXT(String name, NXTCommConnector connector) throws IOException {
        nxtComm = new NXTComm(connector);
		boolean open = nxtComm.open(name, NXTConnection.LCP);
		if (!open) throw new IOException("Failed to connect to " + name);
		nxtCommand.setNXTComm(nxtComm);
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
	 * Get the bluetooth address of the remote device
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
	 * Get the free flash memory on the remote NXT
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
	 * Return the (emulated) Lego firmware version on the remote NXT
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
	
	/**
	 * Returns a list of files on NXT brick.
	 * @param searchCriteria "*.*" or [FileName].* or or *.[Extension] or [FileName].[Extension]
	 * @return An array on file names, or NULL if nothing found.
	 */
	// This method could provide file sizes by returning FileInfo objects
	// instead. It's simpler for users to return fileNames.
	public String [] getFileNames(String searchCriteria) {
		try {
			ArrayList names = new ArrayList();
			FileInfo f = nxtCommand.findFirst(searchCriteria);
			if(f == null)
				return null;
			do {
				names.add(f.fileName);
				if(f != null)
					nxtCommand.closeFile(f.fileHandle); // According to protocol, must be closed when done with it.
					f = nxtCommand.findNext(f.fileHandle);
			} while (f != null);
			
			String [] returnArray = new String [names.size()];
			for(int i=0;i<names.size();i++) {
				returnArray[i] = (String) names.get(i);
			}
			return returnArray;
		} catch (IOException ioe) {
			return null;
		}
	}
	
	/**
	 * Returns a list of all files on NXT brick.
	 * @return An array on file names, or NULL if no files found.
	 */
	public String [] getFileNames() {
		return getFileNames("*.*");
	}
	
	/**
	 * Delete a file from the NXT.
	 * @param fileName
	 * @return 0 = success
	 */
	public byte delete(String fileName) {
		try {
			return nxtCommand.delete(fileName);
		} catch (IOException ioe) {
			return -1;
		}	
	}
	
	/**
	 * Starts a Lego executable file on the NXT.
	 * @param fileName
	 * @return the status (0 = success)
	 */
	public byte startProgram(String fileName) {
		try {
			return nxtCommand.startProgram(fileName);
		} catch (IOException ioe) {
			return -1;
		}
	}
	
	/**
	 * Stops the currently running Lego executable on the NXT.
	 * @param fileName
	 * @return the status (0 = success)
	 */
	public byte stopProgram() {
		try {
			return nxtCommand.stopProgram();
		} catch (IOException ioe) {
			return -1;
		}
	}
	
	/**
	 * Retrieves the file name of the Lego executable currently running on the NXT.
	 * @return the status (0 = success)
	 */
	public String getCurrentProgramName() {
		try {
			return nxtCommand.getCurrentProgramName();
		} catch (IOException ioe) {
			return null;
		}
	}
	
	/**
	 * Send a message to a remote inbox
	 * @param message the message
	 * @param inbox the remote inbox
	 * @return the status (0 = success)
	 */
	public int sendMessage(byte [] message, int inbox) {
		try {
			return nxtCommand.messageWrite(message, (byte)inbox);
		} catch (IOException ioe) {
			return -1;
		}
	}
	
	/**
	 * Get a message from a remote index to a local inbox
	 * @param remoteInbox the remote inbox
	 * @param localInbox the local inbox
	 * @param remove true iff the message should be removed from the remote inbox
	 * @return the message or null if mailed
	 */
	public byte [] receiveMessage(int remoteInbox, int localInbox, boolean remove) {
		try {
			return nxtCommand.messageRead((byte)remoteInbox, (byte)localInbox, remove);
		} catch (IOException ioe) {
			return null;
		}
	}
	
	/**
	 * Play a tone on the remote NXT
	 * @param frequency the frequency of the tone
	 * @param duration the duration in milliseconds
	 * @return the status (0 = success)
	 */
	public int playTone(int frequency, int duration) {
		try {
			return nxtCommand.playTone(frequency, duration);
		} catch (IOException ioe) {
			return -1;
		}
	}
	
	/**
	 * Plays a sound file on the remote NXT. 
	 * @param fileName e.g. "Woops.wav"
	 * @param repeat true = repeat, false = play once.
	 * @return If you receive a non-zero number, the filename is probably wrong
	 * or the file is not uploaded to the remote NXT brick.
	 */
	public byte playSoundFile(String fileName, boolean repeat) {
		try {
			return nxtCommand.playSoundFile(fileName, repeat);
		} catch (IOException ioe) {
			return -1;
		}
	}
	
	/**
	 * Plays a sound file on the remote NXT. 
	 * @param fileName e.g. "Woops.wav"
	 * @return If you receive a non-zero number, the filename is probably wrong
	 * or the file is not uploaded to the remote NXT brick.
	 */
	public byte playSoundFile(String fileName) {
		return playSoundFile(fileName, false);
	}
	
	/**
	 * Stops a sound file that has been playing/repeating on the remote NXT.
	 * @return Error code.
	 */
	public int stopSoundPlayback() {
		try {
			return nxtCommand.stopSoundPlayback();
		} catch (IOException ioe) {
			return -1;
		}
	}
}

