package lejos.pc.comm;

import javax.microedition.io.*;
import javax.bluetooth.*;
import java.io.*;
import java.util.Vector;
import java.util.Enumeration;

public class NXTCommBluecove implements NXTComm, DiscoveryListener  {
	private static Vector devices, nxtInfos;
	private StreamConnection con;
	private OutputStream os;
	private InputStream is;
    private NXTInfo nxtInfo;

	public NXTInfo[] search(String name, int protocol) {
		
		devices = new Vector();
        nxtInfos = new Vector();

        if ((protocol | NXTCommand.BLUETOOTH) == 0) return new NXTInfo[0];

		synchronized (this) {
			try {
				LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, this);
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (BluetoothStateException e) {
				e.printStackTrace();
			}
		}

		for (Enumeration enum_d = devices.elements(); enum_d.hasMoreElements();) {
			RemoteDevice d = (RemoteDevice) enum_d.nextElement();

			try {	
                nxtInfo = new NXTInfo();

                nxtInfo.name = d.getFriendlyName(false);
                if (nxtInfo.name == null || nxtInfo.name.length() == 0)
                	nxtInfo.name = "Unknown";
				nxtInfo.btDeviceAddress = d.getBluetoothAddress();
                nxtInfo.protocol = NXTCommand.BLUETOOTH;

                if (name == null || name.equals(nxtInfo.name)) nxtInfos.addElement(nxtInfo);
				else continue;

                System.out.println("Found: " + nxtInfo.name);
                
      	 		// We want additional attributes, ServiceName (0x100),
    	 		// ServiceDescription (0x101) and ProviderName (0x102).  				

				int[] attributes = {0x100,0x101,0x102};
	
				UUID[] uuids = new UUID[1];
       			uuids[0] = new UUID("1101",true); // Serial Port
    			synchronized (this) {
    				try {
						LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attributes,uuids,d,this);
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} catch (BluetoothStateException e) {
					}
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
        NXTInfo[] nxts = new NXTInfo[nxtInfos.size()];
        for(int i=0;i<nxts.length;i++) nxts[i] = (NXTInfo) nxtInfos.elementAt(i);
        return nxts;
	}

	public boolean open(NXTInfo nxt) {		
		try{
			if (nxt.btResourceString == null) {
				System.out.print("Failed to connect to " + nxt.name + " - is it switched on and paired with PC?");
				return false;
			}
			con = (StreamConnection) Connector.open(nxt.btResourceString);
	        os = con.openOutputStream();
			is  = con.openInputStream();
			return true;
 	 	}
 	 	catch(IOException e){
 	 		System.out.println("Open of " + nxt.name + " failed");
 	 		return false;
 	 	} 
	}

	public void close() {
		try {
			if (os != null) os.close();
			if (is != null) is.close();
			if (con != null) con.close();
		} catch (IOException ioe) {}
	}

    /**
	* Sends a request to the NXT brick.
	* @param message Data to send.
	*/	
    public synchronized byte [] sendRequest(byte [] message, int replyLen) {
    	
    	// length of packet (Least and Most significant byte)
    	// * NOTE: Bluetooth only. If do USB, doesn't need it.
    	int LSB = message.length;
		int MSB = message.length >>> 8;
		
        if (os == null) return new byte[0];

        try {
        	// Send length of packet:
        	os.write((byte)LSB);
    		os.write((byte)MSB);
        	
        	os.write(message);
       	} catch (IOException e) {
        	System.out.println("Write error encountered in NXTCommBluecove.sendData");
        }
       	
       	if (replyLen == 0) return new byte[0];
       	
		byte [] reply = null;
		int length = -1;
		
        if (is == null) return new byte[0];

		try {
			do {
				length = is.read(); // First byte specifies length of packet.
			} while (length < 0);
			
			int lengthMSB = is.read(); // Most Significant Byte value
			length = (0xFF & length) | ((0xFF & lengthMSB) << 8);
			reply = new byte[length];
			is.read(reply);
		} catch (IOException e) {
        	System.out.println("Read error encountered in NXTCommBluecove.sendData");
		}           
        		
		return (reply == null) ? new byte[0] : reply;
    }

	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        //System.out.println("Found Device,  class: " + cod.getMajorDeviceClass() + "/" + cod.getMinorDeviceClass());
		if (cod.getMajorDeviceClass() == 2048 && cod.getMinorDeviceClass() == 4)
			devices.addElement(btDevice);
	}

	public synchronized void inquiryCompleted(int discType) {		
        //if (discType == INQUIRY_COMPLETED) System.out.println("Inquiry completed");
        //else System.out.println("Inquiry Failed");
		notifyAll();
	}

	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        //System.out.println(servRecord.length + " service(s) discovered");
        // Should only be one service on a NXT
        if (servRecord.length != 1) return;
        nxtInfo.btResourceString = servRecord[0].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
	    //System.out.println("Setting url to : " + nxtInfo.btResourceString);
	}

	public synchronized void serviceSearchCompleted(int transID, int respCode) {
		//System.out.println("Service search completed: respCode = " + respCode);
		notifyAll();
	}
	
	public OutputStream getOutputStream() {
		return new BTOutputStream(os);		
	}
	
	public InputStream getInputStream() {
		return is;
	}
}

