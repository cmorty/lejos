package java.lang;

/**
 * Minimal Integer implementation that supports converting an int to a String.
 * @author others
 * @author Sven Köhler
 */
public final class Integer extends Number implements Comparable<Integer>
{
	/**
	 * The largest value of type <code>int</code>. The constant 
	 * value of this field is <tt>2147483647</tt>.
	 */
	public static final int   MAX_VALUE = 0x7fffffff;

	/**
	 * The smallest value of type <code>int</code>. The constant 
	 * value of this field is <tt>-2147483648</tt>.
	 */
	public static final int   MIN_VALUE = 0x80000000;

	public static final int SIZE = 32;
	
	//MISSING public static final Class TYPE
	//MISSING public static Integer decode(String nm)
	//MISSING public static Integer getInteger(String)
	//MISSING public static Integer getInteger(String, int)
	//MISSING public static Integer getInteger(String, Integer)
	
	/**
	 * The value of the Integer.
	 *
	 * @serial
	 */
	private final int value;

	/**
	 * Constructs a newly allocated <code>Integer</code> object that
	 * represents the primitive <code>int</code> argument.
	 *
	 * @param   value   the value to be represented by the <code>Integer</code>.
	 */
	public Integer(int value)
	{
		this.value = value;
	}
	
	public Integer(String s)
	{
		this.value = Integer.parseInt(s);
	}
	
	public int bitCount(int v)
	{
		//See http://www-graphics.stanford.edu/~seander/bithacks.html#CountBitsSetParallel
		
		//first sum up every 1st and 2nd bit, the result are 2bit counters
		//but do it with some nice trick:
		//  11 - (11 >> 1) = 10
		//  10 - (10 >> 1) = 01
		//  01 - (01 >> 1) = 01
		//  00 - (00 >> 1) = 00
		v = v - ((v >>> 1) & 0x55555555);		
		//then sum up every 1st and 2nd of the 2-bit counters => 4bit counters
		v = (v & 0x33333333)  + ((v >>> 2) & 0x33333333);
		//then sum up every i-th and (i+1)-th of the 4bit counters and throw away some of them
		v = (v + (v >>> 4)) & 0x0F0F0F0F;
		//at this point, we have 8bit counters. now we just need to sum them up:
		v += (v >>> 16);
		v += (v >>> 8);
		return v & 0xFF;
	}
	
	public byte byteValue()
	{
		return (byte)this.value;
	}
	
	public int compareTo(Integer ob)
	{
		if (this.value == ob.value)
			return 0;
		
		return (this.value > ob.value) ? 1 : -1;
	}
	
	public double doubleValue()
	{
		return this.value;
	}
	
	public boolean equals(Object o)
	{
		//instanceof returns false for o==null
		return (o instanceof Integer)
			&& (this.value == ((Integer)o).value);
	}
	
	public float floatValue()
	{
		return this.value;
	}
	
	public int hashCode()
	{
		return this.value;
	}
	
	public static int highestOneBit(int v)
	{
		//first set all bits below the highest bit:
		v |= (v >>> 1);
		v |= (v >>> 2);
		v |= (v >>> 4);
		v |= (v >>> 8);
		v |= (v >>> 16);
		//then substract the lower bits
		return v - (v >>> 1);
	}
	
	/**
	 * returns the value of this Integer as int
	 * @return the int value represented by this object.
	 */
	public int intValue()
	{
	   return this.value;
	}

	public long longValue()
	{
		return this.value;
	}
	
	public static int lowestOneBit(int v)
	{
		//if i has the form 11111000 then -i has the form 00001000
		//because -i is actually the same as (~i + 1).
		return v & -v;
	}
	
