package java.lang;

public class MathTest {
	
	private static final double[] FLOORCEIL_INPUT = { 
		-2.5, -2.3, -2.0, -1.7, -1.5, -1.3, -1.0, -0.7, -0.5, -0.3, -0.0,
		0.0, 0.3, 0.5, 0.7, 1.0, 1.3, 1.5, 1.7, 2.0, 2.3, 2.5, 
	};
	private static final double[] FLOOR_EXPECT = {
		-3.0, -3.0, -2.0, -2.0, -2.0, -2.0, -1.0, -1.0, -1.0, -1.0, -0.0,
		0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 
	};
	private static final double[] CEIL_EXPECT = {
		-2.0, -2.0, -2.0, -1.0, -1.0, -1.0, -1.0, -0.0, -0.0, -0.0, -0.0,
		0.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, 
	};
	
	

	private static void assertEqualsExact(float actual, float expected) {
		int a2 = Float.floatToIntBits(actual);
		int e2 = Float.floatToIntBits(expected);
		assertEquals(a2, e2);
	}

	private static void assertEqualsExact(double actual, double expected) {
		long a2 = Double.doubleToLongBits(actual);
		long e2 = Double.doubleToLongBits(expected);
		assertEquals(a2, e2);
	}


	private static void assertEquals(long actual, long expected) {
		if (actual != expected)
			throw new RuntimeException("expected "+expected+" but was "+actual);
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i=0; i<FLOORCEIL_INPUT.length; i++)
			assertEqualsExact(Math.floor(FLOORCEIL_INPUT[i]), FLOOR_EXPECT[i]);
		
		for (int i=0; i<FLOORCEIL_INPUT.length; i++)
			assertEqualsExact(Math.ceil(FLOORCEIL_INPUT[i]), CEIL_EXPECT[i]);
	}


}
