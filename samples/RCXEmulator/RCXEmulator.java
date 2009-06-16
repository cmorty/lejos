import lejos.nxt.*;
import lejos.nxt.addon.*;
import lejos.nxt.rcxcomm.*;

/*
 * Responds to RCX Remote control
 * 
 */
public class RCXEmulator {

	public static void main(String[] args) {
		Serial.setPort(SensorPort.S1);
		RCXLink link = Serial.getLink();
		int msgCount=0;
		link.setDefaultSpeed();
		link.flush();
		
		byte[] packet = new byte[10];
		byte[] reply = new byte[10];
		int c1, c2;
		//String cmd = "Cmd:";
		//String remote = "Remote";
		
		while (true) {;
			if (Serial.isPacketAvailable()) {
				int numBytes = Serial.readPacket(packet);
				//LCD.drawString(cmd, 0, 0);
				//LCD.drawInt((packet[0] & 0xF7), 4,  5, 0);
				//LCD.drawInt(numBytes, 4, 11, 0);
				if ((packet[0] & 0xF7) == 210) { // Remote command
					reply[0] = (byte) (0xFF - (packet[0] & 0xFF));
					Serial.sendPacket(reply, 0, 1);
					c1 = packet[1] & 0xFF;
					c2 = packet[2] & 0xFF;
					//LCD.drawString(remote,0,1);
					//LCD.drawInt(c1, 4, 7, 1);
					//LCD.drawInt(c2, 4, 11, 1);
					if (c1 != 0 || c2 != 0) LCD.clear();
					LCD.drawInt(++msgCount, 4, 0, 4);
					if (c1 == 0) {
						if (c2 == 1) message1();
						else if (c2==0x02) message2();
						else if (c2==0x04) message3();
						else if (c2==0x08) incMotorA();
						else if (c2==0x10) incMotorB();
						else if (c2==0x20) incMotorC();
						else if (c2==0x40) decMotorA();
						else if (c2==0x80) decMotorB();
					} else if (c2 == 0) {
						if (c1==0x01) decMotorC();
						else if (c1==0x02) program1();
						else if (c1==0x04) program2();
						else if (c1==0x08) program3();
						else if (c1==0x10) program4();
						else if (c1==0x20) program5();
						else if (c1==0x40) stopAll();
						else if (c1==0x80) sound();
					}
				}
				LCD.refresh();
			}
		}
	}
	
	private static String msg1 = "MSG1";
	private static String msg2 = "MSG2";
	private static String msg3 = "MSG3";
	private static String incA = "Motor A fwd";
	private static String incB = "Motor B fwd";
	private static String incC = "Motor C fwd";
	private static String decA = "Motor A bwd";
	private static String decB = "Motor B bwd";
	private static String decC = "Motor C bwd";
	private static String prog1 = "Program 1";
	private static String prog2 = "Program 2";
	private static String prog3 = "Program 3";
	private static String prog4 = "Program 4";
	private static String prog5 = "Program 5";
	private static String stop = "Stop All";
	private static String beep = "Sound";
	
	private static void message1() {
	    display(msg1);	
	}
	
	private static void message2() {
		display(msg2);
	}
	
	private static void message3() {
		display(msg3);
	}
	
	private static void incMotorA() {
		display(incA);
	}
	
	private static void incMotorB() {
		display(incB);
	}
	
	private static void incMotorC() {
		display(incC);
	}
	
	private static void decMotorA() {
		display(decA);
	}
	
	private static void decMotorB() {
		display(decB);
	}
	
	private static void decMotorC() {
		display(decC);
	}
	
	private static void display(String msg) {
		LCD.drawString(msg, 0, 3);
		LCD.refresh();
	}
	
	private static void program1() {
	    display(prog1);	
	}
	
	private static void program2() {
	    display(prog2);	
	}
	
	private static void program3() {
	    display(prog3);	
	}
	
	private static void program4() {
	    display(prog4);	
	}
	
	private static void program5() {
	    display(prog5);	
	}
	
	private static void stopAll() {
	    display(stop);	
	}
	
	private static void sound() {
	    display(beep);
	    Sound.beep();
	}
}
