package test.java.lang;

import java.lang.Integer;
import java.lang.Object;
import java.lang.String;
import java.lang.Throwable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test class for java.lang.Object, collecting some useful
 * tests for the core object functionality.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class ObjectTest extends TestCase {
    /**
     * Constructor to create a test case.
     * @param name the nane of the test case
     */
    public ObjectTest(String name) {
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
        suite.addTest(new ObjectTest("testObjectEquals"));
        return suite;
    }

    /**
     * Run one test. Do the dispatch here.
     * @throws Throwable a general error
     */
    protected void runTest() throws Throwable {
        if (getName().equals("testObjectEquals")) {
            testObjectEquals();
        } else {
            fail();
        }
    }

    /**
     * Tests ,whether the Object.equals will work
     * in all cases.     */
    public void testObjectEquals() {
        // plain objects, same object type
        Object o1 = new Object ();
        Object o2 = new Object ();
        assertTrue (o1.equals(o1));
        assertTrue (o2.equals(o2));
        assertTrue (!o1.equals(o2));
        assertTrue (!o2.equals(o1));
        assertTrue (!o1.equals(null));

        // mixed object types
        Integer i1 = new Integer (1);
        assertTrue (i1.equals(i1));
        assertTrue (!o1.equals(i1));
        assertTrue (!i1.equals(o1));
        assertTrue (!i1.equals(null));
    }
}
