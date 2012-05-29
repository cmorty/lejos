package lejos.addon.keyboard;

import java.io.*;

import javax.bluetooth.*;
import javax.microedition.io.*;
import lejos.util.Delay;

/*
 * Developer Notes:
 * TODO: The preferred paradigm is to allow multiple text fields, multiple apps in MIDP
 * and the one with focus is the one that gets the key events. see javax.microedition.lcdui.ItemStateListener?
 * 
 * TODO: Would be slick if it would connect to leJOS as soon as it is turned on, like Windows XP.
 * 
 * Programming keyboard input is not very simple or straightforward.
 * This class basically converts the keyboard scan code into a VK constant (virtual keyboard)
 * located in KeyEvent. The method that does this is getJavaConstant().
 * Then this class then looks at the modifier keys (like shift) and converts the VK
 * constant into the proper ASCII character. 
 * 
 * Freedom Universal is an AT keyboard, not XT. It uses Scan Code Set 2:
 * http://www.computer-engineering.org/ps2keyboard/
 * You will not find characters such as ? or lower case because those are Virtual Keys (VK).
 * Here is a nice keyboard emulator which shows the scancode values to expect:
 * http://www.barcodeman.com/altek/mule/kbemulator/
 */

/**
 * <p>This class will only work with SPP keyboards, not standard HID
 * keyboards. If it doesn't say it supports Bluetooth SPP then it
 * will not work. There are currently only two known SPP models (also available
 * on eBay or Amazon):
 * <li>Freedom Universal Bluetooth keyboard<br>
 * http://www.freedominput.com
 * <li>iTech Virtual Keyboard (SPP only)<br>
 * http://www.virtual-laser-keyboard.com/
 * </p>
 * <p>The SPP keyboards transmit keystrokes as one byte, not two like normal HID keyboards.
 * For this reason, this class won't work properly with a regular HID keyboard as written.
 * With modifications it could be made to work with both SPP and HID keyboards since
 * the key tables are mostly the same.</p>
 * <p>Note: This class is currently only tested with Freedom Universal. If you have problems with the iTech Virtual Keyboard, write to bbagnall@mts.net
 * and I will try my best to adapt this class.</p>
 * @author BB
 */
public class Keyboard extends Thread { // TODO: Use internal Thread so it isn't exposed (especially run()).

	// Typematic Key Repeat:
	/** 
	 * Typematic delay - 0.25 seconds to 1.00 second (500ms default)
	 */
	private int typematicDelay = 500;
	
	/** 
	 * Typematic rate - 2.0 cps (characters per second) to 30.0 cps (10.9 default)
	 */
	private double typematicRate = 10.9;
	private int ratePause =  (int)(1000.0/typematicRate);
	
	private char lastKeyPress = NOT_ASCII_CHAR;
	
	private char oldChar = NOT_ASCII_CHAR;
	
	/**
	 * Represents a key press that is not an ASCII character (e.g. Caps lock, Shift, etc...)
	 */
	private static char NOT_ASCII_CHAR = 0xFFFF;
	
	/**
	 * Command used by keyboards. Full list of commands can be found here:
	 * http://www.computer-engineering.org/ps2keyboard/
	 */
	private static int ECHO = 0xEE; 
	
	/**
	 * Indicates if keyboard has caps lock on.
	 */
	private boolean capsLock = false;
	
	/**
	 * Time between echo command sent to keyboard to prevent it
	 * from going to sleep. Universal Keyboard sleeps after 5000 ms
	 */
	private static int KEEP_ALIVE_DELAY = 4000; // milliseconds
	
	private InputStream in = null;
	private OutputStream out = null;
	
	private int modifiers; // keeps track of modifier keys held down (shift, alt, etc)
	
	private KeyListener keyListener = null;
	
	private Keyboard kb = this; // Used by typematicThread
	
