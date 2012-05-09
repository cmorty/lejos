package junit.tests;

import junit.framework.TestCase;


/**
 * Helper class to execute some test on the class TestCase.
 *
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class MyTestCase extends TestCase {
    
    /** test flag for checking correct behaviour */
    public boolean fSetUpCalled;
    
    /** test flag for checking correct behaviour */
    public boolean fTearDownCalled;
    
    /** test flag for checking correct behaviour */
    public boolean fTestRun;
    
    /** test flag for checking correct behaviour */
    public boolean fSetUPCalledFirst;
    
    /** test flag for checking correct behaviour */
    public boolean fTearDownCalledLast;
    
    /** test flag for checking correct behaviour */
    public boolean fTestRunCalledAfterSetup;

    /**
     * Default constructor.
     *      * @see lejosunit.framework.TestCase#TestCase(String)     */
    public MyTestCase(String name) {
        super(name);
    }

    /**
     * Run one test.
     * 
     * Do the dispatch here.
     * 
     * @throws Throwable a general error
     */
    protected void runTest() throws Throwable {
        if (getName().equals("testOne")) {
            testOne();
        } else {
            fail();
        }
    }

    /**
     * Setup one test case.
     */    protected void setUp() {
        fSetUpCalled = true;
        if (!fTestRun && !fTearDownCalled) {
            fSetUPCalledFirst = true;
        }
    }

    /**
     * De-initialize one test case.
     */
    protected void tearDown() {
        fTearDownCalled = true;
        if (fSetUpCalled && fTestRun) {
            fTearDownCalledLast = true;
        }
    }

    /**
     * test, whether setup and teardown are called in correct order.      */
    public void testOne() {
        fTestRun = true;
        if (fSetUpCalled && !fTearDownCalled) {
            fTestRunCalledAfterSetup = true;
        }
    }
}