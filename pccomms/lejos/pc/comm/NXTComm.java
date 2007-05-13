package lejos.pc.comm;

public interface NXTComm {
	public NXTInfo[] search(String name, int protocol);
	public void open(NXTInfo nxt);
	public void close();
	public void sendData(byte [] message);
	public byte[] readData(int len);	
}
