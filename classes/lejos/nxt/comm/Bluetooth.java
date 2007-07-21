package lejos.nxt.comm;
import java.util.*;

/**
 * Support for Bluetooth communications.
 */
public class Bluetooth {

	public static  final int MSG_BEGIN_INQUIRY = 0;
	public static  final int MSG_CANCEL_INQUIRY = 1;
	public static  final int MSG_CONNECT = 2;
	public static  final int MSG_OPEN_PORT = 3;
	public static  final int MSG_LOOKUP_NAME = 4;
	public static  final int MSG_ADD_DEVICE = 5;
	public static  final int MSG_REMOVE_DEVICE = 6;
	public static  final int MSG_DUMP_LIST = 7;
	public static  final int MSG_CLOSE_CONNECTION = 8;
	public static  final int MSG_ACCEPT_CONNECTION = 9;
	public static  final int MSG_PIN_CODE = 10;
	public static  final int MSG_OPEN_STREAM = 11;
	public static  final int MSG_START_HEART = 12;
	public static  final int MSG_HEARTBEAT = 13;
	public static  final int MSG_INQUIRY_RUNNING = 14;
	public static  final int MSG_INQUIRY_RESULT = 15;
	public static  final int MSG_INQUIRY_STOPPED = 16;
	public static  final int MSG_LOOKUP_NAME_RESULT = 17;
	public static  final int MSG_LOOKUP_NAME_FAILURE = 18;
	public static  final int MSG_CONNECT_RESULT = 19;
	public static  final int MSG_RESET_INDICATION = 20;
	public static  final int MSG_REQUEST_PIN_CODE = 21;
	public static  final int MSG_REQUEST_CONNECTION = 22;
	public static  final int MSG_LIST_RESULT = 23;
	public static  final int MSG_LIST_ITEM = 24;
	public static  final int MSG_LIST_DUMP_STOPPED = 25;
	public static  final int MSG_CLOSE_CONNECTION_RESULT = 26;
	public static  final int MSG_PORT_OPEN_RESULT = 27;
	public static  final int MSG_SET_DISCOVERABLE = 28;
	public static  final int MSG_CLOSE_PORT = 29;
	public static  final int MSG_CLOSE_PORT_RESULT = 30;
	public static  final int MSG_PIN_CODE_ACK = 31;
	public static  final int MSG_DISCOVERABLE_ACK = 32;
	public static  final int MSG_SET_FRIENDLY_NAME = 33;
	public static  final int MSG_SET_FRIENDLY_NAME_ACK = 34;
	public static  final int MSG_GET_LINK_QUALITY = 35;
	public static  final int MSG_LINK_QUALITY_RESULT = 36;
	public static  final int MSG_SET_FACTORY_SETTINGS = 37;
	public static  final int MSG_SET_FACTORY_SETTINGS_ACK = 38;
	public static  final int MSG_GET_LOCAL_ADDR = 39;
	public static  final int MSG_GET_LOCAL_ADDR_RESULT = 40;
	public static  final int MSG_GET_FRIENDLY_NAME = 41;
	public static  final int MSG_GET_DISCOVERABLE = 42;
	public static  final int MSG_GET_PORT_OPEN = 43;
	public static  final int MSG_GET_FRIENDLY_NAME_RESULT = 44;
	public static  final int MSG_GET_DISCOVERABLE_RESULT = 45;
	public static  final int MSG_GET_PORT_OPEN_RESULT = 46;
	public static  final int MSG_GET_VERSION = 47;
	public static  final int MSG_GET_VERSION_RESULT = 48;
	public static  final int MSG_GET_BRICK_STATUSBYTE_RESULT = 49;
	public static  final int MSG_SET_BRICK_STATUSBYTE_RESULT = 50;
	public static  final int MSG_GET_BRICK_STATUSBYTE = 51;
	public static  final int MSG_SET_BRICK_STATUSBYTE = 52;
	
	private static byte[] sendBuf = new byte[256];
	private static byte[] receiveBuf = new byte[128];
	private static byte[] friendlyName = retrieveFriendlyName();
	private static byte[] localAddr = retrieveLocalAddress();
	
	private Bluetooth()
	{	
	}
	
	/**
	 * Low-level method to send a BT command or data
	 * 
	 * @param buf the buffer to send
	 * @param len the number of bytes to send
	 */
	public static native void btSend(byte[] buf, int len);
	
