import java.io.*;
import java.util.Vector;

import lejos.nxt.comm.*;
import lejos.nxt.*;
/*
 * Bluetooth test code.
 * Tests developed alongside the new Bluetooth implementation. Included as an
 * indication how these classes may be tested. Note that some test cases
 * use the names of target Bluetooth devices and may need changing. Also
 * some tests require the use of a PC based test program (to be be found in
 * BTTestPC).
 */

public class bttest {
	static byte [] strToByte(String s)
	{
		byte [] b = new byte[s.length()];
		for(int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			//dOut.writeByte((byte)c);
			b[i] = (byte)c;
		}
		return b;
	}
	
	
	public static boolean badSwitchTest() throws Exception
	{
		// Demonstrates the data loss that switching out of stream mode and into
		// command mode when input is being sent from a remote device. The test
		// program should be used to send data in fast mode to show this problem.
		byte []outBuf = new byte[255];
		byte []inBuf = new byte[255];
		int buflen;
		Debug.out("Bad switch test, expect lost data\n");
		Debug.out("Wait for connection...");
		BTConnection bt = Bluetooth.waitForConnection();
		if (bt == null)
		{
			Debug.out("Did not get connection\n");
			return false;
		}
		Debug.out("After connect\n");
		while (bt.readPacket(inBuf, inBuf.length) == 0) ;
		outBuf[0] = 4;
		outBuf[1] = (byte)131;
		outBuf[2] = 4;

		Debug.out("signal strength " + bt.getSignalStrength() + "\n");		

		while (true)
		{
			//int cnt = bt.readPacket(inBuf, inBuf.length);
			int cnt = bt.read(inBuf, inBuf.length);
			if (cnt == 0) continue;
			if (cnt == -2)
			{
				Debug.out("Lost data, resync\n");
				bt.read(null, 256);
				continue;
			}
			int val = ((int)inBuf[2] & 0xff) + (((int)inBuf[3] & 0xff) << 8);
			Debug.out("Read len " + cnt  +" val " + val + "\n");
			if (val == 0xffff) break;
			if (val == 27) Debug.out("signal strength " + bt.getSignalStrength() + "\n");
		}
		if (bt != null) bt.close();
		return true;
	}
	
	public static boolean ioTest() throws Exception
	{
		// Test various types of I/O with the remote PC program. Run this test
		// and then...
		// 1. Connect using the test program. The test will send data to the PC.
		// 2. Use the fast or slow send buttons to send data to the NXT for
		//    testing the input streams.
		byte []outBuf = new byte[255];
		byte []inBuf = new byte[255];
		int buflen;
		Debug.out("ioTest\n");
		Debug.out("Wait for connection...\n");
		BTConnection bt = Bluetooth.waitForConnection();
		if (bt == null)
		{
			Debug.out("Did not get a connection\n");
			return false;
		}
		Debug.out("After connect\n");
		int start = (int)System.currentTimeMillis();

		// First test low level writes
		outBuf[0] = 4;
		outBuf[1] = (byte)131;
		outBuf[2] = 4;

		Debug.out("signal strength " + bt.getSignalStrength() + "\n");		

		Debug.out("Testing write\n");
		for(int i=0; i < 1000; i++)
		{
			outBuf[3] = (byte)(i & 0xff);
			outBuf[4] = (byte) ((i >> 8) & 0xff);
			bt.write(outBuf, 133);
		}
		Debug.out("write complete time " + ((int) System.currentTimeMillis() - start) + "\n");
		Debug.out("Testing output stream");
		// Now do the same thing using a higher level stream
		OutputStream os = bt.openOutputStream();
		for(int i=0; i < 100; i++)
		{
			os.write(4);
			os.write(3);
			os.write(4);
			os.write(i & 0xff);
			os.write((i>>8) & 0xff);
			os.flush();
		}
		Debug.out("signal strength " + bt.getSignalStrength() + "\n");
		Debug.out("Testing read\n");
		while (true)
		{
			//int cnt = bt.readPacket(inBuf, inBuf.length);
			int cnt = bt.read(inBuf, inBuf.length);
			if (cnt == 0) continue;
			if (cnt == -2)
			{
				Debug.out("Lost data, resync\n");
				bt.read(null, 256);
				continue;
			}
			int val = ((int)inBuf[2] & 0xff) + (((int)inBuf[3] & 0xff) << 8);
					//Debug.out("Memory " + (int)Runtime.getRuntime().freeMemory() + "\n");
			Debug.out("Read len " + cnt  +" val " + val + "\n");
			if (val == 0xffff) break;
		}
		Debug.out("Testing input stream\n");
		// Now test input
		InputStream is = bt.openInputStream();
		while (true)
		{
			//int cnt = bt.readPacket(inBuf, inBuf.length);
			int cnt = is.read(inBuf, 0, 18);
			if (cnt == 0) continue;
			int val = ((int)inBuf[2] & 0xff) + (((int)inBuf[3] & 0xff) << 8);
			Debug.out("Read len " + cnt  +" val " + val + "\n");
			if (val == 0xffff) break;
		}
		if (bt != null) bt.close();
		return true;
	}
	
	
	public static boolean powerOffTest(int delay) throws Exception
	{
		// Test that the NXT Bluetooth module can be powered off. Also check
		// that the LCD continues to operate.
		Debug.out("Power off test\n");
		Bluetooth.setPower(false);
		try{Thread.sleep(1000);}catch(Exception e){}
		LCD.drawString("BT Off...", 0, 0);
		LCD.refresh();
		Debug.out("Power now off\n");
		try{Thread.sleep(delay);}catch(Exception e){}
		LCD.drawString("BT still Off...", 0, 0);
		LCD.refresh();
		Bluetooth.setPower(true);
		LCD.drawString("BT now on...", 0, 0);
		LCD.refresh();
		Debug.out("Power on\n");
		try{Thread.sleep(5000);}catch(Exception e){}
		byte [] ver = Bluetooth.getVersion();
		if (ver == null)
		{
			Debug.out("Failed to get version\n");
			return false;
		}
		Debug.out("Version major " + ver[0] + " minor " + ver[1] + "\n");
		return true;
	}