	public static int numberOfLedingZeros(int v)
	{
		if (v == 0)
			return 32;
		
		//initialize with one, because we assume that the sign bit is zero.
		//if not, we subtract it again at the end of the method.
		int c = 1;
		
		//first 16 bits are zero? add them to counter and remove them by right shift
		if ((v & 0xFFFF0000) == 0) { c += 16; v <<= 16; }
		//first 8 bits are zero? add them to counter and remove them by right shift
		if ((v & 0xFF000000) == 0) { c +=  8; v <<=  8; }
		//first 4 bits are zero? add them to counter and remove them by right shift
		if ((v & 0xF0000000) == 0) { c +=  4; v <<=  4; }
		//first 2 bits are zero? add them to counter and remove them by right shift
		if ((v & 0x30000000) == 0) { c +=  2; v <<=  2; }
		//subtract the sign bit, in case it wasn't zero
		return c - (v >>> 31);
	}
	
	public static int numberOfTrailingZeros(int v)
	{
		if (v == 0)
			return 32;
		
		//initialize with one, because we assume that the last bit is zero.
		//if not, we subtract it again at the end of the method.
		int c = 1;
		
		//last 16 bits are zero? add them to counter and remove them by left shift
		if ((v & 0x0000FFFF) == 0) { c += 16; v >>>= 16; }
		//last 8 bits are zero? add them to counter and remove them by left shift
		if ((v & 0x000000FF) == 0) { c +=  8; v >>>=  8; }
		//last 4 bits are zero? add them to counter and remove them by left shift
		if ((v & 0x0000000F) == 0) { c +=  4; v >>>=  4; }
		//last 2 bits are zero? add them to counter and remove them by left shift
		if ((v & 0x00000003) == 0) { c +=  2; v >>>=  2; }
		//subtract the last bit, in case it wasn't zero
		return c - (v & 1);
	}
	
	public static int parseInt(String s)
	{
		return Integer.parseInt(s, 10);
	}
	
	/**
	 * This method parses an int from a String. The string can
	 * contain letters if the number system is larger than decimal,
	 * such as hexidecimal numbers.
	 * 
	 * @param s The number string e.g. "123" or "FF" if radix is 16.
	 * @param radix The base number system e.g. 16
	 * @return the integer value
	 * @throws NumberFormatException
	 */
	public static int parseInt(String s, int radix)
	{
		WrapperUtils.throwNumberFormat(s, radix);
		
		int len = s.length();
				
		int p;
		int limit;
		boolean negative;
		
		if (len > 0 && s.charAt(0) == '-')
		{
			p = 1;
			limit = MIN_VALUE;
			negative = true;
		}
		else
		{
			p = 0;
			limit = -MAX_VALUE;
			negative = false;
		}
				
		if (len <= p)
			throw new NumberFormatException("string doesn't contain any digits");
		
		int multlimit = limit / radix;
		
		int r = 0;
		while (p < len)
		{
			int digit = WrapperUtils.parseDigit(s.charAt(p++), radix);
			
			if (r < multlimit)
				throw new NumberFormatException("number is too big");			
			r *= radix;
			
			if (r < limit + digit)
				throw new NumberFormatException("number is too big");			
			r -= digit;
		}

		//r is always <= 0, because the negative space is bigger than the positive space
		return negative ? r : -r;
	}

	public static int reverse(int v)
	{
		//see http://www-graphics.stanford.edu/~seander/bithacks.html#ReverseParallel
		
		//first swap every 1st and 2nd bit
		v = (v & 0x55555555) << 1  | ((v >>> 1) & 0x55555555);
		//then swap every 1st and 2nd with every 3rd and 4th
		v = (v & 0x33333333) << 2  | ((v >>> 2) & 0x33333333);
		//then swap every 1,2,3,4th with every 5,6,7,8th
		v = (v & 0x0F0F0F0F) << 4  | ((v >>> 4) & 0x0F0F0F0F);
		//the bits inside each byte have been swapped, now swap the bytes
		return reverseBytes(v); //or instead inline the code here?
	}
	
