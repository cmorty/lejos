package lejos.pc.comm;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

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

	public NXTInfo[] search(String name) throws NXTCommException {

		devices = new Vector<RemoteDevice>();
		nxtInfos = new Vector<NXTInfo>();

		synchronized (this) {
			try {
				LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(
						DiscoveryAgent.GIAC, this);
				try {
					wait();
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
				}
			} catch(Exception e) {
				//System.err.println(e.getMessage());
				throw new NXTCommException("Bluetooth stack not detected", e); 
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
			nxts[i] = nxtInfos.elementAt(i);
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
			throw new NXTCommException("Open of " + nxt.name + " failed.", e);
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

		if (os == null)
			return new byte[0];

		this.write(message);
		
		if (replyLen == 0 || is == null)
			return new byte[0];

		return this.read();
	}

	public byte[] read() throws IOException {

		int lengthLSB = is.read(); // First byte specifies length of packet.
		int lengthMSB = is.read(); // Most Significant Byte value
		if (lengthLSB < 0 || lengthMSB < 0)
			throw new EOFException("unable to read reply packet length");
		
		int offset = 0;
		int length = (0xFF & lengthLSB) | ((0xFF & lengthMSB) << 8);
		byte[] reply = new byte[length];

		while (length > 0)
		{
			int len = is.read(reply, offset, length);
			if (len < 0)
				throw new EOFException("premature end of reply");
			
			offset += len;
			length -= len;
		}
		
		return reply;
	}
	
    public int available() throws IOException {
        return 0;
    }

	public void write(byte[] data) throws IOException {
		// Send length of packet (Least and Most significant byte)
		// * NOTE: Bluetooth only. 
        os.write((byte)data.length);
        os.write((byte)(data.length >>> 8));
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
