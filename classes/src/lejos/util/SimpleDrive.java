package lejos.util;

/**
 * Simple robot driving extension for NXT Charting Logger.
 * <P>
 * Create a class that implements <code>SimpleDriveProvider</code> and register it with an 
 * instance of this class (via the constructor) along with 
 * the <code>LogMessageManager</code> singleton to enable 
 * NXT Charting Logger to display a driver interface to drive your robot.
 * 
 * @author Kirk P. Thompson
 * @see SimpleDriveProvider
 * @see LogMessageManager
 */
public class SimpleDrive extends LogMessageTypeHandler {
	private SimpleDriveProvider driver = null;
	

	/**
	 * Create a <code>SimpleDrive</code> instance using a passed implementation of <code>SimpleDriveProvider</code> and
	 * the <code>LogMessageManager</code> singleton instance. 
	 * 
	 * @param loggerMessageManager lmm The <code>LogMessageManager</code> singleton. See 
	 * 		{@link LogMessageManager#getLogMessageManager(NXTDataLogger)}.
	 * @param driver Your <code>SimpleDriveProvider</code> implementation instance
	 * @see SimpleDriveProvider
	 */
	public SimpleDrive(LogMessageManager loggerMessageManager, SimpleDriveProvider driver) {
		super(loggerMessageManager);
		if (driver==null) {
			throw new IllegalArgumentException("no SimpleDriveProvider");
		}
		this.driver= driver;
	}

	/** Implementation use only. You do not need to use this method.
	 * @see lejos.util.LogMessageTypeHandler#getHandlerTypeID()
	 */
	@Override
	protected final int getHandlerTypeID() {
		return TYPE_ROBOT_DRIVE;
	}

	/* (non-Javadoc)
	 * @see lejos.util.LogMessageTypeHandler#processMessage(byte[], int)
	 */
	@Override
	void processMessage(byte[] message, int typeID) {
//		System.out.println("msglen:" + message.length);
//		System.out.println("typeID:" + typeID);
		
		// ignores broadcast messages
		if (typeID==TYPE_ALWAYS_RECEIVE) return;
		
//				System.out.println(getHandlerID() + ": hndlr " + (message[0] & 0xff));
		// Skip processing if not for me
		if (getHandlerID() != (message[0] & 0xff)) return; // byte 0 => handler ID
		
		// get the command
		int command = message[1] & 0xff;
//		System.out.println("cmd=" + command + ",l=" + (message.length - HEADER_DATA_OFFSET));
		
		if (driver==null) return;
		int intValue;
		byte[] buf = new byte[4];
		float sendValue = Float.NaN;
		String labelString =null;
		
		// process the command
		switch (command) {
			case 0: // SET forward
				driver.driveForward(message[2]!=0);
				break;
			case 1: // SET backward
				driver.driveBackward(message[2]!=0);
				break;
			case 2: // SET TurnLeft
				driver.driveTurnLeft(message[2]!=0);
				break;
			case 3: // SET TurnRight
				driver.driveTurnRight(message[2]!=0);
				break;
			case 4: // SET power
				intValue=EndianTools.decodeIntBE(message, HEADER_DATA_OFFSET);
				driver.setPower(intValue);
				break;
			case 5: // GET power
				intValue = Math.abs(driver.getPower());
				if (intValue>100) intValue= 100;
				EndianTools.encodeIntBE(intValue, buf, 0);
				this.sendMessage(command - 1, buf);
				break;
			case 6: // SET Command 1
				driver.doCommand1(message[2]!=0);
				break;
			case 7: // SET Command 2
				driver.doCommand2(message[2]!=0);
				break;
			case 8: // SET Command 3
				driver.doCommand3(message[2]!=0);
				break;
			case 9: // SET Command 4
				driver.doCommand4(message[2]!=0);
				break;
			case 11: //Get Command 1 1abel (return to pc)
				labelString = driver.getCommand1Label();
				break;
			case 13: //Get Command 2 1abel (return to pc)
				labelString = driver.getCommand2Label();
				break;
			case 15: //Get Command 3 1abel (return to pc)
				labelString = driver.getCommand3Label();
				break;
			case 17: //Get Command 4 1abel (return to pc)
				labelString = driver.getCommand4Label();
				break;
			case 18: // Set Value 1 
				driver.setValue1(bytesToFloat(message));
				break;
			case 19: // Get Value 1 (return to PC)
				sendValue = driver.getValue1();
				break;
			case 20: // Set Value 2 
				driver.setValue2(bytesToFloat(message));
				break;
			case 21: // Get Value 2 (return to PC)
				sendValue = driver.getValue2();
				break;
			case 22: // Set Value 3 
				driver.setValue3(bytesToFloat(message));
				break;
			case 23: // Get Value 3 (return to PC)
				sendValue = driver.getValue3();
				break;
			case 24: // Set Value 4 
				driver.setValue4(bytesToFloat(message));
				break;
			case 25: // Get Value 4 (return to PC)
				sendValue = driver.getValue4();
				break;
			case 27: //Get Value 1 field 1abel (return to pc)
				labelString = driver.getValue1Label();
				break;
			case 29: //Get Value 2 field 1abel (return to pc)
				labelString = driver.getValue2Label();
				break;
			case 31: //Get Value 3 field 1abel (return to pc)
				labelString = driver.getValue3Label();
				break;
			case 33: //Get Value 4 field 1abel (return to pc)
				labelString = driver.getValue4Label();
				break;
			default:
				break;
		}
		
		// send a reply message if value defined
		if (!Float.isNaN(sendValue)) {
			buf = new byte[4];
			EndianTools.encodeIntBE(Float.floatToIntBits(sendValue), buf, 0);
			// shift the command by -1 to associated SET so the GUI is instructed to SET the field with returned value
			sendMessage(command - 1, buf);
		} 
		
		// send label if defined
		if (labelString!=null) {
			this.sendMessage(command - 1, labelString.getBytes());
		}
				
	}
	
	private float bytesToFloat(byte[] buf){
//		System.out.println("flt len " + buf.length);
//		float t = 0;
		
		// include the 2 offset to skip over first handler ID and command byte
		float t = Float.intBitsToFloat(EndianTools.decodeIntBE(buf, HEADER_DATA_OFFSET));
//		System.out.println("flt=" + t);
		return t;
	}
}