	public static int reverseBytes(int v)
	{
		//see http://www-graphics.stanford.edu/~seander/bithacks.html#ReverseParallel
		
		//first swap every 1-8th with every 9-16th bit
		v = (v & 0x00FF00FF) << 8  | ((v >>> 8) & 0x00FF00FF);
		//then swap 1-16th with 17-32nd
		return (v  << 16) | (v >>> 16);
	}
	
	public static int rotateLeft(int v, int bits)
	{
		// v >>> -bits is the same as v >>> (32-bits) 
		return (v << bits) | (v >>> -bits);
	}
	
	public static int rotateRight(int v, int bits)
	{
		// v << -bits is the same as v << (32-bits) 
		return (v >>> bits) | (v << -bits);
	}
	
	public short shortValue()
	{
		return (short)this.value;
	}
	
	public static int signnum(long i)
	{
		//If i is negative, then i >> 31 is -1 because of the signed shift
		//and the rest of the term can be ignored because -1 | anything is -1 again.		
		//If i is positive, then i >> 31 will be zero, but (-i >>> 31) will be 1
		//because the result of unsigned shift is the sign bit of -i.
		return ((int)(i >> 31)) | ((int)(-i >>> 31));
	}
	
	public static String toBinaryString(int v)
	{
		return toUnsignedString(v, 32, 1, 1);
	}
	
	/**
	 * Return the hex representation of an int as a String
	 * @param v the int
	 * @return the hex string
	 */
	public static String toHexString(int v)
	{
		return toUnsignedString(v, 8, 15, 4);
	}
	
	public static String toOctalString(int v)
	{
		return toUnsignedString(v, 11, 7, 3);
	}
	
	private static String toUnsignedString(int v, int maxlen, int mask, int shift)
	{
		char[] buf = new char[maxlen];
		int p = maxlen;
		
		do
		{
			buf[--p] = Character.forDigit(mask & v, 16);
			v >>>= shift;
		} while (v != 0);
		
		return new String(buf, p, maxlen-p);
	}
	
	/**
	 * Returns a String object representing this Integer's value. The 
	 * value is converted to signed decimal representation and returned 
	 * as a string.
	 *
	 * @return  a string representation of the value of this object in
	 *		  base&nbsp;10.
	 */
	public String toString()
	{
		return Integer.toString(this.value);
	}
	
	/**
	 * Returns a new String object representing the specified integer. The 
	 * argument is converted to signed decimal representation and returned 
	 * as a string, exactly as if the argument and radix <tt>10</tt> were 
	 * given as arguments to the toString(int, int) method.
	 *
	 * @param   v   an integer to be converted.
	 * @return  a string representation of the argument in base&nbsp;10.
	 */
	public static String toString(int v)
	{
		return Integer.toString(v, 10);
	}
	
	public static String toString(int v, int radix)
	{
		radix = WrapperUtils.invalidRadixTo10(radix);
		
		int len = WrapperUtils.exactStringLength(v, radix);		
		char[] buf = new char[len];
		
		WrapperUtils.getChars(buf, len, v, radix);		
		return new String(buf);
	}
	
	public static Integer valueOf(int v)
	{
		return new Integer(v);
	}
	
	public static Integer valueOf(String s)
	{
		return Integer.valueOf(s, 10);
	}
	
	public static Integer valueOf(String s, int radix)
	{
		return Integer.valueOf(Integer.parseInt(s, radix));
	}
	
	/**
	 * @deprecated use constant in {@link Character}
	 */
	public static final int MIN_RADIX = Character.MIN_RADIX;
	
	/**
	 * @deprecated use constant in {@link Character}
	 */
	public static final int MAX_RADIX = Character.MAX_RADIX;
	
	/**
	 * @deprecated use {@link Character#digit(char, int)} instead.
	 */
	public static int digit(char ch, int radix)
	{
		return Character.digit((int)ch, radix);
	}

	/**
	 * @deprecated use {@link Character#digit(int, int)} instead.
	 */
	public static int digit(int ch, int radix)
	{
		return Character.digit(ch, radix);
	}	
}
