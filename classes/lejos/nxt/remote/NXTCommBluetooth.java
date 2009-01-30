package lejos.nxt.remote;

import java.io.*;
import lejos.nxt.comm.*;

/**
 * 
 * Initiates communication to a remote NXT. Used by NXTCommand
 * to implement the Lego Communications Protocol (LCP) over Bluetooth.
 *
 */
public class NXTCommBluetooth implements NXTCommRequest {    
	private static BTConnection btc;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	byte[] buf = new byte[64];
	
	public boolean open(String name, int mode) throws IOException {		
		btc = Bluetooth.connect(name, mode);
		if (btc == null) return false;
		
		dis = btc.openDataInputStream();
		dos = btc.openDataOutputStream();
			
		return true;
	}
	
	private void sendData(byte [] data) throws IOException {
		dos.write(data, 0, data.length);
		dos.flush();
	}
	
	private byte[] readData() throws IOException {	
		int len = 0;
		
		while (len == 0) len = btc.readPacket(buf, 64);
		byte [] data = new byte[len];
		for(int i=0;i<len;i++) data[i] = buf[i];
		return data;

	}
	
	public byte[] sendRequest(byte [] message, int replyLen) throws IOException {
		sendData(message);
		if (replyLen == 0) return new byte[0];
		return readData();
	}
	
	public void close() throws IOException {
		dis.close();
		dos.close();
		btc.close();
	}
}
