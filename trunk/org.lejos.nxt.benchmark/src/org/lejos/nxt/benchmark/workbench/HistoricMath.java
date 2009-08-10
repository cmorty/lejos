package org.lejos.nxt.benchmark.workbench;

public class HistoricMath
{
	/**
	 * Square root
	 * @author Paulo Costa
	 */
	public static double sqrtSimple(double x)
	{
		double root = x, guess = 0;

		if (x < 0)
			return Double.NaN;

		// the accuracy test is percentual
		for (int i = 0; (i < 22) && ((guess > x * (1 + 5e-7f)) || (guess < x * (1 - 5e-7f))); i++)
		{
			root = (root + x / root) * 0.5f; // a multiplication is faster than a division
			guess = root * root; // cache the square to the test
		}
		return root;
	}

	// constants for approximating sqrt
	private static final int SQRT_APPROX_EVEN_MULT = 1756;
	private static final int SQRT_APPROX_EVEN_SHIFT = 12;
	private static final int SQRT_APPROX_ODD_MULT = 1170;
	private static final int SQRT_APPROX_ODD_SHIFT = 11;
	private static final long SQRT_APPROX_ODD_ADD = (1L << 52) - ((long)SQRT_APPROX_ODD_MULT << (52 - SQRT_APPROX_ODD_SHIFT));

	/**
	 * Linear approximation of sqrt.
	 * The maximum relative error is about 1/2^6.6.
	 */
	private static double sqrtLinearApprox(double x)
	{
		// @author Sven Köhler
		 
		//don't call this function for zero, infinity or NaN
		//assert (x > 0);
		//assert !Double.isNaN(x);
		//assert !Double.isInfinite(x);
		
		/**
		 * The following is done here:
		 * Imagine a floating point number f with 1 <= f < 2. This can be written as
		 * f = 1 + m. So basically, m is the mantissa of f. Since the sqrt(x) is almost
		 * linear in the interval x=0..1, the term sqrt(f) can be approximated linearly.
		 * This is done by chosing some c to that the function 1+m*c generates minimal
		 * relative errors for all m values in the interval [0,1]. c=1756/4096 seems to
		 * be optimal.
		 * Now imagine a floating point f with 2 <= f < 4. This can be written as
		 * f = 2 + 2*m. Again, m is the manitssa. Since sqrt(x) is almost linear
		 * in the interval x=2..4, the term sqrt(f) can be approximated linearly
		 * again. This time, I have chosen the function 1 + (m*c - c + 1) to
		 * approximate sqrt(f). For m=1 (that means f=4), this function will
		 * always return 2. Again, a value for c has been found, that produces
		 * minimal relative errors for all m in the interval [0,1]. The value
		 * c = 1170/2048 seems to be optimal.   
		 */
		
		/**
		 * In the above text, I describe the handling of numbers with the
		 * exponent 0 (1 <= f < 2) and number with with the exponent 1
		 * (that means 2 <= f < 4). The exponent can be handled separatly.
		 * It's simply deviced by two which is possible since this routine
		 * handles both the odd and even exponent case. 
		 */
		
		//extract exponent (sign bit is zero)
		long bits = Double.doubleToRawLongBits(x);
		int exp = (int)(bits >> 52);
		
		//zero indicates a denormalized value
		if (exp == 0)
		{
			//normalize the value again
			int zeros = Long.numberOfLeadingZeros(bits);
			//calculate normalized exponent
			exp = 12 - zeros;			
			//shift bits for correct mantissa extraction
			bits <<= zeros - 11;
		}
		
		//extract mantissa
		long man = bits & 0x000FFFFFFFFFFFFFL;			
		
		//calculate new exponent (biased by 1023)
		int newexp = ((exp + 1) >> 1) + 511;
		
		//check whether exponent iss odd (note the bias 1023)
		if ((exp & 1) == 0)
			//calculate new mantisssa for odd exponents
			man = ((man * SQRT_APPROX_ODD_MULT) >> SQRT_APPROX_ODD_SHIFT) + SQRT_APPROX_ODD_ADD;
		else
			//calculate new mantisssa for even exponents
			man = (man * SQRT_APPROX_EVEN_MULT) >> SQRT_APPROX_EVEN_SHIFT;
		
		//if this fails, sub 1 from SQRT_APPROX_ODD_ADD
		//assert man < (1L << 52);
		
		//return calculated number
		return Double.longBitsToDouble(((long)newexp << 52) | man);
	}
	
	/**
	 * Square root.
	 */
	public static double sqrtLinear(double x) {
		// @author Sven Köhler
		
		if (x == 0)
			return 0;
		if (x < 0 || Double.isNaN(x))
			return Double.NaN;
		if (x == Double.POSITIVE_INFINITY)
			return Double.POSITIVE_INFINITY;
		
		double root = sqrtLinearApprox(x);
		double root2;
		
		/**
		 * 1 newton step:
		 * root = 0.5 * (root + x / root)
		 */

		/**
		 * 2 newton steps:
		 * root = root + x/root;
		 * root = 0.25 * root + x/root; 
		 */
		
		// 2 newton steps (short form, with test)
		root2 = x/root;
		if (root == root2)
			return root;
		root = root + root2;
		root = 0.25 * root + x/root;
				
		// 1 newton step (without check)
		root2 = x/root;
		root = 0.5 * (root + root2);
				
		return root;
	}

}
