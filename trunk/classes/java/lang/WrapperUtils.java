package java.lang;

/**
 * Just some utility methods for the wrapper classes as well as StringBuffer and StringBuilder.
 * @author Sven Köhler
 */
class WrapperUtils
{
	static char digit(int i)
	{
		//FIXME use Character class
		
		if (i < 10)
			return (char)('0' + i);
		else
			return (char)('a' - 10 + i);
	}

	static int parseDigit(char c, int radix)
	{
		//FIXME use Character class
		
		int r;
		if (c >= '0' && c <= '9')
			r = c - '0';
		else if (c >= 'a' && c <= 'z')
			r = c - ('a'  - 10);
		else if (c >= 'A' && c <= 'Z')
			r = c - ('A'  - 10);
		else
			throw new NumberFormatException("illegal digit character");
		
		if (r >= radix)
			throw new NumberFormatException("digit greater than radix");
		
		return r;
	}
	
	/**
	 * For the parseInt/parseLong methods.
	 */
	static void throwNumberFormat(String s, int radix)
	{
		if (s == null)
			throw new NumberFormatException("string is null");
		
		//FIXME use Character constants		
		if (radix < 2 || radix > 36)
			throw new NumberFormatException("given radix is invalid");
	}
	
	/**
	 * For the toString() methods of Integer/Long.
	 */
	static int invalidRadixTo10(int radix)
	{
		//FIXME use Character constants		
		if (radix < 2 || radix > 36)
			return 10;
		
		return radix;
	}

	/**
	 * Low-level convert of int to char[].
	 */
	static int getChars(char[] buf, int p, int v, int radix)
	{
		int v2 = (v <= 0) ? v : -v;
		
		buf[--p] = digit(-(int)(v2 % radix));
		while (v2 != 0)
		{
			buf[--p] = digit(-(int)(v2 % radix));
			v2 /= radix;
		}
		
		if (v < 0)
			buf[--p] = '-';
		
		return p;
	}

	/**
	 * Low-level convert of long to char[].
	 */
	static int getChars(char[] buf, int p, long v, int radix)
	{
		long v2 = (v <= 0) ? v : -v;
		
		buf[--p] = digit(-(int)(v2 % radix));
		while (v2 != 0)
		{
			buf[--p] = digit(-(int)(v2 % radix));
			v2 /= radix;
		}
		
		if (v < 0)
			buf[--p] = '-';
		
		return p;
	}

	/**
	 * For {@link #approxStringLengthInt(int)} and {@link #approxStringLengthLong(int)}. 
	 */
	static int floorLog2(int v)
	{
		//min radix is 2
		if (v < 4)
			return 1;
		if (v < 8)
			return 2;
		if (v < 16)
			return 3;
		if (v < 32)
			return 4;
		
		//max radix is 36
		return 5;
	}

	/**
	 * Estimate size of buffer for {@link #getChars(char[], int, int, int)}.
	 */
	static int approxStringLengthInt(int radix)
	{
		//the following is >= ceil(64 / log(2, radix)) +1 which is the maximum number of digits + sign
		return 31 / floorLog2(radix) + 2;
	}

	/**
	 * Estimate size of buffer for {@link #getChars(char[], int, long, int)}.
	 */
	static int approxStringLengthLong(int radix)
	{
		//the following is >= ceil(64 / log(2, radix)) +1 which is the maximum number of digits + sign
		return 63 / floorLog2(radix) + 2;
	}

	/**
	 * Exact size of buffer for {@link #getChars(char[], int, int, int)}.
	 */
	static int exactStringLength(int v, int radix)
	{
		int c = (v < 0) ? 2 : 1;
		while (v != 0)
		{
			c++;
			v /= radix;
		}
		return c;
	}

	/**
	 * Exact size of buffer for {@link #getChars(char[], int, long, int)}.
	 */
	static int exactStringLength(long v, int radix)
	{
		int c = (v < 0) ? 2 : 1;
		while (v != 0)
		{
			c++;
			v /= radix;
		}
		return c;
	}
}
