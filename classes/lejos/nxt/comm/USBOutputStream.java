package lejos.nxt.comm;

import java.io.*;

public class USBOutputStream extends OutputStream {
	private final int BUFFER_SIZE = 32;	
	private byte[] buffer = new byte[BUFFER_SIZE];
	private int numBytes;
	
    public void write(int b) {
    	if (numBytes == BUFFER_SIZE) {
    		flush();
    	}
    	buffer[numBytes++] = (byte) b;  	
    }
    
	public void flush() {
		if (numBytes != 0) {
		  USB.usbWrite(buffer,numBytes);
		  numBytes = 0;
		}
	}
}
