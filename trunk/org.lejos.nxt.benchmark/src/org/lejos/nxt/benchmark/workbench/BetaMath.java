package org.lejos.nxt.benchmark.workbench;

public class BetaMath
{
	private static final double ln2 = 0.693147180559945309417232;
	
	/**
	 * Computes square-root of x.
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
		
		// magic constant function for good approximation of 1/sqrt(x)
		// according to http://www.lomont.org/Math/Papers/2003/InvSqrt.pdf
		// also look at http://en.wikipedia.org/wiki/Fast_inverse_square_root
		float isqrt = Float.intBitsToFloat(0x5f375a86 - (Float.floatToRawIntBits(x) >> 1));

		// 2 newton steps for 1/sqrt(x)
		float xhalf = 0.5f * x;
		isqrt = isqrt * (1.5f - xhalf * isqrt * isqrt);
		isqrt = isqrt * (1.5f - xhalf * isqrt * isqrt);
		
		// 1 newton step for sqrt(x)
		return factor * (x * isqrt + 1.0f / isqrt);
	}
	
	private static class LogStuff
	{
		public static final double[] LOGTABLE = {
				1.0/3, 1.0/5, 1.0/7, 1.0/9,
				1.0/11, 1.0/13, 1.0/15, 1.0/17, 1.0/19, 
				1.0/21, 1.0/23, 1.0/25, 1.0/27, 1.0/29, 
				1.0/31, 1.0/33, 1.0/35, 1.0/37, 1.0/39, 
			};			
	}
	
	/**
	 * Natural log function. Returns log(a) to base E.
	 * 
	 * @see <a
	 *      href="">here</a>
	 */
	public static double log(double x)
	{
		// also catches NaN
		if (!(x > 0))
			return (x == 0) ? Double.NEGATIVE_INFINITY : Double.NaN;
		if (x == Double.POSITIVE_INFINITY)
			return Double.POSITIVE_INFINITY;
	
		// Algorithm has been derived from http://www.geocities.com/zabrodskyvlada/aat/a_contents.html 

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
		
		//known ranges:
		//	1 <= $x < 2
		//  0 <= $zeta < 1/3
		//  0 <= $zetasup < 1/9
		//ergo:
		//  $n will converge quickly towards $limit
		
		double[] lt = LogStuff.LOGTABLE;
		
		double n = zetasup;
		for (int j = 0; n > 0x1p-50;j ++)
		{
			ln += n * lt[j];
			n *= zetasup;
		}
		
		return m * ln2 + 2 * zeta * ln;
	}
	
