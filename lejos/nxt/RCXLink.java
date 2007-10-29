package lejos.nxt;

/**
 * Supports Mindsensors NRLink RCX IR adapter.
 * 
 * @author Lawrie Griffiths <lawrie.griffiths@ntlworld.com>
 *
 */
public class RCXLink extends I2CSensor {
	
	private byte[] buf = new byte[4];
	private byte[] buf2 = new byte[1];
	
	public RCXMotor A = new RCXMotor(new RCXRemoteMotorPort(this,0));
	public RCXMotor B = new RCXMotor(new RCXRemoteMotorPort(this,1));
	public RCXMotor C = new RCXMotor(new RCXRemoteMotorPort(this,2));
	
	public RCXLink(I2CPort port) {
		super(port);
	}

	public void runMacro(int addr) {
		buf[0] = 0x52;
		buf[1] = (byte) addr;
		
		sendData(0x41, buf, 2);
	}
	
	public void beep() {
		runMacro(0x39);
	}
	
	public void runProgram(int prog) {
		runMacro(0x05 + (prog*4));
	}
	
	public void forwardStep(int id) {
		runMacro(0x21 + (id*8));
	}
	
	public void backwardStep(int id) {
		runMacro(0x25 + (id*8));
	}
	
	public void setRCXRangeShort() {
		runMacro(0x01);
	}
	
	public void setRCXRangeLong() {
		runMacro(0x04);
	}
	
	public void powerOff() {
		runMacro(0x07);
	}
	
	public void stop() {
		runMacro(0x1D);
	}
	
	public void flush() {
		buf[0] = 0x46;
		sendData(0x41, buf, 1);
	}
	
	public void setDefault() {
		buf[0] = 0x44;
		sendData(0x41, buf, 1);
	}
	
	public void setHighSpeed() {
		buf[0] = 0x48;
		sendData(0x41, buf, 1);
	}
	
	public void setRangeLong() {
		buf[0] = 0x4c;
		sendData(0x41, buf, 1);
	}

	public void setRangeShort() {
		buf[0] = 0x53;
		sendData(0x41, buf, 1);
	}
	
	public void setAPDAOn() {
		buf[0] = 0x4E;
		sendData(0x41, buf, 1);
	}
	
	public void setAPDAOff() {
		buf[0] = 0x4F;
		sendData(0x41, buf, 1);
	}
	
	public void defineMacro(int addr, byte[] macro) {
		buf2[0] = (byte) macro.length;
		sendData((byte) addr,buf2,1);
		sleep();
		sendData((byte) addr+1, macro, macro.length);
	}
	
	public int getStatus() {
		getData(0x41, buf, 1);
		return buf[0] & 0xFF;
	}
	
	public int bytesAvailable() {
		getData(0x40, buf, 1);
		return buf[0] & 0xFF;
	}
	
	public void ping() {
		buf[0] = 0x10;
		defineAndRun(buf,1);
	}
	
	public void sendF7(int msg) {
		buf[0] = (byte) 0xF7;
		buf[1] = (byte) (msg & 0xFF);
		defineAndRun(buf,2);
	}
	
	public void sendRemoteCommand(int msg) {
		buf[0] = (byte) 0xD2;
		buf[1] = (byte) (msg >> 8);
		buf[2] = (byte) (msg & 0xFF);
		defineAndRun(buf,3);
	}
	
	public void setMotorPower(int id, int power) { 
		buf[0] = 0x13;
		buf[1] = (byte) (1 << id);
		buf[2] = 2;
		buf[3] = (byte) power;
		defineMacro(0x78, buf); // Bug: sendData cannot send more than 3 bytes
		sleep();
		buf[0] = (byte) power;
		sendData(0x7C, buf,1);
		sleep();
		runMacro(0x78);
	}
	
	public void stopMotor(int id) {
		buf[0] = 0x21;
		buf[1] = (byte) ((1 << id) | 0x40);
		defineAndRun(buf,2);
	}
	
	public void startMotor(int id) {
		buf[0] = 0x21;
		buf[1] = (byte) ((1 << id) | 0x80);
		defineAndRun(buf,2);
	}
	
	public void fltMotor(int id) {
		buf[0] = 0x21;
		buf[1] = (byte) (1 << id);
		defineAndRun(buf,2);
	}
	
	public void forward(int id) {
		buf[0] = (byte) 0xe1;
		buf[1] = (byte) ((1 << id) | 0x80);
		defineAndRun(buf,2);
	}
	
	public void backward(int id) {
		buf[0] = (byte) 0xe1;
		buf[1] = (byte) (1 << id);
		defineAndRun(buf,2);
	}
	
	public void setRawMode() {
		buf[0] = 0x55;
		sendData(0x41, buf, 1);
	}
	
	public void sendBytes(byte[] data, int len) {
		sendData(0x42,data,len);
		sleep();
		buf[0] = (byte) len;
		sendData(0x40, buf, 1);
	}
	
	public int readBytes(byte [] data) {
		getData(0x40,buf,1);
		int numBytes = buf[0];
		if (numBytes > 0) {
			if (numBytes > data.length) numBytes = data.length;
			sleep();
			getData(0x42,data,numBytes);
		}
		return numBytes;
	}
	
	private void sleep() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {}
	}
	
	private void defineAndRun(byte[] macro, int len) {
		buf2[0] = (byte) len;
		sendData(0x78,buf2,1);
		sleep();
		sendData(0x79, macro, len);
		sleep();		
		runMacro(0x78);
	}
}
