package lejos.nxt.comm;

import java.io.*;
import lejos.nxt.*;
import java.util.*;
/**
 * 
 * Implements the Lego Communication Protocol,
 * with some extensions for lejos NXJ.
 *
 */
public class LCP {
	private static byte[] i2cBuffer = new byte[16];
    private static File[] files = null;
    private static String[] fileNames = null;
    private static int fileIdx = -1;
    private static String currentProgram = null;
    private static File file = null;
    private static FileOutputStream out = null;
    private static FileInputStream in = null;
    private static int numFiles;	
	private static char[] charBuffer = new char[20];
	@SuppressWarnings("unchecked")
	public static Queue<String>[] inBoxes = (Queue<String>[]) new Queue[20];
    
	// Command types constants. Indicates type of packet being sent or received.
	public static byte DIRECT_COMMAND_REPLY = 0x00;
	public static byte SYSTEM_COMMAND_REPLY = 0x01;
	public static byte REPLY_COMMAND = 0x02;
	public static byte DIRECT_COMMAND_NOREPLY = (byte)0x80; // Avoids ~100ms latency
	public static byte SYSTEM_COMMAND_NOREPLY = (byte)0x81; // Avoids ~100ms latency

	// Direct Commands
	public static final byte START_PROGRAM = 0x00;
	public static final byte STOP_PROGRAM = 0x01;
	public static final byte PLAY_SOUND_FILE = 0x02;
	public static final byte PLAY_TONE = 0x03;
	public static final byte SET_OUTPUT_STATE = 0x04;
	public static final byte SET_INPUT_MODE = 0x05;
	public static final byte GET_OUTPUT_STATE = 0x06;
	public static final byte GET_INPUT_VALUES = 0x07;
	public static final byte RESET_SCALED_INPUT_VALUE = 0x08;
	public static final byte MESSAGE_WRITE = 0x09;
	public static final byte RESET_MOTOR_POSITION = 0x0A;	
	public static final byte GET_BATTERY_LEVEL = 0x0B;
	public static final byte STOP_SOUND_PLAYBACK = 0x0C;
	public static final byte KEEP_ALIVE = 0x0D;
	public static final byte LS_GET_STATUS = 0x0E;
	public static final byte LS_WRITE = 0x0F;
	public static final byte LS_READ = 0x10;
	public static final byte GET_CURRENT_PROGRAM_NAME = 0x11;
	public static final byte MESSAGE_READ = 0x13;
	
	// NXJ additions
	public static byte NXJ_DISCONNECT = 0x20; 
	public static byte NXJ_DEFRAG = 0x21;
	
	// System Commands:
	public static final byte OPEN_READ = (byte)0x80;
	public static final byte OPEN_WRITE = (byte)0x81;
	public static final byte READ = (byte)0x82;
	public static final byte WRITE = (byte)0x83;
	public static final byte CLOSE = (byte)0x84;
	public static final byte DELETE = (byte)0x85;
	public static final byte FIND_FIRST = (byte)0x86;
	public static final byte FIND_NEXT = (byte)0x87;
	public static final byte GET_FIRMWARE_VERSION = (byte)0x88;
	public static final byte OPEN_WRITE_LINEAR = (byte)0x89;
	public static final byte OPEN_READ_LINEAR = (byte)0x8A;
	public static final byte OPEN_WRITE_DATA = (byte)0x8B;
	public static final byte OPEN_APPEND_DATA = (byte)0x8C;
	public static final byte BOOT = (byte)0x97;
	public static final byte SET_BRICK_NAME = (byte)0x98;
	public static final byte GET_DEVICE_INFO = (byte)0x9B;
	public static final byte DELETE_USER_FLASH = (byte)0xA0;
	public static final byte POLL_LENGTH = (byte)0xA1;
	public static final byte POLL = (byte)0xA2;
	
	public static final byte NXJ_FIND_FIRST = (byte)0xB6;
	public static final byte NXJ_FIND_NEXT = (byte)0xB7;
	public static final byte NXJ_PACKET_MODE = (byte)0xff;
	
	// Error codes
	