	public static String doubleToString(double x)
	{		
		if (x != x)
			return "NaN";
		
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
		if (x == Double.POSITIVE_INFINITY)
		{
			sb.append("Infinity");
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
		// unfortunatly, the mantissa of subnormal values gets very short
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
	
	private static final int D_TO_STR_MAXEXP = 256; 
	private static final int D_TO_STR_IDXPART1 = 0;
	private static final int D_TO_STR_IDXPART2 = 9;
	private static final int D_TO_STR_IDXPART3 = 18;
	private static final int D_TO_STR_PARTLEN  = 9;
	
	private static class DoubleToStringStuff
	{
		public static final double[] D_TO_STR_POWERS = {
				1E+256, 1E+128, 1E+64, 1E+32, 1E+16, 1E+8, 1E+4, 1E+2, 1E+1,
				1E-256, 1E-128, 1E-64, 1E-32, 1E-16, 1E-8, 1E-4, 1E-2, 1E-1,
				1E-255, 1E-127, 1E-63, 1E-31, 1E-15, 1E-7, 1E-3, 1E-1, 1E-0,
			};
	}
	
	public static String doubleToString2(double x)
	{
		if (x != x)
			return "NaN";
		
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
		if (x == Double.POSITIVE_INFINITY)
		{
			sb.append("Infinity");
			return sb.toString();
		}
		
		int exp = 0;
		double[] powers = DoubleToStringStuff.D_TO_STR_POWERS;

		if (x >= 10)
		{
			for (int i = 0; i < D_TO_STR_PARTLEN; i++)
			{
				if (x >= powers[D_TO_STR_IDXPART1 + i])
				{
					exp += D_TO_STR_MAXEXP >> i;
					x *= powers[D_TO_STR_IDXPART2 + i];
				}
			}
		}
		else if (x < 1)
		{
			for (int i = 0; i < D_TO_STR_PARTLEN; i++)
			{
				if (x < powers[D_TO_STR_IDXPART3 + i])
				{
					exp -= D_TO_STR_MAXEXP >> i;
					x *= powers[D_TO_STR_IDXPART1 + i];
				}
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

	public static String doubleToString3(double x)
	{
		if (x != x)
			return "NaN";
		
		StringBuilder sb = new StringBuilder(22);
		
		//we need to detect -0.0 to be compatible with JDK
		int bits = (int)(Double.doubleToRawLongBits(x) >> 52); 
		if ((bits & 0x8000L) != 0)
		{
			sb.append("-");
			x = -x;
		}
		
		if (x == 0)
		{
			sb.append("0.0");
			return sb.toString();
		}
		if (x == Double.POSITIVE_INFINITY)
		{
			sb.append("Infinity");
			return sb.toString();
		}
		
		int exp;		
		if (x >= Double.MIN_NORMAL)
			exp = 63;
		else
		{
			exp = 0;
			bits = (int)(Double.doubleToRawLongBits(x * 0x1p63) >> 52);
		}
		
		exp += bits & 0x7FF;
		exp = ((exp * 631305) >> 21) - 327;
		
		// at this point, the following should always hold:
		//   floor(log(10, x)) - 1 < exp <= floor(log(10, x))
		x = pow10(x, 14 - exp);
		
		long tmp = 1000000000000000L;
		long digits = (long)(x + 0.5);
		
		if (digits >= tmp)
		{
			exp++;
			digits = (long)(x * 1E-1 + 0.5);
		}
		
		// algorithm shows true value of subnormal doubles
		// unfortunatly, the mantisse of subnormal values gets very short
		// TODO automatically adjust digit count for subnormal values
		
		int tmp1 = 10000000;
		int tmp2 = 1000000;
		int d1 = (int)(digits / tmp1);
		int d2 = (int)(digits - tmp1 * (long)d1);

		int d = d1 / tmp1;
		sb.append((char)('0' + d));
		d1 -= tmp1 * d;
		
		sb.append('.');		
		do
		{
			d = d1 / (tmp1 /= 10);
			sb.append((char)('0' + d));
			d1 -= tmp1 * d;
		}
		while (d1 > 0);
		
		if (d2 > 0)
		{
			while (tmp1 > 1)
			{
				sb.append('0');
				tmp1 /= 10;
			}
			
			do
			{
				d = d2 / tmp2;
				sb.append((char)('0' + d));
				d2 -= tmp2 * d;
				tmp2 /= 10;
			}
			while (d2 > 0);
		}
		
		if (exp != 0)
		{
			sb.append('E');
			sb.append(exp);
		}

		return sb.toString();
	}

	private static final double[] POW10_1 = {1E+1, 1E+2, 1E+4, 1E+8, 1E+16, 1E+32, 1E+64, 1E+128, 1E+256, };
	private static final double[] POW10_2 = {1E-1, 1E-2, 1E-4, 1E-8, 1E-16, 1E-32, 1E-64, 1E-128, 1E-256, };
	
	private static double pow10(double r, int e)
	{
		double[] b;
		if (e >= 0)
			b = POW10_1;
		else
		{
			b = POW10_2;
			e = -e;
		}
		
		if ((e & 0x100) != 0) r *= b[8];
		if ((e & 0x080) != 0) r *= b[7];
		if ((e & 0x040) != 0) r *= b[6];
		if ((e & 0x020) != 0) r *= b[5];
		if ((e & 0x010) != 0) r *= b[4];
		if ((e & 0x008) != 0) r *= b[3];
		if ((e & 0x004) != 0) r *= b[2];
		if ((e & 0x002) != 0) r *= b[1];
		if ((e & 0x001) != 0) r *= b[0];
		
		return r;
	}
	
}
