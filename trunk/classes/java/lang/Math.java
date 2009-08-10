package java.lang;

/**
 * Mathematical functions.
 * 
 * @author <a href="mailto:bbagnall@mst.net">Brian Bagnall</a>
 */
public final class Math
{

	// Math constants
	public static final double E = 2.71828182845904523536;
	public static final double PI = 3.14159265358979323846;

	static final double PI2 = PI / 2;
	static final double ln10 = 2.30258509299405;
	static final double ln2 = 0.693147180559945;

	// Used by log() and exp() methods
	// TODO: The lower bound is probably important for accuracy. Expand when
	// double working.
	private static final double LOWER_BOUND = 0.9999999f;
	private static final double UPPER_BOUND = 1.0D;

	private static final double EXP_REL_BOUND = 0x1.0p-52;
	
	// dividing by 2 for some kind of safety margin
	private static final float ROUND_FLOAT_MAX = Integer.MAX_VALUE >> 1;
	private static final float ROUND_FLOAT_MIN = -ROUND_FLOAT_MAX;
	// dividing by 2 for some kind of safety margin
	private static final double ROUND_DOUBLE_MAX = Long.MAX_VALUE >> 1;
	private static final double ROUND_DOUBLE_MIN = -ROUND_DOUBLE_MAX;

	// Used to generate random numbers.
	private static java.util.Random RAND = new java.util.Random(System.currentTimeMillis());

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

	/**
	 * Random number generator. Returns a double greater than 0.0 and less than
	 * 1.0
	 */
	public static double random()
	{
		final int MAX_INT = 2147483647;
		int n = MAX_INT;

		// Just to ensure it does not return 1.0
		while (n == MAX_INT)
			n = abs(RAND.nextInt());

		return n * (1.0 / MAX_INT);
	}

	/**
	 * Exponential function. Returns E^x (where E is the base of natural
	 * logarithms). author David Edwards
	 * 
	 */
	public static double exp(double a)
	{
		/**
		 * DEVELOPER NOTES: Martin E. Nielsen - modified code to handle large
		 * arguments. = sum a^n/n!, i.e. 1 + x + x^2/2! + x^3/3! Seems to work
		 * better for +ve numbers so force argument to be +ve.
		 */

		boolean neg = a < 0;
		if (neg)
			a = -a;
		
		double term = a;
		double sum = 1;

		for (int fac = 2; true; fac++)
		{
			if (term < sum * EXP_REL_BOUND)
				break;
			
			sum += term;
			term *= a / fac;
		}

		return neg ? 1.0 / sum : sum;
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
		if (x == 0)
			return Double.NaN;

		if (x < 1.0)
			return -log(1.0 / x);

		double m = 0.0;
		double p = 1.0;
		while (p <= x)
		{
			m++;
			p = p * 2;
		}

		m = m - 1;
		double z = x / (p / 2);

		double zeta = (1.0 - z) / (1.0 + z);
		double n = zeta;
		double ln = zeta;
		double zetasup = zeta * zeta;

		for (int j = 1; true; j++)
		{
			n = n * zetasup;
			double newln = ln + n / (2 * j + 1);
			double term = ln / newln;
			if (ln == newln || (term >= LOWER_BOUND && term <= UPPER_BOUND))
				return m * ln2 - 2 * ln;
			ln = newln;
		}
	}

	/**
	 * Power function. This is a slow but accurate method. author David Edwards
	 */
	public static double pow(double a, double b)
	{
		return exp(b * log(a));
	}

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

	/**
	 * Sine function using a Chebyshev-Pade approximation. author Paulo Costa
	 */
	public static double sin(double x) // Using a Chebyshev-Pade approximation
	{
		int n = (int) (x / PI2) + 1; // reduce to the 4th and 1st quadrants
		if (n < 1)
			n = n - 1;
		if ((n & 2) == 0)
			x = x - (n & 0xFFFFFFFE) * PI2; // if it from the 2nd or the 3rd
		// quadrants
		else
			x = -(x - (n & 0xFFFFFFFE) * PI2);

		double x2 = x * x;
		return (0.9238318854f - 0.9595498071e-1f * x2) * x
				/ (0.9238400690f + (0.5797298195e-1f + 0.2031791179e-2f * x2) * x2);
	}

	/**
	 * Cosine function using a Chebyshev-Pade approximation. author Paulo Costa
	 */
	public static double cos(double x)
	{
		int n = (int) (x / PI2) + 1;
		if (n < 1)
			n = n - 1;
		x = x - (n & 0xFFFFFFFE) * PI2; // reduce to the 4th and 1st quadrants

		double x2 = x * x;

		float si = 1f;
		if ((n & 2) != 0)
			si = -1f; // if it from the 2nd or the 3rd quadrants
		return si * (0.9457092528f + (-0.4305320537f + 0.1914993010e-1f * x2) * x2)
				/ (0.9457093212f + (0.4232119630e-1f + 0.9106317690e-3f * x2) * x2);
	}

	/**
	 * Computes square-root of x.
	 */
	public static double sqrt(double x)
	{
		// @author Sven Köhler
		
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
	
	/**
	 * Tangent function.
	 */
	public static double tan(double a)
	{
		return sin(a) / cos(a);
	}

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
			return Float.NaN; // TODO: We don't have Double.NaN yet, so using
			// Float for now.
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

	/**
	 * Converts radians to degrees.
	 */
	public static double toDegrees(double angrad)
	{
		return angrad * (180.0 / PI);
	}

	/**
	 * Converts degrees to radians.
	 */
	public static double toRadians(double angdeg)
	{
		return angdeg * (PI / 180.0);
	}
}
