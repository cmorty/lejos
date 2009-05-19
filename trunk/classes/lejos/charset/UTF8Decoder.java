package lejos.charset;

public class UTF8Decoder implements CharsetDecoder
{
	public int decode(byte[] source, int offset, int limit)
	{
		//assert limit > offset;
		
		int first = source[offset] & 0xFF;
		if (first < 0x80)
			return first;
		if (first < 0xC0 || first >= 0xF8)
			return '?';

		int len;
		if (first < 0xE0)
		{
			first &= ~0xE0;
			len = 2;
		}		
		else if (first < 0xF0)
		{
			first &= ~0xF0;
			len = 3;
		}		
		else
		{
			first &= ~0xF8;
			len = 4;
		}	
		
		for (int i = 1; i < len; i++)
		{
			int b = source[offset + i];
			if ((b & 0xC0) != 0x80)
				return '?';
			first = (first << 6) | (b & 0x3F);
		}
		
		return first;
	}

	public int estimateByteCount(byte[] source, int offset, int limit)
	{
		if (offset >= limit)
			return 1;
		
		int first = source[offset] & 0xFF;		
		if (first < 0xC0 || first >= 0xF8)
			return 1;
		
		int len;
		if (first < 0xE0)
			len = 2;
		else if (first < 0xF0)
			len = 3;
		else
			len = 4;
		
		int end = offset + len;
		if (end > limit)
			end = limit;
		
		for (int i=offset+1; i<end; i++)
			if ((source[i] & 0xC0) != 0x80)
				return i-offset;
		
		return len;
	}

	public int getMaxCharLength()
	{
		return 4;
	}
}
