import lejos.nxt.*;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.RConsole;
import lejos.nxt.comm.NXTConnection;

import javax.bluetooth.RemoteDevice;
import lejos.nxt.comm.BTConnection;
import lejos.devices.*;

import java.util.Vector;
import java.io.*;

/**
 * This is some sample code to demonstrate the Keyboard class. It
 * allows you to connect and display typing on the NXT LCD.
 * Only works with SPP Bluetooth keyboards (very rare). Will not work with
 * HID BT keyboards. See Keyboard Javadocs for more information.
 * @author BB
 */
public class KeyboardTest implements KeyListener {
	
	static boolean cont = true;
	
	public KeyboardTest() {
		Keyboard k = connectKeyboard();
		k.addKeyListener(this);
	}
	
	public static void main(String [] args) {
		//RConsole.openUSB(0);
		//RConsole.openBluetooth(0);
		//System.setErr(new PrintStream(RConsole.openOutputStream()));
		//System.err.println("Starting...");
    	
		KeyboardTest kt = new KeyboardTest();
		LCD.clear();
		LCD.refresh();
		while(cont) {Thread.yield();}
		System.out.println("Quitting...");
		//RConsole.close();
	}
	
	public void keyPressed(KeyEvent e) {
		System.out.print("" + e.getKeyChar());
		if(e.getKeyChar() == 'q') {
			cont = false; // or System.exit(0);
		} else if(e.getKeyChar() == 'z') {
			System.out.println("");
		}
	}
	
	public void keyReleased(KeyEvent e) {
		System.out.println("Key Released: " + e.getKeyChar());
		if(e.getKeyChar() == 'q') {
			cont = false;
		}
	}
	
	public void keyTyped(KeyEvent e) {
		System.out.println("Key Typed: " + e.getKeyChar());
		if(e.getKeyChar() == 'q') {
			cont = false;
		}
	}
	
	public Keyboard connectKeyboard() {
		
		Keyboard k = null;
		
		byte[] cod = {0,0,0,0}; // 0,0,0,0 picks up every Bluetooth device regardless of Class of Device (cod).
				
		final byte[] pin = {(byte) '0', (byte) '0', (byte) '0', (byte) '0'};
		
		InputStream in = null;
		OutputStream out = null;
		
		System.out.println("Searching ...");
		Vector devList = Bluetooth.inquire(5, 10,cod);
		
		if (devList.size() > 0) {
			String[] names = new String[devList.size()];
			for (int i = 0; i < devList.size(); i++) {
				RemoteDevice btrd = ((RemoteDevice) devList.elementAt(i));
				names[i] = btrd.getFriendlyName(false);
			}
				
			TextMenu searchMenu = new TextMenu(names,1);
			String[] subItems = {"Connect"};
			TextMenu subMenu = new TextMenu(subItems,4);
			
			int selected;
			do {
	    		LCD.clear();
				LCD.drawString("Found",6,0);
				LCD.refresh();
				selected = searchMenu.select();
				if (selected >=0) {
					RemoteDevice btrd = ((RemoteDevice) devList.elementAt(selected));
					LCD.drawString(names[selected],0,1);
					LCD.drawString(btrd.getBluetoothAddress(), 0, 2);
					int subSelection = subMenu.select();
					if (subSelection == 0) Bluetooth.addDevice(btrd);
					
					//LCD.clear();
					
					// BELOW: Once paired, no need to use pin? Might
					// use alternate method.
					BTConnection btSPPDevice = null;
					//System.err.println("About to connect w/pin " + (char)pin[0] + (char)pin[1] + (char)pin[2] + (char)pin[3] + "\n");
                    // Open connection in raw/stream mode
                    btSPPDevice = Bluetooth.connect(btrd.getDeviceAddr(), NXTConnection.RAW, pin);
					//if(btSPPDevice == null)
					//	System.err.println("Connect failed.\n");
					//else
					//	System.err.println("Connect worked.\n");
					
					//System.err.println("Setting to stream mode.\n");
					//btSPPDevice.setIOMode(0); // 0 = Stream mode?
					
					try {
						//System.err.println("About to open BTConnection.openInputStream()"  + "\n");
						in = btSPPDevice.openInputStream();
						out = btSPPDevice.openOutputStream();
						//System.err.println("Streams captured.\n");
						k = new Keyboard(in, out);
						selected = -1; // to make it stop looping?
						
					} catch(Exception e) {
						//System.err.println("InputStream NOT captured." + "\n");
					}
				}
			} while (selected >= 0);

		} else {
			LCD.clear();
			LCD.drawString("no devices", 0, 0);
			LCD.refresh();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {}
		}
		return k;
	}
}
