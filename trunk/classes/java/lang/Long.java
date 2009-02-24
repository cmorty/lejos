package java.lang;

/**
 * Wrapper class for long integers.
 * @author Sven Köhler
 */
public class Long extends Number implements Comparable
{
	public static final long MAX_VALUE = 0x7FFFFFFFFFFFFFFFL;
	public static final long MIN_VALUE = 0x1000000000000000L;	
	public static final int SIZE = 64;
	
	//MISSING public static final Class TYPE
	//MISSING public Long decode(String nm)
	//MISSING public static getLong(String)
	//MISSING public static getLong(String, long)
	//MISSING public static getLong(String, Long)
	
	private final long value;
	
	public Long(long value)
	{
		this.value = value;
	}
	
	public Long(String s)
	{
		this.value = Long.parseLong(s);
	}
	
	public int bitCount(long v)
	{
		//I didn't understand http://www-graphics.stanford.edu/~seander/bithacks.html#CountBitsSetParallel
		//So I made up my own algorithm
		
		//first sum up every 1st and 2nd bit, the result fill fit into 2 bits each
		v = (v & 0x5555555555555555L)  + ((v >>> 1) & 0x5555555555555555L);
		//then sum up every 1-2nd with every 3-4th
		v = (v & 0x3333333333333333L)  + ((v >>> 2) & 0x3333333333333333L);
		//then sum up every 1-4th with 5-8th bit
		v = (v & 0x0F0F0F0F0F0F0F0FL)  + ((v >>> 4) & 0x0F0F0F0F0F0F0F0FL);
		//at this point, we have a bit counter every 8 bits. Now we just have sum up all of them:
		int i = ((int)v) + ((int)(v >>> 32));
		i += (i >>> 16);
		i += (i >>> 8);
		return i & 0xFF;
	}
	
	public byte byteValue()
	{
		return (byte)this.value;
	}
	
	public int compareTo(Object o)
	{
		Long ob = (Long)o;
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
		return (o instanceof Long)
			&& (this.value == ((Long)o).value);
	}
	
	public float floatValue()
	{
		return this.value;
	}
	
	public int hashCode()
	{
		return ((int)this.value) ^ ((int)(this.value >>> 32)); 
	}
	
	public static long highestOneBit(long v)
	{
		//first set all bits below the highest bit:
		v |= (v >>> 1);
		v |= (v >>> 2);
		v |= (v >>> 4);
		v |= (v >>> 8);
		v |= (v >>> 16);
		v |= (v >>> 32);
		//then substract the lower bits
		return v - (v >>> 1);
	}
	
	public int intValue()
	{
		return (int)this.value;
	}
	
	public long longValue()
	{
		return this.value;
	}
	
	public static long lowestOneBit(long v)
	{
		//if i has the form 11111000 then -i has the form 00001000
		//because -i is actually the same as (~i + 1).
		return v & -v;
	}
	
	public static long numberOfLedingZeros(long v)
	{
		//initialize with one, because we assume that the sign bit is zero.
		//if not, we subtract it again at the end of the method.
		int c = 1;
		
		//use 32 bit int instead of full long
		int i = (int)(v >>> 32);
		//upper 32 bits are zero? add them to counter and take a look at the lower 32 bits
		if (i == 0)
		{
			i = (int)v;
			if (i == 0)
				return 64;
			
			c += 32;
		}
		
		//first 16 bits are zero? add them to counter and remove them by right shift
		if ((i & 0xFFFF0000) == 0) { c += 16; i <<= 16; }
		//first 8 bits are zero? add them to counter and remove them by right shift
		if ((i & 0xFF000000) == 0) { c +=  8; i <<=  8; }
		//first 4 bits are zero? add them to counter and remove them by right shift
		if ((i & 0xF0000000) == 0) { c +=  4; i <<=  4; }
		//first 2 bits are zero? add them to counter and remove them by right shift
		if ((i & 0x30000000) == 0) { c +=  2; i <<=  2; }
		//subtract the sign bit, in case it wasn't zero
		return c - (i >>> 31);
	}
	
