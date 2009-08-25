package java.lang;

import java.util.Random;

/**
 * Mathematical functions.
 * 
 * @author <a href="mailto:bbagnall@mst.net">Brian Bagnall</a>
 */
public final class Math
{

	// Math constants
	public static final double E = 2.71828182845904523536028747135;
	public static final double PI = 3.14159265358979323846264338328;

	static final double LN2 = 0.693147180559945309417232121458;
	static final double LN10 = 2.30258509299404568401799145468;
	
	private static final double SQRT2 = 1.41421356237309504880168872421;
	private static final double LN_SQRT2 = 0.346573590279972654708616060729;	
	
	private static final double INV_LN2 = 1.44269504088896340735992468100;
	private static final double INV_SQRT2 = 0.707106781186547524400844362105;

	private static final double PIhalf = PI * 0.5;
	private static final double PIhalfhalf = PI * 0.25;
	private static final double PItwice = PI * 2.0;
	
	private static final double DEG_TO_RAD = 0.0174532925199432957692369076849;
	private static final double RAD_TO_DEG = 57.2957795130823208767981548141;
	
	// dividing by 2 for some kind of safety margin
	private static final float ROUND_FLOAT_MAX = Integer.MAX_VALUE >> 1;
	private static final float ROUND_FLOAT_MIN = -ROUND_FLOAT_MAX;
	// dividing by 2 for some kind of safety margin
	private static final double ROUND_DOUBLE_MAX = Long.MAX_VALUE >> 1;
	private static final double ROUND_DOUBLE_MIN = -ROUND_DOUBLE_MAX;

	// Used to generate random numbers.
	private static Random RAND = new Random(System.currentTimeMillis());

	// public static boolean isNaN (double d) {
	// return d != d;
	// }

	private Math()
	{
		// private constructor to make sure this class is not instantiated
	}

	// Private because it only works when -1 < x < 1 but it is used by atan2
	private static double ArcTan(double x)
	{
		// Using a Chebyshev-Pade approximation
		double x2 = x * x;
		return (0.7162721433f + 0.2996857769f * x2) * x
				/ (0.7163164576f + (0.5377299313f + 0.3951620469e-1f * x2) * x2);
	}
	
	/*========================= abs functions =========================*/ 

	/**
	 * Returns the absolute value of a double value. If the argument is not
	 * negative, the argument is returned. If the argument is negative, the
	 * negation of the argument is returned.
	 */
	public static double abs(double a)
	{
		// according to http://www.concentric.net/~Ttwang/tech/javafloat.htm
		return ((a <= 0.0) ? 0.0 - a : a);
	}

	/**
	 * Returns the absolute value of a float value. If the argument is not
	 * negative, the argument is returned. If the argument is negative, the
	 * negation of the argument is returned.
	 */
	public static float abs(float a)
	{
		// according to http://www.concentric.net/~Ttwang/tech/javafloat.htm
		return ((a <= 0.0f) ? 0.0f - a : a);
	}

	/**
	 * Returns the absolute value of a long value. If the argument is not
	 * negative, the argument is returned. If the argument is negative, the
	 * negation of the argument is returned.
	 */
	public static long abs(long a)
	{
		// TODO document that Long.MIN_VALUE == -Long.MIN_VALUE
		return ((a < 0) ? -a : a);
	}

	/**
	 * Returns the absolute value of an integer value. If the argument is not
	 * negative, the argument is returned. If the argument is negative, the
	 * negation of the argument is returned.
	 */
	public static int abs(int a)
	{
		// TODO document that Integer.MIN_VALUE == -Integer.MIN_VALUE
		return ((a < 0) ? -a : a);
	}

	/*========================= signum functions =========================*/ 

	public static float signum(float f)
	{
		if (f == 0)
			return f; // preserve -0.0 and 0.0

		if (f > 0)
			return 1;
		if (f < 0)
			return -1;

		return Float.NaN;
	}

	public static double signum(double d)
	{
		if (d == 0)
			return d; // preserve -0.0 and 0.0

		if (d > 0)
			return 1;
		if (d < 0)
			return -1;

		return Double.NaN;
	}

	/*========================= min/max functions =========================*/ 

	/**
	 * Returns the lesser of two integer values.
	 */
	public static int min(int a, int b)
	{
		return ((a < b) ? a : b);
	}

	/**
	 * Returns the lesser of two long values.
	 */
	public static long min(long a, long b)
	{
		return ((a < b) ? a : b);
	}

	/**
	 * Returns the lesser of two float values.
	 */
	public static float min(float a, float b)
	{
		return ((a < b) ? a : b);
	}

