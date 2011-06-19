package junit.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.util.Util;


/**
 * Test class for Util class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class UtilTest extends TestCase {
    /**
     * Constructor to create a test case.
     * @param name the nane of the test case
     */
    public UtilTest(String name) {
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
        suite.addTest(new UtilTest("testConcat2"));
        suite.addTest(new UtilTest("testConcat2Long"));
        suite.addTest(new UtilTest("testConcat3"));
        suite.addTest(new UtilTest("testConvertTime"));
        return suite;
    }

    /**
     * Run one test. Do the dispatch here.
     * @throws Throwable a general error
     */
    protected void runTest() throws Throwable {
        if (getName().equals("testConcat2")) {
            testConcat2();
        } else if (getName().equals("testConcat2Long")) {
            testConcat2Long();
        } else if (getName().equals("testConcat3")) {
            testConcat3();
        } else if (getName().equals("testConvertTime")) {
            testConvertTime();
        } else {
            fail();
        }
    }

    /**
     * test the string utils concat method     */
    public void testConcat2() {
        String s1 = "abc";
        String s2 = "def";
        String expected = "abcdef";
        String s = Util.concat (s1, s2);
        assertEquals (expected, s);
        
        // other tests
        assertEquals ("123xyz", Util.concat ("123", "xyz"));
    }

    /**
     * test the string utils concat method with long strings
     */
    public void testConcat2Long() {
        // s1 hat 250 chars
        String s1 =   "01234567890123456789012345678901234567890123456789"
                    + "01234567890123456789012345678901234567890123456789"
                    + "01234567890123456789012345678901234567890123456789"
                    + "01234567890123456789012345678901234567890123456789"
                    + "01234567890123456789012345678901234567890123456789";
        String expected = s1 + s1;
        String s = Util.concat (s1, s1);
        assertEquals (expected, s);
    }
    /**
     * test the string utils concat method with 3 args
     */
    public void testConcat3() {
        assertEquals ("abcdef123", 
                      Util.concat ("abc", "def", "123"));
    }

    /**
     * Tests the convert time method.
     */
    public void testConvertTime() {
        char[] s;
        s = Util.convertTime (1);
        assertEquals ("001ms", new String (s, 0, s.length));
        s = Util.convertTime (12);
        assertEquals ("012ms", new String (s, 0, s.length));
        s = Util.convertTime (123);
        assertEquals ("123ms", new String (s, 0, s.length));
        s = Util.convertTime (1234);
        assertEquals ("1.23s", new String (s, 0, s.length));
        s = Util.convertTime (12345);
        assertEquals ("12.3s", new String (s, 0, s.length));
        s = Util.convertTime (123456);
        assertEquals ("123.s", new String (s, 0, s.length));
        s = Util.convertTime (1234567);
        assertEquals ("1234s", new String (s, 0, s.length));
        s = Util.convertTime (12345678);
        assertEquals ("overf", new String (s, 0, s.length));
    }
}