	/**
	 * Thread to handle typematic key repeating.
	 * Typematic data is not buffered within the keyboard.  In the case
	 * where more than one key is held down, only the last key pressed
	 * becomes typematic. Typematic repeat then stops when that key is
	 * released, even though other keys may be held down.
	 * "Set Typematic Rate/Delay" (0xF3) command
	 */
	private Thread typematicThread = new Thread() {
		public void run() {
			while(true) {
				// Watch for key press that is char. (yield) If so, continue until released
				// or new key pressed (doesn't matter which key, all interrupt typematic).
				while(lastKeyPress == NOT_ASCII_CHAR) {Thread.yield();}
				oldChar = lastKeyPress;
				Delay.msDelay(typematicDelay);
				
				while((lastKeyPress != NOT_ASCII_CHAR)&(oldChar == lastKeyPress)) {
					KeyEvent ke = new KeyEvent(kb, KeyEvent.KEY_TYPED, System.currentTimeMillis(), modifiers, 0, lastKeyPress, 0);
					notifyListeners(ke);
					Delay.msDelay(ratePause);
				}
			}
		}
	};
	
	/**
	 * Table converts the scan code into a Java VK constant (see KeyEvent constants).
	 * Index is the scancode, value is the Java constant  
	 * 64 unique keys on Freedom Universal, many of these keys are not present but mapped for future compatibility
	 * The Fn key is not standard, so it is mapped to VK_META.
	 * TODO: Maybe Fn should not register at all as keypress? Instead, it outputs Esc, Pg_up, etc...
	 * as though they are key presses? Overrides actual key?
	 */
	/* DEVNOTES:
	 * http://www.computer-engineering.org/ps2keyboard/scancodes2.html
	 * 
	 * F7 has a scancode of 0x83 which pushed this array much larger, KeyEvent.VK_ALT_GRAPH is an int. 
	 * TODO: Use if-then later for high index values? (lot of zeros near end) like F7, SCROLL, PAGE UP, KP *.
	 * Didn't know how to handle multiple scan codes for print screen, pause. Also, keypad values interfere with multiple scancodes for page up, etc...
	 *  note: VK_ALT is at 0x10 and 0x11 because Universal uses 0x10 for some reason. 
	 */
	 private static byte [] scanCodes = {
		0,KeyEvent.VK_F9,KeyEvent.VK_META,KeyEvent.VK_F5,KeyEvent.VK_F3,KeyEvent.VK_F1,KeyEvent.VK_F2, KeyEvent.VK_WINDOWS, // 0x00 - 0x07
		0,KeyEvent.VK_F10,KeyEvent.VK_F8,KeyEvent.VK_F6,KeyEvent.VK_F4,KeyEvent.VK_TAB,KeyEvent.VK_BACK_QUOTE,0, // 0x08 - 0F
		KeyEvent.VK_ALT,KeyEvent.VK_ALT,KeyEvent.VK_SHIFT,KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_CONTROL,KeyEvent.VK_Q,KeyEvent.VK_1,0, // 0x10 - 0x17
		0,0,KeyEvent.VK_Z,KeyEvent.VK_S,KeyEvent.VK_A,KeyEvent.VK_W,KeyEvent.VK_2, 0, // 0x18 - 0x1F
		0,KeyEvent.VK_C,KeyEvent.VK_X,KeyEvent.VK_D,KeyEvent.VK_E,KeyEvent.VK_4,KeyEvent.VK_3,0, // 0x20 - 0x27
		KeyEvent.VK_UP,KeyEvent.VK_SPACE,KeyEvent.VK_V,KeyEvent.VK_F,KeyEvent.VK_T,KeyEvent.VK_R,KeyEvent.VK_5,KeyEvent.VK_RIGHT, // 0x28 - 0x2F
		0,KeyEvent.VK_N,KeyEvent.VK_B,KeyEvent.VK_H,KeyEvent.VK_G,KeyEvent.VK_Y,KeyEvent.VK_6,0, // 0x30 - 0x37
		0,0,KeyEvent.VK_M,KeyEvent.VK_J,KeyEvent.VK_U,KeyEvent.VK_7,KeyEvent.VK_8,0, // 0x38 - 0x3F
		KeyEvent.VK_UP, KeyEvent.VK_COMMA,KeyEvent.VK_K,KeyEvent.VK_I,KeyEvent.VK_O,KeyEvent.VK_0,KeyEvent.VK_9,0, // 0x40 - 0x47
		0,KeyEvent.VK_PERIOD,KeyEvent.VK_SLASH,KeyEvent.VK_L,KeyEvent.VK_SEMICOLON,KeyEvent.VK_P,KeyEvent.VK_MINUS,0, // 0x48 - 0x4F
		0,0,KeyEvent.VK_QUOTE,0,KeyEvent.VK_OPEN_BRACKET,KeyEvent.VK_EQUALS,0,0, // 0x50 - 0x57
		KeyEvent.VK_CAPS_LOCK,KeyEvent.VK_SHIFT,KeyEvent.VK_ENTER,KeyEvent.VK_CLOSE_BRACKET,KeyEvent.VK_SPACE,KeyEvent.VK_BACK_SLASH,KeyEvent.VK_LEFT,0, // 0x58 - 0x5F
		KeyEvent.VK_DOWN,0,0,0,0,0,KeyEvent.VK_BACK_SPACE,0, // 0x60 - 0x67
		0,KeyEvent.VK_END,0,KeyEvent.VK_LEFT,KeyEvent.VK_HOME,0,0,0, // 0x68 - 0x6F
		KeyEvent.VK_INSERT,KeyEvent.VK_DELETE,KeyEvent.VK_DOWN,0,KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_ESCAPE, KeyEvent.VK_NUM_LOCK, // 0x70 - 0x77
		KeyEvent.VK_F11, KeyEvent.VK_PLUS, KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_SUBTRACT, KeyEvent.VK_MULTIPLY // 0x78 - 0x7C
	}; 
	
