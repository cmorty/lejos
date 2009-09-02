package lejos.pc.comm;

import javax.microedition.io.*;
import javax.bluetooth.*;
import java.io.*;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Implementation of NXTComm using the Bluecove libraries 
 * on Microsoft Windows. 
 * 
 * Should not be used directly - use NXTCommFactory to create
 * an appropriate NXTComm object for your system and the protocol
 * you are using.
 *
 */
public class NXTCommBluecove implements NXTComm, DiscoveryListener {
	private static Vector<RemoteDevice> devices;
    private static Vector<NXTInfo> nxtInfos;
	private StreamConnection con;
	private OutputStream os;
	private InputStream is;
	private NXTInfo nxtInfo;

	public NXTInfo[] search(String name, int protocol) throws NXTCommException {

		devices = new Vector<RemoteDevice>();
		nxtInfos = new Vector<NXTInfo>();

		if ((protocol & NXTCommFactory.BLUETOOTH) == 0)
			return new NXTInfo[0];

		synchronized (this) {
			try {
				LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(
						DiscoveryAgent.GIAC, this);
				try {
					wait();
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
				}
			} catch(Throwable t) {
				//System.err.println(e.getMessage());
				throw new NXTCommException("Bluetooth stack not detected",t); 
			}
		}

		for (Enumeration<RemoteDevice> enum_d = devices.elements(); enum_d.hasMoreElements();) {
			RemoteDevice d = enum_d.nextElement();

			try {
				nxtInfo = new NXTInfo();

				nxtInfo.name = d.getFriendlyName(false);
				if (nxtInfo.name == null || nxtInfo.name.length() == 0)
					nxtInfo.name = "Unknown";
				nxtInfo.deviceAddress = d.getBluetoothAddress();
				nxtInfo.protocol = NXTCommFactory.BLUETOOTH;

				if (name == null || name.equals(nxtInfo.name))
					nxtInfos.addElement(nxtInfo);
				else
					continue;

				System.out.println("Found: " + nxtInfo.name);
 
				// We want additional attributes, ServiceName (0x100),
				// ServiceDescription (0x101) and ProviderName (0x102).

				int[] attributes = { 0x100, 0x101, 0x102 };

				UUID[] uuids = new UUID[1];
				uuids[0] = new UUID("1101", true); // Serial Port
				synchronized (this) {
					try {
						LocalDevice.getLocalDevice().getDiscoveryAgent()
								.searchServices(attributes, uuids, d, this);
						try {
							wait();
						} catch (InterruptedException e) {
							System.err.println(e.getMessage());
						}
					} catch (BluetoothStateException e) {
						System.err.println(e.getMessage());
					}
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
				}

			} catch (IOException e) {
				System.err.println(e.getMessage());

			}

		}
		NXTInfo[] nxts = new NXTInfo[nxtInfos.size()];
		for (int i = 0; i < nxts.length; i++)
			nxts[i] = (NXTInfo) nxtInfos.elementAt(i);
		return nxts;
	}

	public boolean open(NXTInfo nxt, int mode) throws NXTCommException {

        if (mode == RAW) throw new NXTCommException("RAW mode not implemented");
		// Construct URL if not present

		if (nxt.btResourceString == null || nxt.btResourceString.length() < 5
				|| !(nxt.btResourceString.substring(0, 5).equals("btspp"))) {
			nxt.btResourceString = "btspp://"
					+ stripColons(nxt.deviceAddress)
					+ ":1;authenticate=false;encrypt=false";
		}

		try {
			con = (StreamConnection) Connector.open(nxt.btResourceString);
			os = con.openOutputStream();
			is = con.openInputStream();
			nxt.connectionState = (mode == LCP ? NXTConnectionState.LCP_CONNECTED : NXTConnectionState.PACKET_STREAM_CONNECTED);
			return true;
		} catch (IOException e) {
			nxt.connectionState = NXTConnectionState.DISCONNECTED;
			throw new NXTCommException("Open of " + nxt.name + " failed: " + e.getMessage());
		}
	}

    public boolean open(NXTInfo nxt) throws NXTCommException
    {
        return open(nxt, PACKET);
    }

	public void close() throws IOException {
		if (os != null)
			os.close();
		if (is != null)
			is.close();
		if (con != null)
			con.close();
	}

	/**
	 * Sends a request to the NXT brick.
	 * 
	 * @param message
	 *            Data to send.
	 */
	public synchronized byte[] sendRequest(byte[] message, int replyLen)
			throws IOException {

		// length of packet (Least and Most significant byte)
		// * NOTE: Bluetooth only. 
		int LSB = message.length;
		int MSB = message.length >>> 8;

		if (os == null)
			return new byte[0];

		// Send length of packet:
		os.write((byte) LSB);
		os.write((byte) MSB);

		os.write(message);
		os.flush();

		if (replyLen == 0)
			return new byte[0];

		byte[] reply = null;
		int length = -1;

		if (is == null)
			return new byte[0];

		do {
			length = is.read(); // First byte specifies length of packet.
		} while (length < 0);

		int lengthMSB = is.read(); // Most Significant Byte value
		length = (0xFF & length) | ((0xFF & lengthMSB) << 8);
		reply = new byte[length];
		int len = is.read(reply);
		if (len != replyLen) throw new IOException("Unexpected reply length");

		return (reply == null) ? new byte[0] : reply;
	}

	public byte[] read() throws IOException {

        int lsb = is.read();
		if (lsb < 0) return null;
		int msb = is.read();
        if (msb < 0) return null;
        int len = lsb | (msb << 8);
		byte[] bb = new byte[len];
		for (int i=0;i<len;i++) bb[i] = (byte) is.read();

		return bb;
	}
	
    public int available() throws IOException {
        return 0;
    }

	public void write(byte[] data) throws IOException {
        os.write((byte)(data.length & 0xff));
        os.write((byte)((data.length >> 8) & 0xff));
		os.write(data);
		os.flush();
	}

	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		// System.out.println("Found Device, class: " +
		// cod.getMajorDeviceClass() + "/" + cod.getMinorDeviceClass());
		if (cod.getMajorDeviceClass() == 2048 && cod.getMinorDeviceClass() == 4)
			devices.addElement(btDevice);
	}

	public synchronized void inquiryCompleted(int discType) {
		// if (discType == INQUIRY_COMPLETED) System.out.println("Inquiry
		// completed");
		// else System.out.println("Inquiry Failed");
		notifyAll();
	}

	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		// System.out.println(servRecord.length + " service(s) discovered");
		// Should only be one service on a NXT
		if (servRecord.length != 1)
			return;
		nxtInfo.btResourceString = servRecord[0].getConnectionURL(
				ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
		// System.out.println("Setting url to : " + nxtInfo.btResourceString);
	}

	public synchronized void serviceSearchCompleted(int transID, int respCode) {
		// System.out.println("Service search completed: respCode = " +
		// respCode);
		notifyAll();
	}

	public OutputStream getOutputStream() {
		return new NXTCommOutputStream(this);
	}

	public InputStream getInputStream() {
		return new NXTCommInputStream(this);
	}

	public String stripColons(String s) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c != ':') {
				sb.append(c);
			}
		}

		return sb.toString();
	}
}
