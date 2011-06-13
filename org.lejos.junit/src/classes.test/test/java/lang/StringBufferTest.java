package test.java.lang;

import java.lang.Integer;
import java.lang.Object;
import java.lang.String;
import java.lang.StringBuffer;
import java.lang.Throwable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test class for StringBuffer class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class StringBufferTest extends TestCase {
    /**
     * Constructor to create a test case.
     * @param name the nane of the test case
     */
    public StringBufferTest(String name) {
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
        suite.addTest(new StringBufferTest("testAppend"));
        suite.addTest(new StringBufferTest("testAppendNull"));
        return suite;
    }

    /**
     * Run one test. Do the dispatch here.
     * @throws Throwable a general error
     */
    protected void runTest() throws Throwable {
        if (getName().equals("testAppend")) {
            testAppend();
        } else if (getName().equals("testAppendNull")) {
            testAppendNull();
        } else {
            fail();
        }
    }

    /**
     * Tests the StringBuffer.append method with
     * all invariants.     */
    public void testAppend() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append(true);
        sbuf.append(false);
        sbuf.append('A');
        sbuf.append(3.4d);
        sbuf.append(1.2f);
        // @TODO append(int) fails
//        sbuf.append(1);
        sbuf.append(2L);
        sbuf.append(new Object ());
        sbuf.append(new Integer (1));
        sbuf.append("some text");
        sbuf.toString();
    }

    /**
     * Tests whether StringBuffer.append ()
     * accepts a null argument.     */
    public void testAppendNull() {
        StringBuffer sbuf = new StringBuffer();
        // @TODO append(null) fails
        //sbuf.append(null);
        sbuf.toString();
    }
}
