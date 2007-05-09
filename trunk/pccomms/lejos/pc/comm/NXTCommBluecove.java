package lejos.pc.comm;

import javax.microedition.io.*;
import javax.bluetooth.*;
import java.io.*;
import java.util.Vector;
import java.util.Enumeration;

public class NXTCommBluecove implements NXTComm, DiscoveryListener  {
	static Vector devices, services, nxtInfos;
	StreamConnection con;
	String url;
	OutputStream os;
	InputStream is;
    NXTInfo nxtInfo;

	public NXTInfo[] search(String name, int protocol) {
		
		devices = new Vector();
		services = new Vector();
                nxtInfos = new Vector();

                if (protocol != NXTCommand.BLUETOOTH) return new NXTInfo[0];

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
				nxtInfo.btDeviceAddress = d.getBluetoothAddress();
                                nxtInfo.protocol = 1;

                                if (name == null || name.equals(nxtInfo.name)) nxtInfos.addElement(nxtInfo);
				else continue;

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

                                System.out.println(services.size() + " services detected");

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
                NXTInfo[] nxts = new NXTInfo[nxtInfos.size()];
                for(int i=0;i<nxts.length;i++) nxts[i] = (NXTInfo) nxtInfos.elementAt(i);
                return nxts;
	}

	public void open(NXTInfo nxt) {		
    		try{
			if (nxt.btUrl != null) con = (StreamConnection) Connector.open(nxt.btUrl);
                        if (con != null) {
                        	os = con.openOutputStream();
				is = con.openInputStream();
			} else {
				System.out.println("Connection failed");
				System.exit(1);
			}			
     	 	}
     	 	catch(IOException e){
     	 		System.out.println("Open failed");
  			System.exit(1);
     	 	} 
	}

	public void close() {
	}

    	/**
	* Sends a command to the NXT brick.
	* @param message Data to send.
	*/	
    public synchronized void sendData(byte [] message) {
    	
    		// length of packet (Least and Most significant byte)
    		// * NOTE: Bluetooth only. If do USB, doesn't need it.
    		int LSB = message.length;
		int MSB = message.length >>> 8;
		
                if (os == null) return;

        	try {
        		// Send length of packet:
        		os.write((byte)LSB);
    			os.write((byte)MSB);
        	
        		os.write(message);
       		} catch (IOException e) {
        		System.out.println("Error encountered in NXTCommRXTX.sendData()");
        	e.printStackTrace();
        	}            
    	}

	public byte[] readData() {
		byte [] message = null;
		int length = -1;
		
                if (is == null) return new byte[0];

		try {
			do {
				length = is.read(); // First byte specifies length of packet.
			} while (length < 0);
			int MSB = is.read(); // Most Significant Byte value
			length = (0xFF & length) | ((0xFF & MSB) << 8);
			message = new byte[length];
			is.read(message);
		} catch (IOException e) {
			e.printStackTrace();
		}           
        		
		return (message == null)? new byte[0] : message;
        }


	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                System.out.println("Major Device Class: " + cod.getMajorDeviceClass());
                System.out.println("Major Device Class: " + cod.getMinorDeviceClass());
		if (cod.getMajorDeviceClass() == 2048 && cod.getMinorDeviceClass() == 4)
                	devices.addElement(btDevice);

	}

	public synchronized void inquiryCompleted(int discType) {		
                //if (discType == INQUIRY_COMPLETED) System.out.println("Inquiry completed");
                //else System.out.println("Inquiry Failed");
		notifyAll();
	}

	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                System.out.println("Services discovered");
		for (int i = 0; i < servRecord.length; i++) {
			services.addElement(servRecord[i]);
                        url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                        nxtInfo.btUrl = url;
			System.out.println(url);

		}
	}

	public synchronized void serviceSearchCompleted(int transID, int respCode) {
		System.out.println("Service search completed: respCode = " + respCode);
		notifyAll();
	}
}

