package java.io;

import lejos.nxt.Flash;
import java.io.IOException;

public class File {
	//static int count; Used for anything?
	// CONSTANTS:
	/**
	 *  Number of files the file system can store. 
	 *  Defines the size of the files array. If leJOS gets a garbage
	 *  collector we can get rid of this limitation.
	 */
	public static final byte MAX_FILES = 30;
	
	/**
	 * Maximum size of file name. Used because no garbage collector.
	 * If leJOS gets a garbage collector we can recode this. This value
	 * is used to define the character array charBuff[] below.
	 */
	private static final byte MAX_FILENAME = 30;
	
	/**
	 * Maximum number of pages available to user flash memory.
	 * Change this if the start of user flash memory in the
	 * firmware changes.
	 * 
	 */
	private static final int MAX_USER_PAGES = 704;
	
	/**
	 * Signature written to the front of the file table to indicate if the
	 * flash memory contains file table information. By changing this
	 * version number/string, the users file system will reformat automatically.
	 * (i.e. Restarting file system and erasing their current stored classes) 
	 */
	private static final String TABLE_ID = "V_0.2";  
	
	/**
	 * Indicates the starting page of the file table.
	 */
	private static byte TABLE_START_PAGE = 0;
	
	/**
	 *  Number of pages reserved for storing file table information.
	 *  If we want to allow more files to be stored in system, increase
	 *  this number. (!! File table data currently only writes to page 0.)
	 */
	private static byte FILE_TABLE_PAGES = 2;
	
	/**
	 * First page for storing *file data*.
	 */
	private static byte FILE_START_PAGE = (byte)(TABLE_START_PAGE + FILE_TABLE_PAGES); 
	
	/**
	 * The position (order of bytes) where the number of files
	 * is stored in the table. 
	 */
	private static byte NUM_FILES_POS = (byte)TABLE_ID.length();
		
	/**
	 * Indicates the # of bytes per page in the Flash class.
	 * Lawrie Griffiths determines this. Might want to access this
	 * directly from Flash in future from a package level constant in case
	 * we want to alter this number.
	 */
	protected static short BYTES_PER_PAGE = 256;
	
	// GLOBAL STATIC CLASS VARIABLES: 
	/**
	 * Shared buffer. Using this as static class variable because leJOS
	 * lacks a garbage collector.
	 */
	private static byte [] buff = new byte[BYTES_PER_PAGE];
	
	/**
	 * Array containing all the Files in the directory. 
	 */
	 static File [] files = null;
	
	/**
	 * The total number of files in the file system. A negative value 
	 * indicates this variable has not been initialized. Using byte, but
	 * if we expand past the 30 limit (garbage collector) we can use short.
	 */
	public static byte totalFiles = -1;
	
	/**
	 *  Temp buffer of characters used to read file names. If leJOS gets a 
	 *  garbage collector we can eliminate this. Used in readTable()
	 */
	private static char [] charBuff = new char[MAX_FILENAME];
	
	
	// INSTANCE VARIABLES (file name, page location of file, file size, exists):
	/**
	 * The name of the file. Initialized in File constructor.
	 */
	private String file_name;
	
	/**
	 * The starting page location of this file. All files start at the 0 byte
	 * position of the page that they start at.
	 * Init to -1 to indicate not initialized or does that waste memory?
	 */
	short page_location = -1; // !! Make protected when done tests?
	
	/**
	 * The length, in bytes, of this file according to the file table.
	 * A file that does not exists is supposed to equal 0. i.e. The Java SDK
	 * says that it doesn't get written to the file table until it has bytes.
	 */
	int file_length; // 0 when not created yet
	
	/**
	 * Indicates if the file exists as an entry in the file table.
	 */
	boolean exists = false;
	
	/**
	 * Creates a new File object. If this file exists on disk it will
	 * represent that file. If the file does not exist, you will need to
	 * use createNewFile() before writing to the file.
	 * @param name
	 */
	public File(String name) {
		this(name, true);
	}
	
	/**
	 * A private constructor with the option to check if the file_name already 
	 * exists against the files in the file table. Needed this method because
	 * the readTable() method created an array of new File objects (hence had
	 * to call the constructor) but the file list wasn't ready yet so it made
	 * no sense to check the list. 
	 * @param name File name
	 * @param checkExists If true, checks filename against list of files.
	 */
	private File(String name, boolean checkExists) {
		if(!File.tableExists()) File.format();
		this.file_name = name;
		
		if(files == null) {
			 files = new File[MAX_FILES];
			 readTable(files); // Update file data
		}
		
		// Check through file system to see if file with same name exists.
		if(checkExists) {
			for(byte i=0;i<File.totalFiles;i++) {
				if(files[i].file_name.equals(this.file_name)) {
					this.file_length = files[i].file_length;
					this.page_location = files[i].page_location;
					this.exists = true;
					files[i] = this; // Substitute this object in actual array so it remains synchronized.
				}
			}
		} else
			this.exists = true; // If not checking if it exists, means this was made from readTable, therefore it exists for sure.
	}
	