	private DiscoveryAgent da;
	private RemoteDevice btDevice = null;
	private boolean doneInq = false;
	// I think this indicates the BT device is a SPP device
	private static final int SPP_DEVICE = 0x1F00;
	 
	
	/**
	 * Creates a new Keyboard instance using streams from the keyboard. Doesn't matter what the source is (Bluetooth, I2C, etc...)
	 * @param in
	 * @param out
	 */
	public Keyboard(InputStream in, OutputStream out) {
		setup(in, out);
	}

	/**
	 * Helper method used by both constructors.
	 * @param in
	 * @param out
	 */
	private void setup(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
		this.setDaemon(true);
		this.start();
		
		// TODO: Perhaps check if keyboard is a Freedom Universal (buggy) first before switching these?
		// Freedom Universal has a bug that switches DELETE and BACKSPACE values.
		// Switching back in scanCodes array (above):
		scanCodes[0x71] = KeyEvent.VK_BACK_SPACE;
		scanCodes[0x66] = KeyEvent.VK_DELETE;
		
		// Start typematic thread here:
		typematicThread.setDaemon(true);
		typematicThread.start();
	}
	
	/**
	 * Makes Bluetooth connection to SPP keyboard. The keyboard must have been paired already at 
	 * the main menu. Will connect to the first paired SPP keyboard device that is turned on.
	 * NOTE: It can't distinguish between a GPS or Keyboard so make sure only the keyboard is on.
	 * @throws BluetoothStateException If it doesn't find an SPP device to connect to.
	 */
	// TODO: This constructor should throw exceptions, not catch them below!
	public Keyboard() throws BluetoothStateException {
		/* Developer Notes:
		 * The code in here is copied from BTLocationProvider. If that gets updated, should do same here and vice versa.
		 */
		
		/** Inner class DiscoveryListener:
		 * 
		 */
		DiscoveryListener dl = new DiscoveryListener() {

			/* DiscoveryListener methods: */
			public void deviceDiscovered(RemoteDevice btdev, DeviceClass cod) {
				//System.err.println(btdev.getFriendlyName(false) + " discovered.");
				
				if((cod.getMajorDeviceClass() & SPP_DEVICE) == SPP_DEVICE) {
					if(btdev.isAuthenticated()) { // Check if paired.
						btDevice = btdev;
						da.cancelInquiry(this);
					}
				}	
			}

			public void inquiryCompleted(int discType) {
				doneInq = true;
			}
		};
		
		da = LocalDevice.getLocalDevice().getDiscoveryAgent();
		da.startInquiry(DiscoveryAgent.GIAC, dl);
		
		while(!doneInq) {Thread.yield();}
		
		// TODO: What is the procedure if it fails to connect? Return? Throw BT exception?
		if(btDevice == null) throw new BluetoothStateException("Nothing found.");
		
		String address = btDevice.getBluetoothAddress();
		String btaddy = "btspp://" + address;
		
		try {
			StreamConnectionNotifier scn = (StreamConnectionNotifier)Connector.open(btaddy);
			
			if(scn == null)	throw new BluetoothStateException("Failed to connect.");
			StreamConnection c = scn.acceptAndOpen();
			
			// This problem below occurred one time for my Holux GPS. The solution was to
			// remove the device from the Bluetooth menu, then find and pair again.
			// Need to throw exception with message.
			if(c == null) throw new BluetoothStateException("Failed. Try pairing your device again.");
			InputStream in = c.openInputStream();
			OutputStream out = c.openOutputStream();
			setup(in, out);
			
			// c.close(); // TODO: Clean up when done. HOW TO HANDLE IN LOCATION?
			
		} catch(IOException e) {
			throw new BluetoothStateException("Failed to retrieve data streams.");	
		}
	}
	
