package java.lang;

/**
 * An expandable string of characters. Actually not very expandable!
 * 09/25/2001 added number formatting thanks to Martin E. Nielsen.
 * You must ensure that the buffer is large enough to take the formatted
 * number.
 *<P>
 * @author <a href="mailto:martin@egholm-nielsen.dk">Martin E. Nielsen</a>
 */
public final class StringBuffer
{
	private static final int INITIAL_CAPACITY = 10;
	private static final int CAPACITY_INCREMENT_NUM = 3;	//numerator of the increment factor
	private static final int CAPACITY_INCREMENT_DEN = 2;	//denominator of the increment factor
	private static final int CAPACITY_INCREMENT_MIN = 5;	//minimal increment
	
	private static final char[] buf = new char[16];
	
	private char[] characters;
	private int curLen = 0;
	
	private static int newCapacity(int old)
	{
		//must work for old == 0
		return Math.max(old + CAPACITY_INCREMENT_MIN, old * CAPACITY_INCREMENT_NUM / CAPACITY_INCREMENT_DEN); 
	}
	
	public void ensureCapacity(int minCapacity)
	{
		if (characters.length < minCapacity)
		{
			int newCapacity = newCapacity(characters.length);
			while (newCapacity < minCapacity)
				newCapacity = newCapacity(newCapacity);
			
			char[] newData = new char[newCapacity];
			System.arraycopy(characters, 0, newData, 0, curLen);
			characters = newData;
		}
	}

  /**
   * The value of <i>ln(10)</i> used for converting from base
   * <i>e</i> to base 10.
   **/
  private static final float ln10 = 2.30258509f;

  public StringBuffer () {
  	this(INITIAL_CAPACITY);
  }
  
  public StringBuffer (String aString)
  {
    characters = aString.toCharArray();
    curLen = aString.length();
  }

  public StringBuffer (int length)
  {
    characters = new char[length];
  }

  public StringBuffer delete(int start, int end)
  {
        if (start >= 0 && start < end && start < curLen)
        {
                if (end >= curLen)
                        end = curLen;
                else
                        System.arraycopy(characters, end, characters, start, curLen-end);
                        
                curLen -= end-start;
        }
        
        return this;
  }

  public StringBuffer append (String s)
  {
	  if (s == null)
		  s = "null";
	  
	  return this.appendInternal(s);
  }

  public StringBuffer append (Object aObject)
  {
	  String s = (aObject == null) ? "null" : aObject.toString();
	  return this.appendInternal(s);
  }

  public StringBuffer append (boolean aBoolean)
  {
    return this.appendInternal(String.valueOf(aBoolean));
  }
  
  public StringBuffer append (char aChar)
  {
    return this.appendInternal(String.valueOf(aChar));
  }

  public StringBuffer append (int i)
  {
	  int intLen = WrapperUtils.exactStringLength(i, 10);
	  int newLen = curLen + intLen;
	  ensureCapacity(newLen);

	  WrapperUtils.getChars(buf, newLen, i, 10);	  
	  curLen = newLen;
	  
	  return this;
  }

  public StringBuffer append (long aLong)
  {
	  int intLen = WrapperUtils.exactStringLength(aLong, 10);
	  int newLen = curLen + intLen;
	  ensureCapacity(newLen);

	  WrapperUtils.getChars(buf, newLen, aLong, 10);	  	  
	  curLen = newLen;
	  
	  return this;
  }

  public StringBuffer append (float aFloat)
  {
    try {
        append (aFloat, 8);
    } catch (ArrayIndexOutOfBoundsException e) {
        curLen = Math.min(characters.length, curLen);
    }
    
    return this;
  }

  public StringBuffer append (double aDouble)
  {
    try {
        append ((float)aDouble, 8);
    } catch (ArrayIndexOutOfBoundsException e) {
        curLen = Math.min(characters.length, curLen);
    }
    
    return this;
  }
  
  /**
   * Appends a string with no null checking
   */
  private StringBuffer appendInternal(String s) {
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

  public int lastIndexOf(String str) {
      // Note, synchronization achieved via other invocations
      return lastIndexOf(str, curLen);
  }

  public synchronized int lastIndexOf(String str, int fromIndex) {
      return String.lastIndexOf(characters, 0, curLen,
                            str.characters, 0, str.characters.length, fromIndex);
  }
  
  public String toString()
  {
    return new String (characters, 0, curLen);
  }

  public char charAt(int i)
  {
        return characters[i];
  }
  
  public void setCharAt(int i, char ch)
  {
        characters[i] = ch;
  }
  
  public int length()
  {
        return curLen;
  }

  /**
  * Retrieves the contents of the StringBuffer in the form of an array of characters.
  */
  public char [] getChars()
  {
    return characters;
  }
  
  public synchronized String substring(int start) {
      return substring(start, curLen);
  }

  public synchronized String substring(int start, int end) {
      // THIS SHOULD REALLY THROW StringIndexOutOfBoundsException
	  int len = end - start;
	  return new String(characters, start, len);
  }
  
    /**
     * Helper method for converting floats and doubles.
     *
     * @author Martin E. Nielsen
     **/
    private StringBuffer append( float number, int significantDigits ) {
	  synchronized(buf) {
		int charPos = 0;
		int exponent = 0;
		
		if ( number == 0 ) {
			buf[ charPos++ ] = '0';
		} else {
			if ( number < 0 ) {
				buf[ charPos++ ] = '-';
				number = -number;
			} // if

			// calc. the power (base 10) for the given number:
			int pow = ( int )Math.floor( Math.log( number ) / ln10 );

			// use exponential formatting if number too big or too small
			if ( pow < -3 || pow > 6 ) {
				exponent = pow;
				number /= Math.exp( Math.ln10 * exponent );
			} // if

			// Recalc. the pow if exponent removed and d has changed
			pow = ( int )Math.floor( Math.log( number ) / ln10 );

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
		
		// Do we have enough room?
		int newLen = curLen + charPos;		
		this.ensureCapacity(curLen + charPos);
		
		System.arraycopy(buf, 0, characters, curLen, charPos);
		curLen = newLen;
		
		// Restore the exponential format
		if ( exponent != 0 ) {
			append( exponent );
		} // if
		
		return this;
	  }
    }
}


