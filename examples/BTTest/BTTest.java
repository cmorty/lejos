import lejos.nxt.*;
import lejos.nxt.comm.*;

public class BTTest {
	// NOTE: These commands only work with iCommand if NXTCommand.setVerify(true);
	public static void main(String [] args)  throws Exception {
		// Get the version
		
		byte[] msg = new byte[32];
		byte [][] reply = new byte[9][32];
		byte[] dummy = new byte[32];
		byte[] device = new byte[7];
		int n;
		boolean cmdMode = true;
		
		// Read Reset indication messages
		
		Bluetooth.receiveReply(dummy,32);	
		Bluetooth.receiveReply(dummy,32);
		
		//msg[0] = Bluetooth.MSG_GET_VERSION;
		//Bluetooth.sendCommand(msg,1);	
		//Thread.sleep(100);		
		//Bluetooth.receiveReply(reply[0],32);
		
		//msg[0] = Bluetooth.MSG_GET_LOCAL_ADDR;
		//Bluetooth.sendCommand(msg,1);	
		//Thread.sleep(100);		
		//Bluetooth.receiveReply(reply[1],32);
		
		//msg[0] = Bluetooth.MSG_GET_FRIENDLY_NAME;
		//Bluetooth.sendCommand(msg,1);	
		//Thread.sleep(100);		
		//Bluetooth.receiveReply(reply[2],32);
		
		msg[0] = Bluetooth.MSG_GET_DISCOVERABLE;
		Bluetooth.sendCommand(msg,1);
		Thread.sleep(100);	
		Bluetooth.receiveReply(reply[0],32);

		n = 1;
		
		//Bluetooth.btSetCmdMode(1);
		Bluetooth.btStartADConverter();
		
		
		for(;;) {

			if (cmdMode) Bluetooth.receiveReply(reply[n],32);
			else Bluetooth.receiveData(reply[n],32);
			if (reply[n][0] != 0) {
				if (reply[n][1] == Bluetooth.MSG_REQUEST_PIN_CODE) {
					for(int i=0;i<7;i++) device[i] = reply[n][i+2];
					msg[0] = Bluetooth.MSG_PIN_CODE;
					for(int i=0;i<7;i++) msg[i+1] = device[i];
					msg[8] = '1';
					msg[9] = '2';
					msg[10] = '3';
					msg[11] = '4';
					for(int i=0;i<12;i++) msg[i+12] = 0;
					Bluetooth.sendCommand(msg, 24);					
				}	
				if (reply[n][1] == Bluetooth.MSG_REQUEST_CONNECTION) {
					for(int i=0;i<7;i++) device[i] = reply[n][i+2];
					msg[0] = Bluetooth.MSG_ACCEPT_CONNECTION;
					msg[1] = 1;
					Bluetooth.sendCommand(msg, 2);					
				}
				if (reply[n][1] == Bluetooth.MSG_CONNECT_RESULT) {
					Thread.sleep(200);
					Bluetooth.receiveReply(dummy,32);					
					if (dummy[0] == 0) {
						msg[0] = Bluetooth.MSG_OPEN_STREAM;
						msg[1] = reply[n][3];
						Bluetooth.sendCommand(msg, 2);
						Thread.sleep(100);
						Bluetooth.btSetCmdMode(0);
						cmdMode = false;
					} 
				}
				if (reply[n][1] == 0) { // Data
					if (reply[n][3] == 0x88) {
						msg [0] = 7;
						msg[1] = 0;
						msg[2] = 0x02;
						msg[3] = (byte) 0x88;
						msg[4] = 0;
						msg[5] = 2;
						msg[6] = 1;
						msg[7] = 3;
						msg[8] = 1;
						Bluetooth.btSend(msg, 9);						
					}
					if (reply[n][3] == 0x0B) {
						int mv = Battery.getVoltageMilliVolt();
						msg [0] = 5;
						msg[1] = 0;
						msg[2] = 0x02;
						msg[3] = (byte) 0x0B;
						msg[4] = 0;
						msg[5] = (byte) (mv & 0xFF);
						msg[6] = (byte) ((mv >> 8) & 0xFF);
						Bluetooth.btSend(msg, 7);						
					}
					
					// GETOUTPUTSTATE - Currently only returns tacho count
					if (reply[n][3] == 0x06) {
						byte port = reply[n][4]; 
						Motor m = null;
						if(port == 0)
							m = Motor.A;
						else if(port == 1)
							m = Motor.B;
						else m = Motor.C;
						int tacho = m.getTachoCount();
						
						byte mode = 0;
						if (m.isMoving()) mode = 0x01; 
						msg[0] = 25;
						msg[1] = 0;
						msg[2] = 0x02;
						msg[3] = 0x06;
						msg[4] = 0; // Status byte
						msg[5] = port;
						msg[6] = (byte)(m.getSpeed() * 100 / 900); // Power
						msg[7] = mode; // Only contains isMoving at moment
						msg[15] = (byte) (tacho & 0xFF);
						msg[16] = (byte) ((tacho >> 8) & 0xFF);
						msg[17] = (byte) ((tacho >> 16) & 0xFF);
						msg[18] = (byte) ((tacho >> 24) & 0xFF);
						Bluetooth.btSend(msg, 27);						
					}
					
					// SETOUTPUTSTATE
					if(reply[n][3] == 0x04) {
						byte motorid = reply[n][4];
						byte power = reply[n][5];
						int speed = (Math.abs(power) * 900) / 100;
						byte mode = reply[n][6];
						byte regMode = reply[n][7];
						byte turnRatio = reply[n][8];
						byte runState = reply[n][9];
						int tacholimit = (0xFF & reply[n][10]) | ((0xFF & reply[n][11]) << 8)| ((0xFF & reply[n][12]) << 16)| ((0xFF & reply[n][13]) << 24);
						
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
						
						msg[0] = 3;
						msg[1] = 0;
						msg[2] = 0x02;
						msg[3] = 0x04;
						msg[4] = 0; // Status byte
						Bluetooth.btSend(msg, 5);
					}
					
					// SETINPUTMODE
					if (reply[n][3] == 0x05) {
						byte port = reply[n][4];
						int sensorType = reply[n][5] & 0xFF;
						int sensorMode = reply[n][6] & 0xFF;
						SensorPort.PORTS[port].setTypeAndMode(sensorType, sensorMode);

						msg [0] = 3;
						msg[1] = 0;
						msg[2] = 0x02;
						msg[3] = 0x05;
						msg[4] = 0; // Status byte
						Bluetooth.btSend(msg, 5);						
					}
					
					// GETINPUTVALUES
					if (reply[n][3] == 0x07) {
						byte port = reply[n][4];
						SensorPort p;
						if(port == 0)
							p = SensorPort.S1;
						else if(port == 1)
							p = SensorPort.S2;
						else if(port == 2)
							p = SensorPort.S3;
						else p = SensorPort.S4;
						
						msg[0] = 16;
						msg[1] = 0;
						msg[2] = 0x02;
						msg[3] = 0x07;
						msg[4] = 0; // Status byte
						msg[5] = port;
						msg[6] = 1; // true if data is valid
						msg[7] = 1; // true if calibrated
						msg[8] = (byte)p.getType(); // Sensor type
						msg[9] = (byte)p.getMode(); // sensor mode
						int ADVal = p.readRawValue();
						msg[10] = (byte)ADVal;// Raw AD Value
						msg[11] = (byte)(ADVal>>>8);
						int normVal = p.readValue();
						msg[12] = (byte)normVal; // Normalized AD value
						msg[13] = (byte)(normVal>>>8);
						msg[14] = (byte)normVal; // Scaled value NOT IMPLEMENTED
						msg[15] = (byte)(normVal>>>8);
						msg[16] = (byte)normVal; // Calibrated value NOT IMPLEMENTED
						msg[17] = (byte)(normVal>>>8);
						Bluetooth.btSend(msg, 18);						
					}
					
					// LSGETSTATUS (I2C)
					if (reply[n][3] == 0x0E) {
						byte port = reply[n][4]; 
						msg[0] = 4;
						msg[1] = 0;
						msg[2] = 0x02;
						msg[3] = (byte) 0x0E;
						msg[4] = 0; // Status
						msg[5] = (byte)SensorPort.i2cBusyById(port); // Assume this returns bytes ready
						LCD.drawInt(msg[5], 6, 2);
						Bluetooth.btSend(msg, 6);						
					}
					
					// LSWRITE
					
					// LSREAD
				}
			}

			if (reply[n][0] != 0) {
				if (n < 8) n++;
				else {
					for(int i=0;i<8;i++) {
						for(int j=0;j<32;j++) reply[i][j] = reply[i+1][j];
						for(int j=0;i<32;i++) reply[8][j] = 0;
					}
				}
			}
			
			//LCD.clear();
			
			for(int i=0;i<8;i++) {

				LCD.drawInt(Bluetooth.btGetCmdMode(), 10, 0);
				if (reply[i][0] != 0) {
					LCD.drawInt(reply[i][1],0,i);
					if (reply[i][1] == Bluetooth.MSG_GET_VERSION_RESULT) {
						LCD.drawInt(reply[i][2], 3, i);
						LCD.drawInt(reply[i][3], 6, i);
					}
					if (reply[i][1] == Bluetooth.MSG_GET_DISCOVERABLE_RESULT) {
						LCD.drawInt(reply[i][2], 3, i);					
					}
					if (reply[i][1] == Bluetooth.MSG_CONNECT_RESULT) {
						LCD.drawInt(reply[i][2], 3, i);					
					}
					if (reply[i][1] == 0) { // Data
						LCD.drawInt(reply[i][2], 3, i);					
						LCD.drawInt(reply[i][3], 6, i);	
						LCD.drawInt(reply[i][0], 11, i);}
				}
			}
			LCD.refresh();
		}		
	}
}