	/**
	 * Returns the lesser of two double values.
	 */
	public static double min(double a, double b)
	{
		return ((a < b) ? a : b);
	}

	/**
	 * Returns the greater of two integer values.
	 */
	public static int max(int a, int b)
	{
		return ((a > b) ? a : b);
	}

	/**
	 * Returns the greater of two long values.
	 */
	public static long max(long a, long b)
	{
		return ((a > b) ? a : b);
	}

	/**
	 * Returns the greater of two float values.
	 */
	public static float max(float a, float b)
	{
		return ((a > b) ? a : b);
	}

	/**
	 * Returns the greater of two double values.
	 */
	public static double max(double a, double b)
	{
		return ((a > b) ? a : b);
	}

	/*========================= rounding functions =========================*/ 

	/**
	 * Returns the largest (closest to positive infinity) double value that is
	 * not greater than the argument and is equal to a mathematical integer.
	 */
	public static double floor(double a)
	{
		// no rounding required
		if (a < ROUND_DOUBLE_MIN || a > ROUND_DOUBLE_MAX)
			return a;

		long b = (long) a;
		double bd = b;

		// if positive, just strip decimal places
		if (b >= 0)
			return bd;

		// if numbers are equal, there were no decimal places
		if (bd == a)
			return bd;

		// round down since a must have had some decimal places
		return bd - 1;
	}

	/**
	 * Returns the smallest (closest to negative infinity) double value that is
	 * not less than the argument and is equal to a mathematical integer.
	 */
	public static double ceil(double a)
	{
		// no rounding required
		if (a < ROUND_DOUBLE_MIN || a > ROUND_DOUBLE_MAX)
			return a;

		long b = (long) a;
		double bd = b;

		// if negative, just strip decimal places
		if (b <= 0)
			return bd;

		// if numbers are equal, there were no decimal places
		if (bd == a)
			return bd;

		// round up since a must have had some decimal places
		return bd + 1;
	}

	/**
	 * Returns the closest int to the argument.
	 */
	public static int round(float a)
	{
		// no rounding required
		if (a < ROUND_FLOAT_MIN || a > ROUND_FLOAT_MAX)
			return (int) a;

		return (int) Math.floor(a + 0.5);
	}

	/**
	 * Returns the closest int to the argument.
	 */
	public static long round(double a)
	{
		return (long) Math.floor(a + 0.5);
	}

	/**
	 * Returns the closest mathematical integer to the argument.
	 */
	public static double rint(double a)
	{
		// no rounding required
		if (a < ROUND_DOUBLE_MIN || a > ROUND_DOUBLE_MAX)
			return a;

		if (a < 0)
			return (long) (a - 0.5);

		return (long) (a + 0.5);
	}

	/*========================= random functions =========================*/ 

	/**
	 * Random number generator. Returns a double greater than 0.0 and less than
	 * 1.0
	 */
	public static synchronized double random()
	{
		int n = Integer.MAX_VALUE;

		// Just to ensure it does not return 1.0
		while (n == Integer.MAX_VALUE)
			n = abs(RAND.nextInt());

		return n * (1.0 / Integer.MAX_VALUE);
	}

	/*========================= arithmetic functions =========================*/ 

	/**
	 * Computes square-root of x.
	 */
	public static double sqrt(double x)
	{
		// @author Sven Köhler
		
		// also catches NaN
		if (!(x > 0))
			return (x == 0) ? x : Double.NaN;
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
		
		// magic constant function for good approximation of 1/sqrt(x)
		// according to http://www.lomont.org/Math/Papers/2003/InvSqrt.pdf
		// also look at http://en.wikipedia.org/wiki/Fast_inverse_square_root
		double isqrt = Double.longBitsToDouble(0x5fe6ec85e7de30daL - (Double.doubleToRawLongBits(x) >> 1));
		
		// 3 newton steps for 1/sqrt(x)
		double xhalf = 0.5 * x;
		isqrt = isqrt * (1.5 - xhalf * isqrt * isqrt);
		isqrt = isqrt * (1.5 - xhalf * isqrt * isqrt);
		isqrt = isqrt * (1.5 - xhalf * isqrt * isqrt);
		
		// 1 newton step for sqrt(x)
		return factor * (x * isqrt + 1.0 / isqrt);
	}
	
	/*========================= exp/log/pow functions =========================*/ 

