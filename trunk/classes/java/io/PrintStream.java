package java.io;

/**
 * Minimal implementation of PrintStream.
 * 
 * Currently only implements the mandatory write
 * method and println.
 * 
 * @author Lawrie Griffiths
 *
 */
public class PrintStream extends OutputStream {	
	private OutputStream os;
	
    public PrintStream(OutputStream os) {
    	this.os = os;  	
    }
    
    public void write (int c) {
    	try {
    		os.write(c);
    	} catch (IOException ioe) {};
    }
    
    /**
     * Writes a string followed by a newline character
     * to the underlying output stream.
     * 
     * @param s the string to print
     */
    public void println(String s) {
    	for(int i=0;i<s.length();i++) {
    		write(s.charAt(i));
    	}
    	write('\n');
    }
}