	/**
	 * Deletes the file represented by this File object.
	 * @return true if the file is successfully deleted; false otherwise
	 */
	public boolean delete() {
		if(!exists()) return false; // Check if file is in file table.
		// 1. Find where this object is in the files array:
		byte index = -1;
		for(byte i=0;i<File.totalFiles;i++) {
			if(files[i].file_name.equals(this.file_name)) index = i;
		}
		// 2. Update File.totalFiles:
		--File.totalFiles; // One less file
		// 3. If any files remain after this, shuffle them down.
		if(files[index + 1] != null) { // Make sure there are files left after this in array
			// Shuffle array File objects down in array to fill space 
			for(;index<=File.totalFiles;index++) {
				files[index] = files[index + 1]; // This should also set last file to null
			} 
		} else 
			files[index] = null;
		// 3. writeTable() to update table data.
		try { // Impossible for IOException when deleting, therefore catch here.
			File.writeTable(files);
		} catch (IOException e) {}
		// 4. Make this file.exists = false;
		this.exists = false;
		this.file_length = 0;
		try { // Impossible for IOException when deleting, therefore catch here.
			File.defrag();
		} catch (IOException e){}
		return true;
	}

	/**
	 * If the file is a binary executable, begins running it.
	 *
	 */
	public void exec() {
		Flash.exec(page_location, file_length);
	}
	
	/**
	 *  Returns a list of files in the flash file system. Because there are no
	 *  directories, this is a static method in leJOS NXJ. The order of the files
	 *  in the array goes from oldest (0) to newest (highest index array).
	 *  
	 *  @return An array of File objects representing files in the file system.
	 *  The array will be empty if the directory is empty.
	 *  
	 * NOTE: In the Java SDK this method should return an array
	 * of size equaling the number of files. However, because leJOS has no garbage
	 * collector it returns the same array that is always 30 in length. The unused
	 * file spots are null. Use File.totalFiles to determine number of files. 
	 */
	public static File [] listFiles() {
		if(files == null) {
			 files = new File[MAX_FILES];
			 File.readTable(files); // Update file data
		}
		return files;
	}
	
	/**
	 * Returns the name of the file.
	 * @return The name of the file, including the file extension. e.g. "mapdata.txt"
	 * 
	 */
	public String getName() {
		return file_name;
	}
	
	/**
	 * Returns the length of the file denoted by this file name.
	 * @return The length, in bytes, of the file denoted by this file name, or 0 if the file does not exist.
	 */
	public int length() {
		return file_length;
	}
	
	/**
	 * Indicates if the file exists in the flash memory.
	 * @return True indicates the file exists, false means it has not been created.
	 */
	public boolean exists() {
		return exists;
	}
	
	/**
	 * Reads the file information in the table from flash memory and
	 * stores the information in the array supplied. 
	 * @param files An array of File objects. When the method returns the
	 * array will contain File objects for all the files in flash. If a null
	 * File array is given, it will create a new File array.
	 */
	static void readTable(File [] files) {
		// Make sure flash has table id:
		if(!File.tableExists())	File.format();
		File.
		resetTablePointer();
		Flash.readPage(buff, TABLE_START_PAGE); // Kludge to fill data into first page
		// Move pointer to file total:
		byte_pointer = NUM_FILES_POS; // Kludge
		File.totalFiles = readNextByte(); // update total files value 
		
		for(int i=0;i<File.totalFiles;i++) {
			short pageLocation = (short)((0xFF & readNextByte()) | ((0xFF & readNextByte())<<8));
			int fileLength = (0xFF & readNextByte()) | ((0xFF & readNextByte()) <<8) | ((0xFF & readNextByte())<<16) | ((0xFF & readNextByte())<<24);
			
			// The following code attempts to reuse String's. If leJOS gets
			// a garbage collector we can create new strings and reduce this
			// code. It assumes that if files[i] is NOT null then the filename
			// is correct. Relies on delete() to adjust file names correctly.
			if(files[i] == null) {
				byte numChars = readNextByte(); // Size of file name (string length)
				
				for(int j=0;j<numChars;j++) {
					charBuff[j] = (char)readNextByte();
				}
				String name = new String(charBuff, 0, numChars);
				files[i] = new File(name, false); // Uses private constructor so it doesn't check through file list if it already exists.
			}
			files[i].page_location = pageLocation;
			files[i].file_length = fileLength;
		}		
	}
	
