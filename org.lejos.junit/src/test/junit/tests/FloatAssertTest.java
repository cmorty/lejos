package junit.tests;

import junit.framework.FloatAssert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test class for the FloatAssert class and all the assert methods.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class FloatAssertTest extends AssertTest {

    // static attributes
    
    /** Test for good tests. */
    private static final String TEST_OK = "1";

    /** Test with longs. */
    private static final String TEST_ASSERTION_FAILURE = "2";

    // constructor

    /**
     * Constructor to create a test case.
     * @param name the nane of the test case
     */
    public FloatAssertTest(String name) {
        super(name);
    }

    /**
     * Main program to start the TestRunner at RCX
     * @param args will be ignored
     */
    public static void main(String[] args) {
    	junit.textui.TestRunner.main(suite());
    }

    /**
     * Suite method to get all tests to run.
     * @return the whole test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new FloatAssertTest(TEST_OK));
        suite.addTest(new FloatAssertTest(TEST_ASSERTION_FAILURE));
        return suite;
    }

    /**
     * Run one test. Do the dispatch here.
     * @throws Throwable a general error
     */
    protected void runTest() throws Throwable {
        if (TEST_OK.equals(getName())) {
            testOK();
        } else if (TEST_ASSERTION_FAILURE.equals(getName())) {
            testAssertionFailures();
        } else {
            fail();
        }
    }

    /**
     * Test to check the successful assert functionality.     */
    public void testOK() {
        TestCase test = new TestCase("testOK") {
            protected void runTest() {
                // assertEquals
                //   with float
                FloatAssert.assertEquals(4.0f, 4.0f, 0.00001f);
                FloatAssert.assertEquals(null, 4.0f, 4.0f, 0.00001f);
                FloatAssert.assertEquals("msg", 4.0f, 4.0f, 0.00001f);
            }
        };
        Verifier.verifySuccess(test);
    }

    /**
     * Tests situations, whereas lejosunit should indicate
     * an assertion failure.
     * Each test will be encapsulated as an anynomous class.
     */
    public void testAssertionFailures() {
        TestCase test;
        test = new TestCase("testAssertionFailures1") {
            protected void runTest() {
                // assertEquals
                //   with float
                FloatAssert.assertEquals(4f, 5f, 0.00001f);
            }
        };
        Verifier.verifyFailure(test);
    }
}
