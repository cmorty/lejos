package org.lejos.nxt.benchmark.workbench;

public class BetaMath
{
	// Math constants
	private static final double PI = 3.14159265358979323846264338328;
	private static final double LN2 = 0.693147180559945309417232121458;
	
	private static final double SQRT2 = 1.41421356237309504880168872421;
	private static final double SQRTSQRT2 = 1.18920711500272106671749997056;
	private static final double LN_SQRT2 = 0.346573590279972654708616060729;
	private static final double LN_SQRTSQRT2 = 0.173286795139986327354308030364;
	
	private static final double INV_LN2 = 1.44269504088896340735992468100;
	private static final double INV_SQRT2 = 0.707106781186547524400844362105;
	private static final double INV_SQRTSQRT2 = 0.840896415253714543031125476233;
	private static final double INV_LN_SQRTSQRT2 = 5.77078016355585362943969872400;

	private static final double PIhalf = PI * 0.5;
	private static final double PItwice = PI * 2.0;
	private static final double PIhalfhalf = PI * 0.25;

	private static final int STR_NAN_LEN = 3;
	private static final String STR_NAN = "NaN";
	private static final int STR_INFINITY_LEN = 8;
	private static final String STR_INFINITY = "Infinity";
	
	/**
	 * Computes square-root of x.
	 */
	public static float sqrtF(float x)
	{
		// also catches NaN
		if (!(x > 0))
			return (x == 0) ? x : Float.NaN;
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
	
	private static class LogConstants
	{
		public static final double[] LOGTABLE = {
				1.0/3, 1.0/5, 1.0/7, 1.0/9,
				1.0/11, 1.0/13, 1.0/15, 1.0/17, 1.0/19,
				1.0/21, 1.0/23, 1.0/25, 1.0/27, 1.0/29,
				1.0/31, 1.0/33, 1.0/35, 1.0/37, 1.0/39, 
			};			
	}
	
	/**
	 * Natural log function. Returns log(x) to base E.
	 */
	public static double log(double x)
	{
		// also catches NaN
		if (!(x > 0))
			return (x == 0) ? Double.NEGATIVE_INFINITY : Double.NaN;
		if (x == Double.POSITIVE_INFINITY)
			return Double.POSITIVE_INFINITY;
	
		// Algorithm has been derived from the one given at
		// http://www.geocities.com/zabrodskyvlada/aat/a_contents.html 

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
		double zeta2 = zeta * zeta;		
		
		//known ranges:
		//	1 <= $x < 2
		//  0 <= $zeta < 1/3
		//  0 <= $zeta2 < 1/9
		//ergo:
		//  $zetapow will converge quickly towards 0
		
		double[] lt = LogConstants.LOGTABLE;		
		double zetapow = zeta2;
		double r = 1;
		int i = 0;

		while(true)
		{
			double tmp = zetapow * lt[i++];
			if (tmp < 0x1p-52)
				break;
			
			r += tmp;
			zetapow *= zeta2;
		}
		
		return m * LN2 + 2 * zeta * r;
	}
	
	private static double LOG_COEFF_00 = 2.0;
	private static double LOG_COEFF_01 = 0.666666666666666666666666666667;
	private static double LOG_COEFF_02 = 0.4;
	private static double LOG_COEFF_03 = 0.285714285714285714285714285714;
	private static double LOG_COEFF_04 = 0.222222222222222222222222222222;
	private static double LOG_COEFF_05 = 0.181818181818181818181818181818;
	private static double LOG_COEFF_06 = 0.153846153846153846153846153846;
	private static double LOG_COEFF_07 = 0.133333333333333333333333333333;
	private static double LOG_COEFF_08 = 0.117647058823529411764705882353;
	private static double LOG_COEFF_09 = 0.105263157894736842105263157895;
	private static double LOG_COEFF_10 = 0.0952380952380952380952380952381;
	private static double LOG_COEFF_11 = 0.0869565217391304347826086956522;
	private static double LOG_COEFF_12 = 0.08;
	
	/**
	 * Natural log function. Returns log(x) to base E.
	 */
	public static double log2(double x)
	{
		// also catches NaN
		if (!(x > 0))
			return (x == 0) ? Double.NEGATIVE_INFINITY : Double.NaN;
		if (x == Double.POSITIVE_INFINITY)
			return Double.POSITIVE_INFINITY;
	
		// Algorithm has been derived from the one given at
		// http://www.geocities.com/zabrodskyvlada/aat/a_contents.html 

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
		m = (m + (int)(bits >>> 52)) << 2;
		bits = (bits & 0x000FFFFFFFFFFFFFL) | 0x3FF0000000000000L;
		x = Double.longBitsToDouble(bits);
		
		if (x > SQRT2)
		{
			m+=2;
			x *= INV_SQRT2;
		}
		if (x > SQRTSQRT2)
		{
			m++;
			x *= INV_SQRTSQRT2;
		}
		
		double zeta = (x - 1.0) / (x + 1.0);
		double zeta2 = zeta * zeta;		
		
		//known ranges:
		//	1 <= $x < 1.18
		//  0 <= $zeta < 0.0864
		//  0 <= $zeta2 < 0.00747

		double r = LOG_COEFF_00+(LOG_COEFF_01+(LOG_COEFF_02+(LOG_COEFF_03+(LOG_COEFF_04+(LOG_COEFF_05+(LOG_COEFF_06+(LOG_COEFF_07+(LOG_COEFF_08+(LOG_COEFF_09)*zeta2)*zeta2)*zeta2)*zeta2)*zeta2)*zeta2)*zeta2)*zeta2)*zeta2;
		
		return m * LN_SQRTSQRT2 + zeta * r;
	}
	
	/**
	 * Exponential function.
	 * Returns E^x (where E is the base of natural logarithms).
	 */
	public static double exp(double x)
	{
		// also catches NaN
		if (!(x > -750))
			return (x < 0) ? 0 : Double.NaN;
		if (x > 710)
			return Double.POSITIVE_INFINITY;

		int k = (int)(x * INV_LN_SQRTSQRT2);
		if (x < 0)
			k--;
		x -= k * LN_SQRTSQRT2;
		
		//known ranges:
		//	0 <= $x <= LN_SQRTSQRT2
		//ergo:
		//  $xpow will converge quickly towards 0

		double sum = 1;
		double xpow = x;
		int fac = 2;

		while (true)
		{
			if (xpow < 0x1p-52)
				break;
			
			sum += xpow;
			xpow = xpow * x / fac++;
		}
		
		double f1;
		if (k > 4000)
		{
			k -= 4000;
			f1 = 0x1p+1000; 
		}
		else if (k < -4000)
		{
			k += 4000;
			f1 = 0x1p-1000; 
		}
		else
			f1 = 1.0;
		
		double f2 = Double.longBitsToDouble((long)((k >> 2) + 1023) << 52);
		if ((k & 2) != 0)
			f2 *= SQRT2;
		if ((k & 1) != 0)
			f2 *= SQRTSQRT2;
		
		return sum * f2 * f1;
	}
	
	private static class ExpTable
	{
		public final static double[] EXPTABLE =
			{
				1.,
				1.06449445891785942956339059464,
				1.13314845306682631682900722781,
			    1.20623024942098071065558601045,
			    1.28402541668774148407342056806,
			    1.36683794117379636283875677272,
			    1.45499141461820133605379369199,
			    1.54883029863413309799855198460,
			    1.64872127070012814684865078781,
			    1.75505465696029855724404703660,
			    1.86824595743222240650183562019,
			    1.98873746958229183111747734965
			};
	}
	
	private final static double EXPSTEP = 0.0625;
	private final static double INV_EXPSTEP = 16;
	
	
	/**
	 * Exponential function.
	 * Returns E^x (where E is the base of natural logarithms).
	 */
	public static double exp2(double x)
	{
		// also catches NaN
		if (!(x > -750))
			return (x < 0) ? 0 : Double.NaN;
		if (x > 710)
			return Double.POSITIVE_INFINITY;

		int k1 = (int)(x * INV_LN2);
		if (x < 0)
			k1--;
		x -= k1 * LN2;
		
		int k2 = (int)(x * INV_EXPSTEP);
		x -= k2 * EXPSTEP;
		
		//known ranges:
		//	0 <= $x <= LN_SQRTSQRT2
		//ergo:
		//  $xpow will converge quickly towards 0

		double sum = 1;
		double xpow = x;
		int fac = 2;

		while (true)
		{
			if (xpow < 0x1p-52)
				break;
			
			sum += xpow;
			xpow = xpow * x / fac++;
		}
		
		sum *= ExpTable.EXPTABLE[k2];
		
		if (k1 > 1000)
		{
			k1 -= 1000;
			sum *= 0x1p+1000; 
		}
		else if (k1 < -1000)
		{
			k1 += 1000;
			sum *= 0x1p-1000; 
		}
		
		double f2 = Double.longBitsToDouble((long)(k1 + 1023) << 52);
		
		return sum * f2;
	}
	
	// Coefficients of Remez[11,0] approximation of exp(x) for x=0..ln(2)
	private static final double EXP_COEFF_00 = 0.999999999999999996945413312322;
	private static final double EXP_COEFF_01 = 1.00000000000000133475235568738;
	private static final double EXP_COEFF_02 = 0.499999999999904260125463328703;
	private static final double EXP_COEFF_03 = 0.166666666669337812408704211755;
	private static final double EXP_COEFF_04 = 0.416666666283889843730385141088e-1;
	private static final double EXP_COEFF_05 = 0.833333365529436919373436515228e-2;
	private static final double EXP_COEFF_06 = 0.138888718050843901239114642134e-2;
	private static final double EXP_COEFF_07 = 0.198418635994059844531320564776e-3;
	private static final double EXP_COEFF_08 = 0.247878999398272729584741635853e-4;
	private static final double EXP_COEFF_09 = 0.277640957428419777962278449310e-5;
	private static final double EXP_COEFF_10 = 0.256024855062292883779591833098e-6;
	private static final double EXP_COEFF_11 = 0.353472834562099171303604425909e-7;
	
	/**
	 * Exponential function.
	 * Returns E^x (where E is the base of natural logarithms).
	 */
	public static double exp3(double x)
	{
		// also catches NaN
		if (!(x > -750))
			return (x < 0) ? 0 : Double.NaN;
		if (x > 710)
			return Double.POSITIVE_INFINITY;

		int k = (int)(x * INV_LN2);
		if (x < 0)
			k--;
		x -= k * LN2;
		
		double f1 = EXP_COEFF_00+(EXP_COEFF_01+(EXP_COEFF_02+(EXP_COEFF_03+(EXP_COEFF_04+(EXP_COEFF_05+(EXP_COEFF_06+(EXP_COEFF_07+(EXP_COEFF_08+(EXP_COEFF_09+(EXP_COEFF_10+(EXP_COEFF_11)*x)*x)*x)*x)*x)*x)*x)*x)*x)*x)*x;

		if (k > 1000)
		{
			k -= 1000;
			f1 *= 0x1p+1000; 
		}
		else if (k < -1000)
		{
			k += 1000;
			f1 *= 0x1p-1000; 
		}
		
		double f2 = Double.longBitsToDouble((long)(k + 1023) << 52);		
		
		return f1 * f2;
	}
	
	public static String doubleToString(double x)
	{
		char[] sb = new char[25];
		int p = doubleToString(x, sb, 0);
		return new String(sb, 0, p);
	}
	
	private static int doubleToString(double x, char[] sb, int p)
	{
		if (x != x)
		{
			STR_NAN.getChars(0, STR_NAN_LEN, sb, p);
			return p + STR_NAN_LEN;
		}
		
		//we need to detect -0.0 to be compatible with JDK
		long bits = Double.doubleToRawLongBits(x); 
		if ((bits & 0x8000000000000000L) != 0)
		{
			sb[p++] = '-';
			x = -x;
		}
		
		if (x == 0)
		{
			sb[p] = '0';
			sb[p+1] = '.';
			sb[p+2] = '0';
			return p+3;
		}
		if (x == Double.POSITIVE_INFINITY)
		{
			STR_INFINITY.getChars(0, STR_INFINITY_LEN, sb, p);
			return p + STR_INFINITY_LEN;
		}
		
		int exp;		
		if (x >= Double.MIN_NORMAL)
			exp = 62;
		else
		{
			exp = 0;
			bits = Double.doubleToRawLongBits(x * 0x1p62);
		}
		
		exp += (int)(bits >> 52) & 0x7FF;
		exp = ((exp * 631305) >> 21) - 327;
		
		// at this point, the following should always hold:
		//   floor(log(10, x)) - 1 < exp <= floor(log(10, x))
		x = pow10d_bf(x, 14 - exp);
		
		long tmp = 1000000000000000L;
		long digits = (long)(x + 0.5);
		
		if (digits >= tmp)
		{
			exp++;
			digits = (long)(x * 0.1 + 0.5);
		}
		
		// algorithm shows true value of subnormal doubles
		// unfortunatly, the mantisse of subnormal values gets very short
		// TODO automatically adjust digit count for subnormal values
		
		int leading = 0;
		if (exp > 0 && exp < 7)
		{
			leading = exp;
			exp = 0;
		}

		for (int i=0; i<=leading; i++)
		{
			int d = (int)(digits / (tmp /= 10));
			sb[p++] = (char)('0' + d);
			digits -= tmp * d;
		}
		
		sb[p++] = '.';		
		do
		{
			int d = (int)(digits / (tmp /= 10));
			sb[p++] = (char)('0' + d);
			digits -= tmp * d;
		}
		while (digits > 0);
		
		if (exp != 0)
		{
			sb[p++] = 'E';
			if (exp < 0)
			{
				sb[p++] = '-';
				exp = -exp;
			}			
			if (exp >= 100)
				sb[p++] = (char)(exp / 100 + '0');
			if (exp >= 10)
				sb[p++] = (char)(exp / 10 % 10 + '0');
			sb[p++] = (char)(exp % 10 + '0');
		}

		return p;
	}

	private static class Pow10FConstants
	{
		public static final float[] POW10F_1 = {1E+1f, 1E+2f, 1E+4f, 1E+8f, 1E+16f, 1E+32f, };
		public static final float[] POW10F_2 = {1E-1f, 1E-2f, 1E-4f, 1E-8f, 1E-16f, 1E-32f, };	
	}
	
	private static class Pow10DConstants
	{
		public static final double[] POW10D_1 = {1E+1, 1E+2, 1E+4, 1E+8, 1E+16, 1E+32, 1E+64, 1E+128, 1E+256, };
		public static final double[] POW10D_2 = {1E-1, 1E-2, 1E-4, 1E-8, 1E-16, 1E-32, 1E-64, 1E-128, 1E-256, };		
	}
	
	private static double pow10d_bf(double r, int e)
	{
		// bf stand for "Big exponent First"
		
		double[] b;
		if (e >= 0)
			b = Pow10DConstants.POW10D_1;
		else
		{
			b = Pow10DConstants.POW10D_2;
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
	
	private static double pow10d_sf(double r, int e)
	{
		// sf stand for "Small exponent First"
		
		double[] b;
		if (e >= 0)
			b = Pow10DConstants.POW10D_1;
		else
		{
			b = Pow10DConstants.POW10D_2;
			e = -e;
		}
		
		if ((e & 0x001) != 0) r *= b[0];
		if ((e & 0x002) != 0) r *= b[1];
		if ((e & 0x004) != 0) r *= b[2];
		if ((e & 0x008) != 0) r *= b[3];
		if ((e & 0x010) != 0) r *= b[4];
		if ((e & 0x020) != 0) r *= b[5];
		if ((e & 0x040) != 0) r *= b[6];
		if ((e & 0x080) != 0) r *= b[7];
		if ((e & 0x100) != 0) r *= b[8];
		
		return r;
	}
	
	public static String floatToString(float x)
	{
		char[] sb = new char[15];
		int p = floatToString(x, sb, 0);
		return new String(sb, 0, p);
	}
	
	private static int floatToString(float x, char[] sb, int p)
	{
		if (x != x)
		{
			STR_NAN.getChars(0, STR_NAN_LEN, sb, p);
			return p + STR_NAN_LEN;
		}
		
		//we need to detect -0.0 to be compatible with JDK
		int bits = Float.floatToRawIntBits(x); 
		if ((bits & 0x80000000) != 0)
		{
			sb[p++] = '-';
			x = -x;
		}
		
		if (x == 0)
		{
			sb[p] = '0';
			sb[p+1] = '.';
			sb[p+2] = '0';
			return p+3;
		}
		if (x == Float.POSITIVE_INFINITY)
		{
			STR_INFINITY.getChars(0, STR_INFINITY_LEN, sb, p);
			return p + STR_INFINITY_LEN;
		}
		
		int exp;		
		if (x >= Float.MIN_NORMAL)
			exp = 31;
		else
		{
			exp = 0;
			bits = Float.floatToRawIntBits(x * 0x1p31f);
		}
		
		exp += (bits >> 23) & 0xFF;
		exp = ((exp * 5050455) >> 24) - 48;
		
		// at this point, the following should always hold:
		//   floor(log(10, x)) - 1 < exp <= floor(log(10, x))
		x = pow10f_bf(x, 6 - exp);
		
		int tmp = 10000000;
		int digits = (int)(x + 0.5f);
		
		if (digits >= tmp)
		{
			exp++;
			digits = (int)(x * 0.1f + 0.5f);
		}
		
		// algorithm shows true value of subnormal doubles
		// unfortunatly, the mantisse of subnormal values gets very short
		// TODO automatically adjust digit count for subnormal values
		
		int leading = 0;
		if (exp > 0 && exp < 6)
		{
			leading = exp;
			exp = 0;
		}

		for (int i=0; i<=leading; i++)
		{
			int d = digits / (tmp /= 10);
			sb[p++] = (char)('0' + d);
			digits -= tmp * d;
		}
		
		sb[p++] = '.';
		do
		{
			int d = digits / (tmp /= 10);
			sb[p++] = (char)('0' + d);
			digits -= tmp * d;
		}
		while (digits > 0);
		
		if (exp != 0)
		{
			sb[p++] = 'E';
			if (exp < 0)
			{
				sb[p++] = '-';
				exp = -exp;
			}			
			if (exp >= 10)
				sb[p++] = (char)(exp / 10 + '0');
			sb[p++] = (char)(exp % 10 + '0');
		}

		return p;
	}

	private static float pow10f_bf(float r, int e)
	{
		// bf stand for "Big exponent First"
		
		float[] b;
		if (e >= 0)
			b = Pow10FConstants.POW10F_1;
		else
		{
			b = Pow10FConstants.POW10F_2;
			e = -e;
		}
		
		if ((e & 0x020) != 0) r *= b[5];
		if ((e & 0x010) != 0) r *= b[4];
		if ((e & 0x008) != 0) r *= b[3];
		if ((e & 0x004) != 0) r *= b[2];
		if ((e & 0x002) != 0) r *= b[1];
		if ((e & 0x001) != 0) r *= b[0];
		
		return r;
	}
	
	private static float pow10f_sf(float r, int e)
	{
		// sf stand for "Small exponent First"
		
		float[] b;
		if (e >= 0)
			b = Pow10FConstants.POW10F_1;
		else
		{
			b = Pow10FConstants.POW10F_2;
			e = -e;
		}
		
		if ((e & 0x001) != 0) r *= b[0];
		if ((e & 0x002) != 0) r *= b[1];
		if ((e & 0x004) != 0) r *= b[2];
		if ((e & 0x008) != 0) r *= b[3];
		if ((e & 0x010) != 0) r *= b[4];
		if ((e & 0x020) != 0) r *= b[5];
		
		return r;
	}
	
	private static boolean checkString(String s, int p, String c)
	{
		int l = c.length();
		for (int i=0; i<l; i++)
			if (s.charAt(p+i) != c.charAt(i))
				return false;
		
		return true;
	}
	
	/**
	 * Roughly equals abs(minimal exponent of subnormal double in base 10) + digits of long 
	 */
	private static final int STR_TO_DOUBLE_MAXEXP = 350; 
	
	public static double stringToDouble(String s)
	{
		long r = 0;
		int exp = 0;
		
		int l = s.length();
		
		if (l <= 0)
			throw new NumberFormatException();
		
		int p;
		boolean neg;
		switch (s.charAt(0))
		{
			case '-':
				p = 1;
				neg = true;
				break;
			case '+':
				p = 1;
				neg = false;
				break;
			default:
				p = 0;
				neg = false;				
		}
		
		switch (l-p)
		{
			case STR_NAN_LEN:
				if (checkString(s, p, STR_NAN))
					return Double.NaN;
				break;
			case STR_INFINITY_LEN:
				if (checkString(s, p, STR_INFINITY))
					return neg ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
				break;
		}
		
		boolean digits = false;
		
		while (p < l)
		{
			char c = s.charAt(p);
			if (c < '0' || c > '9')
			{
				if (c == '.' || c == 'e' || c == 'E')
					break;
				
				throw new NumberFormatException();
			}
			
			digits = true;
			
			if (r <= (Long.MAX_VALUE - 9) / 10)
				r = r * 10 + (c - '0');
			else
				exp++;
			
			p++;
		}
		
		if (p < l && s.charAt(p) == '.')
		{
			p++;
			
			while (p < l)
			{
				char c = s.charAt(p);
				if (c < '0' || c > '9')
				{
					if (c == 'e' || c == 'E')
						break;
					
					throw new NumberFormatException();
				}
				
				digits = true;
				
				if (r <= (Long.MAX_VALUE - 9) / 10)
				{
					r = r * 10 + (c - '0');				
					exp--;
				}				
				p++;
			}			
		}
		
		if (!digits)
			throw new NumberFormatException();
		
		if (p < l)
		{
			//at this point, s.charAt(p) has to be 'e' or 'E'
			p++;
			
			boolean digitsexp = false;
			
			boolean negexp;
			if (p < l)
			{
				switch (s.charAt(p))
				{
					case '-':
						negexp = true;
						exp = -exp;
						p++;
						break;
					case '+':
						p++;
					default:
						negexp = false;
				}
			}
			else
				negexp = false;
			
			int exp2 = 0;
			while (p < l)
			{
				char c = s.charAt(p);
				if (c < '0' || c > '9')
					throw new NumberFormatException();
				
				digitsexp = true;
				
				if (exp2 + exp < STR_TO_DOUBLE_MAXEXP)
					exp2 = exp2 * 10 + (c - '0');
				
				p++;
			}
			
			if (!digitsexp)
				throw new NumberFormatException();
			
			exp2 += exp;
			exp = negexp ? -exp2 : exp2;
		}
		
		double r2;
		if (exp < -STR_TO_DOUBLE_MAXEXP)
			r2 = 0.0;
		else if (exp > STR_TO_DOUBLE_MAXEXP)
			r2 = Double.POSITIVE_INFINITY;
		else
			r2 = pow10d_sf(r, exp);
		
		return neg ? -r2 : r2;
	}
	
	/**
	 * Roughly equals abs(minimal exponent of subnormal float in base 10) + digits of int 
	 */
	private static final int STR_TO_FLOAT_MAXEXP = 60; 
	
	public static float stringToFloat(String s)
	{
		int r = 0;
		int exp = 0;
		
		int l = s.length();
		
		if (l <= 0)
			throw new NumberFormatException();
		
		int p;
		boolean neg;
		switch (s.charAt(0))
		{
			case '-':
				p = 1;
				neg = true;
				break;
			case '+':
				p = 1;
				neg = false;
				break;
			default:
				p = 0;
				neg = false;				
		}
		
		switch (l-p)
		{
			case STR_NAN_LEN:
				if (checkString(s, p, STR_NAN))
					return Float.NaN;
				break;
			case STR_INFINITY_LEN:
				if (checkString(s, p, STR_INFINITY))
					return neg ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
				break;
		}
		
		boolean digits = false;
		
		while (p < l)
		{
			char c = s.charAt(p);
			if (c < '0' || c > '9')
			{
				if (c == '.' || c == 'e' || c == 'E')
					break;
				
				throw new NumberFormatException();
			}
			
			digits = true;
			
			if (r <= (Integer.MAX_VALUE - 9) / 10)
				r = r * 10 + (c - '0');
			else
				exp++;
			
			p++;
		}
		
		if (p < l && s.charAt(p) == '.')
		{
			p++;
			
			while (p < l)
			{
				char c = s.charAt(p);
				if (c < '0' || c > '9')
				{
					if (c == 'e' || c == 'E')
						break;
					
					throw new NumberFormatException();
				}
				
				digits = true;
				
				if (r <= (Integer.MAX_VALUE - 9) / 10)
				{
					r = r * 10 + (c - '0');				
					exp--;
				}				
				p++;
			}			
		}
		
		if (!digits)
			throw new NumberFormatException();
		
		if (p < l)
		{
			//at this point, s.charAt(p) has to be 'e' or 'E'
			p++;
			
			boolean digitsexp = false;
			
			boolean negexp;
			if (p < l)
			{
				switch (s.charAt(p))
				{
					case '-':
						negexp = true;
						exp = -exp;
						p++;
						break;
					case '+':
						p++;
					default:
						negexp = false;
				}
			}
			else
				negexp = false;
			
			int exp2 = 0;
			while (p < l)
			{
				char c = s.charAt(p);
				if (c < '0' || c > '9')
					throw new NumberFormatException();
				
				digitsexp = true;
				
				if (exp2 + exp < STR_TO_FLOAT_MAXEXP)
					exp2 = exp2 * 10 + (c - '0');
				
				p++;
			}
			
			if (!digitsexp)
				throw new NumberFormatException();
			
			exp2 += exp;
			exp = negexp ? -exp2 : exp2;
		}
		
		float r2;
		if (exp < -STR_TO_FLOAT_MAXEXP)
			r2 = 0.0f;
		else if (exp > STR_TO_FLOAT_MAXEXP)
			r2 = Float.POSITIVE_INFINITY;
		else
			r2 = pow10f_sf(r, exp);
		
		return neg ? -r2 : r2;
	}
	
	// Coefficients of taylor series of sin(x)
	private static final double SIN_COEFF_01 = +1.0000000000000000000000000000000000000000;
	private static final double SIN_COEFF_03 = -0.1666666666666666666666666666666666666667;
	private static final double SIN_COEFF_05 = +0.8333333333333333333333333333333333333333e-2;
	private static final double SIN_COEFF_07 = -0.1984126984126984126984126984126984126984e-3;
	private static final double SIN_COEFF_09 = +0.2755731922398589065255731922398589065256e-5;
	private static final double SIN_COEFF_11 = -0.2505210838544171877505210838544171877505e-7;
	private static final double SIN_COEFF_13 = +0.1605904383682161459939237717015494793273e-9;
	private static final double SIN_COEFF_15 = -0.7647163731819816475901131985788070444155e-12;
	private static final double SIN_COEFF_17 = +0.2811457254345520763198945583010320016233e-14;
	private static final double SIN_COEFF_19 = -0.8220635246624329716955981236872280749221e-17;
	private static final double SIN_COEFF_21 = +0.1957294106339126123084757437350543035529e-19;
	
	// Coefficients of taylor series of cos(x)
	private static final double COS_COEFF_00 = +1.0000000000000000000000000000000000000000;
	private static final double COS_COEFF_02 = -0.5000000000000000000000000000000000000000;
	private static final double COS_COEFF_04 = +0.4166666666666666666666666666666666666667e-1;
	private static final double COS_COEFF_06 = -0.1388888888888888888888888888888888888889e-2;
	private static final double COS_COEFF_08 = +0.2480158730158730158730158730158730158730e-4;
	private static final double COS_COEFF_10 = -0.2755731922398589065255731922398589065256e-6;
	private static final double COS_COEFF_12 = +0.2087675698786809897921009032120143231254e-8;
	private static final double COS_COEFF_14 = -0.1147074559772972471385169797868210566623e-10;
	private static final double COS_COEFF_16 = +0.4779477332387385297438207491117544027597e-13;
	private static final double COS_COEFF_18 = -0.1561920696858622646221636435005733342352e-15;
	private static final double COS_COEFF_20 = +0.4110317623312164858477990618436140374610e-18;

	private static double sin_taylor(double x)
	{
		double x2 = x * x;
		return (SIN_COEFF_01+(SIN_COEFF_03+(SIN_COEFF_05+(SIN_COEFF_07+(SIN_COEFF_09+(SIN_COEFF_11+(SIN_COEFF_13+(SIN_COEFF_15)*x2)*x2)*x2)*x2)*x2)*x2)*x2)*x;
	}
	
	private static double cos_taylor(double x)
	{
		double x2 = x * x;
		return (COS_COEFF_00+(COS_COEFF_02+(COS_COEFF_04+(COS_COEFF_06+(COS_COEFF_08+(COS_COEFF_10+(COS_COEFF_12+(COS_COEFF_14)*x2)*x2)*x2)*x2)*x2)*x2)*x2);
	}
	
	/**
	 * Sine function.
	 */
	public static double sin(double x)
	{		
		int neg = 0;
		
		//reduce to interval [-2PI, +2PI]
		x = x % PItwice;
		
		//reduce to interval [0, 2PI]
		if (x < 0)
		{
			neg++;
			x = -x;
		}
		
		//reduce to interval [0, PI]
		if (x > PI)
		{
			neg++;
			x -= PI;
		}
		
		//reduce to interval [0, PI/2]
		if (x > PIhalf)
			x = PI - x;	
		
		double y;		
		if (x < PIhalfhalf)
			y = sin_taylor(x);
		else
			y = cos_taylor(PIhalf - x);
		
		return ((neg & 1) == 0) ? y : -y;
	}
	
	/**
	 * Cosine function.
	 */
	public static double cos(double x)
	{
		int neg = 0;
		
		//reduce to interval [-2PI, +2PI]
		x = x % PItwice;
		
		//reduce to interval [0, 2PI]
		if (x < 0)
			x = -x;
		
		//reduce to interval [0, PI]
		if (x > PI)
		{
			neg++;
			x -= PI;
		}
		
		//reduce to interval [0, PI/2]
		if (x > PIhalf)
		{
			neg++;
			x = PI - x;
		}
		
		double y;		
		if (x < PIhalfhalf)
			y = cos_taylor(x);
		else
			y = sin_taylor(PIhalf - x);
		
		return ((neg & 1) == 0) ? y : -y;
	}

	/**
	 * Cosine function.
	 */
	public static double tan(double x)
	{
		int neg = 0;
		
		//reduce to interval [-PI, +PI]
		x = x % PI;
		
		//reduce to interval [0, PI]
		if (x < 0)
		{
			neg++;
			x = -x;
		}
		
		//reduce to interval [0, PI/2]
		if (x > PIhalf)
		{
			neg++;
			x = PI - x;
		}
		
		double y;
		if (x < PIhalfhalf)
			y = sin_taylor(x) / cos_taylor(x);
		else
		{
			double tmp = PIhalf - x;
			y = cos_taylor(tmp) / sin_taylor(tmp);
		}
		
		return ((neg & 1) == 0) ? y : -y;
	}

}
