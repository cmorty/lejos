package lejos.nxt.comm;

import lejos.nxt.*;

/**
 * 
 * Implements the Lego Communication Protocol.
 *
 */
public class LCP {
	static byte[] reply = new byte[32];
	static byte[] i2cCommand = new byte[16];
	static byte[] i2cReply = new byte[16];
	static int i2cLen = 0;
	
	
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
		if (cmd[1] == 0x88) 
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
			if(motorid == 0)
				m = Motor.A;
			else if (motorid == 1)
				m = Motor.B;
			else m = Motor.C;
			m.setSpeed(speed);
			
			if(power < 0) tacholimit = -tacholimit;
			
			// Check if command is to STOP:
			if(mode == 0x07 & power == 0) // MOTORON + BRAKE + REGULATED
				m.stop();
			
			// Check if doing tacho rotation
			if(tacholimit != 0)
				m.rotate(tacholimit, true); // Returns immediately
			
			if(mode == 0x07 & power != 0 & tacholimit == 0) { // MOTORON
				if(power>0) m.forward();
				else m.backward();
			}
		}
		
		// RESETMOTORPOSITION
		if (cmd[1] == (byte) 0x0A)
		{
			MotorPort.resetTachoCountById(cmd[2]);
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
		
		// FIND FIRST
		if (cmd[1] == (byte) 0x86)
		{
			reply[2] = (byte) 0x86; // File not found
			len = 28;
		}
		
		// FIND NEXT
		if (cmd[1] == (byte) 0x87)
		{
			reply[2] = (byte) (byte) 0x86; // File not found
			len = 28;
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

