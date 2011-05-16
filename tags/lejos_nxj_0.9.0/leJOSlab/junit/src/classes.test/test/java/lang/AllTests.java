package test.java.lang;

import java.lang.String;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class AllTests {
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
        suite.addTest(PrimitivesTest.suite());
        suite.addTest(ObjectTest.suite());
        suite.addTest(StringTest.suite());
        // StringBuffer test will raise OutOfMemoryError
        // suite.addTest(StringBufferTest.suite());
        return suite;
    }
}