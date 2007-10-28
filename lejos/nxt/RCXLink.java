package lejos.nxt;

public class RCXLink extends I2CSensor {
	
	private byte[] buf = new byte[2];
	
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
	
	public void forward(int id) {
		runMacro(0x21 + (id*8));
	}
	
	public void backward(int id) {
		runMacro(0x25 + (id*8));
	}
	
	public void setRCXShortRange() {
		runMacro(0x01);
	}
	
	public void setRCXLongRange() {
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
	
	public void setLongRange() {
		buf[0] = 0x4c;
		sendData(0x41, buf, 1);
	}

	public void setShortRange() {
		buf[0] = 0x53;
		sendData(0x41, buf, 1);
	}
	
	public void setAPDAOn() {
 		getData(0x42,data,numBytes);
		}
		return numBytes;
	}
}