	/**
	 * Typematic delay is the time after a key is held down that characters start repeating.
	 *
	 * @param delay 250 ms to 1000 ms (500ms default)
	 */
	 
	public void setTypematicDelay(int delay) {
		this.typematicDelay = delay;
	}
	
	/**
	 * Typematic delay is the time after a key is held down that characters start repeating.
	 * @return delay in milliseconds
	 */
	public int getTypematicDelay() {
		return this.typematicDelay;
	}
	
	/**
	 * Typematic rate is the rate characters repeat when a key is held down.
	 * @param rate 2.0 cps (characters per second) to 30.0 cps (10.9 default)
	 */
	public void setTypematicRate(int rate) {
		this.typematicRate = rate;
		this.ratePause =  (int)(1000.0/typematicRate);
	}
	
	/**
	 * Typematic rate is the rate characters repeat when a key is held down.
	 * @return Rate in characters per second (cps)
	 */
	public double getTypematicRate() {
		return this.typematicRate;
	}
	
	/**
	 * Starts a KeyListener listening for events from the keyboard. Only one KeyListener is allowed.
	 *  
	 * @param kl
	 */
	// TODO: If we expand our javax.microedition.lcdui functionality we should add ability for more listeners.
	public void addKeyListener(KeyListener kl) {
		this.keyListener = kl;
	}
	
	/**
	 * Removes the specified KeyListener from the Keyboard so it will no longer notify the listener of new events.
	 * @param kl
	 */
	public void removeKeylistener(KeyListener kl) {
		if(this.keyListener == kl)
			this.keyListener = null;
	}
	
	/**
	 * Notify listener if present.
	 * @param e The key event to send out
	 */
	private void notifyListeners(KeyEvent e) {
		if(keyListener == null) return;
		
		if(e.getID() == KeyEvent.KEY_PRESSED)
			keyListener.keyPressed(e);
		else if(e.getID() == KeyEvent.KEY_RELEASED)
			keyListener.keyReleased(e);
		else if(e.getID() == KeyEvent.KEY_TYPED)
			keyListener.keyTyped(e);
	}
	
	public void run() {
		
		long previousEcho = System.currentTimeMillis();
		// TODO: Perhaps While connected is better. Then if disconnects, it reconnects and starts thread again?
		while(true) {
			
			// Keep-alive code: 
			long now = System.currentTimeMillis();
			if(now - previousEcho >= KEEP_ALIVE_DELAY) {
				try {
					out.write(ECHO);
					out.flush();
				} catch(IOException e) {
					// TODO: Thread could also reconnect if disconnected. Try powering off and on.
					System.err.println("COMMAND EXCEPTION");
				}
				previousEcho = now;
			}

			// Notifier code:
			try {
				if(in.available() > 0) { // Check if byte available.
					int bval = in.read();
					// System.err.println("scancode: " + bval);
					
					KeyEvent e = getKeyEvent(bval, System.currentTimeMillis());
					if(e.getID() == KeyEvent.KEY_PRESSED) {
						oldChar = NOT_ASCII_CHAR; // Prevents repetition if key pressed again.
						lastKeyPress = e.getKeyChar();
					} else if(e.getID() == KeyEvent.KEY_RELEASED) {
						lastKeyPress = NOT_ASCII_CHAR;
					}
					notifyListeners(e);
					
					// Generate KEY_TYPED event:
					if((e.getID() == KeyEvent.KEY_PRESSED) & (e.getKeyChar() != NOT_ASCII_CHAR)) { 
						KeyEvent ke = new KeyEvent(this, KeyEvent.KEY_TYPED, e.getWhen(), modifiers, 0, e.getKeyChar(), 0);
						notifyListeners(ke);
					}
				}
			} catch(IOException e) {/*Debug.out("EXCEPTION");*/}
			Thread.yield();
		}
	}
	