	// Coefficients of Remez[11,0] approximation of exp(x) for x=0..ln(2)
	private static final double COEFF_EXP_00 = 0.999999999999999996945413312322;
	private static final double COEFF_EXP_01 = 1.00000000000000133475235568738;
	private static final double COEFF_EXP_02 = 0.499999999999904260125463328703;
	private static final double COEFF_EXP_03 = 0.166666666669337812408704211755;
	private static final double COEFF_EXP_04 = 0.416666666283889843730385141088e-1;
	private static final double COEFF_EXP_05 = 0.833333365529436919373436515228e-2;
	private static final double COEFF_EXP_06 = 0.138888718050843901239114642134e-2;
	private static final double COEFF_EXP_07 = 0.198418635994059844531320564776e-3;
	private static final double COEFF_EXP_08 = 0.247878999398272729584741635853e-4;
	private static final double COEFF_EXP_09 = 0.277640957428419777962278449310e-5;
	private static final double COEFF_EXP_10 = 0.256024855062292883779591833098e-6;
	private static final double COEFF_EXP_11 = 0.353472834562099171303604425909e-7;
	
	// Coefficients of the zeta-series of ln(x)
	private static double COEFF_LOG_01 = 2.0;
	private static double COEFF_LOG_03 = 0.666666666666666666666666666667;
	private static double COEFF_LOG_05 = 0.4;
	private static double COEFF_LOG_07 = 0.285714285714285714285714285714;
	private static double COEFF_LOG_09 = 0.222222222222222222222222222222;
	private static double COEFF_LOG_11 = 0.181818181818181818181818181818;
	private static double COEFF_LOG_13 = 0.153846153846153846153846153846;
	private static double COEFF_LOG_15 = 0.133333333333333333333333333333;
	private static double COEFF_LOG_17 = 0.117647058823529411764705882353;
	private static double COEFF_LOG_19 = 0.105263157894736842105263157895;
	
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

		int k = (int)(x * INV_LN2);
		if (x < 0)
			k--;
		x -= k * LN2;
		
		double f1 = COEFF_EXP_00+(COEFF_EXP_01+(COEFF_EXP_02+(COEFF_EXP_03+(COEFF_EXP_04+(COEFF_EXP_05+(COEFF_EXP_06+(COEFF_EXP_07+(COEFF_EXP_08+(COEFF_EXP_09+(COEFF_EXP_10+(COEFF_EXP_11)*x)*x)*x)*x)*x)*x)*x)*x)*x)*x)*x;

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
		m = (m + (int)(bits >>> 52)) << 1;
		bits = (bits & 0x000FFFFFFFFFFFFFL) | 0x3FF0000000000000L;
		x = Double.longBitsToDouble(bits);
		
		if (x > SQRT2)
		{
			m++;
			x *= INV_SQRT2;
		}
		
		double zeta = (x - 1.0) / (x + 1.0);
		double zeta2 = zeta * zeta;		
		
		//known ranges:
		//	1 <= $x < 1.41
		//  0 <= $zeta < 0.172
		//  0 <= $zeta2 < 0.0194

