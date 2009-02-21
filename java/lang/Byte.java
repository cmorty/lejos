package java.lang;

/**
 * Wrapper class for bytes.
 * @author Sven Köhler
 */
public final class Byte extends Number
{
	public static final byte MAX_VALUE = 127;
	public static final byte MIN_VALUE = -128;
	public static final int SIZE = 8;
	
	//MISSING public static final Class TYPE
	
	
	private final byte value;
	
	public Byte(byte value)
	{
		this.value = value;
	}
	
	public Byte(String s)
	{
		this(parseByte(s));
	}

	public byte byteValue()
	{
		return this.value;
	}
	
	public int compareTo(Object o)
	{
		Byte ob = (Byte)o;
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
		return (o instanceof Byte)
			&& (this.value == ((Byte)o).value);
	}

	public float floatValue()
	{
		return this.value;
	}
	
	public int hashCode()
	{
		return this.value;
	}

	public int intValue()
	{
		return this.value;
	}

	public long longValue()
	{
		return this.value;
	}
	
	public static byte parseByte(String s) throws NumberFormatException
	{
		return Byte.parseByte(s, 10);
	}
	
	public static byte parseByte(String s, int radix) throws NumberFormatException
	{
		int tmp = Integer.parseInt(s, radix);
		if (tmp < Byte.MIN_VALUE || tmp > Byte.MAX_VALUE)
			throw new NumberFormatException("number is too big");
		
		return (byte)tmp;
	}

	public short shortValue()
	{
		return this.value;
	}
	
	public String toString()
	{
		return Byte.toString(this.value);
	}
	
	public static String toString(byte b)
	{
		return Integer.toString(b);
	}
	
	public static Byte valueOf(byte b)
	{
		return new Byte(b);
	}
	
	public static Byte valueOf(String s)
	{
		return Byte.valueOf(s, 10);
	}
	
	public static Byte valueOf(String s, int radix)
	{
		return Byte.valueOf(Byte.parseByte(s, radix));
	}
}
