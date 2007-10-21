package lejos.nxt;

public class RCXMotorMultiplexer extends I2CSensor implements BasicMotorPort {
	private byte[] buf = new byte[1];
	private int id;
	
	public RCXMotorMultiplexer(I2CPort port, int id) {
		super(port);
		this.id = id;
		setAddress(0x5A);
	}
	
	public void setSpeed(int speed) {
		buf[0] = (byte) speed;
		sendData(0x43 + (id*2), buf, 1);
	}
	
	public int getSpeed() {
		getData(0x43 + (id*2), buf, 1);
	    return buf[0] & 0xFF;
	}
	
	public void setDirection(int direction) {
		buf[0] = (byte) direction;
		sendData(0x42 + (id*2), buf, 1);
	}
	
	public int getDirection() {
		getData(0x42 + (id*2), buf, 1);
	    return buf[0] & 0xFF;
	}
	
	public void controlMotor(int power, int mode) {
		int mmMode = mode;
		if (mmMode == 4) mmMode = 0; // float
		int mmPower = (int) ((float)power * 2.55f);
		if (mmMode == 3) mmPower = 255; // Maximum breaking
		setDirection(mmMode);
		setSpeed(mmPower);
	}
}