	/**
	 * Low-level method to receive BT replies or data
	 * 
	 * @param buf the buffer to receive data in
	 */
	public static native void btReceive(byte[] buf);
	
	/**
	 * Low-level method to switch BC4 chip between command
	 * and data (stream) mode.
	 * 
	 * @param mode 0=data mode, 1=command mode
	 */
	public static native void btSetCmdMode(int mode);
	
	/**
	 * Low-level nethod to get the BC4 chip mode - does not work.
	 */
	public static native int btGetCmdMode();
	
	/**
	 * Low-level method to start ADC converter - does not wok.
	 *
	 */
	public static native void btStartADConverter();
	
	/**
	 * Send a command to the BC4 chip. Must be in command mode.
	 * @param cmd the command
	 * @param len the number of bytes
	 * 
	 */
	public static void sendCommand(byte[] cmd, int len)
	{
		int checkSum = 0;
		
		sendBuf[0] = (byte) (len + 2);
		
		for(int i=0;i<len;i++)
		{
			sendBuf[i+1] = cmd[i];
			checkSum += cmd[i];
		}
		
	    checkSum = -checkSum;
	    sendBuf[len+2] = (byte) ((checkSum >> 8) & 0xff);
	    sendBuf[len+3] = (byte) (checkSum & 0xff);
	    				
		btSend(sendBuf,len+3);
	}
	
	/**
	 * Receive a command or reply from the BC4 chip. 
	 * Must be in command mode.
	 * 
	 * @param buf the buffer to receive the reply
	 * @param bufLen the length of the buffer
	 * @return the number of bytes received
	 */
	public static int receiveReply(byte[] buf, int bufLen)
	{
		int checkSum, negSum, len;
		btReceive(receiveBuf);
		len = receiveBuf[0];
		buf[0] = (byte) len;
		
		if (len == 0) return 0;
		
		checkSum = len;
		
		if (len-1 <= bufLen)
		{
			for(int i=1;i<len-1;i++) 
			{
				buf[i] = receiveBuf[i];
				checkSum += (buf[i] & 0xff);
			}
			negSum = (receiveBuf[len-1] & 0xff) << 8;
			negSum += (receiveBuf[len] & 0xff);
			if (checkSum + negSum == 65536) return len-1;
			else return 0;
		}
		return 0;
	}
	
	/**
	 * Read a data packet (with 2-byte length header) from a stream connection.
	 * Must be in data mode.
	 * 
	 * @param buf the buffer to receive the data in
	 * @param bufLen the length of the buffer
	 * @return the number of bytes received
	 */
	public static int readPacket(byte[] buf, int bufLen)
	{
		int len;
		
		btReceive(receiveBuf);
		len = receiveBuf[0];
		if (len > 0 && len <= bufLen)
		{
	      for(int i=0;i<len;i++) buf[i] = receiveBuf[i+2];
	      return len;
		}
		return 0;
	}
	
	/**
	 * Send a data packet.
	 * Must be in data mode.
	 * @param buf the data to send
	 * @param bufLen the number of bytes to send
	 */
	public static void sendPacket(byte [] buf, int bufLen)
	{
		if (bufLen <= 254)
	    {
			sendBuf[0] = (byte) (bufLen & 0xFF);
			sendBuf[1] = (byte) ((bufLen >> 8) & 0xFF);
			for(int i=0;i<bufLen;i++) sendBuf[i+2] = buf[i];
			btSend(sendBuf,bufLen+2);
	    }
	}
	
