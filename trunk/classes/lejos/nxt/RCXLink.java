package lejos.nxt;

public class RCXLink extends I2CSensor {
	
	private byte[] buf = new byte[2];
	
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
	
	public void flush() {
		buf[0] = 046;
		sendData(0x41, buf, 1);
	}
	
	public void setDefault() {
		buf[0] = 044;
		sendData(0x41, buf, 1);
	}
	
}
