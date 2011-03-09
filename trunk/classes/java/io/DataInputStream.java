package java.io;


/**
 * Reads java data types transmitted as bytes over an InputStream.
 * 
 * @author Sven Köhler
 */
public class DataInputStream extends FilterInputStream implements DataInput
{
	private byte bytearr[] = new byte[80];
    private char chararr[] = new char[80];
    
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
		//TODO implement readUTF
		//throw new UnsupportedOperationException("not yet implemented");
		int utflen = in.readUnsignedShort();
		byte[] bytearr = null;
		char[] chararr = null;
		if (in instanceof DataInputStream) {
			DataInputStream dis = (DataInputStream)in;
			if (dis.bytearr.length < utflen){
				dis.bytearr = new byte[utflen*2];
				dis.chararr = new char[utflen*2];
			}
			chararr = dis.chararr;
			bytearr = dis.bytearr;
		} else {
			bytearr = new byte[utflen];
			chararr = new char[utflen];
		}

		int c, char2, char3;
		int count = 0;
		int chararr_count=0;

		in.readFully(bytearr, 0, utflen);

		while (count < utflen) {
			c = (int) bytearr[count] & 0xff;      
			if (c > 127) break;
			count++;
			chararr[chararr_count++]=(char)c;
		}

		while (count < utflen) {
			c = (int) bytearr[count] & 0xff;
			switch (c >> 4) {
			case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
				/* 0xxxxxxx*/
				count++;
				chararr[chararr_count++]=(char)c;
				break;
			case 12: case 13:
				/* 110x xxxx   10xx xxxx*/
				count += 2;
				if (count > utflen)
					throw new IOException(
							"malformed input: partial character at end");
				char2 = (int) bytearr[count-1];
				if ((char2 & 0xC0) != 0x80)
					throw new IOException(
							"malformed input around byte " + count); 
				chararr[chararr_count++]=(char)(((c & 0x1F) << 6) | 
						(char2 & 0x3F));  
				break;
			case 14:
				/* 1110 xxxx  10xx xxxx  10xx xxxx */
				count += 3;
				if (count > utflen)
					throw new IOException(
							"malformed input: partial character at end");
				char2 = (int) bytearr[count-2];
				char3 = (int) bytearr[count-1];
				if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
					throw new IOException(
							"malformed input around byte " + (count-1));
				chararr[chararr_count++]=(char)(((c     & 0x0F) << 12) |
						((char2 & 0x3F) << 6)  |
						((char3 & 0x3F) << 0));
				break;
			default:
				/* 10xx xxxx,  1111 xxxx */
				throw new IOException(
						"malformed input around byte " + count);
			}
		}
		// The number of chars produced may be less than utflen
		return new String(chararr, 0, chararr_count);
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