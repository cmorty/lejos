package lejos.pc.comm.nxt;

import lejos.pc.comm.*;
import java.io.*;

public class NXT {
	
	private static NXTCommand nxtCommand = NXTCommand.getSingleton();
			
	public static float getFirmwareVersion() {
		try {
			FirmwareInfo f = nxtCommand.getFirmwareVersion();
			return Float.parseFloat(f.firmwareVersion);
		} catch (IOException ioe) {
			return 0;
		}		
	}
	
	public static float getProtocolVersion() {
		try {
			FirmwareInfo f = nxtCommand.getFirmwareVersion();
			return Float.parseFloat(f.protocolVersion);
		} catch (IOException ioe) {
			return 0;
		}	
	}
	
	/**
	 * 
	 * @return Free memory remaining in FLASH
	 */
	public static int getFlashMemory() {
		try {
			DeviceInfo i = nxtCommand.getDeviceInfo();
			return i.freeFlash;
		} catch (IOException ioe) {
			return 0;
		}	 
	}
	
	/**
	 * Deletes all user programs and data in FLASH memory
	 * @return
	 */
	public static byte deleteFlashMemory() {
		try {
			return nxtCommand.deleteUserFlash(); 
		} catch (IOException ioe) {
			return 0;
		}
	}
	
	public static String getBrickName() {
		try {
			DeviceInfo i = nxtCommand.getDeviceInfo();
			return i.NXTname;
		} catch (IOException ioe) {
			return null;
		}
		
	}
	
	public static byte setBrickName(String newName) {
		try {
			return nxtCommand.setFriendlyName(newName);
		} catch (IOException ioe) {
			return 0;
		}
	}
	
	/**
	 * This doesn't seem to be implemented in Lego NXT firmware/protocol?
	 * @return Seems to return 0 every time
	 */
	public static int getSignalStrength() {
		try {
			DeviceInfo i = nxtCommand.getDeviceInfo();
			return i.signalStrength;
		} catch (IOException ioe) {
			return 0;
		}		
	}	
}
