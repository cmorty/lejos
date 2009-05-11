package java.io;

/**
 * Basic Writer implementation. In contrast to the JDK, this class is NOT synchronized.
 * 
 * @author Sven Köhler
 */
public abstract class Writer
{
	private static final int STRING_BUF_SIZE = 32; 
	
	//MISSING protected Object lock
	//MISSING protected Writer(Object lock)
	//MISSING Writer append(CharSequence str)
	//MISSING Writer append(CharSequence str, int start, int end)
	
	protected Writer()
	{
		//nothing
	}
	
	Writer append(char c) throws IOException
	{
		this.write(c);
		return this;
	}
	
	public abstract void close() throws IOException;

	public abstract void flush() throws IOException;
	
	public void write(char[] c) throws IOException
	{
		this.write(c, 0, c.length);
	}
	
	public abstract void write(char[] c, int off, int len) throws IOException;
	
	public void write(int c) throws IOException
	{
		this.write(new char[] { (char)c }, 0, 1);		
	}
	
	public void write(String str) throws IOException
	{
		this.write(str, 0, str.length());
	}
	
	public void write(String str, int off, int len) throws IOException
	{
		char[] buffer = new char[STRING_BUF_SIZE];
		
		while (len > 0)
		{
			int buflen = (len < STRING_BUF_SIZE) ? len : STRING_BUF_SIZE;
			
			int end = off + buflen;
			str.getChars(off, end, buffer, 0);
			
			this.write(buffer, 0, buflen);
			
			off = end;
			len -= buflen;
		}
	}
}
