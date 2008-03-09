package java.io;

/**
 * This is a minimal implementation of BufferedWriter.
 * Normally BufferedWriter extends the abstract class Writer. 
 * The main reason for NXJ including this is to allow the new-line
 * character in text.
 * NOTE: Text writing is not synchronized either like standard Java.
 * 
 * @author BB
 *
 */
public class BufferedWriter {

	OutputStream out;
	
	/**
	 * In the standard Java API the constructor accepts a Writer
	 * object, such as OutputStreamWriter.
	 * @param out
	 */
	public BufferedWriter(OutputStream out) {
		this.out = out;
	}
	
	/**
	 * Currently does not translate ASCII escape sequences like \n
	 * into the appropriate character.
	 * @param s
	 */
	public void write(String s) throws IOException {
		// NEEDS TO WRITE INT instead of BYTE for PC???
		for(int i=0;i<s.length();i++)
			out.write((int)s.toCharArray()[i]);
		
		flush(); // Does real one do this?
	}
	
	/**
	 * Writes char(13) to the destination.
	 *
	 */
	public void newLine() throws IOException {
		// !! Might need to write 4 bytes for int value. 
		out.write(13);
		out.write(10);
		flush(); // Does real one do this?
	}
	
	public void flush() throws IOException {
		out.flush();
	}
}
