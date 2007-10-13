package lejos.nxt.comm;

import java.io.*;

public class BTOutputStream extends OutputStream {
	private final int BUFFER_SIZE = 32;	
	private byte[] buffer = new byte[BUFFER_SIZE+2];
	private int numBytes = 0;
	
    public void write(int b) {
    	if (numBytes == BUFFER_SIZE) {
    		flush();
    	}
    	buffer[numBytes+2] = (byte) b;
    	numBytes++;  	
    }
    
	public void flush() {
		if (numBytes > 0) {
			buffer[0] = (byte) numBytes;
			buffer[1] = 0;
			Bluetooth.btSend(buffer,numBytes+2);
			numBytes = 0;
		}
	}
}
