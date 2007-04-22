package java.io;

import lejos.nxt.Flash;

public class FileInputStream extends InputStream{
	
	int pointer;
	int page_pointer;
	int data_pointer; // Current byte in buff array
	byte [] buff;
	File file;
	
	public FileInputStream(File f) {
		buff = new byte[File.BYTES_PER_PAGE];
		page_pointer = f.page_location;
		data_pointer = 0; // Start of page
		pointer = 0; // Overall mark
		file = f;
		Flash.readPage(buff, page_pointer);
	}
	
	public int read() throws IOException {
		// Check against file size for EOF.
		if(pointer >= file.file_length)
			return -1; // Indicates EOF
				
		int val = buff[data_pointer];
		data_pointer++;
		pointer++;
		if(data_pointer >= File.BYTES_PER_PAGE) {
			data_pointer = 0;
			page_pointer++;
			Flash.readPage(buff, page_pointer);
		}
		return val & 0xff; // Need to return 0-255 value
	}	
}