	/**
	 * Helper method to read next byte from file table. It
	 * automtatically flips to next page when it gets to end
	 * of last page. 
	 * @return Next byte of data from table.
	 */
	private static byte readNextByte() {
		
		if(byte_pointer >= BYTES_PER_PAGE) {
			++page_pointer; // Throw exception here if > FILE_TABLE_PAGES - 1?
			byte_pointer = 0;
			Flash.readPage(buff, page_pointer);
		}
		
		return buff[byte_pointer++];
	}
	
	/**
	 * Writes the file data to the table from the files [] array. 
	 * @param files The array containing a list of Files to write to table. 
	 */
	static void writeTable(File [] files) throws IOException {
		/*
		 * Note: This method doesn't bother assigning empty 
		 * byte positions as 0. Ghost data appears in memory but
		 * it will be ignored.
		 */
		// Move pointer to start of file table:
		resetTablePointer();
		// Write table id (header with version info): 
		for(int i=0;i<TABLE_ID.length();i++) {
			writeNextByte((byte)TABLE_ID.charAt(i));
		}
		// Write total files (when using GC and arrays, can use array length): 
		// POSPONED UNTIL LATER (see Kludge below)
		writeNextByte((byte)0); // used to increment byte pointer 
		
		// Now write all the file info to the table 
		byte arrayIndex = 0;
		if(files.length != 0) { // Will throw exception for 0 length unless this checks
			while(files[arrayIndex] != null) {
				
				if(files[arrayIndex].file_length == -999) break; // !! What is this for? Can't remember why it is here.
				
				try {
					// Write page location of file:
					writeNextByte((byte)files[arrayIndex].page_location);
					writeNextByte((byte)(files[arrayIndex].page_location>>8));
					// Write file size:
					writeNextByte((byte)files[arrayIndex].file_length);
					writeNextByte((byte)(files[arrayIndex].file_length>>8));
					writeNextByte((byte)(files[arrayIndex].file_length>>16));
					writeNextByte((byte)(files[arrayIndex].file_length>>24));
					// Write length of name:
					writeNextByte((byte)(files[arrayIndex].file_name.length()));
					// Write name:
					for(int i=0;i<files[arrayIndex].file_name.length();i++) {
						writeNextByte((byte)files[arrayIndex].file_name.charAt(i));
					}
				} catch (IOException e) {
					// Write total files (ignoring aborted one) before rethrowing IOException:
					
					// Write the current page to flash:
					writeBufftoFlash();
					
					// KLUDGE (should really be done above): Now write total files 
					Flash.readPage(buff, TABLE_START_PAGE);
					buff[NUM_FILES_POS] = arrayIndex; // Update number of files
					File.totalFiles = arrayIndex; // Update total files in File class?
					Flash.writePage(buff, TABLE_START_PAGE);
					throw e;
				} finally {
					// If catch rethrows IOException wonder if finally called? 
				}
				
				++arrayIndex;
				if(arrayIndex >= files.length) break;
				
			}
		}
		
		// Write the current page to flash:
		writeBufftoFlash();
		
		// KLUDGE (should really be done above): Now write total files 
		Flash.readPage(buff, TABLE_START_PAGE);
		buff[NUM_FILES_POS] = arrayIndex; // Update number of files
		File.totalFiles = arrayIndex; // Update total files in File class?
		Flash.writePage(buff, TABLE_START_PAGE);
	}
	
	/**
	 * Couple of global variables used for table pointers.
	 */
	private static short page_pointer;
	private static short byte_pointer;
	
	/**
	 * Helper method to write the next byte to flash memory
	 * and automatically switch to next page.
	 * @param value The value to write.
	 */
	private static void writeNextByte(byte value) throws IOException {
		if(byte_pointer >= BYTES_PER_PAGE) {
			writeBufftoFlash();
			++page_pointer;
			// Throw exception here if > FILE_TABLE_PAGES - 1:
			if(page_pointer >= FILE_TABLE_PAGES)
				throw new IOException("File table is full. Try deleting some files.");
			byte_pointer = 0;
		}
			
		buff[byte_pointer] = value;
		++byte_pointer;
	}
	
	/**
	 * Writes the current page in buff[] to flash
	 *
	 */
	private static void writeBufftoFlash() {
		Flash.writePage(buff, page_pointer);
	}
	
	/**
	 * Resets the global variables for page and byte pointers to start.
	 *
	 */
	private static void resetTablePointer() {
		page_pointer = TABLE_START_PAGE;
		byte_pointer = 0;
	}
	
	/**
	 * Essentially formats the file system by writing TABLE_ID characters to 
	 * the first page of flash memroy. Also writes 0 as the number of files
	 * in the file system, so it can be used to restart/erase all files.
	 *
	 */
	public static void format() {
		// Write TABLE_ID to buff array:
		for(int i=0;i<TABLE_ID.length();i++) {
			buff[i] = (byte)TABLE_ID.charAt(i);
		}
		// Write # of files (0) right after TABLE_ID
		buff[NUM_FILES_POS] = 0;
		Flash.writePage(buff, TABLE_START_PAGE);
//		LCD.drawInt(999, 12,5);
		files = null;
	}
	
