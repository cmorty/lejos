import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Used to run all tests.
 *
 * @author Dan Rollo
 *         Date: Sep 26, 2010
 *         Time: 4:21:00 PM
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
        suite.addTest(lejos.AllTests.suite());
        return suite;
    }
}