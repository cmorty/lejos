package java.io;

import lejos.nxt.Flash;

/*
 * DEVELOPER NOTES:
 * - Requirement is for all files to be contiguous (unfragmented) so
 * that they can be executed.
 * - Files are stored in the flash memory one file after another.
 * - If a user opens a file and wants to write to it, the file system
 * shuffles the file to the end so that it has open space to write.
 * - This File system is currently unthreaded, so all functions are
 * blocked until each call completes. If someone starts writing to
 * a file and it needs to be shuffled, a significant pause can
 * occur to shuffle files around. If this was threaded it might be
 * possible to avoid this pause.
 * 
 * 3/3/2009: TODO Here are some improvements I'm considering:
 * 
 * 1) Now that we have a garbage collector, the fixed sizes of arrays for names (MAX_FILENAME)
 * and number of files in system (MAX_FILES) shouldn't be limited. Also, some of the code to 
 * reuse objects is a little crazy because of no GC.
 * 2) It would be nice to thread some of these operations so they could return immediately
 * and continue running in background. Would need to synchronize critical operations on the 
 * same object, and make sure thread operations are kept non-daemon so they complete before 
 * JVM exits (we don't want a partial write to occur because the JVM terminates). 
 * 3) Directories is a pretty standard feature of a file system. Useful if we want to 
 * assign a specific directory to dump our NXT on-board applications (future goal). 
 * 4) Instead of mirroring the file table data in memory via the files array and totalFiles
 * variables, I'd rather read this data live from flash memory every time it is used within
 * a method (i.e. keep no persistent variables of file table). That might save memory and prevent
 * possible bugs where local data becomes unsynchronized from flash memory file table.
 * However, cached operations might become suspect if a program tries to read file information
 * before it is done writing. Might not be a great idea actually. 
 * 5) If we got really ambitious, allow > 1 file open for writing at a time. It would be very
 * difficult to implement given our contiguous file requirement.
 * 6) Implement the J2ME solution for writing persistent data, to coexist alongside this class.
 * The File class might use some methods in that solution.
 * 
 */

/**
 * Implements a file system using pages of flash memory.
 * Currently has limited functionality and only supports
 * one file open at a time.
 * 
 * @author bb
 */
public class File {
	
	// CONSTANTS:
	/**
	 * MS-DOS File attribute constants:
	 */
	private static final byte READ_ONLY_ATTR = 0x01;
	private static final byte HIDDEN_ATTR = 0x02;
	//private static final byte SYSTEM_ATTR = 0x04; // System file
	//private static final byte VOLUME_LABEL_ATTR = 0x08;
	//private static final byte DIRECTORY_ATTR = 0x10;
	//private static final byte ARCHIVE_ATTR = 0x20;
			
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
	 * Signature written to the front of the file table to indicate if the
	 * flash memory contains file table information. By changing this
	 * version number/string, the users file system will reformat automatically.
	 * (i.e. Restarting file system and erasing their current stored classes) 
	 */
	private static final String TABLE_ID = "V_0.4";  
	
	/**
	 * Indicates the starting page of the file table.
	 */
	private static byte TABLE_START_PAGE = 1;
	
	/**
	 *  Number of pages reserved for storing file table information.
	 *  If we want to allow more files to be stored in system, increase
	 *  this number.
	 */
	// TODO: To make this expand automatically when necessary, we could start 
	// with the last page in memory and work backwards. File table would be at
	// the end of flash memory instead of start. 
	
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
	