	/**
	 * Creates a new file entry in the flash memory.
	 * @return True indicates file was created in flash. False means it already existed or the size is 0 or less.
	 */
	public boolean createNewFile() throws IOException {
		/**
		 * Internally this method updates the page location value and
		 * adds this file instance to the global array of files.
		 * It then writes the current files array
		 * to the file table. It always adds the file to the end
		 * of the array.
		 */
		
		if(exists()) return false; // Exists in file table
		
		if(files == null) {
			 files = new File[MAX_FILES];
			 readTable(files); // Update file data
		}
		
		// Calculate start page by looking at last File in array
		if(File.totalFiles > 0) { // Make sure array not empty
			this.page_location = files[File.totalFiles - 1].page_location;
			int prevFileSize = files[File.totalFiles - 1].file_length;
			if(prevFileSize == 0) prevFileSize = 1; // Kludge to reserve page for empty files.
			int pages = prevFileSize / BYTES_PER_PAGE;
			if(prevFileSize % BYTES_PER_PAGE != 0) pages++;
			this.page_location = (short)(page_location + pages);
		} else { // If array empty, start writing on first page after table data
			this.page_location = File.FILE_START_PAGE;
		}
		
		// Add this file to the end of files array.
		files[File.totalFiles] = this;
		
		File.writeTable(files); // Now update actual data table
		this.exists = true;//file is in the table
		return true;
	}
	
/**
* Move the file a page at a time, in order from low to high memory
* assumes that new starting page location  is lower in flash memory than the old or else that the new pages
* does not overlap with the old.  
* @param page  starting page of the new location.
*/
	 private void moveTo(int page) throws IOException
	{
		int nrPages = file_length/BYTES_PER_PAGE;
		if(file_length%BYTES_PER_PAGE>0) nrPages++;
		int from = page_location;
		int to = page;
		page_location =(short) page;
		for(int i = 0; i<nrPages;i++)
		{
			Flash.readPage(buff, from++);
			Flash.writePage(buff, to++);

		}
		writeTable(files);	
	}

	 /**
	  * move the file to become the last one in flash memory 
	  */
	public void moveToTop() throws IOException
	{
		File  top = files[totalFiles - 1]; // file at top of flash memory
		// !! Is the 1 value below problematic? I want to expand
		// past 1 page for table. Actually is looks okay.
		int page = 1+ top.getPage()+top.length()/BYTES_PER_PAGE;  
		int length = file_length;
		moveTo(page);	
		delete(); // remove from files[] array
		file_length = length;
		createNewFile(); // put back into files[]
	}
	
	/**
	 * Returns to total free memory in the flash file system.
	 */
	public static int freeMemory() {
		int last_page;
		
		if(files == null) {
			 files = new File[MAX_FILES];
			 File.readTable(files); // Update file data
		}
		
		if (totalFiles <= 0) {
			last_page = -1;
		} else {
			File  top = files[totalFiles - 1]; // file at top of flash memory
			last_page = top.getPage()+(top.length()-1)/BYTES_PER_PAGE;
		}
		return (MAX_USER_PAGES - 1 - last_page) * BYTES_PER_PAGE;
	}

/**
* returns location of file in the files[] array
* @return  index of file in files[]
*/
	public  int getIndex()
	{
		int i = 0;
		while( i<totalFiles  && this != files[i]) i++;
		return i;
	}
	/** 
	 * Indicates if the flash memory contains a file table.
	 * Compares header with expected header (TABLE_HEADER) at the 
	 * start of page 0.
	 */
	private static boolean tableExists() {
		boolean formatted = true;
		Flash.readPage(buff, TABLE_START_PAGE);
		for(int i=0;i<TABLE_ID.length();i++) {
			if(buff[i] != TABLE_ID.charAt(i))
				formatted = false;
		}
		return formatted; 
	}
/**
*  assumptions: the files[] array has no nulls, and is in increasing order by page_location
*  this scheme moves moves each file down to fill in the empty pages. 
*/	
	public static void defrag() throws IOException
	{
		File file;
		int page_pointer = FILE_START_PAGE; // smallest memory location possible for current file 

		for(byte  i = 0; i < totalFiles; i++)
		{
			file = files[i];
			if(file.page_location > page_pointer) file.moveTo(page_pointer);					
			page_pointer = file.page_location + file.length()/BYTES_PER_PAGE ;
			if (file.length()%BYTES_PER_PAGE >0 ) page_pointer++;	
		}
		writeTable(files);	// update the file data in flash memory	
	}
	
	public int getPage() {
		return page_location;
	}
	
	public static void reset() {
		files = null;
	}
}