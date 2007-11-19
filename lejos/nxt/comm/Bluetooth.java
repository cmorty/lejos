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
	public static  final int MSG_SET_DISCOVERABLE_ACK = 32;
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
	public static  final int MSG_GET_OPERATING_MODE = 53;
	public static  final int MSG_SET_OPERATING_MODE = 54;
	public static  final int MSG_OPERATING_MODE_RESULT = 55;
	public static  final int MSG_GET_CONNECTION_STATUS = 56;
	public static  final int MSG_CONNECTION_STATUS_RESULT = 57;
	public static  final int MSG_GOTO_DFU_MODE = 58;
	
	private static byte[] sendBuf = new byte[256];
	private static byte[] receiveBuf = new byte[128];
	private static byte[] friendlyName = retrieveFriendlyName();
	private static byte[] localAddr = retrieveLocalAddress();
	private static boolean supressWait = false;
	
	private static byte[] default_pin = 
	    {(byte) '1', (byte) '2', (byte) '3', (byte) '4'};
	
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
	 * Uses default pin "1234"
	 * 
	 * @return a BTConnection
	 */
	public static BTConnection waitForConnection() {
		return waitForConnection(default_pin);
	}
	
	/**
	 * Wait for a remote device to connect.
	 * 
	 * @param pin the pin to use
	 * @return a BTConnection
	 */
	public static BTConnection waitForConnection(byte[] pin)
	{
		byte[] reply = new byte[32];
		byte[] dummy = new byte[32];
		byte[] msg = new byte[32];
		byte[] device = new byte[7];
		boolean cmdMode = true;
		BTConnection btc = null;

		Bluetooth.btSetCmdMode(1);
		Bluetooth.btStartADConverter();

		while (cmdMode & !supressWait)
		{
			receiveReply(reply,32);
			
			if (reply[0] != 0) {
				//LCD.drawInt(reply[1],0, 2);
				//LCD.refresh();
				if (reply[1] == MSG_REQUEST_PIN_CODE) {
					for(int i=0;i<7;i++) device[i] = reply[i+2];
					msg[0] = Bluetooth.MSG_PIN_CODE;
					for(int i=0;i<7;i++) msg[i+1] = device[i];
					for(int i=0;i<16;i++) {
						if (i >= pin.length) msg[i+8] = 0;
						else msg[i+8] = pin[i];
					}
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
			Thread.yield();
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
		if (remoteDevice == null) return null;
		else return connect(remoteDevice.getDeviceAddr());
	}

	/**
	 * Connects to a Device by it's Byte-Device-Address Array
	 * Uses default pin "1234"
	 * 
	 * @param device_addr byte-Array with device-Address
	 * @return BTConnection Object or null
	 */
	public static BTConnection connect(byte[] device_addr) {
		return connect(device_addr, default_pin);
	}
	
	/**
	 * Connects to a Device by it's Byte-Device-Address Array
	 * 
	 * @param device_addr byte-Array with device-Address
	 * @param pin the pin to use
	 * @return BTConnection Object or null
	 */
	public static BTConnection connect(byte[] device_addr, byte[] pin) {

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
					for(int i=0;i<16;i++) {
						if (i >= pin.length) msg[i+8] = 0;
						else msg[i+8] = pin[i];
					}
					sendCommand(msg, 24);					
				} else if (reply[1] == MSG_CONNECT_RESULT) {
					
					
					if (reply[2] == 0) { // Connection failed
						return null;
					}
					
					// Wait a while to check that connection
					// is not immediately terminated
					
					try {
						Thread.sleep(300);
					} catch (InterruptedException ie) {
					}
					
					// If no message is received then the connection is good
					  
					receiveReply(dummy, 32);
					if (dummy[0] == 0) {
						btc = new BTConnection(reply[3]);
						msg[0] = MSG_OPEN_STREAM;
						msg[1] = reply[3];
						sendCommand(msg, 2);
						
						// wait for the open connection to take affect
						
						try {
							Thread.sleep(100);
						} catch (InterruptedException ie) {
						}
						
						// But the BC4 chip in data mode
						
						btSetCmdMode(0);
						cmdMode = false;
						return btc;
					}
		        }
			}
			Thread.yield();
		}
		return btc;
	}
	
	/**
	 * The internal Chip has a list of already paired Devices. This Method returns a 
	 * Vector-List which contains all the known Devices on the List. These need not be reachable. 
	 * To connect to a "not-known"-Device, you should use the Inquiry-Prozess. 
	 * The pairing-Process can also be done with the original Lego-Firmware. The List of known 
	 * devices will not get lost, when installing the LeJOS Firmware. 
	 * 
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
		
		supressWait = true;
		Thread.yield();

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
			
			Thread.yield();
		}
		supressWait = false;
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
		return null;
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
		
		supressWait = true;
		Thread.yield();
		
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
		
		supressWait = false;
		return reply[2] == 0x50;
	}
	
	/**
	 * Add device to known devices
	 * @param d Remote Device
	 * @return true iff remove was successful
	 */
	public static boolean removeDevice(BTRemoteDevice d) {
		byte [] msg = new byte[28];
		byte [] reply = new byte[32];
		byte [] addr = d.getDeviceAddr();

		supressWait = true;
		Thread.yield();
		
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
		
		supressWait = false;
		return reply[2] == 0x50;
	}
	
	/**
	 * Start a Bluetooth inquiry process
	 * 
	 * @param maxDevices the maximum number of devices to discover
	 * @param timeout the timeout value in units of 1.28 econds
	 * @param cod the class of device to look for
	 * @return a vector of all the devices found
	 */
	public static Vector inquire(int maxDevices,  int timeout, byte[] cod) {
		Vector retVec = new Vector();
		byte[] msg = new byte[8];
		byte[] reply = new byte[32];
		byte[] device = new byte[7];
		char[] name = new char[16];
		int nameLen;
		
		supressWait = true;
		Thread.yield();
		
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
					for(int i=0;i<16 && reply[9+i] != 0;i++) {
						name[i] = (char) reply[9+i];
						nameLen++;
					}
					for(int i=0;i<4;i++) cod[i] = reply[25+i];

					// add the Element to the Vector List
					retVec.addElement(new BTRemoteDevice(name, nameLen, device, cod));
				}		
			}
			Thread.yield();
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
		
		supressWait = false;
		
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
	
	/**
	 * Get the persistent status value from the BC4 chip
	 * 
	 * @return the byte value
	 */
	public static int getStatus() {
		byte [] msg = new byte[8];
		byte[] reply = new byte[32];
		
		supressWait = true;
		Thread.yield();
		
		msg[0] = MSG_GET_BRICK_STATUSBYTE;	
		
		sendCommand(msg,1);
		
		while(true) {
			receiveReply(reply,32);		
			if (reply[0] != 0) {
				if (reply[1] == MSG_GET_BRICK_STATUSBYTE_RESULT) {
					supressWait = false;
					return (int) reply[2];
				}
			}
		}
	}

	/**
	 * Set the persistent status byte for the BC4 chip
	 * 
	 * @param status the byte status value
	 */
	public static void setStatus(byte status) {
		byte [] msg = new byte[8];
		byte[] reply = new byte[32];
		
		supressWait = true;	
		Thread.yield();
		
		msg[0] = MSG_SET_BRICK_STATUSBYTE;	
		msg[1] = status;
		msg[2] = 0;
		
		sendCommand(msg,3);
		
		while(true) {
			receiveReply(reply,32);		
			if (reply[0] != 0) {
				if (reply[1] == MSG_SET_BRICK_STATUSBYTE_RESULT) {
					supressWait = false;
					return;
				}
			}
		}		
	}
	
	/**
	 * Get the visibility (discoverable) status of the device
	 * 
	 * @return 1 = visible, 0 = invisible
	 */
	public static int getVisibility() {
		byte [] msg = new byte[8];
		byte[] reply = new byte[32];
		
		supressWait = true;
		Thread.yield();
		
		msg[0] = MSG_GET_DISCOVERABLE;			
		sendCommand(msg,1);
		
		while(true) {
			receiveReply(reply,32);		
			if (reply[0] != 0) {
				if (reply[1] == MSG_GET_DISCOVERABLE_RESULT) {
					supressWait = false;
					return (int) reply[2];
				}
			}
		}	
	}
	
	/**
	 * Get the port open status, 
	 * i.e whether connections are being accepted
	 * 
	 * @return 1 if the port is open, 0 otherwise
	 */
	public static int getPortOpen() {
		byte [] msg = new byte[8];
		byte[] reply = new byte[32];
		
		supressWait = true;
		Thread.yield();
		
		msg[0] = MSG_GET_PORT_OPEN;			
		sendCommand(msg,1);
		
		while(true) {
			receiveReply(reply,32);		
			if (reply[0] != 0) {
				if (reply[1] == MSG_GET_PORT_OPEN_RESULT) {
					supressWait = false;
					return (int) reply[2];
				}
			}
		}	
	}
	
	/**
	 * Get the operating mode (stream breaking or not) 
	 * 
	 * @return 0 = stream breaking mode, 1 = don't break stream mode
	 */
	public static int getOperatingMode() {
		byte [] msg = new byte[8];
		byte[] reply = new byte[32];
		
		supressWait = true;
		Thread.yield();
		
		msg[0] = MSG_GET_OPERATING_MODE;			
		sendCommand(msg,1);
		
		while(true) {
			receiveReply(reply,32);		
			if (reply[0] != 0) {
				if (reply[1] == MSG_OPERATING_MODE_RESULT) {
					supressWait = false;
					return (int) reply[2];
				}
			}
		}	
	}
	
	/**
	 * Set Bluetooth visibility (discoverable) on or off for the local device
	 * 
	 * @param visible true to set visibility on, false to set it off
	 */
	public static void setVisibility(byte visible) {
		byte [] msg = new byte[8];
		byte[] reply = new byte[32];
		
		supressWait = true;	
		Thread.yield();
		
		msg[0] = MSG_SET_DISCOVERABLE;	
		msg[1] = visible;
		
		sendCommand(msg,2);
		
		while(true) {
			receiveReply(reply,32);		
			if (reply[0] != 0) {
				if (reply[1] == MSG_SET_DISCOVERABLE_ACK) {
					supressWait = false;
					return;
				}
			}
		}		
	}
	
	/**
	 * Reset the settings of the BC4 chip to the factory defaults.
	 * The NXT should be restarted after this.
	 *
	 */
	public static void setFactorySettings() {
		byte [] msg = new byte[8];
		byte[] reply = new byte[32];
		
		supressWait = true;	
		Thread.yield();
		
		msg[0] = MSG_SET_FACTORY_SETTINGS;			
		sendCommand(msg,1);
		
		while(true) {
			receiveReply(reply,32);		
			if (reply[0] != 0) {
				if (reply[1] == MSG_SET_FACTORY_SETTINGS_ACK) {
					supressWait = false;
					return;
				}
			}
		}		
	}
	
	/**
	 * Set the operating mode
	 * 
	 * @param mode 0 = Stream breaking, 1 don't break stream 
	 */
	public static void setOperatingMode(byte mode) {
		byte [] msg = new byte[8];
		byte[] reply = new byte[32];
		
		supressWait = true;	
		Thread.yield();
		
		msg[0] = MSG_SET_OPERATING_MODE;
		msg[1] = mode;
		sendCommand(msg,2);
		
		while(true) {
			receiveReply(reply,32);		
			if (reply[0] != 0) {
				if (reply[1] == MSG_OPERATING_MODE_RESULT) {
					supressWait = false;
					return;
				}
			}
		}		
	}
	
	/**
	 * Opens the  port to allow incoming connections.
	 * 
	 * @return an array of three bytes: success, handle, ps_success
	 */
	public static byte[] openPort() {
		byte [] msg = new byte[8];
		byte[] reply = new byte[32];
		byte[] result = new byte[3];
		
		supressWait = true;	
		Thread.yield();
		
		msg[0] = MSG_OPEN_PORT;			
		sendCommand(msg,1);
		
		while(true) {
			receiveReply(reply,32);		
			if (reply[0] != 0) {
				if (reply[1] == MSG_PORT_OPEN_RESULT) {
					result[0] = reply[2];
					result[1] = reply[3];
					result[2] = reply[4];
					supressWait = false;
					return result;
				}
			}
		}		
	}
	
	/**
	 * Closes the  port to disallow incoming connections.
	 * 
	 * @return an array of three bytes: success, handle, ps_success
	 */
	public static byte[] closePort() {
		byte [] msg = new byte[8];
		byte[] reply = new byte[32];
		byte[] result = new byte[3];
		
		supressWait = true;	
		Thread.yield();
		
		msg[0] = MSG_OPEN_PORT;			
		sendCommand(msg,1);
		
		while(true) {
			receiveReply(reply,32);		
			if (reply[0] != 0) {
				if (reply[1] == MSG_PORT_OPEN_RESULT) {
					result[0] = reply[2];
					result[1] = reply[3];
					result[2] = reply[4];
					supressWait = false;
					return result;
				}
			}
		}		
	}
	
	/**
	 * Close an close connection
	 * 
	 * @param handle the handle for the connection
	 * @return the status 0 = success
	 */
	public static int closeConnection(byte handle) {
		byte [] msg = new byte[8];
		byte[] reply = new byte[32];
		
		supressWait = true;	
		Thread.yield();
		
		msg[0] = MSG_CLOSE_CONNECTION;
		msg[1] = handle;
		sendCommand(msg,2);
		
		while(true) {
			receiveReply(reply,32);		
			if (reply[0] != 0) {
				if (reply[1] == MSG_CLOSE_CONNECTION_RESULT) {
					supressWait = false;
					return reply[2];
				}
			}
		}		
	}
	
	/**
	 * Open or reopen a stream for a connection
	 * 
	 * @param handle the handle for the connection
	 * @return the status 0 = success
	 */
	public static void openStream(byte handle) {
		byte [] msg = new byte[8];
		
		supressWait = true;	
		Thread.yield();
		
		msg[0] = MSG_OPEN_STREAM;
		msg[1] = handle;
		sendCommand(msg,2);		
	}
	
	/**
	 * Get the Bluetooth signal strength (link quality)
	 * Higher values mean stronger signal.
	 * Note this cannot be called when a connection is open.
	 * 
	 * @return link quality value 0 to 255. 
	 * 
	 */
	public static int getSignalStrength(byte handle) {
		byte [] msg = new byte[8];
		byte[] reply = new byte[32];
		
		supressWait = true;
		Thread.yield();
		
		msg[0] = MSG_GET_LINK_QUALITY;
		msg[1] = handle;
		sendCommand(msg,2);
		
		while(true) {
			receiveReply(reply,32);		
			if (reply[0] != 0) {
				if (reply[1] == MSG_LINK_QUALITY_RESULT) {
					supressWait = false;
					return (int) ( reply[2] & 0xFF);
				}
			}
		}	
	}
	
	/**
	 * Get the status of all connections
	 * 
	 * @return byte array of status for each handle
	 */
	public static byte[] getConnectionStatus() {
		byte [] msg = new byte[8];
		byte[] reply = new byte[32];
		byte[] result = new byte[4];
		
		supressWait = true;
		Thread.yield();
		
		msg[0] = MSG_GET_CONNECTION_STATUS;			
		sendCommand(msg,1);
		
		while(true) {
			receiveReply(reply,32);		
			if (reply[0] != 0) {
				if (reply[1] == MSG_CONNECTION_STATUS_RESULT) {
					for(int i=0;i<4;i++) result[i] = reply[5+i];
					supressWait = false;
					return result;
				}
			}
		}	
	}
	
	/**
	 * Get the major and minor version of the BlueCore code
	 * 
	 * @return an array of two bytes: major version, minor version
	 */
	public static byte[] getVersion() {
		byte [] msg = new byte[8];
		byte[] reply = new byte[32];
		byte [] version = new byte[2];
		
		supressWait = true;
		Thread.yield();
		
		msg[0] = MSG_GET_VERSION;			
		sendCommand(msg,1);
		
		while(true) {
			receiveReply(reply,32);		
			if (reply[0] != 0) {
				if (reply[1] == MSG_GET_VERSION_RESULT) {
					supressWait = false;
					version[0] = reply[2];
					version[1] = reply[3];
					return version;
				}
			}
		}	
	}
}
