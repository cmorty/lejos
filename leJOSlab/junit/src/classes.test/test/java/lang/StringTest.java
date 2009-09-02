package test.java.lang;

import java.lang.String;
import java.lang.Throwable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test class for java.lang.String, collecting some useful
 * tests for the core String functionality.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class StringTest extends TestCase {
    /**
     * Constructor to create a test case.
     * @param name the nane of the test case
     */
    public StringTest(String name) {
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
        suite.addTest(new StringTest("testToCharArray"));
        return suite;
    }

    /**
     * Run one test. Do the dispatch here.
     * @throws Throwable a general error
     */
    protected void runTest() throws Throwable {
        if (getName().equals("testToCharArray")) {
            testToCharArray();
        } else {
            fail();
        }
    }

    /**
     * Tests ,whether the Object.equals will work
     * in all cases.     */
    public void testToCharArray() {
        String s = "abc";
        char[] c = s.toCharArray ();
        assertEquals (c.length, 3);
        assertEquals ('a', c [0]);
        assertTrue ('a' == c [0]);
        assertTrue ('b' == c [1]);
        assertTrue ('c' == c [2]);
    }
}
