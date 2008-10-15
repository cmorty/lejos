import java.io.*;
import java.util.Vector;

import lejos.nxt.comm.*;
import lejos.nxt.*;
import javax.bluetooth.*;



/*
 * Bluetooth test code.
 * Tests developed alongside the new Bluetooth implementation. Included as an
 * indication how these classes may be tested. Note that some test cases
 * use the names of target Bluetooth devices and may need changing. Also
 * some tests require the use of a PC based test program (to be be found in
 * BTTestPC).
 */

public class BTTest {
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
		RConsole.println("Bad switch test, expect lost data\n");
		RConsole.println("Wait for connection...");
		BTConnection bt = Bluetooth.waitForConnection();
		if (bt == null)
		{
			RConsole.println("Did not get connection\n");
			return false;
		}
		RConsole.println("After connect\n");
		while (bt.readPacket(inBuf, inBuf.length) == 0) ;
		outBuf[0] = 4;
		outBuf[1] = (byte)131;
		outBuf[2] = 4;

		RConsole.println("signal strength " + bt.getSignalStrength() + "\n");		

		while (true)
		{
			//int cnt = bt.readPacket(inBuf, inBuf.length);
			int cnt = bt.read(inBuf, inBuf.length);
			if (cnt == 0) continue;
			if (cnt == -2)
			{
				RConsole.println("Lost data, resync\n");
				bt.read(null, 256);
				continue;
			}
			int val = ((int)inBuf[2] & 0xff) + (((int)inBuf[3] & 0xff) << 8);
			RConsole.println("Read len " + cnt  +" val " + val + "\n");
			if (val == 0xffff) break;
			if (val == 27) RConsole.println("signal strength " + bt.getSignalStrength() + "\n");
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
		RConsole.println("ioTest\n");
		RConsole.println("Wait for connection...\n");
		BTConnection bt = Bluetooth.waitForConnection();
		if (bt == null)
		{
			RConsole.println("Did not get a connection\n");
			return false;
		}
		RConsole.println("After connect\n");
		int start = (int)System.currentTimeMillis();

		// First test low level writes
		outBuf[0] = 4;
		outBuf[1] = (byte)131;
		outBuf[2] = 4;

		RConsole.println("signal strength " + bt.getSignalStrength() + "\n");		

		RConsole.println("Testing write\n");
		for(int i=0; i < 1000; i++)
		{
			outBuf[3] = (byte)(i & 0xff);
			outBuf[4] = (byte) ((i >> 8) & 0xff);
			bt.write(outBuf, 133);
		}
		RConsole.println("write complete time " + ((int) System.currentTimeMillis() - start) + "\n");
		RConsole.println("Testing output stream");
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
		RConsole.println("signal strength " + bt.getSignalStrength() + "\n");
		RConsole.println("Testing read\n");
		while (true)
		{
			//int cnt = bt.readPacket(inBuf, inBuf.length);
			int cnt = bt.read(inBuf, inBuf.length);
			if (cnt == 0) continue;
			if (cnt == -2)
			{
				RConsole.println("Lost data, resync\n");
				bt.read(null, 256);
				continue;
			}
			int val = ((int)inBuf[2] & 0xff) + (((int)inBuf[3] & 0xff) << 8);
					//RConsole.println("Memory " + (int)Runtime.getRuntime().freeMemory() + "\n");
			RConsole.println("Read len " + cnt  +" val " + val + "\n");
			if (val == 0xffff) break;
		}
		RConsole.println("Testing input stream\n");
		// Now test input
		InputStream is = bt.openInputStream();
		while (true)
		{
			//int cnt = bt.readPacket(inBuf, inBuf.length);
			int cnt = is.read(inBuf, 0, 18);
			if (cnt == 0) continue;
			int val = ((int)inBuf[2] & 0xff) + (((int)inBuf[3] & 0xff) << 8);
			RConsole.println("Read len " + cnt  +" val " + val + "\n");
			if (val == 0xffff) break;
		}
		if (bt != null) bt.close();
		return true;
	}
	
	
	public static boolean powerOffTest(int delay) throws Exception
	{
		// Test that the NXT Bluetooth module can be powered off. Also check
		// that the LCD continues to operate.
		RConsole.println("Power off test\n");
		Bluetooth.setPower(false);
		try{Thread.sleep(1000);}catch(Exception e){}
		LCD.drawString("BT Off...", 0, 0);
		LCD.refresh();
		RConsole.println("Power now off\n");
		try{Thread.sleep(delay);}catch(Exception e){}
		LCD.drawString("BT still Off...", 0, 0);
		LCD.refresh();
		Bluetooth.setPower(true);
		LCD.drawString("BT now on...", 0, 0);
		LCD.refresh();
		RConsole.println("Power on\n");
		try{Thread.sleep(5000);}catch(Exception e){}
		byte [] ver = Bluetooth.getVersion();
		if (ver == null)
		{
			RConsole.println("Failed to get version\n");
			return false;
		}
		RConsole.println("Version major " + ver[0] + " minor " + ver[1] + "\n");
		return true;
	}

