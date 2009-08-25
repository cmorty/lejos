package org.lejos.nxt.benchmark.workbench;

public class BetaMath
{
	
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
	
}