	/**
	 * Helper method to generate keyEvent from scan code from keyboard 
	 * @return
	 */
	private KeyEvent getKeyEvent(int scanCode, long timeStamp) {
		
		short id = KeyEvent.KEY_PRESSED;
		byte normalizedScanCode = (byte)scanCode;
		
		if((byte)scanCode < 0) { // if 8th bit on (i.e. key release)
			normalizedScanCode = (byte)(scanCode - 128); // remove 8th bit
			id = KeyEvent.KEY_RELEASED;
		}
		
		int code = getJavaConstant(normalizedScanCode);
		
		// Recalculate modifier
		recalculateModifier(code, id);
		
		// Handle Caps Lock pressed: Possibly beep here?
		if((code == KeyEvent.VK_CAPS_LOCK) & (id == KeyEvent.KEY_PRESSED)) capsLock = !capsLock;
		
		
		// Calculate location.
		int location = getLocation(normalizedScanCode);
		
		// Get ASCII character
		char curChar = getAsciiChar(code);
		
		return  new KeyEvent(this, id, timeStamp, modifiers, code, curChar, location);
	}
	
	private int getLocation(int scanCode) {
		int location = KeyEvent.KEY_LOCATION_STANDARD;
		
		switch(scanCode) {
		case 0x12: // L shift
		case 0x14: // L ctrl
		case 0x1F: // L gui (2 codes in real keyboard)
		case 0x11: // L alt (0x11 on Standard keyboard)
		case 0x10: // L alt (0x10 on Universal)
			location = KeyEvent.KEY_LOCATION_LEFT;
			break;
		case 0x59: // R shift
		case 0x13: // R alt gr (UK keyboards, incl. Freedom Universal) 
		//case 0x14: // R ctrl (2 codes on real keyboard)
		//case 0x27: // R gui (2 codes on real keyboard)
		//case 0x11: // R alt (2 codes on real keyboard)
			location = KeyEvent.KEY_LOCATION_RIGHT;
			break;
		}
		
		// TODO: Didn't implement KeyPad location. Not present on Freedom Universal keyboard.
		
		return location;
	}
	
	/**
	 * Indicates whether or not caps lock is enabled for the keyboard. The only way to change caps lock is to presss the <code>Caps Lock</code> key.
	 * @return true if caps lock is on, false if it is off.
	 */
	public boolean isCapsLock() {
		return capsLock;
	}
	
