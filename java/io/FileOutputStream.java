package java.io;

import lejos.nxt.Flash;

public class FileOutputStream extends OutputStream {

	int page_pointer;
	int data_pointer; // Current byte in buff array
	byte [] buff;
	
	public FileOutputStream(File f) {
		this(f, false);
	}
	
	// !! Haven't implemented append yet
	public FileOutputStream(File f, boolean append) {
		buff = new byte[File.PAGE_SIZE];
		page_pointer = f.page_location;
		data_pointer = 0; // Start of page
	}
	
	
	public void write(int b) throws IOException {
		buff[data_pointer] = (byte)b;
		data_pointer++;
		if(data_pointer >= File.PAGE_SIZE) {
			// Write to flash
			flush();
			// Move to next page
			page_pointer++;
			data_pointer = 0;
		}
	}
	
	public void flush() throws IOException {
		Flash.writePage(buff, page_pointer);
    }
	
	public void close() throws IOException {
		flush();
		// !! Rewrite total bytes in this file (if it was expanded
		// while writing) if it is > f.file_length
	}
}