	public static boolean singleConnectTest(String name, int delay) throws Exception
	{
		// Create a single outbound connection to a device using the serial
		// profile. I tested this by simply running hyper-terminal using one
		// of the widcomm com ports. Test can be run with by slow and fast data
		// rates.
		Debug.out("Single connect test delay " + delay + "\n");
		BTRemoteDevice btrd = Bluetooth.getKnownDevice(name);
		if (btrd == null) {
			Debug.out("No such device " + name + "\n");
			return false;
		}

		BTConnection btc = Bluetooth.connect(btrd);
		Debug.out("After connect\n");
		if (btc == null) {
			Debug.out("Connect failed\n");
			return false;
		}
		byte [] status = Bluetooth.getConnectionStatus();
		if (status == null)
		{
			Debug.out("Failed to get connection status\n");
			btc.close();
			return false;
		}
		for(int i = 0; i < status.length; i++)
			Debug.out("Handle " + i + " status " + status[i] + "\n");
		btc.setIOMode(0);
		for(int i = 0; i < 100; i++)
		{
			byte [] b = strToByte("Hello world " + i + "\r\n");
			if (btc != null) btc.write(b, b.length);
			try{Thread.sleep(delay);}catch(Exception e){}
		}

		if (btc != null) btc.close();
		Debug.out("All closed\n");
		return true;
	}
	
	
	public static boolean multiConnectTest(String name, String name2, boolean userp) throws Exception
	{
		// Test multiple connections...
		// Wait for a packet based connect
		// open stream connections to two systems
		// Read packets from first connect
		// Send text messages to the other two connections
		
		try{Thread.sleep(1000);}catch(Exception e){}
		LCD.drawString("Running...", 0, 0);
		LCD.refresh();	

		Debug.out("Multi connect test readPacket " + userp + "\n");
		Debug.out("Wait for connection...");
		BTConnection bt = Bluetooth.waitForConnection();	
		if (bt == null)
		{
			Debug.out("Wait for connection failed\n");
			return false;
		}
		Debug.out("Connected\n");
			
		BTRemoteDevice btrd = Bluetooth.getKnownDevice(name);
		if (btrd == null) {
			Debug.out("No such device " + name + "\n");
			bt.close();
			return false;
		}

		BTConnection btc = Bluetooth.connect(btrd);
		Debug.out("After connect\n");
		if (btc == null) {
			Debug.out("Connect failed\n");
			bt.close();
			return false;
		}
		
		btrd = Bluetooth.getKnownDevice(name2);
		if (btrd == null) {
			Debug.out("No such device " + name2 +"\n");
			bt.close();
			btc.close();
			return false;
		}

		BTConnection btc2 = Bluetooth.connect(btrd);
		
		if (btc2 == null) {
			Debug.out("Connect fail\n");
			bt.close();
			btc.close();
			return false;
		}		

		btc.setIOMode(0);
		btc2.setIOMode(0);
		byte []inBuf = new byte[255];
		while (true)
		{
			int cnt;
			if (userp)
				cnt = bt.readPacket(inBuf, inBuf.length);
			else
				cnt = bt.read(inBuf, inBuf.length);
			if (cnt == 0) continue;
			if (cnt == -2)
			{
				Debug.out("Lost data, resync\n");
				bt.read(null, 256);
				continue;
			}
			int val = ((int)inBuf[2] & 0xff) + (((int)inBuf[3] & 0xff) << 8);
			Debug.out("Read len " + cnt  +" val " + val + "\n");
			if (val == 0xffff) break;
			byte [] b = strToByte("Hello from bt " + val + "\r\n");
			if (btc != null) btc.write(b, b.length);
			if (btc2 != null) btc2.write(b, b.length);
		}
		if (btc != null) btc.close();
		if (btc2 != null) btc2.close();
		if (bt != null) bt.close();	
		Debug.out("All closed\n");
		return true;
	}
	
