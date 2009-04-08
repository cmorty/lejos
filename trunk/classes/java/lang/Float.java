package java.lang;

/**
 * Minimal Float implementation.
 *
 * @author Lawrie Griffiths
 * @author Sven Köhler
 */
public final class Float extends Number
{
	public static final float POSITIVE_INFINITY = 1.0f / 0.0f;
	public static final float NEGATIVE_INFINITY = -1.0f / 0.0f;
	public static final float NaN = 0.0f / 0.0f;
	
	public static final int SIZE = 32;

	//MISSING implements Comparable
	//MISSING public static Class TYPE
	//MISSING public static int compare(float, float)
	//MISSING public int compareTo(Object)
	//MISSING public static String toHexString(float)

	private float value;
	
	public Float(double value)
	{
		this.value = (float)value;
	}
	
	/**
	 * Constructs a newly allocated Float object that represents the primitive float argument.
	 * @param value - the value to be represented by the Float.
	 */
	public Float(float value)
	{
		this.value = value;
	}
	
	public Float(String s)
	{
		this.value = Float.parseFloat(s);
	}
	
	@Override
	public byte byteValue()
	{
		return (byte)this.value;
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
		return (o instanceof Float)
			&& (floatToIntBits(this.value) == floatToIntBits(((Float)o).value));
	}
	
	/**
	 * Returns the bit representation of a single-float value.
	 * The result is a representation of the floating-point argument 
	 * according to the IEEE 754 floating-point "single 
	 * precision" bit layout. 
	 * <ul>
	 * <li>Bit 31 (the bit that is selected by the mask 
	 * <code>0x80000000</code>) represents the sign of the floating-point 
	 * number. 
	 * <li>Bits 30-23 (the bits that are selected by the mask 
	 * <code>0x7f800000</code>) represent the exponent. 
	 * <li>Bits 22-0 (the bits that are selected by the mask 
	 * <code>0x007fffff</code>) represent the significand (sometimes called 
	 * the mantissa) of the floating-point number. 
	 * <li>If the argument is positive infinity, the result is 
	 * <code>0x7f800000</code>. 
	 * <li>If the argument is negative infinity, the result is 
	 * <code>0xff800000</code>.
	 * <p>
	 * If the argument is NaN, the result is the integer
	 * representing the actual NaN value.  
	 * </ul>
	 * In all cases, the result is an integer that, when given to the 
	 * {@link #intBitsToFloat(int)} method, will produce a floating-point 
	 * value equal to the argument to <code>floatToRawIntBits</code>.
	 * 
	 * @param   value   a floating-point number.
	 * @return  the bits that represent the floating-point number.
	 */
	public static native int floatToRawIntBits(float value);

    /**
	 * Returns the bit representation of a single-float value.
	 * The result is a representation of the floating-point argument
	 * according to the IEEE 754 floating-point "single
	 * precision" bit layout. Unlike <code>floatToRawIntBits</code> this
     * method does collapse all NaN values into a standard single value. This
     * value is <code>0x7fc00000</code>.
     * @param value a floating-point number.
     * @return the bits that represent the floating-point number.
     */
    public static int floatToIntBits(float value)
    {
        int i = floatToRawIntBits(value);
        // Collapse any NaN values
        // Mask out the sign bit for the tests
        int j = i & 0x7fffffff;
        // and check for being in the NaN range
        if (j >= 0x7f800001 && j <= 0x7fffffff)
            j = 0x7fc00000;

        return j;
    }
	/**
	 * Returns the float value of this Float  object.
	 * @return the float value represented by this object
	 */
	@Override
	public float floatValue()
	{
	   return value;
	}
  
	@Override
	public int hashCode()
	{
		return floatToIntBits(this.value);
	}
	
	/**
	 * Returns the single-float corresponding to a given bit represention.
	 * The argument is considered to be a representation of a
	 * floating-point value according to the IEEE 754 floating-point
	 * "single precision" bit layout.
	 * <p>
	 * If the argument is <code>0x7f800000</code>, the result is positive
	 * infinity.
	 * <p>
	 * If the argument is <code>0xff800000</code>, the result is negative
	 * infinity.
	 * <p>
	 * If the argument is any value in the range <code>0x7f800001</code> 
	 * through <code>0x7fffffff</code> or in the range 
	 * <code>0xff800001</code> through <code>0xffffffff</code>, the result is 
	 * NaN. All IEEE 754 NaN values of type <code>float</code> are, in effect,
	 * lumped together by the Java programming language into a single 
	 * <code>float</code> value called NaN.  Distinct values of NaN are only
	 * accessible by use of the <code>Float.floatToRawIntBits</code> method.
	 * <p>
	 * In all other cases, let <i>s</i>, <i>e</i>, and <i>m</i> be three 
	 * values that can be computed from the argument: 
	 * <blockquote><pre>
	 * int s = ((bits >> 31) == 0) ? 1 : -1;
	 * int e = ((bits >> 23) & 0xff);
	 * int m = (e == 0) ?
	 *				 (bits & 0x7fffff) << 1 :
	 *				 (bits & 0x7fffff) | 0x800000;
	 * </pre></blockquote>
	 * Then the floating-point result equals the value of the mathematical 
	 * expression <i>s&#183;m&#183;2<sup>e-150</sup></i>.
	 *
	 * @param   value an integer.
	 * @return  the single-format floating-point value with the same bit
	 *		  pattern.
	 */
	public static native float intBitsToFloat(int value);
	
	@Override
	public int intValue()
	{
		return (int)this.value;
	}
	
	public boolean isInfinite()
	{
		return Float.isInfinite(this.value);
	}
	
	public static boolean isInfinite(float v)
	{
		return v == POSITIVE_INFINITY || v == NEGATIVE_INFINITY;
	}
	
	public boolean isNaN()
	{
		return Float.isNaN(this.value);
	}
	
	public static boolean isNaN(float val)
	{
		return val != val;
	}
	
	@Override
	public long longValue()
	{
		return (long)this.value;
	}
	
	/**
	 * Converts a String value into a float 
	 * @param s String representation of float. Must only contain numbers and an optional decimal, and optional - sign at front.
	 * @return float number
	 */
	public static float parseFloat(String s) throws NumberFormatException
	{
		return (float)Double.parseDouble(s);
	}
	
	@Override
	public short shortValue()
	{
		return (short)this.value;
	}
	
	@Override
	public String toString()
	{
		return Float.toString(this.value);
	}
	
	/**
	 * Convert a float to a String
	 * @param f the float to be converted
	 * @return the String representation of the float
	 */
	public static String toString(float f)
	{
		return String.valueOf(f);
	}
	
	public static Float valueOf(float f)
	{
		return new Float(f);
	}
	
	public static Float valueOf(String s)
	{
		return new Float(s);
	}
}