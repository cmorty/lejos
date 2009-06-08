package java.lang;

/**
 * An expandable string of characters. Actually not very expandable!
 * 09/25/2001 added number formatting thanks to Martin E. Nielsen.
 * You must ensure that the buffer is large enough to take the formatted
 * number.
 *<P>
 * @author <a href="mailto:martin@egholm-nielsen.dk">Martin E. Nielsen</a>
 * @author Sven Köhler
 */
public final class StringBuffer
{
	private static final int INITIAL_CAPACITY = 10;
	private static final int CAPACITY_INCREMENT_NUM = 3;	//numerator of the increment factor
	private static final int CAPACITY_INCREMENT_DEN = 2;	//denominator of the increment factor
	
	private static final char[] buf = new char[32];
	
	private char[] characters;
	private int curLen = 0;
	
	public void ensureCapacity(int minCapacity)
	{
		int cl = characters.length;
		if (cl < minCapacity)
		{
			cl = cl * CAPACITY_INCREMENT_NUM / CAPACITY_INCREMENT_DEN + 1;
			while (cl < minCapacity)
				cl = cl * CAPACITY_INCREMENT_NUM / CAPACITY_INCREMENT_DEN + 1;
			
			char[] newData = new char[cl];
			System.arraycopy(characters, 0, newData, 0, curLen);
			characters = newData;
		}
	}

  public StringBuffer ()
  {
    characters = new char[INITIAL_CAPACITY];
  }
  
  public StringBuffer (String aString)
  {
    characters = aString.toCharArray();
    curLen = aString.length();
  }

  public StringBuffer (int length)
  {
    if (length < 0)
    	throw new NegativeArraySizeException("length is negative");
    
    characters = new char[length];
  }

  public synchronized StringBuffer delete(int start, int end)
  {
	  if (start < 0 || start > curLen)
		  throw new StringIndexOutOfBoundsException(start);
	  if (end < start)
		  throw new StringIndexOutOfBoundsException();
	  if (end > curLen)
		  end = curLen;
	  
      System.arraycopy(characters, end, characters, start, curLen - end);
      curLen -= end - start;
      
      return this;
  }

  public StringBuffer append (String s)
  {
	  return this.appendInternal(s);
  }

  public StringBuffer append (Object aObject)
  {
	  return this.appendInternal(String.valueOf(aObject));
  }

  public StringBuffer append (boolean aBoolean)
  {
    return this.appendInternal(String.valueOf(aBoolean));
  }
  
  public synchronized StringBuffer append (char aChar)
  {
	  int newLen = curLen +1;
	  ensureCapacity(newLen);
	  
	  characters[curLen] = aChar;
	  curLen = newLen;
	  
	  return this;
  }

	public StringBuffer append(char[] c)
	{
		return this.append(c, 0, c.length);
	}
	
	public synchronized StringBuffer append(char[] c, int off, int len)
	{
		int newLen = curLen + len;
		ensureCapacity(newLen);
	  
		for (int i=0; i<len; i++)
			characters[curLen + i] = c[off + i];		
		curLen = newLen;	  
		
		return this;
	}

	public StringBuffer append(CharSequence cs)
	{
		return this.append(cs, 0, cs.length());
	}
	
	public synchronized StringBuffer append(CharSequence cs, int start, int end)
	{
		int len = end - start;
		int newLen = curLen + len;
		ensureCapacity(newLen);
	  
		for (int i=0; i<len; i++)
			characters[curLen + i] = cs.charAt(start + i);		
		curLen = newLen;	  
		
		return this;
	}

  public synchronized StringBuffer append (int i)
  {
	  int intLen = StringUtils.exactStringLength(i, 10);
	  int newLen = curLen + intLen;
	  ensureCapacity(newLen);

	  StringUtils.getChars(characters, newLen, i, 10);	  
	  curLen = newLen;
	  
	  return this;
  }

  public synchronized StringBuffer append (long aLong)
  {
	  int intLen = StringUtils.exactStringLength(aLong, 10);
	  int newLen = curLen + intLen;
	  ensureCapacity(newLen);

	  StringUtils.getChars(characters, newLen, aLong, 10);	  	  
	  curLen = newLen;
	  
	  return this;
  }

  public StringBuffer append (float aFloat)
  {
    append (aFloat, 8);
    return this;
  }

  public StringBuffer append (double aDouble)
  {
    append (aDouble, 17);
    return this;
  }
  
  /**
   * Appends a string with no null checking
   */
  private StringBuffer appendInternal(String s) {
	  if (s == null)
		  s = "null";
	  
    // Reminder: compact code more important than speed
    char[] sc = s.characters;
    int sl = sc.length;
    
    int newlen = curLen + sl;
    this.ensureCapacity(newlen);
    
    System.arraycopy (sc, 0, characters, curLen, sl);    
    curLen = newlen;
    
    return this;
  }
  
