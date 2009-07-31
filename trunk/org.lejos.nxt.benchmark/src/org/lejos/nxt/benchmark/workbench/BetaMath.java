package org.lejos.nxt.benchmark.workbench;

public class BetaMath
{
	private static final double ln2 = 0.693147180559945309417232;
	
	/**
	 * Square root.
	 */
	public static double sqrtD(double x)
	{
		// also catches NaN
		if (!(x > 0))
			return (x == 0) ? 0 : Double.NaN;
		if (x == Double.POSITIVE_INFINITY)
			return x;
	
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
		
		return factor * (x * isqrt + 1.0 / isqrt);
	}
	
	/**
	 * Square root.
	 */
	public static float sqrtF(float x)
	{
		// also catches NaN
		if (!(x > 0))
			return (x == 0) ? 0 : Float.NaN;
		if (x == Float.POSITIVE_INFINITY)
			return x;
	
		// modify values to avoid workaround subnormal values
		float factor;
		if (x >= Float.MIN_NORMAL)
			factor = 0.5f;
		else
		{
			x *= 0x1p32f;
			factor = 0x1p-17f;
		}
		
		// magic constant invsqrt
		// according to http://www.lomont.org/Math/Papers/2003/InvSqrt.pdf
		// also look at http://en.wikipedia.org/wiki/Fast_inverse_square_root
		float isqrt = Float.intBitsToFloat(0x5f375a86 - (Float.floatToRawIntBits(x) >> 1));
		float xhalf = 0.5f * x;
		isqrt = isqrt * (1.5f - xhalf * isqrt * isqrt);
		isqrt = isqrt * (1.5f - xhalf * isqrt * isqrt);
		//isqrt = isqrt * (1.5 - xhalf * isqrt * isqrt);
		
		return factor * (x * isqrt + 1.0f / isqrt);
	}
	
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
	
		final double[] LOGTABLE = {
			1.0/3, 1.0/5, 1.0/7, 1.0/9,
			1.0/11, 1.0/13, 1.0/15, 1.0/17, 1.0/19, 
			1.0/21, 1.0/23, 1.0/25, 1.0/27, 1.0/29, 
			1.0/31, 1.0/33, 1.0/35, 1.0/37, 1.0/39, 
		};

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

	public static String dToStr(double x)
	{		
		StringBuilder sb = new StringBuilder();
		
		//we need to detect -0.0 to be compatible with JDK
		if ((Double.doubleToRawLongBits(x) & 0x8000000000000000L) != 0)
		{
			sb.append("-");
			x = -x;
		}
		if (x == 0)
		{
			sb.append("0.0");
			return sb.toString();
		}
		

		int exp = 0;
		if (x >= 10)
		{
			if (x >= 1E256) { exp+=256; x*=1E-256; }
			if (x >= 1E128) { exp+=128; x*=1E-128; }
			if (x >= 1E64)  { exp+=64;  x*=1E-64; }
			if (x >= 1E32)  { exp+=32;  x*=1E-32; }
			if (x >= 1E16)  { exp+=16;  x*=1E-16; }
			if (x >= 1E8)   { exp+=8;   x*=1E-8; }
			if (x >= 1E4)   { exp+=4;   x*=1E-4; }
			if (x >= 1E2)   { exp+=2;   x*=1E-2; }
			if (x >= 1E1)   { exp+=1;   x*=1E-1; }
		}
		if (x < 1)
		{
			if (x < 1E-255) { exp-=256; x*=1E256; }
			if (x < 1E-127) { exp-=128; x*=1E128; }
			if (x < 1E-63)  { exp-=64;  x*=1E64; }
			if (x < 1E-31)  { exp-=32;  x*=1E32; }
			if (x < 1E-15)  { exp-=16;  x*=1E16; }
			if (x < 1E-7)   { exp-=8;   x*=1E8; }
			if (x < 1E-3)   { exp-=4;   x*=1E4; }
			if (x < 1E-1)   { exp-=2;   x*=1E2; }
			if (x < 1E-0)   { exp-=1;   x*=1E1; }
		}
		
		// algorithm shows true value of subnormal doubles
		// unfortunatly, the mantisse of subnormal values gets very short
		// TODO automatically adjust digit count for subnormal values  
		
		long tmp = 1000000000000000L;		
		long digits = (long)(x * 1E15 + 0.5);
		
		int d = (int)(digits / tmp);
		sb.append((char)('0' + d));
		digits -= tmp * d;
		
		sb.append('.');		
		do
		{
			d = (int)(digits / (tmp /= 10));
			sb.append((char)('0' + d));
			digits -= tmp * d;
		}
		while (digits > 0);
		
		if (exp != 0)
		{
			sb.append('E');
			sb.append(exp);
		}

		return sb.toString();
	}
	
	public static String dToStr2(double x)
	{
		final int D_TO_STR_MAXEXP = 256; 
		final int D_TO_STR_MAXIDX1 = 17;
		final int D_TO_STR_MAXIDX2 = 26;
		final int D_TO_STR_HALF = 9; 
		final double[] D_TO_STR_POWERS = {
			1E+256, 1E+128, 1E+64, 1E+32, 1E+16, 1E+8, 1E+4, 1E+2, 1E+1,
			1E-1, 1E-2, 1E-4, 1E-8, 1E-16, 1E-32, 1E-64, 1E-128, 1E-256,
			1E-255, 1E-127, 1E-63, 1E-31, 1E-15, 1E-7, 1E-3, 1E-1, 1E-0,
		};
		
		int exp = 0;
		for (int i = 0; i < D_TO_STR_HALF; i++)
		{
			if (x >= D_TO_STR_POWERS[i])
			{
				exp += D_TO_STR_MAXEXP >> i;
				x *= D_TO_STR_POWERS[D_TO_STR_MAXIDX1 - i];
			}
		}
		for (int i = 0; i < D_TO_STR_HALF; i++)
		{
			if (x < D_TO_STR_POWERS[D_TO_STR_MAXIDX2 - i])
			{
				exp -= D_TO_STR_MAXEXP >> i;
				x *= D_TO_STR_POWERS[i];
			}
		}
		
		// algorithm shows true value of subnormal doubles
		// unfortunatly, the mantisse of subnormal values gets very short
		// TODO automatically adjust digit count for subnormal values  
		
		long tmp = 1000000000000000L;		
		long digits = (long)(x * 1E15 + 0.5);
		
		while (digits >= tmp)
		{
			exp++;
			digits /= 10;
		}
		
		StringBuilder sb = new StringBuilder();

		int d = (int)(digits / (tmp /= 10));
		sb.append((char)('0' + d));
		digits -= tmp * d;
		
		sb.append('.');		
		do
		{
			d = (int)(digits / (tmp /= 10));
			sb.append((char)('0' + d));
			digits -= tmp * d;
		}
		while (digits > 0);
		
		if (exp != 0)
		{
			sb.append('E');
			sb.append(exp);
		}

		return sb.toString();
	}

}
