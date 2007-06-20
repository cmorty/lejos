package lejos.pc.comm;

import java.io.InputStream;
import java.io.OutputStream;

public class NXTCommBluez implements NXTComm {

	private static final String BDADDR_ANY = "00:00:00:00:00:00";

	private int sk = -1;

	static {
		System.loadLibrary("jbluez");
	}
	
	public NXTInfo[] search(String name, int protocol) {
		String btAddress = search();
		if (btAddress == null) return new NXTInfo[0];
		else {
			NXTInfo nxtInfo = new NXTInfo();
			System.out.println("Setting address to " + btAddress);
			nxtInfo.btDeviceAddress = btAddress;
			nxtInfo.name = "Unknown";
			nxtInfo.protocol = NXTCommand.BLUETOOTH;
			NXTInfo[] nxts = new NXTInfo[1];
			nxts[0] = nxtInfo;
			return nxts;
		}
	}

	public void close() {
		try {
			rcSocketShutdown(sk);
		} catch (BlueZException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			if (sk != -1) {
				try {
					rcSocketClose(sk);
				} catch (BlueZException e1) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
				sk = -1;
			}
		}
	}

	public boolean open(NXTInfo nxt) {
		try {
			open(BDADDR_ANY, nxt.btDeviceAddress, 1);
			return true;
		} catch (BlueZException e) {
			System.out.println("Open failed");
			return false;
		}
		
	}

	public byte [] sendRequest(byte[] request, int replyLen) {
		
		// add lsb & msb
		byte[] lsb_msb = new byte[2];
		lsb_msb[0] = (byte) request.length;
		lsb_msb[1] = (byte) 0x00;
		request = concat(lsb_msb, request);
	
		try {
			rcSocketSend(sk, request);
		} catch (BlueZException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		if (replyLen == 0) return new byte[0];
		
		byte[] data = null;
		try {
			data = rcSocketRecv(sk);
		} catch (BlueZException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		// remove lsb & msb
		data = subArray(data, 2, data.length);

		return data;
	}

	private void open(String l_bdaddr, String r_bdaddr, int channel) throws BlueZException {
		boolean ok = false;

		try {
			System.out.println("Creating socket");
			sk = rcSocketCreate();
			System.out.println("Binding");
			rcSocketBind(sk, l_bdaddr);
			System.out.println("Connecting");
			rcSocketConnect(sk, r_bdaddr, channel);

			ok = true;
		} finally {
			if (!ok) {
				if (sk != -1) {
					rcSocketClose(sk);
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
	
	public OutputStream getOutputStream() {
		return null;		
	}
	
	public InputStream getInputStream() {
		return null;		
	}
	
	native private String search();
	
	native private int rcSocketCreate() throws BlueZException;

	native private void rcSocketBind(int sk, String bdaddr) throws BlueZException;

	native private void rcSocketConnect(int sk, String bdaddr, int channel) throws BlueZException;

	native private void rcSocketSend(int sk, byte[] data) throws BlueZException;

	native private byte[] rcSocketRecv(int sk) throws BlueZException;

	native private void rcSocketShutdown(int sk) throws BlueZException;

	native private void rcSocketClose(int sk) throws BlueZException;

}