	public static final byte MAILBOX_EMPTY = (byte)0x40;
	public static final byte FILE_NOT_FOUND = (byte)0x86;
	public static final byte INSUFFICIENT_MEMORY = (byte) 0xFB;
	public static final byte DIRECTORY_FULL = (byte) 0xFC;
	public static final byte UNDEFINED_ERROR = (byte) 0x8A;
	public static final byte NOT_IMPLEMENTED = (byte) 0xFD;

	
	private LCP()
	{		
	}
	
	/**
	 * Emulates a Lego firmware Direct or System command
	 * @param cmd the buffer containing the command
	 * @param cmdLen the length of the command
	 */
	public static int emulateCommand(byte[] cmd, int cmdLen, byte[] reply)
	{
	    int len = 3;
	    
	    for(int i=0;i<reply.length;i++)reply[i] = 0;
	    
		reply[0] = REPLY_COMMAND;;
		reply[1] = cmd[1];
		
		byte cmdId = cmd[1];
		
		// START PROGRAM
		if (cmdId == START_PROGRAM) {
			init_files();
			currentProgram = getFile(cmd,2);
			if (fileNames != null) {
				for(int i=0;i<fileNames.length;i++) {
					if (currentProgram.equals(fileNames[i])) {
						files[i].exec();
					}
				}
			}
		}
		
		// GET CURRENT PROGRAM NAME
		
		if (cmdId == GET_CURRENT_PROGRAM_NAME) {
			if (currentProgram != null) {
				for(int i=0;i<currentProgram.length() && i < 19;i++) 
					reply[3+i] = (byte) currentProgram.charAt(i); 
			}
		}
		
		// GET BATTERY LEVEL
		if (cmdId == GET_BATTERY_LEVEL) {
			setReplyShortInt(Battery.getVoltageMilliVolt(), reply, 3);
			len = 5;									
		}
		
		// PLAY SOUND FILE
		if (cmdId == PLAY_SOUND_FILE)
		{
			init_files();
			String soundFile = getFile(cmd,3);
			File f = new File(soundFile);
			Sound.playSample(f, 50);
		}
		
		// PLAYTONE
		if (cmdId == PLAY_TONE)
		{
			Sound.playTone(getShortInt(cmd,2), getShortInt(cmd,4));
		}
		
		// GET FIRMWARE VERSION
		if (cmdId == GET_FIRMWARE_VERSION) 
		{
			reply[3] = 2;
			reply[4] = 1;
			reply[5] = 3;
			reply[6] = 1;			
			len = 7;
		}
		
		// GET DEVICE INFO
		if (cmdId == GET_DEVICE_INFO) 
		{
            byte []name = Bluetooth.stringToName(Bluetooth.getFriendlyName());
            // Note this is very odd. The set commmand allows for 16 characters
            // but the get command only allows for 15!
            for(int i=0;i<15;i++) reply[3+i] = name[i];
            byte [] address = Bluetooth.stringToAddress(Bluetooth.getLocalAddress());
            for(int i=0;i<Bluetooth.ADDRESS_LEN;i++) reply[18+i] = address[i];
            setReplyInt(File.freeMemory(),reply,29);
			len = 33;
		}	
		
		// SET BRICK NAME
		if (cmdId == SET_BRICK_NAME) 
		{
            byte [] name = new byte[16];
            for(int i=0;i<Bluetooth.NAME_LEN;i++) name[i] = cmd[i+2];
            Bluetooth.setFriendlyName(Bluetooth.nameToString(name));
			len = 4;
		}	
		
		// GETOUTPUTSTATE 
		if (cmdId == GET_OUTPUT_STATE) {
			byte port = cmd[2]; 
			NXTRegulatedMotor m;
			if(port == 0)
				m = Motor.A;
			else if(port == 1)
				m = Motor.B;
			else m = Motor.C;
			int tacho = m.getTachoCount();
			
			reply[3] = port;
			reply[4] = (byte)(m.getSpeed() * 100 / 900); // Power
			// MODE CALCULATION:
			byte mode = 0;
			if (m.isMoving()) mode = 0x01; // 0x01 = MOTORON
			reply[5] = mode; // Only contains isMoving (MOTORON) at moment
			// REGULATION_MODE CALCULATION:
			byte regulation_mode = 0; // 0 = idle
			if (m.isMoving()) mode = 0x01; // 0x01 = MOTOR_SPEED
			// !! This returns same as run state (below). Whats the diff?
			reply[6] = regulation_mode; // Regulation mode
			// TURN RATIO CALC (ignored):
			byte turn_ratio = 0; // NXJ uses Pilot. Omitting.
			reply[7] = turn_ratio; // Turn ratio
			// RUN_STATE CALCULATION:
			byte run_state = 0;
			if (m.isMoving()) run_state = 0x20; // 0x20 = RUNNING
			reply[8] = run_state; // Run state
			// 9 - 12 = Tacho Limit is currently ignored.
			// In future, it could get this from Motor if a
			// rotate() or rotateTo() command is in progress.
			
			// TachoCount just returns same as RotationCount:
			setReplyInt(tacho,reply,13);
			
			// !! Ignores BlockTacho
			
			// RotationCount:
			setReplyInt(tacho,reply,21);
			
			len = 25;						
		}
		
		// GETINPUTVALUES
		if (cmdId == GET_INPUT_VALUES) {
			byte port = cmd[2];
			SensorPort p = SensorPort.getInstance(port);
			int raw = p.readRawValue();
			int scaled = p.readValue();
			int norm = 1024 - raw;
			
			reply[3] = port;
			reply[4] = 1;
			reply[6] = (byte) p.getType();
			reply[7] = (byte) p.getMode();
			setReplyShortInt(raw, reply, 8);
			setReplyShortInt(norm, reply, 10);
			setReplyShortInt(scaled, reply, 12);		
			len = 16;						
		}
		
		// SETINPUTMODE
		if (cmdId == SET_INPUT_MODE) {
			byte port = cmd[2];
			int sensorType = (cmd[3] & 0xFF);
			int sensorMode = (cmd[4] & 0xFF);
			SensorPort.getInstance(port).setTypeAndMode(sensorType, sensorMode);
		}
		
		// SETOUTPUTSTATE
		if(cmdId == SET_OUTPUT_STATE) {
			byte motorid = cmd[2];
			byte power = cmd[3];
			int speed = (Math.abs(power) * 900) / 100;
			byte mode = cmd[4];
			byte regMode = cmd[5];
			byte turnRatio = cmd[6];
			byte runState = cmd[7];
			int tacholimit = getInt(cmd, 8);
					
			// Initialize motor:
			NXTRegulatedMotor m = null;
		
			for(int i=0;i<3;i++) 
			{			
				if(motorid == 0 || (motorid < 0 && i == 0))
					m = Motor.A;
				else if (motorid == 1 || (motorid < 0 && i == 1))
					m = Motor.B;
				else if (motorid == 2 || (motorid < 0 && i == 2))
				    m = Motor.C;
				
				m.setSpeed(speed);
			
				if(power < 0) tacholimit = -tacholimit;
			
				// Check if command is to STOP:
				if(power == 0) m.stop(true);
			
				// Check if doing tacho rotation
				if(tacholimit != 0)
					m.rotate(tacholimit, true); // Returns immediately
			
				if((mode | 0x01) != 0 && power != 0 && tacholimit == 0) { // MOTORON
					if(power>0) m.forward();
					else m.backward();
				}
				
				if (motorid >= 0) break;
			}
		}
		
		// RESETMOTORPOSITION
		if (cmdId == RESET_MOTOR_POSITION)
		{
			// Check if boolean value (cmd[3]) is false. If so,
			// reset TachoCount (i.e. RotationCount in LEGO FW terminology)
			if(cmd[3] == 0)
				MotorPort.getInstance(cmd[2]).resetTachoCount();				
		}
		
		// KEEPALIVE
		if (cmdId == KEEP_ALIVE)
		{
			len = 7;
		}
		
		// LSWRITE
		if (cmdId == LS_WRITE)
		{
			byte port = cmd[2];
			byte txLen = cmd[3];
			byte rxLen = cmd[4];
			SensorPort p = SensorPort.getInstance(port);
			p.i2cEnable(I2CPort.LEGO_MODE);
			int ret = p.i2cStart(cmd[5], cmd, 6, txLen-1, rxLen);
            p.i2cWaitIOComplete();
		}
		
		// LSREAD
		if (cmdId == LS_READ)
		{
			byte port = cmd[2];
			SensorPort p = SensorPort.getInstance(port);
            int ret = p.i2cComplete(i2cBuffer, 0, i2cBuffer.length);
			reply[3] = (byte) ret;
            if (ret > 0) System.arraycopy(i2cBuffer, 0, reply, 4, ret);
			len = 20;
		}
		
		// LSGETSTATUS
		if (cmdId == LS_GET_STATUS)
		{
			byte port = cmd[2];
			SensorPort p = SensorPort.getInstance(port);
			reply[3] = (byte) (p.i2cStatus() == 0 ? 0 : 1);
			len = 4;
		}
		
		// OPEN READ
		if (cmdId == OPEN_READ)
		{
			init_files();
			file = new File(getFile(cmd,2));
            try {
            	in = new FileInputStream(file);
            	int size = (int) file.length();
            	setReplyInt(size,reply,4);         	
            } catch (Exception e) {
            	reply[2] = FILE_NOT_FOUND;
            }
			len = 8;
		}	
		
		// OPEN WRITE
		if (cmdId == OPEN_WRITE)
		{
			int size = getInt(cmd, 22);
			init_files();
			
			// If insufficient flash memory, report an error			
			if (size > File.freeMemory()) {
				reply[2] = INSUFFICIENT_MEMORY;
			} else {	
				try {
					file = new File(getFile(cmd,2));
				
					if (file.exists()) {
						file.delete();
						numFiles--;
					}

					file.createNewFile();
					fileNames = new String[++numFiles];
					for(int j=0;j<numFiles;j++) fileNames[j] = files[j].getName();
					out = new FileOutputStream(file);
				} catch (Exception e) {
					files = null;
					File.reset(); // force read from file table
					init_files();
					reply[2] = DIRECTORY_FULL;
				}
			}
			len = 4;
		}
		
		// OPEN WRITE LINEAR
		if (cmdId == OPEN_WRITE_LINEAR)
		{
			reply[2] = NOT_IMPLEMENTED;
			len = 4;
		}
		
		// OPEN WRITE DATA
		if (cmdId == OPEN_WRITE_DATA)
		{
			reply[2] = NOT_IMPLEMENTED;
			len = 4;
		}
		
		// OPEN APPEND  DATA
		if (cmdId == OPEN_APPEND_DATA)
		{
			reply[2] = FILE_NOT_FOUND;
			len = 8;
		}
		
		// DEFRAG
		if (cmdId == NXJ_DEFRAG)
		{
			try {
				File.defrag();
			}catch (IOException ioe) {}
		}

		// FIND FIRST
		if (cmdId == FIND_FIRST || cmdId == NXJ_FIND_FIRST)
		{
			init_files();
			if (cmdId == FIND_FIRST) len = 28;
			else len = 32;
			if (numFiles == 0)
			{
				reply[2] = FILE_NOT_FOUND;
			}
			else
			{
				for(int i=0;i<fileNames[0].length();i++) reply[4+i] = (byte) fileNames[0].charAt(i);
				fileIdx = 1;
            	int size = (int) files[0].length();
            	setReplyInt(size,reply,24);
    			
    			if (cmdId == NXJ_FIND_FIRST) {
    				int startPage = files[0].getPage();
    				setReplyInt(startPage,reply,28);   				
    			}
			}
		}
		
		// FIND NEXT
		if (cmdId == FIND_NEXT || cmdId == NXJ_FIND_NEXT)
		{
			if (cmdId == FIND_NEXT) len = 28;
			else len = 32;
			if (fileNames == null || fileIdx >= fileNames.length) reply[2] = FILE_NOT_FOUND;
			else
			{
				for(int i=0;i<fileNames[fileIdx].length();i++) reply[4+i] = (byte) fileNames[fileIdx].charAt(i);
            	int size = (int) files[fileIdx].length();
            	setReplyInt(size,reply,24);   			
    			if (cmdId == NXJ_FIND_NEXT) {
    				int startPage = files[fileIdx].getPage();
    				setReplyInt(startPage,reply,28);  				
    			}
    			
				fileIdx++;
			}
		}
		
		// READ
		if (cmdId == READ)
		{
            int numBytes = getShortInt(cmd,3);
            int bytesRead = 0;
            
            try {
            	bytesRead = in.read(reply, 6, numBytes);
            	setReplyShortInt(bytesRead, reply, 4);
            } catch (IOException ioe) {
            	reply[2] = UNDEFINED_ERROR;
            }

			len = bytesRead + 6;
		}
		
		// WRITE
		if (cmdId == WRITE)
		{
			int dataLen = cmdLen - 3;
			try {
				out.write(cmd,3,dataLen);
				setReplyShortInt(dataLen, reply, 4);
			} catch (Exception ioe) {
				reply[2] = UNDEFINED_ERROR;
			}						

			len = 6;
		}
		
		// DELETE
		if (cmdId == DELETE)
		{
			boolean deleted = false;
			len = 23;
			String fileName = getFile(cmd,2);
			if (fileNames != null) {
				for(int i=0;i<fileNames.length;i++) {
					if (fileName.equals(fileNames[i])) {
						files[i].delete();
						for(int j=0;j<fileName.length();j++) reply[j+3] = (byte) fileName.charAt(i);
						deleted = true;
						fileNames = new String[--numFiles];
						for(int j=0;j<numFiles;j++) fileNames[j] = files[j].getName();
						break;
					}
				}
			}
			if (!deleted) reply[2] = FILE_NOT_FOUND;
		}
		
		// CLOSE
		if (cmdId == CLOSE)
		{
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (Exception ioe) {
					reply[2] = UNDEFINED_ERROR;
				}
				out = null;
			}
			len = 4;
		}
		
