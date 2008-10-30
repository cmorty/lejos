package junit.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test cases for the class TestCase.
 *  
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class TestCaseTest extends TestCase {
    /**
     * Constructor to create a test case.
     * @param name the nane of the test case
     */
    public TestCaseTest(String name) {
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
        suite.addTest(new TestCaseTest("testSetupFails"));
        suite.addTest(new TestCaseTest("testTearDownFails"));
        return suite;
    }

    /**
     * Run one test. Do the dispatch here.
     * @throws Throwable a general error
     */
    protected void runTest() throws Throwable {
        if (getName().equals("testSetupFails")) {
            testSetupFails();
        } else if (getName().equals("testTearDownFails")) {
            testTearDownFails();
        } else {
            fail();
        }
    }


    /**
     * Tests whether an error during setup will be 
     * reported.     */
    public void testSetupFails() {
        TestCase fails = new TestCase("testSetupFails") {
            protected void setUp() {
                throw new Error();
            }
            protected void runTest() {
            }
        };
        Verifier.verifyError(fails);
    }

    /**
     * Tests whether an error during teardown will be 
     * reported.
     */
    public void testTearDownFails() {
        TestCase fails = new TestCase("testTearDownFails") {
            protected void tearDown() {
                throw new Error();
            }
            protected void runTest() {
            }
        };
        Verifier.verifyError(fails);
    }
}