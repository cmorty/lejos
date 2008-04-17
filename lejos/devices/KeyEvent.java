package lejos.devices;

public class KeyEvent {
	
	public static final int SHIFT_MASK = 1;
	public static final int CTRL_MASK = 2;
	public static final int META_MASK = 4;
	public static final int ALT_MASK = 8;
	public static final int ALT_GRAPH_MASK = 32;
	
	public static final int SHIFT_DOWN_MASK = 64;
	public static final int CTRL_DOWN_MASK = 128;
	public static final int META_DOWN_MASK = 256;
	public static final int ALT_DOWN_MASK = 512;
	public static final int ALT_GRAPH_DOWN_MASK = 8192;
	
	/**
	 * Keycode constant for the down arrow key
	 */
	public static final int VK_DOWN = 40;
	
	/**
	 * Keycode constant for the right arrow key
	 */
	public static final int VK_RIGHT = 39;
		
	/**
	 * Keycode constant for the up arrow key
	 */
	public static final int VK_UP = 38;
	
	/**
	 * Keycode constant for the left arrow key
	 */
	public static final int VK_LEFT = 37;
		
	public static short KEY_TYPED = 400;
	public static short KEY_PRESSED = 401;
	public static short KEY_RELEASED = 402;
	
	private Object source;
	private int when;
	private char keyChar;
	private short id;
	private int modifiers;
	private int keyCode;
	
	public KeyEvent(Object source, short id, int when, int modifiers, int keyCode, char keyChar) {
		this.when = when;
		this.keyChar = keyChar;
		this.id = id;
		this.source = source;
		this.modifiers = modifiers;
		this.keyCode = keyCode;
	}
	
	public char getKeyChar() {
		return this.keyChar;
	}
	
	/**
	 * Will actually only return key codes that don't have 
	 * ASCII/Unicode equivalents, such as arrow keys (e.g. VK_UP).
	 * Otherwise it just returns the ASCII value.
	 * @return
	 */
	public int getKeyCode() {
		return this.keyCode;
	}
	
	/**
	 * Not Implemented
	 * @return
	 */
	public int getKeyLocation() {
		return 0;
	}
	
	public int getID() {
		return this.id;
	}
	
	public boolean isShiftDown() {
		return false;
	}
	
	public boolean isControlDown() {
		return false;
	}
	
	public boolean isMetaDown() {
		return false;
	}
	
	public boolean isAltDown() {
		return false;
	}
	
	public boolean isAltGraphDown() {
		return false;
	}
	
	public int getWhen() {
		return this.when;
	}
	
	public int getModifiers() {
		return this.modifiers;
	}
	
	public void consume() {
		
	}
	
	public boolean isConsumed() {
		return false;
	}
	
	public Object getSource() {
		return this.source;
	}
}