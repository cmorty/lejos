package java.io;

import lejos.nxt.Flash;

public class FileOutputStream extends OutputStream {

	/**
	 * Current page this stream is writing to
	 */
	private int page_pointer;
	
	/**
	 * Current byte in *buffer* (buff below) it is writing to
	 */
	private int data_pointer;
	
	/**
	 * A buffer of the same size as a page of flash memory.
	 */
	private byte [] buff;
	
	/**
	 * File attached to this stream
	 */
	File file;
	
	public FileOutputStream(File f) {
		this(f, false);
	}
	
	public FileOutputStream(File f, boolean append) {
		// !! Haven't implemented append yet.
		// !! Will move page_pointer and data_pointer to end of file
		// when this is implemented.
		buff = new byte[File.BYTES_PER_PAGE];
		page_pointer = f.page_location;
		data_pointer = 0; // Start of first page
		file = f;
		// !! When defrag implemented, any FileStream that is open should be locked from defragging.
	}
	
	public void write(int b) throws IOException {
		if(file.page_location < 0) throw new IOException(); // "File has not been created!"
		buff[data_pointer] = (byte)b;
		data_pointer++;
		file.file_length++; // !! NOT CORRECT! Only if new file is made. If writing to existing file or append is used, needs to be different!
		if(data_pointer >= File.BYTES_PER_PAGE) {
			flush(); // Write to flash
			page_pointer++; // Move to next page
			data_pointer = 0;
		}
	}
	
	public void flush() throws IOException {
		Flash.writePage(buff, page_pointer);
    }
	
	public void close() throws IOException {
		// !! Alternate implementation: If this is a new file, perhaps only 
		// write the file table information AFTER close() called so  
		// incomplete/partial files don't exist.
		flush();
		File.writeTable(File.listFiles()); // Updates file size for this file.
	}
}
