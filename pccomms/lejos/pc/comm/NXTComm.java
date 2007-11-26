package lejos.pc.comm;

import java.io.*;

public interface NXTComm {
	public NXTInfo[] search(String name, int protocol) throws NXTCommException;
	public boolean open(NXTInfo nxt) throws NXTCommException;
	public void close() throws IOException;
	public byte[] sendRequest(byte [] message, int replyLen) throws IOException;
	public byte[] read() throws IOException;
	public int available() throws IOException;
	public void write(byte [] data) throws IOException;
	public OutputStream getOutputStream();
	public InputStream getInputStream();
}
