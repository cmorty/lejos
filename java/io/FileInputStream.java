package java.io;

import lejos.nxt.Flash;

public class FileInputStream extends InputStream{
	
	int page_pointer;
	int data_pointer; // Current byte in buff array
	byte [] buff;
	
	public FileInputStream(File f) {
		buff = new byte[File.PAGE_SIZE];
		page_pointer = f.page_location;
		data_pointer = 0; // Start of page
		Flash.readPage(buff, page_pointer);
	}
	
	public int read() throws IOException {
		int val = buff[data_pointer];
		data_pointer++;
		// !! Need to check against file size for EOF.
		// (how to do EOF?)
		if(data_pointer >= File.PAGE_SIZE) {
			data_pointer = 0;
			page_pointer++;
			Flash.readPage(buff, page_pointer);
		}
		return val;
	}	
}