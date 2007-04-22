package lejos.nxt.comm;

import java.io.*;
import lejos.nxt.*;

/**
 * 
 * Implements the Lego Communication Protocol.
 *
 */
public class LCP {
	static byte[] reply = new byte[64];
	static byte[] i2cCommand = new byte[16];
	static byte[] i2cReply = new byte[16];
	static int i2cLen = 0;
	static int currentPage = 0;
    static File[] files = null;
    static String[] fileNames = null;
    static int fileIdx = -1;
    static String currentProgram = null;
    static File file = null;
    static FileOutputStream out = null;
    static FileInputStream in = null;
	
	private LCP()
	{		
	}
	
	/**
	 * Emulates a Lego firmware Direct or System command
	 * @param cmd the buffer containing the command
	 * @param cmdLen the legth of the command
	 */
	public static void emulateCommand(byte[] cmd, int cmdLen)
	{
	    int len = 3;
	    
	    for(int i=0;i<32;i++)reply[i] = 0;
	    
		reply[0] = 0x02;
		reply[1] = cmd[1];
		
		// START PROGRAM
		if (cmd[1] == (byte) 0x00) {
			int filenameLength = 0;
			for(int i=0;i<20 && cmd[i+2] != 0;i++) filenameLength++;
			char[] chars = new char[filenameLength];
			for(int i=0;i<filenameLength;i++) chars[i] = (char) cmd[i+2];
			chars[filenameLength-3] = 'b';
			chars[filenameLength-2] = 'i';
			chars[filenameLength-1] = 'n';
			currentProgram = new String(chars,0,filenameLength);
			//lejos.nxt.LCD.drawString(currentProgram,0,6);
			//lejos.nxt.LCD.refresh();
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
		
		if (cmd[1] == (byte) 0x11) {
			if (currentProgram != null) {
				for(int i=0;i<currentProgram.length() && i < 19;i++) 
					reply[3+i] = (byte) currentProgram.charAt(i); 
			}
		}
		
		// GET BATTERY LEVEL
		if (cmd[1] == 0x0B) {
			int mv = Battery.getVoltageMilliVolt();

			reply[3] = getLSB(mv);
			reply[4] = getMSB(mv);
			len = 5;									
		}
		
		// PLAYTONE
		if (cmd[1] == 0x03)
		{
			Sound.playTone(getInt(cmd,2), getInt(cmd,4));
		}
		
		// GET FIRMWARE VERSION
		if (cmd[1] == (byte) 0x88) 
		{
			reply[3] = 2;
			reply[4] = 1;
			reply[5] = 3;
			reply[6] = 1;			
			len = 7;
		}
		
		// GETOUTPUTSTATE 
		if (cmd[1] == 0x06) {
			byte port = cmd[2]; 
			Motor m;
			if(port == 0)
				m = Motor.A;
			else if(port == 1)
				m = Motor.B;
			else m = Motor.C;
			int tacho = m.getTachoCount();
			
			byte mode = 0;
			if (m.isMoving()) mode = 0x01; 
			
			reply[3] = port;
			reply[4] = (byte)(m.getSpeed() * 100 / 900); // Power
			reply[5] = mode; // Only contains isMoving at moment
			reply[13] = (byte) (tacho & 0xFF);
			reply[14] = (byte) ((tacho >> 8) & 0xFF);
			reply[15] = (byte) ((tacho >> 16) & 0xFF);
			reply[16] = (byte) ((tacho >> 24) & 0xFF);
			len = 25;						
		}
		
		// GETINPUTVALUES
		if (cmd[1] == 0x07) {
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
		if (cmd[1] == 0x05) {
			byte port = cmd[2];
			int sensorType = (cmd[3] & 0xFF);
			int sensorMode = (cmd[4] & 0xFF);
			SensorPort.PORTS[port].setTypeAndMode(sensorType, sensorMode);
		}
		
		// SETOUTPUTSTATE
		if(cmd[1] == 0x04) {
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
		if (cmd[1] == (byte) 0x0A)
		{
			MotorPort.resetTachoCountById(cmd[2]);
		}
		
		// KEEPALIVE
		if (cmd[1] == (byte) 0x0D)
		{
			len = 7;
		}
		
		// LSWRITE
		if (cmd[1] == 0x0F)
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
		if (cmd[1] == 0x10)
		{
			reply[3] = (byte) i2cLen;
			for(int i=0;i<16;i++) reply[i+4] = i2cReply[i];
			len = 20;
			i2cLen = 0;
		}
		
		// LSGETSTATUS
		if (cmd[1] == (byte) 0x0E)
		{
			reply[3] = (byte) i2cLen;
			len = 4;
		}
		
		// OPEN READ
		if (cmd[1] == (byte) 0x80)
		{
			int filenameLength = 0;
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
            	reply[2] = (byte) 0x86; // File not found
            }
			len = 8;
		}	
		
		// OPEN WRITE
		if (cmd[1] == (byte) 0x81)
		{
			int filenameLength = 0;
			for(int i=0;i<20 && cmd[i+2] != 0;i++) filenameLength++;
			char[] chars = new char[filenameLength];
			for(int i=0;i<filenameLength;i++) chars[i] = (char) cmd[i+2];
			file = new File(new String(chars,0,filenameLength));
			int size = cmd[22] & 0xFF;
			size += ((cmd[23] & 0xFF) << 8);
			size += ((cmd[24] & 0xFF) << 16);
			size += ((cmd[25] & 0xFF) << 24);
			// Need check for exists already and current size
			file.createNewFile(); 
			out = new FileOutputStream(file);
			
			len = 4;
		}
		
		// OPEN WRITE LINEAR
		if (cmd[1] == (byte) 0x89)
		{
			len = 4;
		}
		
		// OPEN WRITE DATA
		if (cmd[1] == (byte) 0x8B)
		{
			len = 4;
		}
		
		// OPEN APPEND  DATA
		if (cmd[1] == (byte) 0x8C)
		{
			reply[2] = (byte) 0x86; // File not found
			len = 8;
		}

		// FIND FIRST
		if (cmd[1] == (byte) 0x86)
		{
			files = File.listFiles();
			
			int numFiles = 0;
			for(int i=0;i<files.length && files[i] != null;i++) numFiles++;
			if (numFiles == 0)
			{
				reply[2] = (byte) 0x86; // File not found
			}
			else
			{
				fileNames = new String[numFiles];
				for(int i=0;i<numFiles;i++) fileNames[i] = files[i].getName();
				for(int i=0;i<fileNames[0].length();i++) reply[4+i] = (byte) fileNames[0].charAt(i);
				fileIdx = 1;
			}
			
			len = 28;
		}
		
		// FIND NEXT
		if (cmd[1] == (byte) 0x87)
		{
			if (fileNames == null || fileIdx >= fileNames.length) reply[2] = (byte) 0x86; // File not found
			else
			{
				for(int i=0;i<fileNames[fileIdx].length();i++) reply[4+i] = (byte) fileNames[fileIdx].charAt(i);
				fileIdx++;
			}
			len = 28;
		}
		
		// READ
		if (cmd[1] == (byte) 0x82)
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
		if (cmd[1] == (byte) 0x83)
		{
			int dataLen = cmdLen - 3;
			try {
				out.write(cmd,3,dataLen);
			} catch (IOException ioe) {}
			reply[4] = (byte) (dataLen &0xFF);
			reply[5] = (byte) ((dataLen >> 8) & 0xFF);
			len = 6;
		}
		
		// CLOSE
		if (cmd[1] == (byte) 0x84)
		{
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException ioe) {}
				out = null;
			}
			len = 4;
		}

		if ((cmd[0] & 0x80) == 0) Bluetooth.sendPacket(reply, len);
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

}

