package java.io;

import lejos.nxt.Flash;

/**
 * Writes a stream of bytes to a file.
 * 
 * @author Brian Bagnall
 *
 */
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
	
	/**
	 * create a new OutputStream to write to this file, starting  at the beginning of the file.
	 * @param f  the file this stream writes to
	 */		
	public FileOutputStream(File f) 
	{
		this(f, false);
	}
	
/**
 * create a new OutputStream to write to this file
 * @param f  the file this stream writes to
 * @param append  if true this stream will start writing at the end of the file, otherwise at the beginning
 */	
	public FileOutputStream(File f, boolean append) {
        file = f;
		buff = new byte[Flash.BYTES_PER_PAGE];
		page_pointer = file.page_location;
		data_pointer = 0; // Start of first page

		if(append)
		{
			page_pointer = file.page_location + file.file_length/Flash.BYTES_PER_PAGE ;
			data_pointer =  file.file_length%Flash.BYTES_PER_PAGE;
			Flash.readPage(buff, page_pointer);
		}
		else file.file_length = 0;// can this cause trouble?
	}
	
/**
 * write 1 byte to the file; if necessary, file will be moved become the last file in memory
 */	
	public void write(int b) throws IOException {
		if(file.page_location < 0) throw new IOException(); // "File has not been created!"
		buff[data_pointer] = (byte)b;
		data_pointer++;
		file.file_length++; 
		if(data_pointer >= Flash.BYTES_PER_PAGE) 
		{
			if(file.getIndex()< ( File.totalFiles -1)) 
				{
				file.moveToTop();
				page_pointer = file.page_location + file.file_length/Flash.BYTES_PER_PAGE; 					
				}
			flush(); // Write to flash
			page_pointer++; // Move to next page
			data_pointer = 0;
		}
	}
	
	public void flush() throws IOException {
		Flash.writePage(buff, page_pointer);
    }
	
	/**
	 * Write the buffer to flash memory and update the file parameters in flash.
	 * Resets pointers, so file can be written again from beginning with the same output stream.
	 */	
	public void close() throws IOException {
		// !! Alternate implementation: If this is a new file, perhaps only 
		// write the file table information AFTER close() called so  
		// incomplete/partial files don't exist.
		flush();
		File.writeTable(File.listFiles()); // Updates file size for this file.
        page_pointer = file.page_location;
        data_pointer = 0; // Start of first page
	}
}
