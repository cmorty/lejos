package java.lang;

/**
 * Minimal Double implementation.
 * 
 * @author bb
 * @author Sven Köhler
 */
public final class Double extends Number implements Comparable<Double>
{
	public static final double POSITIVE_INFINITY = 1.0d / 0.0d;
	public static final double NEGATIVE_INFINITY = -1.0d / 0.0d;
	public static final double NaN = 0.0d / 0.0d;
	
	public static final int MAX_EXPONENT = 1023;
	public static final double MAX_VALUE = 0x1.fffffffffffffp+1023;
	
	public static final int MIN_EXPONENT = -1022;
	public static final double MIN_NORMAL = 0x1.0p-1022;
	public static final double MIN_VALUE = 0x1.0p-1074;
	
	public static final int SIZE = 64;
    // References to the following field are automatically replaced with a load
    // of the correct value by the linker, so no need to initialize.
	public static final Class<?> TYPE = null;
	
	//MISSING public static String toHexString(double)
	
	private double value;
	
	public Double(double val)
	{
		this.value = val;
	}
	
	public Double(String val)
	{
		this.value = Double.parseDouble(val);
	}
	
	@Override
	public byte byteValue()
	{
		return (byte)this.value;
	}
	
	public static int compare(double a, double b)
	{
		// normal float compare, NaN should fall through
		if (a > b)
			return 1;
		if (a < b)
			return -1;
		
		// handle NaN
		if (a != a)
			return (b != b) ? 0 : 1;
		if (b != b)
			return -1;
		
		// handle negative and positive zero
		long ra = Double.doubleToRawLongBits(a);
		long rb = Double.doubleToRawLongBits(b);
		
		if (ra > rb)
			return 1;
		if (ra < rb)
			return -1;
		return 0;
	}
	
	public int compareTo(Double other)
	{
		return Double.compare(this.value, other.value);
	}
	
	@Override
	public double doubleValue()
	{
		return this.value;
	}
	
	@Override
	public boolean equals(Object o)
	{
		//instanceof returns false for o==null
		return (o instanceof Double)
			&& (doubleToLongBits(this.value) == doubleToLongBits(((Double)o).value));
	}
	
	@Override
	public float floatValue()
	{
		return (float)this.value;
	}
	
	@Override
	public int hashCode()
	{
		long l = doubleToLongBits(this.value);
		return ((int)l) ^ ((int)(l >>> 32));
	}
	
	@Override
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
		return v == POSITIVE_INFINITY || v == NEGATIVE_INFINITY;
	}

	public boolean isNaN()
	{
		return Double.isNaN(this.value);
	}
	
	public static boolean isNaN(double val)
	{
		return val != val;
	}
	
	@Override
	public long longValue()
	{
		return (long)this.value;
	}
	
	/**
	 * Converts a String value into a double. Must only contain
	 * numbers and an optional decimal, and optional - sign at front.
	 * 
	 * @param s String representation of the floating point number
	 * @return double precision floating point number
	 */
	public static double parseDouble(String s) throws NumberFormatException {
		return StringUtils.stringToDouble(s);
	}
	
	@Override
	public short shortValue()
	{
		return (short)this.value;
	}
	
    @Override
    public String toString()
    {
    	return String.valueOf(this.value);
    }
    
	/**
	 * Convert a double to a String
	 * @param d the double to be converted
	 * @return the String representation of the float
	 */
	public static String toString(double d)
	{
		return String.valueOf(d);
	}
	
	public static Double valueOf(double d)
	{
		return new Double(d);
	}
	
	public static Double valueOf(String s)
	{
		return new Double(s);
	}

	/**
	 * Returns the bit representation of a double-float value.
	 * The result is a representation of the floating-point argument 
	 * according to the IEEE 754 floating-point "double 
	 * precision" bit layout. 
	 * <ul>
	 * <li>If the argument is positive infinity, the result is 
	 * <code>0x7ff0000000000000</code>.
	 * <li>If the argument is negative infinity, the result is 
	 * <code>0xfff0000000000000</code>.
	 * <p>
	 * If the argument is NaN, the result is the integer
	 * representing the actual NaN value.  
	 * </ul>
	 * In all cases, the result is an integer that, when given to the 
	 * {@link #longBitsToDouble(long)} method, will produce a floating-point
	 * value equal to the argument to <code>doubleToRawLongBits</code>.
	 * 
	 * @param   d   a floating-point number.
	 * @return  the bits that represent the floating-point number.
	 */    
    public static native long doubleToRawLongBits(double d);


    /**
	 * Returns the bit representation of a double-float value.
	 * The result is a representation of the floating-point argument
	 * according to the IEEE 754 floating-point "double
	 * precision" bit layout. Unlike <code>doubleToRawLongBits</code> this
     * method does collapse all NaN values into a standard single value. This
     * value is <code>0x7ff8000000000000</code>.
     * @param value a floating-point number.
     * @return the bits that represent the floating-point number.
     */
    public static long doubleToLongBits(double value)
    {
    	// normalize NaN
    	if (value != value)
    		return 0x7ff8000000000000L;
    	
        return doubleToRawLongBits(value);
    }

	/**
	 * Returns the double-float corresponding to a given bit representation.
	 * The argument is considered to be a representation of a
	 * floating-point value according to the IEEE 754 floating-point
	 * "double precision" bit layout.
	 * <p>
	 * If the argument is <code>0x7ff0000000000000</code>, the result is positive
	 * infinity.
	 * <p>
	 * If the argument is <code>0xfff0000000000000</code>, the result is negative
	 * infinity.
	 * <p>
     * If the argument is any value in the range 0x7ff0000000000001L through
     * 0x7fffffffffffffffL or in the range 0xfff0000000000001L through
     * 0xffffffffffffffffL, the result is a NaN.
     * All IEEE 754 NaN values of type <code>float</code> are, in effect,
	 * lumped together by the Java programming language into a single
	 * <code>double</code> value called NaN.  Distinct values of NaN are only
	 * accessible by use of the <code>Double.doubleToRawLongBits</code> method.
	 * <p>
	 *
	 * @param   l a long.
	 * @return  the double-format floating-point value with the same bit
	 *		  pattern.
	 */
    public static native double longBitsToDouble(long l);
}
