package junit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All tests for leJOSUnit.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class AllTests {
    
    /**
     * Main program to start the TestRunner at RCX.
     * 
     * @param args will be ignored
     */
    public static void main(String[] args) {
    	junit.textui.TestRunner.main(suite());
    }

    /**
     * Suite method to get all tests to run.
     * 
     * @return the whole test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(AssertTest.suite());
        suite.addTest(FloatAssertTest.suite());
        suite.addTest(MyTestCaseTest.suite());
        suite.addTest(TestCaseTest.suite());
        // TODO add later
        // suite.addTest(TestRunnerTest.suite());
        // do NOT add UtilTest suite, as testConcatLong 
        // will require lot of memory,
        // which will fail the test suite
        // suite.addTest(UtilTest.suite());
        return suite;
    }
}