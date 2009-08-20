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
    
    @Override
	public void write (int c) {
    	try {
    		os.write(c);
    	} catch (IOException ioe) {};
    }
    
    /**
     * Writes a string to the underlying output stream.
     * 
     * @param s the string to print
     */
    public void print(String s) {
    	for(int i=0;i<s.length();i++) {
    		write(s.charAt(i));
    	}
        //TODO optional flush
    }
    
    /**
     * Flush any pending output in the stream
     */
    @Override
	public void flush()
    {
    	try {
    		os.flush();
    	} catch (IOException ioe) {}      
    }
    
    /**
     * Writes a newline character
     * to the underlying output stream.
     */
    public void println() {
        write('\n');
        //TODO make flush optional
        flush();
    }
    
    /**
     * Writes a string followed by a newline character
     * to the underlying output stream.
     * 
     * @param s the string to print
     */
    public void println(String s) {
        print(s);
        write('\n');
        //TODO make flush optional
        flush();
    }
    
    
    /*** print() Delegates ***/
    
    public void print(boolean v)
    {
    	print(String.valueOf(v));
    }
    
    public void print(char v)
    {
    	print(String.valueOf(v));
    }
    
    public void print(char[] v)
    {
    	print(String.valueOf(v));
    }
    
    public void print(double v)
    {
    	print(String.valueOf(v));
    }
    
    public void print(float v)
    {
    	print(String.valueOf(v));
    }
    
    public void print(int v)
    {
    	print(String.valueOf(v));
    }
    
    public void print(long v)
    {
    	print(String.valueOf(v));
    }
    
    public void print(Object v)
    {
    	print(String.valueOf(v));
    }
    
    /*** println() Delegates ***/
    
    public void println(boolean v)
    {
    	println(String.valueOf(v));
    }
    
    public void println(char v)
    {
    	println(String.valueOf(v));
    }
    
    public void println(char[] v)
    {
    	println(String.valueOf(v));
    }
    
    public void println(double v)
    {
    	println(String.valueOf(v));
    }
    
    public void println(float v)
    {
    	println(String.valueOf(v));
    }
    
    public void println(int v)
    {
    	println(String.valueOf(v));
    }
    
    public void println(long v)
    {
    	println(String.valueOf(v));
    }
    
    public void println(Object v)
    {
    	println(String.valueOf(v));
    }
}
