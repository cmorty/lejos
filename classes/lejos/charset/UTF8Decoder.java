package lejos.charset;

public class UTF8Decoder implements CharsetDecoder
{
	private static final int MIN_NON_ASCII = 0x80;
	private static final int MIN_SEQ2_BYTE = 0xC0;
	private static final int MIN_SEQ3_BYTE = 0xE0;
	private static final int MIN_SEQ4_BYTE = 0xF0;
	private static final int MIN_SEQ5_BYTE = 0xF8;
	
	public int decode(byte[] source, int offset, int limit)
	{
		//assert limit > offset;
		
		int first = source[offset] & 0xFF;
		if (first < MIN_NON_ASCII)
			return first;
		if (first < MIN_SEQ2_BYTE || first >= MIN_SEQ5_BYTE)
			return '?';

		int len;
		if (first < MIN_SEQ3_BYTE)
			len = 2;
		else if (first < MIN_SEQ4_BYTE)
			len = 3;
		else
			len = 4;
		
		if (len > limit - offset)
			return '?';
		
		first &= 0x3F >> len;
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
		if (first < MIN_SEQ2_BYTE || first >= MIN_SEQ5_BYTE)
			return 1;
		
		int len;
		if (first < MIN_SEQ3_BYTE)
			len = 2;
		else if (first < MIN_SEQ4_BYTE)
			len = 3;
		else
			len = 4;
		
		int maxlen = limit - offset;
		if (len > maxlen)
			len = maxlen;
		
		switch (len)
		{
			case 4:
				if ((source[offset + 3] & 0xC0) != 0x80)
					len = 3;
			case 3:
				if ((source[offset + 2] & 0xC0) != 0x80)
					len = 2;
			case 2:
				if ((source[offset + 1] & 0xC0) != 0x80)
					len = 1;
		}
		
		return len;
	}

	public int getMaxCharLength()
	{
		return 4;
	}
}
