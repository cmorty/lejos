package lejos.nxt.remote;

/**
 * 
 * Structure that gives information about a leJOS NXJ file.
 *
 */
public class FileInfo {
	
	/**
	 * The name of the file - up to 20 characters.
	 */
	public String fileName;
	
	/**
	 * The handle for accessing the file.
	 */
	public byte fileHandle;
	
	/**
	 * The size of the file in bytes.
	 */
	public int fileSize;
	
	/**
	 * The status of the file - not used.
	 */
	public byte status;
	
	/**
	 * The start page of the file in flash memory.
	 */
	public int startPage;
	
	public FileInfo(String fileName) {
		this.fileName = fileName;
	}	
}
