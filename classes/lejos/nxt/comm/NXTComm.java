package lejos.nxt.comm;

import java.io.*;

public class NXTComm {
	private static BTConnection btc;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	byte[] buf = new byte[32];
	
	public boolean open(String name) throws IOException {
		BTRemoteDevice btrd = Bluetooth.getKnownDevice(name);	
		if (btrd == null) return false;
		
		btc = Bluetooth.connect(btrd);
		if (btc == null) return false;
		
		dis = btc.openDataInputStream();
		dos = btc.openDataOutputStream();
			
		return true;
	}
	
	public void sendData(byte [] data) throws IOException {
		dos.write(data, 0, data.length);
		dos.flush();
	}
	
	public byte[] readData() throws IOException {	
		int len = 0;
		
		while (len == 0) len = Bluetooth.readPacket(buf, 32);
		byte [] data = new byte[len];
		for(int i=0;i<len;i++) data[i] = buf[i];
		return data;

	}
	
	public void close() throws IOException {
		dis.close();
		dos.close();
		btc.close();
	}
}
