package junit.framework;

/**
 * A Listener for test progress.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public interface TestListener {

    /**
     * An error occurred.
     * 
     * @param test the test to add
     * @param t the exception to be added
     */
    void addError(Test test, Throwable t);

    /**
     * A failure occurred
     * 
     * @param test the test to add
     * @param t the exception to be added
     */
    void addFailure(Test test, AssertionFailedError t);

    /**
     * A test ended.
     * 
     * @param test the test to add
     */
    void endTest(Test test);

    /**
     * A test started.
     * 
     * @param test the test to add
     */
    void startTest(Test test);
}