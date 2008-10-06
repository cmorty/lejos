//import lejosunit.util.Util;

/**
 * A <code>TestFailure</code> collects a failed test together with
 * the caught exception.
 * 
 * <p>
 * leJOSUnit changes:
 * <ul>
 *   <li>Removed methods: trace, isFailure, exceptionMessage</li>
 * </ul>
 * 
 * @see TestResult
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class TestFailure extends Object {

    /** the failed test */
    private Test failedTest;

    /** the thrown exception */
    private Throwable thrownException;

    /**
     * Constructs a TestFailure with the given test and exception.
     * 
     * @param failedTest the failed test
     * @param thrownException the thrown exception by the test case
     */
    public TestFailure(Test failedTest, Throwable thrownException) {
        this.failedTest = failedTest;
        this.thrownException = thrownException;
    }

    /**
     * Gets the failed test.
     * 
     * @return the failed test
     */
    public Test failedTest() {
        return failedTest;
    }

    /**
     * Gets the thrown exception.
     * 
     * @return the thrown exception
     */
    public Throwable thrownException() {
        return thrownException;
    }


    /**
     * Returns a short description of the failure.
     * @return a string representation of a test failure
     */
    public String toString() {
        // do NOT use a StringBuffer
        // StringBuffer buffer = new StringBuffer();
        // buffer.append(failedTest + ": " + thrownException.getMessage());
        // return buffer.toString();
/*
    	String s1 = String.valueOf (failedTest);
        String s2 = ": ";
        String s3 = thrownException.getMessage ();
        return Util.concat (s1, s2, s3);
*/
    	return "";
    }
}