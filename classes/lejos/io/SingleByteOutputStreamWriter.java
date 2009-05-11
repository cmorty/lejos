package lejos.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public abstract class SingleByteOutputStreamWriter extends Writer
{
	private static final int BUFFER_SIZE = 32;
	
	private final OutputStream os;
	private final byte[] buffer;
	
	public SingleByteOutputStreamWriter(OutputStream os)
	{
		this.buffer = new byte[BUFFER_SIZE]; 
		this.os = os;
	}
	
	protected abstract byte getByte(int c);
	
	@Override
	public Writer append(char c) throws IOException
	{
		this.os.write(getByte(c));
		return this;
	}

	@Override
	public void write(int c) throws IOException
	{
		this.os.write(getByte(c));
	}

	@Override
	public Writer append(CharSequence str, int start, int end) throws IOException
	{
		int len = end - start;
		while (len > 0)
		{
			int buflen = (len < BUFFER_SIZE) ? len : BUFFER_SIZE;
			
			for (int i=0; i<buflen; i++)
				buffer[i] = getByte(str.charAt(start + i));
			
			this.os.write(buffer, 0, buflen);
			
			start += buflen;
			len -= buflen;
		}
		return this;
	}

	@Override
	public void write(String str, int off, int len) throws IOException
	{
		while (len > 0)
		{
			int buflen = (len < BUFFER_SIZE) ? len : BUFFER_SIZE;
			
			for (int i=0; i<buflen; i++)
				buffer[i] = getByte(str.charAt(off + i));
			
			this.os.write(buffer, 0, buflen);
			
			off += buflen;
			len -= buflen;
		}
	}

	@Override
	public void write(char[] c, int off, int len) throws IOException
	{
		while (len > 0)
		{
			int buflen = (len < BUFFER_SIZE) ? len : BUFFER_SIZE;
			
			for (int i=0; i<buflen; i++)
				buffer[i] = getByte(c[off + i]);
			
			this.os.write(buffer, 0, buflen);
			
			off += buflen;
			len -= buflen;
		}
	}

	@Override
	public void close() throws IOException
	{
		this.os.close();
	}

	@Override
	public void flush() throws IOException
	{
		this.os.flush();
	}
}