		double r = (COEFF_LOG_01+(COEFF_LOG_03+(COEFF_LOG_05+(COEFF_LOG_07+(COEFF_LOG_09+(COEFF_LOG_11+(COEFF_LOG_13+(COEFF_LOG_15+(COEFF_LOG_17+(COEFF_LOG_19)*zeta2)*zeta2)*zeta2)*zeta2)*zeta2)*zeta2)*zeta2)*zeta2)*zeta2)*zeta;		
		return m * LN_SQRT2 + r;
	}
	
	/**
	 * Power function. This is a slow but accurate method. author David Edwards
	 */
	public static double pow(double a, double b)
	{
		return exp(b * log(a));
	}

	/*========================= trigonometric functions =========================*/ 

	/**
	 * Converts radians to degrees.
	 */
	public static double toDegrees(double angrad)
	{
		return angrad * RAD_TO_DEG;
	}

	/**
	 * Converts degrees to radians.
	 */
	public static double toRadians(double angdeg)
	{
		return angdeg * DEG_TO_RAD;
	}

	// Coefficients of taylor series of sin(x)
	private static final double COEFF_SIN_01 = +1.0000000000000000000000000000000000000000;
	private static final double COEFF_SIN_03 = -0.1666666666666666666666666666666666666667;
	private static final double COEFF_SIN_05 = +0.8333333333333333333333333333333333333333e-2;
	private static final double COEFF_SIN_07 = -0.1984126984126984126984126984126984126984e-3;
	private static final double COEFF_SIN_09 = +0.2755731922398589065255731922398589065256e-5;
	private static final double COEFF_SIN_11 = -0.2505210838544171877505210838544171877505e-7;
	private static final double COEFF_SIN_13 = +0.1605904383682161459939237717015494793273e-9;
	private static final double COEFF_SIN_15 = -0.7647163731819816475901131985788070444155e-12;
	private static final double COEFF_SIN_17 = +0.2811457254345520763198945583010320016233e-14;
	
	// Coefficients of taylor series of cos(x)
	private static final double COEFF_COS_00 = +1.0000000000000000000000000000000000000000;
	private static final double COEFF_COS_02 = -0.5000000000000000000000000000000000000000;
	private static final double COEFF_COS_04 = +0.4166666666666666666666666666666666666667e-1;
	private static final double COEFF_COS_06 = -0.1388888888888888888888888888888888888889e-2;
	private static final double COEFF_COS_08 = +0.2480158730158730158730158730158730158730e-4;
	private static final double COEFF_COS_10 = -0.2755731922398589065255731922398589065256e-6;
	private static final double COEFF_COS_12 = +0.2087675698786809897921009032120143231254e-8;
	private static final double COEFF_COS_14 = -0.1147074559772972471385169797868210566623e-10;
	private static final double COEFF_COS_16 = +0.4779477332387385297438207491117544027597e-13;

	// Coefficients of pade-approximation of tan(x)
	private static final double COEFF_TAN_A01 = +34459425;
	private static final double COEFF_TAN_A03 = -4729725;
	private static final double COEFF_TAN_A05 = +135135;
	private static final double COEFF_TAN_A07 = -990;
	private static final double COEFF_TAN_A09 = +1;
	private static final double COEFF_TAN_B00 = +34459425;
	private static final double COEFF_TAN_B02 = -16216200;
	private static final double COEFF_TAN_B04 = +945945;
	private static final double COEFF_TAN_B06 = -13860;
	private static final double COEFF_TAN_B08 = +45;

	private static double sin_taylor(double x)
	{
		double x2 = x * x;
		return (COEFF_SIN_01+(COEFF_SIN_03+(COEFF_SIN_05+(COEFF_SIN_07+(COEFF_SIN_09+(COEFF_SIN_11+(COEFF_SIN_13+(COEFF_SIN_15+(COEFF_SIN_17)*x2)*x2)*x2)*x2)*x2)*x2)*x2)*x2)*x;
	}
	
	private static double cos_taylor(double x)
	{
		double x2 = x * x;
		return (COEFF_COS_00+(COEFF_COS_02+(COEFF_COS_04+(COEFF_COS_06+(COEFF_COS_08+(COEFF_COS_10+(COEFF_COS_12+(COEFF_COS_14+(COEFF_COS_16)*x2)*x2)*x2)*x2)*x2)*x2)*x2)*x2);
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
		
		boolean inv = x > PIhalfhalf;
		if (inv)
			x = PIhalf - x;
		
		double x2 = x * x;
		double a = (COEFF_TAN_A01+(COEFF_TAN_A03+(COEFF_TAN_A05+(COEFF_TAN_A07+(COEFF_TAN_A09)*x2)*x2)*x2)*x2)*x;
		double b = COEFF_TAN_B00+(COEFF_TAN_B02+(COEFF_TAN_B04+(COEFF_TAN_B06+(COEFF_TAN_B08)*x2)*x2)*x2)*x2;
		
		double y = inv ? b/a : a/b;		
		return ((neg & 1) == 0) ? y : -y;
	}

	/*==================== inverse trigonometric functions ====================*/ 

	/**
	 * Arc tangent function.
	 */
	public static double atan(double x)
	{
		return atan2(x, 1);
	}

	/**
	 * Arc tangent function valid to the four quadrants y and x can have any
	 * value without sigificant precision loss atan2(0,0) returns 0. author
	 * Paulo Costa
	 */
	public static double atan2(double y, double x)
	{
		float ax = (float) abs(x);
		float ay = (float) abs(y);

		if ((ax < 1e-7) && (ay < 1e-7))
			return 0f;

		if (ax > ay)
		{
			if (x < 0)
			{
				if (y >= 0)
					return ArcTan(y / x) + PI;
				else
					return ArcTan(y / x) - PI;
			}
			else
				return ArcTan(y / x);
		}
		else
		{
			if (y < 0)
				return ArcTan(-x / y) - PI / 2;
			else
				return ArcTan(-x / y) + PI / 2;
		}
	}

	/**
	 * Arc cosine function.
	 */
	public static double acos(double a)
	{
		if ((a < -1) || (a > 1))
		{
			return Double.NaN;
		}
		return PI / 2 - atan(a / sqrt(1 - a * a));
	}

	/**
	 * Arc sine function.
	 */
	public static double asin(double a)
	{
		return atan(a / sqrt(1 - a * a));
	}
}
