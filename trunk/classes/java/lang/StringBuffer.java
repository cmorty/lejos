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
  char[] characters;
  int curPos = 0;
  static char [] buf = new char[16];
  static String minInt = "-2147483648";

  /**
   * The value of <i>log(10)</i> used for converting from base
   * <i>e</i> to base 10.
   **/
  private static final float log10 = 2.30258509f;

  public StringBuffer () {
  	characters = new char[20];
  }
  
  public StringBuffer (String aString)
  {
    characters = aString.toCharArray();
    curPos = aString.length();
  }

  public StringBuffer (int length)
  {
    characters = new char[length];
  }

  public StringBuffer delete(int start, int end)
  {
        if (start >= 0 && start < end && start < curPos)
        {
                if (end >= curPos)
                        end = curPos;
                else
                        System.arraycopy(characters, end, characters, start, curPos-end);
                        
                curPos -= end-start;
        }
        
        return this;
  }

  public StringBuffer append (String s)
  {
    s = String.valueOf(s);
    return this.appendInternal(s);
  }

  public StringBuffer append (java.lang.Object aObject)
  {
    return this.appendInternal(String.valueOf(aObject));
  }

  public StringBuffer append (boolean aBoolean)
  {
    return this.appendInternal(aBoolean ? "true" : "false");
  }
  
  public StringBuffer append (char aChar)
  {
    return this.appendInternal(new String (new char[] { aChar }, 0, 1));
  }

  public StringBuffer append (int i)
  {
	// Modified to expand the buffer...
	// Conversion code lifted from Integer, could have just called
	// append(Integer.toString(aInt))
	// but that would have allocated a new string for every call. Not sure
	// how good the garbage collector is at the moment, so probably best
	// to preserve the existing memory allocation behavior.
	int q, r, charPos = buf.length; 
	char sign = 0 ; 

	if (i == Integer.MIN_VALUE) return append(minInt);

	if (i < 0) { 
	   sign = '-' ; 
	   i = -i ; 
	}
	synchronized (buf) {
	  for (;;) { 
	    q = i/10; ; 
	    r = i-(q*10) ;
	    buf [--charPos] = (char) ((int) '0' + r) ; 
	    i = q ; 
	    if (i == 0) break ; 
	  }

	  if (sign != 0) {
	    buf [--charPos] = sign ; 
	  }

	  // Will it fit in the existing space?
	  int len = buf.length - charPos;
	  if (len + curPos > characters.length) {
		char [] nc = new char[curPos + len];
		System.arraycopy (characters, 0, nc, 0, curPos);	
		characters = nc;
	  }
	  System.arraycopy(buf, charPos, characters, curPos, len);
	  curPos += len;
	  return this;
	}
  }

  public StringBuffer append (long aLong)
  {
        return this.appendInternal("<longs not supported>");
  }

  public StringBuffer append (float aFloat)
  {
    try {
        append (aFloat, 8);
    } catch (ArrayIndexOutOfBoundsException e) {
        curPos = Math.min(characters.length, curPos);
    }
    
    return this;
  }

  public StringBuffer append (double aDouble)
  {
    try {
        append ((float)aDouble, 8);
    } catch (ArrayIndexOutOfBoundsException e) {
        curPos = Math.min(characters.length, curPos);
    }
    
    return this;
  }
  
  /**
   * Appends a string with no null checking
   */
  private StringBuffer appendInternal(String s) {
    // Reminder: compact code more important than speed
    char[] sc = s.toCharArray();
    int cl = characters.length;
    int sl = sc.length;
    char [] nc = characters;
    if (sl + curPos > cl)
    {
        nc = new char[sl + curPos];
        System.arraycopy (characters, 0, nc, 0, curPos);
    }
    System.arraycopy (sc, 0, nc, curPos, sl);
    characters = nc;
    curPos += sl;
    
    return this;
  }
  
  public int indexOf(String str) {
      return indexOf(str, 0);
  }

  public synchronized int indexOf(String str, int fromIndex) {
      return String.indexOf(characters, 0, curPos,
                            str.toCharArray(), 0, str.length(), fromIndex);
  }

  public int lastIndexOf(String str) {
      // Note, synchronization achieved via other invocations
      return lastIndexOf(str, curPos);
  }

  public synchronized int lastIndexOf(String str, int fromIndex) {
      return String.lastIndexOf(characters, 0, curPos,
                            str.toCharArray(), 0, str.length(), fromIndex);
  }
  
  public String toString()
  {
    return new String (characters, 0, curPos);
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
        return curPos;
  }

  /**
  * Retrieves the contents of the StringBuffer in the form of an array of characters.
  */
  public char [] getChars()
  {
    return characters;
  }
  
  public synchronized String substring(int start) {
      return substring(start, curPos);
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
		if ( number == 0 ) {
			buf[ charPos++ ] = '0';
		} else {
			if ( number < 0 ) {
				buf[ charPos++ ] = '-';
				number = -number;
			} // if

			// calc. the power (base 10) for the given number:
			int pow = ( int )Math.floor( Math.log( number ) / log10 );
			int exponent = 0;

			// use exponential formatting if number too big or too small
			if ( pow < -3 || pow > 6 ) {
				exponent = pow;
				number /= Math.exp( Math.ln10 * exponent );
			} // if

			// Recalc. the pow if exponent removed and d has changed
			pow = ( int )Math.floor( Math.log( number ) / log10 );

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
			// Do we have enough room?
			if (charPos + curPos > characters.length) {
				  char [] nc = new char[curPos + charPos];
				  System.arraycopy (characters, 0, nc, 0, curPos);	
				  characters = nc;
			}
			System.arraycopy(buf, 0, characters, curPos, charPos);
			curPos += charPos;
			// Restore the exponential format
			if ( exponent != 0 ) {
				append( exponent );
			} // if
		}
		return this;
	  }
    }
}