  public int indexOf(String str) {
      return indexOf(str, 0);
  }

  public synchronized int indexOf(String str, int fromIndex) {
      return String.indexOf(characters, 0, curLen,
                            str.characters, 0, str.characters.length, fromIndex);
  }

  public synchronized int lastIndexOf(String str) {
      // Note, synchronization achieved via other invocations
      return lastIndexOf(str, curLen);
  }

  public synchronized int lastIndexOf(String str, int fromIndex) {
      return String.lastIndexOf(characters, 0, curLen,
                            str.characters, 0, str.characters.length, fromIndex);
  }
  
  @Override
  public synchronized String toString()
  {
    return new String (characters, 0, curLen);
  }

  public synchronized char charAt(int i)
  {
	  if (i < 0 || i >= curLen)
		  throw new StringIndexOutOfBoundsException(i);
	  
        return characters[i];
  }
  
  public synchronized void setCharAt(int i, char ch)
  {
	  if (i < 0 || i >= curLen)
		  throw new StringIndexOutOfBoundsException(i);
	  
        characters[i] = ch;
  }
  
  public synchronized int length()
  {
        return curLen;
  }

  /**
  * Retrieves the contents of the StringBuffer in the form of an array of characters.
  */
  public synchronized char[] getChars()
  {
    char[] r = new char[curLen];
    System.arraycopy(characters, 0, r, 0, curLen);
    return r;
  }
  
  public synchronized String substring(int start) {
      return substring(start, curLen);
  }

  public String substring(int start, int end) {
	  if (start < 0 || start > curLen)
		  throw new StringIndexOutOfBoundsException(start);
	  if (end > curLen)
		  throw new StringIndexOutOfBoundsException(end);
	  if (end < start)
		  throw new StringIndexOutOfBoundsException(end - start);
	  
	  int len = end - start;
	  return new String(characters, start, len);
  }
  
    /**
     * Helper method for converting floats and doubles.
     *
     * @author Martin E. Nielsen
     **/
    private StringBuffer append( double number, int significantDigits ) {
	  synchronized(buf) {
		int charPos = 0;
		int exponent = 0;
		
		//we need to detect -0.0 to be compatible with JDK
		boolean negative = (Double.doubleToRawLongBits(number) & 0x8000000000000000L) != 0;
		if (negative)
		{
			buf[ charPos++ ] = '-';
			number = -number;
		}			
		
		if ( number == 0 ) {
			buf[ charPos++ ] = '0';
			buf[ charPos++ ] = '.';
			buf[ charPos++ ] = '0';
		} else {
			// calc. the power (base 10) for the given number:
			int pow = ( int )Math.floor( Math.log( number ) / Math.ln10 );

			// use exponential formatting if number too big or too small
			if ( pow < -3 || pow > 6 ) {
				exponent = pow;
				number /= Math.exp( Math.ln10 * exponent );
			} // if

			// Recalc. the pow if exponent removed and d has changed
			pow = ( int )Math.floor( Math.log( number ) / Math.ln10 );

			// Decide how many insignificant zeros there will be in the
			// lead of the number.
			int insignificantDigits = -Math.min( 0, pow );

			// Force it to start with at least "0." if necessarry
			pow = Math.max( 0, pow );
				double divisor = Math.pow(10, pow);

			// Loop over the significant digits (17 for double, 8 for float)
			for ( int i = 0, end = significantDigits+insignificantDigits, div; i < end; i++  ) {

				// Add the '.' when passing from 10^0 to 10^-1
				if ( pow == -1 ) {
					buf[ charPos++ ] = '.';
				} // if

				// Find the divisor
				div = ( int ) ( number / divisor );
				// This might happen with 1e6: pow = 5 ( instead of 6 )
				if ( div == 10 ) {
					buf[ charPos++ ] = '1';
					buf[ charPos++ ] = '0';
				} // if
				else {
					buf[ charPos ] = (char)(div + '0');
					charPos++;
				} // else

				number -= div * divisor;
				divisor /= 10.0;
				pow--;

				// Break the loop if we have passed the '.'
				if ( number == 0 && divisor < 0.1 ) break;
			} // for

			// Remove trailing zeros
			while ( buf[ charPos-1 ] == '0' )
				charPos--;

			// Avoid "4." instead of "4.0"
			if ( buf[ charPos-1 ] == '.' )
				charPos++;
			if ( exponent != 0 ) {
				buf[ charPos++ ] = 'E';
			} // if
		}
		
	synchronized (this)	{
		// Do we have enough room?
		int newLen = curLen + charPos;
		this.ensureCapacity(newLen);
		
		System.arraycopy(buf, 0, characters, curLen, charPos);
		curLen = newLen;
		
		// Restore the exponential format
		if ( exponent != 0 ) {
			append( exponent );
		} // if
	  }
	}

      return this;
    }
}


