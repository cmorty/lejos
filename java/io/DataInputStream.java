
package java.io;


/**
 * Reads java data types transmitted as bytes over an InputStream.
 */
public class DataInputStream extends InputStream
{
	//MISSING implements DataInput
	//MISSING extends FilterInputStream
	//MISSING public static final String readUTF()
	//MISSING public final String readUTF()
	
	//TODO should be inherited by FilterInputStream
	protected InputStream  in;
	
	public DataInputStream(InputStream in)
	{
		this.in = in; 
	}
	
	/**
	 * Reads the next byte of data from this input stream. The value 
	 * byte is returned as an <code>int</code> in the range 
	 * <code>0</code> to <code>255</code>. If no byte is available 
	 * because the end of the stream has been reached, the value 
	 * <code>-1</code> is returned. This method blocks until input data 
	 * is available, the end of the stream is detected, or an exception 
	 * is thrown. 
	 * <p>
	 * This method
	 * simply performs <code>in.read()</code> and returns the result.
	 *
	 * @return	the next byte of data, or <code>-1</code> if the end of the
	 *				stream is reached.
	 * @exception  IOException  if an I/O error occurs.
	 * @see		java.io.FilterInputStream#in
	 */
	public int read() throws IOException
	{
		//TODO should be inherited by FilterInputStream
		return in.read();
	}
	
	public final int read(byte b[]) throws IOException
	{
		return in.read(b, 0, b.length);
	}

	public final int read(byte b[], int off, int len) throws IOException
	{
		return in.read(b, off, len);
	}

	private int readByte0() throws IOException
	{
		int ch = in.read();
		if (ch < 0)
			throw new EOFException();
		//actually, InputStream.read() should always return values from 0 to 255
		return ch & 0xFF;
	}

	public final boolean readBoolean() throws IOException
	{
		return (readByte0() != 0);
	}
	
	public final byte readByte() throws IOException
	{
		return (byte)readByte0();
	}
	
	public final char readChar() throws IOException
	{
		int x = readByte0();
		x = (x << 8) | readByte0();
		return (char)x;
	}

	public final double readDouble() throws IOException 
	{
		long x = this.readLong();
		return Double.longBitsToDouble(x);
	}
	
	public final float readFloat() throws IOException
	{
		int x = this.readInt();
		return Float.intBitsToFloat(x);
	}

	public final void readFully(byte b[]) throws IOException
	{
		readFully(b, 0, b.length);
	}
	
	public final void readFully(byte b[], int off, int len) throws IOException
	{
		if (len < 0)
			throw new IOException();

		while (len > 0)
		{
			int count = in.read(b, off, len);
			if (count < 0)
				throw new EOFException();
			
			off += count;
			len -= count;
		}
	}
	
	public final int readInt() throws IOException 
	{
		int x = readByte0();
		x = (x << 8) | readByte0();
		x = (x << 8) | readByte0();
		x = (x << 8) | readByte0();
		return x;
	}
	
	public final long readLong() throws IOException 
	{
		long x = readByte0();
		x = (x << 8) | readByte0();
		x = (x << 8) | readByte0();
		x = (x << 8) | readByte0();		
		x = (x << 8) | readByte0();		
		x = (x << 8) | readByte0();		
		x = (x << 8) | readByte0();		
		x = (x << 8) | readByte0();		
		return x;
	}
	
	public final short readShort() throws IOException 
	{
		int x = readByte0();
		x = (x << 8) | readByte0();
		return (short)x;
	}
	
	public final int readUnsignedByte() throws IOException 
	{
		int x = readByte0();
		return x;
	}
	
	public final int readUnsignedShort() throws IOException
	{
		int x = readByte0();
		x = (x << 8) | readByte0();
		return x;
	}
	
	public final int skipBytes(int n) throws IOException
	{
		int total = 0;
		int cur = 0;
	
		while ((total<n) && ((cur = (int) in.skip(n-total)) > 0))
		{
			total += cur;
		}
		return total;
	}
	
	/**
	 * @deprecated use BufferedReader.readLine instead
	 */
	public final String readLine() throws IOException
	{
		StringBuffer strb = new StringBuffer();
		
		//MISSING readLine() does not recognize \r and \r\n as line endings
		
		while(true)
		{
			int c = this.read();
			
			if (c < 0) { // EOF
				if (strb.length() == 0) return null;
				break;
			}
			
			if (c == '\n')
				break;
			
			strb.append((char)c);
		}
		return strb.toString();
	}
	
	public void close() throws IOException
	{
		//TODO should be inherited by FilterInputStream
		in.close();
	}
}