		// MESSAGE READ		
		if (cmdId == MESSAGE_READ) {
			Queue<String> inBox = inBoxes[cmd[2]];
			reply[3] = cmd[3];
			if (inBox == null || inBox.empty()) {
				reply[2] = MAILBOX_EMPTY;
			} else {
				String msg = (String) (cmd[4] == 0 ? inBox.peek()
						                           : inBox.pop());
				int msgLen = msg.length();
				reply[4] = (byte) (msgLen > 58 ? 58 : msgLen);
				for(int i=0;i<58 && i<msgLen;i++) {
					reply[5+i] = (byte) msg.charAt(i);
				}
			}
			len = 64;
		}
		
		// DELETE USE FLASH
		if (cmdId == DELETE_USER_FLASH) {
			File.format();
			files = null;
			numFiles = 0;
		}
		
		return len;
	}
	
	public static void messageWrite(int mailbox, String msg) {
		if (mailbox < inBoxes.length) {
			if (inBoxes[mailbox] == null) inBoxes[mailbox] = new Queue<String>();
			inBoxes[mailbox].push(msg);			
		}
	}
	
	private static int getShortInt(byte [] cmd, int i)
	{
		return (cmd[i] & 0xFF) + ((cmd[i+1] & 0xFF) << 8);
	}
	
	private static int getInt(byte [] cmd, int i)
	{
		return (cmd[i] & 0xFF) + 
		       ((cmd[i+1] & 0xFF) << 8) +
		       ((cmd[i+2] & 0xFF) << 16) +
		       ((cmd[i+3] & 0xFF) << 24);
	}
	
	private static byte getLSB(int i)
	{
		return (byte) (i & 0xFF);
	}
	
	private static byte getMSB(int i)
	{
		return (byte) ((i >> 8) & 0xFF);
	}
	
	private static byte getMSB1(int i)
	{
		return (byte) ((i >> 16) & 0xFF);
	}
	
	private static byte getMSB2(int i)
	{
		return (byte) ((i >> 24) & 0xFF);
	}
	
	private static void setReplyInt(int n, byte [] reply, int start) {
		reply[start] = getLSB(n);
		reply[start+1] = getMSB(n);
		reply[start+2] = getMSB1(n);
		reply[start+3] = getMSB2(n);
	}
	
	private static void setReplyShortInt(int n, byte [] reply, int start) {
		reply[start] = getLSB(n);
		reply[start+1] = getMSB(n);
	}
	
	private static void init_files() {
		if (files == null) {
			files = File.listFiles();
			numFiles = 0;
			for(int i=0;i<files.length && files[i] != null;i++) numFiles++;
			fileNames = new String[numFiles];
			for(int i=0;i<numFiles;i++) fileNames[i] = files[i].getName();
		}
	}
	
	private static String getFile(byte [] cmd,int start) {
		int filenameLength = 0;
		for(int i=0;i<20 && cmd[i+start] != 0;i++) filenameLength++;
		for(int i=0;i<filenameLength;i++) charBuffer[i] = (char) cmd[i+start];
		return new String(charBuffer,0,filenameLength);
	}
}

