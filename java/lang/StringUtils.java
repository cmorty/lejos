package java.lang;

/**
 * Just some utility methods for the wrapper classes as well as StringBuffer and StringBuilder.
 * @author Sven Köhler
 */
class StringUtils
{
	private static final int STR_NAN_LEN = 3;
	private static final String STR_NAN = "NaN";
	private static final int STR_INFINITY_LEN = 8;
	private static final String STR_INFINITY = "Infinity";
		
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
	 * 
	 * @param p position of the character after the last digit
	 */
	static int getIntChars(char[] buf, int p, int v, int radix)
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
	 * 
	 * @param p position of the character after the last digit
	 */
	static int getLongChars(char[] buf, int p, long v, int radix)
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
	
	static void reverseChars(char[] buf, int start, int end, int len)
	{
		len = Math.min(len, (end - start) >> 1);
		int end2 = start + len;
		int base = start + end - 1;
	
		for (int i = start; i < end2; i++)
		{
			int j = base - i;
			char tmp = buf[i];
			buf[i] = buf[j];
			buf[j] = tmp;
		}
	}

	/**
	 * Exact size of buffer for {@link #getIntChars(char[], int, int, int)}.
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
	 * Exact size of buffer for {@link #getLongChars(char[], int, long, int)}.
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
	
	static final int MAX_FLOAT_CHARS = 32;
	
	static int getFloatChars(float number, char[] buf, int charPos)
	{
		return getDoubleChars(number, buf, charPos, 8);
	}

	static final int MAX_DOUBLE_CHARS = 32;
	
	static int getDoubleChars(double number, char[] buf, int charPos)
	{
		return getDoubleChars(number, buf, charPos, 17);
	}

    /**
     * Helper method for converting floats and doubles.
     *
     * @author Martin E. Nielsen
     * @author Sven Köhler
     **/
	private static int getDoubleChars(double number, char[] buf, int charPos, int significantDigits)
	{
		if (number != number)
		{
			STR_NAN.getChars(0, STR_NAN_LEN, buf, charPos);
			return charPos + STR_NAN_LEN;
		}
		
		//we need to detect -0.0 to be compatible with JDK
		boolean negative = (Double.doubleToRawLongBits(number) & 0x8000000000000000L) != 0;
		if (negative)
		{
			buf[ charPos++ ] = '-';
			number = -number;
		}
		
		if ( number == Double.POSITIVE_INFINITY)
		{
			STR_INFINITY.getChars(0, STR_INFINITY_LEN, buf, charPos);
			return charPos + STR_INFINITY_LEN;
		}
		
		if ( number == 0 ) {
			buf[ charPos++ ] = '0';
			buf[ charPos++ ] = '.';
			buf[ charPos++ ] = '0';
		} else {
			int exponent = 0;
			
			// calc. the power (base 10) for the given number:
			int pow = ( int )Math.floor( Math.log( number ) / Math.LN10 );
	
			// use exponential formatting if number too big or too small
			if ( pow < -3 || pow > 6 ) {
				exponent = pow;
				number /= Math.exp( Math.LN10 * exponent );
			} // if
	
			// Recalc. the pow if exponent removed and d has changed
			pow = ( int )Math.floor( Math.log( number ) / Math.LN10 );
	
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
				charPos += exactStringLength(exponent, 10);
				getIntChars(buf, charPos, exponent, 10);
			} // if
		}
		return charPos;
	}
}
