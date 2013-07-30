package lejos.internal.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import lejos.internal.charset.CharsetDecoder;

public class LejosInputStreamReader extends Reader
{
	private static final int MIN_BUFFERSIZE = 16;
	
	private final CharsetDecoder coder;
	private final InputStream is;
	private final byte[] buffer;
	private int offset;
	private int limit;
	//cache for storing a low surrogate
	private char low;
	
	public LejosInputStreamReader(InputStream is, CharsetDecoder coder, int buffersize)
	{
		this.is = is;
		this.coder = coder;
		
		if (buffersize < MIN_BUFFERSIZE)
			buffersize = MIN_BUFFERSIZE;
		
		if (coder.getMaxCharLength() > buffersize)
			throw new IllegalArgumentException("buffer to small for given charset");
		
		this.buffer = new byte[buffersize];
	}
	
	/**
	 * Fills byte buffer. Makes sure, that at least one decodable codepoint is in the buffer, unless EOF
	 * is reached.
	 * @return number of bytes of the next codepoint in the buffer (may be malformed data), or 0 if the buffer is empty and EOF is reached   
	 * @throws IOException
	 */
	private int fillBuffer() throws IOException
	{
		int req = coder.estimateByteCount(buffer, offset, limit);
		int len = limit - offset;
		if (len < req)
		{		
			int rem = buffer.length - offset;
			if (rem < coder.getMaxCharLength())
			{
				System.arraycopy(buffer, offset, buffer, 0, len);
				offset = 0;
				limit = len;
			}
			
			do
			{
				int tmp = is.read(buffer, limit, buffer.length - limit);
				if (tmp < 0)
					//len is still smaller then req
					return len;
				
				len += tmp;
				limit += tmp;
				//update req, since initial value might have been an approximation
				req = coder.estimateByteCount(buffer, offset, limit);
			} while (len < req);
		}
		return req;
	}
	
	@Override
	public int read() throws IOException
	{
		if (low > 0)
		{
			char tmp = this.low;
			this.low = 0;
			return tmp;
		}
		
		int needed = this.fillBuffer();
		if (needed <= 0)
			return -1;
		
		int cp = this.coder.decode(buffer, offset, limit);
		this.offset += needed;
		
		if (cp < Character.MIN_SUPPLEMENTARY_CODE_POINT)
			return cp;
		
		cp -= Character.MIN_SUPPLEMENTARY_CODE_POINT;
		this.low = (char)(cp & 0x3FF | Character.MIN_LOW_SURROGATE);			
		return (cp >> 10) | Character.MIN_HIGH_SURROGATE;
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		if (len < 1)
			return 0;
		
		int origoff = off;
		int endoff = off + len; 
		
		int needed;
		if (this.low > 0)
		{
			cbuf[off++] = this.low;
			this.low = 0;
			
			if (off >= endoff)
				return 1;
			
			//don't fill buffer to avoid blocking
			needed = this.coder.estimateByteCount(buffer, offset, limit);			
		}
		else
		{
			//fill buffer
			needed = this.fillBuffer();
			if (needed <= 0)
				return -1;
		}
				
		while (limit - offset >= needed)
		{
			int cp = this.coder.decode(buffer, offset, limit);
			this.offset += needed;
						
			if (cp < Character.MIN_SUPPLEMENTARY_CODE_POINT)
			{
				cbuf[off++] = (char)cp;
			}
			else
			{
				cp -= Character.MIN_SUPPLEMENTARY_CODE_POINT;
				cbuf[off++] = (char)((cp >> 10) + Character.MIN_HIGH_SURROGATE);
				char tmp = (char)((cp & 0x3F) + Character.MIN_LOW_SURROGATE);
				if (off < endoff)
				{
					cbuf[off++] = tmp;
				}
				else
				{
					this.low = tmp;
					break;
				}				
			}
			
			if (off >= endoff)
				break;
			
			needed = this.coder.estimateByteCount(buffer, offset, limit);
		}
		
		return off - origoff;
	}
	
	@Override
	public void close() throws IOException
	{
		this.low = 0;
		this.offset = this.limit = 0;
		this.is.close();
	}
}
