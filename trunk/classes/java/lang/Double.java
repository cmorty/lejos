package java.lang;

/**
 * Minimal Double implementation to support parseDouble(), isNaN(), and NaN.
 * @author bb
 *
 */
public class Double
{
    public static final double POSITIVE_INFINITY = 1.0d / 0.0d;
    public static final double NEGATIVE_INFINITY = -1.0d / 0.0d;
	public static final double NaN = 0.0d / 0.0d;
	
    public static final int SIZE = 64;
    
    //MISSING implements Comparable
    //MISSING public static Class TYPE
    //MISSING public static int compare(double, double)
    //MISSING public static long doubleToLongBits(double)
    //MISSING public static long doubleToRawLongBits(double)
    //MISSING public boolean equals(Object obj)
    //MISSING public int hashCode()
    //MISSING public boolean isNaN()
    //MISSING public static boolean isNaN(double)
    //MISSING public static double longBitsToDouble(long)
    //MISSING public static String toHexString(double)
    //MISSING public String toString()
    //MISSING public static String toString(double)
    
	private double value;
	
	public Double(double val)
	{
		this.value = val;
	}
	
	public Double(String val)
	{
		this.value = parseDouble(val);
	}
	
	public byte byteValue()
	{
		return (byte)this.value;
	}
	
	public double doubleValue()
	{
		return this.value;
	}
	
	public float floatValue()
	{
		return (float)this.value;
	}
	
	public int intValue()
	{
		return (int)this.value;
	}
	
	public boolean isInfinite()
	{
		return Double.isInfinite(this.value);
	}
	
    public static boolean isInfinite(double v)
    {
    	return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
    }

    public long longValue()
    {
    	return (long)this.value;
    }
	
	/**
	 * Converts a String value into a float 
	 * @param s String representation of float. Must only contain numbers and an optional decimal, and optional - sign at front.
	 * @return float number
	 */
	public static double parseDouble(String s) throws NumberFormatException {
		boolean negative = (s.charAt(0) == '-'); // Check if negative symbol.
		double result = 0.0D; // Starting value
		int index = s.indexOf('.');
		
		if(index > -1) {
		// Means the decimal place exists, add values to right of it
			int divisor = 1;
			for(int i=index+1;i<s.length();i++) {
				divisor *= 10;
				int curVal = (s.charAt(i)-48); // Convert char to int
				if(curVal > 9|curVal < 0)
					throw new NumberFormatException();
				result += (curVal/divisor);
			}
		}
		else {
			index = s.length(); // If number string had no decimal
		}
			
		
		// Now add number characters to left of decimal
		int multiplier = 1;
		int finish = negative ? 1 : 0; // Determine finishing position
		
		for(int i=index-1;i>= finish;i--) {
			int curVal = (s.charAt(i) - 48); // Convert char to int
			if(curVal > 9|curVal < 0)
				throw new NumberFormatException();
			result += (curVal * multiplier);
			multiplier *= 10;
		}	
		
		return negative ? -result : result;
	}
	
	public short shortValue()
	{
		return (short)this.value;
	}
	
	public static Double valueOf(double d)
	{
		return new Double(d);
	}
	
	public static Double valueOf(String s)
	{
		return new Double(s);
	}
}