	public static boolean listenTest() throws Exception
	{
		// Test the various states of listening for a connection.
		byte []outBuf = new byte[255];
		byte []inBuf = new byte[255];
		int buflen;
		Debug.out("listenTest\n");
		Bluetooth.getVersion();
		Bluetooth.closePort();
		Debug.out("Connecting now should fail\n");
		try{Thread.sleep(20000);}catch(Exception e) {}
		Debug.out("Wait for connection...\n");
		BTConnection bt = Bluetooth.waitForConnection();
		if (bt == null)
		{
			Debug.out("Did not get a connection\n");
			return false;
		}
		Debug.out("After connect\n");
		if (bt != null) bt.close();
		return true;
	}
	
	public static boolean miscTest() throws Exception
	{
		// Test various Bluetooth functions.
		byte [] ver = Bluetooth.getVersion();
		if (ver == null)
		{
			Debug.out("Failed to get version\n");
			return false;
		}
		Debug.out("Version major " + ver[0] + " minor " + ver[1] + "\n");
		int vis = Bluetooth.getVisibility();
		if (vis < 0)
		{
			Debug.out("Failed to get visibility\n");
			return false;
		}
		Debug.out("Current visibility is " + vis + "\n");
		Bluetooth.setVisibility((byte)0);
		if (Bluetooth.getVisibility() != 0)
		{
			Debug.out("Failed to turn off visibility\n");
			return false;
		}
		Debug.out("NXT now not visibile\n");
		try{Thread.sleep(20000);}catch(Exception e) {}	
		Bluetooth.setVisibility((byte)1);
		if (Bluetooth.getVisibility() != 1)
		{
			Debug.out("Failed to turn off visibility\n");
			return false;
		}
		Debug.out("NXT now visibile\n");
		try{Thread.sleep(20000);}catch(Exception e) {}	
		Bluetooth.setVisibility((byte)vis);
		int stat = Bluetooth.getStatus();
		if (stat < 0)
		{
			Debug.out("Failed to read status\n");
			return false;
		}
		Debug.out("Current status is " + stat + "\n");
		Bluetooth.setStatus(2742);
		int val = Bluetooth.getStatus();
		if (val != 2742)
		{
			Debug.out("Failed to set/get status val is " + val + "\n");
			return false;
		}
		Bluetooth.setStatus(stat);
		Debug.out("getStatus OK\n");
		int port = Bluetooth.getPortOpen();
		if (port < 0)
		{
			Debug.out("Failed to read port state\n");
			return false;			
		}
		Bluetooth.openPort();
		if (Bluetooth.getPortOpen() != 1)
		{
			Debug.out("getPortOpen failed\n");
			return false;
		}
		Bluetooth.closePort();
		if (Bluetooth.getPortOpen() != 0)
		{
			Debug.out("getPortOpen failed\n");
			return false;
		}
		if (port == 1) Bluetooth.openPort();
		Debug.out("getPortOpen OK\n");
		int mode=Bluetooth.getOperatingMode();
		if (mode < 0)
		{
			Debug.out("Failed to read operating mode\n");
			return false;			
		}
		Debug.out("Current mode is " + mode + "\n");
		Bluetooth.setOperatingMode((byte)0);
		if (Bluetooth.getOperatingMode() != 0)
		{
			Debug.out("Failed to set/get mode\n");
			return false;
		}
		Bluetooth.setOperatingMode((byte)1);
		if (Bluetooth.getOperatingMode() != 1)
		{
			Debug.out("Failed to set/get mode\n");
			return false;
		}	
		Bluetooth.setOperatingMode((byte)mode);
		Debug.out("Get/Set operating mode OK\n");
		return true;
	}

