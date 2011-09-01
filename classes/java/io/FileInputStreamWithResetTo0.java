package java.io;

/**
 * extends the file input stream with an implementation of the reset-method that always sets the position to zero, i.e. the beginning of the file.
 * Thus you can reach every position in the file with reset() and skip(n) without mark&reset.
 * 
 * @author Michael Mirwaldt
 *
 */
public class FileInputStreamWithResetTo0 extends FileInputStream {

	protected final File file;
	public FileInputStreamWithResetTo0(File file) throws FileNotFoundException {
		super(file);
		this.file = file;
	}
	
	/**
	 * always resets the position to 0, i.e. the beginning of the file.
	 * DOES NOT SUPPORT MARK&RESET!
	 */
	@Override
	public synchronized void reset() throws IOException {
		this.offset = 0;
		this.page_limit = 0;
	}
}