	/**
	 * Wait for a remote device to connect.
	 * Pin currently must be 1234.
	 * 
	 * @return a BTConnection
	 */
	public static BTConnection waitForConnection()
	{
		byte[] reply = new byte[32];
		byte[] dummy = new byte[32];
		byte[] msg = new byte[32];
		byte[] device = new byte[7];
		boolean cmdMode = true;
		BTConnection btc = null;

		Bluetooth.btSetCmdMode(1);
		Bluetooth.btStartADConverter();

		while (cmdMode)
		{
			receiveReply(reply,32);
			
			if (reply[0] != 0) {
				//LCD.drawInt(reply[1],0, 2);
				//LCD.refresh();
				if (reply[1] == MSG_REQUEST_PIN_CODE) {
					for(int i=0;i<7;i++) device[i] = reply[i+2];
					msg[0] = Bluetooth.MSG_PIN_CODE;
					for(int i=0;i<7;i++) msg[i+1] = device[i];
					msg[8] = '1';
					msg[9] = '2';
					msg[10] = '3';
					msg[11] = '4';
					for(int i=0;i<12;i++) msg[i+12] = 0;
					sendCommand(msg, 24);					
				}	
				
				if (reply[1] == MSG_REQUEST_CONNECTION) {
					for(int i=0;i<7;i++) device[i] = reply[i+2];
					msg[0] = MSG_ACCEPT_CONNECTION;
					msg[1] = 1;
					sendCommand(msg, 2);					
				}
				
				if (reply[1] == MSG_CONNECT_RESULT) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException ie) {}
					receiveReply(dummy,32);					
					if (dummy[0] == 0) {
	                    btc = new BTConnection(reply[3]);
						msg[0] = MSG_OPEN_STREAM;
						msg[1] = reply[3];
						sendCommand(msg, 2);
						try {
							Thread.sleep(100);
						} catch (InterruptedException ie) {}
						btSetCmdMode(0);
						cmdMode = false;
					} 
				}
			}
		}
		return btc;
	}
	
	/**
	 * Called when Bluetooth starts up to get the friendly namr
	 * of this device, as this cannot be done when a stream is open.
	 */
	private static byte[] retrieveFriendlyName() {
		byte[] reply = new byte[32];
		byte[] msg = new byte[1];
		byte[] name = new byte[16];
		
		msg[0] = MSG_GET_FRIENDLY_NAME;
		
		sendCommand(msg,1);
		
		boolean gotName = false;
		
		while(!gotName) {
			receiveReply(reply,32);
			
			if (reply[0] != 0 && reply[1] == MSG_GET_FRIENDLY_NAME_RESULT) {
				for(int i=0;i<16;i++) name[i] = reply[i+2];
				gotName = true;
			}
		}
		
		return name;
	}
	
	/**
	 * Get the friendly name of the local device
	 * @return the friendly name
	 */
	public static byte [] getFriendlyName() {
		return friendlyName;
	}
	
	/**
	 * Set the name of the local device
	 * @param name the friendly name for the device
	 */
	public static void setFriendlyName(byte[] name) {
		byte[] reply = new byte[32];
		byte[] msg = new byte[32];
		
		friendlyName = name;
		
		msg[0] = MSG_SET_FRIENDLY_NAME;
		
		for(int i=-0;i<16;i++) msg[i+1] = name[i];
		
		sendCommand(msg,17);
		
		boolean setName = false;
		
		while(!setName) {
			receiveReply(reply,32);
			
			if (reply[0] != 0 && reply[1] == MSG_SET_FRIENDLY_NAME_ACK) {
				setName = true;
			}
		}
	}
	
	/**
	 * get the Bluetooth address of the local device
	 * @return the local address
	 */
	public static byte[] getLocalAddress() {
		return localAddr;
	}
	
	/**
	 * get the local address when Bluetooth starts up
	 * as it cannot be retrivedwhen a stream is open.
	 */
	private static byte[] retrieveLocalAddress() {
		byte[] reply = new byte[32];
		byte[] msg = new byte[1];
		byte[] address = new byte[7];
		
		msg[0] = MSG_GET_LOCAL_ADDR;
		
		sendCommand(msg,1);
		
		boolean gotAddress = false;
		
		while(!gotAddress) {
			receiveReply(reply,32);
			
			if (reply[0] != 0 && reply[1] == MSG_GET_LOCAL_ADDR_RESULT) {
				for(int i=0;i<7;i++) address[i] = reply[i+2];
				gotAddress = true;
			}
		}
		
		return address;
	}
	
	/**
	 * Connects to a remote device
	 * 
	 * @param remoteDevice remote device
	 * @return BTConnection Object or null
	 */
	public static BTConnection connect(BTRemoteDevice remoteDevice) {
		return connect(remoteDevice.getDeviceAddr());
	}
	
	/**
	 * Connects to a Device by it's Byte-Device-Address Array
	 * 
	 * @param device_addr byte-Array with device-Address
	 * @return BTConnection Object or null
	 */
	public static BTConnection connect(byte[] device_addr) {

		boolean cmdMode = true;
		byte[] msg = new byte[32];
		byte[] reply = new byte[32];
		byte[] dummy = new byte[32];
		BTConnection btc = null;
		byte[] device = new byte[7]; // remote device 
				
		Bluetooth.btSetCmdMode(1);
		Bluetooth.btStartADConverter();

		// invoke BC4 Chip to connect
		msg[0] = MSG_CONNECT;
		for (int i = 0; i < 7; i++) {
			msg[i + 1] = device_addr[i];
		}
		sendCommand(msg, 8);

		// receive connection-result
		while (cmdMode) {
			receiveReply(reply, 32);
			
			if (reply[0] != 0) {
				//LCD.drawInt(reply[1], 0, 2);
				//LCD.refresh();
				if (reply[1] == MSG_REQUEST_PIN_CODE) {
					for(int i=0;i<7;i++) device[i] = reply[i+2];
					msg[0] = Bluetooth.MSG_PIN_CODE;
					for(int i=0;i<7;i++) msg[i+1] = device[i];
					msg[8] = '1';
					msg[9] = '2';
					msg[10] = '3';
					msg[11] = '4';
					for(int i=0;i<12;i++) msg[i+12] = 0;
					sendCommand(msg, 24);					
				} else if (reply[1] == MSG_CONNECT_RESULT) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException ie) {
					}
					  
					receiveReply(dummy, 32);
					if (dummy[0] == 0) {
						btc = new BTConnection(reply[3]);
						msg[0] = MSG_OPEN_STREAM;
						msg[1] = reply[3];
						sendCommand(msg, 2);
						try {
							Thread.sleep(100);
						} catch (InterruptedException ie) {
						}
						btSetCmdMode(0);
						cmdMode = false;
					}
		        }
			}
		}
		return btc;
	}
	
	/**
	 * The internal Chip has a list of already paired Devices. This Method returns a 
	 * Vector-List which contains all the known Devices on the List. These need not be reachable. 
	 * To connect to a "not-known"-Device, you should use the Inquiry-Prozess. 
	 * The pairing-Process can also be done with the original Lego-Firmware. The List of known 
	 * devices will not get lost, when installing the LeJOS Firmware. 
	 * @return Vector with List of known Devices
	 */
	public static Vector getKnownDevicesList() {

		boolean cmdMode = true;
		byte[] msg = new byte[2];
		byte[] reply = new byte[32];
		byte[] device = new byte[7];
		byte[] devclass = new byte[4];
		Vector retVec = new Vector(1);
		BTRemoteDevice curDevice;

		Bluetooth.btSetCmdMode(1);
		Bluetooth.btStartADConverter();

		// invoke BC4 Chip to send the DumpList
		msg[0] = MSG_DUMP_LIST;
		sendCommand(msg, 1);

		// receive DeviceList one by one
		while (cmdMode) {
			receiveReply(reply, 32);

			if (reply[0] != 0) {

				if (reply[1] == MSG_LIST_ITEM) {

					// Get MAC-Address
					for (int i = 0; i < 7; i++)
						device[i] = reply[i + 2];
					
					// Get the friendly Name, it is terminated by Zero
					char[] c_ar = new char[16];
					int ci = 0;
					for (; (ci < 16 && reply[ci + 9] != 0); ci++)
						c_ar[ci] = (char) reply[ci + 9];

					// Get Device-Class
					for (int i = 0; i < 4; i++)
						devclass[i] = reply[i + 25];
					
					// create BTRemoteDevice
					
					curDevice = new BTRemoteDevice(c_ar, ci, device, devclass);

					// add the Element to the Vector List
					retVec.addElement(curDevice);
				}

				if (reply[1] == MSG_LIST_DUMP_STOPPED) {
					break;
				}
			}
		}
		return retVec;
	}
	
	/**
	 * Gets a Device of the BC4-Chips internal list of known Devices 
	 * (those who have been paired before) into the BTDevice Object. 
	 * @param fName Friendly-Name of the device
	 * @return BTDevice Object or null, if not found.
	 */
	public static BTRemoteDevice getKnownDevice(String fName) {
		BTRemoteDevice btd = null;
		//look the name up in List of Known Devices
		Vector devList = getKnownDevicesList();
		if (devList.size() > 0) {
			for (int i = 0; i < devList.size(); i++) {
				btd = (BTRemoteDevice) devList.elementAt(i);
				if (btd.getFriendlyName().equals(fName)) {
					return btd; 
				}
			}
		}
		return btd;
	}
	
	/**
	 * Add device to known devices
	 * @param d Remote Device
	 * @return true iff add was successful
	 */
	public static boolean addDevice(BTRemoteDevice d) {
		byte [] msg = new byte[28];
		byte [] reply = new byte[32];
		byte [] addr = d.getDeviceAddr();
		String name = d.getFriendlyName();
		byte[] cod = d.getDeviceClass();
		msg[0] = MSG_ADD_DEVICE;
		for(int i=0;i<7;i++) msg[i+1] = addr[i];
		for(int i=0;i<name.length();i++)  msg[i+8] = (byte) name.charAt(i);
		for(int i=0;i<4;i++) msg[i+24] = cod[i];
		
		sendCommand(msg,28);
		
		boolean added = false;
		
		while(!added) {
			receiveReply(reply,32);
			
			if (reply[0] != 0 && reply[1] == MSG_LIST_RESULT) {
				added = true;
			}
		}
		
		return reply[2] == 0x50;
	}
	
	/**
	 * Add device to known devices
	 * @param d Remote Device
	 * @return true iff add was successful
	 */
	public static boolean removeDevice(BTRemoteDevice d) {
		byte [] msg = new byte[28];
		byte [] reply = new byte[32];
		byte [] addr = d.getDeviceAddr();

		msg[0] = MSG_REMOVE_DEVICE;		
		for(int i=0;i<7;i++) msg[i+1] = addr[i];
		
		sendCommand(msg,8);
		
		boolean removed = false;
		
		while(!removed) {
			receiveReply(reply,32);
			
			if (reply[0] != 0 && reply[1] == MSG_LIST_RESULT) {
				removed = true;
			}
		}
		
		return reply[2] == 0x50;
	}
	
	public static Vector inquire(int maxDevices,  int timeout, byte[] cod) {
		Vector retVec = new Vector();
		byte[] msg = new byte[8];
		byte[] reply = new byte[32];
		byte[] device = new byte[7];
		char[] name = new char[16];
		int nameLen;
		
		msg[0] = MSG_BEGIN_INQUIRY;
		msg[1] = (byte) maxDevices;
		msg[2] = 0;
		msg[3] = (byte) timeout;
		for(int i=0;i<4;i++) msg[4+i] = cod[i];
		
		sendCommand(msg, 8);
		
		boolean stopped = false;
		
		while(!stopped) {
			receiveReply(reply,32);
			
			if (reply[0] != 0) {
				if (reply[1] == MSG_INQUIRY_STOPPED) stopped = true;
				else if (reply[1] == MSG_INQUIRY_RESULT) {
					for(int i=0;i<7;i++) device[i] = reply[2+i];
					nameLen = 0;
					for(int i=0;i<16 & reply[9+i] != 0;i++) {
						name[i] = (char) reply[9+i];
						nameLen++;
					}
					for(int i=0;i<4;i++) cod[i] = reply[25+i];

					// add the Element to the Vector List
					retVec.addElement(new BTRemoteDevice(name, nameLen, device, cod));
				}
				
			}	
		}
		
		// Fill in the names
		
		for (int i = 0; i < retVec.size(); i++) {
			BTRemoteDevice btrd = ((BTRemoteDevice) retVec.elementAt(i));
            String s = btrd.getFriendlyName();
            if (s.length() == 0) {
            	String nm = lookupName(btrd.getDeviceAddr());
            	btrd.setFriendlyName(nm.toCharArray(),nm.length());
            }
		}
		
		return retVec;		
	}
	
	/**
	 * Look up the name of a device using its address
	 * 
	 * @param deviceAddr
	 * @return friendly name of device
	 */
	public static String lookupName(byte [] deviceAddr) {
		byte [] msg = new byte[8];
		byte[] reply = new byte[32];
		char[] name = new char[16];
		
		msg[0] = MSG_LOOKUP_NAME;	
		for(int i=0;i<7;i++) msg[i+1] = deviceAddr[i];
		
		sendCommand(msg,8);
		
		while(true) {
			receiveReply(reply,32);		
			if (reply[0] != 0) {
				if (reply[1] == MSG_LOOKUP_NAME_RESULT) {
					int nameLen = 0;
					for(int i=0;i<16 && reply[9+i] != 0;i++) {
						nameLen++;
						name[i] = (char) reply[9+i];
					}
					return new String(name,0,nameLen);

				} else if (reply[1] == MSG_LOOKUP_NAME_FAILURE) 
					break;	
			}
		}

		return "";
	}
}

