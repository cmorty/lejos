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


}