package junit.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test class for class TestCase using a helper class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class MyTestCaseTest extends TestCase {
    /** the test case to be tested. Initialized via setup */
    private MyTestCase myTestCase;

    /**
     * Constructor to create a test case.
     * @param name the nane of the test case
     */
    public MyTestCaseTest(String name) {
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
        suite.addTest(new MyTestCaseTest("testSetupCalled"));
        suite.addTest(new MyTestCaseTest("testTearDownCalled"));
        suite.addTest(new MyTestCaseTest("testTestRan"));

        return suite;
    }

    /**
     * Run one test. Do the dispatch here.
     * @throws Throwable a general error
     */
    protected void runTest() throws Throwable {
        if (getName().equals("testSetupCalled")) {
            testSetupCalled();
        } else if (getName().equals("testTearDownCalled")) {
            testTearDownCalled();
        } else if (getName().equals("testTestRan")) {
            testTestRan();
        }
    }

    /**
     * Setup the environment for this test.
     * Includes a new test case, which has been run.     * @see lejosunit.framework.TestCase#setUp()     */
    protected void setUp() {
        myTestCase = new MyTestCase("testOne");
        myTestCase.run();
    }

    /**
     * Cleanup the test case environment.
     */
    protected void tearDown() {
        myTestCase = null;
    }

    /**
     * Tests, whether the setup has been called correct.     */
    public void testSetupCalled() {
        assertTrue("Setup should have been called.", myTestCase.fSetUpCalled);
        assertTrue("Setup should have been called first", 
                   myTestCase.fSetUPCalledFirst);
    }

    /**
     * Tests, whether the tear down has been called correct.
     */
    public void testTearDownCalled() {
        assertTrue("tearDown should have been called.", 
                   myTestCase.fTearDownCalled);
        assertTrue("tearDown should have been called last", 
                   myTestCase.fTearDownCalledLast);
    }

    /**
     * Tests, whether the test has been running correct.
     */
    public void testTestRan() {
        assertTrue("The test should have been run.", myTestCase.fTestRun);
        assertTrue("test should have been called after setup", 
                   myTestCase.fTestRunCalledAfterSetup);
    }
}