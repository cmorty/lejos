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
	 * @throws FileNotFoundException
	 */		
	public FileOutputStream(File f) throws FileNotFoundException 
	{
		this(f, false);
	}
	
/**
 * create a new OutputStream to write to this file
 * @param f  the file this stream writes to
 * @param append  if true this stream will start writing at the end of the file, otherwise at the beginning
 */	
	public FileOutputStream(File f, boolean append) throws FileNotFoundException {
        file = f;
		buff = new byte[Flash.BYTES_PER_PAGE];
		
		// make sure that file is re-created, so that the pages for the file
		// are allocated at the end of the filesystem.
		if (!append && file.exists())
			file.delete();

		// create file, in case it does not exist
		// Note: createNewFile does nothing, when file already exists.
		try
		{
			file.createNewFile();
		}
		catch (IOException e)
		{
			//exception chaning would be nice 
			throw new FileNotFoundException("file could not be created");
		}
		
		if(append)
		{
			page_pointer = file.page_location + file.file_length/Flash.BYTES_PER_PAGE ;
			data_pointer =  file.file_length%Flash.BYTES_PER_PAGE;
			Flash.readPage(buff, page_pointer);
		}
		else
		{
			page_pointer = file.page_location;
			data_pointer = 0; // Start of first page
			file.file_length = 0;// can this cause trouble?
		}
	}
	
/**
 * write 1 byte to the file; if necessary, file will be moved become the last file in memory
 */	
	@Override
	public void write(int b) throws IOException {
		if (buff == null)
			throw new IOException("stream is closed");
		
		buff[data_pointer] = (byte)b;
		data_pointer++;
		file.file_length++; 
		if(data_pointer >= Flash.BYTES_PER_PAGE) 
		{
			flush(); // Write to flash
			page_pointer++; // Move to next page
			data_pointer = 0;
		}
	}
	
	@Override
	public void flush() throws IOException {
		if (buff == null)
			throw new IOException("stream is closed");
		
		if(file.getIndex()< ( File.totalFiles -1)) 
		{
			int old = file.page_location;
			file.moveToTop();
			page_pointer += file.page_location - old; 					
		}
		Flash.writePage(buff, page_pointer);
    }
	
	/**
	 * Write the buffer to flash memory and update the file parameters in flash.
	 * Resets pointers, so file can be written again from beginning with the same output stream.
	 */	
	public void close() throws IOException {
		if (buff != null)
		{
			// !! Alternate implementation: If this is a new file, perhaps only 
			// write the file table information AFTER close() called so  
			// incomplete/partial files don't exist.
			flush();
			File.writeTable(File.listFiles()); // Updates file size for this file.
			
			buff = null;			
		}
	}
}
