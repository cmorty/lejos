package lejos.nxt;

/**
 * Read and write access to flash memory in pages.
 * 
 * @author Lawrie Griffiths
 *
 */
public class Flash {
	
	/**
	 * Maximum number of pages available to user flash memory.
     * This value is obtained automatically from the firmware.
	 * 
	 */
	public static final int MAX_USER_PAGES = NXT.getUserPages();
	
	/**
	 * Indicates the # of bytes per page in a page of Flash memory.
	 */
	public static short BYTES_PER_PAGE = 256;

	private Flash()
	{
	}
	
	public static native void readPage(byte[] buf, int pageNum);

	public static native void writePage(byte[] buf, int pageNum);
	
	public static native void exec(int pageNum, int size);
}
