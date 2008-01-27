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

    static char buf [] = new char [12] ; 

    public static int parseInt(String s, int radix)
			throws NumberFormatException {
		if (s == null) {
			throw new NumberFormatException();
			//throw new NumberFormatException("null");
		}

		if (radix < Character.MIN_RADIX) {
			throw new NumberFormatException();
			//throw new NumberFormatException("radix " + radix
			//		+ " less than Character.MIN_RADIX");
		}

		if (radix > Character.MAX_RADIX) {
			throw new NumberFormatException();
			//throw new NumberFormatException("radix " + radix
			//		+ " greater than Character.MAX_RADIX");
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
					//throw NumberFormatException.forInputString(s);

				if (len == 1) // Cannot have lone "+" or "-"
					throw new NumberFormatException();
					//throw NumberFormatException.forInputString(s);
				i++;
			}
			multmin = limit / radix;
			while (i < len) {
				// Accumulating negatively avoids surprises near MAX_VALUE
				digit = Character.digit(s.charAt(i++), radix);
				if (digit < 0) {
					throw new NumberFormatException();
					//throw NumberFormatException.forInputString(s);
				}
				if (result < multmin) {
					throw new NumberFormatException();
					//throw NumberFormatException.forInputString(s);
				}
				result *= radix;
				if (result < limit + digit) {
					throw new NumberFormatException();
					//throw NumberFormatException.forInputString(s);
				}
				result -= digit;
			}
		} else {
			throw new NumberFormatException();
			//throw NumberFormatException.forInputString(s);
		}
		return negative ? result : -result; // !! Is this backwards? 
	}


	public static int parseInt(String s) throws NumberFormatException {
		return parseInt(s,10);
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
}

   