	// GLOBAL STATIC CLASS VARIABLES: 
	/**
	 * Shared buffer. Using this as static class variable because leJOS
	 * lacks a garbage collector.
	 */
	private static byte [] buff = new byte[Flash.BYTES_PER_PAGE];
	
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
	 * Byte that stores bit-wise data of file attributes, like hidden,
	 * locked, compressed, delete on exit, etc...
	 * See file attribute constants above
	 */
	byte file_attributes;
	
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
		// 4. Make this file.exists = false and length = 0.
		this.exists = false;
		this.file_length = 0;
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
	 */
	public static File [] listFiles() {
		if(files == null) {
			 files = new File[MAX_FILES];
			 File.readTable(files); // Update file data
		}
		
		/*File [] retFiles = new File[totalFiles]; 
		for(int i=0;i<retFiles.length;i++) {
			retFiles[i] = files[i];
		}
		
		return retFiles;*/
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
	public long length() {
		return (long) file_length;
	}
	
	/**
	 * Indicates if the file exists in the flash memory.
	 * @return True indicates the file exists, false means it has not been created.
	 */
	public boolean exists() {
		return exists;
	}
		
	public boolean canRead() {
		return true; // All files can be read in NXJ
	}
	
	public boolean canWrite() {
		return !((file_attributes & READ_ONLY_ATTR) == READ_ONLY_ATTR);
	}
	
	public boolean isHidden() {
		return (file_attributes & HIDDEN_ATTR) == HIDDEN_ATTR;
	}
		
	public boolean setReadOnly() {
		file_attributes = (byte)(file_attributes | READ_ONLY_ATTR);
		return true; // Supposed to return false if unsuccessful
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
		File.resetTablePointer();
		Flash.readPage(buff, TABLE_START_PAGE); // Kludge to fill data into first page
		// Move pointer to file total:
		byte_pointer = NUM_FILES_POS; // Kludge
		File.totalFiles = readNextByte(); // update total files value 
		
		for(int i=0;i<File.totalFiles;i++) {
			short pageLocation = (short)((0xFF & readNextByte()) | ((0xFF & readNextByte())<<8));
			int fileLength = (0xFF & readNextByte()) | ((0xFF & readNextByte()) <<8) | ((0xFF & readNextByte())<<16) | ((0xFF & readNextByte())<<24);
			byte fileAttributes = readNextByte();
			// TODO: The following code attempts to reuse String's. If leJOS gets
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
			files[i].file_attributes = fileAttributes;
		}		
	}
	
	/**
	 * Helper method to read next byte from file table. It
	 * automatically flips to next page when it gets to end
	 * of last page. 
	 * @return Next byte of data from table.
	 */
	private static byte readNextByte() {
		
		if(byte_pointer >= Flash.BYTES_PER_PAGE) {
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
		if(files != null && files.length != 0) { // Will throw exception for 0 length unless this checks
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
					// Write file attributes:
					writeNextByte(files[arrayIndex].file_attributes);
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
		if(byte_pointer >= Flash.BYTES_PER_PAGE) {
			writeBufftoFlash();
			++page_pointer;
			// Throw exception here if > FILE_TABLE_PAGES - 1:
			if(page_pointer >= FILE_TABLE_PAGES){
				throw new IOException("File table is full. Try deleting some files.");
			}
			byte_pointer = 0;
		}
			
		buff[byte_pointer] = value;
		++byte_pointer;
	}
	
	/*
	 * Old debugger method. Comment out when no longer buggy.
	public static void dumpFileTable() {
		if(files == null) listFiles(); // Fill list
		
		RConsole.print("byte_pointer = " + byte_pointer + "\n");
		RConsole.print("page_pointer = " + page_pointer + "\n");
		RConsole.print("FILE_TABLE_PAGES = " + FILE_TABLE_PAGES + "\n");
		RConsole.print("files.length = " + files.length + "\n");
		RConsole.print("totalFiles = " + totalFiles + "\n");
	
		for(int i=TABLE_START_PAGE;i<FILE_START_PAGE;i++) {
			Flash.readPage(buff, i);
			for(int j=0;j<Flash.BYTES_PER_PAGE;j++) {
				if(j % 8 == 0) RConsole.print("\n");
				RConsole.print(buff[j] + ", ");
			}
			
			for(int k=0;k<Flash.BYTES_PER_PAGE;k++) {
				if(k % 8 == 0)  RConsole.print("\n");
				RConsole.print((char)buff[k] + " | ");
			}
		}
		RConsole.print("Please copy and paste this into an email to bbagnall@mts.net");
         
	}
	*/
	
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
	 * the first page of flash memory. Also writes 0 as the number of files
	 * in the file system, so it can be used to restart/erase all files.
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
			int pages = prevFileSize / Flash.BYTES_PER_PAGE;
			if(prevFileSize % Flash.BYTES_PER_PAGE != 0) pages++;
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
		int nrPages = file_length/Flash.BYTES_PER_PAGE;
		if(file_length%Flash.BYTES_PER_PAGE>0) nrPages++;
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
	 * Move the file to become the last one in flash memory.
	 */
	public void moveToTop() throws IOException
	{
		File  top = files[totalFiles - 1]; // file at top of flash memory
		// !! Is the 1 value below problematic? I want to expand
		// past 1 page for table. Actually is looks okay.
		int page = 1+ top.getPage()+ (int) top.length()/Flash.BYTES_PER_PAGE;  
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
			last_page = top.getPage()+((int) top.length()-1)/Flash.BYTES_PER_PAGE;
		}
		return (Flash.MAX_USER_PAGES - 1 - last_page) * Flash.BYTES_PER_PAGE;
	}

	/**
	 * Returns location of file in the files[] array
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
	 * Defrag the file system.
	 * 
	 * WARNING: should only be called from the startup menu.
	 * If called from a user program, can cause the current program to
	 * be moved resulting in a data abort of other firmware crash.
	 * 
	 * Assumptions: the files[] array has no nulls, and is in increasing order by page_location.
	 * This scheme moves moves each file down to fill in the empty pages. 
	 */	
	// TODO: This isn't a standard Java API method and should not be public. - BB
	public static void defrag() throws IOException
	{
		int page_pointer = FILE_START_PAGE; // smallest memory location possible for current file 

		//call to initialize files array
		File.listFiles();
		
		for(byte  i = 0; i < totalFiles; i++)
		{
			File file = files[i];
			if(file.page_location > page_pointer)
				file.moveTo(page_pointer);					
			page_pointer = file.page_location + (int) file.length()/Flash.BYTES_PER_PAGE ;
			if (file.length()%Flash.BYTES_PER_PAGE >0 )
				page_pointer++;	
		}
		writeTable(files);	// update the file data in flash memory	
	}
	
	/**
	 * Internal method used to get the page number of the start of the file.
	 * 
	 * @return page number
	 */
	// TODO: This isn't a standard Java API method and should not be public. It is used by  LCP and Sound. - BB
	public int getPage() {
		return page_location;
	}
	
	/**
	 * Reset the files array after an error.
	 * Forces listFiles to read from the file table.
	 */
	// TODO: This isn't a standard Java API method and should not be public. Used by LCP. - BB
	public static void reset() {
		files = null;
	}
}