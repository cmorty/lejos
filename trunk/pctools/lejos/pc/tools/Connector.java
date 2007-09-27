package lejos.pc.tools;

import java.io.*;

import lejos.pc.comm.*;

/**
 * conneccts to a NXT using either Bluetooth or USB and builds input and output
 * data streams.
 * 
 * @author Roger Glassey 22/08/2007
 */

public class Connector {
	private boolean _usb = false;
	DataInputStream dataIn;
	DataOutputStream dataOut;
	InputStream is;
	OutputStream os;
	NXTComm nxtComm;

	/**
	 * 
	 * @param NXT
	 *            can be the friendly name of the NXT or a 16 character address
	 * @param useUSB
	 * @return true if connection was made
	 */
	public boolean startConnector(String NXT, boolean useUSB)
			throws NXTCommException {
		NXTInfo[] nxtInfo;
		_usb = useUSB;
		if (_usb) {
			nxtComm = new NXTCommLibnxt();
			System.out.println("searching");
			nxtInfo = nxtComm.search(null, NXTCommFactory.USB);
			if (nxtInfo.length == 0) {
				System.out.println("No NXT Found");
				return false;
			}
			nxtComm.open(nxtInfo[0]);
			System.out.println(" Opened " + nxtInfo[0].name);
		} else {
			nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
			if (NXT == null || NXT == " ") {
				System.out.println("search for all");
				nxtInfo = nxtComm.search(NXT, NXTCommFactory.BLUETOOTH);
			} else if (NXT.length() < 8) {
				System.out.println("search for " + NXT);
				nxtInfo = nxtComm.search(NXT, NXTCommFactory.BLUETOOTH);

			} else {
				nxtInfo = new NXTInfo[1];
				nxtInfo[0] = new NXTInfo("unknown ", NXT);// NXT is actually
															// address
			}
			if (nxtInfo.length == 0) {
				System.out
						.println("No NXT Found:  is BT adatper on? is NXT on? ");
				System.exit(1);
			}
			System.out.println("Connecting to " + nxtInfo[0].name + " "
					+ nxtInfo[0].btDeviceAddress);
			boolean opened = nxtComm.open(nxtInfo[0]);
			if (!opened) {
				System.out.println("Failed to open " + nxtInfo[0].name + " "
						+ nxtInfo[0].btDeviceAddress);
				System.exit(1);
			}
			System.out.println("Connected to " + nxtInfo[0].name);
		}
		is = nxtComm.getInputStream();
		dataIn = new DataInputStream(nxtComm.getInputStream());
		os = nxtComm.getOutputStream();
		dataOut = new DataOutputStream(os);
		return true;
	}

	/**
	 * @return the InputStream for this connection;
	 */
	public InputStream getInputStream() {
		return is;
	}

	/**
	 * @return the DataInputStream for this connection;
	 */
	public DataInputStream getDataIn() {
		return dataIn;
	}

	/**
	 * @return the OutputSteram for this connection;
	 */
	public OutputStream getOutputStream() {
		return os;
	}

	/**
	 * @return the DataOutputStream for this connection
	 */
	public DataOutputStream getDataOut() {
		return dataOut;
	}

	public static void main(String[] args) {
		try {
			Connector con = new Connector();
			con.startConnector("NXT", false);
		} catch (Throwable t) {
			System.err.println(t.getMessage());
		}
		// DataInputStream din= btm.getDataIn();
		// while(true)
		// {
		// try{ System.out.println(din.readFloat());} catch(IOException e){}
		// }

	}

}
