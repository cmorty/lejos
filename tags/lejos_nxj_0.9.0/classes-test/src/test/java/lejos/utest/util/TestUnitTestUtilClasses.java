package lejos.utest.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit Test of Utility to find project root dir, independent of IDE/current dir, etc.
 *
 * @author Dan Rollo
 *         Date: Jun 24, 2010
 *         Time: 10:17:47 PM
 */
public class TestUnitTestUtilClasses extends TestCase {

    /**
     * Default constructor.
     * @param name test name
     * @see TestCase#TestCase(String)
     */
    public TestUnitTestUtilClasses(String name) {
        super(name);
    }

    /**
     * Suite method to get all tests to run.
     *
     * @return the whole test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new TestUnitTestUtilClasses("testNullInstance"));
        return suite;
    }

    /**
     * Run one test.
     *
     * Do the dispatch here.
     *
     * @throws Throwable a general error
     */
    protected void runTest() throws Throwable {
        if (getName().equals("testNullInstance")) {
            testNullInstance();
        } else {
            fail();
        }
    }

    /*
    private UnitTestUtil origInstance;

    protected void setUp() throws Exception {
        try {
            origInstance = UnitTestUtil.getInstance();
        } catch (IllegalStateException e) {
            origInstance = null;
        }
    }
    
    protected void tearDown() throws Exception {
        UnitTestUtil.setProject(origInstance);
    }
    */

    public void testNullInstance() throws Exception {
        UnitTestUtil.setProject(null);
        try {
            UnitTestUtil.getInstance();
            fail("Should fail when Project instance not set.");
        } catch (IllegalStateException e) {
            assertEquals(UnitTestUtil.MSG_NULL_INSTANCE, e.getMessage());
        }
    }

    /*
    public void testGetProjectRootDir() throws Exception {
        UnitTestUtil.setProject(new UnitTestUtilClasses());
        assertNotNull(UnitTestUtil.getProjectRootDir());
        assertTrue(UnitTestUtil.getProjectRootDir().getAbsolutePath().endsWith(
                UnitTestUtilClasses.PROJECT_ROOT_DIR_NAME));
    }
    //*/
}