	/**
	 * Converts a Java constant into the proper ASCII character. For the most part, this method attempts to save memory by 
	 * using the Java constant (code) to calculate the proper character. Sometimes it is a direct conversion by casting into 
	 * a char, other times it needs a direct conversion to a different ASCII code.
	 * @param code
	 * @return
	 */
	private char getAsciiChar(int code) {
		
		int ascii = code;
		boolean shifted = (modifiers & KeyEvent.SHIFT_MASK) == KeyEvent.SHIFT_MASK;
		
		// Letter keys: 
		if(code >= KeyEvent.VK_A & code <= KeyEvent.VK_Z) {
			boolean capitalize = isCapsLock() ^ shifted; 
			if(!capitalize) ascii += 32; // convert to lower case
			return (char)ascii;
		} 
		
		// Number keys:
		else if(code >= KeyEvent.VK_0 & code <= KeyEvent.VK_9) { // TODO: Redundant if you are using switch below?
			// Shift
			if(shifted) {
				switch (code) {
		        	case KeyEvent.VK_1:
		        	case KeyEvent.VK_3:
		        	case KeyEvent.VK_4:
		        	case KeyEvent.VK_5:
		        		ascii -= 16;
		        		break;
		        	case KeyEvent.VK_7:
		        	case KeyEvent.VK_9:
		        		ascii -= 17;
		        		break;
		        	case KeyEvent.VK_2:
		        		ascii += 14;
		        		break;
		        	case KeyEvent.VK_6:
		        		ascii += 40;
		        		break;
		        	case KeyEvent.VK_8:
		        		ascii -= 14;
		        		break;
		        	case KeyEvent.VK_0:
		        		ascii -= 7;
		        		break;
				}	
			}
			return (char)ascii;
		}
		
		// Various characters:
		// TODO: I'm not sure if all this branching logical code saves over a simple table array.
		// Direct ASCII translation: ; - = [ ] \ ; , . / NOT: ` '
		int shiftModifier = 0;
		switch (code) {
		case KeyEvent.VK_SPACE:
			return (char)ascii;
		case KeyEvent.VK_MINUS:
    		shiftModifier = (shifted?50:0);
    		return(char)(ascii + shiftModifier);
    	case KeyEvent.VK_EQUALS:
    		shiftModifier = (shifted?-18:0);
    		return(char)(ascii + shiftModifier);
    	case KeyEvent.VK_OPEN_BRACKET:
    	case KeyEvent.VK_CLOSE_BRACKET:
    	case KeyEvent.VK_BACK_SLASH:
    		shiftModifier = (shifted?32:0);
    		return(char)(ascii + shiftModifier);
    	case KeyEvent.VK_SEMICOLON:
    		shiftModifier = (shifted?-1:0);
    		return(char)(ascii + shiftModifier);
    	case KeyEvent.VK_QUOTE:
    		ascii = 39; // Correct ascii value for '
    		shiftModifier = (shifted?-5:0);
    		return(char)(ascii + shiftModifier);
    	case KeyEvent.VK_COMMA:
    	case KeyEvent.VK_PERIOD:
    	case KeyEvent.VK_SLASH:
    		shiftModifier = (shifted?16:0);
    		return(char)(ascii + shiftModifier);
    	case KeyEvent.VK_BACK_QUOTE: // TODO: This char is suspicious on leJOS. ` or ~ don't show up right. Test raw values for char.
    		ascii = 96; // Correct ascii value for `
    		shiftModifier = (shifted?30:0);
    		return(char)(ascii + shiftModifier);
		}
		
		// Handle Enter, Tab, Backspace
		switch (code) {
	    	case KeyEvent.VK_ENTER:
	    	case KeyEvent.VK_TAB:
	    	case KeyEvent.VK_BACK_SPACE:
	    		return(char)ascii;
		}
		
		// The TYPED char should only appear if valid. Ctrl, Shift etc.. do NOT produce TYPED events.
		return NOT_ASCII_CHAR;
	}
	
	private int getJavaConstant(byte scanCode) {
		
		// Code to check if Fn held down, and if Esc, Pg Up, Pg Down, Home, End pressed.
		if((modifiers & KeyEvent.META_MASK) == KeyEvent.META_MASK) {
			switch(scanCodes[scanCode]) {
			case KeyEvent.VK_BACK_QUOTE:
				return KeyEvent.VK_ESCAPE;
			case KeyEvent.VK_UP:
				return KeyEvent.VK_PAGE_UP;
			case KeyEvent.VK_DOWN:
				return KeyEvent.VK_PAGE_DOWN;
			case KeyEvent.VK_LEFT:
				return KeyEvent.VK_HOME;
			case KeyEvent.VK_RIGHT:
				return KeyEvent.VK_END;
			}
		}
			
		return scanCodes[scanCode];
	}
	
	/**
	 * Determines which modifier keys are held down.
	 * @param code
	 * @param id
	 */
	private void recalculateModifier(int code, short id) {
		int val = 0;
		switch (code) {
        	case KeyEvent.VK_SHIFT:
        		val = KeyEvent.SHIFT_MASK;
        		break;
        	case KeyEvent.VK_CONTROL:
        		val = KeyEvent.CTRL_MASK;
        		break;
        	case KeyEvent.VK_META:
        		val = KeyEvent.META_MASK;
        		break;
        	case KeyEvent.VK_ALT:
        		val = KeyEvent.ALT_MASK;
        		break;
        	case KeyEvent.VK_ALT_GRAPH:
        		val = KeyEvent.ALT_GRAPH_MASK;
        		break;			
		}
		if(id == KeyEvent.KEY_PRESSED) {
			// ADD new modifier
			modifiers += val;
		} else if(id == KeyEvent.KEY_RELEASED) {
			// SUBTRACT new modifier
			modifiers -= val;
		}
	}
}
