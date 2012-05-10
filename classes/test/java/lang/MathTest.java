package java.lang;

public class MathTest {
	
	private static final double[] FLOORCEIL_INPUT = {
		Double.NEGATIVE_INFINITY, -2.5, -2.3, -2.0, -1.7, -1.5, -1.3, -1.0, -0.7, -0.5, -0.3, -0.0,
		0.0, 0.3, 0.5, 0.7, 1.0, 1.3, 1.5, 1.7, 2.0, 2.3, 2.5, Double.POSITIVE_INFINITY,
		Double.NaN,
	};
	private static final double[] FLOOR_EXPECT = {
		Double.NEGATIVE_INFINITY, -3.0, -3.0, -2.0, -2.0, -2.0, -2.0, -1.0, -1.0, -1.0, -1.0, -0.0,
		0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, Double.POSITIVE_INFINITY,
		Double.NaN,
	};
	private static final double[] CEIL_EXPECT = {
		Double.NEGATIVE_INFINITY, -2.0, -2.0, -2.0, -1.0, -1.0, -1.0, -1.0, -0.0, -0.0, -0.0, -0.0,
		0.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, Double.POSITIVE_INFINITY,
		Double.NaN,
	};
	
	private static final double[] ORDER = {
		Double.NEGATIVE_INFINITY, -2.5, -2.3, -2.0, -1.7, -1.5, -1.3, -1.0, -0.7, -0.5, -0.3, -0.0,
		0.0, 0.3, 0.5, 0.7, 1.0, 1.3, 1.5, 1.7, 2.0, 2.3, 2.5, Double.POSITIVE_INFINITY,
		Double.NaN,
	};
	

	private static void assertEqualsExact(float actual, float expected) {
		int a2 = Float.floatToIntBits(actual);
		int e2 = Float.floatToIntBits(expected);
		if (a2 != e2)
			throw new RuntimeException("expected "+expected+" but was "+actual);
	}

	private static void assertEqualsExact(double actual, double expected) {
		long a2 = Double.doubleToLongBits(actual);
		long e2 = Double.doubleToLongBits(expected);
		if (a2 != e2)
			throw new RuntimeException("expected "+expected+" but was "+actual);
	}


	private static void assertEquals(long actual, long expected) {
		if (actual != expected)
			throw new RuntimeException("expected "+expected+" but was "+actual);
	}
	
	private void testFloor() {
		for (int i=0; i<FLOORCEIL_INPUT.length; i++)
			assertEqualsExact(Math.floor(FLOORCEIL_INPUT[i]), FLOOR_EXPECT[i]);
	}
	
	private void testCeil() {
		for (int i=0; i<FLOORCEIL_INPUT.length; i++)
			assertEqualsExact(Math.ceil(FLOORCEIL_INPUT[i]), CEIL_EXPECT[i]);
	}

	private void testCompare() {
		assertEquals(Double.compare(ORDER[0], ORDER[0]), 0);
		for (int i=1; i<ORDER.length; i++)
		{
			assertEquals(Double.compare(ORDER[i-1], ORDER[i]), -1);
			assertEquals(Double.compare(ORDER[i], ORDER[i-1]), 1);
			assertEquals(Double.compare(ORDER[i], ORDER[i]), 0);
		}
	}

	private void testMin() {
		for (int i=0; i<ORDER.length-1; i++)
		{
			assertEqualsExact(Math.min(Float.NaN, (float)ORDER[i]), Float.NaN);
			assertEqualsExact(Math.min((float)ORDER[i], Float.NaN), Float.NaN);
			assertEqualsExact(Math.min(Double.NaN, ORDER[i]), Double.NaN);
			assertEqualsExact(Math.min(ORDER[i], Double.NaN), Double.NaN);
		}
		for (int i=1; i<ORDER.length-1; i++)
		{
			assertEqualsExact(Math.min((float)ORDER[i-1], (float)ORDER[i]), (float)ORDER[i-1]);
			assertEqualsExact(Math.min((float)ORDER[i], (float)ORDER[i-1]), (float)ORDER[i-1]);
			assertEqualsExact(Math.min(ORDER[i-1], ORDER[i]), ORDER[i-1]);
			assertEqualsExact(Math.min(ORDER[i], ORDER[i-1]), ORDER[i-1]);
		}
	}

	private void testMax() {
		for (int i=0; i<ORDER.length-1; i++)
		{
			assertEqualsExact(Math.max(Float.NaN, (float)ORDER[i]), Float.NaN);
			assertEqualsExact(Math.max((float)ORDER[i], Float.NaN), Float.NaN);
			assertEqualsExact(Math.max(Double.NaN, ORDER[i]), Double.NaN);
			assertEqualsExact(Math.max(ORDER[i], Double.NaN), Double.NaN);
		}
		for (int i=1; i<ORDER.length-1; i++)
		{
			assertEqualsExact(Math.max((float)ORDER[i-1], (float)ORDER[i]), (float)ORDER[i]);
			assertEqualsExact(Math.max((float)ORDER[i], (float)ORDER[i-1]), (float)ORDER[i]);
			assertEqualsExact(Math.max(ORDER[i-1], ORDER[i]), ORDER[i]);
			assertEqualsExact(Math.max(ORDER[i], ORDER[i-1]), ORDER[i]);
		}
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MathTest t = new MathTest();
		t.testFloor();
		t.testCeil();
		t.testCompare();
		t.testMin();
		t.testMax();
	}


}
