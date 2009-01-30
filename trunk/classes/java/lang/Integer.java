package java.lang;

/**
 * Minimal Integer implementation that supports converting an int to a String.
 *
 */

  public final class Integer {
    /**
     * The smallest value of type <code>int</code>. The constant 
     * value of this field is <tt>-2147483648</tt>.
     */
    public static final int   MIN_VALUE = 0x80000000;

    /**
     * The largest value of type <code>int</code>. The constant 
     * value of this field is <tt>2147483647</tt>.
     */
    public static final int   MAX_VALUE = 0x7fffffff;

    /**
     * Normally this is stored in Character in the standard 
     * java.lang package but we don't have Character.
     */
    public static final int MIN_RADIX = 2;
    
    /**
     * Normally this is stored in Character in the standard 
     * java.lang package but we don't have Character.
     */
    public static final int MAX_RADIX = 36;
    
    private static int LOWER_LIMIT = '0'; // '0' value = 48
	private static int UPPER_LIMIT = '9'; // '9' value = 57
	
	private static int LOWER_CHAR = 'A'; 
	private static int UPPER_CHAR = 'Z'; 
	
    private static final char[] hexDigits = {'0','1','2','3','4','5','6','7', 
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * The value of the Integer.
     *
     * @serial
     */
    private int value;

    /**
     * Constructs a newly allocated <code>Integer</code> object that
     * represents the primitive <code>int</code> argument.
     *
     * @param   value   the value to be represented by the <code>Integer</code>.
     */
    public Integer(int value) {
	this.value = value;
    }
/**
 * returns the value of this Integer as int
 * @return the int value represented by this object.
 */
    public int intValue()
    {
       return value;
    }
    static char buf [] = new char [12] ; 

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
			throws NumberFormatException {
		if (s == null) {
			throw new NumberFormatException();
			// throw new NumberFormatException("null");
		}

		if (radix < Integer.MIN_RADIX) {
			throw new NumberFormatException();
			// throw new NumberFormatException("radix " + radix
			// + " less than Character.MIN_RADIX");
		}

		if (radix > Integer.MAX_RADIX) {
			throw new NumberFormatException();
			// throw new NumberFormatException("radix " + radix
			// + " greater than Character.MAX_RADIX");
		}

		int result = 0;
		boolean negative = false;
		int i = 0, len = s.length();
		int limit = -Integer.MAX_VALUE;
		int multmin;
		int digit;

		if (len > 0) {
			char firstChar = s.charAt(0);
			if (firstChar < '0') { // Possible leading "+" or "-"
				if (firstChar == '-') {
					negative = true;
					limit = Integer.MIN_VALUE;
				} else if (firstChar != '+')
					throw new NumberFormatException();
				// throw NumberFormatException.forInputString(s);

				if (len == 1) // Cannot have lone "+" or "-"
					throw new NumberFormatException();
				// throw NumberFormatException.forInputString(s);
				i++;
			}
			multmin = limit / radix;
			while (i < len) {
				// Accumulating negatively avoids surprises near MAX_VALUE
				digit = digit(s.charAt(i++), radix);
				if (digit < 0) {
					throw new NumberFormatException();
					// throw NumberFormatException.forInputString(s);
				}
				if (result < multmin) {
					throw new NumberFormatException();
					// throw NumberFormatException.forInputString(s);
				}
				result *= radix;
				if (result < limit + digit) {
					throw new NumberFormatException();
					// throw NumberFormatException.forInputString(s);
				}
				result -= digit;
			}
		} else {
			throw new NumberFormatException();
			// throw NumberFormatException.forInputString(s);
		}
		return negative ? result : -result; // !! Is this backwards?
	}

	public static int parseInt(String s) throws NumberFormatException {
		return parseInt(s, 10);
	}
	
	/**
	 * This method accepts a character such as '7' or 'C' and converts
	 * it to a number value. So '7' returns 7 and 'C' returns 12,
	 * which is the hexidecimal value of C. You must specify a radix
	 * for the number system, and if your character is outside the
	 * bounds it throws a NumberFormatException. e.g. 'G' with radix
	 * of 16 will throw the exception.
	 * 
	 * Normally this method is in Character but we don't have that class.
	 * If we ever make one we can move this method to it.
	 * @param ch
	 * @param radix
	 * @return the digit
	 */
	public static int digit(char ch, int radix) {
		return digit((int)ch, radix);
	}

	public static int digit(int codePoint, int radix) {
		int val = codePoint;
		
		// Handle decimal numbers
		if(codePoint >= LOWER_LIMIT & codePoint <= UPPER_LIMIT) {
			val = val - LOWER_LIMIT;
		} else
		// Handle characters like A, B, etc...
		if(codePoint >= LOWER_CHAR & codePoint <= UPPER_CHAR) {
			val = (val - LOWER_CHAR) + 10;
		}
		
		if(val < radix&val >=0)	
			return val;
		else
			throw new NumberFormatException();
	}    
    /**
     * Returns a new String object representing the specified integer. The 
     * argument is converted to signed decimal representation and returned 
     * as a string, exactly as if the argument and radix <tt>10</tt> were 
     * given as arguments to the toString(int, int) method.
     *
     * @param   i   an integer to be converted.
     * @return  a string representation of the argument in base&nbsp;10.
     */
    public static String toString(int i) {
	  synchronized(buf) {
       int q, r, charPos = 12; 
       char sign = 0 ; 

       if (i == Integer.MIN_VALUE) return "-2147483648";

       if (i < 0) { 
          sign = '-' ; 
          i = -i ; 
       }

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

       return new String ( buf, charPos, 12 - charPos) ; 
     }
   }

   /**
    * Returns a String object representing this Integer's value. The 
    * value is converted to signed decimal representation and returned 
    * as a string.
    *
    * @return  a string representation of the value of this object in
    *          base&nbsp;10.
    */
    public String toString() {
    	return toString(value);
    }
    
    /**
     * Return the hex representation of an int as a String
     * @param i the int
     * @return the hex string
     */
    public static String toHexString(int i) {
        char[] buf = new char[8];
        int pos = 8;
        do {
            buf[--pos] = hexDigits[i & 0xF];
            i >>>= 4;
        } while (i != 0);

        return new String(buf, pos, (8 - pos));
    }
}

   
              