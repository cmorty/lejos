
/**
 * A <em>Test</em> can be run and collect its results.
 *
 * @see TestResult
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public interface Test {
    
    /**
     * Counts the number of test cases that will be run by this test.
     * 
     * @return the number of test cases
     */
    int countTestCases();

    /**
     * Runs a test and collects its result in a TestResult instance.
     * 
     * @param result the test result to write the results into.
     */
    void run(TestResult result);
}