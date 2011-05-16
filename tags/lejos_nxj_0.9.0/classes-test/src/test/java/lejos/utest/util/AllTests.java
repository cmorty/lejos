package lejos.utest.util;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Dan Rollo
 *         Date: Sep 26, 2010
 *         Time: 4:22:32 PM
 */
public class AllTests {

    /**
     * Main program to start the TestRunner at RCX.
     *
     * @param args ignored
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
        suite.addTest(TestUnitTestUtilClasses.suite());
        return suite;
    }
}
