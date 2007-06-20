package lejos.pc.comm;

import java.io.*;

public interface NXTComm {
	public NXTInfo[] search(String name, int protocol);
	public boolean open(NXTInfo nxt);
	public void close();
	public byte[] sendRequest(byte [] message, int replyLen);
	public OutputStream getOutputStream();
	public InputStream getInputStream();
}
