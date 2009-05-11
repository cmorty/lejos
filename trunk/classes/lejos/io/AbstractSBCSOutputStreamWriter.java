package lejos.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Abstract Single Byte Character Set OutputStream Writer.
 * @author Sven Köhler
 */
public abstract class AbstractSBCSOutputStreamWriter extends Writer
{
	private static final int BUFFER_SIZE = 32;
	private static final byte ERROR_CHAR = (byte)'?';

	private final OutputStream os;
	private final byte[] buffer;
	//cache for storing a high surrogate
	private char high;
	
	public AbstractSBCSOutputStreamWriter(OutputStream os)
	{
		this.buffer = new byte[BUFFER_SIZE]; 
		this.os = os;
	}
	
	protected abstract byte getByte(int c);
	
	
	private int writeChar(int len, char c) throws IOException
	{
		if (Character.isHighSurrogate(c))		
		{
			if (this.high > 0)
				len = this.bufferAdd(len, ERROR_CHAR);
			
			this.high = c;
			return len;
		}
		
		int cp;
		if (!Character.isLowSurrogate(c))
			cp = c;
		else
		{
			if (this.high == 0)
				return this.bufferAdd(len, ERROR_CHAR);
			
			cp = Character.toCodePoint(high, c);
			this.high = 0;
		}
		
		return this.bufferAdd(len, getByte(cp));
	}
	
	private int bufferAdd(int len, byte c) throws IOException
	{
		if (len >= BUFFER_SIZE)
		{
			this.bufferFlush(len);
			len = 0;
		}
		
		this.buffer[len] = c;
		return len + 1;
	}
	
	private void bufferFlush(int len) throws IOException
	{
		this.os.write(this.buffer, 0, len);
	}
	
	
	@Override
	public Writer append(char c) throws IOException
	{
		this.bufferFlush(this.writeChar(0, c));
		return this;
	}

	@Override
	public void write(int c) throws IOException
	{
		this.bufferFlush(this.writeChar(0, (char)c));
	}

	@Override
	public Writer append(CharSequence str, int start, int end) throws IOException
	{
		int bl = 0;
		int len = end - start;
		while (len > 0)
		{
			int buflen = (len < BUFFER_SIZE) ? len : BUFFER_SIZE;
			
			for (int i=0; i<buflen; i++)
				bl = this.writeChar(bl, str.charAt(start + i));
			
			start += buflen;
			len -= buflen;
		}
		this.bufferFlush(bl);
		return this;
	}

	@Override
	public void write(String str, int off, int len) throws IOException
	{
		int bl = 0;
		while (len > 0)
		{
			int buflen = (len < BUFFER_SIZE) ? len : BUFFER_SIZE;
			
			for (int i=0; i<buflen; i++)
				bl = this.writeChar(bl, str.charAt(off + i));
			
			this.os.write(buffer, 0, buflen);
			
			off += buflen;
			len -= buflen;
		}
		this.bufferFlush(bl);
	}

	@Override
	public void write(char[] c, int off, int len) throws IOException
	{
		int bl = 0;
		while (len > 0)
		{
			int buflen = (len < BUFFER_SIZE) ? len : BUFFER_SIZE;
			
			for (int i=0; i<buflen; i++)
				bl = this.writeChar(bl, c[off + i]);
			
			this.os.write(buffer, 0, buflen);
			
			off += buflen;
			len -= buflen;
		}
		this.bufferFlush(bl);
	}

	@Override
	public void close() throws IOException
	{
		if (this.high > 0)
		{
			this.high = 0;
			this.os.write(ERROR_CHAR);
		}
		
		this.os.close();
	}

	@Override
	public void flush() throws IOException
	{
		this.os.flush();
	}
}
