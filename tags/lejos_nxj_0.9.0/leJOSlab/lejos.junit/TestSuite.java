
import java.util.Vector;


/**
 * A <code>TestSuite</code> is a <code>Composite</code> of Tests.
 * It runs a collection of test cases. Here is an example using
 * the dynamic test definition.
 * <pre>
 * TestSuite suite= new TestSuite();
 * suite.addTest(new MathTest("testAdd"));
 * suite.addTest(new MathTest("testDivideByZero"));
 * </pre>
 * As leJOS does not support reflection, 
 * dynamic test cases extraction will not be supported.
 * <p>
 * lelosunit changes:
 * <ul>
 *   <li>Remove all methods with Class arguments</li>
 *   <li>Remove all methods which are using Method</li>
 * </ul>
 * 
 * @see Test
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class TestSuite implements Test {
    /** list of all tests */
    private Vector fTests = new Vector(10);

    /**
      * Constructs an empty TestSuite.
      */
    public TestSuite() {
    }

    /**
     * Adds a test to the suite.
     * 
     * @param test the test to be added
     */
    public void addTest(Test test) {
        fTests.addElement(test);
    }

    /**
     * Counts the number of test cases that will be run by this test.
     * 
     * @return the number of test cases
     */
    public int countTestCases() {
        int count = 0;
        // do not use enumerations
        // for (Enumeration e= tests(); e.hasMoreElements(); ) {
        //     Test test= (Test)e.nextElement();
        for (int i = 0; i < testCount(); i++) {
            Test test = (Test) fTests.elementAt(i);
            count = count + test.countTestCases();
        }
        return count;
    }

    /**
     * Runs the tests and collects their result in a TestResult.
     * 
     * @param result the test result, to write the results into
     */
    public void run(TestResult result) {
        // do not use enumerations
        // for (Enumeration e= tests(); e.hasMoreElements(); ) {
        for (int i = 0; i < testCount(); i++) {
            if (result.shouldStop()) {
                break;
            }
            // Test test= (Test)e.nextElement();
            Test test = (Test) fTests.elementAt(i);
            runTest (test, result);
        }
    }

    /**
     * Run the given test into the result.
     * 
     * @param test the test to be run
     * @param result the result, where the test has to be run
     */
    public void runTest(Test test, TestResult result) {
        test.run(result);
    }
    
    /**
     * Returns the number of tests in this suite.
     * 
     * @return the number of tests withing this this suite
     */
    public int testCount() {
        return fTests.size();
    }
}