	public static boolean singleConnectTest(String name, int delay) throws Exception
	{
		// Create a single outbound connection to a device using the serial
		// profile. I tested this by simply running hyper-terminal using one
		// of the widcomm com ports. Test can be run with by slow and fast data
		// rates.
		RConsole.println("Single connect test delay " + delay + "\n");
		RemoteDevice btrd = Bluetooth.getKnownDevice(name);
		if (btrd == null) {
			RConsole.println("No such device " + name + "\n");
			return false;
		}

		BTConnection btc = Bluetooth.connect(btrd);
		RConsole.println("After connect\n");
		if (btc == null) {
			RConsole.println("Connect failed\n");
			return false;
		}
		byte [] status = Bluetooth.getConnectionStatus();
		if (status == null)
		{
			RConsole.println("Failed to get connection status\n");
			btc.close();
			return false;
		}
		for(int i = 0; i < status.length; i++)
			RConsole.println("Handle " + i + " status " + status[i] + "\n");
		btc.setIOMode(NXTConnection.RAW);
		for(int i = 0; i < 100; i++)
		{
			byte [] b = strToByte("Hello world " + i + "\r\n");
			if (btc != null) btc.write(b, b.length);
			try{Thread.sleep(delay);}catch(Exception e){}
		}

		if (btc != null) btc.close();
		RConsole.println("All closed\n");
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

		RConsole.println("Multi connect test readPacket " + userp + "\n");
		RConsole.println("Wait for connection...");
		BTConnection bt = Bluetooth.waitForConnection();	
		if (bt == null)
		{
			RConsole.println("Wait for connection failed\n");
			return false;
		}
		RConsole.println("Connected\n");
			
		RemoteDevice btrd = Bluetooth.getKnownDevice(name);
		if (btrd == null) {
			RConsole.println("No such device " + name + "\n");
			bt.close();
			return false;
		}

		BTConnection btc = Bluetooth.connect(btrd);
		RConsole.println("After connect\n");
		if (btc == null) {
			RConsole.println("Connect failed\n");
			bt.close();
			return false;
		}
		
		btrd = Bluetooth.getKnownDevice(name2);
		if (btrd == null) {
			RConsole.println("No such device " + name2 +"\n");
			bt.close();
			btc.close();
			return false;
		}

		BTConnection btc2 = Bluetooth.connect(btrd);
		
		if (btc2 == null) {
			RConsole.println("Connect fail\n");
			bt.close();
			btc.close();
			return false;
		}		

		btc.setIOMode(NXTConnection.RAW);
		btc2.setIOMode(NXTConnection.RAW);
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
				RConsole.println("Lost data, resync\n");
				bt.read(null, 256);
				continue;
			}
			int val = ((int)inBuf[2] & 0xff) + (((int)inBuf[3] & 0xff) << 8);
			RConsole.println("Read len " + cnt  +" val " + val + "\n");
			if (val == 0xffff) break;
			byte [] b = strToByte("Hello from bt " + val + "\r\n");
			if (btc != null) btc.write(b, b.length);
			if (btc2 != null) btc2.write(b, b.length);
		}
		if (btc != null) btc.close();
		if (btc2 != null) btc2.close();
		if (bt != null) bt.close();	
		RConsole.println("All closed\n");
		return true;
	}
	
	public static boolean listenTest() throws Exception
	{
		// Test the various states of listening for a connection.
		byte []outBuf = new byte[255];
		byte []inBuf = new byte[255];
		int buflen;
		RConsole.println("listenTest\n");
		Bluetooth.getVersion();
		Bluetooth.closePort();
		RConsole.println("Connecting now should fail\n");
		try{Thread.sleep(20000);}catch(Exception e) {}
		RConsole.println("Wait for connection...\n");
		BTConnection bt = Bluetooth.waitForConnection();
		if (bt == null)
		{
			RConsole.println("Did not get a connection\n");
			return false;
		}
		RConsole.println("After connect\n");
		if (bt != null) bt.close();
		return true;
	}
	
	public static boolean miscTest() throws Exception
	{
		// Test various Bluetooth functions.
		byte [] ver = Bluetooth.getVersion();
		if (ver == null)
		{
			RConsole.println("Failed to get version\n");
			return false;
		}
		RConsole.println("Version major " + ver[0] + " minor " + ver[1] + "\n");
		int vis = Bluetooth.getVisibility();
		if (vis < 0)
		{
			RConsole.println("Failed to get visibility\n");
			return false;
		}
		RConsole.println("Current visibility is " + vis + "\n");
		Bluetooth.setVisibility((byte)0);
		if (Bluetooth.getVisibility() != 0)
		{
			RConsole.println("Failed to turn off visibility\n");
			return false;
		}
		RConsole.println("NXT now not visibile\n");
		try{Thread.sleep(20000);}catch(Exception e) {}	
		Bluetooth.setVisibility((byte)1);
		if (Bluetooth.getVisibility() != 1)
		{
			RConsole.println("Failed to turn off visibility\n");
			return false;
		}
		RConsole.println("NXT now visibile\n");
		try{Thread.sleep(20000);}catch(Exception e) {}	
		Bluetooth.setVisibility((byte)vis);
		int stat = Bluetooth.getStatus();
		if (stat < 0)
		{
			RConsole.println("Failed to read status\n");
			return false;
		}
		RConsole.println("Current status is " + stat + "\n");
		Bluetooth.setStatus(2742);
		int val = Bluetooth.getStatus();
		if (val != 2742)
		{
			RConsole.println("Failed to set/get status val is " + val + "\n");
			return false;
		}
		Bluetooth.setStatus(stat);
		RConsole.println("getStatus OK\n");
		int port = Bluetooth.getPortOpen();
		if (port < 0)
		{
			RConsole.println("Failed to read port state\n");
			return false;			
		}
		Bluetooth.openPort();
		if (Bluetooth.getPortOpen() != 1)
		{
			RConsole.println("getPortOpen failed\n");
			return false;
		}
		Bluetooth.closePort();
		if (Bluetooth.getPortOpen() != 0)
		{
			RConsole.println("getPortOpen failed\n");
			return false;
		}
		if (port == 1) Bluetooth.openPort();
		RConsole.println("getPortOpen OK\n");
		int mode=Bluetooth.getOperatingMode();
		if (mode < 0)
		{
			RConsole.println("Failed to read operating mode\n");
			return false;			
		}
		RConsole.println("Current mode is " + mode + "\n");
		Bluetooth.setOperatingMode((byte)0);
		if (Bluetooth.getOperatingMode() != 0)
		{
			RConsole.println("Failed to set/get mode\n");
			return false;
		}
		Bluetooth.setOperatingMode((byte)1);
		if (Bluetooth.getOperatingMode() != 1)
		{
			RConsole.println("Failed to set/get mode\n");
			return false;
		}	
		Bluetooth.setOperatingMode((byte)mode);
		RConsole.println("Get/Set operating mode OK\n");
		return true;
	}

	public static boolean deviceTest() throws Exception
	{
		// Test the device discovery, addition and removal functions.
		RConsole.println("deviceTest\n");
		Vector curList = Bluetooth.getKnownDevicesList();
		if (curList == null)
		{
			RConsole.println("getKnownDeviceList returns null\n");
			return false;
		}
		for(int i = 0; i < curList.size(); i++)
			RConsole.println("Current device " + ((RemoteDevice)curList.elementAt(i)).getFriendlyName(false) + "\n");
		RConsole.println("Delete all\n");
		for(int i = 0; i < curList.size(); i++)
			Bluetooth.removeDevice((RemoteDevice)curList.elementAt(i));
		Vector newList = Bluetooth.getKnownDevicesList();
		if (newList == null)
		{
			RConsole.println("getKnownDeviceList returns null\n");
			return false;
		}
		for(int i = 0; i < newList.size(); i++)
			RConsole.println("Current device " + ((RemoteDevice)newList.elementAt(i)).getFriendlyName(false) + "\n");

		byte[] cod = {0,0,0,0}; // Any
		RConsole.println("Searching...\n");
		Vector devList = Bluetooth.inquire(5, 10,cod);
		if (devList == null)
		{
			RConsole.println("Inquire returns null\n");
			return false;
		}
		if (devList.size() > 0) {
			String[] names = new String[devList.size()];
			for (int i = 0; i < devList.size(); i++) {
				RemoteDevice btrd = ((RemoteDevice) devList.elementAt(i));
				names[i] = btrd.getFriendlyName(false);
				RConsole.println("Got device " + names[i] + "\n");
			}
		}
		RConsole.println("Add all\n");
		for(int i = 0; i < devList.size(); i++)
			if (!Bluetooth.addDevice((RemoteDevice)devList.elementAt(i)))
			{
				RConsole.println("Failed to add device " + ((RemoteDevice)devList.elementAt(i)).getFriendlyName(false) + "\n");
				return false;
			}
		newList = Bluetooth.getKnownDevicesList();
		if (newList == null)
		{
			RConsole.println("getKnownDeviceList returns null\n");
			return false;
		}
		for(int i = 0; i < newList.size(); i++)
			RConsole.println("Current device " + ((RemoteDevice)newList.elementAt(i)).getFriendlyName(false) + "\n");
		byte[] name = Bluetooth.getFriendlyName();
		byte[] saved = name;
		char [] cName = new char[name.length];
		int cNameLen = 0;
		for(int i = 0; i < name.length && name[i] != 0; i++)
				cName[cNameLen++] = (char)name[i];
		String sName = new String(cName, 0, cNameLen);
		RConsole.println("Friendly name is " + sName + "\n");
		byte [] newName = {(byte)'T', (byte)'E', (byte)'S', (byte)'T'};
		byte [] newNamePad = new byte[16];
		System.arraycopy(newName, 0, newNamePad, 0, newName.length);
		Bluetooth.setFriendlyName(newNamePad);
		name = Bluetooth.getFriendlyName();
		cNameLen = 0;
		for(int i = 0; i < name.length && name[i] != 0; i++)
				cName[cNameLen++] = (char)name[i];
		sName = new String(cName, 0, cNameLen);
		RConsole.println("New friendly name is " + sName + "\n");		
		Bluetooth.setFriendlyName(saved);	
		name = Bluetooth.getFriendlyName();
		cNameLen = 0;
		for(int i = 0; i < name.length && name[i] != 0; i++)
				cName[cNameLen++] = (char)name[i];
		sName = new String(cName, 0, cNameLen);
		RConsole.println("Reset friendly name is " + sName + "\n");
		byte [] addr = Bluetooth.getLocalAddress();
		RConsole.println("Local address is");
		for(int i = 0; i < addr.length; i++)
			RConsole.println(" " + addr[i]);
		RConsole.println("\n");
		return true;
	}
	
	
	public static void main(String[] args) throws Exception
	{
		int testCnt = 0;
		int passCnt = 0;
		RConsole.open();
		RConsole.println("Hello from bt test\n");
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
		RConsole.println("Tests complete. Tested " + testCnt + " passed " + passCnt + "\n");
		try{Thread.sleep(5000);} catch(Exception e){}
		RConsole.close();
	}
}


		