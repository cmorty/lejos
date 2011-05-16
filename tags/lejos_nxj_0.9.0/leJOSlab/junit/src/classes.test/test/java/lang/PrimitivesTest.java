package test.java.lang;

import java.lang.String;
import java.lang.Throwable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test class for primitive types.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class PrimitivesTest extends TestCase {
    /**
     * Constructor to create a test case.
     * @param name the nane of the test case
     */
    public PrimitivesTest(String name) {
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
        suite.addTest(new PrimitivesTest("testLong"));
        return suite;
    }

    /**
     * Run one test. Do the dispatch here.
     * @throws Throwable a general error
     */
    protected void runTest() throws Throwable {
        if (getName().equals("testLong")) {
            testLong();
        } else {
            fail();
        }
    }

    /**
     * Tests the conversion from long to int and vice versa.
     */    
    public void testLong() {
        long l1 = 1L;
        int i1 = (int) l1;
        assertEquals (1, i1);
        long l2 = 2L;
        int i2 = (int) l2;
        assertEquals (2, i2);
    }
}
