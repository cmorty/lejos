package junit.examples;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Example for an overall test suite.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
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
        suite.addTest(OneTest.suite());
        suite.addTest(TwoTest.suite());
        return suite;
    }
}