package lejos.keyboard;

import java.io.*;

/*
 * Developer Notes:
 * The following document explains keyboard control:
 * http://www.computer-engineering.org/ps2keyboard/
 */

/**
 * This class will only work with SPP keyboards, not standard HID
 * keyboards. If it doesn't say it supports Bluetooth SPP then it
 * will not work. There are only two known SPP models (also available
 * on eBay or Amazon):
 * Freedom Universal Bluetooth keyboard
 * http://www.freedominput.com
 * iTech Virtual Keyboard (SPP only)
 * http://www.virtual-laser-keyboard.com/
 * 
 * Note: This class is currently only tested with Freedom Universal
 * 
 */
// TODO: Create a method that returns a stream of ASCII from keyboard. Thread handles keep alive, auto connect
// and reconnecting if disconnected.
// TODO: Currently only handles make code. Need break code (key release).
// TODO: Repeat when key held down. (See above)


// Typematic Key Repeat:
// Typematic delay - 0.25 seconds to 1.00 second (500ms default)
// Typematic rate - 2.0 cps (characters per second) to 30.0 cps (10.9 default)
// "Set Typematic Rate/Delay" (0xF3) command
// Typematic data is not buffered within the keyboard.  In the case 
// where more than one key is held down, only the last key pressed 
// becomes typematic.  Typematic repeat then stops when that key is 
// released, even though other keys may be held down.

public class Keyboard extends Thread {
	
	/**
	 * Command used by keyboards. Full list of commands can be found here:
	 * http://www.computer-engineering.org/ps2keyboard/
	 */
	private static int ECHO = 0xEE; 
	
	/**
	 * Time between echo command sent to keyboard to prevent it
	 * from going to sleep. Universal Keyboard sleeps after 5000 ms
	 */
	private static int KEEP_ALIVE_DELAY = 4000; // milliseconds
	
	private static int MAX_LISTENERS = 3;
	
	private InputStream in = null;
	private OutputStream out = null;
	
	// Use Vector? Resizable array?
	private KeyListener [] listeners = new KeyListener[MAX_LISTENERS];
		
	/**
	 * The index of this array is the Scan Code (e.g. 0x1C) and the
	 * value at that index is the appropriate ASCII key (without shift key).
	 * NOTE: There is no ASCII Caps Lock for 0x58. There is shift-in and shift-out, but that
	 * seems to have a different function than Caps (i.e. Caps only applies to A-Z, but shift 
	 * applies to number keys displaying symbols).
	 * Note: Scan code 0x12 is left-shift, however ASCII has shift-in (0x0F) and shift-out (0x0E) so
	 * I'm uncertain how to handle this as a stream. Will have to do some special case statements. This
	 * might not really be a factor anyway since shifts probably irrelevant to ASCII stream.
	 * Note: ASCII has "Device Control" 1-4 I'm assuming ALT and CTRL fit in here. Maybe Fn. Maybe Windows key. 
	 * Again, these might be irrelevant to ASCII stream.
	 * NOTE: Enter - equivalent to line feed? (ASCII 0x0A) Should stream
	 * send two characters: line feed and carriage return?
	 * NOTE: There are no ASCII characters for arrow keys.
	 */
	// 64 unique keys on Freedom Universal, array is 113, many blanks	
	private static byte [] scanCodes = {
		0,0,0,0,0,0,0,0x14,0,0,0,0,0,0x09,0x60,0, // 0x00
		0,0x12,0x0F,0x12,0x11,0x71,0x31,0,0,0,0x7A,0x73,0x61,0x77,0x32,0, // 0x10
		0,0x63,0x78,0x64,0x65,0x34,0x33,0,0,0x20,0x76,0x66,0x74,0x72,0x35,0, // 0x20
		0,0x6E,0x62,0x68,0x67,0x79,0x36,0,0,0,0x6D,0x6A,0x75,0x37,0x38,0, // 0x30
		0,0,0x6B,0x69,0x6F,0x30,0x39,0,0,0x2E,0x2F,0x6C,0x3B,0x70,0x2D,0, // 0x40
		0,0,0x27,0,0x5B,0x3D,0,0,0x00,0x0F,0x0A,0x5D,0x20,0x5C,0,0, // 0x50
		0,0,0,0,0,0,0x7F,0,0,0,0,0,0,0,0,0, // 0x60
		0,0x08 // 0x70
	}; 
	
	public Keyboard(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
		this.setDaemon(true);
		this.start();
	}
	
	// TODO: Make InputStream that implements KeyListener, outputs ASCII
	public void addKeyListener(KeyListener l) {
		// !! Test code until I figure out storage object
		listeners[0] = l;
	}
	
	public void removeKeylistener(KeyListener l) {
		// !! Test code until I figure out storage object
		listeners[0] = null;
	}
	
	private void notifyListeners(KeyEvent e) {
		// !! Test code until I figure out storage object
		if(e.getID() == KeyEvent.KEY_PRESSED)
			listeners[0].keyPressed(e);
		else if(e.getID() == KeyEvent.KEY_RELEASED)
			listeners[0].keyReleased(e);
		else if(e.getID() == KeyEvent.KEY_TYPED)
			listeners[0].keyTyped(e);
	}
	
	/**
	 * Converts raw keyboard scan code into ASCII.
	 * Works with lower case only.
	 * @param scanCode
	 * @return the ascii character
	 */
	// TODO: Make private
	public static byte getASCII(byte scanCode) {
		if(scanCode < 0) // bit 8 makes value 0
			return 0; // !! Need to handle break code here
		return scanCodes[scanCode]; // !! handles make codes only
	}
	
	public void run() {
		//Debug.out("Keyboard thread started.\n");
		int previousEcho = (int)System.currentTimeMillis();
		while(true) {
			
			// Keep-alive code:
			int now = (int)System.currentTimeMillis();
			if(now - previousEcho >= KEEP_ALIVE_DELAY) {
				try {
					//Debug.out("Echo Sent\n");
					out.write(ECHO);
					out.flush();
				} catch(IOException e) {/*Debug.out("COMMAND EXCEPTION");*/}
				previousEcho = now;
			}

			// Notifier code:
			try {
				if(in.available() > 0) { // Check if byte available.
					int bval = in.read();
					KeyEvent e = getKeyEvent(bval, now);
					if(e != null) notifyListeners(e); // ignore non-chars
				}
			} catch(IOException e) {/*Debug.out("EXCEPTION");*/}
			Thread.yield();
		}
	}
	
	/**
	 * Helper method to generate keyEvent from scan code from keyboard 
	 * @return
	 */
	private KeyEvent getKeyEvent(int scanCode, int timeStamp) {
		// TEST CODE:
		char curChar = (char)Keyboard.getASCII((byte)scanCode);
		if((byte)scanCode < 0) return null; // if 8th bit on (i.e. key release)
		KeyEvent e = new KeyEvent(this, KeyEvent.KEY_PRESSED, timeStamp, 0, 0, curChar);
		//Debug.out(++sentenceCount + ": " + bval + " = " + (char)Keyboard.getASCII((byte)bval) + "\n");
		//System.out.println("" + (char)Keyboard.getASCII((byte)scanCode));
		return e;
	}
}
