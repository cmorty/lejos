package lejos.nxt;

public class RPiI2c {
	public native int open(int address);
	public native int read(byte[] data, int len);
	public native int write(byte[] data, int len);
	public native int close();
}
