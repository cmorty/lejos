package junit.tests;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test class for the Assert class and all the assert methods.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class AssertTest extends TestCase {

    // static attributes
    
    /** Test for good tests. */
    private static final String TEST_OK = "1";

    /** Test with longs. */
    private static final String TEST_LONGS = "2";

    /** Test for assertion failures. */
    private static final String TEST_ASSERTION_FAILURES = "3";

    /** Test for errors. */
    private static final String TEST_ERRORS = "4";

    /** Test fail with no args. */
    private static final String TEST_FAIL_NO_ARGS = "5";

    /** Test fail with args. */
    private static final String TEST_FAIL_ARGS = "6";

    /** Test exception priorities. */
    private static final String TEST_EXCEPTION_PRIORITY = "7";

    // constructor

    /**
     * Constructor to create a test case.
     * 
     * @param name the nane of the test case
     */
    public AssertTest(String name) {
        super(name);
    }

    /**
     * Main program to start the TestRunner at RCX.
     * 
     * @param args will be ignored
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
        suite.addTest(new AssertTest(TEST_OK));
        suite.addTest(new AssertTest(TEST_LONGS));
        suite.addTest(new AssertTest(TEST_ASSERTION_FAILURES));
        suite.addTest(new AssertTest(TEST_ERRORS));
        suite.addTest(new AssertTest(TEST_FAIL_NO_ARGS));
        suite.addTest(new AssertTest(TEST_FAIL_ARGS));
        suite.addTest(new AssertTest(TEST_EXCEPTION_PRIORITY));
        return suite;
    }

    /**
     * Run one test. Do the dispatch here.
     * @throws Throwable a general error
     */
    protected void runTest() throws Throwable {
        if (TEST_OK.equals (getName())) {
            testOK();
        } else if (TEST_LONGS.equals(getName())) {
            testLongs();
        } else if (TEST_ASSERTION_FAILURES.equals (getName())) {
            testAssertionFailures();
        } else if (TEST_ERRORS.equals(getName())) {
            testErrors();
        } else if (TEST_FAIL_NO_ARGS.equals(getName())) {
            testFailNoArgs();
        } else if (TEST_FAIL_ARGS.equals(getName())) {
            testFailArgs();
        } else if (TEST_EXCEPTION_PRIORITY.equals(getName())) {
            testExceptionPriority();
        } else {
            fail();
        }
    }

    /**
     * tests a lot of situations, whereas lejosunit should indicate
     * a correct execution.     */
    public void testOK() {
        TestCase test = new TestCase("testOK") {
            protected void runTest() {
                // local variables
                Object o = new Object();
                Object o2 = new Object();

                // assertTrue, assertFalse
                assertTrue(true);
                assertTrue("", true);
                assertFalse(false);
                assertFalse("", false);

                // assertEquals
                //   with objects
                assertEquals(null, null);
                assertEquals(o, o);
                assertEquals("", null, null);
                assertEquals("", o, o);

                //   with strings
                assertEquals("", "");
                assertEquals("", "", "");
                assertEquals("x", "x");
                assertEquals("", "y", "y");

                //   with int
                assertEquals(1, 1);
                assertEquals("", 1, 1);

                //   with long
                assertEquals(2L, 2L);
                // @TODO jhi does not work. See Bug.java
//                assertEquals("", 2L, 2L);

                //   with byte
                assertEquals((byte) 3, (byte) 3);
                assertEquals("", (byte) 3, (byte) 3);

                //   with short
                assertEquals((short) 4, (short) 4);
                assertEquals("", (short) 4, (short) 4);

                // assertNull, assertNotNull
                assertNull(null);
                assertNull("", null);
                assertNotNull(o);
                assertNotNull("", o);

                // assertSame, assertNotSame
                assertSame(o, o);
                assertSame("", o, o);
                assertSame(null, null);
                assertSame("", null, null);
                assertNotSame(o, o2);
                assertNotSame("", o, o2);
                assertNotSame(o, null);
                assertNotSame(null, o);
                assertNotSame("", o, null);
                assertNotSame("", null, o);
            }
        };
        Verifier.verifySuccess(test);
    }

    /**
     * Tests longs in combination with strings.
     * 
     * Specified when detecting a bug with long handling
     * in JavaVM.
     */
    public void testLongs () {
        TestCase test = new TestCase("testLongs") {
            protected void runTest() {
                assertEquals("", 2L, 2L);
                assertEquals(1L, 1L);
                assertEquals(2L, 2L);
                assertEquals(3L, 3L);
                assertEquals((int) 1L, (int) 1L);
                assertEquals("", (int) 1L, (int) 1L);
                assertEquals("", 1L, 1L);
                assertEquals("", (int) 1L, (int) 1L);
                assertEquals("dsfgsdfg", 2L, 2L);
                assertEquals("", (int) 1L, (int) 1L);
                assertEquals("", 3L, 3L);
            }
        };
        Verifier.verifySuccess(test);
    }

    /**
     * Tests situations, whereas lejosunit should indicate
     * an assertion.
     * 
     * Each test will be encapsulated as an anynomous class.
     */
    public void testAssertionFailures() {
        TestCase test;
        test = new TestCase("testAssertionFailures1a") {
            protected void runTest() {
                // assertTrue
                assertTrue(false);
            }
        };
        Verifier.verifyFailure(test);

        test = new TestCase("testAssertionFailures1b") {
            protected void runTest() {
                // assertTrue
                assertNull(new Object ());
            }
        };
        Verifier.verifyFailure(test);

        test = new TestCase("testAssertionFailures1c") {
            protected void runTest() {
                // assertTrue
                assertNotNull(null);
            }
        };
        Verifier.verifyFailure(test);

        test = new TestCase("testAssertionFailures2") {
            protected void runTest() {
                // assertTrue
                assertTrue("", false);
            }
        };
        Verifier.verifyFailure(test);

        test = new TestCase("testAssertionFailures3a") {
            protected void runTest() {
                // local variables
                Object o = new Object();

                // assertEquals
                // assertEquals(null, o);
                assertEquals(o, null);
            }
        };
        Verifier.verifyFailure(test);

        test = new TestCase("testAssertionFailures3b") {
            protected void runTest() {
                // local variables
                Object o = new Object();

                // assertEquals
                // assertEquals(null, o);
                assertEquals(null, o);
            }
        };
        Verifier.verifyFailure(test);

        test = new TestCase("testAssertionFailures4") {
            protected void runTest() {
                // local variables
                Object o1 = new Object();
                Object o2 = new Object();

                // assertEquals
                assertEquals(o1, o2);
            }
        };
        Verifier.verifyFailure(test);
       
        test = new TestCase("testAssertionFailures5") {
            protected void runTest() {
                // fail
                fail();
            }
        };
        Verifier.verifyFailure(test);

        test = new TestCase("testAssertionFailures6") {
            protected void runTest() {
                // fail
                fail();
            }
        };
        Verifier.verifyFailure(test);

        test = new TestCase("testAssertionFailures7") {
            protected void runTest() {
                // assertNotNull                
                assertNotNull(null);
            }
        };
        Verifier.verifyFailure(test);

        test = new TestCase("testAssertionFailures8") {
            protected void runTest() {
                // assertSame
                // @TODO jhi is this correct ?
                assertSame(new Integer(1), new Integer(1));
            }
        };
        Verifier.verifyFailure(test);
    }

    /**
     * Tests situations, whereas lejosunit should indicate
     * an error.
     * 
     * Each test will be encapsulated as an anynomous class.
     */
    public void testErrors() {
        TestCase test;
        test = new TestCase("testErrors1") {
            protected void runTest() {
                throw new Error();
            }
        };
        Verifier.verifyError(test);

        test = new TestCase("testErrors2") {
            protected void runTest() {
                throw new RuntimeException();
            }
        };
        Verifier.verifyError(test);
    }

    /**
     * Test, whether fail will work.
     */
    public void testFailNoArgs() {
        try {
            fail();
        } catch (AssertionFailedError ex) {
            // ignore
            return;
        }
        fail ();
    }

    /**
     * Test, whether fail with arguments will work.
     */
    public void testFailArgs() {
        try {
            fail();
        } catch (AssertionFailedError ex) {
            return;
        }
        fail ();
    }

    /**
     * Test, whether exceptions will be handled in the correct way:
     * first should assertion failed error be raisen.
     */
    public void testExceptionPriority() {
        try {
            throw new AssertionFailedError ();
        } catch (AssertionFailedError ex) {
            // ignore, will be expected to be here
        } catch (Throwable ex) {
            // must not happen
            fail (); // assertion must be catched before
        }
    }
}