	public static boolean deviceTest() throws Exception
	{
		// Test the device discovery, addition and removal functions.
		Debug.out("deviceTest\n");
		Vector curList = Bluetooth.getKnownDevicesList();
		if (curList == null)
		{
			Debug.out("getKnownDeviceList returns null\n");
			return false;
		}
		for(int i = 0; i < curList.size(); i++)
			Debug.out("Current device " + ((BTRemoteDevice)curList.elementAt(i)).getFriendlyName() + "\n");
		Debug.out("Delete all\n");
		for(int i = 0; i < curList.size(); i++)
			Bluetooth.removeDevice((BTRemoteDevice)curList.elementAt(i));
		Vector newList = Bluetooth.getKnownDevicesList();
		if (newList == null)
		{
			Debug.out("getKnownDeviceList returns null\n");
			return false;
		}
		for(int i = 0; i < newList.size(); i++)
			Debug.out("Current device " + ((BTRemoteDevice)newList.elementAt(i)).getFriendlyName() + "\n");

		byte[] cod = {0,0,0,0}; // Any
		Debug.out("Searching...\n");
		Vector devList = Bluetooth.inquire(5, 10,cod);
		if (devList == null)
		{
			Debug.out("Inquire returns null\n");
			return false;
		}
		if (devList.size() > 0) {
			String[] names = new String[devList.size()];
			for (int i = 0; i < devList.size(); i++) {
				BTRemoteDevice btrd = ((BTRemoteDevice) devList.elementAt(i));
				names[i] = btrd.getFriendlyName();
				Debug.out("Got device " + names[i] + "\n");
			}
		}
		Debug.out("Add all\n");
		for(int i = 0; i < devList.size(); i++)
			if (!Bluetooth.addDevice((BTRemoteDevice)devList.elementAt(i)))
			{
				Debug.out("Failed to add device " + ((BTRemoteDevice)devList.elementAt(i)).getFriendlyName() + "\n");
				return false;
			}
		newList = Bluetooth.getKnownDevicesList();
		if (newList == null)
		{
			Debug.out("getKnownDeviceList returns null\n");
			return false;
		}
		for(int i = 0; i < newList.size(); i++)
			Debug.out("Current device " + ((BTRemoteDevice)newList.elementAt(i)).getFriendlyName() + "\n");
		byte[] name = Bluetooth.getFriendlyName();
		byte[] saved = name;
		char [] cName = new char[name.length];
		int cNameLen = 0;
		for(int i = 0; i < name.length && name[i] != 0; i++)
				cName[cNameLen++] = (char)name[i];
		String sName = new String(cName, 0, cNameLen);
		Debug.out("Friendly name is " + sName + "\n");
		byte [] newName = {(byte)'T', (byte)'E', (byte)'S', (byte)'T'};
		byte [] newNamePad = new byte[16];
		System.arraycopy(newName, 0, newNamePad, 0, newName.length);
		Bluetooth.setFriendlyName(newNamePad);
		name = Bluetooth.getFriendlyName();
		cNameLen = 0;
		for(int i = 0; i < name.length && name[i] != 0; i++)
				cName[cNameLen++] = (char)name[i];
		sName = new String(cName, 0, cNameLen);
		Debug.out("New friendly name is " + sName + "\n");		
		Bluetooth.setFriendlyName(saved);	
		name = Bluetooth.getFriendlyName();
		cNameLen = 0;
		for(int i = 0; i < name.length && name[i] != 0; i++)
				cName[cNameLen++] = (char)name[i];
		sName = new String(cName, 0, cNameLen);
		Debug.out("Reset friendly name is " + sName + "\n");
		byte [] addr = Bluetooth.getLocalAddress();
		Debug.out("Local address is");
		for(int i = 0; i < addr.length; i++)
			Debug.out(" " + addr[i]);
		Debug.out("\n");
		return true;
	}
	
	
	public static void main(String[] args) throws Exception
	{
		int testCnt = 0;
		int passCnt = 0;
		Debug.open();
		Debug.out("Hello from bt test\n");
		testCnt++; if (powerOffTest(10000)) passCnt++;
		testCnt++; if (deviceTest()) passCnt++;
		testCnt++; if (miscTest()) passCnt++;
		testCnt++; if (listenTest()) passCnt++;
		testCnt++; if (ioTest()) passCnt++;
		testCnt++; if (singleConnectTest("EEYOREII", 1000)) passCnt++;
		testCnt++; if (singleConnectTest("EEYOREII", 1)) passCnt++;
		testCnt++; if (multiConnectTest("EEYORE", "EEYOREII", false)) passCnt++;
		testCnt++; if (multiConnectTest("EEYORE", "EEYOREII", true)) passCnt++;
		testCnt++; if (badSwitchTest()) passCnt++;
		Debug.out("Tests complete. Tested " + testCnt + " passed " + passCnt + "\n");
		try{Thread.sleep(5000);} catch(Exception e){}
		Debug.close();
	}
}


		