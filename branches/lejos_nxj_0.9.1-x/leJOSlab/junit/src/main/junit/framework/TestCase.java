package junit.framework;

/**
 * A test case defines the fixture to run multiple tests.
 * 
 * To define a test case<br>
 * <ol>
 *   <li>implement a subclass of TestCase<br></li>
 *   <li>define instance variables that store the state of the fixture<br></li>
 *   <li>initialize the fixture state by overriding <code>setUp</code><br></li>
 *   <li>clean-up after a test by overriding <code>tearDown</code>.<br></li>
 * </ol>
 * Each test runs in its own fixture so there
 * can be no side effects among test runs.
 * Here is an example:
 * <pre>
 * public class MathTest extends TestCase {
 *     protected double fValue1;
 *     protected double fValue2;
 *
 *     public MathTest(String name) {
 *         super(name);
 *     }
 *
 *    protected void setUp() {
 *         fValue1= 2.0;
 *         fValue2= 3.0;
 *     }
 * }
 * </pre>
 *
 * For each test implement a method which interacts
 * with the fixture. Verify the expected results with assertions specified
 * by calling <code>assert</code> with a boolean.
 * <pre>
 *    protected void testAdd() {
 *        double result= fValue1 + fValue2;
 *        assert(result == 5.0);
 *    }
 * </pre>
 * lejosunit does NOT support Java Reflection.
 * Therefore, only a static type safe call of 
 * test methods can be used.
 * So, a test case is responsible to 
 * call the right test method.
 * This can be done by overriding the runTest() method.
 * Example:
 * Define the runTest () as dispatcher method:
 * <pre>
 * public void runTest () {
 *   if ("testAdd".equals (getName ()) {
 *     testAdd ();
 *   } else if ("testDivideByZero".equals (getName ()) {{
 *     testSubtract ();
 *   }
 * }
 * </pre>
 * Create a new test and run it:
 * <pre>
 * Test test = new MathTest ("testAdd");
 * test.run ();
 * </pre>
 * 
 * A convenient way to do so is with an anonymous inner class.
 * <pre>
 * Test test= new MathTest("add") {
 *        public void runTest() {
 *            testAdd();
 *        }
 * };
 * test.run();
 * </pre>
 * 
 * The tests to be run can be collected into a TestSuite. leJOSUnit provides
 * different <i>test runners</i> which can run a test suite and 
 * collect the results.
 * A test runner either expects a static method <code>suite</code> as the entry
 * point to get a test to run or it will extract the suite automatically.
 * <pre>
 * public static Test suite() {
 *      TestSuite suite = new TestSuite ();
 *      suite.addTest(new MathTest("testAdd"));
 *      suite.addTest(new MathTest("testDivideByZero"));
 *      return suite;
 *  }
 * </pre>
 * @see TestResult
 * @see TestSuite
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public abstract class TestCase extends Assert implements Test {

    /**
     * the name of the test case
     */
    private String fName;

    /**
     * No-arg constructor to enable serialization.
     * 
     * This method is not intended to be used by mere mortals.
     */
    public TestCase() {
        fName = null;
    }

    /**
     * Constructs a test case with the given name.
     * 
     * @param name the name of the test has to be specified
     *         at object creation time
     */
    public TestCase(String name) {
        fName = name;
    }

    /**
     * Counts the number of test cases executed by run(TestResult result).
     * 
     * @return always 1
     */
    public int countTestCases() {
        return 1;
    }

    /**
     * Creates a default TestResult object
     *
     * @see TestResult
     */
    protected TestResult createResult() {
        return new TestResult();
    }

    /**
     * A convenience method to run this test, collecting the results with a
     * default TestResult object.
     *
     * @see TestResult
     */
    public TestResult run() {
        TestResult result = createResult();
        run(result);
        return result;
    }

    /**
     * Runs the test case and collects the results in TestResult.
     * 
     * @param result the test results to write into
     */
    public void run(TestResult result) {
        result.run(this);
    }

    /**
     * Runs the bare test sequence.
     * @exception Throwable if any exception is thrown
     */
    public void runBare() throws Throwable {
        setUp();
        try {
            runTest();
        } finally {
            tearDown();
        }
    }

    /**
     * Override to run the test and assert its state.
     * @exception Throwable if any exception is thrown
     */
    protected abstract void runTest() throws Throwable;

    /**
     * Sets up the fixture, for example, open a network connection.
     * 
     * This method is called before a test is executed.
     * 
     * @throws Exception can be raised in general
     */
    protected void setUp() throws Exception {
    }

    /**
     * Tears down the fixture, for example, close a network connection.
     * 
     * This method is called after a test is executed.
     * 
     * @throws Exception can be raised in general
     */
    protected void tearDown() throws Exception {
    }

    /**
     * Returns a string representation of the test case
     * 
     * @return the string representation of a test case
     */
    public String toString() {
        // lejosunit: no class name extractable through reflection
        // @TODO jhi 2002-10-11 what to do instead ?
        return getName();

        // + "("getClass().getName()+
        //       + "???" + ")";
    }

    /**
     * Gets the name of a TestCase
     * @return returns a String
     */
    public String getName() {
        return fName;
    }


    /**
     * Sets the name of a TestCase
     * @param name The name to set
     */
// commented out: Not used in leJOSUnit, save some memory
//    public void setName(String name) {
//        fName = name;
//    }
}