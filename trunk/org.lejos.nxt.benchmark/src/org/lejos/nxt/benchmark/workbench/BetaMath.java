package org.lejos.nxt.benchmark.workbench;

public class BetaMath
{
	private static final double ln2 = 0.693147180559945309417232;
	
	/**
	 * Square root.
	 */
	public static double sqrt(double x)
	{
		// also catches NaN
		if (!(x > 0))
			return (x == 0) ? 0 : Double.NaN;
		if (x == Double.POSITIVE_INFINITY)
			return Double.POSITIVE_INFINITY;
	
		// modify values to avoid workaround subnormal values
		double factor;
		if (x >= Double.MIN_NORMAL)
			factor = 0.5;
		else
		{
			x *= 0x1p64;
			factor = 0x1p-33;
		}
		
		// magic constant invsqrt
		// according to http://www.lomont.org/Math/Papers/2003/InvSqrt.pdf
		// also look at http://en.wikipedia.org/wiki/Fast_inverse_square_root
		double isqrt = Double.longBitsToDouble(0x5fe6ec85e7de30daL - (Double.doubleToRawLongBits(x) >> 1));
		double xhalf = 0.5 * x;
		isqrt = isqrt * (1.5 - xhalf * isqrt * isqrt);
		isqrt = isqrt * (1.5 - xhalf * isqrt * isqrt);
		isqrt = isqrt * (1.5 - xhalf * isqrt * isqrt);
		
		//return 0.5 * (x * isqrt + 1.0 / isqrt);
		return factor * (x * isqrt + 1.0 / isqrt);
	}
	
	private static final double[] LOGTABLE = {
			1.0/3, 1.0/5, 1.0/7, 1.0/9,
			1.0/11, 1.0/13, 1.0/15, 1.0/17, 1.0/19, 
			1.0/21, 1.0/23, 1.0/25, 1.0/27, 1.0/29, 
			1.0/31, 1.0/33, 1.0/35, 1.0/37, 1.0/39, 
	};

	/**
	 * Natural log function. Returns log(a) to base E Replaced with an algorithm
	 * that does not use exponents and so works with large arguments.
	 * 
	 * @see <a
	 *      href="http://www.geocities.com/zabrodskyvlada/aat/a_contents.html">here</a>
	 */
	public static double log(double x)
	{
		if (x <= 0)
			return Double.NaN;
		if (x == Double.POSITIVE_INFINITY)
			return Double.POSITIVE_INFINITY;
	
		int m;
		if (x >= Double.MIN_NORMAL)
			m = -1023;
		else
		{
			m = -1023-64;
			x *= 0x1p64;
		}
	
		//extract mantissa and reset exponent
		long bits = Double.doubleToRawLongBits(x);
		m += (int)(bits >>> 52);
		bits = (bits & 0x000FFFFFFFFFFFFFL) + 0x3FF0000000000000L;
		x = Double.longBitsToDouble(bits);
		
		double zeta = (x - 1.0) / (x + 1.0);
		double zetasup = zeta * zeta;		
		double ln = 1;
		
		//knows ranges:
		//	1 <= $x < 2
		//  0 <= $zeta < 1/3
		//  0 <= $zetasup < 1/9
		//ergo:
		//  $n will converge quickly towards $limit
		
		double n = zetasup;
		for (int j = 0; n > 0x1p-50;j ++)
		{
			ln += n * LOGTABLE[j];
			n *= zetasup;
		}
		
		return m * ln2 + 2 * zeta * ln;
	}

}
