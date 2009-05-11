package lejos.io;

import java.io.OutputStream;

/**
 * @author Sven Köhler
 */
public class UTF8OutputStreamWriter extends AbstractOutputStreamWriter
{
	public UTF8OutputStreamWriter(OutputStream os)
	{
		super(os);
	}
	
	@Override
	protected int getBytes(byte[] buf, int len, int cp)
	{
		if (cp < 0 || cp > 0x1FFFFF)
			buf[len++] = ERROR_CHAR;		
		else if (cp <= 0x7F)
			buf[len++] = (byte)cp;
		else if (cp <= 0x7FF)
		{
			buf[len++] = (byte)((cp >> 6) | 0xC0);
			buf[len++] = (byte)(cp & 0x3F | 0x80);
		}
		else if (cp <= 0xFFFF)
		{
			buf[len++] = (byte)((cp >> 12) | 0xE0);
			buf[len++] = (byte)((cp >> 6) | 0x80);
			buf[len++] = (byte)(cp & 0x3F | 0x80);
		}
		else
		{
			buf[len++] = (byte)((cp >> 18) | 0xF0);
			buf[len++] = (byte)((cp >> 12) | 0x80);
			buf[len++] = (byte)((cp >> 6) | 0x80);
			buf[len++] = (byte)(cp & 0x3F | 0x80);
		}
		return len;
	}	
}
