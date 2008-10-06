
/**
 * The class <code>FloatAssert</code> implements
 * all methods related to float and Math libs.
 * 
 * Use this class only if required,
 * as Math lib will be linked,
 * which requires much more memory.
 * <p>
 * Doubles are not yet supported, due to lejos limitations.
 *
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class FloatAssert extends Assert {

    /**
     * Protect constructor since it is a static only class
     */
    protected FloatAssert() {
    }

    /**
     * Asserts that two floats are equal concerning a delta.
     * 
     * If the expected value is infinity,
     * then the delta value is ignored.
     * 
     * @param message the assertion message
     * @param expected the expected value
     * @param actual the actual value to be tested
     * @param delta the delta when two floats will be considered the same
     */
    public static void assertEquals(String message, float expected, 
                                    float actual, float delta) {
        // ignore message
        assertEquals(expected, actual, delta);
    }

    /**
     * Asserts that two floats are equal concerning a delta.
     * 
     * If the expected value is infinity,
     * then the delta value is ignored.
     * 
     * @param expected the expected value
     * @param actual the actual value to be tested
     * @param delta the delta when two floats will be considered the same
     */
    public static void assertEquals(float expected, float actual, float delta) {
        // handle infinity specially since subtracting to 
        // infinite values gives NaN and the
        // the following test fails
        // lejosunit: disable this test for the moment
        // @TODO jhi 2003-05-06 to be implemented
        if (false/*Float.isInfinite(expected)*/) {
            if (!(expected == actual)) {
                // lejosunit: do NOT convert to a float,
                // use StringBuffer instead
                // failNotEquals(message, 
                //     new Float(expected), new Float(actual));
                failNotEquals(new StringBuffer().append(expected).toString(), 
                              new StringBuffer().append(actual).toString());
            }
        } else if (!(Math.abs(expected - actual) <= delta)) {
            // lejosunit: do NOT convert to a Float,
            // use StringBuffer instead
            // failNotEquals(message, new Float(expected), new Float(actual));

            failNotEquals(new StringBuffer().append(expected).toString(), 
                          new StringBuffer().append(actual).toString());
        }
    }
}
