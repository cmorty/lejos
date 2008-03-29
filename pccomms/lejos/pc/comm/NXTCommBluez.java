package lejos.pc.comm;

import java.io.*;

/**
 * Implementation of NXTComm using the the jbluez library 
 * on Linux or Unix systems. 
 * 
 * Should not be used directly - use NXTCommFactory to create
 * an appropriate NXTComm object for your system and the protocol
 * you are using.
 *
 */
public class NXTCommBluez implements NXTComm {

	private static final String BDADDR_ANY = "00:00:00:00:00:00";

	private int sk = -1;

	static {
		System.loadLibrary("jbluez");
	}
	
	public NXTInfo[] search(String name, int protocol) {
		String[] btString = null;
		
		try {
			btString = search(name);
		} catch (BlueZException e) {
			System.err.println(e.getMessage());	
		}
		if (btString == null) return new NXTInfo[0];
		else {
			NXTInfo[] nxts = new NXTInfo[btString.length];
			for(int i=0;i<btString.length;i++) {
				NXTInfo nxtInfo = new NXTInfo();
				if (btString[i] == null) {
					System.err.println("Null btString");
					return new NXTInfo[0];
				}
				int sep = btString[i].indexOf("::");
				//System.out.println("Setting address to " + btAddress);
				nxtInfo.btDeviceAddress =  btString[i].substring(sep+2);
				nxtInfo.name = btString[i].substring(0, sep);
				nxtInfo.protocol = NXTCommFactory.BLUETOOTH;
				nxtInfo.btResourceString = btString[i];
				
				nxts[i] = nxtInfo;			
			}
			return nxts;
		}
	}

	public void close() throws IOException{
		try {
			rcSocketShutdown(sk);
		} catch (IOException ioe) {
			//System.err.println("Shutdown failed");
		}
		if (sk != -1) rcSocketClose(sk);
		sk = -1;
	}

	public boolean open(NXTInfo nxt) {
		try {
			open(BDADDR_ANY, nxt.btDeviceAddress, 1);
			return true;
		} catch (BlueZException e) {
			System.err.println("Error from open: " + e.getMessage());
			return false;
		}
		
	}

	public byte [] sendRequest(byte[] request, int replyLen) throws IOException {
		
		// add lsb & msb
		byte[] lsb_msb = new byte[2];
		lsb_msb[0] = (byte) request.length;
		lsb_msb[1] = (byte) 0x00;
		request = concat(lsb_msb, request);
	
	    rcSocketSend(sk, request);
		
		if (replyLen == 0) return new byte[0];
		
		byte[] data = null;
	    data = rcSocketRecv(sk);
	
		// remove lsb & msb
		data = subArray(data, 2, data.length);

		return data;
	}

	private void open(String l_bdaddr, String r_bdaddr, int channel) throws BlueZException {
		boolean ok = false;

		try {
			//System.out.println("Creating socket");
			sk = rcSocketCreate();
			//System.out.println("Binding");
			rcSocketBind(sk, l_bdaddr);
			//System.out.println("Connecting");
			rcSocketConnect(sk, r_bdaddr, channel);

			ok = true;
		} finally {
			if (!ok) {
				if (sk != -1) {
					try {
						rcSocketClose(sk);
					} catch (IOException ioe) {}
					sk = -1;
				}
			}
		}
	}
	
	private byte[] concat(byte[] data1, byte[] data2) {
		int l1 = data1.length;
		int l2 = data2.length;
		
		byte[] data = new byte[l1 + l2];
		System.arraycopy(data1, 0, data, 0, l1);
		System.arraycopy(data2, 0, data, l1, l2);
		
		return data;
	}
		
	
	private byte[] subArray(byte[] data, int start, int end) {	

		byte[] result = new byte[end - start];
		System.arraycopy(data, start, result, 0, end - start);

		return result;
	}
	
	public byte [] read () throws IOException {
		byte [] packet = rcSocketRecv(sk); // Can read multiple packets
		if (packet == null || packet.length == 0) return null;
		int len = packet.length, dataLen = 0;
		int i = 0, j = 0;
		while (i < len-2) {
			int lsb = packet[i++];
			int msb = packet[i++];
			if (msb != 0)
				throw new IOException("Packet more than 255 bytes");
            dataLen += lsb;
            i += lsb;
		}
		if (i != len) throw new IOException("Incomplete packet");	
		byte [] data = new byte [dataLen];
		
		i = 0;
		while (i < len-2) {
			int lsb = packet[i++];
			i++; // Skip msb
            for(int k = 0;k<lsb;k++) data[j++] = packet[i+k];
            i += lsb;
		}
		return data;
	}
	
	public int available() throws IOException {
		return 0;
	}
	
	public void write(byte[] data) throws IOException {
		byte[] lsb_msb = new byte[2];
		lsb_msb[0] = (byte) data.length;
		lsb_msb[1] = (byte) ((data.length >> 8) & 0xff);
		rcSocketSend(sk, concat(lsb_msb, data));
	}
	
	public OutputStream getOutputStream() {
		return new NXTCommOutputStream(this);		
	}
	
	public InputStream getInputStream() {
		return new NXTCommInputStream(this);		
	}
	
	native private String[] search(String name) throws BlueZException;
	
	native private int rcSocketCreate() throws BlueZException;

	native private void rcSocketBind(int sk, String bdaddr) throws BlueZException;

	native private void rcSocketConnect(int sk, String bdaddr, int channel) throws BlueZException;

	native public void rcSocketSend(int sk, byte[] data) throws IOException;

	native public byte[] rcSocketRecv(int sk) throws IOException;

	native private void rcSocketShutdown(int sk) throws IOException;

	native private void rcSocketClose(int sk) throws IOException;

}
