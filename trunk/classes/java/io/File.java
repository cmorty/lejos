package java.io;

import lejos.nxt.Flash;

/*
 * leJOS Developer instructions:
 * The file table is stored in page 0 and 1 (as shown in FILE_TABLE_START and
 * FILE_TABLE_PAGES). The file table data is laid out as follows:
 * Byte 0-4: "LEJOS"
 * Byte 5: Number of files in directory
 * Byte 6-7: Page location of file #1 (value = 2 to 895)
 * Byte 8-11: File length (size of file as number of bytes)
 * Byte 12: Number of characters in filename
 * Byte 13 to 13 + Number of characters in filename: File name (String)
 * [repeats data after byte 5 for each additional file in directory]
 */
public class File {
	
	static File [] files;
	static byte totalFiles = -1; // Negative indicates not initialized
	static String header = "LEJOS";  // Written to front of file table
	// First table entry (past header and number of files)
	static byte TOTAL_FILES_LOCATION = (byte)(header.length()); 
	
	static byte MAX_FILES = 30; // Defines size of files array
	static short TOTAL_PAGES = 896; // 0 to 895
	static short PAGE_SIZE = 256; // # bytes per page
	static byte [] buff = new byte[PAGE_SIZE]; // Buffer
	static byte FILE_TABLE_START = 0; // Beginning page of file table
	static byte FILE_TABLE_PAGES = 2; // Number of pages reserved to file table
	static byte FILE_START_PAGE = (byte)(FILE_TABLE_START + FILE_TABLE_PAGES); 
	
	static byte table_pointer;
	
	short page_location; // Starting block of file
	String name; // file name
	int file_length; // Total bytes of file
	
	private static char [] charBuff = new char[30]; // Used in listFiles()
	
	public File(String name) {
		if(!File.isFormatted()) File.format();
		// !! Check if filename exists. If yes, update info in this object?
		this.name = name;
	}
	
	/**
	 * Retrieves a list of File objects. Each item in the list represents
	 * a file in the directory.
	 * @return Array of files.
	 * Note: Due to the lack of garbage collection the array is always 30 in
	 * length regardless of the number of files in the directory. The first null
	 * value in the list indicates the end of the list.
	 */
	static public File[] listFiles() {
		if(!File.isFormatted())
			File.format();
		if(files == null) {
			// !! update files array with file info 
			files = new File[MAX_FILES];
		}
		Flash.readPage(buff, FILE_TABLE_START);
		table_pointer = TOTAL_FILES_LOCATION;
		totalFiles = buff[table_pointer];
		for(int i=0;i<totalFiles;i++) {
			short pageLocation = (short)((0xFF & buff[++table_pointer]) | ((0xFF & buff[++table_pointer])<<8)); // !! Possible bug
			int fileLength = (0xFF & buff[++table_pointer]) | ((0xFF & buff[++table_pointer]) <<8) | ((0xFF & buff[++table_pointer])<<16) | ((0xFF & buff[++table_pointer])<<24); // !! Possible bug
			
			if(files[i] == null) {
				byte nameChars = buff[++table_pointer];
				for(int j=0;j<nameChars;j++) {
					charBuff[j] = (char)buff[++table_pointer];
				}
				String name = new String(charBuff, 0, nameChars);
				files[i] = new File(name);
			}
			files[i].page_location = pageLocation;
			files[i].file_length = fileLength;
		}
		return files;
	}
	
	public void exec() {
		Flash.exec(page_location, file_length);
	}
	
	public void delete() {
		// !! Rewrite file table without this file
		// !! Auto-defrag?
	}
	
	// Note this currently operates in first page of memory only
	// Needs to be expanded to handle more files over multiple pages
	
	/**
	 * Creates a new 
	 * @param size The number of bytes in this file.
	 */
	public void createNewFile(int size) {
		
		// !! Check if file name already exists.
		
		this.file_length = size;
		
		// Move table_pointer to first empty table entry:
		File [] files = listFiles();
		page_location = FILE_START_PAGE;
		table_pointer++; // Move to first empty file table location
		
		// Find last page used, so next page will be page start
		// Look at info in last File in listFiles(). Size and page number
		// indicate first free page.
		int index = 0;
		while(files[index+1] != null) {
			++index;
		}
		if(files[index] != null) {
			page_location = files[index].page_location;
			int fileSize = files[index].file_length;
			int pages = fileSize / PAGE_SIZE;
			if(fileSize % PAGE_SIZE != 0) pages++;
			page_location = (short)(page_location + pages);
		} else {
			// !! Store new values here so it's live data?
		}
		
		// Write page location of file:
		buff[table_pointer] = (byte)page_location;
		buff[table_pointer + 1] = (byte)(page_location>>8);
		// Write file size:
		buff[table_pointer + 2] = (byte)(size);
		buff[table_pointer + 3] = (byte)(size>>8);
		buff[table_pointer + 4] = (byte)(size>>16);
		buff[table_pointer + 5] = (byte)(size>>24);
		// Write length of name:
		buff[table_pointer + 6] = (byte)(name.length());
		// Write name:
		for(int i=0;i<name.length();i++) {
			buff[table_pointer + 7 + i] = (byte)name.charAt(i);
		}
		// Assign this to files array:
		files[totalFiles] = this;
		
		// Increase file count by one and write to flash
		buff[TOTAL_FILES_LOCATION] = ++totalFiles;
		Flash.writePage(buff, FILE_TABLE_START);
	}
	
	/**
	 * "Formats" the flash drive by writing "LEJOS" to the first 5 bytes,
	 * followed by the number of files in the directory (0 to start with)
	 *
	 */
	static public void format() {
		for(int i=0;i<header.length();i++) {
			buff[i] = (byte)header.charAt(i);
		}
		buff[header.length()] = 0; // Number of files = 0 when formated
 		Flash.writePage(buff, FILE_TABLE_START);
	}
	
	// Compares header with expected header
	public static boolean isFormatted() {
		boolean formatted = true;
		Flash.readPage(buff, FILE_TABLE_START);
		for(int i=0;i<header.length();i++) {
			if(buff[i] != header.charAt(i))
				formatted = false;
		}
		return formatted; 
	}
	
	public boolean exists() {
		// Check with 'files' array if a file with this name exists
		boolean exists = false; 
		
		if(files == null)
			File.listFiles(); // update list
				
		for(int i=0;i<File.totalFiles;i++) {
			if(files[i].name.equals(this.name)) {
				exists = true;
			}
		}
		return exists;
	}
	
	public static void displayPage(int page, int loc) {
		int chars_per_line = 16;
		int rows = 8;
		Flash.readPage(buff, page);
		for(int y=0;y<rows;y++) {
			for(int i=0;i<chars_per_line/4;i++) {
				lejos.nxt.LCD.drawInt(buff[i + (4 * y) + loc], i*4, y);
			}
		}
		lejos.nxt.LCD.refresh();
	}
}