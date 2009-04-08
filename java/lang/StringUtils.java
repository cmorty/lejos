package java.lang;

/**
 * Just some utility methods for the wrapper classes as well as StringBuffer and StringBuilder.
 * @author Sven Köhler
 */
class StringUtils
{
	static int parseDigit(char c, int radix)
	{
		int r = Character.digit((int)c, radix);		
		if (r < 0)
			throw new NumberFormatException("illegal digit character");
		
		return r;
	}
	
	/**
	 * For the parseInt/parseLong methods.
	 */
	static void throwNumberFormat(String s, int radix)
	{
		if (s == null)
			throw new NumberFormatException("string is null");
		
		if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
			throw new NumberFormatException("given radix is invalid");
	}
	
	/**
	 * For the toString() methods of Integer/Long.
	 */
	static int invalidRadixTo10(int radix)
	{
		if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
			return 10;
		
		return radix;
	}

	/**
	 * Low-level convert of int to char[].
	 */
	static int getChars(char[] buf, int p, int v, int radix)
	{
		int v2 = (v <= 0) ? v : -v;
		
		do
		{
			buf[--p] = Character.forDigit(-(v2 % radix), radix);
			v2 /= radix;
		} while (v2 != 0);
		
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
		
		do
		{
			buf[--p] = Character.forDigit(-(int)(v2 % radix), radix);
			v2 /= radix;
		} while (v2 != 0);
		
		if (v < 0)
			buf[--p] = '-';
		
		return p;
	}

	/**
	 * For {@link #approxStringLengthInt(int)} and {@link #approxStringLengthLong(int)}. 
	 */
	private static int floorLog2(int v)
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
		int c = (v < 0) ? 1 : 0;
		do
		{
			c++;
			v /= radix;
		} while (v != 0);
		
		return c;
	}

	/**
	 * Exact size of buffer for {@link #getChars(char[], int, long, int)}.
	 */
	static int exactStringLength(long v, int radix)
	{
		int c = (v < 0) ? 1 : 0;
		do
		{
			c++;
			v /= radix;
		} while (v != 0);
		
		return c;
	}
}
