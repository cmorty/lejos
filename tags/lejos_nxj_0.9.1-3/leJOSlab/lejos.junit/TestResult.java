import java.util.Vector;


/**
 * A <code>TestResult</code> collects the results of executing
 * a test case.
 * 
 * It is an instance of the Collecting Parameter pattern.
 * The test framework distinguishes between <i>failures</i> and <i>errors</i>.
 * A failure is anticipated and checked for with assertions. Errors are
 * unanticipated problems like an <code>ArrayIndexOutOfBoundsException</code>.
 *
 * <p>
 * lejosunit changes:
 * <ul>
 *   <li>errors(), ... will return TestFailure[] instead of Enumeration.</li>
 *   <li>removed some deprecated methods</li>
 *   <li>Only support one listener only</li>
 * </ul>
 * 
 * @see Test
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class TestResult extends Object {
    
    /** list with all failures */
    private Vector failures;

    /** list with all errors */
    private Vector errors;

    /**
     * The only one test listenere.
     * against JUnit, which supports multiple listeners.
     */
    private TestListener listener;

    /** number of tests, which have been running */
    private int runTests;

    /** flag, to indicate to stop the test run */
    private boolean stop;

    /**
     * Default constructor. 
     */
    public TestResult() {
        failures = new Vector();
        errors = new Vector();
        listener = null;
        runTests = 0;
        stop = false;
    }

    /**
     * Adds an error to the list of errors.
     * 
     * The passed in exception caused the error.
     * 
     * @param test the test to add to error list
     * @param t the throwable to add to error list
     */
    public synchronized void addError(Test test, Throwable t) {
        errors.addElement(new TestFailure(test, t));
        if (listener != null) {
            listener.addError(test, t);
        }
    }

    /**
     * Adds a failure to the list of failures.
     * 
     * The passed in exception caused the failure.
     * 
     * @param test the test to add to failure list
     * @param t the throwable to add to failure list
     */
    public synchronized void addFailure(Test test, AssertionFailedError t) {
        failures.addElement(new TestFailure(test, t));
        if (listener != null) {
            listener.addFailure(test, t);
        }
    }

    /**
     * Registers a TestListener.
     * 
     * @param aListener the listerner to set for the test result
     */
    public synchronized void setListener(TestListener aListener) {
        listener = aListener;
    }

    /**
     * Informs the listener that a test was completed.
     * 
     * @param test the test which indicates the end 
     */
    public void endTest(Test test) {
        if (listener != null) {
            listener.endTest(test);
        }
    }

    /**
     * Returns an Enumeration for the errors
     * 
     * @return an array of TestFailure. Can be an empty array.
     */
    public synchronized Object[] errors() {
        return errors.toArray();
    }

    /**
     * Returns an Enumeration for the failures.
     * 
     * @return an array of TestFailure. Can be an empty array.  
     */
    public synchronized Object[] failures() {
        return failures.toArray();
    }

    /**
     * Runs a TestCase.
     * 
     * @param test the test case which will be running.
     */
    protected void run(final TestCase test) {
        startTest(test);
        Protectable p = new Protectable() {
            public void protect() throws Throwable {
                test.runBare();
            }
        };
        runProtected(test, p);
        endTest(test);
    }

    /**
     * Gets the number of run tests.
     * 
     * @return the number of tests
     */
    public synchronized int runCount() {
        return runTests;
    }

    /**
     * Runs a TestCase.
     * 
     * @param test the test which have to be running
     * @param p the protectable to catch all exceptions
     */
    public void runProtected(final Test test, Protectable p) {
        try {
            p.protect();
        } catch (AssertionFailedError e) {
            addFailure(test, e);
        } catch (ThreadDeath e) { // don't catch ThreadDeath by accident
            throw e;
        } catch (Throwable e) {
            addError(test, e);
        }
    }

    /**
     * Checks whether the test run should stop.
     * 
     * @return true, if test runner should stop the test suite
     */
    public synchronized boolean shouldStop() {
        return stop;
    }

    /**
     * Informs the result that a test will be started.
     * 
     * @param test the test which indicates start of an test case
     */
    public void startTest(Test test) {
        final int count = test.countTestCases();
        synchronized (this) {
            runTests += count;
        }
        if (listener != null) {
            listener.startTest(test);
        }
    }

    /**
     * Marks that the test run should stop.
     */
    public synchronized void stop() {
        stop = true;
    }

    /**
     * Gets the number of detected errors.
     * 
     * @return the number of errors counted
     */
    public synchronized int errorCount() {
        return errors.size();
    }

    /**
     * Gets the number of detected failures.
     * 
     * @return the number of failures counted
     */
    public synchronized int failureCount() {
        return failures.size();
    }

    /**
     * Returns whether the entire test was successful or not.
     * 
     * @return true, if everything was successful
     */
    public synchronized boolean wasSuccessful() {
        return (failureCount() == 0) && (errorCount() == 0);
    }
}