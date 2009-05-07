package lejos.keyboard;

import java.io.*;

import lejos.nxt.Button;

/*
 * Developer Notes:
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
 * <p>Note: This class is currently only tested with Freedom Universal</p>
 * 
 */

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
	
	int modifiers; // keeps track of modifier keys held down (shift, alt, etc)
	
	// Use Vector? Resizable array?
	private KeyListener keyListener = null;
		
	/**
	 * Table converts the scan code into a Java VK constant (see KeyEvent constants).
	 * Index is the scancode, value is the Java constant  
	 * 64 unique keys on Freedom Universal, many of these keys are not present but mapped for future compatibility
	 */
	/* DEVNOTES: Unable to figure out constants for the keys "L GUI E0, 1F", "R GUI E0, 27", "APPS E0, 2F" See:
	 * http://www.computer-engineering.org/ps2keyboard/scancodes2.html
	 * VK_BACK_QUOTE blew the size to 222 (short). Perhaps I can get away with if-then in method in getJavaConstant()? (Doubles memory usage by ~128 bytes otherwise.)
	 * TODO: Change the constants in KeyEvent to something smaller. I think they are arbitrary. Some are short and even int.
	 * 
	 * F7 has a scancode of 0x83 which pushed this array much larger, KeyEvent.VK_ALT_GRAPH is an int. 
	 * TODO: Use if-then later in method for large vals and high index values (lot of zeros near end) like F7, SCROLL, PAGE UP, KP *.
	 * Didn't know how to handle multiple scan codes for print screen, pause. Also, keypad values interfere with multiple scancodes for page up, etc...
	 *  
	 */
	 private static short [] scanCodes = {
		0,KeyEvent.VK_F9,0,KeyEvent.VK_F5,KeyEvent.VK_F3,KeyEvent.VK_F1,KeyEvent.VK_F2, KeyEvent.VK_WINDOWS, // 0x00 - 0x07
		0,KeyEvent.VK_F10,KeyEvent.VK_F8,KeyEvent.VK_F6,KeyEvent.VK_F4,KeyEvent.VK_TAB,KeyEvent.VK_BACK_QUOTE,0, // 0x08 - 0F
		KeyEvent.VK_ALT,KeyEvent.VK_ALT,KeyEvent.VK_SHIFT,KeyEvent.VK_ALT, KeyEvent.VK_CONTROL,KeyEvent.VK_Q,KeyEvent.VK_1,0, // 0x10 - 0x17
		0,0,KeyEvent.VK_Z,KeyEvent.VK_S,KeyEvent.VK_A,KeyEvent.VK_W,KeyEvent.VK_2, 0, // 0x18 - 0x1F
		0,KeyEvent.VK_C,KeyEvent.VK_X,KeyEvent.VK_D,KeyEvent.VK_E,KeyEvent.VK_4,KeyEvent.VK_3,0, // 0x20 - 0x27
		0,KeyEvent.VK_SPACE,KeyEvent.VK_V,KeyEvent.VK_F,KeyEvent.VK_T,KeyEvent.VK_R,KeyEvent.VK_5,KeyEvent.VK_RIGHT, // 0x28 - 0x2F
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
	
	public Keyboard(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
		this.setDaemon(true);
		this.start();
		
		// TODO: Perhaps test if keyboard is Freedom (mine has a Bug) and then switch DELETE and BACKSPACE values in array above.
	}
	
	// TODO: methods setTypematicDelay, setTypematicRate() - 0 for no repeating?
	
	/**
	 * Starts a KeyListener listening for events from the keyboard. Only one KeyListener is allowed.
	 *  
	 * @param kl
	 */
	// TODO: If we expand our javax.microedition.lcdui functionality we should add ability for more listeners.
	public void addKeyListener(KeyListener kl) {
		this.keyListener = kl;
	}
	
	public void removeKeylistener(KeyListener l) {
		this.keyListener = null;
	}
	
	private void notifyListeners(KeyEvent e) {
		if(keyListener == null) return;
		
		// TODO: Notify other listeners in collection if present.
		if(e.getID() == KeyEvent.KEY_PRESSED)
			keyListener.keyPressed(e);
		else if(e.getID() == KeyEvent.KEY_RELEASED)
			keyListener.keyReleased(e);
		else if(e.getID() == KeyEvent.KEY_TYPED)
			keyListener.keyTyped(e);
	}
	
	public void run() {
		
		int previousEcho = (int)System.currentTimeMillis();
		// TODO: Perhaps While connected is better. Then if disconnects, it reconnects and starts thread again?
		while(true) {
			
			// Keep-alive code: TODO Use long instead.
			int now = (int)System.currentTimeMillis();
			if(now - previousEcho >= KEEP_ALIVE_DELAY) {
				try {
					//Debug.out("Echo Sent\n");
					out.write(ECHO);
					out.flush();
				} catch(IOException e) {
					// TODO: Thread handles keep alive. Should it also reconnect if disconnected? Try powering off and on.
					System.err.println("COMMAND EXCEPTION");
				}
				previousEcho = now;
			}

			// Notifier code:
			try {
				if(in.available() > 0) { // Check if byte available.
					// TODO Not sure if this byte is two bytes with F0 value when key released, or one negative byte.
					int bval = in.read();
					
					// TODO: Fire appropriate events for PRESSED, RELEASED, TYPED.
					KeyEvent e = getKeyEvent(bval, System.currentTimeMillis());
					notifyListeners(e);
					
					// Generate KEY_TYPED:
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
		byte normCode = (byte)scanCode;
		
		// TODO: Delete? Might not be necessary to do this here. Plus, can subtract from scanCode since KEY_RELEASED already set.
		if((byte)scanCode < 0) { // if 8th bit on (i.e. key release)
			normCode = (byte)(scanCode - 128); // remove 8th bit TODO Is this correct?
			id = KeyEvent.KEY_RELEASED;
		}
		
		int code = getJavaConstant((byte)normCode);
		
		// Handle Caps Lock pressed:
		if((code == KeyEvent.VK_CAPS_LOCK) & (id == KeyEvent.KEY_PRESSED)) capsLock = !capsLock;
		
		// Recalculate modifier
		recalculateModifier(code, id);
		
		// TODO: Calculate location.
		int location = KeyEvent.KEY_LOCATION_STANDARD;
		
		// TODO: Both KEY_TYPED and KEY_PRESSED repeatedly fire the notify when key held down. Thread must do this somewhere.
		// NOTE: KEY_TYPED is a virtual key (e.g. '?') whereas KEY_PRESSED and RELEASED are merely physical key (e.g. '/') 
		// (For KEY_TYPED events, the keyCode is always VK_UNDEFINED. AND the location is always unknown)
		// No key typed events are generated for keys that don't generate Unicode characters (e.g. action keys, modifier keys, etc.). 
		char curChar = getAsciiChar(code);
		
		return  new KeyEvent(this, id, timeStamp, modifiers, code, curChar, location);
	}
	
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
	
	public static int getJavaConstant(byte scanCode) {
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
