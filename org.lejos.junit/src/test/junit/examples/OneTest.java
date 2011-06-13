package junit.examples;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Example test case class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class OneTest extends TestCase {
    
    // static attributes
    
    /** name of testOne */
    private static final String TEST_ONE = "1";

    /** name of testTwo */
    private static final String TEST_TWO = "2";

    // constructors
    
    /**
     * Constructor to create a test case.
     * 
     * @param name the nane of the test case     */
    public OneTest(String name) {
        super(name);
    }

    // static methods

    /**
     * Suite method to get all tests to run.
     * 
     * @return the whole test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new OneTest(TEST_ONE));
        suite.addTest(new OneTest(TEST_TWO));
        return suite;
    }

    // public methods
     
    /**
     * Run one test. Do the dispatch here.
     * 
     * @throws Throwable a general error     */
    protected void runTest() throws Throwable {
        if (TEST_ONE.equals(getName())) {
            testOne();
        } else if (TEST_TWO.equals(getName())) {
            testTwo();
        } else {
            fail();
        }
    }

    // test methods

    /**
     * A test, which will raise an AssertionFailureException.     */
    public void testOne() {
        assertTrue(false);
    }

    /**
     * A test, which will raise an Error.
     */
    public void testTwo() {
        throw new RuntimeException();
    }
}
