package junit.tests;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.framework.TestResult;


/**
 * Helper class to verify test results.
 * Can be in package scope.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
class Verifier extends Assert {
    /**
     * Helper method, to check existence of an error.
     * @param test the test case to verify
     */
    public static void verifyError(TestCase test) {
        TestResult result = test.run();
        assertTrue(result.runCount() == 1);
        assertTrue(result.failureCount() == 0);
        assertTrue(result.errorCount() == 1);
    }

    /**
     * Helper method, to check existence of an assertion.
     * @param test the test case to verify
     */
    public static void verifyFailure(TestCase test) {
        TestResult result = test.run();
        assertTrue(result.runCount() == 1);
        assertTrue(result.failureCount() == 1);
        assertTrue(result.errorCount() == 0);
    }

    /**
     * Helper method, to check existence of an succesful execution.
     * @param test the test case to verify
     */
    public static void verifySuccess(TestCase test) {
        TestResult result = test.run();
        assertTrue(result.runCount() == 1);
        assertTrue(result.failureCount() == 0);
        assertTrue(result.errorCount() == 0);
    }
}
