package java.io;

import lejos.nxt.Flash;

/**
 * Reads a stream of bytes from a file.
 * 
 * @author Brian Bagnall
 *
 */
public class FileInputStream extends InputStream{
	
	int pointer;
	int page_pointer;
	int data_pointer; // Current byte in buff array
	byte [] buff;
	File file;
	
	public FileInputStream(File f) {
        file = f;
		buff = new byte[Flash.BYTES_PER_PAGE];
		page_pointer = file.page_location;
		data_pointer = 0; // Start of page
		pointer = 0; // Overall mark;
		Flash.readPage(buff, page_pointer);
	}
	
	public int available() throws IOException {
        return file.file_length - pointer;
    }

    public int read() throws IOException {
		// Check against file size for EOF.
		if(this.available() <= 0)
			return -1; // Indicates EOF
				
		int val = buff[data_pointer];
		data_pointer++;
		pointer++;
		if(data_pointer >= Flash.BYTES_PER_PAGE) {
			data_pointer = 0;
			page_pointer++;
			Flash.readPage(buff, page_pointer);
		}
		return val & 0xff; // Need to return 0-255 value
	}	
    
    /**
     * resets pointers so next read() is from the start of the file;
     */
    public void reset()
    {
        page_pointer = file.page_location;
        data_pointer = 0; // Start of page
        pointer = 0; 
        Flash.readPage(buff, page_pointer);
    }
}