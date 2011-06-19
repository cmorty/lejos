package junit.framework;

// TODO cross port RCX/NXT
// import josx.platform.rcx.Sound;



/**
 * A set of assert methods.
 * <p>
 * lejosunit changes:
 * <ul>
 *   <li>assert methods for double and Double removed</li>
 *   <li>
 *     assert methods for float moved to FloatAssert,
 *     to minimize memory for lejosunit if NOT using floats.
 *   </li>
 *   <li>Do not use classes like Boolean, Character, Byte, Short</li>
 * </ul>
 * 
 * Change for v1.1:
 * <p>
 * ignore now all string parameter for the assert and fail
 * methods. Reduces string handling
 * </p>
 *
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class Assert {

    // static attributes

    /** constant to print, used some times. */
    private static final String TRUE = "true";

    /** constant to print, used some times. */
    private static final String FALSE = "false";

    /** constant to print, used some times. */
    // TODO enable later when supporting assertions with string
    // private static final String NULL = "null";

    /**
     * Protect constructor since it is a static only class
     */
    protected Assert() {
    }

    /**
     * Asserts that a condition is true.
     * 
     * If it isn't it throws an AssertionFailedError 
     * with the given message.
     * 
     * @param message the assertion message
     * @param condition the condition to be tested
     */
    public static void assertTrue(String message, boolean condition) {
        // ignore message
        assertTrue (condition);
    }

    /**
     * Asserts that a condition is false.
     * 
     * If it isn't it throws an AssertionFailedError 
     * with the given message.
     * 
     * @param message the assertion message
     * @param condition the condition to be tested
     */
    public static void assertFalse(String message, boolean condition) {
        // ignore message
        assertTrue (!condition);
    }
    
    /**
     * Asserts that a condition is true.
     * 
     * If it isn't it throws an AssertionFailedError.
     * 
     * @param condition the condition to be tested
     */
    public static void assertTrue(boolean condition) {
        if (!condition) {
            fail();
        }
    }
    
    /**
     * Asserts that a condition is false.
     * 
     * If it isn't it throws an AssertionFailedError.
     * 
     * @param condition the condition to be tested
     */
    public static void assertFalse(boolean condition) {
        assertTrue(!condition);
    }    

    /**
     * Fails a test with the given message.
     * 
     * @param message the assertion message
     */
    public static void fail(String message) {
        // ignore message
        throw new AssertionFailedError();
    }

    /**
     * Fails a test with no message. 
     */
    public static void fail() {
        throw new AssertionFailedError();
    }

    /**
     * Asserts that two objects are equal.
     * 
     * If they are not, an AssertionFailedError is thrown.
     * 
     * @param message the assertion message
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertEquals(String message, 
                                      Object expected, Object actual) {
        // ignore message
        assertEquals(expected, actual);
    }

    /**
     * Asserts that two objects are equal.
     * 
     * If they are not, an AssertionFailedError is thrown.
     * 
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertEquals(Object expected, Object actual) {
        if ((expected == null) && (actual == null)) {
            return;
        }
        if ((expected != null) && expected.equals(actual)) {
            return;
        }
        failNotEquals(expected, actual);
    }

    /**
     * Asserts that two longs are equal.
     * 
     * @param message the assertion message
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertEquals(String message, 
                                      long expected, long actual) {
        int i = (int) expected;
        int j = (int) actual;
        if (j == i) {
        	// TODO cross port RCX/NXT
            // Sound.beepSequence();
        }
        // ignore message
        assertEquals(expected, actual);
    }

    /**
     * Asserts that two longs are equal.
     * 
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertEquals(long expected, long actual) {
        // lejosunit: downcast long to int, will be fine for lejosunit
        assertEquals((int) expected, (int) actual);
    }

    /**
     * Asserts that two booleans are equal.
     * 
     * @param message the assertion message
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertEquals(String message, 
                                      boolean expected, boolean actual) {
        // ignore message
        assertEquals(expected, actual);
    }

    /**
     * Asserts that two booleans are equal.
     * 
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertEquals(boolean expected, boolean actual) {
        // lejosunit: no Boolean class available
        // assertEquals(message, new Boolean(expected), new Boolean(actual));
        
        if (expected != actual) {
            failNotEquals(expected ? TRUE : FALSE, 
                          actual ? TRUE : FALSE);
        }
    }

    /**
     * Asserts that two bytes are equal.
     * 
     * @param message the assertion message
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertEquals(String message, 
                                    byte expected, byte actual) {
        // ignore message
        assertEquals(expected, actual);
    }

    /**
     * Asserts that two bytes are equal.
     * 
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertEquals(byte expected, byte actual) {
        // lejosunit: no Byte class available
        // assertEquals(message, new Byte(expected), new Byte(actual));
        if (expected != actual) {
            failNotEquals(new Integer(expected), new Integer(actual));
        }
    }

    /**
     * Asserts that two chars are equal.
     * 
     * @param message the assertion message
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertEquals(String message, 
                                    char expected, char actual) {
        // ignore message
        assertEquals(expected, actual);
    }

    /**
     * Asserts that two chars are equal.
     * 
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertEquals(char expected, char actual) {
        // lejosunit: No Character class available
        // assertEquals(message, 
        //     new Character(expected), new Character(actual));
        if (expected != actual) {
            char[] expArray = new char[] { expected };
            char[] actArray = new char[] { actual };
            failNotEquals(new String(expArray, 0, 1), 
                          new String(actArray, 0, 1));
        }
    }

    /**
     * Asserts that two shorts are equal.
     * 
     * @param message the assertion message
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertEquals(String message,
                                      short expected, short actual) {
        // ignore message
        assertEquals(expected, actual);
    }

    /**
     * Asserts that two shorts are equal.
     * 
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertEquals(short expected, short actual) {
        // lejosunit: Does not support Short class
        // assertEquals(message, new Short(expected), new Short(actual));
        if (expected != actual) {
            failNotEquals(new Integer(expected), new Integer(actual));
        }
    }

    /**
     * Asserts that two ints are equal.
     * 
     * @param message the assertion message
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertEquals(String message, 
                                      int expected, int actual) {
        // ignore message
        assertEquals(expected, actual);
    }

    /**
     * Asserts that two ints are equal.
     * 
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            failNotEquals(new Integer(expected), new Integer(actual));
        }
    }

    /**
     * Asserts that an object isn't null.
     * 
     * @param object the object to be tested
     */
    public static void assertNotNull(Object object) {
        assertTrue(object != null);
    }

    /**
     * Asserts that an object isn't null.
     * 
     * @param message the assertion message
     * @param object the object to be tested
     */
    public static void assertNotNull(String message, Object object) {
        // ignore message
        assertTrue(object != null);
    }

    /**
     * Asserts that an object is null.
     * 
     * @param object the object to be tested
     */
    public static void assertNull(Object object) {
        assertTrue(object == null);
    }

    /**
     * Asserts that an object is null.
     * 
     * @param message the assertion message
     * @param object the object to be tested
     */
    public static void assertNull(String message, Object object) {
        // ignore message
        assertTrue(object == null);
    }

    /**
     * Asserts that two objects refer to the same object.
     * 
     * If they are not an AssertionFailedError is thrown.
     * 
     * @param message the assertion message
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertSame(String message,
                                    Object expected, Object actual) {
        // ignore message
        assertSame(expected, actual);
    }

    /**
     * Asserts that two objects refer to the same object.
     * 
     * If they are not the same an AssertionFailedError is thrown.
     * 
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertSame(Object expected, Object actual) {
        if (expected == actual) {
            return;
        }
        // ignore message
        failSame(expected, actual, true);
    }

    /**
     * Asserts that two objects refer NOT to the same object.
     * 
     * If they are the same, an AssertionFailedError 
     * is thrown with the given message.
     * 
     * @param message the assertion message
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertNotSame(String message, 
                                       Object expected, Object actual) {
        // ignore message
        assertNotSame(expected, actual);
    }

    /**
     * Asserts that two objects refer not to the same object.
     * 
     * If they are the same, an AssertionFailedError is thrown.
     * 
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    public static void assertNotSame(Object expected, Object actual) {
        if (expected == actual) {
            failSame(expected, actual, false);
        }
    }

    // protected methods

    /**
     * Helper method to print out the result
     * 
     * @param expected the expected value
     * @param actual the actual value to be tested
     */
    protected static void failNotEquals(/*String message, */Object expected, 
                                        Object actual) {
/* for the moment, ignore all strings,
   simply fail                                        
        String s1;
        if (message != null) {
            s1 = Util.concat (message, " expected:<");
        } else {
            s1 = "expected:<";
        }
        // append(null) will fail, therefore print careful
        String s2;
        if (expected == null) {
            s2 = NULL;
        } else {
            s2 = expected.toString ();
        }
        String s3 = "> but was:<";
        // append(null) same here
        String s4;
        if (actual == null) {
            s4 = NULL;
        } else {
            s4 = actual.toString ();
        }
        String s5 = Util.concat (s1, s2, s3);
        String s6 = Util.concat (s4, s5, ">");
        fail(s6);
*/
        fail ();        
    }

    /**
     * Helper method to print out the result
     * 
     * @param expected the expected value
     * @param actual the actual value to be tested
     * @param same true, if we expect the same
     */
    protected static void failSame(/*String message, */ 
                                     Object expected, Object actual, 
                                     boolean same) {
/* for the moment, ignore all strings, 
   simply fail                                        
        // do not printout the objects.
        // this would use StringBuffer, 
        // which would enforce to link the Math library
        String s;
        if (message != null) {
            s = Util.concat (message, 
                             same ? " expected same" : " expected not same");
        } else {
            s = same ? " expected same" : " expected not same";
        }
        fail(s);
*/
        fail ();
    }
}
