package lejos.pc.comm;

import java.io.*;

public interface NXTComm {
	public NXTInfo[] search(String name, int protocol);
	public boolean open(NXTInfo nxt);
	public void close() throws IOException;
	public byte[] sendRequest(byte [] message, int replyLen) throws IOException;
	public byte[] read() throws IOException;
	public void write(byte [] data) throws IOException;
	public OutputStream getOutputStream();
	public InputStream getInputStream();
}
