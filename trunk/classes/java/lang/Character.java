package java.lang;

public final class Character implements Comparable<Character>
{
    public static final int MIN_RADIX = 2;
    public static final int MAX_RADIX = 36;
    
    public static final char MIN_LOW_SURROGATE = '\uDC00';
    public static final char MAX_LOW_SURROGATE = '\uDFFF';
    public static final char MIN_HIGH_SURROGATE = '\uD800';
    public static final char MAX_HIGH_SURROGATE = '\uDBFF';
    
    public static final int MIN_SUPPLEMENTARY_CODE_POINT = 0x10000;
	
	//MISSING everything
    
    private final char value;
    
    public Character(char c)
    {
    	this.value = c;
    }
    
    public char charValue()
    {
    	return this.charValue();
    }
    
	public int compareTo(Character ob)
	{
		if (this.value == ob.value)
			return 0;
		
		return (this.value > ob.value) ? 1 : -1;
	}
	
	/**
	 * Identifies if a character is a numerical digit. 
	 * 
	 * @param ch the character
	 * @return true iff the character is a numerical digit
	 */
	public static boolean isDigit(char ch) {
		return (ch >= 48 & ch<=57);
	}
	
	public static boolean isLowSurrogate(char ch)
	{
		return ch >= MIN_LOW_SURROGATE && ch <= MAX_LOW_SURROGATE;
	}
		
	public static boolean isHighSurrogate(char ch)
	{
		return ch >= MIN_HIGH_SURROGATE && ch <= MAX_HIGH_SURROGATE;
	}
	
	public static boolean isSurrogatePair(char high, char low)
	{
		return isHighSurrogate(high) && isLowSurrogate(low);
	}
	
	public static int toCodePoint(char high, char low)
	{
		//no validating required, says JDK javadocs
		//TODO does the compiler really inline and merge the constants?
		return high << 10 + low - (MIN_HIGH_SURROGATE << 10) - MIN_LOW_SURROGATE + MIN_SUPPLEMENTARY_CODE_POINT;
	}
		
	/**
	 * This method accepts a character such as '7' or 'C' and converts
	 * it to a number value. So '7' returns 7 and 'C' returns 12,
	 * which is the hexidecimal value of C. You must specify a radix
	 * for the number system. If the digit is not defined for the specified
	 * radix or the radix is invalid, -1 is returned.
	 * 
	 * @param ch the character
	 * @param radix the radix
	 * @return the numerical value of the digit
	 */
	public static int digit(char ch, int radix)
	{
		return digit((int)ch, radix);
	}

	public static int digit(int ch, int radix)
	{
		//MISSING only handles latin1 for now
		
		if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
			return -1;
		
		if (ch >= '0' && ch <= '9')
			ch -= '0';
		else if (ch >= 'a' && ch <= 'z')
			ch -= 'a'  - 10;
		else if (ch >= 'A' && ch <= 'Z')
			ch -= 'A'  - 10;
		else
			return -1;
		
		if (ch >= radix)	
			return -1;
		
		return ch;
	}	

	@Override
	public boolean equals(Object o)
	{
		//instanceof returns false for o==null
		return (o instanceof Character)
			&& (this.value == ((Character)o).value);
	}
	
	public static char forDigit(int digit, int radix)
	{
		if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
			return '\u0000';
		if (digit < 0 || digit >= radix)
			return '\u0000';
		
		char r;
		if (digit < 10)
			r = (char)(digit + '0');
		else
			r = (char)(digit + ('a' - 10));
		
		return r;
	}
	
	@Override
	public int hashCode()
	{
		return this.value;
	}

	@Override
	public String toString()
	{
		return Character.toString(this.value);
	}
	
	public static String toString(char c)
	{
		return String.valueOf(c);
	}
	
	public static Character valueOf(char c)
	{
		return new Character(c);
	}
}