	public static long numberOfTrailingZeros(long v)
	{
		//initialize with one, because we assume that the last bit is zero.
		//if not, we subtract it again at the end of the method.
		int c = 1;
		
		//use 32 bit int instead of full long
		int i = (int)v;
		//lower 32 bits are zero? add them to counter and take a look at the upper 32 bits
		if (i == 0)
		{
			i = (int)(v >>> 32);
			if (i == 0)
				return 64;
			
			c += 32;
		}
		
		//last 16 bits are zero? add them to counter and remove them by left shift
		if ((i & 0x0000FFFF) == 0) { c += 16; i >>>= 16; }
		//last 8 bits are zero? add them to counter and remove them by left shift
		if ((i & 0x000000FF) == 0) { c +=  8; i >>>=  8; }
		//last 4 bits are zero? add them to counter and remove them by left shift
		if ((i & 0x0000000F) == 0) { c +=  4; i >>>=  4; }
		//last 2 bits are zero? add them to counter and remove them by left shift
		if ((i & 0x00000003) == 0) { c +=  2; i >>>=  2; }
		//subtract the last bit, in case it wasn't zero
		return c - (i & 1);
	}
	
	public static long parseLong(String s)
	{
		return Long.parseLong(s, 10);
	}
	
	public static long parseLong(String s, int radix)
	{
		//FIXME
		throw new UnsupportedOperationException();
	}
	
	public static long reverse(long v)
	{
		//see http://www-graphics.stanford.edu/~seander/bithacks.html#ReverseParallel
		
		//first swap every 1st and 2nd bit
		v = (v & 0x5555555555555555L) << 1  | ((v >>> 1) & 0x5555555555555555L);
		//then swap every 1st and 2nd with every 3rd and 4th
		v = (v & 0x3333333333333333L) << 2  | ((v >>> 2) & 0x3333333333333333L);
		//then swap every 1,2,3,4th with every 5,6,7,8th
		v = (v & 0x0F0F0F0F0F0F0F0FL) << 4  | ((v >>> 4) & 0x0F0F0F0F0F0F0F0FL);
		//the bits inside each byte have been swapped, now swap the bytes
		return reverseBytes(v); //or instead inline the code here?
	}
	
	public static long reverseBytes(long v)
	{
		//see http://www-graphics.stanford.edu/~seander/bithacks.html#ReverseParallel
		
		//first swap every 1-8th with every 9-16th bit
		v = (v & 0x00FF00FF00FF00FFL) << 8  | ((v >>> 8) & 0x00FF00FF00FF00FFL);
		//then swap every 1-16th with every 17-32nd
		v = (v & 0x0000FFFF0000FFFFL) << 16 | ((v >>> 16) & 0x0000FFFF0000FFFFL);
		//then swap 1-32nd with 33-64th
		return (v  << 32) | (v >>> 32);
	}
	
	public static long rotateLeft(long v, int bits)
	{
		// v >>> -bits is the same as v >>> (64-bits) 
		return (v << bits) | (v >>> -bits);
	}
	
	public static long rotateRight(long v, int bits)
	{
		// v << -bits is the same as v << (64-bits) 
		return (v >>> bits) | (v << -bits);
	}
	
	public short shortValue()
	{
		return (short)this.value;
	}
	
	public static int signnum(long i)
	{
		//If i is negative, then i >> 63 is -1 because of the signed shift
		//and the rest of the term can be ignored because -1 | anything is -1 again.		
		//If i is positive, then i >> 63 will be zero, but (-i >>> 63) will be 1
		//because the result of unsigned shift is the sign bit of -i.
		return ((int)(i >> 63)) | ((int)(-i >>> 63));
	}
	
	public static String toBinaryString(long v)
	{
		//FIXME
		throw new UnsupportedOperationException();
	}
	
	public static String toHexString(long v)
	{
		//FIXME
		throw new UnsupportedOperationException();
	}
	
	public static String toOctalString(long v)
	{
		//FIXME
		throw new UnsupportedOperationException();
	}
	
	public String toString()
	{
		return Long.toString(this.value);
	}
	
	public static String toString(long v)
	{
		return Long.toString(v, 10);
	}
	
	public static String toString(long v, int radix)
	{
		//FIXME
		throw new UnsupportedOperationException();
	}
	
	public static Long valueOf(long v)
	{
		return new Long(v);
	}
	
	public static Long valueOf(String s)
	{
		return Long.valueOf(s, 10);
	}
	
	public static Long valueOf(String s, int radix)
	{
		return Long.valueOf(Long.parseLong(s, radix));
	}
}
