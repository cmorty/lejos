package lejos.nxt.comm;

import java.io.*;
import lejos.nxt.*;

/**
 * 
 * Implements the Lego Communication Protocol,
 * with some extensions for lejos NXJ.
 *
 */
public class LCP {
	private static byte[] i2cReply = new byte[16];
	private static int i2cLen = 0;
    private static File[] files = null;
    private static String[] fileNames = null;
    private static int fileIdx = -1;
    private static String currentProgram = null;
    private static File file = null;
    private static FileOutputStream out = null;
    private static FileInputStream in = null;
    private static int numFiles;
    
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
	
	// Error codes
	
	public static final byte FILE_NOT_FOUND = (byte)0x86;

	private LCP()
	{		
	}
	
	/**
	 * Emulates a Lego firmware Direct or System command
	 * @param cmd the buffer containing the command
	 * @param cmdLen the legth of the command
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
			int filenameLength = 0;
			init_files();
			for(int i=0;i<20 && cmd[i+2] != 0;i++) filenameLength++;
			char[] chars = new char[filenameLength];
			for(int i=0;i<filenameLength;i++) chars[i] = (char) cmd[i+2];
			currentProgram = new String(chars,0,filenameLength);
			if (fileNames != null) {
				for(int i=0;i<fileNames.length;i++) {
					if (currentProgram.equals(fileNames[i])) {
						LCD.clear();
						LCD.refresh();
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
			int mv = Battery.getVoltageMilliVolt();

			reply[3] = getLSB(mv);
			reply[4] = getMSB(mv);
			len = 5;									
		}
		
		// PLAYTONE
		if (cmdId == PLAY_TONE)
		{
			Sound.playTone(getInt(cmd,2), getInt(cmd,4));
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
            byte [] name = Bluetooth.getFriendlyName();
            for(int i=0;i<15;i++) reply[3+i] = name[i];
            byte [] address = Bluetooth.getLocalAddress();
            for(int i=0;i<7;i++) reply[18+i] = address[i];
            int freeMem = File.freeMemory();
			reply[29] = (byte) (freeMem & 0xFF);
			reply[30] = (byte) ((freeMem >> 8) & 0xFF);
			reply[31] = (byte) ((freeMem >> 16) & 0xFF);
			reply[32] = (byte) ((freeMem >> 24) & 0xFF);
			len = 33;
		}	
		
		// SET BRICK NAME
		if (cmdId == SET_BRICK_NAME) 
		{
            byte [] name = new byte[16];
            for(int i=0;i<16;i++) name[i] = cmd[i+2];
            Bluetooth.setFriendlyName(name);
			len = 4;
		}	
		
		// GETOUTPUTSTATE 
		if (cmdId == GET_OUTPUT_STATE) {
			byte port = cmd[2]; 
			Motor m;
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
			reply[13] = (byte) (tacho & 0xFF);
			reply[14] = (byte) ((tacho >> 8) & 0xFF);
			reply[15] = (byte) ((tacho >> 16) & 0xFF);
			reply[16] = (byte) ((tacho >> 24) & 0xFF);
			len = 25;						
		}
		
		// GETINPUTVALUES
		if (cmdId == GET_INPUT_VALUES) {
			byte port = cmd[2];
			int raw = SensorPort.PORTS[port].readRawValue();
			int scaled = SensorPort.PORTS[port].readValue();
			int norm = 1024 - raw;
			
			reply[3] = port;
			reply[4] = 1;
			reply[5] = 0;
			reply[6] = (byte) SensorPort.PORTS[port].getType();
			reply[7] = (byte) SensorPort.PORTS[port].getMode();
			reply[8] = getLSB(raw);
			reply[9] = getMSB(raw);
			reply[10] = getLSB(norm);
			reply[11] = getMSB(norm);
			reply[12] = getLSB(scaled);
			reply[13] = getMSB(scaled);
			reply[14] = 0;
			reply[15] = 0;		
			len = 16;						
		}
		
		// SETINPUTMODE
		if (cmdId == SET_INPUT_MODE) {
			byte port = cmd[2];
			int sensorType = (cmd[3] & 0xFF);
			int sensorMode = (cmd[4] & 0xFF);
			SensorPort.PORTS[port].setTypeAndMode(sensorType, sensorMode);
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
			int tacholimit = (0xFF & cmd[8]) | ((0xFF & cmd[9]) << 8)| ((0xFF & cmd[10]) << 16)| ((0xFF & cmd[11]) << 24);
			
			// Initialize motor:
			Motor m = null;
		
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
				if(power == 0) m.stop();
			
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
			MotorPort.resetTachoCountById(cmd[2]);
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
			SensorPort.i2cEnableById(port);
			try {Thread.sleep(100);} catch(InterruptedException ie) {}
			int ret = SensorPort.i2cStartById(port, cmd[5] >> 1, cmd[6], rxLen, i2cReply, rxLen, 0);

			while (SensorPort.i2cBusyById(port) != 0) {
				Thread.yield();
			}
			try {Thread.sleep(100);} catch(InterruptedException ie) {}
			i2cLen = rxLen;
		}
		
		// LSREAD
		if (cmdId == LS_READ)
		{
			reply[3] = (byte) i2cLen;
			for(int i=0;i<16;i++) reply[i+4] = i2cReply[i];
			len = 20;
			i2cLen = 0;
		}
		
		// LSGETSTATUS
		if (cmdId == LS_GET_STATUS)
		{
			reply[3] = (byte) i2cLen;
			len = 4;
		}
		
		// OPEN READ
		if (cmdId == OPEN_READ)
		{
			int filenameLength = 0;
			init_files();
			for(int i=0;i<20 && cmd[i+2] != 0;i++) filenameLength++;
			char[] chars = new char[filenameLength];
			for(int i=0;i<filenameLength;i++) chars[i] = (char) cmd[i+2];
			file = new File(new String(chars,0,filenameLength));
            try {
            	in = new FileInputStream(file);
            	int size = file.length();
            	cmd[4] = (byte) (size & 0xFF);
    			cmd[5] = (byte) ((size >> 8) & 0xFF);
    			cmd[6] = (byte) ((size >> 16) & 0xFF);
    			cmd[7] = (byte) ((size >> 24) & 0xFF);          	
            } catch (Exception e) {
            	reply[2] = FILE_NOT_FOUND;
            }
			len = 8;
		}	
		
		// OPEN WRITE
		if (cmdId == OPEN_WRITE)
		{
			int filenameLength = 0;
			init_files();
			for(int i=0;i<20 && cmd[i+2] != 0;i++) filenameLength++;
			char[] chars = new char[filenameLength];
			for(int i=0;i<filenameLength;i++) chars[i] = (char) cmd[i+2];
			file = new File(new String(chars,0,filenameLength));
			int size = cmd[22] & 0xFF;
			size += ((cmd[23] & 0xFF) << 8);
			size += ((cmd[24] & 0xFF) << 16);
			size += ((cmd[25] & 0xFF) << 24);
			if (file.exists()) {
				file.delete();
				numFiles--;
			}
			file.createNewFile();
			fileNames = new String[++numFiles];
			for(int j=0;j<numFiles;j++) fileNames[j] = files[j].getName();
			out = new FileOutputStream(file);
			
			len = 4;
		}
		
		// OPEN WRITE LINEAR
		if (cmdId == OPEN_WRITE_LINEAR)
		{
			len = 4;
		}
		
		// OPEN WRITE DATA
		if (cmdId == OPEN_WRITE_DATA)
		{
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
			File.defrag();
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
            	int size = files[0].length();
            	reply[24] = (byte) (size & 0xFF);
    			reply[25] = (byte) ((size >> 8) & 0xFF);
    			reply[26] = (byte) ((size >> 16) & 0xFF);
    			reply[27] = (byte) ((size >> 24) & 0xFF);
    			
    			if (cmdId == NXJ_FIND_FIRST) {
    				int startPage = files[0].getPage();
    				reply[28] = (byte) (startPage & 0xFF);
        			reply[29] = (byte) ((startPage >> 8) & 0xFF);
        			reply[30] = (byte) ((startPage >> 16) & 0xFF);
        			reply[31] = (byte) ((startPage >> 24) & 0xFF);   				
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
            	int size = files[fileIdx].length();
            	reply[24] = (byte) (size & 0xFF);
    			reply[25] = (byte) ((size >> 8) & 0xFF);
    			reply[26] = (byte) ((size >> 16) & 0xFF);
    			reply[27] = (byte) ((size >> 24) & 0xFF);
    			
    			if (cmdId == NXJ_FIND_NEXT) {
    				int startPage = files[fileIdx].getPage();
    				reply[28] = (byte) (startPage & 0xFF);
        			reply[29] = (byte) ((startPage >> 8) & 0xFF);
        			reply[30] = (byte) ((startPage >> 16) & 0xFF);
        			reply[31] = (byte) ((startPage >> 24) & 0xFF);   				
    			}
    			
				fileIdx++;
			}
		}
		
		// READ
		if (cmdId == READ)
		{
            int numBytes = ((cmd[4] & 0xFF) << 8) + (cmd[3] & 0xFF);
            int bytesRead = 0;
            
            try {
            	bytesRead = in.read(reply,6, numBytes);
            } catch (IOException ioe) {}
			reply[4] = (byte) (bytesRead & 0xFF);
			reply[5] = (byte) ((bytesRead << 8) & 0xFF);
			len = bytesRead + 6;
		}
		
		// WRITE
		if (cmdId == WRITE)
		{
			int dataLen = cmdLen - 3;
			try {
				out.write(cmd,3,dataLen);
			} catch (Exception ioe) {
				//LCD.drawString("Exception", 0, 7);
				//LCD.refresh();						
			}
			reply[4] = (byte) (dataLen &0xFF);
			reply[5] = (byte) ((dataLen >> 8) & 0xFF);
			len = 6;
		}
		
		// DELETE
		if (cmdId == DELETE)
		{
			int filenameLength = 0;
			boolean deleted = false;
			len = 23;
			for(int i=0;i<20 && cmd[i+2] != 0;i++) filenameLength++;
			char[] chars = new char[filenameLength];
			for(int i=0;i<filenameLength;i++) chars[i] = (char) cmd[i+2];
			String fileName = new String(chars,0,filenameLength);
			if (fileNames != null) {
				for(int i=0;i<fileNames.length;i++) {
					if (fileName.equals(fileNames[i])) {
						files[i].delete();
						for(int j=0;j<filenameLength;j++) reply[j+3] = (byte) chars[j];
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
					//LCD.drawString("Exception",0,7);
					//LCD.refresh();
				}
				out = null;
			}
			len = 4;
		}
		
		return len;
	}
	
	private static int getInt(byte [] cmd, int i)
	{
		return (cmd[i] & 0xFF) + ((cmd[i+1] & 0xFF) << 8);
	}
	
	private static byte getLSB(int i)
	{
		return (byte) (i & 0xFF);
	}
	
	private static byte getMSB(int i)
	{
		return (byte) ((i >> 8) & 0xFF);
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
}

