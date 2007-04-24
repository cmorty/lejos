package java.io;

import lejos.nxt.Flash;

public class File {

	// CONSTANTS:
	/**
	 *  Number of files the file system can store. 
	 *  Defines the size of the files array. If leJOS gets a garbage
	 *  collector we can get rid of this limitation.
	 */
	private static final byte MAX_FILES = 30;
	
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
	private static File [] files = null;
	
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
		File.writeTable(files);
		// 4. Make this file.exists = false;
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
	static void readTable(File [] files) { // !! Make private when done tests!
		// Make sure flash has table id:
		if(!File.tableExists())	File.format();
		
		Flash.readPage(buff, TABLE_START_PAGE);
		// page_pos is the byte position in the page (pointer):
		short page_pos = NUM_FILES_POS;
		File.totalFiles = buff[page_pos]; // update total files value 
		
		for(int i=0;i<File.totalFiles;i++) {
			short pageLocation = (short)((0xFF & buff[++page_pos]) | ((0xFF & buff[++page_pos])<<8));
			int fileLength = (0xFF & buff[++page_pos]) | ((0xFF & buff[++page_pos]) <<8) | ((0xFF & buff[++page_pos])<<16) | ((0xFF & buff[++page_pos])<<24);
			
			// The following code attempts to reuse String's. If leJOS gets
			// a garbage collector we can create new strings and reduce this
			// code. It assumes that if files[i] is NOT null then the filename
			// is correct. Relies on delete() to adjust file names correctly.
			if(files[i] == null) {
				byte numChars = buff[++page_pos]; // Size of file name (string length)
				
				for(int j=0;j<numChars;j++) {
					charBuff[j] = (char)buff[++page_pos];
				}
				String name = new String(charBuff, 0, numChars);
				files[i] = new File(name, false); // Uses private constructor so it doesn't check through file list if it already exists.
			}
			files[i].page_location = pageLocation;
			files[i].file_length = fileLength;
		}
	}
	
	/**
	 * Writes the file data to the table from the files [] array. 
	 * NOTE: Currently can only use first page of flash to store table! ~ 8 files
	 * @param files The array containing a list of Files to write to table. 
	 */
	static void writeTable(File [] files) { // !! Make private when done tests!
		short table_pointer = NUM_FILES_POS; // Move pointer to start of table
		
		/* 
		 * NOTE: The code below attempts to reuse File objects in the files 
		 * array rather than making it null. An unused File object is identified
		 * as having -999 for the file_length. In effect, -999 is the same as
		 * having a null value in the files array. If leJOS gets a garbage 
		 * collector then this code can be reduced. 
		*/
		// !! THERE IS NO USE DOING THIS REUSING CRAP! WHen they create a new
		// File object it gets added to array anyway. If they delete a file,
		// just null it from the array. If they hang onto the file instance and 
		// use createNewFile() after deleting it, that's fine too (just gets
		// added to array again).
		byte arrayIndex = 0;
		if(files.length != 0) { // Will throw exception for 0 length unless this checks
			while(files[arrayIndex] != null) {
				
				if(files[arrayIndex].file_length == -999) break;
				
				// Write page location of file:
				buff[++table_pointer] = (byte)files[arrayIndex].page_location;
				buff[++table_pointer] = (byte)(files[arrayIndex].page_location>>8);
				// Write file size:
				buff[++table_pointer] = (byte)files[arrayIndex].file_length;
				buff[++table_pointer] = (byte)(files[arrayIndex].file_length>>8);
				buff[++table_pointer] = (byte)(files[arrayIndex].file_length>>16);
				buff[++table_pointer] = (byte)(files[arrayIndex].file_length>>24);
				// Write length of name:
				buff[++table_pointer] = (byte)(files[arrayIndex].file_name.length());
				// Write name:
				for(int i=0;i<files[arrayIndex].file_name.length();i++) {
					buff[++table_pointer] = (byte)files[arrayIndex].file_name.charAt(i);
				}
				++arrayIndex;
				if(arrayIndex >= files.length) break;
			}
		}
		buff[NUM_FILES_POS] = arrayIndex; // Update number of files
		File.totalFiles = arrayIndex; // Update total files in File class?
		Flash.writePage(buff, TABLE_START_PAGE);
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
	}
	
	/**
	 * Creates a new file entry in the flash memory. [?CUT: According to the standard
	 * Java API, a file of 0 length is not written to the file system. Therefore
	 * this will only be added to the file system when the first byte is 
	 * written using FileOutputStream.?]
	 * @param size The number of bytes in this file.
	 * @return True indicates file was created in flash. False means it already existed or the size is 0 or less.
	 */
	public boolean createNewFile() {
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
		
		return true;
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
	
	public int getPage() {
		return page_location;
	}
}