package java.io;


/**
 * Reads java data types transmitted as bytes over an InputStream.
 * 
 * @author Sven Köhler
 */
public class DataInputStream extends FilterInputStream implements DataInput
{
	public DataInputStream(InputStream in)
	{
		super(in); 
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
			//TODO is this correct?
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
	
	public final String readUTF() throws IOException
	{
		return readUTF(this);
	}
	
	public static final String readUTF(DataInput in) throws IOException
	{
		throw new UnsupportedOperationException("not yet implemented");
	}
	
	public final int skipBytes(int n) throws IOException
	{
		return (int)this.in.skip(n);
	}
	
	/**
	 * Deprecated. This method assumes ISO-8859-1 encoding and does only recognize \n and \r\n line-endings. 
	 * 
	 * @deprecated broken in various ways, use BufferedReader.readLine instead
	 */
	@Deprecated
	public final String readLine() throws IOException
	{
		StringBuilder strb = new StringBuilder();
		
		//MISSING readLine() does not recognize \r line endings
		
		while(true)
		{
			int c = this.read();
			
			// catch EOF
			if (c < 0)
			{ 
				if (strb.length() == 0)
					return null;
				
				break;
			}
			
			if (c == '\n')
			{
				int p = strb.length() - 1;
				if (p >= 0 && strb.charAt(p) == '\r')
					return strb.substring(0, p);
				
				break;
			}
			
			strb.append((char)c);
		}
				
		return strb.toString();
	}
}