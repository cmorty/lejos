import lejos.nxt.*;

public class BTTest {
	
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
						int tacho = Motor.getTachoCountById(port);
						LCD.drawInt(tacho, 9, 3);
						LCD.refresh();
						msg[0] = 25;
						msg[1] = 0;
						msg[2] = 0x02;
						msg[3] = 0x06;
						msg[4] = port;
						msg[15] = (byte) (tacho & 0xFF);
						msg[16] = (byte) ((tacho >> 8) & 0xFF);
						msg[17] = (byte) ((tacho >> 16) & 0xFF);
						msg[18] = (byte) ((tacho >> 24) & 0xFF);
						Bluetooth.btSend(msg, 27);						
					}
					
					// Motor control
					if(reply[n][3] == 0x04) {
						Motor m = null;
						byte motorid = reply[n][4];
						byte power = reply[n][5];
						LCD.drawInt(motorid, 9, 2); // = 1 !!
						LCD.refresh();
						if(motorid == 0)
							m = Motor.A;
						else if (motorid == 1)
							m = Motor.B;
						else m = Motor.C;
						for(int i=0;i<reply[n].length;i++) {
							LCD.drawString("reply          ",3,2);
							LCD.drawInt(reply[n][i], 12, 2);
							LCD.drawInt(i, 9, 2);
							LCD.refresh();
							while(!Button.ENTER.isPressed()) {}
							Thread.sleep(250);
						}
						
						int tacholimit = (0xFF & reply[n][10]) | ((0xFF & reply[n][11]) << 8)| ((0xFF & reply[n][12]) << 16)| ((0xFF & reply[n][13]) << 24);
						LCD.drawInt(tacholimit, 9, 3);
						LCD.refresh();
						
						//if(power < 0) tacholimit = -tacholimit;
						m.rotate(tacholimit); // Doesn't return until done.
						msg[0] = 3;
						msg[1] = 0;
						msg[2] = 0x02;
						msg[3] = 0x04;
						msg[4] = 0; // Status byte
						Bluetooth.btSend(msg, 5);
					}
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
