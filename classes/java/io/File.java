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
	 * flash memory contains file table information.
	 */
	private static final String TABLE_ID = "LEJOS";  
	
	/**
	 * Indicates the starting page of the file table.
	 */
	private static byte TABLE_START_PAGE = 0;
	
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
	private static short BYTES_PER_PAGE = 256;
	
	// GLOBAL STATIC CLASS VARIABLES: 
	/**
	 * Shared buffer. Using this as static class variable because leJOS
	 * lacks a garbage collector.
	 */
	private static byte [] buff = new byte[BYTES_PER_PAGE];
	
	/**
	 * Array containing all the Files in the directory. 
	 */
	private static File [] files = new File[MAX_FILES];
	
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
	
	
	// INSTANCE VARIABLES (file name, page location of file, file size):
	/**
	 * The name of the file. Initialized in File constructor.
	 */
	private String file_name;
	
	/**
	 * The starting page location of this file. All files start at the 0 byte
	 * position of the page that they start at.
	 * Init to -1 to indicate not initialized or does that waste memory?
	 */
	short page_location; // !! Make private when done tests!
	
	/**
	 * The length, in bytes, of this file according to the file table.
	 * A file that does not exists is supposed to equal 0. i.e. The Java SDK
	 * says that it doesn't get written to the file table until it has bytes.
	 */
	private int file_length;
	
	public File(String name) {
		if(!File.tableExists()) File.format();
		this.file_name = name;
		
		/* !! CHECK IF FILE EXISTS!!!
		 * !! A file that does not exist is supposed to return length() of 0.
		 * !! But my exists() method might just check length().
		 * So perhaps this method should just look through names manually to 
		 * see if it exists, then steal the file_length and page_location vals.
		if(exists()) {
			// Assign proper values to this object
			for(int i=0;i<FileOld.totalFiles;i++) {
				if(files[i].name.equals(this.name)) {
					this.file_length = files[i].file_length;
					this.page_location = files[i].page_location;
				}
			}
		}
		*/
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
		File.readTable(files); // Update files array with actual files.
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
	 * Reads the file information in the table from flash memory and
	 * stores the information in the array supplied. 
	 * @param files An array of File objects. When the method returns the
	 * array will contain File objects for all the files in flash. If a null
	 * File array is given, it will create a new File array.
	 */
	static void readTable(File [] files) { // !! Make private!
		// Make sure flash has table id:
		if(!File.tableExists())	File.format();
		
		Flash.readPage(buff, TABLE_START_PAGE);
		// page_pos is the byte position in the page (pointer):
		short page_pos = NUM_FILES_POS;
		totalFiles = buff[page_pos]; // update total files value 
		for(int i=0;i<totalFiles;i++) {
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
				files[i] = new File(name);
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
	void writeTable(File [] files) { // !! Make private!
		short table_pointer = NUM_FILES_POS; // Move pointer to start of table
		
		/* 
		 * NOTE: The code below attempts to reuse File objects in the files 
		 * array rather than making it null. An unused File object is identified
		 * as having -999 for the file_length. In effect, -999 is the same as
		 * having a null value in the files array. If leJOS gets a garbage 
		 * collector then this code can be reduced. 
		*/
		byte arrayIndex = 0;
		while(files[arrayIndex].file_length != 999 | files[arrayIndex] != null) {
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
		}
		Flash.writePage(buff, TABLE_START_PAGE);
	}
	
	/**
	 * Essentially formats the file system by writing TABLE_ID characters to 
	 * the first page of flash memroy. Also writes 0 as the number of files
	 * in the file system, so it can be used to restart/erase all files.
	 *
	 */
	private static void format() {
		// Write TABLE_ID to buff array:
		for(int i=0;i<TABLE_ID.length();i++) {
			buff[i] = (byte)TABLE_ID.charAt(i);
		}
		// Write # of files (0) right after TABLE_ID
		buff[NUM_FILES_POS] = 0;
 		Flash.writePage(buff, TABLE_START_PAGE);
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
}