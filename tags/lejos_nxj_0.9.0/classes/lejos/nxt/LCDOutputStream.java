package lejos.nxt;

import java.io.*;

/**
 * A simple output stream that implements console output.
 * It writes to the bottom line of the screen, scrolling the
 * LCD up one line when writing to character position 0, 
 * and starting a new line when the position reaches 16
 * or a new line character is wriiten. 
 * 
 * Used by System.out.println.
 * 
 * @author Lawrie Griffiths
 *
 */
public class LCDOutputStream extends OutputStream {
	private int pos = 0;
	
	public void write(int c) {	
		if (c == '\n') {
			pos = 0;
			return;
		}	
		if (pos >= LCD.DISPLAY_CHAR_WIDTH) pos = 0;
		if (pos == 0) LCD.scroll();
		LCD.drawChar((char) c, pos++, LCD.DISPLAY_CHAR_DEPTH - 1);
	}
}
